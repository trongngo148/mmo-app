package com.mmoteam.twotapp.activities;


import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.hanks.htextview.HTextView;
import com.hanks.htextview.HTextViewType;
import com.mmoteam.twotapp.R;
import com.mmoteam.twotapp.app.App;
import com.mmoteam.twotapp.utils.AppUtils;
import com.mmoteam.twotapp.utils.CustomRequest;
import com.mmoteam.twotapp.utils.Dialogs;
import com.mmoteam.twotapp.views.CountDownTimerView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.mmoteam.twotapp.constants.Constants.ACCOUNT_BALANCE;
import static com.mmoteam.twotapp.constants.Constants.ACCOUNT_WATCHADS;
import static com.mmoteam.twotapp.constants.Constants.DEBUG_MODE;
import static com.mmoteam.twotapp.constants.Constants.ERROR_SUCCESS;




public class WatchAds extends AppCompatActivity {

    Button watchads;

    //    RewardedVideoAd mAd;
    RewardedAd mAd;

    WatchAds context;
    private Menu menu;

    HTextView txxx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchads);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.watchads);

        txxx = (HTextView) findViewById(R.id.txxx);
        // be sure to set custom typeface before setting the animate type, otherwise the font may not be updated.
        txxx.setAnimateType(HTextViewType.LINE);
        txxx.animateText("Video đang tải"); // animate


        watchads = (Button) findViewById(R.id.watchads);
        watchads.setEnabled(false);

        loadAd();
        context = this;

    }

    void loadAd() {
        txxx.animateText("Video đang tải");
        MobileAds.initialize(this,getString(R.string.admob_appId));
        mAd = new RewardedAd(this, getString(R.string.reward_ad_unit_id));
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                txxx.animateText("Video đã sẵn sàng!");
                watchads.setEnabled(true);
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
                txxx.animateText("Video không sẵn sàng");
            }
        };
        mAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);

        watchads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAd.isLoaded()) {
                    Activity activityContext = WatchAds.this;
                    RewardedAdCallback adCallback = new RewardedAdCallback() {
                        public void onRewardedAdOpened() {
                            // Ad opened.
                        }

                        public void onRewardedAdClosed() {
                            // Ad closed.
                            watchads.setEnabled(false);
                            mAd = createAndLoadRewardedAd();
                        }

                        @Override
                        public void onUserEarnedReward(@NonNull com.google.android.gms.ads.rewarded.RewardItem rewardItem) {
                            super.onUserEarnedReward(rewardItem);
                            WatchReward();
                        }

                        public void onUserEarnedReward(@NonNull RewardItem reward) {
                            // User earned reward.
                        }

                        public void onRewardedAdFailedToShow(int errorCode) {
                            txxx.animateText("Video không hiện được");
                        }
                    };
                    mAd.show(activityContext, adCallback);
                } else {
                    Log.d("TAG", "The rewarded ad wasn't loaded yet.");
                }
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

    public RewardedAd createAndLoadRewardedAd() {
        txxx.animateText("Video đang tải");
        RewardedAd mAd = new RewardedAd(this,getString(R.string.reward_ad_unit_id));
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                txxx.animateText("Video sẵn sàng!");
                watchads.setEnabled(true);
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
                txxx.animateText("Video không sẵn sàng");
            }
        };
        mAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
        return mAd;
    }


    void WatchReward() {


        CustomRequest WatchRequest = new CustomRequest(Request.Method.POST, ACCOUNT_WATCHADS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {

                            JSONObject Response = new JSONObject(App.getInstance().deData(response.toString()));

                            if (!Response.getBoolean("error") && Response.getInt("error_code") == ERROR_SUCCESS) {

                                // Reward received Succesfully
                                Dialogs.succesDialog(context, getResources().getString(R.string.congratulations), App.getInstance().get("WATCH_REWARD", "") + " " + getResources().getString(R.string.app_currency) + " " + getResources().getString(R.string.successfull_received), false, false, "", getResources().getString(R.string.ok), null);
                                updateBalanceInBg();

                            } else if (Response.getInt("error_code") == 410) {

                                // Reward Taken Today - Try Again Tomorrow
                                showTimerDialog(Response.getInt("error_description"));

                            } else if (Response.getInt("error_code") == 699 || Response.getInt("error_code") == 999) {

                                Dialogs.validationError(context, Response.getInt("error_code"));

                            } else if (DEBUG_MODE) {

                                // For Testing ONLY - intended for Developer Use ONLY not visible for Normal App user
                                Dialogs.errorDialog(context, Response.getString("error_code"), Response.getString("error_description"), false, false, "", getResources().getString(R.string.ok), null);

                            } else {

                                // Server error
                                Dialogs.serverError(context, getResources().getString(R.string.ok), null);

                            }

                        } catch (Exception e) {

                            if (!DEBUG_MODE) {
                                Dialogs.serverError(context, getResources().getString(R.string.ok), null);
                            } else {
                                Dialogs.errorDialog(context, "Got Error", e.toString() + ", please contact developer immediately", false, false, "", getResources().getString(R.string.ok), null);
                            }

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                if (!DEBUG_MODE) {
                    Dialogs.serverError(context, getResources().getString(R.string.ok), null);
                } else {
                    Dialogs.errorDialog(context, "Got Error", error.toString(), true, false, "", getResources().getString(R.string.ok), null);
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("data", App.getInstance().getData());
                return params;
            }
        };

        App.getInstance().addToRequestQueue(WatchRequest);

    }

    void updateBalanceInBg() {

        CustomRequest balanceRequest = new CustomRequest(Request.Method.POST, ACCOUNT_BALANCE, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (!response.getBoolean("error")) {

                                setOptionTitle(getString(R.string.app_currency).toUpperCase() + " : " + response.getString("user_balance"));
                                App.getInstance().store("balance", response.getString("user_balance"));

                            } else if (response.getInt("error_code") == 699 || response.getInt("error_code") == 999) {

                                Dialogs.validationError(context, response.getInt("error_code"));

                            } else if (response.getInt("error_code") == 799) {

                                Dialogs.warningDialog(context, getResources().getString(R.string.update_app), getResources().getString(R.string.update_app_description), false, false, "", getResources().getString(R.string.update), new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        AppUtils.gotoMarket(context);
                                    }
                                });

                            }

                        } catch (Exception e) {
                            // do nothin
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("data", App.getInstance().getData());
                return params;
            }
        };

        App.getInstance().addToRequestQueue(balanceRequest);
    }

    void showTimerDialog(int TimeLeft) {

        CountDownTimerView timerView = new CountDownTimerView(context);
        timerView.setTextSize(getResources().getInteger(R.integer.daily_checkin_timer_size));
        timerView.setPadding(0, 0, 0, 25);
        timerView.setGravity(Gravity.CENTER);
        timerView.setTime(TimeLeft * 1000);
        timerView.startCountDown();
        Dialogs.customDialog(context, timerView, getResources().getString(R.string.daily_reward_taken), false, false, "", getResources().getString(R.string.ok), null);

    }

    private void setOptionTitle(String title) {
        MenuItem item = menu.findItem(R.id.points);
        item.setTitle(title);
    }
}

////        mLog.append(String.format(Locale.getDefault(),
////                "You received %d %s!\n",rewardItem.getAmount(),rewardItem.getType()));
