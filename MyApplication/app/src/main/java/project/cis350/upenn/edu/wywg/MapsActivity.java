package project.cis350.upenn.edu.wywg;


import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import static project.cis350.upenn.edu.wywg.LocationsListActivity.scaleDownBitmap;
import static project.cis350.upenn.edu.wywg.R.id.map;

;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GoogleMap.InfoWindowAdapter, GoogleMap.OnMapClickListener{

    private GoogleMap mMap;

    private ArrayList<Marker> markersBeen;
    private ArrayList<Marker> markersToGo;
    private ArrayList<Location> locationsBeen;
    private ArrayList<Location> locationsToGo;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    private double home_long;
    private double home_lat;
    private String addressText;
    private LatLng latLng;
    private MarkerOptions markerOptions;
    private String selectedPlace;
    private Boolean markerHighlighted = false;
    private Marker highlightMarker;

    private double myLatitude;
    private double myLongtitude;

    protected Button button_myLoc;
//    protected Button button;
//    protected EditText editText;
    protected Toolbar toolbar;
    DataSnapshot snap;
    String username;
    //    protected SearchView sv;
    private String locationName = "";

    private Button recommendationsB;
    private boolean recommendationsOn = false;
    private boolean recent = false;
    private boolean popular = false;

    private ArrayList<Location> allLocations = new ArrayList<>();
    private ArrayList<Location> recommendationList = new ArrayList<>();
    private ArrayList<Location> recommendationList2 = new ArrayList<>();
    private ArrayList<Marker> markersRecommend;
    private ArrayList<Marker> markersRecommend2;

    // recommendation filters
    RecommendationsFragment recFragment;
    //what we're filtering by
    boolean cost = false;
    boolean rating = false;
    View v;
    double price;
    float ratingNum;
    private Map<String, Integer> positionMap;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        snap = LoginActivity.snap;
        username = LoginActivity.username;
        Iterable<DataSnapshot> locIndices = snap.child("users").child(username).child("locations").getChildren();
        setPosMap(locIndices);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        mDrawerList = (ListView)findViewById(R.id.navList);mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        snap = LoginActivity.snap;
        username = LoginActivity.username;

        locationsBeen = new ArrayList<Location>();
        locationsToGo = new ArrayList<Location>();
        markersBeen = new ArrayList<Marker>();
        markersToGo = new ArrayList<Marker>();
        markersRecommend = new ArrayList<Marker>();
        markersRecommend2 = new ArrayList<Marker>();

        getLocations();

        recommendationsB = (Button) findViewById(R.id.recommendations);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        button_myLoc = (Button) findViewById(R.id.button2);
        GPSTracker gpsTracker = new GPSTracker(MapsActivity.this);

        if(gpsTracker.canGetLocation()) {
            myLatitude = gpsTracker.getLatitude();
            myLongtitude = gpsTracker.getLongitude();
        }
//        findViewById(R.id.map).setOnClickListener(this);
    }

