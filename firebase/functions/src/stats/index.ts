import * as admin from 'firebase-admin';

import { Period, day as dailyPeriod } from './../period';
import { Meal } from './../model';
import { sumFactor, daily } from './utils';

export abstract class Stats {
  kcal: number;
  lct: number;
  mct: number;
  carbohydrates: number;
  protein: number;
  roughage: number;
	salt: number;
	total: number;
	period: Period;

	private days: number;

  abstract persist(): void;

  constructor(meals: Array<Meal>, period: Period) {
    this.kcal = sumFactor(meals, i => i.calories);
    this.lct = sumFactor(meals, i => i.lct);
    this.mct = sumFactor(meals, i => i.mtc);
    this.carbohydrates = sumFactor(meals, i => i.carbohydrates);
    this.protein = sumFactor(meals, i => i.protein);
    this.roughage = sumFactor(meals, i => i.roughage);
    this.salt = sumFactor(meals, i => i.salt);
		this.period = period;
		this.days = period.daysDiff();
		this.total = this.lct + this.mct + this.carbohydrates + this.protein + this.roughage + this.salt;
  }
}

export class DailyStats extends Stats {

  persist() {
    admin.database()
  		.ref('stats/daily/' + this.period.year + "/" + this.period.month + "/" + this.period.day)
  		.set(this)
  }
}

export class MonthlyStats extends Stats {

  lctAvg: number = 0;
  kcalAvg: number = 0;
  mctAvg: number = 0;
	carbohydratesAvg: number = 0;
  proteinAvg: number = 0;
  roughageAvg: number = 0;
	saltAvg: number = 0;

  constructor(meals: Array<Meal>, period: Period) {
    super(meals, period);

    const lastDay = new Date(period.end).getDate();
    const dailys: Array<Stats> = Array.from({length: lastDay}, (v, k) => k + 1)
        .map(day => {
          const date = new Date(period.end);
          return dailyPeriod(date.getFullYear(), date.getMonth(), day);
        })
        .map(p => daily(meals.filter(m => m.time >= p.start && m.time <= p.end), p));

		const meaningful: Array<Stats> = meaningfulStats(dailys)
    const size: number = meaningful.length;

		if(size > 0) {
	    this.kcalAvg = avg(meaningful, size, s => s.kcal)
	    this.mctAvg = avg(meaningful, size, s => s.mct)
	    this.lctAvg = avg(meaningful, size, s => s.lct)
			this.carbohydratesAvg = avg(meaningful, size, s => s.carbohydrates)
	    this.proteinAvg = avg(meaningful, size, s => s.protein)
	    this.roughageAvg = avg(meaningful, size, s => s.roughage)
			this.saltAvg = avg(meaningful, size, s => s.salt)
		}
  }

  persist() {
    admin.database()
  		.ref('stats/monthly/' + this.period.year + "/" + this.period.month)
  		.set(this)
  }

	kcalMetrics() {
		return metrics(Nutrient.Calories, this.kcalAvg, this.kcal, 0)
	}

	lctMetrics() {
		return metrics(Nutrient.Lct, this.lctAvg, this.lct, this.total)
	}

	mctMetrics() {
		return metrics(Nutrient.Mct, this.mctAvg, this.mct, this.total)
	}

	carbohydratesMetrics() {
		return metrics(Nutrient.Carbohydrates, this.carbohydratesAvg, this.carbohydrates, this.total)
	}

	proteinMetrics() {
		return metrics(Nutrient.Protein, this.proteinAvg, this.protein, this.total)
	}

	roughageMetrics() {
		return metrics(Nutrient.Roughage, this.roughageAvg, this.roughage, this.total)
	}

	saltMetrics() {
		return metrics(Nutrient.Salt, this.saltAvg, this.salt, this.total)
	}
}

function formatNumber(n: number) {
	return n.toPrecision(n.toFixed().toString().length + 2)
}

function avg(dailyStats: Stats[], size: number, f: (s: Stats) => number): number {
	return dailyStats.map(s => f(s)).reduce((a, b) => a + b) / size;
}

function meaningfulStats(allStats: Stats[]): Stats[] {
	allStats.sort((a: Stats, b: Stats) => a.kcal - b.kcal);
	const medium = allStats.map((m: Stats) => m.kcal)[(allStats.length-1)/2];
	return allStats.filter((m: Stats) => m.kcal > medium * 0.8);
}

function percents(n: number, total: number) {
	return formatNumber(n*100/total) + "%";
}

export function formattedValue(value: number, suffix: String) {
	return formatNumber(value) + " " + suffix;
}

export enum Nutrient {
    Calories = "Kalorie",
		Lct = "LCT",
	  Mct = "MCT",
	  Carbohydrates = "Weglowodany",
	  Protein = "Bialko",
	  Roughage = "Blonnik",
		Salt = "Sol"
}


function unit(nutrient: Nutrient): String {
    switch (nutrient) {
        case Nutrient.Calories:
					return "kcal";
        default:
          return "g";
    }
}

export function metrics(nutrient: Nutrient, avg: number, total: number, sum: number) {
		const u: String = unit(nutrient)
    switch (nutrient) {
        case Nutrient.Calories:
					return [nutrient, formattedValue(avg, u), formattedValue(total, u), "100%"];
        default:
					return [nutrient, formattedValue(avg, u), formattedValue(total, u), percents(total, sum)]

    }
}
