"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : new P(function (resolve) { resolve(result.value); }).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
Object.defineProperty(exports, "__esModule", { value: true });
const admin = require("firebase-admin");
const model_1 = require("./../model");
const ingredientsPromise = () => admin.database().ref('ingredients').once('value');
function preProccessIngredients(data) {
    return Object.keys(data)
        .map(key => data[key])
        .filter((i) => i.deleted != true);
}
function preProccessMeals(data) {
    return Object.keys(data)
        .map(key => data[key])
        .filter((m) => m.deleted != true);
}
function mealsPromise(period) {
    return __awaiter(this, void 0, void 0, function* () {
        const values = yield Promise.all([ingredientsPromise(), period.mealsPromise()]);
        const ingredients = preProccessIngredients(values[0].val());
        const meals = preProccessMeals(values[1].val()).map(m => new model_1.Meal(m, ingredients));
        meals.forEach((m) => { m.ingredients = m.ingredients.filter((i) => i.ingredient.deleted != true); });
        const promise = new Promise((resolve) => { resolve(meals); });
        return promise;
    });
}
exports.mealsPromise = mealsPromise;
function dailyStatsPromise(period) {
    return admin.database()
        .ref('stats/daily/' + period.year + "/" + (period.month + 1) + "/" + period.day)
        .once('value');
}
exports.dailyStatsPromise = dailyStatsPromise;
function montlyStatsPromise(period) {
    console.log('stats/monthly/' + period.year + "/" + (period.month + 1));
    return admin.database()
        .ref('stats/monthly/' + period.year + "/" + (period.month + 1))
        .once('value');
}
exports.montlyStatsPromise = montlyStatsPromise;
