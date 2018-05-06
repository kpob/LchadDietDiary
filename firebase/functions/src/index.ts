import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

admin.initializeApp();

// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript
const ingredientsPromise = () => admin.database().ref('ingredients').once('value')

exports.sendMealNotification = functions.database.ref('meals/{mealId}').onCreate((snap, context) => {

	const data = snap.val();

	const senderToken = data.senderToken;
	const mealType = data.name;

	console.log("data " + data);
  	// Get the list of device notification tokens.
	return admin.database().ref('users').once('value').then(result => {
	    // Check if there are any device tokens.
	    if (!result.hasChildren()) {
	      return console.log('There are no notification tokens to send to.');
	    }
	    // Notification details.
	    const payload = {
	      notification: {
	        title: 'Staś zajada!',
	        body: getNotificationBody(mealType),
	        sound: 'mniam',
	        android_channel_id: 'Default'
	      }
	    };

    	// Listing all tokens.
    	const tokens = Object.keys(result.val());
			//remove senderToken
    	const senderTokenIdx = tokens.indexOf(senderToken);
    	if(senderTokenIdx > -1) {
    		tokens.splice(senderTokenIdx, 1);
    	}

    	if(tokens.length === 0) {
    		return console.log("Sender is the only user - do not send a notification");
    	}

			const today: Period = new TodayPeroid();
			ingredientsPromise()
				.then(ingredientsResult => {
					const ingredients = preProccessIngredients(ingredientsResult);

					today.mealsPromise().then(mealsResult => {
						const meals = preProccessMeals(mealsResult).map(m => new Meal(m, ingredients));
            new DailyStats(meals, today).persist();
					});
			});

     	return admin.messaging().sendToDevice(tokens, payload).then(response => {
				console.log("send notification");
			});
  });
});

exports.stats = functions.https.onRequest((req, res) => {
	console.log("prepare stats!");

	const lastMonth: Period = new LastMonthPeriod();

	ingredientsPromise()
		.then(ingredientsResult => {
			console.log("handle ingredients!");
			const ingredients = preProccessIngredients(ingredientsResult);

			lastMonth.mealsPromise().then(mealsResult => {
				const meals = preProccessMeals(mealsResult).map(m => new Meal(m, ingredients));
        new MonthlyStats(meals, lastMonth).persist();
			});
	});
  res.status(200).send("ok\n");
});
/*
* UTILITIES
*/
function preProccessIngredients(result): Array<DbIngredient> {
	const ingredients = [];
	if(result.hasChildren()) {
		result.forEach(item => { ingredients.push(item.val()) });
		ingredients.filter(i => i.deleted === false );
	}
	return ingredients;
}

function preProccessMeals(result): Array<DbMeal> {
	const meals = [];
	if(result.hasChildren()) {
		result.forEach(item => { meals.push(item.val()) });
		meals.filter(m => m.deleted === false);
		meals.forEach(m => { m.ingredients = m.ingredients.filter(i => i.deleted === false) });
	}
	return meals;
}

const sumFactorInSingleMeal = (meal: Meal, f: (i: DbIngredient) => number) =>
	meal.ingredients.map(i => i.weight / 100 * f(i.ingredient)).reduce((t, s) => t + s)

const sumFactor = (meals: Array<Meal>, f: (i: DbIngredient) => number) =>
	meals.map(m => sumFactorInSingleMeal(m, f)).reduce((t, s) => t + s);

function getNotificationBody(mealType): string {
	switch (mealType) {
	    case "DESSERT": return "Pyszna kaszka!";
	    case "DINNER": return "Smakowity obiadek!";
	    case "MILK": return "Dobre mleczko!";
	    default: return "Mniam! Mniam!";
	}
}

class Period {
  start: number;
  end: number;
  year: number;
  month: number;
  day: number;

  constructor(d1: Date, d2: Date) {
    this.start = d1.getTime();
  	this.end = d2.getTime();
  	this.year = d1.getFullYear();
  	this.month = d1.getMonth();
  	this.day = d1.getDate();
  }

	mealsPromise() {
		return admin.database().ref('meals')
			.orderByChild('time')
			.startAt(this.start, 'time')
			.endAt(this.end, 'time')
			.once('value')
	}
}

class TodayPeroid extends Period {

  constructor() {
    const d1 = new Date();
  	d1.setHours(0);
  	d1.setMinutes(0);

  	const d2 = new Date();
  	d2.setHours(23);
  	d2.setMinutes(59);

  	super(d1, d2);
  }
}

class LastMonthPeriod extends Period {
  constructor() {
    const days = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];

  	const now = new Date();
  	let month = now.getMonth();
  	let year = now.getFullYear();

  	if(month === 0) {
  		month = 11;
  		year = year - 1;
  	} else {
  		month = month - 1;
  	}

  	const d1 = new Date();
  	d1.setMonth(month);
  	d1.setFullYear(year);
  	d1.setDate(1);
  	d1.setHours(0);
  	d1.setMinutes(0);

  	const d2 = new Date();
  	d2.setMonth(month);
  	d2.setFullYear(year);
  	d2.setDate(days[month]);
  	d2.setHours(23);
  	d2.setMinutes(59);

  	super(d1, d2);
  }
}

interface DbIngredient {
	readonly id: string;
  readonly name: string;
  readonly calories: number;
  readonly lct: number;
  readonly mtc: number;
  readonly carbohydrates: number;
  readonly protein: number;
  readonly roughage: number;
  readonly salt: number;

  readonly deleted: boolean;
}

interface DbMeal {
  readonly name: string;
  readonly ingredients: Array<DbMealIngredient>;
  readonly deleted: boolean;
}

interface DbMealIngredient {
  readonly ingredientId: string;
	readonly weight: number;
  readonly deleted: boolean;
}

class Meal {

  name: string;
  ingredients: Array<MealIngredient>;

  constructor(meal: DbMeal, ingredients: Array<DbIngredient>) {
    this.name = meal.name;
  	this.ingredients = meal.ingredients.map(i => new MealIngredient(i, ingredients));
  }
}

class MealIngredient {

  weight: number;
  ingredient: DbIngredient;

  constructor(i: DbMealIngredient, ingredients: Array<DbIngredient>) {
    this.weight = i.weight;
  	this.ingredient = ingredients.find(ing => ing.id === i.ingredientId)
  }
}

abstract class Stats {
  kcal: number;
  lct: number;
  mct: number;
  carbohydrates: number;
  protein: number;
  roughage: number;
	salt: number;
  meals: Array<Meal>;
	period: Period;

  abstract persist(): void;

  constructor(meals: Array<Meal>, period: Period) {
    this.kcal = sumFactor(meals, i => i.calories);
    this.lct = sumFactor(meals, i => i.lct);
    this.mct = sumFactor(meals, i => i.mtc);
    this.carbohydrates = sumFactor(meals, i => i.carbohydrates);
    this.protein = sumFactor(meals, i => i.protein);
    this.roughage = sumFactor(meals, i => i.roughage);
    this.salt = sumFactor(meals, i => i.salt);
    this.meals = meals;

		this.period = period;
  }
}

class DailyStats extends Stats {

  persist() {
    admin.database()
  		.ref('stats/daily/' + this.period.year + "/" + (this.period.month + 1) + "/" + this.period.day)
  		.set(this)
  }
}

class MonthlyStats extends Stats {

  persist() {
    admin.database()
  		.ref('stats/monthly/' + this.period.year + "/" + (this.period.month + 1))
  		.set(this)
  }
}
