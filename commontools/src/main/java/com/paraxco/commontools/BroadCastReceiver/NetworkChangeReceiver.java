package com.paraxco.commontools.BroadCastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;

import com.paraxco.commontools.Observers.NetworkObserverHandler;
import com.paraxco.commontools.Observers.NetworkStateLiveData;
import com.paraxco.commontools.Utils.Utils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    AtomicBoolean isRegistered = new AtomicBoolean(false);

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras() != null) {
            Boolean currentState = Utils.isNetworkAvailable(context);
            //to prevent multiple call from device
            if (NetworkObserverHandler.getInstance().getData() != currentState)
                informObservers(currentState);


        }
    }

    private void informObservers(Boolean currentState) {
        NetworkObserverHandler.getInstance().informObservers(currentState);
        NetworkStateLiveData.getInstance().postValue(new NetworkStateLiveData.NetworkState(currentState));
    }

    public void registerService(Context context) {

        if (!isRegistered.get())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            if (getObserversCount() == 1)//for the first time
                try {
                    context.registerReceiver(this,
                            new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                    isRegistered.set(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
    }

    public void unRegisterService(Context context) {
        if (isRegistered.get())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            if (getObserversCount() == 0 && lastCont == 1)//for the last time
                try {
                    context.unregisterReceiver(this);
                    isRegistered.set(false);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
    }

}
