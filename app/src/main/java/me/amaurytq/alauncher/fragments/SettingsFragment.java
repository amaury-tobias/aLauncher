package me.amaurytq.alauncher.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import me.amaurytq.alauncher.R;

public class SettingsFragment extends Fragment {

    public static final int SETTINGS_AUTO_THEME = 5501;
    public static final int SETTINGS_WALLPAPER = 5502;
    public static final int SETTINGS_COLOR_H = 5503;
    public static final int SETTINGS_COLOR_B = 5504;

    public static final String AUTO_THEME = "AUTO_THEME";
    private static final String COLOR_H = "COLOR_H";
    private static final String COLOR_B = "COLOR_B";

    private OnSettingsFragmentInteractionListener mListener;
    private boolean _autoTheme;
    private int _colorH;
    private int _colorB;

    public static SettingsFragment newInstance(boolean autoTheme, int colorH, int colorB){
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putBoolean(AUTO_THEME, autoTheme);
        args.putInt(COLOR_H, colorH);
        args.putInt(COLOR_B, colorB);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _autoTheme = getArguments().getBoolean(AUTO_THEME);
            _colorH = getArguments().getInt(COLOR_H);
            _colorB = getArguments().getInt(COLOR_B);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_settings, container, false);

        fragment.findViewById(R.id.switch_auto_theme).setOnClickListener(v -> {
            _autoTheme = !_autoTheme;
            mListener.onSettingsClickListener(SETTINGS_AUTO_THEME, _autoTheme);
        });
        fragment.findViewById(R.id.settingWallpaper).setOnClickListener(v -> mListener.onSettingsClickListener(SETTINGS_WALLPAPER));
        fragment.findViewById(R.id.settingColorH).setOnClickListener(v -> mListener.onSettingsClickListener(SETTINGS_COLOR_H));
        fragment.findViewById(R.id.settingColorB).setOnClickListener(v -> mListener.onSettingsClickListener(SETTINGS_COLOR_B));

        ((Switch) fragment.findViewById(R.id.switch_auto_theme)).setChecked(_autoTheme);
        fragment.findViewById(R.id.settingColorH).setBackgroundColor(_colorH);
        fragment.findViewById(R.id.settingColorB).setBackgroundColor(_colorB);

        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingsFragmentInteractionListener)
            mListener = (OnSettingsFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnSettingsFragmentInteractionListener {
        void onSettingsClickListener(int option, Object... args);
    }
}
