package me.amaurytq.alauncher;

import android.app.WallpaperManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    public static final int RESULT_PRO_IMG = 1;

    TextView tvWallpaper;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(getApplicationContext(), "BACK Pressed", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);

        tvWallpaper = findViewById(R.id.tvWallpaper);
        tvWallpaper.setOnClickListener(selectWallpaper);
    }

    private View.OnClickListener selectWallpaper = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_PRO_IMG);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PRO_IMG:
                    try {
                        if (data != null) {
                            final Uri imageUri = data.getData();
                            WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                            Intent intent = new Intent(myWallpaperManager.getCropAndSetWallpaperIntent(imageUri));
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}
