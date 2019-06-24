
package com.example.neighbourapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.neighbourapplication.controller.IncidentController;
import com.example.neighbourapplication.controller.WatchProgrammeController;
import com.example.neighbourapplication.model.Incident;
import com.example.neighbourapplication.model.WatchProgramme;
import com.example.neighbourapplication.sqlite.IncidentDB;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, WatchProgrammeDialog.watchProgrammeDialogListener, AddIncidentDialog.AddIncidentDialogListener, LocationListener {
    GeoPoint currentLocation;
    LocationManager locationManager;
    SharedPreferences sharedPreferences;
    String username;

    final int LOGIN_REQUEST = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sharedPreferences = getSharedPreferences("NeighbourApp", MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);
        if(username==null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST);
        }
        System.out.println(username);
        FirebaseMessaging.getInstance().subscribeToTopic("incidentupdate");

        if(!(PackageManager.PERMISSION_GRANTED==checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION))){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1337);
        }

        if (!(PackageManager.PERMISSION_GRANTED == checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE))) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 5);
        } else
            uploadPending();



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_home);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;
        try
        {
            fragment = HomeFragment.class.newInstance();
        }catch (Exception e) {
            e.printStackTrace();
        }
        fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the HomeFragment/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.addReport) {

            AddIncidentDialog addIncidentDialog = new AddIncidentDialog();
            addIncidentDialog.show(getSupportFragmentManager(),"Add Incident");
            try {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null)
                    currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                else {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null)
                        currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                    else
                        currentLocation = new GeoPoint(101, 100);
                }


            } catch (SecurityException e) {
                e.printStackTrace();
            }
              return true;
        }
        else if (id== R.id.addActvity){
            WatchProgrammeDialog watchProgrammeDialog = new WatchProgrammeDialog();
            watchProgrammeDialog.show(getSupportFragmentManager(),"Add Activity");
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void ShowFragment(int itemId) {
        Fragment fragment = null;

        switch (itemId) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_profile:
                fragment = new ProfileInfoFragment();
                break;
            case R.id.nav_incident:
                fragment = new IncidentListFragment();
                break;
            case R.id.nav_neighbourHome:
                fragment = new NeighbourInfoFragment();
                break;
            case R.id.nav_programme:
                fragment = new WatchProgrammeFragment();
                break;
            case R.id.nav_logout:
                logout();
                break;
        }

        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, fragment);
            fragmentTransaction.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==LOGIN_REQUEST){
            if(resultCode==RESULT_OK){
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username",data.getStringExtra("username"));
                editor.putString("email",data.getStringExtra("email"));
                editor.commit();
                finish();
                startActivity(getIntent());
            }
            else
                finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        ShowFragment(item.getItemId());

    //@Override
    //public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        //int id = item.getItemId();

       // if (id == R.id.nav_home) {
            // Handle the camera action
        //} else if (id == R.id.nav_profile) {

        //} else if (id == R.id.nav_incident) {

       // } else if (id == R.id.nav_neighbourHome) {

       // } else if (id == R.id.nav_programme) {

       // } else if (id == R.id.nav_analysis) {

        //}


        return true;
    }

    @Override
    public void applyTexts(String name, String date, String time, String note, Uri imageUri) {
        IncidentController incidentController = new IncidentController(getApplicationContext());
        Incident incident = new Incident();
        incident.setIncidentName(name);
        incident.setDate(date);
        incident.setTime(time);
        incident.setLocation(currentLocation);
        incident.setDescription(note);
        incidentController.insertIncident(incident, imageUri, this);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public  void logout(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("username");
        editor.commit();
        finish();
        startActivity(getIntent());
    }

    @Override
    public void watchProgrammeInput(String programmeName, String description, String dateStart, String dateEnd) {
        WatchProgrammeController watchProgrammeController = new WatchProgrammeController(getApplicationContext());
        WatchProgramme watchProgramme = new WatchProgramme();
        watchProgramme.setProgrammeName(programmeName);
        watchProgramme.setDescription(description);
        watchProgramme.setDateStart(dateStart);
        watchProgramme.setDateEnd(dateEnd);
        watchProgramme.setUsername(username);
        watchProgrammeController.insertWatchProgramme(watchProgramme, this);


    }

    public void uploadPending() {
        IncidentDB incidentDB = new IncidentDB(this);
        HashMap<String, Incident> incidents = new HashMap<>();
        incidentDB.fnGetIncidents(IncidentDB.tblNameIncidentToUpload, incidents);
        if (incidents.size() != 0) {
            Toast.makeText(this, "Detected pending incident report, uploading now...", Toast.LENGTH_SHORT).show();
            IncidentController incidentController = new IncidentController(this);
            incidentController.insertIncident(incidents, this);
        }
    }
}
