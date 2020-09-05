package com.paraxco.commontools.Utils.RetryHelper

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import com.paraxco.commontools.Observers.NetworkStateLiveData
import com.paraxco.commontools.Observers.RetryHelper

object DefaultNetworkErrorDialog {

    fun startShowingDefaultNetworkErrorDialog(context: Context?, owner: LifecycleOwner) {
        NetworkStateLiveData.getInstance().registerService(context)
        NetworkStateLiveData.getInstance().observe(owner, Observer {
            handleNetworkEvent(it, context)

        })
    }

    fun startShowingDefaultNetworkErrorDialog(context: Context?) {
        if(context is LifecycleOwner)
            startShowingDefaultNetworkErrorDialog(context,context as LifecycleOwner)
        else {
            NetworkStateLiveData.getInstance().registerService(context)
            NetworkStateLiveData.getInstance().observeForever {
                handleNetworkEvent(it, context)
            }
        }
    }

    fun stopShowingDefaultNetworkErrorDialog(context: Context?) {
        RetryHelper.dismisDialog()
        NetworkStateLiveData.getInstance().unRegisterService(context)

    }

    private fun handleNetworkEvent(networkState: NetworkStateLiveData.NetworkState?, context: Context?) {
        networkState?.let {
            RetryHelper.unLockDismissedDialog()
            if (it.isConnected)
                RetryHelper.dismisDialog()
            else
                context?.let { RetryHelper.showDialog(it) }

        }
    }

}