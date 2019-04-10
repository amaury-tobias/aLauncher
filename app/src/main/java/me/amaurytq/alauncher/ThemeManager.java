package me.amaurytq.alauncher;


import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.palette.graphics.Palette;

import static android.content.Context.MODE_PRIVATE;

class ThemeManager {
    
    static void setColorsFromBackground(final Context context) {
        WallpaperManager wmInstance = WallpaperManager.getInstance(context);

        Drawable wallpaperDrawable = wmInstance.getDrawable();
        Bitmap bitmap = ((BitmapDrawable)wallpaperDrawable).getBitmap();

        SharedPreferences sharedPreferences = context.getSharedPreferences("prefs", MODE_PRIVATE);
        Palette p = Palette.from(bitmap).generate();
        int color;
        Palette.Swatch swatch = p.getVibrantSwatch();

        if (swatch == null) color = Color.WHITE;
        else color = swatch.getRgb();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("color", color);
        editor.apply();
    }

}
