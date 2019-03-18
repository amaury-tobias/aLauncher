package me.amaurytq.alauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PackageBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        MainActivity instance = ((MainActivity)context);
        if (instance != null && action != null) {
            switch (action) {
                case Intent.ACTION_PACKAGE_ADDED:
                    instance.updateAdapter();
                    break;
                case Intent.ACTION_PACKAGE_REMOVED:
                    instance.updateAdapter();
                    break;
            }
        }
    }
}
