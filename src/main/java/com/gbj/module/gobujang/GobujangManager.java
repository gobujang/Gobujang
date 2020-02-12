package com.gbj.module.gobujang;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class GobujangManager {
    private static final String ACTION_GOBUJANG_RECEIVER = "com.gbj.gobujang.server.receiver";
    private static final String ACTION_CLIENT_RECEIVER = "com.gbj.gobujang.client.receiver";

    private static final int EXECUTE              =  0;
    private static final int GOBUJANG_NO_RESPONSE = -1;
    private static final int EXPIRED              = -2;

    private static int result = GOBUJANG_NO_RESPONSE;

    private static Looper broadcastReceiverLooper = null;
    private static Handler broadcastReceiverHandler = null;
    private static HandlerThread broadcastReceiverThread = null;
    private static final String HANDLERTHREAD_NAME = "broadcastReceiverThread";

    /**
     * register client receiver.
     */
    private static void registerClientReceiver(Context context) {
        broadcastReceiverThread = new HandlerThread(HANDLERTHREAD_NAME);
        broadcastReceiverThread.start();

        broadcastReceiverLooper = broadcastReceiverThread.getLooper();
        broadcastReceiverHandler = new Handler(broadcastReceiverLooper);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CLIENT_RECEIVER);
        context.registerReceiver(mReceiver, filter,
                null, broadcastReceiverHandler);
    }

    /**
     * unregister client receiver.
     */
    private static void unregisterClientReceiver(Context context) {
        try {
            context.unregisterReceiver(mReceiver);
        }
        catch (IllegalArgumentException e) {}
        catch (Exception e) {}
        finally {}
        broadcastReceiverLooper.quit();
    }

    /**
     * call gobujang launcher receiver.
     */
    private static void callGobujangReceiver(Context context, String appCode) {
        Intent intent = new Intent(ACTION_GOBUJANG_RECEIVER);
        intent.putExtra("appCode", appCode);
        context.sendBroadcast(intent);
    }

    /**
     * reset default int value.
     */
    private static void resetResult() {
        result = GOBUJANG_NO_RESPONSE;
    }

    /**
     * @param context current context {@link Context}.
     * @param appCode  to be installed, always required.
     * @return EXECUTE = 0, GOBUJANG_NO_RESPONSE = -1, EXPIRED = -2.
     */
    public static int init(Context context, String appCode) {
        resetResult();
        registerClientReceiver(context);
        callGobujangReceiver(context, appCode);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        unregisterClientReceiver(context);
        return result;
    }

    private static BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null)
            {
                result = bundle.getInt("executable");
            }
        }
    };


}
