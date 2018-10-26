import * as admin from 'firebase-admin';

import { Period } from './../period';
import { Meal, MealIngredient } from './../model';
import { DbIngredient, DbMeal } from './../model/db';

//utility functions
const ingredientsPromise = () => admin.database().ref('ingredients').once('value');

function preProccessIngredients(data: any): DbIngredient[] {
	return Object.keys(data)
		.map(key => data[key])
		.filter((i: DbIngredient) => i.deleted !== true);
}

function preProccessMeals(data: any): DbMeal[] {
		return Object.keys(data)
			.map(key => data[key])
			.filter((m: DbMeal) => m.deleted !== true);
}


//exported functions
export async function mealsPromise(period: Period): Promise<Meal[]> {
		const values = await Promise.all([ingredientsPromise(), period.mealsPromise()]);
		const ingredients = preProccessIngredients(values[0].val());
		const meals: Meal[] = preProccessMeals(values[1].val()).map(m => new Meal(m, ingredients));
		meals.forEach((m: Meal) => { m.ingredients = m.ingredients.filter((i: MealIngredient) => i.ingredient.deleted !== true) });

		const promise: Promise<Meal[]> = new Promise((resolve) => { resolve(meals) });
		return promise;
}

export function dailyStatsPromise(period: Period) {
	return admin.database()
		.ref('stats/daily/' + period.year + "/" + period.month + "/" + period.day)
		.once('value')
}

export function allDailyStatsPromise(year: number, month: number) {
	return admin.database()
		.ref('stats/daily/' + year + "/" + month)
		.once('value')
}

export function montlyStatsPromise(period: Period) {
	return admin.database()
		.ref('stats/monthly/' + period.year + "/" + period.month)
		.once('value')
}
