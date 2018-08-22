import * as admin from 'firebase-admin';

import * as Stats from './stats';
import * as Notifications from './notifications';
import * as Views from './views';

admin.initializeApp();

exports.sendMealNotification = Notifications.newMealNotification
exports.sendEmailWithMonthlyStats = Notifications.montlyStatsEmail

exports.monthlyStats = Stats.updateMonthlyStats
exports.dailyStats = Stats.updateDailyStats

exports.getMeals = Views.mealsView
exports.showDialyStats = Views.dailyStatsView
exports.generatePdf = Views.pdf
