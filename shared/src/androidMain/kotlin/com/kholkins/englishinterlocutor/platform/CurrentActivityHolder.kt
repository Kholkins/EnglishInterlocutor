package com.kholkins.englishinterlocutor.platform

import android.app.Activity
import java.lang.ref.WeakReference

object CurrentActivityHolder {
    private var activityRef: WeakReference<Activity>? = null

    fun set(activity: Activity?) {
        activityRef = activity?.let { WeakReference(it) }
    }

    fun get(): Activity? = activityRef?.get()
}
