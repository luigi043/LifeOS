package com.luigi.lifeos.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService

/**
 * One channel per *user-visible category* so users can disable
 * "Bill reminders" without losing "Workout reminders".
 *
 * Register early — call once from Application.onCreate.
 */
object NotificationChannels {
    const val MEALS = "channel.meals"
    const val WORKOUTS = "channel.workouts"
    const val BILLS = "channel.bills"
    const val HYDRATION = "channel.hydration"
    const val WELLNESS = "channel.wellness"

    fun register(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService<NotificationManager>() ?: return
        listOf(
            channel(MEALS, "Meal reminders"),
            channel(WORKOUTS, "Workout reminders"),
            channel(BILLS, "Bill reminders"),
            channel(HYDRATION, "Hydration nudges"),
            channel(WELLNESS, "Wellness check-ins"),
        ).forEach(manager::createNotificationChannel)
    }

    private fun channel(id: String, name: String) =
        NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT)
}
