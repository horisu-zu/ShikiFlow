package com.example.shikiflow.utils.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelGroupCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.shikiflow.R

object NotificationUtils {
    fun Context.createNotificationChannel(
        id: String,
        name: String,
        description: String,
        groupId: String,
        groupName: String,
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT
    ) {
        with(NotificationManagerCompat.from(applicationContext)) {
            createNotificationChannelGroup(
                NotificationChannelGroup(groupId, groupName)
            )

            val channel = NotificationChannel(id, name, importance).apply {
                this.description = description
                this.group = group
            }

            createNotificationChannel(channel)
        }
    }

    fun Context.showNotification(
        notificationId: Int,
        channelId: String,
        title: String,
        text: String,
        largeIcon: Bitmap? = null,
        bigPicture: Bitmap? = null,
        style: NotificationCompat.Style? = null,
        pendingIntent: PendingIntent? = null,
        group: String? = null,
        isGroupSummary: Boolean = false
    ): Boolean {
        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_shikilogo)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setGroup(group)
            .setGroupSummary(isGroupSummary)
            .setLargeIcon(largeIcon)

        when {
            bigPicture != null -> builder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bigPicture)
                    .bigLargeIcon(null as Bitmap?)
            )
            style != null -> builder.setStyle(style)
        }

        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }

            notify(notificationId, builder.build())

            return true
        }
    }
}