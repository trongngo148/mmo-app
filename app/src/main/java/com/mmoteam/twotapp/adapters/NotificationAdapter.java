package com.mmoteam.twotapp.adapters;



import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mmoteam.twotapp.R;
import com.mmoteam.twotapp.model.Transactions;

import java.util.List;


/**
 * Created by DroidOXY
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    private Context context;
    private List<Transactions> listItem;
    String PrevDate;

    public NotificationAdapter(Context context, List<Transactions> listItem) {
        this.context = context;
        this.listItem = listItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_list,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,final int position) {


        final Transactions transaction = listItem.get(position);

        String date = transaction.getTnDate();
        String TnType = transaction.getTnType();
        String status = transaction.getStatus();

        if(position == 0){

            PrevDate = "empty";

        }else{

            PrevDate = listItem.get(position - 1).getTnDate();
        }

        holder.tnName.setText(transaction.getTnName());
        holder.tncat.setText("#id : " + transaction.getTnId());

        if (date.intern() != PrevDate.intern()){

            holder.date.setText(date);
            holder.date.setVisibility(View.VISIBLE);
        }



        /** Glide.with(context).load(transaction.getImage())
         .apply(new RequestOptions().override(60,60))
         .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
         .apply(RequestOptions.skipMemoryCacheOf(true))
         .into(holder.image);  **/

    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date,tnName,tncat,amount,statusName;
        ImageView image;
        LinearLayout SingleItem;
        public ViewHolder(View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            tnName = itemView.findViewById(R.id.tnName);
            tncat = itemView.findViewById(R.id.tnType);
            amount = itemView.findViewById(R.id.amount);
            statusName = itemView.findViewById(R.id.statusName);
            image = itemView.findViewById(R.id.image);
            SingleItem = itemView.findViewById(R.id.SingleItem);
        }
    }
}
