package org.happypeng.sumatora.android.superrubyspan.demo;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.happypeng.sumatora.android.superrubyspan.SuperRubySpan;

public class MainActivity extends AppCompatActivity {
    private CharSequence test1() {
        SpannableStringBuilder textBuilder =
                new SpannableStringBuilder();
        SpannableStringBuilder furiganaBuilder =
                new SpannableStringBuilder();
        SpannableStringBuilder furiganaFuriganaBuilder =
                new SpannableStringBuilder();

        furiganaBuilder.append("漢字2");

        furiganaBuilder.setSpan(new RelativeSizeSpan(0.75f),
                0, furiganaBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        furiganaBuilder.setSpan(new ForegroundColorSpan(Color.RED),
                0, furiganaBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        furiganaBuilder.setSpan(new BackgroundColorSpan(Color.YELLOW),
                0, furiganaBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        furiganaFuriganaBuilder.append("かんじ");

        furiganaFuriganaBuilder.setSpan(new RelativeSizeSpan(0.75f),
                0, furiganaBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        furiganaFuriganaBuilder.setSpan(new ForegroundColorSpan(Color.CYAN),
                0, furiganaBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        furiganaFuriganaBuilder.setSpan(new BackgroundColorSpan(Color.MAGENTA),
                0, furiganaBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        furiganaFuriganaBuilder.setSpan(new StrikethroughSpan(),
                0, furiganaBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        furiganaBuilder.setSpan(new SuperRubySpan(furiganaFuriganaBuilder),
                0, furiganaBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textBuilder.append("漢字");

        textBuilder.setSpan(new SuperRubySpan(furiganaBuilder),
                0, textBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textBuilder.setSpan(new ForegroundColorSpan(Color.BLUE),
                0, textBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textBuilder.setSpan(new BackgroundColorSpan(Color.GREEN),
                0, textBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textBuilder.setSpan(new UnderlineSpan(),
                0, textBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return textBuilder;
    }

    private CharSequence test2() {
        SpannableStringBuilder spannableStringBuilder =
                new SpannableStringBuilder();
        SpannableStringBuilder furiganaBuilder =
                new SpannableStringBuilder();
        SpannableStringBuilder furiganaFuriganaBuilder =
                new SpannableStringBuilder();

        spannableStringBuilder.append("test");

        final Drawable myIcon = getResources().getDrawable(R.drawable.ic_android_black_24dp);
        myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());

        spannableStringBuilder.setSpan(new ImageSpan(myIcon, ImageSpan.ALIGN_BASELINE),
                0, spannableStringBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        furiganaBuilder.append("アンドロイド");

        furiganaBuilder.setSpan(new RelativeSizeSpan(0.75f),
                0, furiganaBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        furiganaBuilder.setSpan(new ForegroundColorSpan(Color.RED),
                0, furiganaBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        furiganaBuilder.setSpan(new BackgroundColorSpan(Color.YELLOW),
                0, furiganaBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        furiganaBuilder.setSpan(new RelativeSizeSpan(0.75f),
                0, furiganaBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        furiganaFuriganaBuilder.append("Android");

        furiganaBuilder.setSpan(new SuperRubySpan(furiganaFuriganaBuilder),
                0, furiganaBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableStringBuilder.setSpan(new SuperRubySpan(furiganaBuilder),
                0, spannableStringBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableStringBuilder;
    }

    private CharSequence test3() {
        SpannableStringBuilder spannableStringBuilder =
                new SpannableStringBuilder();
        SpannableStringBuilder furiganaBuilder =
                new SpannableStringBuilder();
        SpannableStringBuilder furiganaFuriganaBuilder =
                new SpannableStringBuilder();

        spannableStringBuilder.append("アンドロイド");

        furiganaBuilder.append("test");

        final Drawable myIcon = getResources().getDrawable(R.drawable.ic_android_black_24dp);
        myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());

        furiganaBuilder.setSpan(new ImageSpan(myIcon, ImageSpan.ALIGN_BASELINE),
                0, furiganaBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        furiganaFuriganaBuilder.append("Android");

        furiganaFuriganaBuilder.setSpan(new RelativeSizeSpan(0.75f),
                0, furiganaFuriganaBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        furiganaBuilder.setSpan(new SuperRubySpan(furiganaFuriganaBuilder),
                0, furiganaBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableStringBuilder.setSpan(new SuperRubySpan(furiganaBuilder),
                0, spannableStringBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableStringBuilder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView mainTextView = findViewById(R.id.main_text_view);

        SpannableStringBuilder spannableStringBuilder =
                new SpannableStringBuilder();

        spannableStringBuilder.append(test1());
        spannableStringBuilder.append(" ");
        spannableStringBuilder.append(test2());
        spannableStringBuilder.append(" ");
        spannableStringBuilder.append(test3());

        mainTextView.setText(spannableStringBuilder);
    }
}
