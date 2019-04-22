package me.amaurytq.alauncher.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import me.amaurytq.alauncher.R;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {

    public static final String THEME = "THEME";

    private static final String COLOR_H = "COLOR_H";
    private static final String COLOR_B = "COLOR_B";

    private OnSettingsFragmentInteractionListener mListener;

    private int _theme;
    private int _colorH;

    public static SettingsFragment newInstance(int theme, int colorH, int colorB){
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putInt(THEME, theme);
        args.putInt(COLOR_H, colorH);
        args.putInt(COLOR_B, colorB);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _theme = getArguments().getInt(THEME);
            _colorH = getArguments().getInt(COLOR_H);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_settings, container, false);

        fragment.findViewById(R.id.switch_theme).setOnClickListener(v -> setTheme());
        fragment.findViewById(R.id.auto_color).setOnClickListener(v -> mListener.autoColor());
        fragment.findViewById(R.id.settingWallpaper).setOnClickListener(v -> mListener.setWallpaper());
        fragment.findViewById(R.id.settingColorH).setOnClickListener(v -> mListener.pickColor(_colorH));

        ((Switch) fragment.findViewById(R.id.switch_theme)).setChecked(_theme == 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ((Switch) fragment.findViewById(R.id.switch_theme)).setThumbTintList(ColorStateList.valueOf(_colorH));
        }
        fragment.findViewById(R.id.color_1).setBackgroundColor(_colorH);

        return fragment;
    }

    private void setTheme() {
        if (_theme == 0) _theme = 1;
        else _theme = 0;
        Objects.requireNonNull(getActivity())
                .getSharedPreferences("prefs", MODE_PRIVATE)
                .edit().putInt(THEME, _theme).apply();
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
        void setWallpaper();
        void pickColor(int color);
        void autoColor();
    }
}
