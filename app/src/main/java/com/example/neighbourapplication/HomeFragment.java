package com.example.neighbourapplication;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.neighbourapplication.controller.IncidentController;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;

public class HomeFragment extends Fragment implements LocationListener {
    RecyclerView watchProgrammeRecycler;
    WatchProgrammeAdapter watchProgrammeAdapter;
    MapView mapView;
    GoogleMap googleMap;
    private Location location;
    LocationManager locationManager;
    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        //returning layout file
        final View rootView = inflater.inflate(R.layout.home_fragment, container, false);
        getActivity().setTitle("HomeFragment Page");
        mapView = rootView.findViewById(R.id.homeMap);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        try{
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location!=null)
                this.location = location;
            else{
                location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(location!=null)
                    this.location = location;
                else
                    this.location = new Location(LocationManager.GPS_PROVIDER);
            }


        }catch (SecurityException e){
            e.printStackTrace();
        }

        MapsInitializer.initialize(getActivity().getApplicationContext());
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng( location.getLatitude(), location.getLongitude()),15));
                IncidentController incidentController = new IncidentController(getContext());
                incidentController.getIncidentsLocation(googleMap);
                try {
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                }catch (SecurityException e ){
                    e.printStackTrace();
                }
            }
        });
        return rootView;
    }



    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
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
}
