package com.paraxco.commontools.Observers;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.NonNull;

import com.paraxco.commontools.BroadCastReceiver.NetworkChangeReceiver;

public class NetworkStateLiveData extends MutableLiveData<NetworkStateLiveData.NetworkState> {

    static NetworkStateLiveData instance;
    static NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();


    public static NetworkStateLiveData getInstance() {
        if (instance == null)
            instance = new NetworkStateLiveData();
        return instance;
    }

    public NetworkStateLiveData() {
        super();
    }

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<NetworkState> observer) {
        super.observe(owner, observer);
    }

    @Override
    public void observeForever(@NonNull Observer<NetworkState> observer) {
        super.observeForever(observer);
    }

    public void registerService(Context context) {
        networkChangeReceiver.registerService(context);
        //inform initial value
       networkChangeReceiver.refreshCurrentState(context);

    }

    public void unRegisterService(Context context) {
        networkChangeReceiver.unRegisterService(context);
    }


    public static class NetworkState {
        boolean isConnected;

        public NetworkState(boolean isConnected) {
            this.isConnected = isConnected;
        }

        public boolean getIsConnected() {
            return isConnected;
        }
    }
}


