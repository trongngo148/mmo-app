package com.mmoteam.twotapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface.OnClickListener;

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
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.mmoteam.twotapp.constants.Constants.ACCOUNT_BALANCE;
import static com.mmoteam.twotapp.constants.Constants.APP_BAUCUA;
import static com.mmoteam.twotapp.constants.Constants.ERROR_SUCCESS;


public class BauCua extends Activity  implements OnClickListener {
    GridView gridView;
    Custom_Gridview_banco adapter;
    Integer[] dshinh = {R.drawable.nai,R.drawable.bau,R.drawable.ga,R.drawable.ca,
            R.drawable.cua,R.drawable.tom};
    Integer[] dshinh2 = {R.drawable.nai2,R.drawable.bau2,R.drawable.ga2,R.drawable.ca2,
            R.drawable.cua2,R.drawable.tom2};
    AnimationDrawable cdxingau1,cdxingau2,cdxingau3;
    ImageView hinhxingau1,hinhxingau2,hinhxingau3;
    Random randomxingau;
    int giatrixingau1,giatrixingau2,giatrixingau3;
    public static Integer[] gtdatcuoc = new Integer[6];
    int tongtiencu,tongtienmoi;
    TextView tvtien;
    int tempQuay=0;
    Timer timer = new Timer();
    Handler handler;
    int tienthuong,kiemtra;
    CountDownTimer demthoigian;
    SharedPreferences luutru;
    Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            randomxingau1();
            randomxingau2();
            randomxingau3();
            for(int i = 0;i<gtdatcuoc.length;i++) {
                if (gtdatcuoc[i] !=0) {
                    if (i==giatrixingau1){
                        tienthuong += gtdatcuoc[i];
                    }
                    if (i==giatrixingau2){
                        tienthuong += gtdatcuoc[i];
                    }
                    if (i==giatrixingau3){
                        tienthuong += gtdatcuoc[i];
                    }
                    if (i != giatrixingau1 && i != giatrixingau2 && i != giatrixingau3){
                        tienthuong -= gtdatcuoc[i];
                    }
                }
            }
            if (tienthuong>0){
                Toast.makeText(getApplicationContext(),"Chúc mừng bạn đã trúng: "+tienthuong+" points",Toast.LENGTH_SHORT).show();
            }
            else if (tienthuong==0){
                Toast.makeText(getApplicationContext(),"Chúc bạn may mắn lần sau! ",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(),"Rất tiếc! Bạn đã bị trừ: "+tienthuong+" points",Toast.LENGTH_SHORT).show();
            }
            luudulieunguoidung(tienthuong);
            tempQuay=0;
            tvtien.setText(String.valueOf(tongtienmoi));
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baucua );


        hinhxingau1 = (ImageView)findViewById(R.id.xingau1);
        hinhxingau2 = (ImageView)findViewById(R.id.xingau2);
        hinhxingau3 = (ImageView)findViewById(R.id.xingau3);
        tvtien = (TextView)findViewById(R.id.tvtien);
        gridView = (GridView)findViewById(R.id.gvbanco);
        adapter = new Custom_Gridview_banco(this,R.layout.custombanco,dshinh);
        gridView.setAdapter(adapter);
        luutru = getSharedPreferences("luu tru thong tin", Context.MODE_PRIVATE);
        updatePoints();
        //tongtiencu = luutru.getInt("tong tien", 1000);
        tvtien.setText(String.valueOf(tongtiencu));

        //tang money cho nguoi choi

