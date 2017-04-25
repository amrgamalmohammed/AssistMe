package com.assistmeapp.amrga.assistme;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;

import com.assistmeapp.amrga.assistme.model.DialogFetcher;

/**
 * Created by amrga on 9/17/2016.
 */
public class ActionFilter {

    private String action;
    private Context context;

    public ActionFilter (Context context) {
        this.context = context;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean executeAction() {
        if (action.equals(DialogFetcher.RINGER) || action.equals(DialogFetcher.SILENT) || action.equals(DialogFetcher.VIBRATE)) {
            return changeAudioSettings(action);
        }
        else if (action.equals(DialogFetcher.BLUETOOTH_ON) || action.equals(DialogFetcher.BLUETOOTH_OFF)) {
            return changeBluetoothSettings(action);
        }
        else if (action.equals(DialogFetcher.WIFI_ON) || action.equals(DialogFetcher.WIFI_OFF)) {
            return changeWifiSettings(action);
        }
        else if (action.equals(DialogFetcher.DIM_LIGHT)) {
            return changeLightSettings(action);
        }
        return false;
    }

    private boolean changeAudioSettings(String settings) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        switch (settings) {
            case DialogFetcher.RINGER:
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                return true;
            case DialogFetcher.SILENT :
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                return true;
            case DialogFetcher.VIBRATE :
                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                return true;
        }

        return false;
    }

    private boolean changeBluetoothSettings (String settings) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        switch (settings) {
            case DialogFetcher.BLUETOOTH_ON :
                return bluetoothAdapter.enable();
            case DialogFetcher.BLUETOOTH_OFF :
                return bluetoothAdapter.disable();
        }
        return false;
    }

    private boolean changeWifiSettings(String settings) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        switch (settings) {
            case DialogFetcher.WIFI_ON:
                return wifiManager.setWifiEnabled(true);
            case DialogFetcher.WIFI_OFF:
                return wifiManager.setWifiEnabled(false);
        }
        return false;
    }

    private boolean changeLightSettings(String settings) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context.getApplicationContext()))  {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return false;
            }
        }
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 10);
        return true;
    }
}
