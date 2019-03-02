package me.amaurytq.alauncher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PackageListAdapter extends BaseAdapter {

    private PackageManager packageManager;
    private List<AppInfo> appsList;

    public PackageListAdapter(Context context) {
        packageManager = context.getPackageManager();
        updateList();
    }

    public void updateList() {
        appsList = new ArrayList<>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> allApps = packageManager.queryIntentActivities(i, 0);

        for(ResolveInfo ri : allApps) {
            AppInfo app = new AppInfo();
            app.setLabel(ri.loadLabel(packageManager));
            app.setPackageName(ri.activityInfo.packageName);
            app.setIcon(ri.activityInfo.loadIcon(packageManager));
            appsList.add(app);
        }

        Collections.sort(appsList, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo o1, AppInfo o2) {
                return o1.getLabel().toString().compareTo(o2.getLabel().toString());
            }
        });
    }

    @Override
    public int getCount() {
        return appsList.size();
    }

    @Override
    public Object getItem(int position) {
        return appsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.launch_item, null);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(appsList.get(position).getPackageName().toString());
                context.startActivity(launchIntent);
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Context context = v.getContext();
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(100);
                Intent launchIntent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                launchIntent.setData(Uri.parse("package:" + appsList.get(position).getPackageName().toString()));
                context.startActivity(launchIntent);
                return true;
            }
        });

        TextView tvAppName = convertView.findViewById(R.id.tvAppName);
        tvAppName.setText(appsList.get(position).getLabel());
        return convertView;
    }
}
