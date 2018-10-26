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
}

export interface DbMonthlyStats {
	readonly kcal: number;
	readonly kcalAvg: number;
	readonly lct: number;
	readonly lctAvg: number;
	readonly mct: number;
	readonly mctAvg: number;
	readonly carbohydrates: number;
	readonly carbohydratesAvg: number;
	readonly protein: number;
	readonly proteinAvg: number;
	readonly roughage: number;
	readonly roughageAvg: number;
	readonly salt: number;
	readonly saltAvg: number;
	readonly total: number;
	readonly period: DbPeriod;
	readonly days: number;
}


export interface DbPeriod {
	readonly day: number;
	readonly end: number;
	readonly month: number;
	readonly start: number;
	readonly year: number;
}
