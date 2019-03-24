package me.amaurytq.alauncher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import me.amaurytq.alauncher.fragments.AppListFragment;
import me.amaurytq.alauncher.fragments.SettingsFragment;
import me.amaurytq.alauncher.fragments.content.ApplicationContent;
import me.amaurytq.alauncher.fragments.models.ApplicationItem;

public class MainActivity extends AppCompatActivity implements
        AppListFragment.OnListFragmentInteractionListener,
        SettingsFragment.OnSettingsFragmentInteractionListener {

    private TextClock tcMonth;
    private SharedPreferences sharedPreferences;

    private static final String APP_LIST_TAG = "appList";
    private static final String SETTINGS_TAG = "settings";

    private AppListFragmentManager appListFragmentManager;

    private void changeFragment(Fragment newFragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment currentFragment = fm.findFragmentByTag(tag);

        if (currentFragment != null && currentFragment.isVisible()) return;

        if (newFragment instanceof AppListFragmentManager)
            appListFragmentManager = (AppListFragmentManager) newFragment;

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
        appListFragmentManager.updateColor(color);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PrefManager prefManager = new PrefManager(this);

        if (prefManager.isFirstTimeLaunch()) {
            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
        }
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        AppListFragment appListFragment = AppListFragment.newInstance(sharedPreferences.getInt("color", Color.WHITE));
        changeFragment(appListFragment, APP_LIST_TAG);
        appListFragmentManager = appListFragment;

        initialize();

        String description = "El color de la fecha se actualiza en base a los colores de tu wallpaper";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            description = description.concat(", pero para esto necesitamos tu permiso para hacerlo, presiona la fecha para darnos ese permiso");

        if (prefManager.isFirstTimeLaunch()) {
            TapTargetView.showFor(this,
                    TapTarget.forView(tcMonth,
                            "Color Personalizado",
                            description)
                            .textTypeface(Typeface.SANS_SERIF)
                            .cancelable(false)
                            .targetRadius(85),
                    new TapTargetView.Listener() {
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                    1);
                            super.onTargetClick(view);
                        }
                    });
            //prefManager.setFirstTimeLaunch(false);
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initialize() {
        ApplicationContent.setPackageManager(this);
        ApplicationContent.fillItemList();

        tcMonth = findViewById(R.id.tcMonth);

        updateColors();
        LinearLayout linearTop = findViewById(R.id.linearTop);
        linearTop.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this){
            public void onSwipeLeft() {
                FragmentManager fm = getSupportFragmentManager();
                Fragment currentFragment = fm.findFragmentByTag(SETTINGS_TAG);
                if (currentFragment != null && currentFragment.isVisible())
                    changeFragment(
                            AppListFragment.newInstance(sharedPreferences.getInt("color", Color.WHITE))
                            , APP_LIST_TAG);
                else
                    changeFragment(new SettingsFragment(), SETTINGS_TAG);
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
            appListFragmentManager.updateAdapter();
        }
    }

    @Override
    public void onLongClickListener(ApplicationItem item) {
        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);

        try {
            Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.setData(Uri.parse("package:".concat(item.packageName)));
            MainActivity.this.startActivity(i);
        } catch (Exception error) {
            Log.e("EE", error.getMessage());
            error.printStackTrace();
            Toast.makeText(MainActivity.this, "La aplicación no se encuentra instalada", Toast.LENGTH_SHORT).show();
            ApplicationContent.fillItemList();
            appListFragmentManager.updateAdapter();
        }
    }

    @Override
    public void onSwipeRefreshAppList() {
        updateColors();
        ApplicationContent.fillItemList();
        appListFragmentManager.updateAdapter();
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
                        startActivityForResult(intent, 333);
                    } else {
                        Intent intent = new Intent(manager.getCropAndSetWallpaperIntent(imageUri));
                        startActivity(intent);
                    }
                    changeFragment(
                            AppListFragment.newInstance(sharedPreferences.getInt("color", Color.WHITE))
                            , APP_LIST_TAG);
                    break;
                case 333:
                    updateColors();
                    break;
            }
        }
    }

}
