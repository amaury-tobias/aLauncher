package me.amaurytq.alauncher.fragments.content;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.amaurytq.alauncher.fragments.models.ApplicationItem;

public class ApplicationContent {

    public static final List<ApplicationItem> ITEMS = new ArrayList<>();
    private static PackageManager _packageManager;

    public static void setPackageManager(Context context) {
        _packageManager = context.getPackageManager();
    }

    public static void fillItemList() {
        if (_packageManager == null) return;

        ITEMS.clear();
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> applicationResolver = _packageManager.queryIntentActivities(i, 0);
        for (ResolveInfo ri : applicationResolver) {
            ApplicationItem applicationItem = new ApplicationItem(
                    (String) ri.loadLabel(_packageManager),
                    ri.activityInfo.packageName
            );
            addItem(applicationItem);
        }
        sortItemList();
    }

    private static void sortItemList() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N){
            ITEMS.sort(new Comparator<ApplicationItem>() {
                @Override
                public int compare(ApplicationItem o1, ApplicationItem o2) {
                    return o1.packageLabel.compareTo(o2.packageLabel);
                }
            });
        }
        else {
            Collections.sort(ITEMS, new Comparator<ApplicationItem>() {
                @Override
                public int compare(ApplicationItem o1, ApplicationItem o2) {
                    return o1.packageLabel.compareTo(o2.packageLabel);
                }
            });
        }
    }

    private static void addItem(ApplicationItem item) {
        ITEMS.add(item);
    }

}
