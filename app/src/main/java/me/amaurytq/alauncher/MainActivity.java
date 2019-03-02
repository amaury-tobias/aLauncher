package me.amaurytq.alauncher;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
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

    private ListView lvApps;
    private PackageListAdapter appsAdapter;
    private TextClock tcHour;
    private TextView tvMes;

    private static MainActivity instance;

    public static final int RESULT_PRO_IMG=1;

    public static MainActivity getInstance(){
        return instance;
    }

    @Override
    protected void onStart() {
        super.onStart();
        instance = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("EEEE dd MMMM", getApplicationContext().getResources().getConfiguration().locale);
        String month_name = month_date.format(calendar.getTime());
        tvMes.setText(month_name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initialize();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_PRO_IMG:
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();
                        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                        Intent intent = new Intent(myWallpaperManager.getCropAndSetWallpaperIntent(imageUri));
                        startActivity(intent);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public void updateAdapter() {
        appsAdapter.updateList();
        this.appsAdapter.notifyDataSetChanged();
    }

    private void initialize() {
        lvApps = findViewById(R.id.lvApps);
        tcHour = findViewById(R.id.tcHour);
        tvMes = findViewById(R.id.tvMes);

        appsAdapter = new PackageListAdapter(this);
        lvApps.setAdapter(appsAdapter);
        tcHour.setOnLongClickListener(selectWallpaper);
    }
}
