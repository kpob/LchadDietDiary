import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

import * as Utils from './../utils';
import * as Stats from './../stats';
import * as Period from './../period';
import { Meal, DbStats, DbPeriod } from './../model';

const pdfkit = require('pdfkit');

export const mealsView = functions.https.onRequest(async (req, res) => {
  const time = req.query.since;
  let p: Period.Period = Period.days(7);
  if(typeof time !== 'undefined') {
    p = Period.time(Number.parseInt(time));
  }

  try {
    const meals = await Utils.mealsPromise(p);
    res.setHeader('Content-Type', 'application/json');
    res.send(JSON.stringify(meals.filter(m => m.time >= p.start)));
  } catch(error) {
    console.log(error);
    res.status(500).send(error)
  }
});

export const dailyStatsView = functions.https.onRequest(async (req, res) => {
	const y: number = req.query.year;
	const m: number = req.query.month;
	const d: number = req.query.day;

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
    const dbStats: DbStats = statsSnapshot.val();

    let stats: Stats.MonthlyStats = new Stats.MonthlyStats([], period);
    stats.kcal = dbStats.kcal
    stats.lct = dbStats.lct
    stats.mct = dbStats.mct
    stats.carbohydrates = dbStats.carbohydrates
    stats.protein = dbStats.protein
    stats.roughage = dbStats.roughage
  	stats.salt = dbStats.salt

    var pdfGenerator = new PdfGenerator(
      period,
      'Stas Pobiarzyn - Raport miesieczny'
    );

    console.log("Period");
    console.log(period);
    console.log(stats);
    pdfGenerator.generateHeader();
    pdfGenerator.setFont(16, 'Times-Bold')
    pdfGenerator.renderRow(["Nazwa", "Srednia", "Lacznie", "Udzial w diecie"])
    pdfGenerator.setFont(14, 'Times-Roman')

    pdfGenerator.renderRow(stats.kcalMetrics())
    pdfGenerator.renderRow(stats.lctMetrics())
    pdfGenerator.renderRow(stats.mctMetrics())
    pdfGenerator.renderRow(stats.carbohydratesMetrics())
    pdfGenerator.renderRow(stats.proteinMetrics())
    pdfGenerator.renderRow(stats.roughageMetrics())
    pdfGenerator.renderRow(stats.saltMetrics())
    pdfGenerator.end();

    res.setHeader('Content-Type', 'application/json');
    res.status(200).send(stats);
  } catch(error) {
    console.log(error);
    res.status(500).send(error)
  }

});

class PdfGenerator {
  readonly columnWidth: number = 120;
  readonly textMargin: number = 20;
  y: number = 200;
  x: number = 50;
  period: Period.Period;

  title: String;
  doc: any;

  constructor(period: Period.Period, title: String) {
    this.period = period
    this.title = title;

    const time = new Date().getTime();
    const filename = '/test/summary' + time + '.pdf';
    const myPdfFile = admin.storage().bucket().file(filename);
    this.doc = new pdfkit();
    const stream = this.doc.pipe(myPdfFile.createWriteStream());
  }

  generateHeader() {
    this.doc.fontSize(26).font('Times-Bold')
    this.doc.text(this.title, { align: 'center' })
    this.doc.moveDown()
    this.doc.fontSize(16)

    let d1 = new Date(this.period.start);
    let d2 = new Date(this.period.end);
    let from = d1.getDate() +'.'+  (d1.getMonth() + 1) + '.' + d1.getFullYear();
    let to = d2.getDate() +'.'+  (d2.getMonth() + 1) + '.' + d2.getFullYear();

    this.doc.text('Za okres ' + from + '-' + to, { align: 'center' })
    this.doc.fontSize(14).font('Times-Roman')
  }

  renderRow(labels: String[]) {
    console.log("labels")
    console.log(labels)
    for(var i = 0; i < 4; i++) {
      this.doc.text(labels[i], 72 + i * this.columnWidth, this.y + this.textMargin, {
        width: this.columnWidth,
        align: 'center'
      })
      .rect(this.doc.x, this.y, this.columnWidth, 50)
      .stroke()
    }
    this.y += 50;
  }

  setFont(size: number, name: String) {
    console.log("set fonte " + size)
    this.doc.fontSize(size).font(name)
  }

  end() {
    this.doc.end();
  }
}

function daysInMonth(month: number, year: number) {
    return new Date(year, month, 0).getDate();
}
