package me.amaurytq.alauncher;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private PackageListAdapter appsAdapter;
    private TextView tvMes;
    private SharedPreferences sharedPreferences;

    public static final int RESULT_PRO_IMG=1;


    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);

        updateCalendar();

        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        Bitmap bitmap = ((BitmapDrawable)wallpaperDrawable).getBitmap();

        ColorManager.setColorsFromBackground(this, bitmap);
        updateColors();
    }

    private void updateColors() {
        int color = sharedPreferences.getInt("color", R.color.colorTextLauncher);
        //tcHour.setTextColor(color);
        tvMes.setTextColor(color);
    }

    private void updateCalendar() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("EE dd", getApplicationContext().getResources().getConfiguration().locale);
        String month_name = month_date.format(calendar.getTime()).toUpperCase();
        tvMes.setText(month_name);
    }

    public void updateAdapter() {
        appsAdapter.updateList();
        this.appsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initialize();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_PRO_IMG:
                if (resultCode == RESULT_OK) {
                    try {
                        if (data != null) {
                            final Uri imageUri = data.getData();
                            WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                            Intent intent = new Intent(myWallpaperManager.getCropAndSetWallpaperIntent(imageUri));
                            startActivity(intent);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void initialize() {
        ListView lvApps = findViewById(R.id.lvApps);
        TextClock tcHour = findViewById(R.id.tcHour);
        tvMes = findViewById(R.id.tvMes);

        appsAdapter = new PackageListAdapter(this);
        lvApps.setAdapter(appsAdapter);
        tcHour.setOnLongClickListener(selectWallpaper);
    }

    private View.OnLongClickListener selectWallpaper = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(100);
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_PRO_IMG);
            return true;
        }
    };
}
