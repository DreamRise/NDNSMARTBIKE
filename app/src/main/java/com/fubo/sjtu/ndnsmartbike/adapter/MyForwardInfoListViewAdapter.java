package com.fubo.sjtu.ndnsmartbike.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fubo.sjtu.ndnsmartbike.R;
import com.fubo.sjtu.ndnsmartbike.model.ForwardInfo;
import com.fubo.sjtu.ndnsmartbike.utils.SimpleDateFormatUtil;

import java.util.List;

/**
 * Created by sjtu on 2015/11/24.
 */
public class MyForwardInfoListViewAdapter extends BaseAdapter {

    private List<ForwardInfo> forwardInfoList;
    private Context context;

    public MyForwardInfoListViewAdapter(Context context, List<ForwardInfo> forwardInfos) {
        this.context=context;
        this.forwardInfoList = forwardInfos;
    }
    @Override
    public int getCount() {
        return forwardInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return forwardInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=null;
        if (convertView==null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_forwardinfo_layout,
                    null);
        }
        view=convertView;
        ((TextView)(view.findViewById(R.id.tv_forwardId))).setText(forwardInfoList.get(position).getId());
        ((TextView)(view.findViewById(R.id.tv_forwardDataName))).setText(forwardInfoList.get(position).getDataName());
        ((TextView)(view.findViewById(R.id.tv_forwardInterestName))).setText(forwardInfoList.get(position).getInterestName());
        ((TextView)(view.findViewById(R.id.tv_forwardInterestFrom))).setText(forwardInfoList.get
                (position).getInterestFrom());
        if (forwardInfoList.get(position).getType()==0) {
            ((TextView) (view.findViewById(R.id.tv_forwardType))).setText(context.getResources().getString(R.string.interest_packet));
        }else{
            ((TextView) (view.findViewById(R.id.tv_forwardType))).setText(context.getResources().getString(R.string.data_packet));
        }
        ((TextView)(view.findViewById(R.id.tv_forwardFlag))).setText(""+forwardInfoList.get(position).getFlag());
        ((TextView) (view.findViewById(R.id.tv_forwardBuildDate))).setText(SimpleDateFormatUtil
                .formatUtilDate(forwardInfoList.get(position).getBuildDate(),
                        SimpleDateFormatUtil.NORMAL_UTIL_DATE_FORMAT));
        return view;
    }
}
