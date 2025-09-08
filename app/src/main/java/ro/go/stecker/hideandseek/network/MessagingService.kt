package ro.go.stecker.hideandseek.network

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ro.go.stecker.hideandseek.MainActivity
import ro.go.stecker.hideandseek.R
import ro.go.stecker.hideandseek.data.CardsRepository
import ro.go.stecker.hideandseek.data.firestore.PlayerType
import kotlin.random.Random

val newCurseTextVariants = listOf(
    R.string.new_curse_1,
    R.string.new_curse_2,
    R.string.new_curse_3,
    R.string.new_curse_4
)

val expiredCurseTextVariants = mapOf(
    PlayerType.Hider to listOf(
        R.string.expired_curse_hider_1,
        R.string.expired_curse_hider_2,
        R.string.expired_curse_hider_3
    ),
    PlayerType.Seeker to listOf(
        R.string.expired_curse_seeker_1,
        R.string.expired_curse_seeker_2,
        R.string.expired_curse_seeker_3
    )
)

class MessagingService: FirebaseMessagingService() {

//    override fun onNewToken(token: String) {
//        super.onNewToken(token)
//        Log.d(TAG, "Refreshed token: $token")
//    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        message.notification?.let {
            if(it.title != null && it.body != null)
                showNotification(it.title!!, it.body!!)
        }

        if(message.data.isNotEmpty())
            handleDataMessage(message.data)
    }

    fun showNotification(title: String, body: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT)

        val channelId = "Default"

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelName = "Curse Updates"

        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        manager.createNotificationChannel(channel)

        manager.notify(Random.nextInt(), notificationBuilder)
    }

    fun handleDataMessage(data: Map<String, String>) {
        when(data["type"]) {
            "play" -> {
                showNotification(
                    title = getString(R.string.new_curse_title),
                    body =
                        getString(
                            newCurseTextVariants[Random.nextInt(0, 3)],
                            data["sender"],
                            getString(CardsRepository.first{ it.id == data["id"]?.toIntOrNull() }.name)
                        )
                )
            }
            "delete" -> {
                val playerType =
                    if(data["receiver"] == "hider") PlayerType.Hider
                    else PlayerType.Seeker

                showNotification(
                    title = getString(R.string.expired_curse_title),
                    body =
                        getString(
                            expiredCurseTextVariants[playerType]?.get(Random.nextInt(0, 2)) ?: 0,
                            getString(CardsRepository.first{ it.id == data["id"]?.toIntOrNull() }.name)
                        )
                )
            }
            "time" -> {
                val text =
                    if(data["time"] == "5") R.string.time_left_five
                    else R.string.time_left_ten

                showNotification(
                    title = getString(R.string.time_left_title),
                    body =
                        getString(
                            text,
                            getString(CardsRepository.first{ it.id == data["id"]?.toIntOrNull() }.name)
                        )
                )
            }
        }
    }

}