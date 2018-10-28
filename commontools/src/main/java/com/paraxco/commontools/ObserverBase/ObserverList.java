package com.paraxco.commontools.ObserverBase;

import com.paraxco.commontools.Observers.NetworkObserverHandler;

/**
 * Created by Amin on 03/12/2017.
 */

public class ObserverList {
    public static NetworkObserverHandler getNetworkObserverHandler(){
        return NetworkObserverHandler.getInstance();
    }
}
