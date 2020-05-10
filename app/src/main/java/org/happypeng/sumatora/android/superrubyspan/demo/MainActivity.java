package org.happypeng.sumatora.android.superrubyspan.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

import org.happypeng.sumatora.android.superrubyspan.SuperRubySpan;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView mainTextView = findViewById(R.id.main_text_view);

        SpannableStringBuilder textBuilder =
                new SpannableStringBuilder();
        SpannableStringBuilder furiganaBuilder =
                new SpannableStringBuilder();

        furiganaBuilder.append("かんじ");

        furiganaBuilder.setSpan(new RelativeSizeSpan(0.75f),
                0, furiganaBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        furiganaBuilder.setSpan(new ForegroundColorSpan(Color.RED),
                0, furiganaBuilder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        furiganaBuilder.setSpan(new BackgroundColorSpan(Color.YELLOW),
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

        mainTextView.setText(textBuilder);
    }
}
