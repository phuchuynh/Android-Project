package htp.mapsplantravelapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import htp.mapsplantravelapplication.model.ObjectPlan;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    // Tooolbar
    private FABToolbarLayout toolbarLayout;
    private View locate, show, search, list;
    private TextView txtInfo;
    private View fab;
    //search
    private Menu searchMenu;
    //getlocation
    LocationManager lm;
    Location  location;
    String provider;
    //GetOjectLocation
     private ObjectPlan objectPlan=null;


    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MapsActivity.this, "Location permission is not available for google map api", Toast.LENGTH_LONG).show();
            return;
        }
        findViewById();
//        mMap.setMyLocationEnabled(true);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }

    public void findViewById() {
        toolbarLayout = (FABToolbarLayout) findViewById(R.id.fabtoolbar);
        list = (View) findViewById(R.id.list_button);
        locate = (View) findViewById(R.id.location_button);
        search = (View) findViewById(R.id.search_button);
        show = (View) findViewById(R.id.show_list_button);

        searchMenu = (Menu) findViewById(R.id.action_search);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MapsActivity.this, "Location permission is not available for google map api", Toast.LENGTH_LONG).show();
            return;
        }
        mMap.setMyLocationEnabled(true);
       // set INforwindown  in GG Maps
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.activity_inforwindown, null);
                LatLng location = marker.getPosition();
                String address = getLocation(location.latitude, location.longitude);
                TextView txtAddress = (TextView) v.findViewById(R.id.txt_address);
                txtAddress.setText(address);

                mMap.setOnInfoWindowClickListener(MapsActivity.this);
                return v;

            }
        });


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                googleMap.clear();
                MarkerOptions markerOptions= new MarkerOptions();
                markerOptions.position(latLng);
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                Marker marker= googleMap.addMarker(markerOptions);
                String   address= getLocation(marker.getPosition().latitude, marker.getPosition().longitude);
                marker.showInfoWindow();
                toolbarLayout.show();
                objectPlan = new ObjectPlan();
                objectPlan.setPlace(address);
                objectPlan.setLat(marker.getPosition().latitude);
                objectPlan.setLng(marker.getPosition().longitude);
                list.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (objectPlan!=null)
                        {
                            Intent intent = new Intent(MapsActivity.this, AddListActivity.class);
                            intent.putExtra("objPlan", objectPlan);
                            startActivity(intent);
                        }
                    }
                });
                search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                show.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MapsActivity.this, ShowListActivity.class);
                        intent.putExtra("objPlan", objectPlan);
                        startActivity(intent);
                    }
                });
                locate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getMyLocation();
                    }
                });


            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.menu_search_list, menu);
        return super.onCreateOptionsMenu(menu);

    }

     public  void  onLocationChange(Location location)
     {
         mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),13));
         CameraPosition cameraPosition= new CameraPosition.Builder()
                 .target(new LatLng(location.getLatitude(),location.getLongitude()))
                 .zoom(16)
                 .build();
         mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
         VisibleRegion visibleRegion= mMap.getProjection().getVisibleRegion();
         double left= visibleRegion.latLngBounds.southwest.longitude;
         double top= visibleRegion.latLngBounds.northeast.longitude;
         double right= visibleRegion.latLngBounds.northeast.latitude;
         double bottom =visibleRegion.latLngBounds.southwest.latitude;

         Toast.makeText(MapsActivity.this,"dis:"+top, Toast.LENGTH_SHORT).show();


     }


    public  void getMyLocation()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MapsActivity.this, "Location permission is not available for google map api", Toast.LENGTH_LONG).show();
            return;
        }
         lm=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria= new Criteria();
         provider= lm.getBestProvider(criteria,false);
        location=lm.getLastKnownLocation(provider);
        if (location!=null)
        {
            double latitude = location.getLatitude();
            double langitude = location.getLongitude();

            onLocationChange(location);

        }
    }

    public String getLocation(double lat, double lng) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addr = geocoder.getFromLocation(lat, lng, 1);
            StringBuilder stringBuilder = new StringBuilder();
            if (addr.size() > 0) {
                Address address = addr.get(0);
                //stringBuilder.append(address.getSubLocality());

                for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                    stringBuilder.append(address.getAddressLine(i)).append(" ");
                stringBuilder.append(address.getCountryName());

                //stringBuilder.append(address.getLocality()).append(",");
            }
            String addressString = stringBuilder.toString();
            return addressString;
        } catch (IOException e) {
            return "Not found address";
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }
}









