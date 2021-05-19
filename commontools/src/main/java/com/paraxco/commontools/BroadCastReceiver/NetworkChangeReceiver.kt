package com.paraxco.commontools.BroadCastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Handler
import com.paraxco.commontools.Observers.NetworkObserverHandler
import com.paraxco.commontools.Observers.NetworkStateLiveData
import com.paraxco.commontools.Observers.NetworkStateLiveData.NetworkState
import com.paraxco.commontools.Utils.Utils
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 */
class NetworkChangeReceiver : BroadcastReceiver() {
    companion object {
        var PingBeforeInform = false
        var pingHost = "www.google.com"
        var pingTimeOut = 2000
        var pingMechanism: ((host: String, timeout: Int) -> Boolean)? = null

        //        private var future: Future<*>? = null
        private var threadExecutor: ExecutorService? = null

        fun informNetworkChange(connectionState: Boolean) {
            if (PingBeforeInform) checkNetworkStateByPing() else informObservers(connectionState)
        }

        private fun checkNetworkStateByPing() {
            val handler = Handler()
            threadExecutor = threadExecutor ?: Executors.newSingleThreadExecutor()

//            future =
            threadExecutor?.submit {
                val isReachable = pingMechanism?.run { pingMechanism?.invoke(pingHost, pingTimeOut) }
                        ?: Utils.isConnectedToThisServer(pingHost, pingTimeOut)

                handler.post { if (isReachable) informObservers(true) else informObservers(false) }
            }
        }

        private fun informObservers(currentState: Boolean) {
            NetworkObserverHandler.getInstance().informObservers(currentState)
            NetworkStateLiveData.getInstance().postValue(NetworkState(currentState))
        }
    }

    var isRegistered = AtomicBoolean(false)
    private var lastState: Boolean? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.extras != null) {
            val currentState = Utils.isNetworkAvailable(context)
            //to prevent multiple call from device
            if (lastState == null || lastState != currentState) {
                lastState = currentState
                informNetworkChange(currentState)
            }
        }
    }

    fun registerService(context: Context) {
        if (!isRegistered.get()) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            if (getObserversCount() == 1)//for the first time
            try {
                context.registerReceiver(this,
                        IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
                isRegistered.set(true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun unRegisterService(context: Context) {
        if (isRegistered.get()) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            if (getObserversCount() == 0 && lastCont == 1)//for the last time
            try {
                context.unregisterReceiver(this)
                isRegistered.set(false)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }
    }

    fun refreshCurrentState(context: Context?) {
        val currentState = Utils.isNetworkAvailable(context)
        informNetworkChange(currentState)
    }
}