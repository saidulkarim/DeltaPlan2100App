package com.cegis.deltaplan2100;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.cegis.deltaplan2100.utility.JustifiedTextView;

public class MessageActivity extends AppCompatActivity {
    private Handler mHandler = new Handler();
    private static int TIME_OUT = 2000;//4000;

    Animation atg;
    ImageView imageViewCircular;
    JustifiedTextView txtPmMessage;
    TextView txtPM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        this.setTitle("Prime Minister Message");

        atg = AnimationUtils.loadAnimation(this, R.anim.atg);
        imageViewCircular = findViewById(R.id.imageViewCircular);
        txtPmMessage = findViewById(R.id.txtPmMessage);
        txtPM = findViewById(R.id.txtPM);

        TextView justifiedParagraph = findViewById(R.id.txtPmMessage);
        justifiedParagraph.setText(R.string.text_prime_minister);
        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/titillium_semi_bold.ttf");
        justifiedParagraph.setTypeface(tf);

        imageViewCircular.setAnimation(atg);
        txtPmMessage.setAnimation(atg);
        txtPM.setAnimation(atg);

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
