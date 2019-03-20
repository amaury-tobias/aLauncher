package me.amaurytq.alauncher;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextClock;

import me.amaurytq.alauncher.fragments.AppListFragment;
import me.amaurytq.alauncher.fragments.MyAppListRecyclerViewAdapter;
import me.amaurytq.alauncher.fragments.SettingsFragment;
import me.amaurytq.alauncher.fragments.models.ApplicationItem;

public class MainActivity extends AppCompatActivity implements
        AppListFragment.OnListFragmentInteractionListener,
        SettingsFragment.OnSettingsFragmentInteractionListener {

    private TextClock tcMonth;
    private SharedPreferences sharedPreferences;

    private void changeFragment(Fragment newFragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment currentFragment = fm.findFragmentByTag(tag);
        if (currentFragment != null && currentFragment.isVisible()) {
            return;
        }

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.containerFrameMain, newFragment, tag);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        changeFragment(new AppListFragment(), "appList");
        // AppListFragment._adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                ThemeManager.setColorsFromBackground(getApplicationContext());
            }
        } else {
            ThemeManager.setColorsFromBackground(getApplicationContext());
        }
        if (AppListFragment._adapter != null)
            AppListFragment._adapter.notifyDataSetChanged();
        updateColors();
    }

    private void updateColors() {
        int color = sharedPreferences.getInt("color", Color.WHITE);
        tcMonth.setTextColor(color);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PrefManager prefManager = new PrefManager(this);
        if (prefManager.isFirstTimeLaunch()) {
            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
            prefManager.setFirstTimeLaunch(false);
        }
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        changeFragment(new AppListFragment(), "appList");
        initialize();
    }

    private void initialize() {

        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        TextClock tcHour = findViewById(R.id.tcHour);
        tcMonth = findViewById(R.id.tcMonth);

        tcHour.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(100);
                changeFragment(new SettingsFragment(), "settings");
                return false;
            }
        });
    }


    @Override
    public void onClickListener(ApplicationItem item) {
        Intent i = getApplicationContext().getPackageManager().getLaunchIntentForPackage(item.packageName);
        getApplicationContext().startActivity(i);
    }

    @Override
    public void onLongClickListener(ApplicationItem item) {
        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
        Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.setData(Uri.parse("package:".concat(item.packageName)));
        getApplicationContext().startActivity(i);
    }

    private static final int RESULT_SELECT_WALLPAPER = 222;

    @Override
    public void onSettingsClickListener(int option) {
        switch (option) {
            case SettingsFragment.GO_SETTINGS_WALLPAPER:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_SELECT_WALLPAPER);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_SELECT_WALLPAPER:
                    if (null == data) return;
                    final Uri imageUri = data.getData();
                    WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (!manager.isSetWallpaperAllowed() && !manager.isWallpaperSupported())return;
                        Intent intent = new Intent(manager.getCropAndSetWallpaperIntent(imageUri));
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(manager.getCropAndSetWallpaperIntent(imageUri));
                        startActivity(intent);
                    }
                    changeFragment(new AppListFragment(), "appList");
                    break;
            }
        }
    }
}
