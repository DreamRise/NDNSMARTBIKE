package com.fubo.sjtu.ndnsmartbike.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.fubo.sjtu.ndnsmartbike.MyApplication;
import com.fubo.sjtu.ndnsmartbike.R;
import com.fubo.sjtu.ndnsmartbike.adapter.MyForwardInfoListViewAdapter;
import com.fubo.sjtu.ndnsmartbike.database.ForwardInfoDataHelper;
import com.fubo.sjtu.ndnsmartbike.model.ForwardInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjtu on 2015/11/24.
 */
public class ForwardInfoActivity extends AppCompatActivity {
    private TextView tvUserId;
    private ListView lvForwardInfo;
    private MyForwardInfoListViewAdapter myForwardInfoListViewAdapter;
    private ForwardInfoDataHelper forwardInfoDataHelper;
    private List<ForwardInfo> forwardInfoList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forwardinfo);
        initView();
        initData();
    }

    private void initView() {
        lvForwardInfo = (ListView) findViewById(R.id.lv_forwardinfo);
        tvUserId = (TextView) findViewById(R.id.tv_userId);
    }

    private void initData() {
        tvUserId.setText("用户id:"+MyApplication.getUser().getUserId());
        forwardInfoDataHelper = ForwardInfoDataHelper.getInstance(getApplicationContext());
        forwardInfoList = forwardInfoDataHelper.selectForwardInfoAll();
        myForwardInfoListViewAdapter = new MyForwardInfoListViewAdapter(getApplicationContext(),
                forwardInfoList);
        lvForwardInfo.setAdapter(myForwardInfoListViewAdapter);
    }
}
