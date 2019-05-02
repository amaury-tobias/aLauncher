package me.amaurytq.alauncher;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.AlarmClock;
import android.view.Window;
import android.widget.TextClock;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.palette.graphics.Palette;
import me.amaurytq.alauncher.database.models.AppItem;
import me.amaurytq.alauncher.fragments.AppInfoDialogFragment;
import me.amaurytq.alauncher.fragments.AppListFragment;
import me.amaurytq.alauncher.fragments.SettingsFragment;
import me.amaurytq.alauncher.utils.OnSwipeTouchListener;
import me.amaurytq.alauncher.utils.PrefManager;
import me.priyesh.chroma.ChromaDialog;
import me.priyesh.chroma.ColorMode;

public class MainActivity extends AppCompatActivity implements
        SettingsFragment.OnSettingsFragmentInteractionListener,
        AppListFragment.OnListFragmentInteractionListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        AppInfoDialogFragment.AppInfoListener {

    private TextClock tcMonth;
    private SharedPreferences sharedPreferences;

    private static final String APP_LIST_TAG = "appList";
    private static final String SETTINGS_TAG = "settings";

    private static final int RESULT_SELECT_WALLPAPER = 222;
    private static final int RESULT_SET_WALLPAPER = 444;

    private void changeFragment(Fragment newFragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment currentFragment = fm.findFragmentByTag(tag);

        if (currentFragment != null && currentFragment.isVisible()) return;

        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        ft.replace(R.id.containerFrameMain, newFragment, tag);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onBackPressed() {}

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
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        AppListFragment appListFragment = AppListFragment.newInstance(sharedPreferences.getInt("color", Color.WHITE));
        changeFragment(appListFragment, APP_LIST_TAG);

        initialize();

        String description = "El color del tema se actualiza con tu fondo de pantalla, pero para esto necesitamos tu permiso para hacerlo, presiona la fecha para darnos ese permiso";

        if (prefManager.isFirstTimeLaunch()) {
            TapTargetView.showFor(this,
                    TapTarget.forView(tcMonth,
                            "Color Personalizado",
                            description)
                            .textTypeface(Typeface.SANS_SERIF)
                            .outerCircleColor(R.color.colorAccent)
                            .cancelable(false)
                            .targetRadius(70),
                    new TapTargetView.Listener() {
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                    1);
                            super.onTargetClick(view);
                        }
                    });
        }
        prefManager.setFirstTimeLaunch(false);
    }

    private void initialize() {



        tcMonth = findViewById(R.id.tcMonth);
        tcMonth.setTextColor(sharedPreferences.getInt("color", Color.WHITE));
        tcMonth.setOnClickListener(v -> {
            Intent calIntent = new Intent(Intent.ACTION_MAIN);
            calIntent.addCategory(Intent.CATEGORY_APP_CALENDAR);
            calIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(calIntent);
        });

        findViewById(R.id.linearTop).setOnTouchListener(new OnSwipeTouchListener(MainActivity.this){
            public void onSwipeLeft() {
                Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(SETTINGS_TAG);
                if (currentFragment != null && currentFragment.isVisible())
                    changeFragment(
                            AppListFragment.newInstance(sharedPreferences.getInt("color", Color.WHITE))
                            , APP_LIST_TAG);
                else
                    changeFragment(
                            SettingsFragment.newInstance(
                                    sharedPreferences.getInt(SettingsFragment.THEME, 0),
                                    sharedPreferences.getInt("color", Color.WHITE),
                                    sharedPreferences.getInt("color", Color.WHITE))
                            , SETTINGS_TAG);
            }
        });

        findViewById(R.id.tcHour).setOnClickListener(v -> {
            Intent openClockIntent = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
            openClockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MainActivity.this.startActivity(openClockIntent);
        });
    }


    @Override
    public void onClickListener(AppItem item) {
        Intent i = getApplicationContext().getPackageManager().getLaunchIntentForPackage(item.packageName);
        getApplicationContext().startActivity(i);
    }

    @Override
    public boolean onLongClickListener(AppItem item) {
        String APP_INFO_TAG = "me.amaurytq.me.AppItemInfo";
        ((Vibrator) getApplicationContext()
                .getSystemService(Context.VIBRATOR_SERVICE))
                .vibrate(100);
        BottomSheetDialogFragment bsdFragment =
                AppInfoDialogFragment.newInstance(item);

        bsdFragment.show(getSupportFragmentManager(), APP_INFO_TAG);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case RESULT_SELECT_WALLPAPER:
                if (null == data) return;
                else cropAndSetWallpaper(data);
                break;
            case RESULT_SET_WALLPAPER:
                getColorsFromBackground();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SettingsFragment.THEME)) {
            recreate();
        }
        if (key.equals("color")) {
            tcMonth.setTextColor(sharedPreferences.getInt("color", Color.WHITE));
            changeFragment(AppListFragment.newInstance(sharedPreferences.getInt("color", Color.WHITE)), APP_LIST_TAG);
        }
    }

    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();
        SharedPreferences settings = getSharedPreferences("prefs", MODE_PRIVATE);
        int active = settings.getInt(SettingsFragment.THEME, 0);
        theme.applyStyle(resolveTheme(active), true);
        return theme;
    }

    private int resolveTheme(int i) {
        switch (i)
        {
            case 1:
                return R.style.AppThemeLight;
            default:
                return R.style.AppThemeDark;
        }
    }

    private void cropAndSetWallpaper(Intent data) {
        final Uri imageUri = data.getData();
        WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!manager.isSetWallpaperAllowed() && !manager.isWallpaperSupported())return;
            Intent intent = new Intent(manager.getCropAndSetWallpaperIntent(imageUri));
            startActivityForResult(intent, RESULT_SET_WALLPAPER);
        } else {
            Intent intent = new Intent(manager.getCropAndSetWallpaperIntent(imageUri));
            startActivityForResult(intent, RESULT_SET_WALLPAPER);
        }
    }

    public void getColorsFromBackground() {
        WallpaperManager wmInstance = WallpaperManager.getInstance(MainActivity.this);
        Bitmap bitmap = ((BitmapDrawable)wmInstance.getDrawable()).getBitmap();

        Palette p = Palette.from(bitmap).generate();
        int color;
        Palette.Swatch swatch = p.getVibrantSwatch();

        if (swatch == null) color = Color.WHITE;
        else color = swatch.getRgb();

        sharedPreferences.edit().putInt("color", color).apply();
    }

    // SETTINGS FRAGMENT
    @Override
    public void setWallpaper() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_SELECT_WALLPAPER);
    }

    @Override
    public void pickColor(int _color) {
        new ChromaDialog.Builder()
                .initialColor(_color)
                .colorMode(ColorMode.HSV)
                .onColorSelected(color -> getSharedPreferences("prefs", MODE_PRIVATE)
                        .edit().putInt("color", color).apply())
                .create()
                .show(getSupportFragmentManager(), "chromaDialog");
    }

    @Override
    public void autoColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                getColorsFromBackground();
            }
        } else getColorsFromBackground();
    }

    @Override
    public void onAppInfoClicked(int position) {

    }

    @Override
    public void uninstall(AppItem mAppitem) {
        Uri packageURI = Uri.parse("package:".concat(mAppitem.packageName));
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        startActivity(uninstallIntent);
    }
}
