const test = require('firebase-functions-test')();

const functions = require('firebase-functions');
const year = functions.config().date.year;
const month = functions.config().date.month;
const day = functions.config().date.day;

test.mockConfig({ date: { year: 2018 }, { month: 2}, { day: 20 }});

adminInitStub = sinon.stub(admin, 'initializeApp');

const myFunctions = require('../index.js');
