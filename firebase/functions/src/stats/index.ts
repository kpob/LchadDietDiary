import * as admin from 'firebase-admin';
import * as functions from 'firebase-functions';

import { Period, day as dailyPeriod, lastMonth, month } from './../period';
import { Meal, DbIngredient, DbPeriod, DbStats } from './../model';

import * as Utils from './../utils';

const sumFactorInSingleMeal = (meal: Meal, f: (i: DbIngredient) => number) =>
	meal.ingredients.map(i => i.weight / 100 * f(i.ingredient)).reduce((t, s) => t + s)

function sumFactor(meals: Array<Meal>, f: (i: DbIngredient) => number) {
	if(meals.length == 0) {
		return 0;
	}
	return meals.map(m => sumFactorInSingleMeal(m, f)).reduce((t, s) => t + s);
}

const monthly: (meals: Array<Meal>, period: Period) => Stats = (m, p) => new MonthlyStats(m, p);
const daily: (meals: Array<Meal>, period: Period) => Stats = (m, p) => new DailyStats(m, p);

export const updateMonthlyStats = functions.https.onRequest(async (req, res) => {
	const y = req.query.year;
	const m = req.query.month;
  let period: Period = lastMonth();
  if(typeof y !== 'undefined' && typeof m !== 'undefined') {
    period = month(y, m);
  }
	console.log(period);
	try {
		const meals = await Utils.mealsPromise(period);
		monthly(meals, period).persist();
		res.status(200).send("ok\n");
	} catch(error) {
		console.log(error);
		res.status(500).send(error)
	}
});

export const updateDailyStats = functions.https.onRequest(async (req, res) => {
	const y: number = req.query.year;
	const m: number = req.query.month;
	const d: number = req.query.day;

	const period: Period = dailyPeriod(y, m, d);
	try {
		const meals = await Utils.mealsPromise(period);
		daily(meals, period).persist();
		res.status(200).send("ok\n");
	} catch(error) {
		res.status(500).send(error)
	}
});

export abstract class Stats {
  kcal: number;
  lct: number;
  mct: number;
  carbohydrates: number;
  protein: number;
  roughage: number;
	salt: number;
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

		console.log("stats days diff " + this.days);
  }

	total() {
		return this.lct + this.mct + this.carbohydrates + this.protein + this.roughage + this.salt;
	}

	percents(n: number) {
		return formatNumber(n*100/this.total()) + "%";
	}

	roughageString() {
		return this.roughage.toPrecision(3);
	}

	kcalString() {
		return this.kcal.toPrecision(3);
	}

	kcalMetrics() {
		const kcal = this.kcal
		return ["Kalorie", formatNumber(kcal/this.days) + " kcal", formatNumber(kcal) + " kcal", "100%"]
	}

	lctMetrics() {
		return ["LCT", formatNumber(this.lct/this.days) + " g", formatNumber(this.lct) + " g", this.percents(this.lct)]
	}

	mctMetrics() {
		return ["MCT",  formatNumber(this.mct/this.days) + " g", formatNumber(this.mct) + " g", this.percents(this.mct)]
	}

	carbohydratesMetrics() {

		return ["Weglowodany", formatNumber(this.carbohydrates/this.days) + " g", formatNumber(this.carbohydrates) + " g", this.percents(this.carbohydrates)]
	}

	proteinMetrics() {
		return ["Bialko", formatNumber(this.protein/this.days) + " g", formatNumber(this.protein) + " g", this.percents(this.protein)]
	}

	roughageMetrics() {

		return ["Blonnik", formatNumber(this.roughage/this.days) + " g", formatNumber(this.roughage) + " g", this.percents(this.roughage)]
	}

	saltMetrics() {
		return ["Sol", formatNumber(this.salt/this.days) + " g", formatNumber(this.salt) + " g", this.percents(this.salt)]
	}
}

class DailyStats extends Stats {

  meals: Array<Meal>;

  constructor(meals: Array<Meal>, period: Period) {
    super(meals, period);
    this.meals = meals;
  }

  persist() {
    admin.database()
  		.ref('stats/daily/' + this.period.year + "/" + (this.period.month + 1) + "/" + this.period.day)
  		.set(this)
  }
}

export class MonthlyStats extends Stats {

  lctAvg: number = 0;
  kcalAvg: number = 0;
  mctAvg: number = 0;

  constructor(meals: Array<Meal>, period: Period) {
    super(meals, period);

    const lastDay = new Date(period.end).getDate();
    const dailys: Array<Stats> = Array.from({length: lastDay}, (v, k) => k + 1)
        .map(day => {
          const date = new Date(period.end);
          return dailyPeriod(date.getFullYear(), date.getMonth(), day);
        })
        .map(p => daily(meals.filter(m => m.time >= p.start && m.time <= p.end), p));

    dailys.sort((a: Stats, b: Stats) => a.kcal - b.kcal);
    const medium = dailys.map((m: Stats) => m.kcal)[(dailys.length-1)/2];
    const filtered = dailys.filter((m: Stats) => m.kcal > medium * 0.8);
    const filteredSize = filtered.length;
		console.log("aaa")
		console.log(filteredSize)
		if(filteredSize > 0) {
	    this.kcalAvg = filtered.map(s => s.kcal).reduce((a, b) => a + b) / filteredSize;
	    this.mctAvg = filtered.map(s => s.mct).reduce((a, b) => a + b) / filteredSize;
	    this.lctAvg = filtered.map(s => s.lct).reduce((a, b) => a + b) / filteredSize;
		}
  }
  persist() {
    admin.database()
  		.ref('stats/monthly/' + this.period.year + "/" + (this.period.month + 1))
  		.set(this)
  }
}

function formatNumber(n: number) {
	return n.toPrecision(n.toFixed().toString().length + 2)
}
