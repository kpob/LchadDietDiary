import * as admin from 'firebase-admin';

export class Period {
  start: number;
  end: number;
  year: number;
  month: number;
  day: number;

  constructor(d1: Date, d2: Date) {
    this.start = d1.getTime();
    this.end = d2.getTime();
    this.year = d1.getFullYear();
    this.month = d1.getMonth();
    this.day = d1.getDate();

console.log(d1)
console.log(d2)
    console.log(this)
  }

	mealsPromise() {
		return admin.database().ref('meals')
			.orderByChild('time')
			.startAt(this.start, 'time')
			.endAt(this.end, 'time')
			.once('value')
	};

  mealsPromiseWithLimit(limit: number) {
		return admin.database().ref('meals')
			.orderByChild('time')
			.startAt(this.start, 'time')
			.endAt(this.end, 'time')
      .limitToLast(limit)
			.once('value')
	};

  stats() {
    return admin.database()
  		.ref('stats/daily/' + this.year + "/" + this.month + "/" + this.day)
  		.once('value')
  };

  daysDiff() {
    const timeDiff = Math.abs(this.end - this.start);
    return Math.ceil(timeDiff / (1000 * 3600 * 24));
  }
}

class TodayPeroid extends Period {

  constructor() {
    const d1 = new Date();
  	d1.setHours(0);
  	d1.setMinutes(0);

  	const d2 = new Date();
  	d2.setHours(23);
  	d2.setMinutes(59);

  	super(d1, d2);
  }
}

class DayPeriod extends Period {
	constructor(y: number, m: number, d: number) {
    console.log('day')
    console.log(m)
    const d1 = new Date(y, m, d);
  	d1.setHours(0);
  	d1.setMinutes(0);

  	const d2 = new Date(y, m, d);
  	d2.setHours(23);
  	d2.setMinutes(59);

  	super(d1, d2);
  }
}

class LastMonthPeriod extends Period {
  constructor() {
    const days = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];

  	const now = new Date();
  	let month = now.getMonth();
  	let year = now.getFullYear();

  	if(month === 0) {
  		month = 11;
  		year = year - 1;
  	} else {
  		month = month - 1;
  	}

  	const d1 = new Date();
  	d1.setMonth(month);
  	d1.setFullYear(year);
  	d1.setDate(1);
  	d1.setHours(0);
  	d1.setMinutes(0);

  	const d2 = new Date();
  	d2.setMonth(month);
  	d2.setFullYear(year);
  	d2.setDate(days[month]);
  	d2.setHours(23);
  	d2.setMinutes(59);

  	super(d1, d2);
  }
}

class MonthPeriod extends Period {
  constructor(year: number, month: number) {
  	const d1 = new Date(year, month, 1);
  	d1.setHours(0);
  	d1.setMinutes(0);

  	const d2 = new Date(year, month, 0);
  	d2.setHours(23);
  	d2.setMinutes(59);

  	super(d1, d2);
  }
}

class DaysPeriod extends Period {

  constructor(days: number) {
    const end = new Date();
    const oneDay = 86400000;
    const start = new Date(end.getTime() - oneDay * days);

    super(start, end);
  }
}

class TimePeriod extends Period {

  constructor(since: number) {
    super(new Date(since), new Date());
  }
}

export const today: () => Period = () => new TodayPeroid();
export const day: (y: number, m: number, d: number) => Period = (y, m, d) => new DayPeriod(y, m, d);
export const lastMonth: () => Period = () => new LastMonthPeriod();
export const days: (d: number) => Period = (d) => new DaysPeriod(d);
export const time: (since: number) => Period = (since) => new TimePeriod(since);
export const month: (y: number, m: number) => Period = (y, m) => new MonthPeriod(y, m);
