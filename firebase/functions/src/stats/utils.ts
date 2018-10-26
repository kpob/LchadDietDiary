import { DbIngredient } from './../model/db';
import { Meal } from './../model';
import { Stats, DailyStats, MonthlyStats } from './../stats';
import { Period } from './../period';

const sumFactorInSingleMeal = (meal: Meal, f: (i: DbIngredient) => number) =>
	meal.ingredients
  .map(i => i.weight / 100 * f(i.ingredient))
  .reduce((t, s) => t + s)

export function sumFactor(meals: Array<Meal>, f: (i: DbIngredient) => number) {
	if(meals.length === 0) {
		return 0;
	}
	return meals.map(m => sumFactorInSingleMeal(m, f)).reduce((t, s) => t + s);
}

export const monthly: (meals: Array<Meal>, period: Period) => Stats = (m, p) => new MonthlyStats(m, p);
export const daily: (meals: Array<Meal>, period: Period) => Stats = (m, p) => new DailyStats(m, p);
