package com.mmoteam.twotapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.mmoteam.twotapp.R;

public class InPutPassActivity extends AppCompatActivity {

    PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    TextView thongbao;

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            if(password.equals(pin)) {
                Intent intent = new Intent(getApplicationContext(), AppActivity.class);
                startActivity(intent);
                finish();
            }
        }

        @Override
        public void onEmpty() {
            thongbao.setText("Sai Mật Khẩu");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {

        }
    };

    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_in_put_pass);
        thongbao =(TextView) findViewById(R.id.thongbao);
        SharedPreferences preferences= getSharedPreferences("PREFS",0);
        password=preferences.getString("password","0");

        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(mPinLockListener);
        mPinLockView.setPinLength(4);
        mPinLockView.setTextColor(ContextCompat.getColor(this, R.color.white));

        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);
    }
}
