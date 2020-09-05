package com.paraxco.commontools.BroadCastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;

import com.paraxco.commontools.Observers.NetworkObserverHandler;
import com.paraxco.commontools.Observers.NetworkStateLiveData;
import com.paraxco.commontools.Utils.Utils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    public static boolean PingBeforeInform = false;
    public static String pingHost = "www.google.com";
    public static int pingTimeOut = 500;

    AtomicBoolean isRegistered = new AtomicBoolean(false);
    private Boolean lastState = null;
    private Thread pingThread;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras() != null) {
            boolean currentState = Utils.isNetworkAvailable(context);
            //to prevent multiple call from device
            if (lastState == null || lastState != currentState) {
                lastState = currentState;
                changeState(currentState);
            }

        }
    }

    private void changeState(Boolean currentState) {
        if (!currentState || !PingBeforeInform)
            informObservers(currentState);
        else
            checkNetworkStateByPing();
    }

    private void checkNetworkStateByPing() {

        final Handler handler = new Handler();
        if (pingThread != null)
            pingThread.stop();

        pingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean isReachable = Utils.isConnectedToThisServer(pingHost, pingTimeOut);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isReachable)
                            informObservers(true);
                        else
                            informObservers(false);
                    }
                });
            }
        });
        pingThread.start();
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

    public void refreshCurrentState(Context context) {
        Boolean currentState = Utils.isNetworkAvailable(context);
        changeState(currentState);
    }
}
