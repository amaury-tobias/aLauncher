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

public class MainActivity extends AppCompatActivity {

    private ListView lvApps;
    private RAdapter appsAdapter;
    private TextClock tcHora;

    public static final int RESULT_PRO_IMG=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // FUENTE DE ICONO YANTRA ONE
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        lvApps = findViewById(R.id.lvApps);
        tcHora = findViewById(R.id.tcHora);

        appsAdapter = new RAdapter(this);
        lvApps.setAdapter(appsAdapter);

        tcHora.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(100);
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_PRO_IMG);
                return true;
            }
        });
    }

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
}
