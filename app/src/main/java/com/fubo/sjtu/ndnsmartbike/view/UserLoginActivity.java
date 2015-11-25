package com.fubo.sjtu.ndnsmartbike.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.fubo.sjtu.ndnsmartbike.MyApplication;
import com.fubo.sjtu.ndnsmartbike.R;
import com.fubo.sjtu.ndnsmartbike.base.BaseActivity;
import com.fubo.sjtu.ndnsmartbike.model.UserInfo;
import com.fubo.sjtu.ndnsmartbike.utils.UUIDUtil;


/**
 * Created by sjtu on 2015/11/15.
 */
public class UserLoginActivity extends BaseActivity {

    private EditText etUserName;
    private EditText etUserDes;
    private EditText etBirthYear;
    private RadioGroup rgSex;
    private Button btRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_setting);
        initView();
        initEvent();
    }

    private void initView() {
        etUserName = (EditText) findViewById(R.id.et_user_name);
        etUserDes = (EditText) findViewById(R.id.et_user_des);
        etBirthYear = (EditText) findViewById(R.id.et_birth_year);
        rgSex = (RadioGroup) findViewById(R.id.rg_sex);
        btRegister = (Button) findViewById(R.id.bt_user_register);
        //头像


        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("registered", false) == true) {
            etUserName.setText(MyApplication.getUser().getUserName());
            etUserDes.setText(MyApplication.getUser().getUserDes());
            etBirthYear.setText("" + MyApplication.getUser().getBirthYear());
            if (MyApplication.getUser().getSex() == 0)
                rgSex.check(R.id.male);
            else
                rgSex.check(R.id.female);
        }
    }

    private void initEvent() {
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("registered", false) == false) {
            btRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //更新用户信息
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUserName(etUserName.getText().toString());
                    userInfo.setUserDes(etUserDes.getText().toString());
                    userInfo.setUserId(UUIDUtil.getUUID());
                    userInfo.setFlag(1);
                    userInfo.setReserve("");
                    userInfo.setBirthYear(Integer.valueOf(etBirthYear.getText().toString()));
                    switch (rgSex.getCheckedRadioButtonId()) {
                        case R.id.male:
                            userInfo.setSex(0);
                            break;
                        case R.id.female:
                            userInfo.setSex(1);
                            break;
                        default:
                            break;
                    }
                    //暂时没有头像
                    MyApplication.setUserInfo(userInfo);
                    //更新SharedPreference
                    updateUserPreference();
                    //退出
                    setResult(0);
                    finish();
                }
            });
        } else
            btRegister.setClickable(false);
    }

    private void updateUserPreference() {
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("registered", true).putString("userId", MyApplication
                .getUser().getUserId()).putString("userName", MyApplication.getUser().getUserName
                ()).putString("userDes", MyApplication.getUser().getUserDes()).putInt
                ("birthYear", MyApplication.getUser().getBirthYear()).putInt("flag",
                MyApplication.getUser().getFlag()).putString("reserve", MyApplication.getUser()
                .getReserve()).putInt("sex", MyApplication.getUser().getSex()).commit();
    }
}
