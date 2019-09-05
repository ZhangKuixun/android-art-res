package com.ryg.chapter_2.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.ryg.chapter_2.utils.MyConstants;

public class MessengerService extends Service {

    private static final String TAG = "MessengerService";

    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MyConstants.MSG_FROM_CLIENT:
                    Log.i(TAG, "receive msg from Client:" + msg.getData().getString("msg"));

                    Messenger client = msg.replyTo;
                    Message relpyMessage = Message.obtain(null, MyConstants.MSG_FROM_SERVICE);
                    Bundle bundle = new Bundle();

                    bundle.putString("reply", "嗯，你的消息我已经收到，稍后会回复你。");

                    relpyMessage.setData(bundle);
                    try {
                        client.send(relpyMessage);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    // 创建一个Service来处理客户端请求，同时创建一个Handler并通过它来创建一个Messenger，
    // 然后在Service#onBind中返回Messenger对象Binder即可。
    private final Messenger mMessenger = new Messenger(new MessengerHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();//验证权限的方法 同样适用于Messenger中。
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

}
