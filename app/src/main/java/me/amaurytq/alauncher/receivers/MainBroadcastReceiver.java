package me.amaurytq.alauncher.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import me.amaurytq.alauncher.MainActivity;

public class MainBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            Intent i;
            switch (action) {
                case Intent.ACTION_PACKAGE_ADDED:
                    MainActivity.updateAdapter();
                    break;
                case Intent.ACTION_PACKAGE_REMOVED:
                    MainActivity.updateAdapter();
                    break;
            }
        }
    }
}
