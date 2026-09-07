const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const { initializeApp } = require("firebase-admin/app");
const { getMessaging } = require("firebase-admin/messaging");

initializeApp();

function bilingual(si, en, fallback) {
  if (si && en) return `${si} | ${en}`;
  return si || en || fallback;
}

exports.notifyAnnouncement = onDocumentCreated("announcements/{announcementId}", async (event) => {
  const data = event.data?.data();
  if (!data || data.notifyUsers === false) return;

  const title = bilingual(data.titleSi, data.titleEn, "Kolalagala Ancient Temple");
  const body = bilingual(data.messageSi, data.messageEn, "New temple announcement");

  await getMessaging().send({
    topic: "temple_all",
    notification: { title, body },
    data: {
      type: "announcement",
      id: event.params.announcementId
    },
    android: {
      priority: "high",
      notification: { channelId: "temple_announcements" }
    }
  });
});

exports.notifyEvent = onDocumentCreated("events/{eventId}", async (event) => {
  const data = event.data?.data();
  if (!data || data.notifyUsers === false) return;

  const title = bilingual(data.titleSi, data.titleEn, "New Temple Event");
  const detail = [data.date, data.time].filter(Boolean).join(" • ");
  const body = detail || bilingual(data.descriptionSi, data.descriptionEn, "New temple program");

  await getMessaging().send({
    topic: "temple_all",
    notification: { title, body },
    data: {
      type: "event",
      id: event.params.eventId
    },
    android: {
      priority: "high",
      notification: { channelId: "temple_announcements" }
    }
  });
});
