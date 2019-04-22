package me.amaurytq.alauncher.fragments.content;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.amaurytq.alauncher.database.models.AppItem;

public class ApplicationContent {

    public static final List<AppItem> ITEMS = new ArrayList<>();
    private static PackageManager _packageManager;
    private static OnApplicationContentInteractionListener mListener;

    public static void setPackageManager(Context context) {
        _packageManager = context.getPackageManager();
    }

    public static void fillAppItemList(List<AppItem> appItemList) {
        ITEMS.clear();
        ITEMS.addAll(appItemList);
    }

    public static List<AppItem> getAppItemList() {
        if (_packageManager == null) return null;
        List<AppItem> appItemList = new ArrayList<>();

        List<ApplicationInfo> packages = _packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            if (_packageManager.getLaunchIntentForPackage(packageInfo.packageName) != null) {
                AppItem appItem = new AppItem();
                appItem.isHidden = false;
                appItem.packageLabel = (String) packageInfo.loadLabel(_packageManager);
                appItem.packageName = packageInfo.packageName;
                appItem.isSystemApp = ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
                addItem(appItem);
            }
        }
        sortItemList();
        return appItemList;
    }

    public static void fillItemList() {
        if (_packageManager == null) return;
        ITEMS.clear();

        List<ApplicationInfo> packages = _packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            if (_packageManager.getLaunchIntentForPackage(packageInfo.packageName) != null) {
                AppItem appItem = new AppItem();
                appItem.isHidden = false;
                appItem.packageLabel = (String) packageInfo.loadLabel(_packageManager);
                appItem.packageName = packageInfo.packageName;
                appItem.isSystemApp = ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
                addItem(appItem);
            }
        }
        if (mListener != null) mListener.notifyUpdated();
        sortItemList();

    }

    private static void sortItemList() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N){
            ITEMS.sort((o1, o2) -> o1.packageLabel.compareTo(o2.packageLabel));
        }
        else {
            Collections.sort(ITEMS, (o1, o2) -> o1.packageLabel.compareTo(o2.packageLabel));
        }
    }

    private static void addItem(AppItem appItem) {
        ITEMS.add(appItem);
    }

    public static void setListener(OnApplicationContentInteractionListener listener) {
        mListener = listener;
    }

    public interface OnApplicationContentInteractionListener {
        void notifyUpdated();
    }
}
