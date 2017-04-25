package com.assistmeapp.amrga.assistme.model;

import android.util.Pair;

import com.assistmeapp.amrga.assistme.DialogItem;
import com.assistmeapp.amrga.assistme.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amrga on 9/10/2016.
 */
public class DialogFetcher {

    private List<DialogItem> list;
    public static final String SILENT = "Silent";
    public static final String VIBRATE  = "Vibrate";
    public static final String RINGER = "Ringer";
    public static final String WIFI_ON = "Wifi on";
    public static final String WIFI_OFF = "Wifi off";
    public static final String BLUETOOTH_ON = "Bluetooth on";
    public static final String BLUETOOTH_OFF = "Bluetooth off";
    public static final String DIM_LIGHT = "Dim light";

    public List<DialogItem> getList(String caller) {
        list = new ArrayList<>();
        switch (caller) {
            case "actionFragment" :
                return getActionList();
            case "tagFragment" :
                return getTagList();
            default:
                return null;
        }
    }

    private List<DialogItem> getActionList() {
        list.clear();
        for (String s : actions) {
            DialogItem dialogItem = new DialogItem();
            dialogItem.setText(s);
            dialogItem.setTagThumbnail(-1);
            list.add(dialogItem);
        }
        return list;
    }

    private List<DialogItem> getTagList() {
        list.clear();
        for (Pair p : tags) {
            DialogItem dialogItem = new DialogItem();
            dialogItem.setText((String) p.second);
            dialogItem.setTagThumbnail((Integer) p.first);
            list.add(dialogItem);
        }
        return list;
    }

    private String[] actions = new String[]{SILENT,VIBRATE,RINGER,WIFI_ON, WIFI_OFF, BLUETOOTH_ON, BLUETOOTH_OFF, DIM_LIGHT};

    private Pair<Integer, String>[] tags = new Pair[]{new Pair(R.drawable.ic_reading,"Reading"), new Pair(R.drawable.ic_sleeping,"Sleeping"),
                                            new Pair(R.drawable.ic_eating,"Eating"), new Pair(R.drawable.ic_coffee, "Cafe`"), new Pair(R.drawable.ic_sports,"Sports"),
                                            new Pair(R.drawable.ic_cinema, "Cinema"), new Pair(R.drawable.ic_house,"Housework"), new Pair(R.drawable.ic_relation,"Relationship"),
                                            new Pair(R.drawable.ic_shopping, "Shopping"), new Pair(R.drawable.ic_internet,"Internet"),new Pair(R.drawable.ic_study,"Studying"),
                                            new Pair(R.drawable.ic_pray, "Praying"), new Pair(R.drawable.ic_work, "Work"), new Pair(R.drawable.ic_phone, "Call"),
                                            new Pair(R.drawable.ic_email, "Email")};
}
