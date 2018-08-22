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
const functions = require("firebase-functions");
const period_1 = require("./../period");
const Utils = require("./../utils");
const sumFactorInSingleMeal = (meal, f) => meal.ingredients.map(i => i.weight / 100 * f(i.ingredient)).reduce((t, s) => t + s);
function sumFactor(meals, f) {
    if (meals.length == 0) {
        return 0;
    }
    return meals.map(m => sumFactorInSingleMeal(m, f)).reduce((t, s) => t + s);
}
const monthly = (m, p) => new MonthlyStats(m, p);
const daily = (m, p) => new DailyStats(m, p);
exports.updateMonthlyStats = functions.https.onRequest((req, res) => __awaiter(this, void 0, void 0, function* () {
    const y = req.query.year;
    const m = req.query.month;
    let period = period_1.lastMonth();
    if (typeof y !== 'undefined' && typeof m !== 'undefined') {
        period = period_1.month(y, m);
    }
    console.log(period);
    try {
        const meals = yield Utils.mealsPromise(period);
        monthly(meals, period).persist();
        res.status(200).send("ok\n");
    }
    catch (error) {
        console.log(error);
        res.status(500).send(error);
    }
}));
exports.updateDailyStats = functions.https.onRequest((req, res) => __awaiter(this, void 0, void 0, function* () {
    const y = req.query.year;
    const m = req.query.month;
    const d = req.query.day;
    const period = period_1.day(y, m, d);
    try {
        const meals = yield Utils.mealsPromise(period);
        daily(meals, period).persist();
        res.status(200).send("ok\n");
    }
    catch (error) {
        res.status(500).send(error);
    }
}));
class Stats {
    constructor(meals, period) {
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
    percents(n) {
        return formatNumber(n * 100 / this.total()) + "%";
    }
    roughageString() {
        return this.roughage.toPrecision(3);
    }
    kcalString() {
        return this.kcal.toPrecision(3);
    }
    kcalMetrics() {
        const kcal = this.kcal;
        return ["Kalorie", formatNumber(kcal / this.days) + " kcal", formatNumber(kcal) + " kcal", "100%"];
    }
    lctMetrics() {
        return ["LCT", formatNumber(this.lct / this.days) + " g", formatNumber(this.lct) + " g", this.percents(this.lct)];
    }
    mctMetrics() {
        return ["MCT", formatNumber(this.mct / this.days) + " g", formatNumber(this.mct) + " g", this.percents(this.mct)];
    }
    carbohydratesMetrics() {
        return ["Weglowodany", formatNumber(this.carbohydrates / this.days) + " g", formatNumber(this.carbohydrates) + " g", this.percents(this.carbohydrates)];
    }
    proteinMetrics() {
        return ["Bialko", formatNumber(this.protein / this.days) + " g", formatNumber(this.protein) + " g", this.percents(this.protein)];
    }
    roughageMetrics() {
        return ["Blonnik", formatNumber(this.roughage / this.days) + " g", formatNumber(this.roughage) + " g", this.percents(this.roughage)];
    }
    saltMetrics() {
        return ["Sol", formatNumber(this.salt / this.days) + " g", formatNumber(this.salt) + " g", this.percents(this.salt)];
    }
}
exports.Stats = Stats;
class DailyStats extends Stats {
    constructor(meals, period) {
        super(meals, period);
        this.meals = meals;
    }
    persist() {
        admin.database()
            .ref('stats/daily/' + this.period.year + "/" + (this.period.month + 1) + "/" + this.period.day)
            .set(this);
    }
}
class MonthlyStats extends Stats {
    constructor(meals, period) {
        super(meals, period);
        this.lctAvg = 0;
        this.kcalAvg = 0;
        this.mctAvg = 0;
        const lastDay = new Date(period.end).getDate();
        const dailys = Array.from({ length: lastDay }, (v, k) => k + 1)
            .map(day => {
            const date = new Date(period.end);
            return period_1.day(date.getFullYear(), date.getMonth(), day);
        })
            .map(p => daily(meals.filter(m => m.time >= p.start && m.time <= p.end), p));
        dailys.sort((a, b) => a.kcal - b.kcal);
        const medium = dailys.map((m) => m.kcal)[(dailys.length - 1) / 2];
        const filtered = dailys.filter((m) => m.kcal > medium * 0.8);
        const filteredSize = filtered.length;
        console.log("aaa");
        console.log(filteredSize);
        if (filteredSize > 0) {
            this.kcalAvg = filtered.map(s => s.kcal).reduce((a, b) => a + b) / filteredSize;
            this.mctAvg = filtered.map(s => s.mct).reduce((a, b) => a + b) / filteredSize;
            this.lctAvg = filtered.map(s => s.lct).reduce((a, b) => a + b) / filteredSize;
        }
    }
    persist() {
        admin.database()
            .ref('stats/monthly/' + this.period.year + "/" + (this.period.month + 1))
            .set(this);
    }
}
exports.MonthlyStats = MonthlyStats;
function formatNumber(n) {
    return n.toPrecision(n.toFixed().toString().length + 2);
}
