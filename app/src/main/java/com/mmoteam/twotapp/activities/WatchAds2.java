package com.mmoteam.twotapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.applovin.adview.AppLovinIncentivizedInterstitial;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdClickListener;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdRewardListener;
import com.applovin.sdk.AppLovinAdVideoPlaybackListener;
import com.hanks.htextview.HTextView;
import com.hanks.htextview.HTextViewType;
import com.mmoteam.twotapp.R;

import java.util.Map;

public class WatchAds2 extends AppCompatActivity {

    public Button loadads;
    public Button startads;
    public HTextView txxx2;
    private AppLovinIncentivizedInterstitial incentivizedInterstitial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchads2);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.watchads);

        loadads=(Button) findViewById(R.id.loadads);
        startads=(Button) findViewById(R.id.startads);
        txxx2=(HTextView) findViewById(R.id.txxx2);
        txxx2.setAnimateType(HTextViewType.LINE);
        txxx2.animateText("Hãy tải video lên !");
        incentivizedInterstitial = AppLovinIncentivizedInterstitial.create( getApplicationContext() );

        loadads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAds();
            }
        });

        startads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startads.setEnabled(false);
                AppLovinAdRewardListener adRewardListener = new AppLovinAdRewardListener() {
                    @Override
                    public void userRewardVerified(AppLovinAd appLovinAd,Map map) {
                        txxx2.animateText("Nhan tien");
                    }

                    @Override
                    public void userOverQuota(AppLovinAd appLovinAd,Map map) {

                    }

                    @Override
                    public void userRewardRejected(AppLovinAd appLovinAd,Map map) {

                    }

                    @Override
                    public void validationRequestFailed(AppLovinAd appLovinAd, int responseCode) {

                    }

                    @Override
                    public void userDeclinedToViewAd(AppLovinAd appLovinAd) {

                    }
                };

                AppLovinAdVideoPlaybackListener adVideoPlaybackListener =new AppLovinAdVideoPlaybackListener() {
                    @Override
                    public void videoPlaybackBegan(AppLovinAd ad) {

                    }

                    @Override
                    public void videoPlaybackEnded(AppLovinAd ad, double percentViewed, boolean fullyWatched) {

                    }
                };

                AppLovinAdDisplayListener adDisplayListener = new AppLovinAdDisplayListener() {
                    @Override
                    public void adDisplayed(AppLovinAd ad) {

                    }

                    @Override
                    public void adHidden(AppLovinAd ad) {

                    }
                };

                AppLovinAdClickListener adClickListener = new AppLovinAdClickListener() {
                    @Override
                    public void adClicked(AppLovinAd ad) {

                    }
                };
                incentivizedInterstitial.show(WatchAds2.this,adRewardListener,adVideoPlaybackListener,adDisplayListener,adClickListener);

            }
        });

    }

    void loadAds(){
        txxx2.animateText("Video đang tải lên....");
        startads.setEnabled(false);
        incentivizedInterstitial.preload(new AppLovinAdLoadListener()
        {
            @Override
            public void adReceived(AppLovinAd ad) {
                txxx2.animateText("Video đã tải lên !");
                startads.setEnabled(true);
            }

            @Override
            public void failedToReceiveAd(int errorCode) {
                txxx2.animateText("Video không tải được !");
                txxx2.animateText("Lỗi: "+errorCode);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
