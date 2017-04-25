package com.assistmeapp.amrga.assistme;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.assistmeapp.amrga.assistme.model.AlarmInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by amrga on 9/7/2016.
 */
public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.CustomViewHolder> {

    private Context context;
    private List<AlarmItem> list;
    private String callerName;
    private String fragmentName;
    private DataFetcher df;
    private UpdateSubject updateSubject;

    public AlarmAdapter (Context context, String name, UpdateSubject updateSubject) {
        this.context = context;
        df = new DataFetcher();
        list = new ArrayList<>();
        fragmentName = name;
        this.updateSubject = updateSubject;
        switch (fragmentName) {
            case "TodayFragment" :
                list.clear();
                callerName = "today";
                list.addAll(df.getData(callerName));
                break;
            case "ThisWeekFragment" :
                list.clear();
                callerName = "week";
                list.addAll(df.getData(callerName));
                break;
            case "ThisMonthFragment" :
                list.clear();
                callerName = "month";
                list.addAll(df.getData(callerName));
                break;
        }
        notifyDataSetChanged();
    }

    @Override
    public CustomViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_card, parent, false);

        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder (final CustomViewHolder holder, int position) {
        AlarmItem item = list.get(position);
        holder.title.setText(item.getTitle());
        holder.message.setText(item.getMessage());
        Calendar cal = Calendar.getInstance();
        long now = cal.getTimeInMillis();
        cal.setTimeInMillis(item.getDate());
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        String min = minute < 10 ? "0"+String.valueOf(minute) : String.valueOf(minute);
        String time = hour > 12 ? String.valueOf(hour-12)+":"+min+" PM" :
                                    String.valueOf(hour)+":"+min+" AM";
        holder.at.setText(time);

        cal.setTimeInMillis(item.getDate()-now);
        long remainingTime = cal.getTimeInMillis();
        String remaining = "";
        remaining += (remainingTime/(1000*60*60*24))%30 != 0 ? "\n"+String.valueOf((remainingTime/(1000*60*60*24))%30)+" days" : "";
        remaining += (remainingTime/(1000*60*60))%24 != 0 ? "\n"+String.valueOf((remainingTime/(1000*60*60))%24)+" hours" : "";
        remaining += (remainingTime/(1000*60))%60 != 0 ? "\n"+String.valueOf((remainingTime/(1000*60))%60)+" minutes" : "";
        remaining += (remainingTime/(1000))%60 != 0 ? "\n"+String.valueOf((remainingTime/(1000))%60)+" seconds" : "";

        holder.remaining.setText(remaining);

        holder.thumbnail.setImageResource(item.getThumbnail());

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, holder.getLayoutPosition());
            }
        });
    }

    private void showPopupMenu(View view, int position) {

        PopupMenu menu = new PopupMenu(context, view);
        MenuInflater inflater = menu.getMenuInflater();
        inflater.inflate(R.menu.menu_alarm, menu.getMenu());
        menu.setOnMenuItemClickListener(new CustomMenuItemClickListener(position));
        menu.show();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView title, at, message, remaining;
        private ImageView thumbnail, overflow;

        public CustomViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.alarm_title);
            at = (TextView) view.findViewById(R.id.time_at);
            message = (TextView) view.findViewById(R.id.alarm_message);
            remaining = (TextView) view.findViewById(R.id.remaining_time);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overFlow);
        }
    }

    class CustomMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private int position;

        public CustomMenuItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            long id = list.get(position).getId();
            switch (menuItem.getItemId()) {
                case R.id.action_delete:
                    Intent delAllService = new Intent(context, TaskService.class);
                    delAllService.setAction(TaskService.CANCEL_ALL);
                    delAllService.putExtra(AlarmInfo.COL_ALARMID, String.valueOf(id));
                    delAllService.putExtra("POSITION", position);
                    delAllService.putExtra("FRAGMENT", fragmentName);
                    context.startService(delAllService);
                    return true;
                case R.id.action_delete_rep:
                    Intent delRepService = new Intent(context, TaskService.class);
                    delRepService.setAction(TaskService.CANCEL_REPEATING);
                    delRepService.putExtra(AlarmInfo.COL_ALARMID, String.valueOf(id));
                    delRepService.putExtra("POSITION", position);
                    delRepService.putExtra("FRAGMENT", fragmentName);
                    context.startService(delRepService);
                    return true;
                default:
                    return false;
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void commitUpdate() {
        List<AlarmItem> oldList = new ArrayList<>();
        oldList.addAll(list);
        int oldSize = oldList.size();
        list.clear();
        list.addAll(df.getData(callerName));
        int newSize = list.size();
        int j = 0; int i; List<Integer> positions = new ArrayList<>();
        for (i =0; i < oldSize & j < newSize; i++) {
            if (oldList.get(i).getId()==list.get(j).getId()) {
                j++;
            }
            else {
                positions.add(i);
            }
        }
        positions.add(i);
        if (i != (oldSize-1)) {
            while (oldSize-i > 1) {
                i++;
                positions.add(i);
            }
        }
        for (j = 0; j < positions.size(); j++) {
            notifyItemRemoved(positions.get(j)-j);
        }
    }

    public void commitUpdateBlind() {
        int oldSize = list.size();
        list.clear();
        list.addAll(df.getData(callerName));
        int newSize = list.size();
        if (oldSize > newSize) {
            for (int i = 0; i < (oldSize-newSize); i++) {
                notifyDataSetChanged();
            }
        }
        else if (newSize > oldSize) {
            for (int i = 0; i < (newSize-oldSize); i++) {
                notifyDataSetChanged();
            }
        }
    }
}
