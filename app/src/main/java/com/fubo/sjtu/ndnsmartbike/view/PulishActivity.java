package com.fubo.sjtu.ndnsmartbike.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.fubo.sjtu.ndnsmartbike.MyApplication;
import com.fubo.sjtu.ndnsmartbike.R;
import com.fubo.sjtu.ndnsmartbike.database.ActivityInfoDataHelper;
import com.fubo.sjtu.ndnsmartbike.model.ActivityInfo;
import com.fubo.sjtu.ndnsmartbike.utils.SimpleDateFormatUtil;
import com.fubo.sjtu.ndnsmartbike.utils.UUIDUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by sjtu on 2015/11/18.
 */
public class PulishActivity extends AppCompatActivity {
    private MaterialEditText edit_activity_title;
    private MaterialEditText edit_activity_start_place;
    private MaterialEditText edit_activity_end_place;
    private MaterialEditText edit_activity_des;
    private TextView edit_activity_time;
    private Button bt_publish;
    private final Date activityDate = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        initView();
        initEvent();
    }

    private void initEvent() {
        edit_activity_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();

                final TimePickerDialog timePickerDialog = new TimePickerDialog(PulishActivity
                        .this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        activityDate.setHours(hourOfDay);
                        activityDate.setMinutes(minute);
                        activityDate.setSeconds(0);
                        edit_activity_time.setText(SimpleDateFormatUtil.formatUtilDate(activityDate, SimpleDateFormatUtil.UTIL_DATE_WITHOUT_SECOND));
                    }
                }, calendar.getTime().getHours(), calendar.getTime().getMinutes(), true);
                DatePickerDialog datePickerDialog = new DatePickerDialog(PulishActivity.this, new
                        DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int
                                    dayOfMonth) {
                                activityDate.setYear(year-1900);
                                activityDate.setMonth(monthOfYear);
                                activityDate.setDate(dayOfMonth);
                                timePickerDialog.show();
                            }
                        }, calendar.getTime().getYear()+1900, calendar.getTime().getMonth(), calendar.getTime().getDate());
                datePickerDialog.show();
            }
        });
        bt_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityInfo activityInfo=new ActivityInfo();
                activityInfo.setActivityTitle(edit_activity_title.getText().toString());
                activityInfo.setReserve("");
                activityInfo.setActivityStartPlace(edit_activity_start_place.getText().toString());
                activityInfo.setActivityDes(edit_activity_des.getText().toString());
                activityInfo.setActivityEndPlace(edit_activity_end_place.getText().toString());
                activityInfo.setActivityBuildDate(new Date());
                activityInfo.setActivityDate(activityDate);
                activityInfo.setFlag(ActivityInfo.ACTIVITY_FLAG_NORMAL);
                activityInfo.setId(UUIDUtil.getUUID());
                activityInfo.setPublisherId(MyApplication.getUser().getUserId());
                ActivityInfoDataHelper activityInfoDataHelper = ActivityInfoDataHelper
                        .getInstance(getApplicationContext());
                activityInfoDataHelper.insertActivity(activityInfo);
                setResult(1);
                finish();
            }
        });
    }

    private void initView() {
        edit_activity_title = (MaterialEditText) findViewById(R.id.edit_activity_title);
        edit_activity_start_place = (MaterialEditText) findViewById(R.id.edit_activity_start_place);
        edit_activity_end_place = (MaterialEditText) findViewById(R.id.edit_activity_end_place);
        edit_activity_des = (MaterialEditText) findViewById(R.id.edit_activity_des);
        edit_activity_time = (TextView) findViewById(R.id.edit_activity_time);
        bt_publish = (Button) findViewById(R.id.bt_publish);
    }
}
