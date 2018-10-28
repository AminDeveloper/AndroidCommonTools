package com.paraxco.commontools.Observers;

import android.app.Dialog
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.paraxco.commontools.R
import com.paraxco.commontools.Utils.RetryHelper.RetryHelperLive
import com.paraxco.commontools.Utils.SmartLogger
import com.paraxco.commontools.Utils.Utils
import java.lang.Exception
import java.util.concurrent.atomic.AtomicBoolean

/**
 * calls specified method [call]  with specified [delaySecond] if network is available after calling retry()
 * disable it in on destroy  events
 *
 * if network become available retries automatically
 *
 */
class RetryHelper(val context: Context, var numOfFinished: Int = 1) : NetworkObserverHandler.NetworkChangeObserver {

    companion object {
        fun getInstanceAndCall(context: Context, call: () -> Any?, numOfFinished: Int = 1): RetryHelper {
            return getInstanceAndCall(context, null, call, numOfFinished)
        }

        fun getInstanceAndCall(activity: AppCompatActivity, call: () -> Any?, numOfFinished: Int = 1): RetryHelper {
            return getInstanceAndCall(activity, activity as LifecycleOwner, call, numOfFinished)
        }

        fun getInstanceAndCall(fragment: Fragment, call: () -> Any?, numOfFinished: Int = 1): RetryHelper {
            return getInstanceAndCall(fragment.context!!, fragment as LifecycleOwner , call, numOfFinished)
        }

        fun getInstanceAndCall(context: Context, owner: LifecycleOwner?, call: () -> Any?, numOfFinished: Int = 1): RetryHelper {
            val instance = RetryHelper(context, numOfFinished)
            instance.initializeAndCall(call, owner)
            return instance
        }

        var networkErrorDialog = R.layout.network_error_dialog

        private var dialog: Dialog? = null
        private var dismissed = false//in each timee network connects just show it once and if dialog dismissed by user do not show it again unless network is connected and disconnected again

        private var wasShowing = false//dialog was showing before dialog section start or during dialog section
        private var inNoDialogSection = false

        //Pause
        private var paused = false

        private var retriedDuringPause = false //retry has been called after pause
        /**
         * dismiss dialog and no longer show until endNoDialogSection() is called
         */
        fun startNoDialogSection() {
            inNoDialogSection = true

            if (dialog != null && dialog!!.isShowing) {
                dismisLoading()
                wasShowing = true
            } else
                wasShowing = false

//            wasDismissed = dismissed
        }

        /**
         * show dialog if was showing before startNoDialogSection() and shows dialog if needded from now
         */
        fun endNoDialogSection(context: Context) {
            inNoDialogSection = false
//            SmartLogger.logDebug(wasDismissed.toString())
            SmartLogger.logDebug((dialog != null).toString())
            SmartLogger.logDebug(wasShowing.toString())

            if (wasShowing) {
                showDialog(context)
                wasShowing = false
            }

        }

        fun dismisLoading() {

            if (dialog != null && dialog!!.isShowing)
                try {
                    dialog?.dismiss()
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
        }

        fun showDialog(context: Context) {
            if (inNoDialogSection) {
                if (!dismissed)
                    wasShowing = true
                return
            }
            if (!dismissed && (dialog == null || !dialog!!.isShowing)) {
                dialog = Dialog(context, R.style.DialogStyle)
                //        alert.setTitle(title);
                dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog?.setContentView(networkErrorDialog)
                dialog?.setCanceledOnTouchOutside(true)
                dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT)
                dialog?.findViewById<View>(R.id.layMain)?.setOnClickListener {
                    dismisLoading()
                }
                dialog?.setOnDismissListener {
                    if (!inNoDialogSection)
                        dismissed = true
//                    wasDismissed = true
//                    wasShowing = false
                }
//            dialog?.setCancelable(false)
                try {
                    dialog?.show()

                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }
        }

    }

    var enabled = true
    var delaySecond: Long = 8
    var call: (() -> Any?)? = null
    private var liveData: RetryHelperLive? = null
    /**
     * if owner is passed no need to explicit call for lifecycle like pause resume and disable
     */
    fun initializeAndCall(call: () -> Any?, owner: LifecycleOwner? = null) {
        this.call = call
        owner?.let {
            liveData = RetryHelperLive(this)
            liveData?.observe(it, Observer {
                call.invoke()
            })
        }
        invokeMethod()
    }

    private fun invokeMethod() {
        if (liveData == null)
            call?.invoke()
        else
            liveData?.invoke()
    }

    fun retry() {
        retry(true)
    }

    fun pauseRetry() {
        paused = true
    }

    fun resumeRetry() {
        paused = false
        if (retriedDuringPause)
            retry()
    }

    private var calling = AtomicBoolean(false)

    /**
     * call it when something has gone wrong and retry needed
     */
    fun retry(whithDelay: Boolean) {
        if (paused) {
            retriedDuringPause = true
            return
        }

        if (!enabled) {
            NetworkObserverHandler.getInstance().removeObserver(this)
            return
        }
        if (calling.getAndSet(true))
            return
        showDialog(context)


        SmartLogger.logDebug("retry")
        if (Utils.isNetworkAvailable(context)) {
            NetworkObserverHandler.getInstance().removeObserver(this)
            Handler().postDelayed({
                if (!enabled)
                    return@postDelayed
                calling.set(false)
               invokeMethod()

                SmartLogger.logDebug("invoked")

            }, if (whithDelay) delaySecond * 1000 else 0)
        } else {
            calling.set(false)
            NetworkObserverHandler.getInstance().addObserver(this)
        }
    }

    /**
     * ends retry cycle and
     */
    fun finished() {
        numOfFinished -= 1
        if (numOfFinished == 0) {
            disable()
            dismisLoading()
        }
    }


    override fun getContextForNetworkObserver(): Context {
        return context
    }

    override fun onNetworkStateChange(connected: Boolean) {
        dismissed = false
        if (connected)
            retry(false)
    }

    /**
     * cancels all running task and observers
     * call it in on destroy
     */
    fun disable() {
        enabled = false
        NetworkObserverHandler.getInstance().removeObserver(this)
    }

}
