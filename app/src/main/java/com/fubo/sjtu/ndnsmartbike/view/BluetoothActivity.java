package com.fubo.sjtu.ndnsmartbike.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.fubo.sjtu.ndnsmartbike.R;
import com.fubo.sjtu.ndnsmartbike.service.BluetoothService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjtu on 2015/11/19.
 */
public class BluetoothActivity extends AppCompatActivity {

    private ListView mListView;
    private List<String> deviceAddress = new ArrayList<>();
    private ArrayAdapter<String> simpleAdapter;
    private MyReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        Intent intent = new Intent();
        intent.setClass(this, BluetoothService.class);
        intent.setAction("search");
        startService(intent);
        mListView = (ListView) findViewById(R.id.listview_bluetooth);
        simpleAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout
                .simple_list_item_1, deviceAddress);
        mListView.setAdapter(simpleAdapter);
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("findNewDevice");
        intentFilter.addAction("connect_complete");
        registerReceiver(myReceiver, intentFilter);
        initEvent();
    }

    private void initEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(BluetoothActivity.this, BluetoothService.class);
                intent.setAction("connect");
                intent.putExtra("address", deviceAddress.get(position).split("/")[1]);
                startService(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(myReceiver);
        super.onDestroy();
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                switch (action) {
                    case "findNewDevice":
                        deviceAddress.add(intent.getStringExtra("deviceName")+"/"+intent.getStringExtra("deviceAddress"));
                        simpleAdapter.notifyDataSetChanged();
                        break;
                    case "connect_complete":
                        Toast.makeText(getApplicationContext(),"连接成功",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