//        demthoigian = new CountDownTimer(180000,1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                long milis = millisUntilFinished;
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        };
        handler = new Handler(callback);

    }
    private void setPoints(String title){
        tvtien.setText(title);
    }
    void updatePoints() {

        CustomRequest balanceRequest = new CustomRequest(Request.Method.POST, ACCOUNT_BALANCE,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            if(!response.getBoolean("error")){
                                //Luu tong tien tu database vao bien
                                tongtiencu = luutru.getInt("tong tien", Integer.parseInt(response.getString("user_balance")));
                                tongtiencu = Integer.parseInt(response.getString("user_balance"));
                                setPoints(response.getString("user_balance"));
                                App.getInstance().store("balance",response.getString("user_balance"));

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
                return params;
            }
        };

        App.getInstance().addToRequestQueue(balanceRequest);
    }

    private void updatePointDb(){

        CustomRequest balanceRequest = new CustomRequest(Request.Method.POST, APP_BAUCUA,null,
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
                params.put("tongtienmoi", String.valueOf(tongtienmoi));
                return params;
            }
        };

        App.getInstance().addToRequestQueue(balanceRequest);
    }

    private void luudulieunguoidung(int tienthuong){
        SharedPreferences.Editor edit = luutru.edit();
        tongtienmoi = tongtiencu + tienthuong;
        edit.putInt("tong tien", tongtienmoi);
        updatePointDb();
        //updatePoints();
        tongtiencu=tongtienmoi;
        edit.commit();
    }

    public void back(View v){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    public  void lacxingau(View v){
        hinhxingau1.setImageResource(R.drawable.hinhdongxingau);
        hinhxingau2.setImageResource(R.drawable.hinhdongxingau);
        hinhxingau3.setImageResource(R.drawable.hinhdongxingau);

        cdxingau1 = (AnimationDrawable) hinhxingau1.getDrawable();
        cdxingau2 = (AnimationDrawable) hinhxingau2.getDrawable();
        cdxingau3 = (AnimationDrawable) hinhxingau3.getDrawable();
        kiemtra = 0;
        for (int i = 0;i<gtdatcuoc.length;i++){
            kiemtra += gtdatcuoc[i];

        }
        if (kiemtra == 0){
            Toast.makeText(getApplicationContext(),"Mời bạn đặt cược (Bấm vào hình)!",Toast.LENGTH_SHORT).show();
        }
        else {
            if (kiemtra>tongtiencu){
                Toast.makeText(getApplicationContext(),"Bạn không đủ tiền để đặt cược!",Toast.LENGTH_SHORT).show();
            }else if(tempQuay==1){
                Toast.makeText(getApplicationContext(),"Đang xoay! Vui lòng đợi ",Toast.LENGTH_SHORT).show();
            }
            else{


                cdxingau1.start();
                cdxingau2.start();
                cdxingau3.start();
                tempQuay=1;
                tienthuong = 0;
                timer.schedule(new lacxingau(), 1000);
            }
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    class lacxingau extends TimerTask{
        public void run(){
            handler.sendEmptyMessage(0);

        }

    }
    private void randomxingau1(){
        randomxingau = new Random();
        int rd = randomxingau.nextInt(6);
        switch (rd){
            case 0:
                hinhxingau1.setImageResource(dshinh2[0]);
                giatrixingau1 = rd;
                break;
            case 1:
                hinhxingau1.setImageResource(dshinh2[1]);
                giatrixingau1 = rd;
                break;
            case 2:
                hinhxingau1.setImageResource(dshinh2[2]);
                giatrixingau1 = rd;
                break;
            case 3:
                hinhxingau1.setImageResource(dshinh2[3]);
                giatrixingau1 = rd;
                break;
            case 4:
                hinhxingau1.setImageResource(dshinh2[4]);
                giatrixingau1 = rd;
                break;
            case 5:
                hinhxingau1.setImageResource(dshinh2[5]);
                giatrixingau1 = rd;
                break;
        }
    }
    private void randomxingau2(){
        randomxingau = new Random();
        int rd = randomxingau.nextInt(6);
        switch (rd){
            case 0:
                hinhxingau2.setImageResource(dshinh2[0]);
                giatrixingau2 = rd;
                break;
            case 1:
                hinhxingau2.setImageResource(dshinh2[1]);
                giatrixingau2 = rd;
                break;
            case 2:
                hinhxingau2.setImageResource(dshinh2[2]);
                giatrixingau2 = rd;
                break;
            case 3:
                hinhxingau2.setImageResource(dshinh2[3]);
                giatrixingau2 = rd;
                break;
            case 4:
                hinhxingau2.setImageResource(dshinh2[4]);
                giatrixingau2 = rd;
                break;
            case 5:
                hinhxingau2.setImageResource(dshinh2[5]);
                giatrixingau2 = rd;
                break;
        }
    }
    private void randomxingau3(){
        randomxingau = new Random();
        int rd = randomxingau.nextInt(6);
        switch (rd){
            case 0:
                hinhxingau3.setImageResource(dshinh2[0]);
                giatrixingau3 = rd;
                break;
            case 1:
                hinhxingau3.setImageResource(dshinh2[1]);
                giatrixingau3 = rd;
                break;
            case 2:
                hinhxingau3.setImageResource(dshinh2[2]);
                giatrixingau3 = rd;
                break;
            case 3:
                hinhxingau3.setImageResource(dshinh2[3]);
                giatrixingau3 = rd;
                break;
            case 4:
                hinhxingau3.setImageResource(dshinh2[4]);
                giatrixingau3 = rd;
                break;
            case 5:
                hinhxingau3.setImageResource(dshinh2[5]);
                giatrixingau3 = rd;
                break;
        }
    }
}