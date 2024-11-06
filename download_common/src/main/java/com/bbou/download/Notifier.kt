/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bbou.download.common.R
import java.util.Locale
import kotlin.random.Random

/**
 * Notifier.
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class Notifier {

    /**
     * Notification type
     */
    enum class NotificationType {

        /**
         * Start
         */
        START,

        /**
         * Update
         */
        UPDATE,

        /**
         * Finish
         */
        FINISH,

        /**
         * Cancel
         */
        CANCEL
    }

    /**
     * Cancel UI notification
     *
     * @param notificationId notificationId
     */
    fun cancelNotification(context: Context, notificationId: Int) {
        if (notificationId != 0) {
            val manager = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            manager.cancel(notificationId)
        }
    }

    companion object {

        private const val TAG = "Notifier"

        /**
         * Channel id key
         */
        private const val CHANNEL_ID = "download_notification_channel"

        /**
         * Id for the current notification
         */
        fun notificationId() = Random.nextInt(Int.MAX_VALUE)

        /**
         * Fire UI notification
         *
         * @param notificationId notification id
         * @param type           notification
         * @param args           arguments
         */
        fun fireNotification(context: Context, notificationId: Int, type: NotificationType, contentText0: String?, vararg args: Any) {
            // get an instance of the NotificationManager service
            var contentText = contentText0
            val manager = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)

            // content
            val contentTitle = context.getString(R.string.title_download)

            // notification
            val notification: Notification?
            when (type) {
                NotificationType.START -> {
                    contentText += ' '.toString() + context.getString(R.string.status_download_running)
                    notification = makeNotificationStartOrUpdate(context, contentTitle, contentText, notificationId)
                }

                NotificationType.UPDATE -> {
                    //val downloaded = args[0] as Float
                    //val percent = (downloaded * 100).toInt()
                    contentText += ' '.toString() // + context.getString(R.string.status_download_running) + ' ' + percent + '%'
                    notification = makeNotificationStartOrUpdate(context, contentTitle, contentText, notificationId)
                }

                NotificationType.FINISH -> {
                    val success = args[0] as Boolean
                    contentText += ' '.toString() + context.getString(if (success) R.string.status_download_successful else R.string.status_download_fail)
                    notification = makeNotificationFinish(context, contentTitle, contentText)

                    // cancel previous
                    manager.cancel(notificationId)
                }

                NotificationType.CANCEL -> {
                    manager.cancel(notificationId)
                    return
                }
            }

            // issue notification
            if (notification != null) {
                Log.d(TAG, "Notification id=" + notificationId + " type=" + type.name)
                manager.notify(notificationId, notification)
            }
        }

        private fun makeNotificationStartOrUpdate(context: Context, contentTitle: String?, contentText: String?, id: Int): Notification? {
            try {
                // builder
                val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                //.setColor(some color)
                // intent
                val intent: Intent = makeCancelIntent(context, id)

                // pending intent
                val pendingIntent: PendingIntent = makePendingIntent(context, intent)

                // action
                val action = NotificationCompat.Action.Builder(R.drawable.ic_notif_cancel, context.getString(R.string.action_cancel).uppercase(Locale.getDefault()), pendingIntent).build()
                builder.addAction(action)
                return builder.build()
            } catch (ignored: SecurityException) {
            }
            return null
        }

        private fun makeNotificationFinish(context: Context, contentTitle: String?, contentText: String?): Notification? {
            try {
                // builder
                val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                    .setSmallIcon(android.R.drawable.stat_sys_download_done)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)

                // build notification
                return builder.build()
            } catch (ignored: SecurityException) {
            }
            return null
        }

        /**
         * Init notification channel
         *
         * @param context context
         */
        fun initChannels(context: Context) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                return
            }
            val channel = NotificationChannel(CHANNEL_ID, "Download", NotificationManager.IMPORTANCE_LOW)
            channel.description = "Download Channel"
            channel.setSound(null, null)
            val notificationManager = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            notificationManager.createNotificationChannel(channel)
        }

        // N O T I F I C A T I O N

        /**
         * Notification id key
         */
        private const val NOTIFICATION_ID = "notification_id"

        // C A N C E L   B R O A D C A S T

        /**
         * Cancel download action
         */
        const val ACTION_DOWNLOAD_CANCEL = "action_cancel_download"

        /**
         * Make cancel intent
         *
         * @param context context
         * @param notificationId notification id
         * @return
         */
        fun makeCancelIntent(context: Context, notificationId: Int): Intent {
            val intent = Intent()
            intent.setPackage(context.applicationContext.packageName)
            intent.action = ACTION_DOWNLOAD_CANCEL
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.putExtra(NOTIFICATION_ID, notificationId)
            return intent
        }

        /**
         * MAke pending intent
         *
         * @param context context
         * @param intent payload intent
         * @return pending intent
         */
        fun makePendingIntent(context: Context, intent: Intent): PendingIntent {
            val uid = System.currentTimeMillis().toInt()
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT else
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_CANCEL_CURRENT
            return PendingIntent.getBroadcast(context, uid, intent, flags)
        }

        /**
         * Request cancel
         *
         * @param context context
         * @param notificationId notification id
         */
        fun requestCancel(context: Context, notificationId: Int) {
            val intent = makeCancelIntent(context, notificationId)
            Log.d(TAG, "Sending cancel request (broadcast intent)")
            context.sendBroadcast(intent)
        }

        /**
         * Request pending cancel
         *
         * @param context context
         * @param notificationId notification id
         */
        fun requestPendingCancel(context: Context, notificationId: Int) {
            try {
                val intent = makeCancelIntent(context, notificationId)
                val pendingIntent = makePendingIntent(context, intent)
                Log.d(TAG, "Pending intent $pendingIntent")
                Log.d(TAG, "Pending intent creator package " + pendingIntent.creatorPackage)
                Log.d(TAG, "Sending cancel request (sent pending intent)")
                // pendingIntent.send()
                pendingIntent.send(1313, { _: PendingIntent?, _: Intent?, resultCode: Int, _: String?, _: Bundle? -> Log.d(TAG, "Sent pending intent $resultCode") }, null)
            } catch (e: PendingIntent.CanceledException) {
                throw RuntimeException(e)
            }
        }
    }
}
