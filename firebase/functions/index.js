"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const admin = require("firebase-admin");
const Stats = require("./stats");
const Notifications = require("./notifications");
const Views = require("./views");
admin.initializeApp();
exports.sendMealNotification = Notifications.newMealNotification;
exports.sendEmailWithMonthlyStats = Notifications.montlyStatsEmail;
exports.monthlyStats = Stats.updateMonthlyStats;
exports.dailyStats = Stats.updateDailyStats;
exports.getMeals = Views.mealsView;
exports.showDialyStats = Views.dailyStatsView;
exports.generatePdf = Views.pdf;
