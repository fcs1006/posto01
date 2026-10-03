package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationHelper {

    private const val CHANNEL_PROMOS_ID = "autoposto_promos"
    private const val CHANNEL_PROMOS_NAME = "Promoções do Clube 01"
    private const val CHANNEL_PUMP_ID = "autoposto_pump"
    private const val CHANNEL_PUMP_NAME = "Abastecimento & Pista Petros"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val promoChannel = NotificationChannel(
                CHANNEL_PROMOS_ID,
                CHANNEL_PROMOS_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Receba promoções exclusivas personalizadas para seu veículo"
            }

            val pumpChannel = NotificationChannel(
                CHANNEL_PUMP_ID,
                CHANNEL_PUMP_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificações em tempo real sobre bico liberado e pagamento aprovado"
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(promoChannel)
            notificationManager.createNotificationChannel(pumpChannel)
        }
    }

    fun showPumpNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int = 101
    ) {
        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val builder = NotificationCompat.Builder(context, CHANNEL_PUMP_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            notificationManager.notify(notificationId, builder.build())
        } catch (e: Exception) {
            // Notifications might be silenced or blocked by user
        }
    }

    fun showPromoNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int = 102
    ) {
        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val builder = NotificationCompat.Builder(context, CHANNEL_PROMOS_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            notificationManager.notify(notificationId, builder.build())
        } catch (e: Exception) {
            // Suppress if permissions not granted
        }
    }
}