//    public void onClick(View v) {
//        switch(v.getId()) {
//            case R.id.map:
//                highlightMarker.hideInfoWindow();
//                break;
//
//        }
//
//
//
//    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setInfoWindowAdapter(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
        {
            @Override public void onInfoWindowClick(Marker marker) {
                final Location l = getLocationFromMarker(marker);
                int position = 0;
                if(!l.getPics().isEmpty()){
                    l.setP(l.getPics().get(0));

                }
                for(String s : positionMap.keySet()){
                    if(l.getName().equals(s)){
                        position = positionMap.get(s);
                    }
                }
                Intent myIntent = new Intent(MapsActivity.this, LocationActivity.class);
                myIntent.putExtra("loc", Parcels.wrap(l));
                myIntent.putExtra("Position", position + "");
                String pic = (String) snap.child("users").child(username).
                        child("locations").child(position+"").child("pics").child("0").getValue();
                if(pic != null){
                    byte[] imageAsBytes = Base64.decode(pic.getBytes(), Base64.DEFAULT);
                    Bitmap b = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);

                    b = scaleDownBitmap(b, 100, getApplicationContext());

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    b.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    myIntent.putExtra("pic", byteArray);

                }
                MapsActivity.this.startActivity(myIntent);
            }
        });

        // add markers retrieved from database
        for (Location location : locationsBeen) {
            addBeenMarker(location);
        }
        for (Location location : locationsToGo) {
            addToGoMarker(location);
        }

        getAllLocations();
        getRecommendations();
        Map<Location, Integer> popularity = getPopularity();
        getRecommendations2(popularity);

        setAutocompleteFragment();
        setButton_myLoc();
        setButtonRecommendation();

        setNotafication();
    }

    private void setPosMap(Iterable<DataSnapshot> locIndices) {
        positionMap = new HashMap<>();

        int i = 0;
        String s = "";
        for(DataSnapshot dss: locIndices){
            s = (String)dss.child("name").getValue();
            positionMap.put(s, i);
            i++;
        }
    }


    public void setAutocompleteFragment(){
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.getView().setBackgroundColor(Color.WHITE);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                selectedPlace = place.getName().toString();

                Geocoder geocoder = new Geocoder(getBaseContext());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocationName(selectedPlace, 1);
                    if (addresses != null && !addresses.equals(""))
                        search(addresses);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Status status) {
                System.out.println("Error, please try again");
            }
        });
    }

    public void setButton_myLoc(){
        button_myLoc.setOnClickListener(new View.OnClickListener(){

            //button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Geocoder geocoder = new Geocoder(getBaseContext());
                List<Address> addresses;

                try {
                    // Getting a maximum of 3 Address that matches the input
                    // text
                    addresses = geocoder.getFromLocation(myLatitude, myLongtitude, 1);
                    if (addresses != null && !addresses.equals(""))
                        search(addresses);
                } catch (Exception e) {
                }
            }
        });
    }

    public void setButtonRecommendation(){
        recommendationsB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!recommendationsOn) {
                    // show recommendations
                    showRadioButtonDialog();
                } else {
                    // remove recommendations
                    recent = false;
                    popular = false;
                    for (Marker m : markersRecommend) {
                        m.setVisible(false);
                    }
                    for (Marker m : markersRecommend2) {
                        m.setVisible(false);
                    }
                    recommendationsOn = false;
                    rating = false;
                    cost = false;
                }
            }
        });
    }

    public void setNotafication(){
        Intent intent = getIntent();
        String notifLocation = intent.getStringExtra("Location");
        //Toast.makeText(this, notifLocation , Toast.LENGTH_SHORT).show();
        List<Map> locations = (ArrayList<Map>) snap.child("users").child(username).child("locations").getValue();
        if(locations != null){
            for(Map l: locations){
                if(l != null ){
                    String locName = (String) l.get("name");
                    if(locName.equals(notifLocation)&& l.get("lat") != null && l.get("longi") !=  null){
                        latLng = new LatLng((double)l.get("lat"), (double)l.get("longi"));
                        mMap.clear();
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(5));
                    }
                }
            }
        }
    }

    private void addDrawerItems() {
        String[] osArray = { "All Locations", "All Users", "Map", "Profile", "Sign Out"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mDrawerList.getItemAtPosition(position).toString().equals("All Users")) {
                    Intent myIntent = new Intent(MapsActivity.this, UsersListActivity.class);
                    MapsActivity.this.startActivity(myIntent);
                } else if (mDrawerList.getItemAtPosition(position).toString().equals("All Locations")) {
                    Intent myIntent = new Intent(MapsActivity.this, LocationsListActivity.class);
                    MapsActivity.this.startActivity(myIntent);
                } else if (mDrawerList.getItemAtPosition(position).toString().equals("Map")) {
                    Intent myIntent = new Intent(MapsActivity.this, MapsActivity.class);
                    MapsActivity.this.startActivity(myIntent);
                } else if (mDrawerList.getItemAtPosition(position).toString().equals("Profile")) {
                    Intent myIntent = new Intent(MapsActivity.this, UserFormActivity.class);
                    MapsActivity.this.startActivity(myIntent);
                } else if (mDrawerList.getItemAtPosition(position).toString().equals("Sign Out")) {
                    Intent myIntent = new Intent(MapsActivity.this, LoginActivity.class);
                    MapsActivity.this.startActivity(myIntent);
                }
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getSupportActionBar().setTitle("Menu");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent myIntent = new Intent(MapsActivity.this, SettingsActivity.class);
            MapsActivity.this.startActivity(myIntent);
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void getNotification(String name){

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, MapsActivity.class);
        notificationIntent.putExtra("Location", name);

        PendingIntent intent = PendingIntent.getActivity(this, m,
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notif = new Notification.Builder(this)
                .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                .setContentTitle("Added to a new place")
                .setContentText("You have been added to " + name + "!")
                .setContentIntent(intent)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(m, notif);
    }

    private void showRadioButtonDialog() {

        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.radiobutton_dialog);
        List<String> stringList=new ArrayList<>();  // here is list
        stringList.add("Most recent");
        stringList.add("Most popular");
        RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);

        for(int i=0;i<stringList.size();i++){
            RadioButton rb=new RadioButton(this); // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText(stringList.get(i));
            rg.addView(rb);
        }

        dialog.show();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);

                    if (btn.getId() == checkedId) {
                        if (x == 0) {
                            recent = true;
                            popular = false;

                            dialog.cancel();
                            showRecDialog();
                            //dialog.closeOptionsMenu();
                        } else {
                            popular = true;
                            recent = false;

                            dialog.cancel();
                            showRecDialog();
                            //dialog.closeOptionsMenu();
                        }
                        recommendationsOn = true;
                    }
                }
            }
        });
    }

    private void showRecDialog() {
        FragmentManager fm = getSupportFragmentManager();
        recFragment = RecommendationsFragment.newInstance("Some Title");
        recFragment.show(fm, "rec_frag");
    }

    public void toDone(View view) {
        if (recFragment.cbCost.isChecked()) {
            cost = true;
        }
        if (recFragment.cbRating.isChecked()) {
            rating = true;
        }

        System.out.println("cost: " + cost +", " + "rating: " + rating);
        view = recFragment.v;
        if (cost) {
            doCost();
        }

        if (rating) {
            doRating();
        }

        recFragment.dismiss();
        if (recent) {
            doRecent();
        } else if (popular) {
            doPopular();
        }
    }

    public void doCost(){
        EditText etCost = recFragment.etCostFilter;
        if (etCost.getText()!= null) {
            String c = etCost.getText().toString();
            if (c != null && c.length() >= 1) {
                price = Double.parseDouble(c);
            }
        }
        System.out.println("cost" + price);
    }

    public void doRating(){
        float rb = recFragment.rb.getRating();
        ratingNum = rb;
        System.out.println("rating" + ratingNum);
    }

    public void doRecent(){
        //System.out.println()
        for (Marker m : markersRecommend) {
            if (cost) {
                if (getLocationFromMarker(m).getCost() <= price) {
                    m.setVisible(true);
                }
            }
            if (rating) {
                if (getLocationFromMarker(m).getRating() >= ratingNum) {
                    m.setVisible(true);
                }
            }

            if (!cost && ! rating) {
                m.setVisible(true);
            }
        }
    }

    public void doPopular(){
        for (Marker m : markersRecommend2) {
            if (cost) {
                if (getLocationFromMarker(m).getCost() <= price) {
                    m.setVisible(true);
                }
            }
            if (rating) {
                if (getLocationFromMarker(m).getRating() >= ratingNum) {
                    m.setVisible(true);
                }
            }
            if (!cost && !rating) {
                m.setVisible(true);
            }
        }
    }
    protected void search(List<Address> addresses) {

        Address address = (Address) addresses.get(0);
        home_long = address.getLongitude();
        home_lat = address.getLatitude();
        latLng = new LatLng(address.getLatitude(), address.getLongitude());

        addressText = String.format(
                "%s, %s",
                address.getMaxAddressLineIndex() > 0 ? address
                        .getAddressLine(0) : "", address.getCountryName());

        markerOptions = new MarkerOptions();

        markerOptions.position(latLng);
        markerOptions.title(addressText);

        locationName = address.getAddressLine(0);

        mMap.clear();
        //mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        this.highlightMarker = marker;
        marker.showInfoWindow();
        this.markerHighlighted = true;
        return false;
    }



    public void getLocations() {
        //locations insert one
        List<Map> locations = (ArrayList<Map>) snap.child("users").child(username).child("locations").getValue();
        System.out.println("HERE in getLocations()");

        if (locations != null) {
            for (int i = 0; i < locations.size(); i++) {
                if (locations.get(i) != null) {
                    Map loc = locations.get(i);
                    String name = (String) loc.get("name");
                    String description = (String) loc.get("description");

                    String journal = (String) loc.get("journal");
                    boolean been;
                    if (loc.get("been") == null) {
                        been = false;
                    } else {
                        been = (boolean) loc.get("been");
                    }
                    Object latitude1 = ((Map) loc.get("coordinates")).get("latitude");
                    double latitude = ((Number) latitude1).doubleValue();
                    Object longitude1 = ((Map) loc.get("coordinates")).get("longitude");
                    double longitude = ((Number) longitude1).doubleValue();

                    if (loc.get("notified") != null) {
                        if (!(boolean) loc.get("notified")) {
                            getNotification(name);
                            LoginActivity.usersRef.child(username).child("locations").child(i + "").child("notified").setValue(true);
                        }
                    }

                    List<String> pics = (List<String>) loc.get("pics");

                    if (pics == null) {
                        pics = new ArrayList<String>();
                    }

                    List<String> users = (List<String>) loc.get("users");

                    if (users == null) {
                        users = new ArrayList<String>();
                    }

                    //System.out.println("images!! " + pics.toString());

                    Location l = new Location(name, description, latitude, longitude, been, journal);
                    l.setPics(pics);
                    l.setUsers(users);
                    if (been) {
                        addBeenLocation(l);
                    } else {
                        addToGoLocation(l);
                    }
                }
            }
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return prepareInfoView(marker);
    }

    @Override
    public View getInfoContents(Marker marker) {
        return prepareInfoView(marker);
    }

    private View prepareInfoView(Marker marker) {
        final Location l = getLocationFromMarker(marker);
        getWeather(l);

        LinearLayout infoView = new LinearLayout(MapsActivity.this);
        LayoutInflater inflater = LayoutInflater.from(this);

        infoView.setOrientation(LinearLayout.HORIZONTAL);
        infoView.setMinimumWidth(700);
        infoView.setMinimumHeight(300);
        infoView.setBackgroundResource(R.drawable.rounded_corner);

        LinearLayout subInfoView = new LinearLayout(MapsActivity.this);
        subInfoView.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT );

        int sizeInDP = 25;
        int marginInDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sizeInDP, getResources()
                        .getDisplayMetrics());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(30, 20, 30, 0);
        infoView.setBackgroundColor(Color.WHITE);

        TextView name = new TextView(MapsActivity.this);
        name.setText(l.getName());
        name.setTextSize(20);
        name.setTypeface(Typeface.DEFAULT_BOLD);

        TextView weather = new TextView(MapsActivity.this);
        char degree = 0x00B0;
        String w = "Temperature: " + (int) l.getTemperature() + " " + degree + "F";
        weather.setText(w);
        weather.setTextSize(15);

        TextView journal = new TextView(MapsActivity.this);
        journal.setTextSize(15);
        journal.setText("Journal: " + l.getJournal());

        TextView users = new TextView(MapsActivity.this);
        users.setTextSize(15);

        LinearLayout photoInfoView = new LinearLayout(MapsActivity.this);
        photoInfoView.setOrientation(LinearLayout.HORIZONTAL);

        Iterator<String> i = l.getPics().iterator();
        boolean hasPics = false;
        while (i.hasNext()) {
            hasPics = true;
            ImageView infoImageView = new ImageView(MapsActivity.this);

            int width = 200;
            int height = 200;
            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width, height);
            infoImageView.setLayoutParams(parms);

            String base64Image = i.next();
            byte[] imageAsBytes = Base64.decode(base64Image.getBytes(), Base64.DEFAULT);
            Bitmap b = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);

            infoImageView.setImageBitmap(b);
            photoInfoView.addView(infoImageView);
        }

        Iterator<String> i2 = l.getUsers().iterator();
        String u = "";
        while (i2.hasNext()) {
            u += i2.next() + " ";
        }
        users.setText("Users Added: " + u);

        subInfoView.addView(name);
        subInfoView.addView(weather);
        if (!l.getJournal().isEmpty()) {
            subInfoView.addView(journal);
        }
        if (!u.isEmpty()) {
            subInfoView.addView(users);
        }
        subInfoView.addView(photoInfoView);
        infoView.addView(subInfoView, layoutParams);

        return infoView;
    }

    private void getWeather(Location l) {
        WeatherServiceAsync task = new WeatherServiceAsync(l);
        task.execute();
    }

    public Location getLocationFromMarker(Marker marker) {
        Location l = null;

        int i = markersToGo.indexOf(marker);
        if (i != -1) {
            l = locationsToGo.get(i);
        } else {
            i = markersBeen.indexOf(marker);
            if (i != -1) {
                l = locationsBeen.get(i);
            } else {
                i = markersRecommend.indexOf(marker);
                if (i != -1) {
                    l = recommendationList.get(i);
                } else {
                    i = markersRecommend2.indexOf(marker);
                    l = recommendationList2.get(i);
                }
            }
        }
        return l;
    }

    @Override
    public void onMapClick(LatLng point) {
        // TODO - go to Sanjana's form (diff view) and add marker
        if(this.markerHighlighted){
            this.markerHighlighted = false;
            this.highlightMarker.hideInfoWindow();
        }else {
            Intent myIntent = new Intent(MapsActivity.this, LocationFormActivity.class);
            myIntent.putExtra("point", point);
            myIntent.putExtra("lat", point.latitude);
            myIntent.putExtra("long", point.longitude);
            myIntent.putExtra("name", locationName);
            MapsActivity.this.startActivity(myIntent);
        }
    }

    public void toLocationsList(View view) {
        Intent myIntent = new Intent(MapsActivity.this, LocationsListActivity.class);
        MapsActivity.this.startActivity(myIntent);
    }

    public void toUsersList(View view) {
        Intent myIntent = new Intent(MapsActivity.this, UsersListActivity.class);
        MapsActivity.this.startActivity(myIntent);
    }


    public void addBeenLocation(Location l) {
        locationsBeen.add(l);
    }

    public void addToGoLocation(Location l) {
        locationsToGo.add(l);
    }

    public void addBeenMarker(Location l) {
        Marker m = mMap.addMarker(new MarkerOptions().position(l.getCoordinates()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        markersBeen.add(m);
    }

    public void addToGoMarker(Location l) {
        Marker m = mMap.addMarker(new MarkerOptions().position(l.getCoordinates()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        markersToGo.add(m);
    }

    public void getAllLocations() {
        ArrayList<String> users = new ArrayList<>();

        Map<String, Object> m = (Map<String, Object>) snap.child("users").getValue();

        for (Map.Entry<String, Object> entry : m.entrySet()) {
            Map singleUser = (Map) entry.getValue();
            users.add((String) singleUser.get("name"));
        }

        for (String p : users) {
            List<Map> locations = (ArrayList<Map>) snap.child("users").child(p).child("locations").getValue();
            for (int i = 0; i < locations.size(); i++) {
                Map loc = locations.get(i);
                if (loc != null) {
                    String name = (String) loc.get("name");
                    String description = (String) loc.get("description");
                    Object latitude1 = ((Map) loc.get("coordinates")).get("latitude");
                    double latitude = ((Number) latitude1).doubleValue();
                    Object longitude1 = ((Map) loc.get("coordinates")).get("longitude");
                    double longitude = ((Number) longitude1).doubleValue();

                    String journal = (String) loc.get("journal");
                    boolean been = false;

                    if (loc.get("been") != null) {
                        been = (boolean) loc.get("been");
                    }
                    List<String> pics = (List<String>) loc.get("pics");

                    if (pics == null) {
                        pics = new ArrayList<String>();
                    }

                    List<String> withUsers = (List<String>) loc.get("users");

                    if (withUsers == null) {
                        withUsers = new ArrayList<String>();
                    }

                    if (loc.get("timeAdded") != null) {
                        Long lo = (Long) ((Map) loc.get("timeAdded")).get("year");
                        int year = lo.intValue() + 1900;
                        lo = (Long) ((Map) loc.get("timeAdded")).get("month");
                        int month = lo.intValue();
                        lo = (Long) ((Map) loc.get("timeAdded")).get("day");
                        int date = lo.intValue();
                        lo = (Long) ((Map) loc.get("timeAdded")).get("hours");
                        int hour = lo.intValue();
                        lo = (Long) ((Map) loc.get("timeAdded")).get("minutes");
                        int min = lo.intValue();

                        Calendar c = new GregorianCalendar();
                        c.set(year, month, date, hour, min);

                        Location l = new Location(name, description, latitude, longitude, been, journal);
                        l.setPics(pics);
                        l.setUsers(withUsers);
                        l.setCal(c);
                        allLocations.add(l);
                    }
                }
            }
        }
    }

    public void getRecommendations() {
        if (allLocations.size() < 5) {
            recommendationList.addAll(allLocations);
        } else {
            Collections.sort(allLocations);
            for (int i = 0; i < 5; i++) {
                recommendationList.add(allLocations.get(i));
            }
        }


        for (Location l: recommendationList) {
            Marker m = mMap.addMarker(new MarkerOptions().position(l.getCoordinates()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
            markersRecommend.add(m);
            m.setVisible(false);
        }
    }

    public Map<Location, Integer> getPopularity(){
        Map<Location, Integer> popularity = new HashMap<Location, Integer>();
        for (Location l : allLocations) {
            String name = l.getName().toLowerCase();
            Location loc = null;
            if (popularity.size() > 0) {
                for (Location l2 : popularity.keySet()) {
                    System.out.println("lala " + l2);
                    if (name.equals(l2.getName().toLowerCase())) {
                        loc = l2;
                        break;
                    }
                }
            }
            if (loc != null) {
                popularity.put(loc, popularity.get(loc) + 1);
            } else {
                popularity.put(l, 1);
            }
        }

        Set<Location> toRemove = new HashSet<Location>();

        for (Location l : popularity.keySet()) {
            if (popularity.get(l) == 1) {
                toRemove.add(l);
            }
        }

        for (Location l : toRemove) {
            popularity.remove(l);
        }
        return popularity;
    }

    public void getRecommendations2(Map<Location, Integer> popularity) {

        PriorityQueue<Location> pq = new PriorityQueue<Location>();

        if (popularity.size() > 5) {
            for (Location l : popularity.keySet()) {
                if (pq.size() < 5) {
                    pq.add(l);
                } else {
                    if (popularity.get(l) > popularity.get(pq.peek())) {
                        pq.poll();
                        pq.add(l);
                    }
                }
            }

            // move from pq to list
            recommendationList2.addAll(pq);
        } else {
            // move from map to list
            recommendationList2.addAll(popularity.keySet());
        }

        for (Location l : recommendationList2) {
                Marker m = mMap.addMarker(new MarkerOptions().position(l.getCoordinates()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                markersRecommend2.add(m);
                m.setVisible(false);
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }



}