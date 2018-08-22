!function(e,t){for(var s in t)e[s]=t[s]}(this,function(e){var t={};function s(n){if(t[n])return t[n].exports;var i=t[n]={i:n,l:!1,exports:{}};return e[n].call(i.exports,i,i.exports,s),i.l=!0,i.exports}return s.m=e,s.c=t,s.d=function(e,t,n){s.o(e,t)||Object.defineProperty(e,t,{configurable:!1,enumerable:!0,get:n})},s.r=function(e){Object.defineProperty(e,"__esModule",{value:!0})},s.n=function(e){var t=e&&e.__esModule?function(){return e.default}:function(){return e};return s.d(t,"a",t),t},s.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)},s.p="",s.w={},s(s.s=8)}([function(e,t){e.exports=require("firebase-admin")},function(e,t,s){"use strict";Object.defineProperty(t,"__esModule",{value:!0});const n=s(0);class i{constructor(e,t){this.start=e.getTime(),this.end=t.getTime(),this.year=e.getFullYear(),this.month=e.getMonth(),this.day=e.getDate()}mealsPromise(){return n.database().ref("meals").orderByChild("time").startAt(this.start,"time").endAt(this.end,"time").once("value")}mealsPromiseWithLimit(e){return n.database().ref("meals").orderByChild("time").startAt(this.start,"time").endAt(this.end,"time").limitToLast(e).once("value")}}t.Period=i;t.today=(()=>new class extends i{constructor(){const e=new Date;e.setHours(0),e.setMinutes(0);const t=new Date;t.setHours(23),t.setMinutes(59),super(e,t)}}),t.day=((e,t,s)=>new class extends i{constructor(e,t,s){const n=new Date;n.setFullYear(e),n.setMonth(t),n.setDate(s),n.setHours(0),n.setMinutes(0);const i=new Date;i.setFullYear(e),i.setMonth(t),i.setDate(s),i.setHours(23),i.setMinutes(59),super(n,i)}}(e,t,s)),t.lastMonth=(()=>new class extends i{constructor(){const e=new Date;let t=e.getMonth(),s=e.getFullYear();0===t?(t=11,s-=1):t-=1;const n=new Date;n.setMonth(t),n.setFullYear(s),n.setDate(1),n.setHours(0),n.setMinutes(0);const i=new Date;i.setMonth(t),i.setFullYear(s),i.setDate([31,28,31,30,31,30,31,31,30,31,30,31][t]),i.setHours(23),i.setMinutes(59),super(n,i)}}),t.days=(e=>new class extends i{constructor(e){const t=new Date;super(new Date(t.getTime()-864e5*e),t)}}(e)),t.time=(e=>new class extends i{constructor(e){super(new Date(e),new Date)}}(e))},function(e,t,s){"use strict";Object.defineProperty(t,"__esModule",{value:!0});const n=s(0),i=s(6),a=()=>n.database().ref("ingredients").once("value");function r(e){const t=[];return e.hasChildren()&&(e.forEach(e=>{t.push(e.val())}),t.filter(e=>!1===e.deleted)),t}function o(e){const t=[];return e.hasChildren()&&(e.forEach(e=>{t.push(e.val())}),t.filter(e=>!1===e.deleted),t.forEach(e=>{e.ingredients=e.ingredients.filter(e=>!1===e.deleted)})),t}t.updateStats=function(e,t){a().then(s=>{const n=r(s);e.mealsPromise().then(e=>{const s=o(e).map(e=>new i.Meal(e,n));t(s).persist()})})},t.doOnMeals=function(e,t){a().then(s=>{const n=r(s);e.mealsPromise().then(e=>{const s=o(e).map(e=>new i.Meal(e,n));t(s)})})}},function(e,t){e.exports=require("firebase-functions")},function(e,t){e.exports=require("pdfkit")},function(e,t,s){"use strict";Object.defineProperty(t,"__esModule",{value:!0});const n=s(3),i=s(2),a=s(1);s(4);t.mealsView=n.https.onRequest((e,t)=>{const s=e.query.since;let n=a.days(7);void 0!==s&&(n=a.time(Number.parseInt(s))),i.doOnMeals(n,e=>{t.setHeader("Content-Type","application/json"),t.send(JSON.stringify(e.filter(e=>e.time>=n.start)))})}),t.pdf=n.https.onRequest((e,t)=>(console.log("EEEEE"),t.status(200).send("HHHHHEEEEJJJ")))},function(e,t,s){"use strict";Object.defineProperty(t,"__esModule",{value:!0});t.Meal=class{constructor(e,t){this.name=e.name,this.time=e.time,this.ingredients=e.ingredients.map(e=>new n(e,t)),this.kcal=this.ingredients.map(e=>e.weight/100*e.ingredient.calories).reduce((e,t)=>e+t),this.mct=this.ingredients.map(e=>e.weight/100*e.ingredient.mtc).reduce((e,t)=>e+t),this.lct=this.ingredients.map(e=>e.weight/100*e.ingredient.lct).reduce((e,t)=>e+t)}};class n{constructor(e,t){this.weight=e.weight;const s=t.find(t=>t.id===e.ingredientId);this.ingredient=void 0===s?{id:"",name:"",calories:0,lct:0,mtc:0,carbohydrates:0,protein:0,roughage:0,salt:0,deleted:!0}:s}}t.MealIngredient=n},function(e,t,s){"use strict";Object.defineProperty(t,"__esModule",{value:!0});const n=s(0),i=s(1),a=(e,t)=>e.map(e=>((e,t)=>e.ingredients.map(e=>e.weight/100*t(e.ingredient)).reduce((e,t)=>e+t))(e,t)).reduce((e,t)=>e+t);t.monthly=((e,s)=>new class extends r{constructor(e,s){super(e,s);const n=new Date(s.end).getDate(),a=Array.from({length:n},(e,t)=>t+1).map(e=>{const t=new Date(s.end);return i.day(t.getFullYear(),t.getMonth(),e)}).map(s=>t.daily(e.filter(e=>e.time>=s.start&&e.time<=s.end),s));a.sort((e,t)=>e.kcal-t.kcal);const r=a.map(e=>e.kcal)[(a.length-1)/2],o=a.filter(e=>e.kcal>.8*r),c=o.length;this.kcalAvg=o.map(e=>e.kcal).reduce((e,t)=>e+t)/c,this.mctAvg=o.map(e=>e.mct).reduce((e,t)=>e+t)/c,this.lctAvg=o.map(e=>e.lct).reduce((e,t)=>e+t)/c}persist(){n.database().ref("stats/monthly/"+this.period.year+"/"+(this.period.month+1)).set(this)}}(e,s)),t.daily=((e,t)=>new class extends r{constructor(e,t){super(e,t),this.meals=e}persist(){n.database().ref("stats/daily/"+this.period.year+"/"+(this.period.month+1)+"/"+this.period.day).set(this)}}(e,t));class r{constructor(e,t){this.kcal=a(e,e=>e.calories),this.lct=a(e,e=>e.lct),this.mct=a(e,e=>e.mtc),this.carbohydrates=a(e,e=>e.carbohydrates),this.protein=a(e,e=>e.protein),this.roughage=a(e,e=>e.roughage),this.salt=a(e,e=>e.salt),this.period=t}}t.Stats=r},function(e,t,s){"use strict";Object.defineProperty(t,"__esModule",{value:!0});const n=s(3),i=s(0),a=s(1),r=s(7),o=s(2),c=s(5);i.initializeApp(),t.sendMealNotification=n.database.ref("meals/{mealId}").onCreate((e,t)=>{console.log("dsdsd");const s=e.val(),n=s.senderToken,c=s.name;return i.database().ref("users").once("value").then(e=>{if(!e.hasChildren())return console.log("There are no notification tokens to send to.");const t=function(e){let t="";switch(e){case"DESSERT":t="Pyszna kaszka!";case"DINNER":t="Smakowity obiadek!";case"MILK":t="Dobre mleczko!";default:t="Mniam! Mniam!"}return{notification:{title:"Staś zajada!",body:t,sound:"mniam",android_channel_id:"Default"}}}(c),s=Object.keys(e.val()),l=s.indexOf(n);if(l>-1&&s.splice(l,1),0===s.length)return console.log("Sender is the only user - do not send a notification");const d=a.today();return o.updateStats(d,e=>r.daily(e,d)),i.messaging().sendToDevice(s,t).then(e=>{console.log("send notification")})})}),t.monthlyStats=n.https.onRequest((e,t)=>{console.log("UUUUU");const s=a.lastMonth();o.updateStats(s,e=>r.monthly(e,s)),t.status(200).send("ok\n")}),t.dailyStats=n.https.onRequest((e,t)=>{const s=e.query.year,n=e.query.month,i=e.query.day,c=a.day(s,n,i);o.updateStats(c,e=>r.daily(e,c)),t.status(200).send("ok\n")}),t.getMeals=c.mealsView,t.generatePdf=c.pdf}]));