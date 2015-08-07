package com.yorshahar.locker.activity;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yorshahar.locker.R;
import com.yorshahar.locker.service.MyService;

/**
 * Created by yorshahar on 8/6/15.
 */
public class ContainerActivity extends Activity {

    private TextView textView;
    private ServiceConnection serviceConnection;
    private Service service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.containter_layout);
//
//        textView = (TextView) findViewById(R.id.textView);
//        textView.setText("This phone is now locked!");
//        textView.setTextSize(30);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                int a = 2;
                try {
                    if (MyService.class.getSimpleName().equals(binder.getInterfaceDescriptor())) {
                        service = (Service) binder.queryLocalInterface(binder.getInterfaceDescriptor());

//                        setContentView(((MyService) service).getContainerView());
                        RelativeLayout rootView = ((MyService) service).getContainerView();
                        textView = (TextView) rootView.findViewById(R.id.textView);
                        textView.setText("This phone is now locked!");
                        textView.setTextSize(30);
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                int a = 2;
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();


        Intent intent = new Intent(this, MyService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

}
