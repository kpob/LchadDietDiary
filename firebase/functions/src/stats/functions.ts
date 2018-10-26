import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

import { Period, day, lastMonth, month } from './../period';
import { DbMeal, DbIngredient } from './../model/db';

import * as Stats from './utils';

import * as Utils from './../utils';

export const createMonthlyStats = functions.https.onRequest(async (req, res) => {
	const y = req.query.year;
	const m = req.query.month;

  let period: Period = lastMonth();
  if(typeof y !== 'undefined' && typeof m !== 'undefined') {
    period = month(y, m);
  }
	console.log(period);
	try {
		const meals = await Utils.mealsPromise(period);
		const stats = Stats.monthly(meals, period);
		stats.persist();
		res.status(200).send(stats);
	} catch(error) {
		console.log(error);
		res.status(500).send(error)
	}
});

export const createDailyStats = functions.https.onRequest(async (req, res) => {
	const y = req.query.year;
	const m = req.query.month;
	const d = req.query.day;

  if(typeof y === 'undefined' || typeof m === 'undefined' || typeof d === 'undefined') {
    res.status(400).send("Missing arguments: specify year, month and day.\n")
  }

	const period: Period = day(y, m, d);
	console.log(req.query)
	console.log(m)
	console.log(period)
	try {
		const meals = await Utils.mealsPromise(period);
		const stats = Stats.daily(meals, period);
    stats.persist();
		res.status(200).send(stats);
	} catch(error) {
		res.status(500).send(error)
	}
});

export const updateDailyStats = async function(period: Period, meal: DbMeal) {
  const newMeal: any = meal.ingredients.map(async function (i): Promise<DbIngredient>  {
    const ingredientSnapshot = await admin.database().ref('ingredients/' + i.ingredientId).once('value');
    const ingredient = ingredientSnapshot.val();
    return {
      id: ingredient.id,
      name: ingredient.name,
      calories: ingredient.calories,
      lct: ingredient.lct * i.weight / 100,
      mtc: ingredient.mtc * i.weight / 100,
      carbohydrates: ingredient.carbohydrates * i.weight / 100,
      protein: ingredient.protein * i.weight / 100,
      roughage: ingredient.roughage * i.weight / 100,
    	salt: ingredient.salt * i.weight / 100,
      deleted: ingredient.deleted
    }
  }).reduce(async function (p1: Promise<DbIngredient>, p2: Promise<DbIngredient>): Promise<any> {
    const a = await p1;
    const b = await p2;
    if(a.deleted && b.deleted) {
      return { kcal: 0, lct: 0, mct: 0, carbohydrates: 0, protein: 0, roughage: 0, salt: 0, total: 0 }
    }

    if(a.deleted) {
      const total = b.lct + b.mtc + b.carbohydrates + b.protein + b.roughage + b.salt;
      return { kcal: b.calories, lct: b.lct, mct: b.mtc, carbohydrates: b.carbohydrates, protein: b.protein, roughage: b.roughage, salt: b.salt, total: total};
    }
    if(b.deleted) {
      const total = a.lct + a.mtc + a.carbohydrates + a.protein + a.roughage + a.salt;
      return { kcal: a.calories, lct: a.lct, mct: a.mtc, carbohydrates: a.carbohydrates, protein: a.protein, roughage: a.roughage, salt: a.salt, total: total };
    }
    const totalB = b.lct + b.mtc + b.carbohydrates + b.protein + b.roughage + b.salt;
    const totalA = a.lct + a.mtc + a.carbohydrates + a.protein + a.roughage + a.salt;

    return {
      calories: a.calories + b.calories,
      lct: a.lct + b.lct,
      mtc: a.mtc + b.mtc,
      carbohydrates: a.carbohydrates + b.carbohydrates,
      protein: a.protein + b.protein,
      roughage: a.roughage + b.roughage,
      salt: a.salt + b.salt,
      total: totalA + totalB
    }
  });

  const dailyRef = admin.database()
    .ref('stats/daily/' + period.year + "/" + period.month + "/" + period.day);
  const snapshot = await dailyRef.once('value');
  const data = snapshot.val();

  const updated = {
    kcal: data.kcal + newMeal.kcal,
    lct: data.lct + newMeal.lct,
    mct: data.mct + newMeal.mct,
    carbohydrates: data.carbohydrates + newMeal.carbohydrates,
    protein: data.protein + newMeal.protein,
    roughage: data.roughage + newMeal.roughage,
  	salt: data.salt + newMeal.salt,
    period: data.period,
    meals: [...data.meals, meal]
  }
  dailyRef.set(updated);
}
