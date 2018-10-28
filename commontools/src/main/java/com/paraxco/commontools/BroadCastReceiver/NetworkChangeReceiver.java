package com.paraxco.commontools.BroadCastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.paraxco.commontools.Observers.NetworkObserverHandler;
import com.paraxco.commontools.Utils.Utils;

/**
 *
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
//    private static List<NetworkListener> observers = new LinkedList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras() != null) {
            Boolean currentState = Utils.isNetworkAvailable(context);
            //to prevent multiple call from device
            if (NetworkObserverHandler.getInstance().getData() != currentState)
                NetworkObserverHandler.getInstance().informObservers(currentState);

//            for (NetworkListener observer : observers)
//                observer.onNetworkStateChange(Utils.isNetworkAvailable(context));
        }
    }


//    /**
//     * notifies listener when network status changed
//     * it will immediately notify the current state
//     * do not forget to unregister when it is not nessessary to avoid memory leak!
//     *
//     * @param context
//     * @param listener
//     */
//    public synchronized static void registerObserver(Context context, NetworkListener listener) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            if (observers.size() == 0)
//                context.registerReceiver(instance,
//                        new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
//        }
//        observers.add(listener);
//        listener.onNetworkStateChange(Utils.isNetworkAvailable(context));
//    }
//
//    public synchronized static void unRegisterObserver(Context context, NetworkListener listener) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            if (observers.size() == 0)
//                context.unregisterReceiver(instance);
//        }
//        observers.remove(listener);
//    }
////    public interface NetworkListener {
////        void onNetworkStateChange(boolean connected);
////    }


}
