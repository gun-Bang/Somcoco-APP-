package com.example.somcoco.tour;

import android.content.Intent;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.somcoco.AppHelper;
import com.example.somcoco.R;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BuildingInfoActivity extends AppCompatActivity {

    String cps;
    String tagID;
    FragmentManager manager = getSupportFragmentManager();
    FragmentTransaction transaction = manager.beginTransaction();
    NavigationView navigationView;
    RecyclerView recyclerView;
    Toolbar toolbar;
    DrawerLayout drawer;
    //FrameLayout content;
    ActionBarDrawerToggle toggle;
    MediaPlayer mp;
    FacilityListAdapter adapter;
    TextView buildName;
    ImageView buildImg;
    ImageView buildPlay;
    ImageView buildPause;
    SeekBar buildSeekbar;
    Switch buildLang;
    TextView buildContents;
    TextView buildTitle;
    String kor;
    String eng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buildinginfo);

        if(AppHelper.requestQueue == null){
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        Intent inIntent = getIntent();
        tagID = inIntent.getStringExtra("tagID");
        cps = inIntent.getStringExtra("cps");

        buildName = (TextView) findViewById(R.id.build_info_name);
        buildImg = (ImageView) findViewById(R.id.build_img);
        buildPlay = (ImageView) findViewById(R.id.build_play);
        buildPause = (ImageView) findViewById(R.id.build_pause);
        buildSeekbar = (SeekBar) findViewById(R.id.build_seekbar);
        buildLang =  (Switch) findViewById(R.id.build_lang);
        buildContents = (TextView) findViewById(R.id.build_contents);
        buildTitle = (TextView) findViewById(R.id.build_title);

        toolbar = (Toolbar) findViewById(R.id.build_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        getSupportActionBar().setTitle(null);

        //content = (FrameLayout) findViewById(R.id.build_container);
        drawer = (DrawerLayout) findViewById(R.id.build_drawer);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);

        navigationView = (NavigationView) findViewById(R.id.build_nav);
        recyclerView = (RecyclerView) findViewById(R.id.facil_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new FacilityListAdapter(getApplicationContext());

        adapter.setOnItemClickListener(new FacilityListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(FacilityListAdapter.ViewHolder holder, View view, int position) {
                FacilityListItem item = adapter.getItem(position);

                if (mp != null) {
                    mp.pause();
                    mp.release();
                    mp = null;
                }

                sendRequestFacility(tagID, cps, String.valueOf(item.getFacilityNum()));
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        buildLang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    buildContents.setText(eng);
                }else{
                    buildContents.setText(kor);
                }
            }
        });

        buildTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp != null) {
                    mp.pause();
                    mp.release();
                    mp = null;
                }
                sendRequestBuild(tagID, cps);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        adapter.items.clear();
        sendRequestBuild(tagID, cps);
        sendRequestFacList(tagID, cps);

        buildPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(fromUser)
                            mp.seekTo(progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                mp.start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(mp.isPlaying()){
                            try {
                                //Thread.sleep(1000);
                                buildSeekbar.setProgress(mp.getCurrentPosition());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });

        buildPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.pause();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            mp.pause();
            mp.release();
            mp = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mp != null) {
            mp.pause();
            mp.release();
            mp = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

            if (mp != null) {
                mp.pause();
                mp.release();
                mp = null;
            }
            finish();
        }
    }

    public void sendRequestBuild(String nfc, String cps){
        String url = "https://somcoco.co.kr/application/building_info.jsp";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            processResponseBuild(response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("nfc", nfc);
                params.put("cps", cps);

                return params;
            }
        };
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }

    public void processResponseBuild(String response) throws IOException {
        Gson gson = new Gson();
        BuildingInfoItem buildingInfoItem = gson.fromJson(response, BuildingInfoItem.class);

        kor = buildingInfoItem.getBuildingIntroKr();
        eng = buildingInfoItem.getBuildingIntroEn();

        buildName.setText(buildingInfoItem.getBuildingName());
        buildTitle.setText(buildingInfoItem.getBuildingName());
        Glide.with(this)
                .load(buildingInfoItem.getBuildingImage())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(buildImg);
        buildContents.setText(buildingInfoItem.getBuildingIntroKr());

        try{
            mp = new MediaPlayer();
            mp.setDataSource(buildingInfoItem.getBuildingAudio());
            mp.prepare();
            buildSeekbar.setMax(mp.getDuration());
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void sendRequestFacList(String nfc, String cps){
        String url = "https://somcoco.co.kr/application/facility_list.jsp";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processResponseFacList(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("nfc", nfc);
                params.put("cps", cps);

                return params;
            }
        };
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }

    public void processResponseFacList(String response){
        Gson gson = new Gson();
        FacilityListArray facilityListArray = gson.fromJson(response, FacilityListArray.class);

        if (facilityListArray != null) {
            for (int i = 0; i < facilityListArray.facilityList.size(); i++){
                adapter.addItem(new FacilityListItem(facilityListArray.getItem(i).getFacilityNum(), facilityListArray.getItem(i).getFacilityName()));
            }
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
        }
    }

    public void sendRequestFacility(String nfc, String cps, String num){
        String url = "https://somcoco.co.kr/application/facility_info.jsp";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            processResponseFacility(response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("nfc",nfc);
                params.put("cps",cps);
                params.put("num",num);

                return params;
            }
        };
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }

    public void processResponseFacility(String response) throws IOException {
        Gson gson = new Gson();
        FacilityInfoItem facilityInfoItem = gson.fromJson(response, FacilityInfoItem.class);



        kor = facilityInfoItem.getFacilityInfoKr();
        eng = facilityInfoItem.getFacilityInfoEn();

        buildName.setText(facilityInfoItem.getFacilityName());
        Glide.with(this)
                .load(facilityInfoItem.getFacilityImg())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(buildImg);
        buildContents.setText(facilityInfoItem.getFacilityInfoKr());
        try {
            mp = new MediaPlayer();
            mp.setDataSource(facilityInfoItem.getFacilityAudio());
            mp.prepare();
            buildSeekbar.setMax(mp.getDuration());
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
