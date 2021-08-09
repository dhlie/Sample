package com.dhl.base.utils

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import com.dhl.base.ContextHolder

/**
 *
 * Author: duanhaoliang
 * Create: 2021/7/19 14:13
 * Description:
 *
 */
class NotificationUtil private constructor(){
    companion object {

        /**
         * 创建通知渠道
         */
        fun createChannel(channelId: String, channelName: String, importance: Int, channelInit: ((NotificationChannel) -> Unit)? = null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, channelName, importance)
                channelInit?.invoke(channel)
                NotificationManagerCompat.from(ContextHolder.appContext).createNotificationChannel(channel)
            }
        }

        /**
         * app 通知是否开启
         */
        fun areNotificationsEnabled(context: Context): Boolean {
            return NotificationManagerCompat.from(context).areNotificationsEnabled()
        }

        /**
         * 打开通知设置页面
         */
        fun navigateToNotificationSetting(context: Activity) {
            // for Android 4 and below
            val KEY_PACKAGE = "package"
            // for Android 5-7
            val KEY_APP_PACKAGE = "app_package"
            val KEY_APP_UID = "app_uid"
            // for Android O
            val KEY_EXTRA_APP_PACKAGE = "android.provider.extra.APP_PACKAGE"
            val packageName = context.packageName
            val intent = Intent()
            // try to navigate to the notification settings screen first.
            // It should work on Android 5.0 and above, but might not on below.
            try {
                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                intent.putExtra(KEY_APP_PACKAGE, packageName)
                intent.putExtra(KEY_APP_UID, context.applicationInfo.uid)
                intent.putExtra(KEY_EXTRA_APP_PACKAGE, packageName)
                val resolveInfo = context.packageManager.resolveActivity(intent, 0)
                if (resolveInfo != null) {
                    context.startActivity(intent)
                } else {
                    // if above can't navigate to the destination, then navigate to the application detail screen,
                    // which will cost one more step into the notification settings.
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    intent.data = Uri.fromParts(KEY_PACKAGE, packageName, null)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }

        /**
         * 渠道通知开关是否开启
         */
        fun isChannelEnabled(context: Context, channelId: String): Boolean {
            return if (areNotificationsEnabled(context)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationManagerCompat.from(ContextHolder.appContext).getNotificationChannel(channelId) ?: return false
                    return channel.importance > NotificationManager.IMPORTANCE_NONE
                } else {
                    true
                }
            } else {
                false
            }
        }

        /**
         * 打开渠道通知设置页面
         */
        fun navigateToChannelNotificationSetting(context: Activity, channelId: String) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId)
                context.startActivity(intent)
            }
        }

    }
}