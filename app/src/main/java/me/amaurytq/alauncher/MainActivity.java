package me.amaurytq.alauncher;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.Toast;

import me.amaurytq.alauncher.fragments.AppListFragment;
import me.amaurytq.alauncher.fragments.MyAppListRecyclerViewAdapter;
import me.amaurytq.alauncher.fragments.SettingsFragment;
import me.amaurytq.alauncher.fragments.content.ApplicationContent;
import me.amaurytq.alauncher.fragments.models.ApplicationItem;

public class MainActivity extends AppCompatActivity implements
        AppListFragment.OnListFragmentInteractionListener,
        SettingsFragment.OnSettingsFragmentInteractionListener {

    private TextClock tcMonth;
    private SharedPreferences sharedPreferences;
    private static final AppListFragment APP_LIST_FRAGMENT = new AppListFragment();
    private static final SettingsFragment SETTINGS_FRAGMENT = new SettingsFragment();

    private static final String APP_LIST_TAG = "appList";
    private static final String SETTINGS_TAG = "settings";

    private void changeFragment(Fragment newFragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();

        Fragment currentFragment = fm.findFragmentByTag(tag);
        if (currentFragment != null && currentFragment.isVisible()) {
            return;
        }

        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        ft.replace(R.id.containerFrameMain, newFragment, tag);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onBackPressed() {}

    private void updateColors() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                ThemeManager.setColorsFromBackground(getApplicationContext());
            }
        } else {
            ThemeManager.setColorsFromBackground(getApplicationContext());
        }
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
        changeFragment(APP_LIST_FRAGMENT, APP_LIST_TAG);

        initialize();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initialize() {
        ApplicationContent.setPackageManager(this);
        ApplicationContent.fillItemList();

        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        //TextClock tcHour = findViewById(R.id.tcHour);
        tcMonth = findViewById(R.id.tcMonth);
        updateColors();
        LinearLayout linearTop = findViewById(R.id.linearTop);
        linearTop.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this){
            public void onSwipeLeft() {
                FragmentManager fm = getSupportFragmentManager();

                Fragment currentFragment = fm.findFragmentByTag(SETTINGS_TAG);
                if (currentFragment != null && currentFragment.isVisible())
                    changeFragment(APP_LIST_FRAGMENT, APP_LIST_TAG);
                else
                    changeFragment(SETTINGS_FRAGMENT, SETTINGS_TAG);
            }
        });
    }


    @Override
    public void onClickListener(ApplicationItem item) {
        try {
            Intent i = getApplicationContext().getPackageManager().getLaunchIntentForPackage(item.packageName);
            getApplicationContext().startActivity(i);
        } catch (Exception error) {
            Toast.makeText(MainActivity.this, "La aplicación no se encuentra instalada", Toast.LENGTH_SHORT).show();
            ApplicationContent.fillItemList();
            AppListFragment._adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLongClickListener(ApplicationItem item) {
        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);

        try {
            Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.setData(Uri.parse("package:".concat(item.packageName)));
            getApplicationContext().startActivity(i);
        } catch (Exception error) {
            Toast.makeText(MainActivity.this, "La aplicación no se encuentra instalada", Toast.LENGTH_SHORT).show();
            ApplicationContent.fillItemList();
            AppListFragment._adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSwipeRefreshAppList() {
        updateColors();
        ApplicationContent.fillItemList();
        AppListFragment._adapter.notifyDataSetChanged();
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
                    changeFragment(APP_LIST_FRAGMENT, APP_LIST_TAG);
                    break;
            }
        }
    }

}
