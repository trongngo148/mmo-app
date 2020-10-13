package com.mmoteam.twotapp.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mmoteam.twotapp.R;
import com.mmoteam.twotapp.activities.AppActivity;
import com.mmoteam.twotapp.activities.FragmentsActivity;
import com.mmoteam.twotapp.app.App;

public class FragmentProfile extends Fragment {

    public FragmentProfile(){

    }
    View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_profile,container,false);
        TextView fullname = view.findViewById(R.id.nav_bar_display_name1);
        TextView email = view.findViewById(R.id.nav_bar_display_email1);
        TextView phone =view.findViewById(R.id.nav_bar_display_phone1);
        TextView usern =view.findViewById(R.id.username1);
        usern.setText(App.getInstance().getUsername());
        fullname.setText(App.getInstance().getFullname());
        email.setText(App.getInstance().getEmail());
        phone.setText(App.getInstance().getPhone());

        final Button logout=view.findViewById(R.id.button_logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());

                ad.setTitle("Bạn muốn đăng xuất ?");


                ad.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int which) {
                App.getInstance().logout();
                Intent intent = new Intent(getActivity(), AppActivity.class);
                startActivity(intent);
                finish();
                    }
                });

                ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int which) {
                        dlg.cancel();
                    }
                });

                ad.show();
            }
        });

        return view;
    }
    void finish(){

        Activity close = getActivity();
        if(close instanceof FragmentsActivity){
            FragmentsActivity show = (FragmentsActivity) close;
            show.closeActivity();
        }

    }
}
