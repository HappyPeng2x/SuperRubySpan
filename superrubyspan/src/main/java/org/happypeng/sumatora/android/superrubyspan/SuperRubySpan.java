package org.happypeng.sumatora.android.superrubyspan;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.MetricAffectingSpan;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SuperRubySpan extends ReplacementSpan {
    private final @NonNull CharSequence mFurigana;

    public SuperRubySpan(final @NonNull CharSequence aFurigana) {
        mFurigana = aFurigana;
    }

    private static void applySpansToPaint(@NonNull Spanned aText, int aStart, int aEnd,
                                          @NonNull TextPaint aPaint) {
        MetricAffectingSpan[] metricAffectingSpans =
                aText.getSpans(aStart, aEnd,
                        MetricAffectingSpan.class);

        for (MetricAffectingSpan span : metricAffectingSpans) {
            if (!(span instanceof SuperRubySpan)) {
                span.updateMeasureState(aPaint);
            }
        }

        CharacterStyle[] characterStyles =
                aText.getSpans(aStart, aEnd,
                        CharacterStyle.class);

        for (CharacterStyle style : characterStyles) {
            style.updateDrawState(aPaint);
        }
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        final TextPaint furiganaPaint = new TextPaint(paint);
        final TextPaint textPaint = new TextPaint(paint);

        if (text instanceof Spanned) {
            applySpansToPaint((Spanned) text, start, end,
                    textPaint);
        }

        if (mFurigana instanceof Spanned) {
            applySpansToPaint((Spanned) mFurigana, 0, mFurigana.length(),
                    furiganaPaint);
        }

        final Paint.FontMetricsInt furiganaFm = furiganaPaint.getFontMetricsInt();

        if (fm != null) {
            final Paint.FontMetricsInt textFm = paint.getFontMetricsInt();

            fm.bottom = textFm.bottom;
            fm.ascent = textFm.ascent + (furiganaFm.ascent - furiganaFm.descent);
            fm.top = textFm.ascent + (furiganaFm.top - furiganaFm.descent);
            fm.descent = textFm.descent;
            fm.leading = textFm.leading;
        }

        final float textSize = textPaint.measureText(text, start, end);
        final float furiganaSize = furiganaPaint.measureText(mFurigana, 0, mFurigana.length());

        return Math.round(Math.max(textSize, furiganaSize));
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        final TextPaint furiganaPaint = new TextPaint(paint);
        final TextPaint textPaint = new TextPaint(paint);

        if (text instanceof Spanned) {
            applySpansToPaint((Spanned) text, start, end,
                    textPaint);
        }

        if (mFurigana instanceof Spanned) {
            applySpansToPaint((Spanned) mFurigana, 0, mFurigana.length(),
                    furiganaPaint);
        }

        final Paint.FontMetricsInt textFm = textPaint.getFontMetricsInt();
        final Paint.FontMetricsInt furiganaFm = furiganaPaint.getFontMetricsInt();;


        final float furiganaTextSize = furiganaPaint.measureText(mFurigana, 0, mFurigana.length());
        final float textSize = textPaint.measureText(text, start, end);

        final float spanSize = Math.max(furiganaTextSize, textSize);

        if (textPaint.bgColor != 0) {
            int previousColor = textPaint.getColor();
            Paint.Style previousStyle = textPaint.getStyle();
            textPaint.setColor(textPaint.bgColor);
            textPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(x + (spanSize - textSize)/2f,
                    top - (furiganaFm.top - furiganaFm.descent),
                    x + (spanSize - textSize)/2f + textSize,
                    bottom, textPaint);
            textPaint.setStyle(previousStyle);
            textPaint.setColor(previousColor);
        }

        if (furiganaPaint.bgColor != 0) {
            int previousColor = furiganaPaint.getColor();
            Paint.Style previousStyle = furiganaPaint.getStyle();
            furiganaPaint.setColor(furiganaPaint.bgColor);
            furiganaPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(x + (spanSize - furiganaTextSize)/2f,
                    top,
                    x + (spanSize - furiganaTextSize)/2f + furiganaTextSize,
                    top - (furiganaFm.top - furiganaFm.descent), furiganaPaint);
            furiganaPaint.setStyle(previousStyle);
            furiganaPaint.setColor(previousColor);
        }

        canvas.drawText(text, start, end,
                x + (spanSize - textSize) / 2f, y, textPaint);

        canvas.drawText(mFurigana, 0, mFurigana.length(),
                x + (spanSize - furiganaTextSize) / 2f,
                y + textFm.ascent - furiganaFm.descent,
                furiganaPaint);
    }
}
