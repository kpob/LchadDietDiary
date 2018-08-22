"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const functions = require("firebase-functions");
const admin = require("firebase-admin");
const Period = require("./../period");
const nodemailer = require('nodemailer');
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
exports.newMealNotification = functions.database.ref('meals/{mealId}').onCreate((snap, context) => {
    const data = snap.val();
    const senderToken = data.senderToken;
    const mealType = data.name;
    // Get the list of device notification tokens.
    return admin.database().ref('users').once('value').then(result => {
        // Check if there are any device tokens.
        if (!result.hasChildren()) {
            return console.log('There are no notification tokens to send to.');
        }
        // Notification details.
        const payload = getNotificationPayload(mealType);
        // Listing all tokens.
        const tokens = Object.keys(result.val());
        //remove senderToken
        const senderTokenIdx = tokens.indexOf(senderToken);
        if (senderTokenIdx > -1) {
            tokens.splice(senderTokenIdx, 1);
        }
        if (tokens.length === 0) {
            return console.log("Sender is the only user - do not send a notification");
        }
        return admin.messaging().sendToDevice(tokens, payload).then(response => {
            console.log("send notification");
        });
    });
});
exports.montlyStatsEmail = functions.https.onRequest((req, res) => {
    let mailOptions = {
        from: `${APP_NAME} <noreply@firebase.com>`,
        to: "krzysztof.pobiarzyn@gmail.com",
        subject: 'Podsumowanie miesiąca',
        text: 'Hej Mania! Teraz jeszcze wysyłam głupotki, ale kiedyś wyślę super rzeczy!',
        html: '<p><b>Hej</b> za nami kolejny trudny miesiąc, ale znowu daliśmy radę!</p>' +
            '<p>W tym miesiącu Staś zjadł 1000 kcal, co daje średnio 100 kcal na dobę!</p>' +
            '<p>Największy apetyt miał 21.08 kiedy to zjadł aż 2000 kcal</p>' +
            '<p>Pobierz szczegółowy raport</p>',
    };
    mailTransport.sendMail(mailOptions).then(() => {
        let lastMonth = Period.lastMonth();
        let stats = lastMonth.stats().then(result => {
            res.setHeader('Content-Type', 'application/json');
            res.send(JSON.stringify({
                result: result
            }));
        });
    });
});
function getNotificationPayload(mealType) {
    let body = "";
    switch (mealType) {
        case "DESSERT": body = "Pyszna kaszka!";
        case "DINNER": body = "Smakowity obiadek!";
        case "MILK": body = "Dobre mleczko!";
        default: body = "Mniam! Mniam!";
    }
    return {
        notification: {
            title: 'Staś zajada!',
            body: body,
            sound: 'mniam',
            android_channel_id: 'Default'
        }
    };
}
