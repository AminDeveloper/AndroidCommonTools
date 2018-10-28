package com.paraxco.commontools.Utils;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import static com.paraxco.commontools.Utils.NotificationHelper.PENDING_INTENT;
import static com.paraxco.commontools.Utils.NotificationHelper.decreaseAndGetCount;
import static com.paraxco.commontools.Utils.NotificationHelper.showBadge;

/**
 * Created by Amin on 18/11/2017.
 */

public class NotificationService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        PendingIntent pendingIntent = intent.getParcelableExtra(PENDING_INTENT);
        doActions(pendingIntent);
        intent = null;
        stopSelf();

        return  Service.START_NOT_STICKY;
    }

    public void doActions(PendingIntent pIntent) {
        if (pIntent != null)
            try {
                pIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }

        showBadge(getApplicationContext(), decreaseAndGetCount(getApplicationContext()));
    }


}
