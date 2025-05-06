package com.example.semestralna_praca.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.semestralna_praca.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class DailyQuestReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.d("QuestReminder", "Worker spustený")

        val uid = Firebase.auth.currentUser?.uid ?: return Result.success()
        val db = Firebase.firestore

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val todayStart = calendar.time

        db.collection("users")
            .document(uid)
            .collection("quests")
            .whereEqualTo("done", false)
            .whereGreaterThanOrEqualTo("createdAt", todayStart)
            .get()
            .addOnSuccessListener { snapshot ->
                Log.d("QuestReminder", "Získané questy: ${snapshot.size()}")

                if (snapshot.isEmpty) {
                    showNotification(
                        "Nezabudni na quest!",
                        "Dnes si si ešte nevybral nový quest. Chceš to napraviť?"
                    )
                }
            }
            .addOnFailureListener { e ->
                Log.e("QuestReminder", "Chyba pri získavaní questov", e)
            }

        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "quest_reminder_channel"
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Quest Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        // 🔒 Skontroluj oprávnenie pre Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            applicationContext.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("QuestReminder", "Notifikácia zablokovaná – chýba oprávnenie POST_NOTIFICATIONS")
            return
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Uisti sa, že tento drawable existuje
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(1, notification)

        Log.d("QuestReminder", "Notifikácia zobrazená")
    }

}
