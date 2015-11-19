package com.fubo.sjtu.ndnsmartbike.base;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by sjtu on 2015/11/15.
 */
public class BaseActivity extends AppCompatActivity {

    public void showShortToast(String content){
        Toast.makeText(getApplicationContext(),content,Toast.LENGTH_SHORT).show();
    }
    public void showLongToast(String content) {
        Toast.makeText(getApplicationContext(), content, Toast.LENGTH_LONG).show();
    }
}
