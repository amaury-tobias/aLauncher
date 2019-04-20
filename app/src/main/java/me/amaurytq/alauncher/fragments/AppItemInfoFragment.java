package me.amaurytq.alauncher.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import me.amaurytq.alauncher.R;
import me.amaurytq.alauncher.database.models.AppItem;

public class AppItemInfoFragment extends DialogFragment {

    public static AppItemInfoFragment newInstance(AppItem appItem) {
        AppItemInfoFragment fragment = new AppItemInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable("APP_ITEM", appItem);
        fragment.setArguments(args);
        return fragment;
    }
    private AppItem _appItem;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            _appItem = (AppItem) getArguments().getSerializable("APP_ITEM");
        }
        return createDialog();
    }

    private Dialog createDialog() {

        Dialog dialog = new Dialog(Objects.requireNonNull(getActivity()));
        Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.app_item_info);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ((TextView)dialog.findViewById(R.id.app_info_title)).setText(_appItem.packageLabel);
        if (!_appItem.isSystemApp) {
            dialog.findViewById(R.id.uninstall_option).setVisibility(View.VISIBLE);
            dialog.findViewById(R.id.uninstall_option).setOnClickListener(v -> {
                Uri packageUri = Uri.parse("package:".concat(_appItem.packageName));
                Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
                startActivity(uninstallIntent);
            });
        }
        dialog.findViewById(R.id.app_info_button).setOnClickListener(v -> {
            Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.setData(Uri.parse("package:".concat(_appItem.packageName)));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        return dialog;
    }
}
