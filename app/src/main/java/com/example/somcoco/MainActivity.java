package com.example.somcoco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.somcoco.community.CommunityActivity;
import com.example.somcoco.etc.Calendar;
import com.example.somcoco.etc.EtcFragment;
import com.example.somcoco.home.HomeFragment;
import com.example.somcoco.campuson.CampusOnFragment;
import com.example.somcoco.maplocation.CampusLocationActivity;
import com.example.somcoco.searchcampus.CampusSearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentManager manager = getSupportFragmentManager();
    FragmentTransaction transaction = manager.beginTransaction();
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    HomeFragment homeFragment;
    CampusOnFragment campusOnFragment;
    EtcFragment etcFragment;
    NavigationView navigationView;
    ImageView search;
    ImageView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        search = (ImageView) findViewById(R.id.main_search);
        calendar = (ImageView) findViewById(R.id.main_calendar);
        homeFragment = new HomeFragment();
        campusOnFragment = new CampusOnFragment();
        etcFragment = new EtcFragment();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        //toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();

        bottomNavigationView.setItemIconTintList(null);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                        return true;
                    case R.id.campus_location:
                        Intent intent_loc = new Intent(getApplicationContext(), CampusLocationActivity.class);
                        intent_loc.putExtra("check",false);
                        startActivity(intent_loc);
                        return true;
                    case R.id.community:
                        Intent intent_comm = new Intent(getApplicationContext(), CommunityActivity.class);
                        startActivity(intent_comm);
                        return true;
                    case R.id.etc:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, etcFragment).commit();
                        return true;
                }
                return false;
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CampusSearchActivity.class);
                startActivity(intent);
            }
        });

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Calendar.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.draw_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                return true;
            case R.id.draw_maps:
                Intent intent_loc = new Intent(getApplicationContext(), CampusLocationActivity.class);
                startActivity(intent_loc);
                return true;
            case R.id.draw_community:
                Intent intent_comm = new Intent(getApplicationContext(), CommunityActivity.class);
                startActivity(intent_comm);
                return true;
            case R.id.draw_more:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, etcFragment).commit();
                return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}