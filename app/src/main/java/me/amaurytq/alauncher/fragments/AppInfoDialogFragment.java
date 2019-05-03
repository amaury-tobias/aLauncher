package me.amaurytq.alauncher.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import me.amaurytq.alauncher.R;
import me.amaurytq.alauncher.database.models.AppItem;

public class AppInfoDialogFragment extends BottomSheetDialogFragment {

    private static final int RESULT_UNINSTALLED = 1234;
    private AppInfoListener mListener;
    private AppItem mAppitem;

    public static AppInfoDialogFragment newInstance(AppItem appItem) {
        final AppInfoDialogFragment fragment = new AppInfoDialogFragment();
        fragment.mAppitem = appItem;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appinfo_list_dialog, container, false);
        /*
        if (!mAppitem.isSystemApp) {
            view.findViewById(R.id.uninstall_option).setVisibility(View.VISIBLE);
            view.findViewById(R.id.uninstall_option).setOnClickListener(v -> {
                mListener.uninstall(mAppitem);
            });
        }
        */
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ((TextView)view.findViewById(R.id.app_info_title)).setText(mAppitem.packageLabel);

        view.findViewById(R.id.app_info_button).setOnClickListener(v -> {
            Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.setData(Uri.parse("package:".concat(mAppitem.packageName)));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent instanceof AppInfoListener) {
            mListener = (AppInfoListener) parent;
        } else {
            mListener = (AppInfoListener) context;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_UNINSTALLED:
                dismiss();
                break;
        }
    }

    public interface AppInfoListener {
        void onAppInfoClicked(int position);
        void uninstall(AppItem packageName);
    }

}
