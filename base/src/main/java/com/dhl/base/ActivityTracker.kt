package com.dhl.base

import android.app.Activity
import android.app.Application
import android.os.Bundle

object ActivityTracker {

    private val activityList: ArrayList<Activity> = ArrayList()

    fun init(app: Application) {
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                log { "Lifecycle -> onActivityCreated - ${activity::class.java.simpleName}" }
                activityList.add(activity)
            }

            override fun onActivityStarted(activity: Activity) {
                log { "Lifecycle -> onActivityStarted - ${activity::class.java.simpleName}" }
            }

            override fun onActivityResumed(activity: Activity) {
                log { "Lifecycle -> onActivityResumed - ${activity::class.java.simpleName}" }
            }

            override fun onActivityPaused(activity: Activity) {
                log { "Lifecycle -> onActivityPaused - ${activity::class.java.simpleName}" }
            }

            override fun onActivityStopped(activity: Activity) {
                log { "Lifecycle -> onActivityStopped - ${activity::class.java.simpleName}" }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                log { "Lifecycle -> onActivitySaveInstanceState - ${activity::class.java.simpleName}" }
            }

            override fun onActivityDestroyed(activity: Activity) {
                log { "Lifecycle -> onActivityDestroyed - ${activity::class.java.simpleName}" }
                activityList.remove(activity)
            }

        })

    }

    fun clearAllActivity() {
        for (activity: Activity in activityList) {
            activity.finish()
        }
    }

}
