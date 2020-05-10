/*
 * Copyright (C) 2020 Nicolas Centa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

import java.util.LinkedList;
import java.util.List;

public class SuperRubySpan extends ReplacementSpan {
    private final @NonNull CharSequence mFurigana;

    public SuperRubySpan(final @NonNull CharSequence aFurigana) {
        mFurigana = aFurigana;
    }

    private <T> List<T> getSpans(final @NonNull Spanned text, int start, int end, Class<T> type) {
        final LinkedList<T> list = new LinkedList<>();

        for (T span : ((Spanned) text).getSpans(start, end, type)) {
            if (span != this) {
                list.add(span);
            }
        }

        return list;
    }

    private static class TextSizeInformation {
        final Paint.FontMetricsInt fontMetricsInt;
        final float size;
        final List<CharSequenceSizedElement> charSequenceSizedElements;

        TextSizeInformation(final Paint.FontMetricsInt aFontMetricsInt,
                            final float aSize,
                            final List<CharSequenceSizedElement> aCharSequenceSizedElements) {
            fontMetricsInt = aFontMetricsInt;
            size = aSize;
            charSequenceSizedElements = aCharSequenceSizedElements;
        }
    }

    private static class CharSequenceElement {
        final int start;
        final int end;
        final List<ReplacementSpan> replacementSpans;
        final List<MetricAffectingSpan> metricAffectingSpans;
        final List<CharacterStyle> characterStyles;

        private CharSequenceElement(final int aStart,
                                    final int aEnd,
                                    final List<ReplacementSpan> aReplacementSpans,
                                    final List<MetricAffectingSpan> aMetricAffectingSpans,
                                    final List<CharacterStyle> aCharacterStyles) {
            start = aStart;
            end = aEnd;
            replacementSpans = aReplacementSpans;
            metricAffectingSpans = aMetricAffectingSpans;
            characterStyles = aCharacterStyles;
        }
    }

    private static class CharSequenceSizedElement {
        final CharSequenceElement charSequenceElement;
        final float size;
        final TextPaint textPaint;
        final Paint.FontMetricsInt fontMetricsInt;

        private CharSequenceSizedElement(final CharSequenceElement aCharSequenceElement,
                                         final float aSize,
                                         final TextPaint aTextPaint,
                                         final Paint.FontMetricsInt aFontMetricsInt) {
            charSequenceElement = aCharSequenceElement;
            size = aSize;
            textPaint = aTextPaint;
            fontMetricsInt = aFontMetricsInt;
        }
    }

    private List<CharSequenceElement> getCharSequenceElements(final @NonNull CharSequence text, int start, int end) {
        final Spanned textSpanned = text instanceof Spanned ? (Spanned) text : null;
        final List<ReplacementSpan> replacementSpans = textSpanned != null ? getSpans(textSpanned, start, end, ReplacementSpan.class) : null;
        final List<MetricAffectingSpan> metricAffectingSpans = textSpanned != null ? getSpans(textSpanned, start, end, MetricAffectingSpan.class) : null;
        final List<CharacterStyle> characterStyles = textSpanned != null ? getSpans(textSpanned, start, end, CharacterStyle.class) : null;
        final String textString = text.toString();
        final LinkedList<CharSequenceElement> charSequenceElements = new LinkedList<>();

        int cursor = start;

        while (cursor < end) {
            int nextCursor = textString.offsetByCodePoints(cursor, 1);
            LinkedList<MetricAffectingSpan> metricAffectingSpansSub = null;
            LinkedList<CharacterStyle> characterStylesSub = null;

            if (textSpanned != null) {
                LinkedList<ReplacementSpan> replacementSpansSub = null;

                for (ReplacementSpan replacementSpan : replacementSpans) {
                    final int spanStart = textSpanned.getSpanStart(replacementSpan);
                    final int spanEnd = textSpanned.getSpanEnd(replacementSpan);

                    if (spanStart >= cursor) {
                        if (replacementSpansSub == null) {
                            replacementSpansSub = new LinkedList<>();
                        }

                        replacementSpansSub.add(replacementSpan);

                        if (spanEnd > nextCursor) {
                            nextCursor = spanEnd;
                        }
                    }
                }

                if (replacementSpansSub != null) {
                    charSequenceElements.add(new CharSequenceElement(cursor, nextCursor,
                            replacementSpansSub, null, null));

                    cursor = nextCursor;

                    continue;
                }

                for (MetricAffectingSpan metricAffectingSpan : metricAffectingSpans) {
                    final int spanStart = textSpanned.getSpanStart(metricAffectingSpan);
                    final int spanEnd = textSpanned.getSpanEnd(metricAffectingSpan);

                    if (spanStart <= cursor && spanEnd >= nextCursor) {
                        if (metricAffectingSpansSub == null) {
                            metricAffectingSpansSub = new LinkedList<>();
                        }

                        metricAffectingSpansSub.add(metricAffectingSpan);
                    }
                }

                for (CharacterStyle characterStyle : characterStyles) {
                    final int spanStart = textSpanned.getSpanStart(characterStyle);
                    final int spanEnd = textSpanned.getSpanEnd(characterStyle);

                    if (spanStart <= cursor && spanEnd >= nextCursor) {
                        if (characterStylesSub == null) {
                            characterStylesSub = new LinkedList<>();
                        }

                        characterStylesSub.add(characterStyle);
                    }
                }
            }

            charSequenceElements.add(new CharSequenceElement(cursor, nextCursor,
                    null, metricAffectingSpansSub, characterStylesSub));

            cursor = nextCursor;
        }

        return charSequenceElements;
    }

    private static void mergeFontMetricsInt(final @NonNull Paint.FontMetricsInt baseFontMetricsInt,
                                            final @NonNull Paint.FontMetricsInt newFontMetricsInt) {
        baseFontMetricsInt.leading = Math.max(baseFontMetricsInt.leading, newFontMetricsInt.leading);

        baseFontMetricsInt.descent = Math.max(baseFontMetricsInt.descent, newFontMetricsInt.descent);
        baseFontMetricsInt.bottom = Math.max(baseFontMetricsInt.bottom, newFontMetricsInt.bottom);

        baseFontMetricsInt.ascent = Math.min(baseFontMetricsInt.ascent, newFontMetricsInt.ascent);
        baseFontMetricsInt.top = Math.min(baseFontMetricsInt.top, newFontMetricsInt.top);
    }

    private TextSizeInformation getTextSize(final @NonNull Paint paint, final @NonNull CharSequence text, int start, int end) {
        final List<CharSequenceElement> charSequenceElements = getCharSequenceElements(text, start, end);
        final Paint.FontMetricsInt fm = new Paint.FontMetricsInt();
        final LinkedList<CharSequenceSizedElement> charSequenceSizedElements = new LinkedList<>();

        boolean a = paint.isUnderlineText();

        int size = 0;

        for (CharSequenceElement charSequenceElement : charSequenceElements) {
            if (charSequenceElement.replacementSpans != null) {
                final ReplacementSpan replacementSpan = charSequenceElement.replacementSpans.get(charSequenceElement.replacementSpans.size() - 1);
                final Paint.FontMetricsInt fontMetricsInt = new Paint.FontMetricsInt();
                final float elementSize = replacementSpan.getSize(paint, text, charSequenceElement.start, charSequenceElement.end, fontMetricsInt);

                charSequenceSizedElements.add(new CharSequenceSizedElement(charSequenceElement,
                        elementSize, new TextPaint(paint), fontMetricsInt));

                size += elementSize;
                mergeFontMetricsInt(fm, fontMetricsInt);
            } else {
                final TextPaint textPaint = new TextPaint(paint);

                if (charSequenceElement.metricAffectingSpans != null) {
                    for (MetricAffectingSpan metricAffectingSpan : charSequenceElement.metricAffectingSpans) {
                        metricAffectingSpan.updateMeasureState(textPaint);
                    }
                }

                if (charSequenceElement.characterStyles != null) {
                    for (CharacterStyle characterStyle : charSequenceElement.characterStyles) {
                        characterStyle.updateDrawState(textPaint);
                    }
                }

                final Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
                final float elementSize = textPaint.measureText(text, charSequenceElement.start, charSequenceElement.end);

                charSequenceSizedElements.add(new CharSequenceSizedElement(charSequenceElement,
                        elementSize, textPaint, fontMetricsInt));

                size += elementSize;
                mergeFontMetricsInt(fm, fontMetricsInt);
            }
        }

        return new TextSizeInformation(fm, size, charSequenceSizedElements);
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        final TextSizeInformation textSizeInformation = getTextSize(paint, text, start, end);
        final TextPaint inheritPaint = new TextPaint(paint);

        // For RelativeSizeSpan
        if (textSizeInformation.charSequenceSizedElements.size() > 0) {
            inheritPaint.setTextSize(textSizeInformation.charSequenceSizedElements.get(0).textPaint.getTextSize());
        }

        final TextSizeInformation furiganaSizeInformation = getTextSize(inheritPaint,
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
        boolean a = paint.isUnderlineText();

        final TextPaint inheritPaint = new TextPaint(paint);

        final TextSizeInformation textSizeInformation = getTextSize(paint, text, start, end);

        // For RelativeSizeSpan
        if (textSizeInformation.charSequenceSizedElements.size() > 0) {
            inheritPaint.setTextSize(textSizeInformation.charSequenceSizedElements.get(0).textPaint.getTextSize());
        }

        final TextSizeInformation furiganaSizeInformation = getTextSize(inheritPaint, mFurigana, 0, mFurigana.length());
        final float spanSize = Math.round(Math.max(textSizeInformation.size,
                furiganaSizeInformation.size));

        float cursor = x + (spanSize - textSizeInformation.size) / 2f;

        for (CharSequenceSizedElement charSequenceSizedElement : textSizeInformation.charSequenceSizedElements) {
            if(charSequenceSizedElement.textPaint.bgColor != 0) {
                int previousColor = charSequenceSizedElement.textPaint.getColor();
                Paint.Style previousStyle = charSequenceSizedElement.textPaint.getStyle();
                charSequenceSizedElement.textPaint.setColor(charSequenceSizedElement.textPaint.bgColor);
                charSequenceSizedElement.textPaint.setStyle(Paint.Style.FILL);
                canvas.drawRect(cursor,
                        y + charSequenceSizedElement.fontMetricsInt.top,
                        cursor + charSequenceSizedElement.size,
                        y + charSequenceSizedElement.fontMetricsInt.bottom, charSequenceSizedElement.textPaint);
                charSequenceSizedElement.textPaint.setStyle(previousStyle);
                charSequenceSizedElement.textPaint.setColor(previousColor);
            }

            if (charSequenceSizedElement.charSequenceElement.replacementSpans != null &&
                    charSequenceSizedElement.charSequenceElement.replacementSpans.size() > 0) {
                charSequenceSizedElement.charSequenceElement.replacementSpans.get(
                        charSequenceSizedElement.charSequenceElement.replacementSpans.size() - 1).draw(canvas, text, start, end,
                        cursor,
                        top - textSizeInformation.fontMetricsInt.ascent +
                                furiganaSizeInformation.fontMetricsInt.descent,
                        y,
                        bottom, charSequenceSizedElement.textPaint);
            } else {
                canvas.drawText(text, charSequenceSizedElement.charSequenceElement.start,
                        charSequenceSizedElement.charSequenceElement.end,
                        cursor, y, charSequenceSizedElement.textPaint);
            }

            cursor += charSequenceSizedElement.size;
        }

        float furiganaCursor = x + (spanSize - furiganaSizeInformation.size) / 2f;

        for (CharSequenceSizedElement charSequenceSizedElement : furiganaSizeInformation.charSequenceSizedElements) {
            if (charSequenceSizedElement.textPaint.bgColor != 0) {
                int previousColor = charSequenceSizedElement.textPaint.getColor();
                Paint.Style previousStyle = charSequenceSizedElement.textPaint.getStyle();
                charSequenceSizedElement.textPaint.setColor(charSequenceSizedElement.textPaint.bgColor);
                charSequenceSizedElement.textPaint.setStyle(Paint.Style.FILL);
                canvas.drawRect(furiganaCursor,
                        y + textSizeInformation.fontMetricsInt.ascent -
                                charSequenceSizedElement.fontMetricsInt.descent
                                + charSequenceSizedElement.fontMetricsInt.top,
                        furiganaCursor + charSequenceSizedElement.size,
                        y + textSizeInformation.fontMetricsInt.ascent -
                                charSequenceSizedElement.fontMetricsInt.descent
                                + charSequenceSizedElement.fontMetricsInt.bottom, charSequenceSizedElement.textPaint);
                charSequenceSizedElement.textPaint.setStyle(previousStyle);
                charSequenceSizedElement.textPaint.setColor(previousColor);
            }

            if (charSequenceSizedElement.charSequenceElement.replacementSpans != null &&
                    charSequenceSizedElement.charSequenceElement.replacementSpans.size() > 0) {
                boolean b = charSequenceSizedElement.textPaint.isUnderlineText();

                charSequenceSizedElement.charSequenceElement.replacementSpans.get(
                        charSequenceSizedElement.charSequenceElement.replacementSpans.size() - 1).draw(canvas,
                        mFurigana,
                        charSequenceSizedElement.charSequenceElement.start,
                        charSequenceSizedElement.charSequenceElement.end,
                        furiganaCursor,
                        top,
                        y + textSizeInformation.fontMetricsInt.ascent -
                                furiganaSizeInformation.fontMetricsInt.descent,
                        bottom + textSizeInformation.fontMetricsInt.ascent -
                                furiganaSizeInformation.fontMetricsInt.descent -
                                furiganaSizeInformation.fontMetricsInt.bottom,
                        charSequenceSizedElement.textPaint);
            } else {
                canvas.drawText(mFurigana, charSequenceSizedElement.charSequenceElement.start,
                        charSequenceSizedElement.charSequenceElement.end,
                        furiganaCursor, y + textSizeInformation.fontMetricsInt.ascent -
                                furiganaSizeInformation.fontMetricsInt.descent, charSequenceSizedElement.textPaint);
            }

            furiganaCursor += charSequenceSizedElement.size;
        }
    }
}
