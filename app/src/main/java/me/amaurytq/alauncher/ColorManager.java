package me.amaurytq.alauncher;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;

import static android.content.Context.MODE_PRIVATE;

class ColorManager {
    
    static void setColorsFromBackground(final Context context, Bitmap bitmap) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@Nullable Palette palette) {
                if (palette != null) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("prefs", MODE_PRIVATE);
                    Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                    if(vibrantSwatch != null) {
                        int color = vibrantSwatch.getRgb();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("color", color);
                        editor.apply();
                    }
                }
            }
        });
    }

}
