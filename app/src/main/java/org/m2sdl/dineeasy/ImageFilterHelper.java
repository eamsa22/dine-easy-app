package org.m2sdl.dineeasy;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public class ImageFilterHelper {

    public static Bitmap applyGrayscale(Bitmap bitmap) {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        return applyFilter(bitmap, filter);
    }

    public static Bitmap applySepia(Bitmap bitmap) {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        ColorMatrix sepiaMatrix = new ColorMatrix();
        sepiaMatrix.setScale(1f, 0.95f, 0.82f, 1.0f);
        colorMatrix.postConcat(sepiaMatrix);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        return applyFilter(bitmap, filter);
    }

    private static Bitmap applyFilter(Bitmap bitmap, ColorMatrixColorFilter filter) {
        Bitmap filteredBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(filteredBitmap);
        Paint paint = new Paint();
        paint.setColorFilter(filter);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return filteredBitmap;
    }
}

