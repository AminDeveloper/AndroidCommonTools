package com.paraxco.commontools.Observers;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;

import com.paraxco.commontools.BroadCastReceiver.NetworkChangeReceiver;
import com.paraxco.commontools.ObserverBase.ObserverHandlerBase;
import com.paraxco.commontools.ObserverBase.StatefullObserverHandler;
import com.paraxco.commontools.Utils.Utils;

import java.util.List;

/**
 * Created by Amin on 03/12/2017.
 */

public class NetworkObserverHandler extends StatefullObserverHandler<NetworkObserverHandler.NetworkChangeObserver, Boolean> {
    static NetworkObserverHandler instance;
    static NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();

    public static NetworkObserverHandler getInstance() {
        if (instance == null)
            instance = new NetworkObserverHandler();
        return instance;
    }

    private NetworkObserverHandler() {

    }


    /**
     * notifies listener when network status changed
     * it will immediately notify the current state
     * do not forget to removeObserver when it is not nessessary to avoid memory leak!
     *
     * @param observer
     */
    @Override
    public void addObserver(NetworkChangeObserver observer) {
        super.addObserver(observer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (getObserversCount() == 1)//for the first time
                observer.getContextForNetworkObserver().registerReceiver(networkChangeReceiver,
                        new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        informObservers(Utils.isNetworkAvailable(observer.getContextForNetworkObserver()));
    }

    @Override
    public void removeObserver(NetworkChangeObserver observer) {
        int lastCont=getObserversCount();
        super.removeObserver(observer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (getObserversCount() == 0 && lastCont==1)//for the last time
                try {
                    observer.getContextForNetworkObserver().unregisterReceiver(networkChangeReceiver);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    protected void informObserverInternal(NetworkChangeObserver observe, Boolean data) {
        observe.onNetworkStateChange(data);
    }

    public interface NetworkChangeObserver {
        void onNetworkStateChange(Boolean connected);
        Context getContextForNetworkObserver();
    }
}

