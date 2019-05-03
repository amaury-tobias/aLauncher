package me.amaurytq.alauncher.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    @OnClick(R.id.switch_theme)
    void onSwitchThemeClick() {
        setTheme();
    }

    @OnClick(R.id.auto_color)
    void onAutoColorClick() {
        mListener.autoColor();
    }

    @OnClick(R.id.settingWallpaper)
    void onSetWallpaperClick() {
        mListener.setWallpaper();
    }

    @OnClick(R.id.settingColorH)
    void onPickColorClick() {
        mListener.pickColor(_colorH);
    }

    @BindView(R.id.switch_theme) Switch mSwitch;
    @BindView(R.id.color_1) ImageView mImageView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, fragment);
        mSwitch.setChecked(_theme == 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mSwitch.setThumbTintList(ColorStateList.valueOf(_colorH));
        }
        mImageView.setBackgroundColor(_colorH);
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
