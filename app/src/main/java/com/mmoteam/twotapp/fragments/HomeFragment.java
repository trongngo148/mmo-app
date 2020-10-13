package com.mmoteam.twotapp.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.mmoteam.twotapp.R;
import com.mmoteam.twotapp.adapters.OfferWallsAdapter;
import com.mmoteam.twotapp.adapters.OffersAdapter;
import com.mmoteam.twotapp.app.App;
import com.mmoteam.twotapp.model.OfferWalls;
import com.mmoteam.twotapp.model.Offers;
import com.mmoteam.twotapp.utils.CustomRequest;
import com.mmoteam.twotapp.utils.Dialogs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.mmoteam.twotapp.constants.Constants.API_OFFERTORO;
import static com.mmoteam.twotapp.constants.Constants.APP_OFFERWALLS;
import static com.mmoteam.twotapp.constants.Constants.DEBUG_MODE;

/**
 * Created by DroidOXY
 */
 
public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }
    private AdView mAdView;
    View view;
    ViewFlipper view_flip;

    ProgressBar progressBar,progressBarOfferwalls;

    RecyclerView offers_list,offerwalls_list;
    private OffersAdapter offersAdapter;
    private OfferWallsAdapter offerWallsAdapter;
    ArrayList<OfferWalls> offerWalls;
    ArrayList<Offers> offers;

    LinearLayout offerWallsTitle,offerWallsTopSpace,offersTitle,offersTopSpace;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        int image[]={R.drawable.posterfi1, R.drawable.posterfi2};
        view_flip=(ViewFlipper)view.findViewById(R.id.view_flip);
        for(int i=0;i<image.length;i++){
            ImageView imageView=new ImageView(getActivity());
            imageView.setBackgroundResource(image[i]);
            view_flip.addView(imageView);
            view_flip.setFlipInterval(6000);
            view_flip.setAutoStart(true);
            view_flip.setInAnimation(getActivity(),android.R.anim.slide_in_left);
            view_flip.setOutAnimation(getActivity(),android.R.anim.slide_in_left);
        }
        for(int iimage: image){
            ImageView imageView=new ImageView(getActivity());
            imageView.setBackgroundResource(iimage);
            view_flip.addView(imageView);
            view_flip.setFlipInterval(6000);
            view_flip.setAutoStart(true);
            view_flip.setInAnimation(getActivity(),android.R.anim.slide_in_left);
            view_flip.setOutAnimation(getActivity(),android.R.anim.slide_in_left);
        }




        offerWallsTopSpace = view.findViewById(R.id.offerWallsTopSpace);
        offerWallsTitle = view.findViewById(R.id.offerWallsTitle);
        offersTitle = view.findViewById(R.id.offersTitle);
        offersTopSpace = view.findViewById(R.id.offersTopSpace);

        progressBarOfferwalls = view.findViewById(R.id.progressBarOfferwalls);
        progressBar = view.findViewById(R.id.progressBar);

        /* Offers Walls Listview code is here*/
        offerwalls_list = view.findViewById(R.id.offerwalls_list);
        offerWalls = new ArrayList<>();
        offerWallsAdapter = new OfferWallsAdapter(getActivity(),offerWalls);


        RecyclerView.LayoutManager offerWallsLayoutManager = new GridLayoutManager(getActivity(),3);
        // RecyclerView.LayoutManager offerWallsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        offerwalls_list.setLayoutManager(offerWallsLayoutManager);
        offerwalls_list.setItemAnimator(new DefaultItemAnimator());
        offerwalls_list.setAdapter(offerWallsAdapter);


        /* Offers Listview code is here*/
        offers_list = view.findViewById(R.id.offers_list);
        offers = new ArrayList<>();
        offersAdapter = new OffersAdapter(getActivity(),offers);


        RecyclerView.LayoutManager offersLayoutmanager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        offers_list.setLayoutManager(offersLayoutmanager);
        offers_list.setItemAnimator(new DefaultItemAnimator());
        offers_list.setAdapter(offersAdapter);
        //admob
        MobileAds.initialize(getContext(), getString(R.string.admob_appId));
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        load_offerwalls();

        if(!App.getInstance().get("API_OFFERS_ACTIVE",true)){

            offersTopSpace.setVisibility(View.GONE);
            offersTitle.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            offers_list.setVisibility(View.GONE);

        }

        if(App.getInstance().get("OfferToroAPIOffersActive",true) && App.getInstance().get("API_OFFERS_ACTIVE",true)){ load_offertoro(); }

        return view;




    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        offersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onResume() {
        super.onResume();

        offersAdapter.notifyDataSetChanged();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    void load_offerwalls(){

        progressBarOfferwalls.setVisibility(View.GONE);
        CustomRequest offerwallsRequest = new CustomRequest(Request.Method.POST, APP_OFFERWALLS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONObject Response = new JSONObject(App.getInstance().deData(response.toString()));

                            if (!Response.getBoolean("error")) {

                                JSONArray offerwalls = Response.getJSONArray("offerwalls");

                                if(offerwalls.length() < 1){
                                    offerWallsTopSpace.setVisibility(View.GONE);
                                    offerWallsTitle.setVisibility(View.GONE);
                                    progressBarOfferwalls.setVisibility(View.GONE);
                                    offerwalls_list.setVisibility(View.GONE);
                                }

                                for (int i = 0; i < offerwalls.length(); i++) {

                                    JSONObject obj = offerwalls.getJSONObject(i);

                                    OfferWalls singleOfferWall = new OfferWalls();

                                    singleOfferWall.setOfferid(obj.getString("offer_id"));
                                    singleOfferWall.setTitle(obj.getString("offer_title"));
                                    singleOfferWall.setSubtitle(obj.getString("offer_subtitle"));
                                    singleOfferWall.setImage(obj.getString("offer_thumbnail"));
                                    singleOfferWall.setAmount(obj.getString("offer_points"));
                                    singleOfferWall.setType(obj.getString("offer_type"));
                                    singleOfferWall.setStatus(obj.getString("offer_status"));
                                    singleOfferWall.setPartner("offerwalls");

                                    if(obj.get("offer_status").equals("Active")){
                                        offerWalls.add(singleOfferWall);
                                    }

                                    progressBarOfferwalls.setVisibility(View.GONE);

                                }
                                offerWallsAdapter.notifyDataSetChanged();

                            }else if(Response.getInt("error_code") == 699 || Response.getInt("error_code") == 999){

                                Dialogs.validationError(getContext(),Response.getInt("error_code"));

                            }else{

                                if(!DEBUG_MODE){
                                    Dialogs.serverError(getContext(),getResources().getString(R.string.ok),null);
                                }
                            }

                        } catch (JSONException e) {
                            if(!DEBUG_MODE){
                                Dialogs.serverError(getContext(),getResources().getString(R.string.ok),null);
                            }else{
                                Dialogs.errorDialog(getContext(),"Got Error",e.toString() + ", please contact developer immediately",true,false,"","ok",null);
                            }
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if(DEBUG_MODE){
                    Dialogs.warningDialog(getContext(),"Got Error",error.toString(),true,false,"","ok",null);
                }else{

                    offerWallsTopSpace.setVisibility(View.GONE);
                    offerWallsTitle.setVisibility(View.GONE);
                    progressBarOfferwalls.setVisibility(View.GONE);
                    offerwalls_list.setVisibility(View.GONE);
                }

            }
        });

        App.getInstance().addToRequestQueue(offerwallsRequest);

    }

    void load_offertoro(){

        progressBar.setVisibility(View.VISIBLE);
        CustomRequest offertoroOffersRequest = new CustomRequest(Request.Method.POST, API_OFFERTORO, null,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONObject jsonObjMain = new JSONObject(response.toString());

                    JSONObject mmmm = jsonObjMain.getJSONObject("response");

                    JSONArray alloffers = mmmm.getJSONArray("offers");

                    for (int i = 0; i < alloffers.length(); i++) {

                        JSONObject obj = alloffers.getJSONObject(i);

                        String offerid = obj.getString("offer_id");
                        String uniq_id = obj.getString("offer_id");
                        String title = obj.getString("offer_name");
                        String url = obj.getString("offer_url_easy");
                        String thumbnail = obj.getString("image_url");
                        String subtitle = obj.getString("offer_desc");
                        String partner = "offertoro";

                        String amount = obj.getString("amount");
                        String OriginalAmount = obj.getString("amount");

                        String bg_image = "";
                        String instructions_title = "Offer Instructions : ";
                        String instruction_one = "1. "+subtitle;
                        String instruction_two = "2. Amount will be Credited within 24 hours after verification";
                        String instruction_three = "3. Check history for progress";
                        String instruction_four = "4. Skip those installed before ( unqualified won't get Rewarded )";

                        Offers beanClassForRecyclerView_contacts = new Offers(thumbnail,title,amount,OriginalAmount,url,subtitle,partner,uniq_id,offerid,bg_image,instructions_title,instruction_one,instruction_two,instruction_three,instruction_four,false);
                        offers.add(beanClassForRecyclerView_contacts);

                        progressBar.setVisibility(View.GONE);

                    }
                    offersAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    // do nothin
                }
            }}, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // do nothin
            }}){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("country", App.getInstance().getCountryCode());
                return params;
            }
        };

        App.getInstance().addToRequestQueue(offertoroOffersRequest);

    }
	
}
