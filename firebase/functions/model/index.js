"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class Meal {
    constructor(meal, ingredients) {
        this.name = meal.name;
        this.time = meal.time;
        this.ingredients = meal.ingredients.map(i => new MealIngredient(i, ingredients));
        this.kcal = this.ingredients.map(i => i.weight / 100 * i.ingredient.calories).reduce((a, b) => a + b);
        this.mct = this.ingredients.map(i => i.weight / 100 * i.ingredient.mtc).reduce((a, b) => a + b);
        this.lct = this.ingredients.map(i => i.weight / 100 * i.ingredient.lct).reduce((a, b) => a + b);
    }
}
exports.Meal = Meal;
class MealIngredient {
    constructor(mealIngredient, ingredients) {
        this.weight = mealIngredient.weight;
        const tmp = ingredients.find(i => i.id === mealIngredient.ingredientId);
        if (typeof tmp === 'undefined') {
            this.ingredient = { id: "", name: "", calories: 0, lct: 0, mtc: 0, carbohydrates: 0, protein: 0, roughage: 0, salt: 0, deleted: true };
        }
        else {
            this.ingredient = tmp;
        }
    }
}
exports.MealIngredient = MealIngredient;
