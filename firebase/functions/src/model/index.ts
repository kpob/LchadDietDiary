export interface DbIngredient {
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

export interface DbMeal {
  readonly name: string;
	readonly time: number;
  readonly ingredients: Array<DbMealIngredient>;
  readonly deleted: boolean;
}

export interface DbMealIngredient {
  readonly ingredientId: string;
	readonly weight: number;
  readonly deleted: boolean;
}

export interface DbStats {
	readonly carbohydrates: number;
	readonly days: number;
	readonly kcal: number;
	readonly kcalAvg: number;
	readonly lct: number;
	readonly lctAvg: number;
	readonly mct: number;
	readonly mctAvg: number;
	readonly protein: number;
	readonly roughage: number;
	readonly salt: number;
  readonly period: DbPeriod;
}

export interface DbPeriod {

	readonly day: number;
	readonly end: number;
	readonly month: number;
	readonly start: number;
	readonly year: number;
}

export class Meal {
  name: string;
	kcal: number;
	mct: number;
	lct: number;
	time: number;
  ingredients: Array<MealIngredient>;

  constructor(meal: DbMeal, ingredients: Array<DbIngredient>) {
    this.name = meal.name;
		this.time = meal.time;
  	this.ingredients = meal.ingredients.map(i => new MealIngredient(i, ingredients));
		this.kcal = this.ingredients.map(i => i.weight/100 * i.ingredient.calories).reduce((a, b) => a + b);
		this.mct = this.ingredients.map(i => i.weight/100 * i.ingredient.mtc).reduce((a, b) => a + b);
		this.lct = this.ingredients.map(i => i.weight/100 * i.ingredient.lct).reduce((a, b) => a + b);
  }
}

export class MealIngredient {
  weight: number;
  ingredient: DbIngredient;

  constructor(mealIngredient: DbMealIngredient, ingredients: Array<DbIngredient>) {
    this.weight = mealIngredient.weight;
		const tmp = ingredients.find(i => i.id === mealIngredient.ingredientId);
		if(typeof tmp === 'undefined') {
			this.ingredient = { id: "", name: "", calories: 0, lct: 0, mtc: 0, carbohydrates: 0, protein: 0, roughage: 0, salt: 0, deleted: true };
		} else {
			this.ingredient = tmp;
		}
  }
}
