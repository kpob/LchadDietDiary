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
const functions = require("firebase-functions");
const admin = require("firebase-admin");
const Utils = require("./../utils");
const Stats = require("./../stats");
const Period = require("./../period");
const pdfkit = require('pdfkit');
exports.mealsView = functions.https.onRequest((req, res) => __awaiter(this, void 0, void 0, function* () {
    const time = req.query.since;
    let p = Period.days(7);
    if (typeof time !== 'undefined') {
        p = Period.time(Number.parseInt(time));
    }
    try {
        const meals = yield Utils.mealsPromise(p);
        res.setHeader('Content-Type', 'application/json');
        res.send(JSON.stringify(meals.filter(m => m.time >= p.start)));
    }
    catch (error) {
        console.log(error);
        res.status(500).send(error);
    }
}));
exports.dailyStatsView = functions.https.onRequest((req, res) => __awaiter(this, void 0, void 0, function* () {
    const y = req.query.year;
    const m = req.query.month;
    const d = req.query.day;
    const period = Period.day(y, m, d);
    try {
        let statsSnapshot = yield Utils.dailyStatsPromise(period);
        const response = Object.assign({}, statsSnapshot.val());
        res.setHeader('Content-Type', 'application/json');
        res.send(response);
    }
    catch (error) {
        res.status(500).send(error);
    }
}));
exports.pdf = functions.https.onRequest((req, res) => __awaiter(this, void 0, void 0, function* () {
    const period = Period.lastMonth();
    try {
        let statsSnapshot = yield Utils.montlyStatsPromise(period);
        const dbStats = statsSnapshot.val();
        let stats = new Stats.MonthlyStats([], period);
        stats.kcal = dbStats.kcal;
        stats.lct = dbStats.lct;
        stats.mct = dbStats.mct;
        stats.carbohydrates = dbStats.carbohydrates;
        stats.protein = dbStats.protein;
        stats.roughage = dbStats.roughage;
        stats.salt = dbStats.salt;
        var pdfGenerator = new PdfGenerator(period, 'Stas Pobiarzyn - Raport miesieczny');
        console.log("Period");
        console.log(period);
        console.log(stats);
        pdfGenerator.generateHeader();
        pdfGenerator.setFont(16, 'Times-Bold');
        pdfGenerator.renderRow(["Nazwa", "Srednia", "Lacznie", "Udzial w diecie"]);
        pdfGenerator.setFont(14, 'Times-Roman');
        pdfGenerator.renderRow(stats.kcalMetrics());
        pdfGenerator.renderRow(stats.lctMetrics());
        pdfGenerator.renderRow(stats.mctMetrics());
        pdfGenerator.renderRow(stats.carbohydratesMetrics());
        pdfGenerator.renderRow(stats.proteinMetrics());
        pdfGenerator.renderRow(stats.roughageMetrics());
        pdfGenerator.renderRow(stats.saltMetrics());
        pdfGenerator.end();
        res.setHeader('Content-Type', 'application/json');
        res.status(200).send(stats);
    }
    catch (error) {
        console.log(error);
        res.status(500).send(error);
    }
}));
class PdfGenerator {
    constructor(period, title) {
        this.columnWidth = 120;
        this.textMargin = 20;
        this.y = 200;
        this.x = 50;
        this.period = period;
        this.title = title;
        const time = new Date().getTime();
        const filename = '/test/summary' + time + '.pdf';
        const myPdfFile = admin.storage().bucket().file(filename);
        this.doc = new pdfkit();
        const stream = this.doc.pipe(myPdfFile.createWriteStream());
    }
    generateHeader() {
        this.doc.fontSize(26).font('Times-Bold');
        this.doc.text(this.title, { align: 'center' });
        this.doc.moveDown();
        this.doc.fontSize(16);
        let d1 = new Date(this.period.start);
        let d2 = new Date(this.period.end);
        let from = d1.getDate() + '.' + (d1.getMonth() + 1) + '.' + d1.getFullYear();
        let to = d2.getDate() + '.' + (d2.getMonth() + 1) + '.' + d2.getFullYear();
        this.doc.text('Za okres ' + from + '-' + to, { align: 'center' });
        this.doc.fontSize(14).font('Times-Roman');
    }
    renderRow(labels) {
        console.log("labels");
        console.log(labels);
        for (var i = 0; i < 4; i++) {
            this.doc.text(labels[i], 72 + i * this.columnWidth, this.y + this.textMargin, {
                width: this.columnWidth,
                align: 'center'
            })
                .rect(this.doc.x, this.y, this.columnWidth, 50)
                .stroke();
        }
        this.y += 50;
    }
    setFont(size, name) {
        console.log("set fonte " + size);
        this.doc.fontSize(size).font(name);
    }
    end() {
        this.doc.end();
    }
}
function daysInMonth(month, year) {
    return new Date(year, month, 0).getDate();
}
