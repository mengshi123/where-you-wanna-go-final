package project.cis350.upenn.edu.wywg;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.parceler.Parcels;

public class LocationActivity extends AppCompatActivity {
    TextView tvName;
    TextView tvJournal;
    TextView tvDescription;
    TextView tvRating;
    TextView tvCost;
    Intent intent;
    byte[] byteArray;

    Button editButton;
    Button deleteButton;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        tvName = (TextView)findViewById(R.id.tvName);
        tvJournal = (TextView)findViewById(R.id.tvJournal);
        tvDescription = (TextView)findViewById(R.id.tvDescription);
        tvRating = (TextView)findViewById(R.id.tvRating);
        tvCost = (TextView)findViewById(R.id.tvCost);
        final Location l = (Location) Parcels.unwrap(getIntent().getParcelableExtra("loc"));
        final String position = getIntent().getStringExtra("Position");
        username = getIntent().getStringExtra("username");
        tvName.setText(l.getName());
        if (!l.getJournal().isEmpty()) {
            tvJournal.setText(l.getJournal());
        } else {
            tvJournal.setText("No journal entries.");
        }
        tvDescription.setText(l.getDescription());
        intent = getIntent();
        editButton = (Button) findViewById(R.id.edit_button);
        deleteButton = (Button) findViewById(R.id.delete_button);

        System.out.println("Specific" + tvRating +"," + l.getRating());
        tvRating.setText("Rating: " + l.getRating());

        tvCost.setText("Cost: " + l.getCost());

        mDrawerList = (ListView)findViewById(R.id.navList);mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
//        Spinner spinner = (Spinner) findViewById(R.id.spinner);
//// Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.past, android.R.layout.simple_spinner_item);
//// Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//// Apply the adapter to the spinner
//        spinner.setAdapter(adapter);



        ImageView iv = (ImageView) findViewById(R.id.image1);
        //String[] pics = intent.getStringArrayExtra("pic");
//        List<String> lPics= intent.getStringArrayListExtra("pic");
//        String pic;
        //String pic = intent.getStringExtra("pic");

        if(getIntent().getByteArrayExtra("pic")!=null){

//            String fName = getIntent().getStringExtra("filename");
//            String path = Environment.getExternalStorageDirectory() + fName + ".png";
//            Bitmap bmp = BitmapFactory.decodeFile(path);

            byteArray = getIntent().getByteArrayExtra("pic");
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            iv.setImageBitmap(bmp);
        }
        else{
            //Toast.makeText(this, "no", Toast.LENGTH_SHORT).show();
        }



//        String pic = "";
//        if (pics != null) {
//            if(pic!=null){
//                Toast.makeText(this, "yes" , Toast.LENGTH_SHORT).show();
//
//                String base64Image = pic;
//                byte[] imageAsBytes = Base64.decode(base64Image.getBytes(), Base64.DEFAULT);
//                Bitmap b = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
//                iv.setImageBitmap(b);
//            }


//            Iterator<String> i = lPics.iterator();
//            LinearLayout photoInfoView = new LinearLayout(LocationActivity.this);
//            photoInfoView.setOrientation(LinearLayout.HORIZONTAL);
//            while (i.hasNext()) {
//                ImageView infoImageView = new ImageView(LocationActivity.this);
////            infoImageView.setMaxHeight(50);
////            infoImageView.setMaxWidth(50);
//
//                int width = 200;
//                int height = 200;
//                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width, height);
//                infoImageView.setLayoutParams(parms);
//
//                String base64Image = i.next();
//                byte[] imageAsBytes = Base64.decode(base64Image.getBytes(), Base64.DEFAULT);
//                Bitmap b = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
//
//                iv.setImageBitmap(b);
//                infoImageView.setImageBitmap(b);
//                photoInfoView.addView(infoImageView);
//            }



//        else {
//            Toast.makeText(this, "no", Toast.LENGTH_SHORT).show();
//        }
        LinearLayout photoInfoView = new LinearLayout(LocationActivity.this);
//        photoInfoView.setOrientation(LinearLayout.HORIZONTAL);
//        ImageView infoImageView = (ImageView)findViewById(R.id.ivImage);
//            infoImageView.setMaxHeight(50);
//            infoImageView.setMaxWidth(50);

//        int width = 200;
//        int height = 200;
//        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width, height);
//        infoImageView.setLayoutParams(parms);



//        infoImageView.setImageBitmap(b);



        editButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(username.equals(LoginActivity.username)){
                    Intent myIntent = new Intent(LocationActivity.this, LocationFormActivity.class);
                    myIntent.putExtra("edit", true);
                    myIntent.putExtra("name", l.getName());
                    myIntent.putExtra("desc", l.getDescription());
                    myIntent.putExtra("journal", l.getJournal());
                    myIntent.putExtra("cost", l.getCost());
                    myIntent.putExtra("rating", (float) l.getRating());
                    myIntent.putExtra("been", l.getBeen());
                    // rating, been/togo
                    LocationActivity.this.startActivity(myIntent);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Without Permission!", Toast.LENGTH_SHORT).show();
                }



            }

        });

        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(username.equals(LoginActivity.username)){
                    Toast.makeText(LocationActivity.this, "You will miss me!", Toast.LENGTH_SHORT).show();

                    Intent myIntent = new Intent(LocationActivity.this, LocationsListActivity.class);
                    myIntent.putExtra("Delete", "delete");
                    myIntent.putExtra("Position", position);
                    LocationActivity.this.startActivity(myIntent);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Without Permission!", Toast.LENGTH_SHORT).show();
                }

            }

        });

    }

    private void addDrawerItems() {
        final String[] osArray = { "All Locations", "All Users", "Map", "Sign Out"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nowPosition = mDrawerList.getItemAtPosition(position).toString();
                Intent myIntent = new Intent(LocationActivity.this, UserActivity.matchClass(nowPosition,osArray));
                LocationActivity.this.startActivity(myIntent);
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

//    static Class matchClass(String position,String[] list){
//        final ArrayList<String> osArray = new ArrayList<String>(Arrays.asList(list));
//        final Class[] listClass = {LocationsListActivity.class, UsersListActivity.class, MapsActivity.class, LoginActivity.class};
//        int index = osArray.indexOf(position);
//        return listClass[index];
//    }
}

