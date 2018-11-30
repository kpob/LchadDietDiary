import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

import * as Period from './../period';
import * as Stats from './../stats';
import { PdfGenerator } from './../views/pdf_generator';
import { DbMonthlyStats } from './../model/db';
import * as Utils from './../utils';

const nodemailer = require('nodemailer');
const base64 = require('base64-stream');

const gmailEmail = functions.config().gmail.email;
const gmailPassword = functions.config().gmail.password;
const mailTransport = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: gmailEmail,
    pass: gmailPassword,
  },
});
const APP_NAME = 'Stasiowy Dzienniczek';

export const newMealNotification = functions.database.ref('meals/{mealId}').onCreate((snap, context) => {
	const data = snap.val();

	const senderToken = data.senderToken;
	const mealType = data.name;
  let kcal = data.kcal;

  // Get the list of device notification tokens.
	return admin.database().ref('users').once('value').then(result => {
	    // Check if there are any device tokens.
	    if (!result.hasChildren()) {
	      return console.log('There are no notification tokens to send to.');
	    }
      if (kcal === 'undefined') {
        kcal = 0;
      }
	    const payload = getNotificationPayload(mealType, kcal);
    	const tokens = Object.keys(result.val());
			//remove senderToken
    	const senderTokenIdx = tokens.indexOf(senderToken);
    	if(senderTokenIdx > -1) {
    		tokens.splice(senderTokenIdx, 1);
    	}

    	if(tokens.length === 0) {
    	   return console.log("Sender is the only user - do not send a notification");
    	}

     	return admin.messaging().sendToDevice(tokens, payload).then(response => {
			     console.log("send notification");
			});
  });
});

export const montlyStatsEmail = functions.https.onRequest(async (req, res) => {
    const period: Period.Period = Period.lastMonth();
    let statsSnapshot = await Utils.montlyStatsPromise(period);
    const stats: DbMonthlyStats = statsSnapshot.val();

    const pdfGenerator = new PdfGenerator(
      period,
      'Stas Pobiarzyn - Raport miesieczny'
    );
    pdfGenerator.generateMonthlyReport(stats);

    let finalString = ''; // contains the base64 string
    let stream = pdfGenerator.doc.pipe(base64.encode());
    pdfGenerator.end();

    stream.on('data', function(chunk: String) {
        finalString += chunk;
    });

    stream.on('end', function() {
      // the stream is at its end, so push the resulting base64 string to the response
  		const mailOptions = {
  	    from: `${APP_NAME} <noreply@firebase.com>`,
  	    to: "Krzysztof Pobiarżyn <krzysztof.pobiarzyn@gmail.com>, Mania Bzdęga <mania.bzdega@gmail.com>,",
  			subject: 'Podsumowanie miesiąca',
  			html:
              '<p>Hej!</p>' +
              '<p>Za nami kolejny trudny miesiąc, dużo nowych wrażeń, emocji, ale znowu daliśmy radę!</p>' +
              '<p>W tym miesiącu Staś zjadł aż <b>' + Stats.formattedValue(stats.kcal, "kcal") + '</b>' +
              ', co daje średnio <b>' + Stats.formattedValue(stats.kcalAvg, "kcal") + '</b> na dobę!</p>' +
  						'<p>Szczegółowy raport w załączniku</p>',
        attachments: [
          {   // stream as an attachment
            filename: 'raport.pdf',
            content: finalString,
            encoding: 'base64'
          }
        ]
  	  };

  	  mailTransport.sendMail(mailOptions).then(() => {
  			res.json(finalString);
  	  });
  });
});

function getNotificationPayload(mealType: string, kcal: number): any {
	let body: string = "";

  if (kcal > 0) {
    switch (mealType) {
        console.log("save " + mealType);
  	    case "DESSERT": body = "Deserek " + kcal.toFixed(1) + "kcal";
  	    case "DINNER": body = "Obiadek " + kcal.toFixed(1) + "kcal";
  	    case "MILK": body = "Mleczko! " + kcal.toFixed(1) + "kcal";
  	    default: body = "Mniam! Mniam! " + kcal.toFixed(1) + "kcal";
  	}
  } else {
  	switch (mealType) {
  	    case "DESSERT": body = "Pyszna kaszka!";
  	    case "DINNER": body = "Smakowity obiadek!";
  	    case "MILK": body = "Dobre mleczko!";
  	    default: body = "Mniam! Mniam!";
  	}
  }

	return {
		notification: {
			title: 'Staś zajada!',
			body: body,
			sound: 'mniam',
			android_channel_id: 'Default'
		}
	}
}
