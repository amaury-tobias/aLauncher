package me.amaurytq.alauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PackageBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_PACKAGE_ADDED.equals(action) || Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
            MainActivity instance = MainActivity.getInstance();
            instance.updateAdapter();
        }
    }
}
