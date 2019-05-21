package me.amaurytq.alauncher.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

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
        Collections.sort(pkgAppsList, new ResolveInfo.DisplayNameComparator(packageManager));

        for (ResolveInfo resolveInfo: pkgAppsList) {
            AppItem item = new AppItem();
            item.isHidden = false;
            item.packageLabel = resolveInfo.loadLabel(packageManager).toString();
            item.packageName = resolveInfo.activityInfo.packageName;
            item.isSystemApp = ((resolveInfo.activityInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
            items.add(item);
        }

        return items;
    }
}
