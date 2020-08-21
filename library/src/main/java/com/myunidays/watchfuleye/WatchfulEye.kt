package com.myunidays.watchfuleye

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

@Module
object WatchfulEye {

    private val activities = mutableSetOf<Activity>()

    @[Provides IntoSet]
    fun getActivities(): MutableSet<Activity> = activities

    @Provides
    fun nullableActivity(): Activity? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 -> {
            activities.lastOrNull { !it.isDestroyed && !it.isFinishing }
        }
        else -> {
            activities.lastOrNull { !it.isFinishing }
        }
    } ?: activities.lastOrNull { it.hasWindowFocus() }

    @Provides
    fun requireActivity(): Activity = nullableActivity() ?: activities.last()

    interface Callbacks {
        fun onActivityFromCold()
        fun onActivityFromColdOrBackground()
        fun onActivityFromBackground()
        fun onApplicationBackgrounded()
    }

    private val installed: MutableSet<String> = mutableSetOf()

    private val allCallbacks: MutableSet<Callbacks> = mutableSetOf()

    @JvmStatic
    fun registerCallbacks(callbacks: Callbacks) {
        (callbacks as? Context)?.let(::install)
        allCallbacks += callbacks
    }

    @JvmStatic
    fun unregisterCallbacks(callbacks: Callbacks) {
        allCallbacks -= callbacks
    }

    @JvmStatic
    fun install(context: Context) {
        val application = context.applicationContext as Application
        val packageName: String = application.packageName
        if (packageName !in installed) {
            application.registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks)
            installed += packageName
        }
    }

    @JvmStatic
    fun uninstall(context: Context) {
        val application = context.applicationContext as Application
        val packageName: String = application.packageName
        if (packageName in installed) {
            application.unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks)
            installed -= packageName
        }
    }

    internal inline fun notifyAll(
        crossinline function: (Callbacks) -> Unit,
        activity: Activity? = null,
    ) {
        activity?.let {
            allCallbacks.filter { it is Activity && it === activity }.forEach(function)
        }
        allCallbacks.filter { activity == null || it !is Activity }.forEach(function)
    }

    object ActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

        private val history = mutableSetOf<String>()

        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
            registerActivity(activity)
        }

        override fun onActivityStarted(activity: Activity) {
            registerActivity(activity)
        }

        override fun onActivityResumed(activity: Activity) {
            registerActivity(activity)
        }

        override fun onActivityPaused(activity: Activity) {
            removeActivity(activity)
        }

        override fun onActivityStopped(activity: Activity) {
            removeActivity(activity)
        }

        override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {
            removeActivity(activity)
        }

        override fun onActivityDestroyed(activity: Activity) {
            removeActivity(activity)
            (activity as? Callbacks)?.let(WatchfulEye::unregisterCallbacks)
        }

        private fun registerActivity(activity: Activity) {
            val activities = getActivities()
            val beforeCount = activities.size
            activities += activity
            if (beforeCount == 0) {
                if (history.size == 0) {
                    notifyAll(Callbacks::onActivityFromCold, activity)
                    notifyAll(Callbacks::onActivityFromColdOrBackground, activity)
                } else {
                    notifyAll(Callbacks::onActivityFromBackground, activity)
                }
            }
        }

        private fun removeActivity(activity: Activity) {
            val activities = getActivities()
            if (activities.size > 0) {
                activities -= activity
                history += "${activity::class.java}"
                if (activities.size == 0) {
                    notifyAll(Callbacks::onApplicationBackgrounded, activity)
                }
            }
        }
    }
}
