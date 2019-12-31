package com.cegis.deltaplan2100;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class MessageActivity extends AppCompatActivity {
    private Handler mHandler = new Handler();
    private static int TIME_OUT = 500;//4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        this.setTitle("Prime Minister Message");

        TextView justifiedParagraph = findViewById(R.id.txtPmMessage);
        justifiedParagraph.setText(R.string.text_prime_minister);
        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/titillium_semi_bold.ttf");
        justifiedParagraph.setTypeface(tf);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MessageActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, TIME_OUT);
    }
}
