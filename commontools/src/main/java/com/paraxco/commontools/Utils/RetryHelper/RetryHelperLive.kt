package com.paraxco.commontools.Utils.RetryHelper

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import com.paraxco.commontools.Observers.RetryHelper

class RetryHelperLive(val retryHelper: RetryHelper) : LiveData<String>() {
    override fun onActive() {
        super.onActive()
        retryHelper.resumeThisRetry()

    }

    override fun removeObserver(observer: Observer<String>) {
        super.removeObserver(observer)
        if (!hasObservers()) {
            retryHelper.disable()
//            SmartLogger.logDebug("disabled")
        }
    }
    override fun onInactive() {
        super.onInactive()
        if(!hasActiveObservers())
            retryHelper.pauseThisRetry()
    }


    fun invoke() {
        value = "call"

    }
}