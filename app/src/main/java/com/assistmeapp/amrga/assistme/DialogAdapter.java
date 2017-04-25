package com.assistmeapp.amrga.assistme;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.assistmeapp.amrga.assistme.model.DialogFetcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amrga on 9/10/2016.
 */
public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.CustomViewHolder> {

    private Context context;
    private List<DialogItem> list;
    private String callerName;

    public DialogAdapter(Context context, String callerName) {
        this.context = context;
        this.callerName = callerName;
        list = new ArrayList<>();
        list.clear();
        DialogFetcher df = new DialogFetcher();
        list.addAll(df.getList(callerName));
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dialog_card, parent, false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, final int position) {
        DialogItem dialogItem = list.get(position);
        holder.dialogItem.setText(dialogItem.getText());
        if (dialogItem.getTagThumbnail() != -1) {

            holder.tag.setImageResource(dialogItem.getTagThumbnail());
        }
        else {
            holder.tag.setVisibility(View.GONE);
        }
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callerName.equals("actionFragment")) {
                    NewTaskActivity.setActionText(list.get(position).getText());
                }
                else if (callerName.equals("tagFragment")) {
                    NewTaskActivity.setTag(list.get(position).getTagThumbnail(),
                                                    list.get(position).getText());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private ImageView tag;
        private TextView dialogItem;
        private LinearLayout linearLayout;

        public CustomViewHolder(View itemView) {
            super(itemView);
            tag = (ImageView) itemView.findViewById(R.id.tag_thumbnail);
            dialogItem = (TextView) itemView.findViewById(R.id.dialog_item);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.dialog_card_linear_layout);
        }
    }

}
