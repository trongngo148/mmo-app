package com.mmoteam.twotapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mmoteam.twotapp.R;
import com.mmoteam.twotapp.app.App;
import com.mmoteam.twotapp.utils.AppUtils;
import com.mmoteam.twotapp.utils.CustomRequest;
import com.mmoteam.twotapp.utils.Dialogs;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.mmoteam.twotapp.constants.Constants.APP_BAUCUA;
import static com.mmoteam.twotapp.constants.Constants.APP_QUAYSO;
import static com.mmoteam.twotapp.constants.Constants.ERROR_SUCCESS;

public class QuaySo extends AppCompatActivity {


//    private InterstitialAd mInterstitialAd;


    ImageView imageView_wheel;
    ImageButton imageButton_spin;
    int degree = 0;
    int degree_old = 0;
    Random r;
    int score=0;
    public static final float FACTOR = 15f;

    TextView textView;
    String user_id;

    //    DatabaseReference  user_id_child;
    int intValue;
    String current_score;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quayso);
        imageView_wheel = (ImageView) findViewById(R.id.wheel);
        imageButton_spin = (ImageButton) findViewById(R.id.button);


        getSupportActionBar().hide();
//        MobileAds.initialize(getApplicationContext(), "ca-app-pub-2157316892113364~5431497100");
        current_score  = currentNumber(360 - (degree % 360));


        r = new Random();
//        DatabaseReference databaseReference;
//        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
//
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        user_id = mAuth.getCurrentUser().getUid();
//
//        user_id_child = databaseReference.child(user_id);

       /* FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mroot = firebaseDatabase.getReference();
        DatabaseReference user_ref = mroot.child("users");*/






//        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId("ca-app-pub-2157316892113364/7786197702");
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());










        imageButton_spin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                degree_old = degree % 360;
                degree = r.nextInt(3600) + 720;

                RotateAnimation rotateAnimation = new RotateAnimation(degree_old, degree,
                        RotateAnimation.RELATIVE_TO_SELF, .5f,
                        RotateAnimation.RELATIVE_TO_SELF, .5f);


                rotateAnimation.setDuration(3600);
                rotateAnimation.setFillAfter(true);
                rotateAnimation.setInterpolator(new DecelerateInterpolator());


                rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {


                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        String temp=currentNumber(360 - (degree % 360));




                        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();

                        int n =  intValue+score;
                        editor.putInt("your_int_key", n);
                        updatePointQS();
                        editor.apply();


                        SharedPreferences spe = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                        int  myIntValue = spe.getInt("your_int_key", 0);


//                        user_id_child.child("scores").setValue(+n);
                        diaglog();



//                        if (mInterstitialAd.isLoaded()) {
//                            mInterstitialAd.show();
//                        } else {
//                            Log.d("TAG", "The interstitial wasn't loaded yet.");
//                        }




                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView_wheel.startAnimation(rotateAnimation);


            }
        });








    }

    private void updatePointQS(){

        CustomRequest balanceRequest = new CustomRequest(Request.Method.POST, APP_QUAYSO,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            if(!response.getBoolean("error") && response.getInt("error_code") == ERROR_SUCCESS){
                                //test
                                //Dialogs.succesDialog(getApplicationContext(), getResources().getString(R.string.congratulations), App.getInstance().get("WATCH_REWARD", "") + " " + getResources().getString(R.string.app_currency) + " " + getResources().getString(R.string.successfull_received), false, false, "", getResources().getString(R.string.ok), null);


                            }else if(response.getInt("error_code") == 699 || response.getInt("error_code") == 999){

                                Dialogs.validationError(getApplicationContext(),response.getInt("error_code"));

                            }else if(response.getInt("error_code") == 799) {

                                Dialogs.warningDialog(getApplicationContext(), getResources().getString(R.string.update_app), getResources().getString(R.string.update_app_description), false, false, "", getResources().getString(R.string.update), new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        AppUtils.gotoMarket(getApplicationContext());
                                    }
                                });

                            }

                        }catch (Exception e){
                            // do nothin
                        }

                    }},new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}}){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("data", App.getInstance().getData());
                params.put("checkinTurn", String.valueOf(score));
                return params;
            }
        };

        App.getInstance().addToRequestQueue(balanceRequest);
    }












    private String currentNumber(int degree){

        String text = "";



        if(degree>= (FACTOR*1) && degree<(FACTOR*3)  ){

            text = "2";

            score = score+2;

        }


        if(degree>= (FACTOR*3) && degree<(FACTOR*5)  ){

            text = "3";
            score = score+3;
        }

        if(degree>= (FACTOR*5) && degree<(FACTOR*7)  ){

            text = "10";
            score = score+10;
        }

        if(degree>= (FACTOR*7) && degree<(FACTOR*9)  ){

            text = "5";
            score = score+5;

        }

        if(degree>= (FACTOR*9) && degree<(FACTOR*11)  ){

            text = "6";
            score = score+6;
        }

        if(degree>= (FACTOR*11) && degree<(FACTOR*13)  ){

            text = "7";
            score = score+7;
        }

        if(degree>= (FACTOR*13) && degree<(FACTOR*15)  ){

            text = "8";
            score = score+8;
        }

        if(degree>= (FACTOR*15) && degree<(FACTOR*17)  ){

            text = "9";
            score = score+9;
        }

        if(degree>= (FACTOR*17) && degree<(FACTOR*19)  ){

            text = "100";
            score = score+100;
        }

        if(degree>= (FACTOR*19) && degree<(FACTOR*21)  ){

            text = "11";
            score = score+11;
        }

        if(degree>= (FACTOR*21) && degree<(FACTOR*23)  ){

            text = "12";
            score = score+12;
        }

        if(degree>= (FACTOR*23) && degree<(360) || degree>=0 && degree <(FACTOR*1) ){

            text = "0 point";

        }

        return text;



    }

    public void diaglog(){


        final Dialog dialog = new Dialog(QuaySo.this);
        dialog.setContentView(R.layout.custom_dialog);
        Button dialogButton = (Button) dialog.findViewById(R.id.cool_id);
        TextView textView = (TextView)dialog.findViewById(R.id.dialog_score_id);
        String a = currentNumber(360 - (degree % 360));

        textView.setText(a+" "+"Points");






        // if button is clicked, close the custom dialog

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();


            }
        });
        dialog.show();

    }

//    @Override
//    public void finish() {
//
//
//        Intent intent = new Intent(Spinwheel.this,Home_page.class);
//
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        startActivity(intent);
//
//
//    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent mIntent = getIntent();
        intValue = mIntent.getIntExtra("INT", 0);




    }
}
