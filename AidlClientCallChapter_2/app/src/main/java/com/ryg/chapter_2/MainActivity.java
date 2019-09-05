package com.ryg.chapter_2;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ryg.chapter_2.aidl.Book;
import com.ryg.chapter_2.aidl.IBookManager;
import com.ryg.chapter_2.aidl.IOnNewBookArrivedListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Kevin-MA";
    private static final int MESSAGE_NEW_BOOK_ARRIVED = 1;

    private IBookManager mRemoteBookManager;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_NEW_BOOK_ARRIVED:
                    Log.d(TAG, "receive new book :" + msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    /**
     * 判断Binder是否死亡，方案一：在客户端的Binder线程中。
     * 声明一个 DeathRecipient 对象。 DeathRecipient 只有一个方法 binderDied ，当Binder死
     * 亡的时候，系统就会回调 DeathRecipient 方法。
     */
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.d(TAG, "binder died. tname:" + Thread.currentThread().getName());
            if (mRemoteBookManager == null)
                return;
            mRemoteBookManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mRemoteBookManager = null;
            // TODO:这里重新绑定远程Service
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {//主线程中执行

        /**
         * 服务绑定成功
         * @param service 如果是同进程，这里返回的是RemoteService的匿名内部类Stub。
         *                如果是不同的进程，这里返回的是Binderproxy。不同的进程创建的对象不一样。
         */
        public void onServiceConnected(ComponentName className, IBinder service) {
            IBookManager bookManager = IBookManager.Stub.asInterface(service);
            Log.i(TAG, "bookManager:" + bookManager);

            mRemoteBookManager = bookManager;
            try {
                //死亡代理
                mRemoteBookManager.asBinder().linkToDeath(mDeathRecipient, 0);

                List<Book> list = bookManager.getBookList();
                Log.i(TAG, "query book list, list type:" + list.getClass().getCanonicalName());
                Log.i(TAG, "query book list:" + list.toString());

                Book newBook = new Book(3, "Android进阶");
                bookManager.addBook(newBook);

                Log.i(TAG, "add book:" + newBook);

                List<Book> newList = bookManager.getBookList();

                Log.i(TAG, "query book list:" + newList.toString());

                bookManager.registerListener(mOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        //判断Binder是否死亡，方案二：在客户端的UI线程中。
        public void onServiceDisconnected(ComponentName className) {
            mRemoteBookManager = null;
            Log.d(TAG, "onServiceDisconnected. tname:" + Thread.currentThread().getName());
        }
    };

    private IOnNewBookArrivedListener mOnNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {

        @Override
        public void onNewBookArrived(Book newBook) throws RemoteException {
            mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, newBook)
                    .sendToTarget();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        Intent intent = new Intent();
        intent.setAction("com.ryg.BookManagerService.launch");
        intent.setPackage("com.ryg.chapter_2");
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void onButton1Click(View view) {
        Toast.makeText(this, "click button1", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (mRemoteBookManager != null) {
                    try {
                        List<Book> newList = mRemoteBookManager.getBookList();
                        Log.e(TAG, "newList:" + newList);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        if (mRemoteBookManager != null && mRemoteBookManager.asBinder().isBinderAlive()) {
            try {
                Log.i(TAG, "unregister listener:" + mOnNewBookArrivedListener);
                mRemoteBookManager.unregisterListener(mOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mConnection);
        super.onDestroy();
    }

}
