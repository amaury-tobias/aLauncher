package me.amaurytq.alauncher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextClock;

public class MainActivity extends AppCompatActivity {

    static private PackageListAdapter appsAdapter;
    private TextClock tcMonth;
    private SharedPreferences sharedPreferences;

    @Override
    public void onBackPressed() {}

    @Override
    protected void onResume() {
        super.onResume();
        ThemeManager.setColorsFromBackground(getApplicationContext());
        updateColors();
    }

    public void updateColors() {
        int color = sharedPreferences.getInt("color", R.color.colorTextLauncher);
        tcMonth.setTextColor(color);
    }

    public static void updateAdapter() {
        appsAdapter.updateList();
        appsAdapter.notifyDataSetChanged();
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
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Action.SETTINGS_UPDATED:
                    // Toast.makeText(this,"settingsUpdated", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void initialize() {
        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        ListView lvApps = findViewById(R.id.lvApps);
        TextClock tcHour = findViewById(R.id.tcHour);
        tcMonth = findViewById(R.id.tcMonth);

        appsAdapter = new PackageListAdapter(this);
        lvApps.setAdapter(appsAdapter);

        tcHour.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(100);
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent, Action.SETTINGS_UPDATED);
                return false;
            }
        });
    }


}
