package com.rpmsousa.abc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;


public class Lettergrid extends Drawable {
    private final Paint mPaint;
    private final Typeface[] mTypeface;
    private final char[] mCharacters;
    private static final float LETTER_DENSITY = 0.8f;
    private static final float LETTER_SIZE = 300.0f;
    private Letter[] mLettergrid;
    private boolean mfull = true;
    private boolean mplaying = false;
    private int mIndex;
    private float mLwidth, mLheight;
    private static Letterspeech mLetterspeech;

    static private class Letter {
        public int typeface;
        public int index;
        public float x,y,angle;
        public float dx, dy;
        public float size;
        public float x1, y1;
        public float dx1, dy1;
        public float size1;
        public int r,g,b;
    }

    Lettergrid(Context context, Letterspeech speech) {
        mLetterspeech = speech;

        mPaint = new Paint();

        mPaint.setAntiAlias(true);
        mPaint.setSubpixelText(true);
        mPaint.setTextAlign(Paint.Align.LEFT);

        mCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();

        mTypeface = new Typeface[5];
        mTypeface[0] = Typeface.create("cursive", Typeface.NORMAL);
        mTypeface[1] = Typeface.create("sans-serif", Typeface.NORMAL);
        mTypeface[2] = Typeface.create("serif", Typeface.NORMAL);
        mTypeface[3] = Typeface.create("monospace", Typeface.NORMAL);
        mTypeface[4] = Typeface.create("casual", Typeface.NORMAL);
    }

    public boolean set(float x, float y) {
        float xmin, xmax;
        float ymin, ymax;

        if (mplaying)
            return false;

        callback c = status -> {
            mplaying = false;
            System.out.println("Done talking");
        };

        if (mfull) {
            for (int i = 0; i < mLettergrid.length; i++) {
                xmin = mLettergrid[i].x - mLwidth / 2;
                xmax = xmin + mLwidth;
                ymin = mLettergrid[i].y - mLheight / 2;
                ymax = ymin + mLheight;

                if (x >= xmin && x <= xmax
                && y >= ymin && y <= ymax) {
                    mIndex = i;
                    mfull = false;

                    mplaying = true;
                    mLetterspeech.speak(String.valueOf(mCharacters[mLettergrid[i].index]), c);

                    return true;
                }
            }
        } else {
            mfull = true;
            return true;
        }

        return false;
    }

    private void draw_letter(Canvas canvas) {
        Path path = new Path();

        mPaint.setTextSize(mLettergrid[mIndex].size1);

        mPaint.setTypeface(mTypeface[mLettergrid[mIndex].typeface]);
        mPaint.getTextPath(mCharacters, mLettergrid[mIndex].index, 1, 0, 0, path);

     //   mPaint.setARGB(255, 255, 255, 255);
     //   canvas.drawLine(mLettergrid[mIndex].x1, mLettergrid[mIndex].y1, mLettergrid[mIndex].x1 + mLettergrid[mIndex].dx1, mLettergrid[mIndex].y1, mPaint);
     //   canvas.drawLine(mLettergrid[mIndex].x1, mLettergrid[mIndex].y1, mLettergrid[mIndex].x1, mLettergrid[mIndex].y1 - mLettergrid[mIndex].dy1, mPaint);

        mPaint.setARGB(255, mLettergrid[mIndex].r, mLettergrid[mIndex].g, mLettergrid[mIndex].b);

        canvas.save();
        canvas.translate(mLettergrid[mIndex].x1, mLettergrid[mIndex].y1);
        canvas.drawPath(path, mPaint);
        canvas.restore();
    }

    private void draw_grid(Canvas canvas) {
        Path path = new Path();

        for (Letter letter : mLettergrid) {
            mPaint.setTextSize(letter.size);
            mPaint.setTypeface(mTypeface[letter.typeface]);
            mPaint.setARGB(255, letter.r, letter.g, letter.b);
            mPaint.getTextPath(mCharacters, letter.index, 1, 0, 0, path);
            canvas.save();
            canvas.translate(letter.x, letter.y);
            canvas.rotate(letter.angle);
            canvas.translate(-letter.dx, -letter.dy);
            canvas.drawPath(path, mPaint);
            canvas.restore();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (mfull)
            draw_grid(canvas);
        else
            draw_letter(canvas);
    }

    private void create(Rect wbounds, float density, float size) {

        int n_width = (int) (wbounds.width() / size);
        int n_height = (int) (wbounds.height() / size);
        int total = (int)((n_width * n_height) * density);
        boolean[][] used;
        Rect bounds = new Rect();
        float l_size;

        used = new boolean[n_width][n_height];
        mLettergrid = new Letter[total];

        mLwidth = wbounds.width() / n_width;
        mLheight = wbounds.height() / n_height;

        l_size = Math.min(wbounds.width(), wbounds.height());

        for (int i = 0, k, l; i < mLettergrid.length; i++) {

            do {
                k = (int)(Math.random() * n_width);
                l = (int)(Math.random() * n_height);
            } while (used[k][l]);

            used[k][l] = true;

            mLettergrid[i] = new Letter();

            mLettergrid[i].typeface = (int)(Math.random() * mTypeface.length);
            mLettergrid[i].index = (int)(Math.random() * mCharacters.length);
            mLettergrid[i].x = k * mLwidth + mLwidth / 2;
            mLettergrid[i].y = l * mLheight + mLheight / 2;

            mPaint.setTextSize(size);
            mPaint.setTypeface(mTypeface[mLettergrid[i].typeface]);

            mPaint.getTextBounds(mCharacters, mLettergrid[i].index, 1, bounds);
            mLettergrid[i].dx = bounds.centerX();
            mLettergrid[i].dy = bounds.centerY();
            mLettergrid[i].size = size;

            mLettergrid[i].angle = (float)(Math.random() * 120.0 - 60.0);
            mLettergrid[i].r = (int)(Math.random() * 255);
            mLettergrid[i].g = (int)(Math.random() * 255);
            mLettergrid[i].b = (int)(Math.random() * 255);

            mPaint.setTextSize(l_size);
            mPaint.getTextBounds(mCharacters, mLettergrid[i].index, 1, bounds);
            mLettergrid[i].x1 = wbounds.centerX() - bounds.centerX();
            mLettergrid[i].y1 = wbounds.centerY() - bounds.centerY();
            mLettergrid[i].dx1 = bounds.width();
            mLettergrid[i].dy1 = bounds.height();
            mLettergrid[i].size1 = l_size;
        }
    }

    @Override
    public void onBoundsChange (Rect bounds) {

        create(bounds, LETTER_DENSITY, LETTER_SIZE);
    }

    @Override
    public void setAlpha(int alpha) {
        // This method is required
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        // This method is required
    }

    @Override
    public int getOpacity() {
        // Must be PixelFormat.UNKNOWN, TRANSLUCENT, TRANSPARENT, or OPAQUE
        return PixelFormat.OPAQUE;
    }

}
