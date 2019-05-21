package me.amaurytq.alauncher.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import me.amaurytq.alauncher.R;
import me.amaurytq.alauncher.database.models.AppItem;

public class AppInfoDialogFragment extends BottomSheetDialogFragment {

    private AppItem mAppItem;

    public static AppInfoDialogFragment newInstance(AppItem appItem) {
        final AppInfoDialogFragment fragment = new AppInfoDialogFragment();
        fragment.mAppItem = appItem;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appinfo_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ((TextView)view.findViewById(R.id.app_info_title)).setText(mAppItem.packageLabel);

        if (!mAppItem.isSystemApp) {
            view.findViewById(R.id.uninstall_option).setVisibility(View.VISIBLE);
            view.findViewById(R.id.uninstall_option).setOnClickListener(v -> {
                Uri packageURI = Uri.parse("package:".concat(mAppItem.packageName));
                Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
                uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(uninstallIntent);
                dismiss();
            });
        }

        view.findViewById(R.id.app_info_button).setOnClickListener(v -> {
            Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.setData(Uri.parse("package:".concat(mAppItem.packageName)));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            dismiss();
        });
    }

}
