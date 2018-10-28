package com.paraxco.commontools.Utils.RetryHelper

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import com.paraxco.commontools.Observers.RetryHelper
import com.paraxco.commontools.Utils.SmartLogger

class RetryHelperLive(val retryHelper: RetryHelper) : LiveData<String>() {
    override fun onActive() {
        super.onActive()
        retryHelper.resumeRetry()

    }

    override fun removeObserver(observer: Observer<String>) {
        super.removeObserver(observer)
        if (!hasObservers()) {
            retryHelper.disable()
            SmartLogger.logDebug("disabled")
        }
    }
    override fun onInactive() {
        super.onInactive()
        if(!hasActiveObservers())
            retryHelper.pauseRetry()
    }


    fun invoke() {
        value = "call"

    }
}