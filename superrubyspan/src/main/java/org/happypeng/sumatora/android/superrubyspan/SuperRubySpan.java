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

    private void applySpansToPaint(@NonNull Spanned aText, int aStart, int aEnd,
                                   @NonNull TextPaint aPaint) {
        MetricAffectingSpan[] metricAffectingSpans =
                aText.getSpans(aStart, aEnd,
                        MetricAffectingSpan.class);

        for (MetricAffectingSpan span : metricAffectingSpans) {
            if (span != this) {
                span.updateMeasureState(aPaint);
            }
        }

        CharacterStyle[] characterStyles =
                aText.getSpans(aStart, aEnd,
                        CharacterStyle.class);

        for (CharacterStyle style : characterStyles) {
            if (style != this) {
                style.updateDrawState(aPaint);
            }
        }
    }

    private ReplacementSpan getReplacementSpan(@NonNull CharSequence text, int start, int end) {
        ReplacementSpan replacementSpan = null;

        if (!(text instanceof Spanned)) {
            return null;
        }

        for (ReplacementSpan span : ((Spanned) text).getSpans(start, end, ReplacementSpan.class))  {
            if (span != this) {
                replacementSpan = span;
            }
        }

        return replacementSpan;
    }

    private static class TextSizeInformation {
        final TextPaint paint;
        final Paint.FontMetricsInt fontMetricsInt;
        final float size;
        final ReplacementSpan replacementSpan;

        TextSizeInformation(final TextPaint aPaint,
                            final Paint.FontMetricsInt aFontMetricsInt,
                            final float aSize,
                            final ReplacementSpan aReplacementSpan) {
            paint = aPaint;
            fontMetricsInt = aFontMetricsInt;
            size = aSize;
            replacementSpan = aReplacementSpan;
        }
    }

    private TextSizeInformation getTextSize(@NonNull Paint paint, CharSequence text, int start, int end) {
        final ReplacementSpan replacementSpan = getReplacementSpan(text, start, end);

        if (replacementSpan != null) {
            final Paint.FontMetricsInt fontMetricsInt = new Paint.FontMetricsInt();
            final float size = replacementSpan.getSize(paint, text, start, end, fontMetricsInt);

            return new TextSizeInformation(null, fontMetricsInt, size, replacementSpan);
        }

        final TextPaint textPaint = new TextPaint(paint);

        if (text instanceof Spanned) {
            applySpansToPaint((Spanned) text, start, end,
                    textPaint);
        }

        final Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
        final float size = textPaint.measureText(text, start, end);

        return new TextSizeInformation(textPaint, fontMetricsInt, size, null);
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        final TextSizeInformation textSizeInformation = getTextSize(paint, text, start, end);
        final TextSizeInformation furiganaSizeInformation = getTextSize(textSizeInformation.paint == null ? paint : textSizeInformation.paint,
                mFurigana, 0, mFurigana.length());

        if (fm != null) {
            fm.bottom = textSizeInformation.fontMetricsInt.bottom;
            fm.ascent = textSizeInformation.fontMetricsInt.ascent +
                    (furiganaSizeInformation.fontMetricsInt.ascent - furiganaSizeInformation.fontMetricsInt.descent);
            fm.top = textSizeInformation.fontMetricsInt.ascent +
                    (furiganaSizeInformation.fontMetricsInt.top - furiganaSizeInformation.fontMetricsInt.descent);
            fm.descent = textSizeInformation.fontMetricsInt.descent;
            fm.leading = textSizeInformation.fontMetricsInt.leading;
        }

        return Math.round(Math.max(textSizeInformation.size,
                furiganaSizeInformation.size));
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        final TextSizeInformation textSizeInformation = getTextSize(paint, text, start, end);
        final TextSizeInformation furiganaSizeInformation = getTextSize(textSizeInformation.paint == null ? paint : textSizeInformation.paint, mFurigana, 0, mFurigana.length());
        final float spanSize = Math.round(Math.max(textSizeInformation.size,
                furiganaSizeInformation.size));

        if (textSizeInformation.paint != null) {
            if(textSizeInformation.paint.bgColor != 0) {
                int previousColor = textSizeInformation.paint.getColor();
                Paint.Style previousStyle = textSizeInformation.paint.getStyle();
                textSizeInformation.paint.setColor(textSizeInformation.paint.bgColor);
                textSizeInformation.paint.setStyle(Paint.Style.FILL);
                canvas.drawRect(x + (spanSize - textSizeInformation.size) / 2f,
                        y + textSizeInformation.fontMetricsInt.top,
                        x + (spanSize - textSizeInformation.size) / 2f + textSizeInformation.size,
                        y + textSizeInformation.fontMetricsInt.bottom, textSizeInformation.paint);
                textSizeInformation.paint.setStyle(previousStyle);
                textSizeInformation.paint.setColor(previousColor);
            }

            canvas.drawText(text, start, end,
                    x + (spanSize - textSizeInformation.size) / 2f, y, textSizeInformation.paint);
        } else if (textSizeInformation.replacementSpan != null) {
            textSizeInformation.replacementSpan.draw(canvas, text, start, end,
                    x + (spanSize - textSizeInformation.size) / 2f,
                    top - textSizeInformation.fontMetricsInt.ascent +
                            furiganaSizeInformation.fontMetricsInt.descent,
                    y,
                    bottom, paint);
        }

        if (furiganaSizeInformation.paint != null) {
            if (furiganaSizeInformation.paint.bgColor != 0) {
                int previousColor = furiganaSizeInformation.paint.getColor();
                Paint.Style previousStyle = furiganaSizeInformation.paint.getStyle();
                furiganaSizeInformation.paint.setColor(furiganaSizeInformation.paint.bgColor);
                furiganaSizeInformation.paint.setStyle(Paint.Style.FILL);
                canvas.drawRect(x + (spanSize - furiganaSizeInformation.size) / 2f,
                        y + textSizeInformation.fontMetricsInt.ascent -
                                furiganaSizeInformation.fontMetricsInt.descent
                                + furiganaSizeInformation.fontMetricsInt.top,
                        x + (spanSize - furiganaSizeInformation.size) / 2f + furiganaSizeInformation.size,
                        y + textSizeInformation.fontMetricsInt.ascent -
                                furiganaSizeInformation.fontMetricsInt.descent
                                + furiganaSizeInformation.fontMetricsInt.bottom, furiganaSizeInformation.paint);
                furiganaSizeInformation.paint.setStyle(previousStyle);
                furiganaSizeInformation.paint.setColor(previousColor);
            }

            canvas.drawText(mFurigana, 0, mFurigana.length(),
                    x + (spanSize - furiganaSizeInformation.size) / 2f,
                    y + textSizeInformation.fontMetricsInt.ascent -
                            furiganaSizeInformation.fontMetricsInt.descent,
                    furiganaSizeInformation.paint);
        } else if (furiganaSizeInformation.replacementSpan != null) {
            furiganaSizeInformation.replacementSpan.draw(canvas, mFurigana, 0, mFurigana.length(),
                    x + (spanSize - furiganaSizeInformation.size) / 2f,
                    top,
                    y + textSizeInformation.fontMetricsInt.ascent -
                            furiganaSizeInformation.fontMetricsInt.descent,
                    bottom + textSizeInformation.fontMetricsInt.ascent -
                            furiganaSizeInformation.fontMetricsInt.descent -
                        furiganaSizeInformation.fontMetricsInt.bottom,
                        paint);
        }
    }
}
