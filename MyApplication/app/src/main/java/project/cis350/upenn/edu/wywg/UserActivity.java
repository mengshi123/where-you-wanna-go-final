package project.cis350.upenn.edu.wywg;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by sanjanasarkar on 3/23/17.
 */

public class UserActivity extends AppCompatActivity {
    DataSnapshot snap;
    String username;
    Set<String> addedToPlaces = new HashSet<String>();

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        snap = LoginActivity.snap;
        Bundle extras = getIntent().getExtras();
        List<Location> locs;
        String sex;
        String emailAd;
        String number;

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        if (extras != null) {
            TextView usernameView = (TextView)findViewById(R.id.user_profile_name);
            username = extras.getString("username");
            usernameView.setText(username);

            //set 'been' and 'want to go'
            TextView locView = (TextView)findViewById(R.id.locs);
            locs = getLocations(username);

            ArrayList<String> beenLoc = new ArrayList<>();
            ArrayList<String> wantLoc = new ArrayList<>();

            for (Location l : locs) {
                if (l.getBeen()) {
                    beenLoc.add(l.getName());
                } else {
                    wantLoc.add((l.getName()));
                }
            }
            locView.setText("Been: " + formText(beenLoc) + "\n" + "Want to go: " + formText(wantLoc));

            //set places be added to
            TextView addedLocView = (TextView)findViewById(R.id.addedLocs);
            getAllLocations();
            addedLocView.setText("Locations added to: " + formText(addedToPlaces));

            TextView showSex = (TextView)findViewById(R.id.profie_show_sex);
            sex = getSex(username);
            showSex.setText("Gender: " + sex);

            TextView emailView = (TextView)findViewById(R.id.emailAd);
            emailAd = getEmailAd(username);
            Boolean showE = (Boolean)snap.child("users").child(username).child("showEmail").getValue();
            if(showE!= null && showE) {
                emailView.setText("Email Address: " + emailAd);
            }else{
                emailView.setText("Email Address: N/A");
            }

            TextView numberView = (TextView)findViewById(R.id.phoneNumber);
            number = getNumber(username);
            Boolean showN = (Boolean)snap.child("users").child(username).child("showNumber").getValue();
            if(showN != null && showN) {
                numberView.setText("Phone Number: " + number);
            }else{
                numberView.setText("Phone Number: N/A");
            }
        }

        //A new button is added，realized the function to jump to a new page
        Button myButton = (Button)findViewById(R.id.viewDetail);

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(UserActivity.this,LocationsListActivity.class);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });

    }

    private String formText(Collection<String> locations){
        String locText = "";
        for(String s : locations){
            locText += " " + s + ",";
        }

        //delete the final ','
        int textLen = locText.length();
        if(textLen>0){
            locText = locText.substring(0,textLen-1);
        }
        return locText;
    }

    public String getSex(String username){
        String sex = (String) snap.child("users").child(username).child("sex").getValue();
        return sex;
    }

    public String getEmailAd(String username){
        String emailAd = (String) snap.child("users").child(username).child("email").getValue();
        return emailAd;

    }

    public String getNumber(String username){
        String phoneNumber = (String) snap.child("users").child(username).child("number").getValue();
        return phoneNumber;
    }

    public List<Location> getLocations(String username) {
        //locations insert one
        //add all locations(map) under specific username to a list
        List<Map> locations = (ArrayList<Map>) snap.child("users").child(username).child("locations").getValue();
        List<Location> tempLocs = new ArrayList<Location>();

        //extract info in each location(map) and convert it to a Location object
        //add created objects to a list and return this list
        for (int i = 0; i < locations.size(); i++) {
            Map loc = locations.get(i);
            if (loc != null) {
                String name = (String) loc.get("name");
                String description = (String) loc.get("description");
                Map coord = (Map) loc.get("coordinates");
                double latitude = ((Number)coord.get("latitude")).doubleValue();
                double longitude = ((Number)coord.get("longitude")).doubleValue();
                //Object latitude1 = ((Map) loc.get("coordinates")).get("latitude");
                //double latitude = ((Number) latitude1).doubleValue();
                // Object longitude1 = ((Map) loc.get("coordinates")).get("longitude");
                //double longitude = ((Number) longitude1).doubleValue();
                String journal = (String) loc.get("journal");
                boolean been = false;
                if (loc.get("been") != null) {
                    been = (boolean) loc.get("been");
                }
                tempLocs.add(new Location(name, description, latitude, longitude, been, journal));
            }
        }
        return tempLocs;
    }
    
    public void getAllLocations() {
        //get a list of all the users
        ArrayList<String> users = new ArrayList<>();
        Map<String, Object> m = (Map<String, Object>) snap.child("users").getValue();
        for (Map.Entry<String, Object> entry : m.entrySet()) {
            Map singleUser = (Map) entry.getValue();
            users.add((String) singleUser.get("name"));
        }

        for (String p : users) {
            //look for locations under specific username
            List<Map> locations = (ArrayList<Map>) snap.child("users").child(p).child("locations").getValue();
            for (int i = 0; i < locations.size(); i++) {
                Map loc = locations.get(i);
                //search for withUsers match current username
                if (loc != null) {
                    List<String> withUsers = (List<String>) loc.get("users");
                    if (withUsers != null && !withUsers.isEmpty()) {
                        for (String s : withUsers) {
                            if (s.equals(username)) {
                                addedToPlaces.add((String) loc.get("name"));
                            }
                        }
                    }
                }
            }
        }
    }

    static Class matchClass(String position,String[] list){
        final ArrayList<String> osArray = new ArrayList<String>(Arrays.asList(list));
        final Class[] listClass = {LocationsListActivity.class, UsersListActivity.class, MapsActivity.class, LoginActivity.class};
        int index = osArray.indexOf(position);
        return listClass[index];
    }

    private void addDrawerItems() {
        final String[] osArray = { "All Locations", "All Users", "Map", "Sign Out"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nowPosition = mDrawerList.getItemAtPosition(position).toString();
                Intent myIntent = new Intent(UserActivity.this, matchClass(nowPosition,osArray));
                UserActivity.this.startActivity(myIntent);
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
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
