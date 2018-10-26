import * as functions from 'firebase-functions';

import * as Utils from './../utils';
import * as Period from './../period';
import { DbMonthlyStats } from './../model/db';
import { PdfGenerator } from './pdf_generator';

export const mealsView = functions.https.onRequest(async (req, res) => {
  const time = req.query.since;

  let period: Period.Period = Period.days(1);
  if(typeof time !== 'undefined') {
    period = Period.time(Number.parseInt(time));
  }

  try {
    const meals = await Utils.mealsPromise(period);
    res.setHeader('Content-Type', 'application/json');
    res.send(JSON.stringify(meals.filter(m => m.time >= period.start)));
  } catch(error) {
    console.log(error);
    res.status(500).send(error)
  }
});

export const dailyStatsView = functions.https.onRequest(async (req, res) => {
	const y: number = req.query.year;
	const m: number = req.query.month;
	const d: number = req.query.day;

  if(typeof d === 'undefined') {
    let statsSnapshot = await Utils.allDailyStatsPromise(y, m);
    let data = statsSnapshot.val();

    res.setHeader('Content-Type', 'application/json');
    res.send(Object.keys(data)
      .map(key => data[key])
      .map(function(s) {
        return {
          year: s.period.year,
          month: s.period.month,
          day: s.period.day,
          kcal: s.kcal
        }
      })
    );
    return
  }

  const period: Period.Period = Period.day(y, m, d);
  try {
    let statsSnapshot = await Utils.dailyStatsPromise(period);
    const response = Object.assign({}, statsSnapshot.val());
    res.setHeader('Content-Type', 'application/json');
    res.send(response);
  } catch(error) {
    res.status(500).send(error)
  }
});

export const pdf = functions.https.onRequest(async (req, res) => {
  const period: Period.Period = Period.lastMonth();

  try {
    let statsSnapshot = await Utils.montlyStatsPromise(period);
    const stats: DbMonthlyStats = statsSnapshot.val();

    const pdfGenerator = new PdfGenerator(
      period,
      'Stas Pobiarzyn - Raport miesieczny'
    );
    pdfGenerator.doc.pipe(res)
    pdfGenerator.generateMonthlyReport(stats)
    pdfGenerator.chart()

    pdfGenerator.end();
  } catch(error) {
    console.log(error);
    res.status(500).send(error)
  }
});

function daysInMonth(month: number, year: number) {
    return new Date(year, month, 0).getDate();
}
