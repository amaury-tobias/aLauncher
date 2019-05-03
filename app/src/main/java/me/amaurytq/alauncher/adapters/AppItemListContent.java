package me.amaurytq.alauncher.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.amaurytq.alauncher.database.models.AppItem;

public class AppItemListContent {
    public final List<AppItem> appItems = new ArrayList<>();
    private Context context;

    public AppItemListContent(Context context) {
        this.context = context;
        fillList();
    }

    public void fillList() {
        appItems.clear();
        appItems.addAll(getAppItems());
    }

    private List<AppItem> getAppItems() {
        List<AppItem> items = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = packageManager.queryIntentActivities( mainIntent, 0);
        for (ResolveInfo resolveInfo: pkgAppsList) {
            AppItem item = new AppItem();
            item.isHidden = false;
            item.packageLabel = (String) resolveInfo.loadLabel(packageManager);
            item.packageName = resolveInfo.activityInfo.packageName;
            item.isSystemApp = ((resolveInfo.activityInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
            items.add(item);
        }

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N){
            items.sort((o1, o2) -> o1.packageName.toUpperCase().compareTo(o2.packageLabel.toUpperCase()));
        }
        else {
            Collections.sort(items, (o1, o2) -> o1.packageLabel.toUpperCase().compareTo(o2.packageLabel.toUpperCase()));
        }
        return items;
    }
}
