package com.ryg.chapter_2.messenger;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.ryg.chapter_2.R;
import com.ryg.chapter_2.utils.MyConstants;

public class MessengerActivity extends Activity {

    private static final String TAG = "MessengerActivity";

    private Messenger mService;
    private Messenger mGetReplyMessenger = new Messenger(new MessengerHandler());

    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MyConstants.MSG_FROM_SERVICE:
                    Log.i(TAG, "receive msg from Service:" + msg.getData().getString("reply"));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            //2.利用服务端返回的IBinder对象来创建一个Messenger，通过这个Messenger就可以向服务端发送消息了，
            //  消息类型是 Message 。
            mService = new Messenger(service);

            Log.d(TAG, "bind service");

            Message msg = Message.obtain(null, MyConstants.MSG_FROM_CLIENT);
            Bundle data = new Bundle();
            data.putString("msg", "hello, this is client.");
            msg.setData(data);

            // 如果需要服务端响应，则需要创建一个Handler并通过它来创建一个Messenger，并通过Message的replyTo
            // 参数传递给服务端。服务端通过Message的 replyTo 参数就可以回应客户端了。
            msg.replyTo = mGetReplyMessenger;
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        Intent intent = new Intent("com.ryg.MessengerService.launch");
        intent.setPackage(getPackageName());
        //1.绑定服务端的Sevice
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }
}
