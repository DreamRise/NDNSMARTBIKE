package com.fubo.sjtu.ndnsmartbike.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fubo.sjtu.ndnsmartbike.R;
import com.fubo.sjtu.ndnsmartbike.model.ActivityInfo;
import com.fubo.sjtu.ndnsmartbike.utils.SimpleDateFormatUtil;

import java.util.List;

/**
 * Created by sjtu on 2015/11/6.
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ActivityInfo> data;
    private Context context;
    private static final int TYPE_ITEM=0;
    private static final int TYPE_FOOTER=1;
    private View footer;
    private boolean isLoading=false;
    @Override
    public int getItemViewType(int position) {
        if(position+1==getItemCount())
            return TYPE_FOOTER;
        else
            return TYPE_ITEM;
    }

    public MyRecyclerViewAdapter(Context context,List<ActivityInfo> data){
        this.context=context;
        this.data=data;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=null;
        if (viewType==TYPE_ITEM) {
            view = LayoutInflater.from(context).inflate(R.layout.item_activityinfo_layout_new, null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ItemViewHolder(view);
        }
        else {
            footer= LayoutInflater.from(context).inflate(R.layout.recyclerview_footer_layout,null);
            footer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            footer.setVisibility(View.GONE);
            return new FooterViewHolder(footer);
        }

    }
    public void showLoadMore(){
        if (!isLoading){
            isLoading=true;
            footer.setVisibility(View.VISIBLE);
        }
    }
    public void loadComplete(){
        if (isLoading){
            isLoading=false;
            footer.setVisibility(View.GONE);
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof  ItemViewHolder) {
            ((ItemViewHolder) holder).tv_activityinfo_title.setText(data.get(position).getActivityTitle());
            ((ItemViewHolder) holder).tv_activityinfo_time.setText(SimpleDateFormatUtil
                    .formatUtilDate(data.get(position).getActivityDate(), SimpleDateFormatUtil
                            .UTIL_DATE_WITHOUT_SECOND));
            ((ItemViewHolder) holder).tv_activityinfo_route.setText(data.get(position)
                    .getActivityStartPlace() + "-" + data.get(position).getActivityEndPlace());
            ((ItemViewHolder) holder).tv_activityinfo_des.setText(data.get(position)
                    .getActivityDes());
        }
    }

    @Override
    public int getItemCount() {
        return data.size()+1;
    }
    class FooterViewHolder extends  RecyclerView.ViewHolder{
        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }
    class ItemViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_activityinfo_title;
        public TextView tv_activityinfo_time;
        public TextView tv_activityinfo_route;
        public TextView tv_activityinfo_des;
        public ItemViewHolder(View itemView) {
            super(itemView);
            tv_activityinfo_title=(TextView)itemView.findViewById(R.id.tv_activityinfo_title);
            tv_activityinfo_time=(TextView)itemView.findViewById(R.id.tv_activityinfo_time);
            tv_activityinfo_route=(TextView)itemView.findViewById(R.id.tv_activityinfo_route);
            tv_activityinfo_des=(TextView)itemView.findViewById(R.id.tv_activityinfo_des);
        }
    }
}
