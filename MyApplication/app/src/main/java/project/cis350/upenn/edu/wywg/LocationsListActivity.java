package project.cis350.upenn.edu.wywg;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationsListActivity extends AppCompatActivity {
    DataSnapshot snap;
    String username;
    private ArrayList<Location> locationsBeen = new ArrayList<Location>();
    LocationsAdapter adapter;
    ArrayList<Location> arrayOfLocations;
    private boolean east = false;
    private boolean north = false;
    private boolean filterOn = false;

    FilterFragment editNameDialogFragment;
    //what we're filtering by
    boolean cost = false;
    boolean rating = false;
    boolean hemisphere = false;
    boolean past = false;
    View view;
    String locationType; //where
    String travelPurpose; //what

    boolean type = false;  //where
    boolean purpose = false;  //what
    double price = 0;
    double ratingNum = 0;
    boolean beenBefore = false;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private Map<Integer, String> positionMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations_list);
        snap = LoginActivity.snap;
        if(getIntent().getStringExtra("username")==null){
            username = LoginActivity.username;
        }
        else{
            username = getIntent().getStringExtra("username");
        }

        /**
        positionMap = new HashMap<>();
//        List<Map> locations = (ArrayList<Map>) snap.child("users").child(username).child("locations").getValue();
        Iterable<DataSnapshot> locIndices = snap.child("users").child(username).child("locations").getChildren();
        int i = 0;
        for(DataSnapshot dss: locIndices){
            positionMap.put(i, dss.getKey());
            i++;
        }
         **/

        Iterable<DataSnapshot> locIndices = snap.child("users").child(username).child("locations").getChildren();
        setPosMap(locIndices);

        if(getIntent()!= null){
           // Toast.makeText(LocationsListActivity.this, "deleted", Toast.LENGTH_SHORT).show();
            if(getIntent().getStringExtra("Delete")!=null) {
                if (getIntent().getStringExtra("Delete").equals("delete")) {
                    snap.child("users").child(username).child("locations").
                            child(positionMap.get(Integer.parseInt(getIntent().getStringExtra("Position")))).getRef().removeValue();
                    Intent myIntent = new Intent(LocationsListActivity.this, LocationsListActivity.class);
                    LocationsListActivity.this.startActivity(myIntent);
                }
            }
        }

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        arrayOfLocations = new ArrayList<Location>();
        adapter = new LocationsAdapter(this, arrayOfLocations);
        ListView listView = (ListView) findViewById(R.id.lvItems);
        listView.setAdapter(adapter);
        adapter.addAll(locationsBeen);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**@Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Location l = (Location) parent.getItemAtPosition(position);
            if(!l.getPics().isEmpty()){
            l.setP(l.getPics().get(0));

            }
            Intent myIntent = new Intent(LocationsListActivity.this, LocationActivity.class);
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

            LocationsListActivity.this.startActivity(myIntent);
            }
             **/

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Location l = (Location) parent.getItemAtPosition(position);
                if(!l.getPics().isEmpty()){
                    l.setP(l.getPics().get(0));

                }
                Intent myIntent = new Intent(LocationsListActivity.this, LocationActivity.class);
                myIntent.putExtra("loc", Parcels.wrap(l));
                myIntent.putExtra("Position", position + "");
                // put the location owner's username into the intent
                myIntent.putExtra("username", username);

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

                LocationsListActivity.this.startActivity(myIntent);
            }
        });
    }


    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h= (int) (newHeight*densityMultiplier);
        int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));

        photo=Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }

    // the criteria of the filtering process
    public void toDone(View view) {
        if (editNameDialogFragment.cbEast.isChecked()) {
            east = true;
        } else {
            east = false;
        }
        if (editNameDialogFragment.cbNorth.isChecked()) {
            north = true;
        } else {
            north = false;
        }
        if (editNameDialogFragment.cbFilterOn.isChecked()) {
            filterOn = true;
        } else {
            filterOn = false;
        }
        //get the item chosen in spinner
        locationType = editNameDialogFragment.spinnerLocationType.getSelectedItem().toString(); //this is where
        travelPurpose = editNameDialogFragment.spinnerTravelPurpose.getSelectedItem().toString();  //this is what

        //Toast.makeText(getApplicationContext(),locationType+" "+type+" "+travelPurpose+" "+purpose, Toast.LENGTH_SHORT).show();

        view = editNameDialogFragment.v;

        int selectedId = editNameDialogFragment.rg.getCheckedRadioButtonId();

        // find the radiobutton by returned id

        RadioButton radioButton = (RadioButton) view.findViewById(selectedId);
        String radioText = "";
        if (radioButton != null) {
            radioText = radioButton.getText().toString();
        }

        cost = false;
        rating = false;
        hemisphere = false;
        past = false;
        beenBefore = false;
        if (radioText.equals("Cost")) {
            cost = true;
            EditText etCost = editNameDialogFragment.etCostFilter;
            if (etCost.getText()!= null) {
                String c = etCost.getText().toString();
                if (c != null && c.length() >= 1) {
                    price = Double.parseDouble(c);
                }
            }
        }
        else if (radioText.equals("Rating")) {
            float rb = editNameDialogFragment.rb.getRating();
            ratingNum = rb;
            rating = true;
        }
        else if (radioText.equals("Where You've Been/Want To Go")) {
            past = true;
            if (editNameDialogFragment.beenThere.isChecked()) {
                beenBefore = true;
            }
        }
        else if (radioText.equals("Hemisphere")) {
            hemisphere = true;
        }
        if (!travelPurpose.equals("Default")){
            this.purpose = true;
        }
        if (!locationType.equals("Default")){
            this.type = true;
        }




        //Toast.makeText(getApplicationContext(),locationType+" "+type+" "+travelPurpose+" "+purpose, Toast.LENGTH_SHORT).show();

        editNameDialogFragment.dismiss();
        loadLocations();

    }

    // retrieve location info under specific username in db
    // create corresponding location objects and add them in to an ArrayList
    public List<Location> getLocations() {
        //locations insert one
        List<Map> locations = (ArrayList<Map>) snap.child("users").child(username).child("locations").getValue();

        List<Location> tempLocs = new ArrayList<Location>();
        for (int i = 0; i < locations.size(); i++) {
            Map loc = locations.get(i);
            if (loc != null) {
                String name = (String) loc.get("name");
                String description = (String) loc.get("description");
                if (description.isEmpty()) {
                    description = "This location does not have a description.";
                }
                Object latitude1 = ((Map) loc.get("coordinates")).get("latitude");
                double latitude = ((Number) latitude1).doubleValue();
                Object longitude1 = ((Map) loc.get("coordinates")).get("longitude");
                double longitude = ((Number) longitude1).doubleValue();
                double rating = 0;
                double cost = 0;
                Object rating1 = (loc.get("rating"));
                if (rating1 != null) {
                    rating = ((Number) rating1).doubleValue();
                }

                Object cost1 = (loc.get("cost"));
                if (cost1 != null) {
                    cost = ((Number) cost1).doubleValue();
                }


                String journal = (String) loc.get("journal");
                boolean been = false;

                if (loc.get("been") != null) {
                    been = (boolean) loc.get("been");
                }
                String where = (String) loc.get("where");
                String what =  (String) loc.get("what");
                Location loc1 = new Location(name, description, latitude, longitude, been, journal, where, what);
                loc1.setRating(rating);
                loc1.setCost(cost);
                tempLocs.add(loc1);


            }
        }
        return tempLocs;
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.clear();
        adapter.addAll(getLocations());
    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        editNameDialogFragment = FilterFragment.newInstance("Some Title");
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }

    // load the locations which satisfy the filter condition
    private void loadLocations() {
        arrayOfLocations.clear();
        adapter.notifyDataSetChanged();
        List<Location> included = new ArrayList<Location>();
        if (filterOn) {
            for (Location location : getLocations()) {
                double lat = location.getLat();
                double longi = location.getLongi();
                double cost = location.getCost();
                double rating = location.getRating();
                boolean been = location.getBeen();
                boolean e = false;
                boolean n = false;
                String type = location.getWhere();
                String purpose = location.getWhat();

                if (this.cost) {
                    if (cost < price) {
                        included.add(location);
                    }
                } else if (this.rating) {
                    if (rating >= ratingNum) {
                        included.add(location);
                    }
                } else if (past) {
                    if (beenBefore) {
                        if (been) {
                            included.add(location);
                        }
                    } else {
                        if (!been) {
                            included.add(location);
                        }
                    }
                }

                else if (hemisphere) {
                    if (longi > 0) {
                        e = true;
                    }
                    if (lat > 0) {
                        n = true;
                    }
                    if (east == e && north == n) {
                        included.add(location);
                    }
                }
                if(this.type || this.purpose){
                    if(this.type && this.purpose) {
                        if (locationType.equals(type) && travelPurpose.equals(purpose)){
                            included.add(location);
                        }
                    else if(this.type){
                            if(locationType.equals(type)){
                                included.add(location);
                            }
                        }
                    else if(this.purpose){
                            if(travelPurpose.equals(purpose)){
                                included.add(location);
                            }
                        }
                    }
                }
            }
        } else {
            included = getLocations();
        }

        arrayOfLocations.addAll(included);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_filter:
                showEditDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void addDrawerItems() {
        final String[] osArray = { "All Locations", "All Users", "Map", "Sign Out"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nowPosition = mDrawerList.getItemAtPosition(position).toString();
                Intent myIntent = new Intent(LocationsListActivity.this, UserActivity.matchClass(nowPosition,osArray));
                LocationsListActivity.this.startActivity(myIntent);
            }
        });
    }

    /**
    private void addDrawerItems() {
        String[] osArray = { "All Locations", "All Users", "Map", "Sign Out"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mDrawerList.getItemAtPosition(position).toString().equals("All Users")) {
                    Intent myIntent = new Intent(LocationsListActivity.this, UsersListActivity.class);
                    LocationsListActivity.this.startActivity(myIntent);
                } else if (mDrawerList.getItemAtPosition(position).toString().equals("All Locations")) {
                    Intent myIntent = new Intent(LocationsListActivity.this, LocationsListActivity.class);
                    LocationsListActivity.this.startActivity(myIntent);
                } else if (mDrawerList.getItemAtPosition(position).toString().equals("Map")) {
                    Intent myIntent = new Intent(LocationsListActivity.this, MapsActivity.class);
                    LocationsListActivity.this.startActivity(myIntent);
                } else if (mDrawerList.getItemAtPosition(position).toString().equals("Sign Out")) {
                    Intent myIntent = new Intent(LocationsListActivity.this, LoginActivity.class);
                    LocationsListActivity.this.startActivity(myIntent);
                }
            }
        });
    }
     **/

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
                getSupportActionBar().setTitle(mActivityTitle);
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

    private void setPosMap(Iterable<DataSnapshot> locIndices) {
        positionMap = new HashMap<>();

        int i = 0;
        for(DataSnapshot dss: locIndices){
            positionMap.put(i, dss.getKey());
            i++;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.locations_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                arrayOfLocations.clear();
                adapter.notifyDataSetChanged();
                ArrayList<Location> included = new ArrayList<Location>();
                for (Location location : getLocations()) {
                    String q = query.toLowerCase();
                    String d = location.getDescription().toLowerCase();
                    String n = location.getName().toLowerCase();
                    if (d.contains(q) || n.contains(q)) {
                        included.add(location);
                    }
                }


                arrayOfLocations.addAll(included);
                adapter.notifyDataSetChanged();
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


}
