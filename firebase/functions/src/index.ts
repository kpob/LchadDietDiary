import * as admin from 'firebase-admin';
import * as functions from 'firebase-functions';

import * as Stats from './stats/functions';
import * as Notifications from './notifications';
import * as Views from './views';

let request = require('request');

admin.initializeApp();

exports.sendMealNotification = Notifications.newMealNotification
exports.sendEmailWithMonthlyStats = Notifications.montlyStatsEmail

exports.create_monthly_stats = Stats.createMonthlyStats
exports.create_daily_stats = Stats.createDailyStats

exports.meals = Views.mealsView
exports.dialy_stats = Views.dailyStatsView
exports.monthly_raport = Views.pdf
