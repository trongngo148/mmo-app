package com.mmoteam.twotapp.adapters;

import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mmoteam.twotapp.R;
import com.mmoteam.twotapp.app.App;
import com.mmoteam.twotapp.model.Payouts;
import com.mmoteam.twotapp.utils.CustomRequest;
import com.mmoteam.twotapp.utils.Dialogs;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.mmoteam.twotapp.constants.Constants.ACCOUNT_REDEEM;
import static com.mmoteam.twotapp.constants.Constants.DEBUG_MODE;
import static com.mmoteam.twotapp.constants.Constants.ERROR_SUCCESS;

/**
 * Created by DroidOXY
 */

public class PayoutsAdapter extends RecyclerView.Adapter<PayoutsAdapter.ViewHolder>{

    private Context context;
    private List<Payouts> listItem;

    public PayoutsAdapter(Context context, List<Payouts> listItem) {
        this.context = context;
        this.listItem = listItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_redeem_list,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        String PrevItem;
        final Payouts payout = listItem.get(position);

        final String title = payout.getPayoutName();
        final String subtitle = payout.getSubtitle();
        final String message = payout.getPayoutMessage();
        final String amount = payout.getAmount();
        final String points = payout.getReqPoints();
        final String payoutId = payout.getPayoutId();
        final String status = payout.getStatus();
        final String image = payout.getImage();

        if(App.getInstance().get("REDEEM_DISPLAY_TITLE",false)){

            if(position == 0){

                PrevItem = "empty";

            }else{

                PrevItem = listItem.get(position - 1).getPayoutName();
            }

            if (!title.equals(PrevItem)){

                holder.date.setText(title);
                holder.date.setVisibility(View.VISIBLE);
            }

        }

        holder.tnName.setText(title);
        holder.tncat.setText(subtitle);

        if(App.getInstance().get("REDEEM_DISPLAY_AMOUNT",true)){
            holder.amount.setText(amount);
        }else{
            holder.amount.setText(context.getResources().getString(R.string.redeem));
        }

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && App.getInstance().get("REDEEM_DISPLAY_AMOUNT_BG",false)){

            holder.amount.setTextColor(context.getResources().getColor(R.color.white));
            holder.amount.setTextSize(14);
            holder.amount.setBackground(ContextCompat.getDrawable(context,R.drawable.rounded_bg));
            //holder.amount.setBackground(context.getDrawable(R.drawable.rounded_bg));
        }

        Glide.with(context).load(image)
                .apply(new RequestOptions().override(60,60))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .into(holder.image);


        holder.SingleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Redeem(title, subtitle, message, amount, points, payoutId, status, image);

            }
        });

    }

    public void Redeem(String title, String subtitle, String message, String amount, String points, final String payoutId, String status, String image){

        if(Integer.parseInt(App.getInstance().getBalance()) >= Integer.parseInt(points)){

            final EditText editText = new EditText(context);
            editText.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(99)});
            editText.setMinLines(2);

            Dialogs.editTextDialog(context, editText, message, false, true, context.getResources().getString(R.string.cancel), context.getResources().getString(R.string.proceed), new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {

                    String payoutTo = editText.getText().toString();

                    if(!payoutTo.isEmpty()){

                        if(payoutTo.length() < 4){

                            Dialogs.errorDialog(context,context.getResources().getString(R.string.error),context.getResources().getString(R.string.enter_something),true,false,"",context.getResources().getString(R.string.ok),null);

                        }else{

                            sweetAlertDialog.dismiss();
                            processRedeem(payoutId, payoutTo);
                        }

                    }else{

                        Dialogs.errorDialog(context,context.getResources().getString(R.string.error),context.getResources().getString(R.string.enter_something),true,false,"",context.getResources().getString(R.string.ok),null);
                    }
                }
            });

        }else{

            Dialogs.warningDialog(context,  context.getResources().getString(R.string.oops),context.getResources().getString(R.string.no_enough)+" "+context.getResources().getString(R.string.app_currency)+" "+context.getResources().getString(R.string.to)+" "+context.getResources().getString(R.string.redeem), false, false, "", context.getResources().getString(R.string.ok), null);

        }

    }

    void processRedeem(final String payoutId, final String payoutTo){

        CustomRequest redeemRequest = new CustomRequest(Request.Method.POST, ACCOUNT_REDEEM,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try{

                            JSONObject Response = new JSONObject(App.getInstance().deData(response.toString()));

                            if(!Response.getBoolean("error") && Response.getInt("error_code") == ERROR_SUCCESS){

                                // Success
                                Dialogs.succesDialog(context,context.getResources().getString(R.string.redeem_success_title),context.getResources().getString(R.string.redeem_succes_message),false,false,"",context.getResources().getString(R.string.ok),null);

                                App.getInstance().updateBalance();

                            }else if(Response.getInt("error_code") == 420){

                                // No Enough Balance
                                Dialogs.warningDialog(context,  context.getResources().getString(R.string.oops),context.getResources().getString(R.string.no_enough)+" "+context.getResources().getString(R.string.app_currency)+" "+context.getResources().getString(R.string.to)+" "+context.getResources().getString(R.string.redeem), false, false, "", context.getResources().getString(R.string.ok), null);

                            }else if(Response.getInt("error_code") == 699 || Response.getInt("error_code") == 999){

                                Dialogs.validationError(context,Response.getInt("error_code"));

                            }else if(DEBUG_MODE){

                                // For Testing ONLY - intended for Developer Use ONLY not visible for Normal App user
                                Dialogs.errorDialog(context,Response.getString("error_code"),Response.getString("error_description"),false,false,"",context.getResources().getString(R.string.ok),null);

                            }else{

                                // Server error
                                Dialogs.serverError(context, context.getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                        finish();
                                    }
                                });
                            }

                        }catch (Exception e){

                            if(!DEBUG_MODE){
                                Dialogs.serverError(context, context.getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                        finish();
                                    }
                                });
                            }else{
                                Dialogs.errorDialog(context,"Got Error",e.toString() + ", please contact developer immediately",true,false,"","ok",null);
                            }

                        }

                    }},new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {



                if(!DEBUG_MODE){
                    Dialogs.serverError(context, context.getResources().getString(R.string.ok), new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
//                            finish();
                        }
                    });
                }else{
                    Dialogs.errorDialog(context,"Got Error",error.toString(),true,false,"","ok",null);
                }

            }}){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("data", App.getInstance().getDataCustom(payoutId, payoutTo));
                return params;
            }
        };

        App.getInstance().addToRequestQueue(redeemRequest);

    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView date,tnName,tncat,amount;
        ImageView image;
        LinearLayout SingleItem;
        ViewHolder(View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            tnName = itemView.findViewById(R.id.tnName);
            tncat = itemView.findViewById(R.id.tnType);
            amount = itemView.findViewById(R.id.amount);
            image = itemView.findViewById(R.id.image);
            SingleItem = itemView.findViewById(R.id.SingleItem);
        }
    }
}
