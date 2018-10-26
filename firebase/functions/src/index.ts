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

exports.jira_clone = functions.https.onRequest(async (req, res) => {
    var options = {
      url: 'https://jira.sisms.pl/rest/api/latest/issue/',
      headers: {
        'Content-Type': 'application/json'
        'Authorization': 'Basic a3BvYmlhcnp5bjoxMjNXaXRvbGRQeXJrb3N6'
      }
    };

    function callback(error, response, body) {
      if (!error && response.statusCode == 200) {
        var info = JSON.parse(body);
        console.log(info.stargazers_count + " Stars");
        console.log(info.forks_count + " Forks");
      }
    }

  request(options, callback);
});
