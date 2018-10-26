import * as admin from 'firebase-admin';

import * as Period from './../period';
import { Nutrient, metrics } from './../stats';
import { DbMonthlyStats } from './../model/db';

const pdfkit = require('pdfkit');

const center = [300, 300];
const radius = 180;
const legendStart = center[1] + radius + 40;

function getCoordinatesForPercent(percent: number) {
  const x = Math.cos(2 * Math.PI * percent) * radius + center[0];
  const y = Math.sin(2 * Math.PI * percent) * radius + center[1];
  return [x, y];
}

function createPath(p1: Array<number>, p2: Array<number>, percent: number) {
  let flag = 0;
  if(percent > .5) {
      flag = 1
  }
  return 'M ' + p1[0] + ' ' + p1[1] + ' A ' + radius + ' ' + radius + ' 0 ' + flag + ' 1 ' +
    p2[0] + ' ' + p2[1] + ' L ' + center[0] + ' ' + center[1] + ' Z';
}

export class PdfGenerator {
  readonly columnWidth: number = 120;
  readonly textMargin: number = 20;
  y: number = 200;
  x: number = 50;
  period: Period.Period;

  title: String;
  doc: any;
  stream: any;

  constructor(period: Period.Period, title: String) {
    this.period = period
    this.title = title;
    this.doc = new pdfkit();
  }

  toFile() {
    const filename = '/raports/summary_' + this.period.year + "_" + (this.period.month+1) + '.pdf';
    const myPdfFile = admin.storage().bucket().file(filename);
    this.stream = this.doc.pipe(myPdfFile.createWriteStream());
  }

  generateHeader() {
    this.doc.fontSize(26).font('Times-Bold')
    this.doc.text(this.title, { align: 'center' })
    this.doc.moveDown()
    this.doc.fontSize(16)

    const d1 = new Date(this.period.start);
    const d2 = new Date(this.period.end);
    const from = d1.getDate() +'.'+  (d1.getMonth() + 1) + '.' + d1.getFullYear();
    const to = d2.getDate() +'.'+  (d2.getMonth() + 1) + '.' + d2.getFullYear();

    this.doc.text('Za okres ' + from + '-' + to, { align: 'center' })
    this.doc.fontSize(14).font('Times-Roman')
  }

  renderRow(labels: String[]) {
    for(let i = 0; i < 4; i++) {
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
    this.doc
      .fontSize(size)
      .font(name)
  }

  generateMonthlyReport(stats: DbMonthlyStats) {
    this.generateHeader();
    this.setFont(16, 'Times-Bold')
    this.renderRow(["Nazwa", "Srednia", "Lacznie", "Udzial w diecie"])
    this.setFont(14, 'Times-Roman')

    const total = stats.total;
    this.renderRow(metrics(Nutrient.Calories, stats.kcalAvg, stats.kcal, total))
    this.renderRow(metrics(Nutrient.Lct, stats.lctAvg, stats.lct, total))
    this.renderRow(metrics(Nutrient.Mct, stats.mctAvg, stats.mct, total))
    this.renderRow(metrics(Nutrient.Carbohydrates, stats.carbohydratesAvg, stats.carbohydrates, total))
    this.renderRow(metrics(Nutrient.Protein, stats.proteinAvg, stats.protein, total))
    this.renderRow(metrics(Nutrient.Roughage, stats.roughageAvg, stats.roughage, total))
    this.renderRow(metrics(Nutrient.Salt, stats.saltAvg, stats.salt, total))
  }

  chart() {
    const slices = [
        { percent: 0.6825, color: '#293b95', label: 'Weglowodany' },
        { percent: 0.0221, color: '#1aa2b7', label: 'LCT' },
        { percent: 0.1201, color: '#a8ce57', label: 'MCT' },
        { percent: 0.155, color: '#fcae31', label: 'Bialko' },
        { percent: 0.0166, color: '#eb2130', label: 'Blonnik' },
        { percent: 0.0036, color: '#ba1171', label: 'Sol' },
    ];
    let percentCumulative = 0.0;

    this.doc.addPage()
    this.doc.fontSize(24).text('Wykres')
    //LEGEND
    for (let i = 0; i < slices.length; i++)  {
        this.doc.rect(40, legendStart + 24 * (i), 20, 20).fill(slices[i].color)
        this.doc.fontSize(12)
               .fillColor('black')
               .text(slices[i].label, 70, legendStart + 6 + 24 * i)
    }
    //DRAW CHART
    for (const slice of slices) {
        const p1 = getCoordinatesForPercent(percentCumulative);
        percentCumulative += slice.percent;
        const p2 = getCoordinatesForPercent(percentCumulative);

        this.doc.path(createPath(p1, p2, slice.percent))
              .fill(slice.color)
    }
  }

  async meals() {


    // try {
    //   const meals = await Utils.mealsPromise(this.period);
    //
    //
    //
    // } catch(error) {
    //   console.log(error);
    //
    // }
  }

  end() {
    this.doc.end();
  }
}
