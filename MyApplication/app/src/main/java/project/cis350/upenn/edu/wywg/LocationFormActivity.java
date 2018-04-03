package project.cis350.upenn.edu.wywg;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class LocationFormActivity extends AppCompatActivity implements View.OnClickListener {

    DataSnapshot snap = LoginActivity.snap;
    String username = LoginActivity.username;
    double lat;
    double longi;
    String locName;
    boolean edit;



    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private RatingBar rating;
    private EditText etCost;
    private EditText etDescription;

    private static final int RESULT_LOAD_IMAGE = 1;
    ImageView imageToUpload;
    Uri selectedImage;
    List<Uri> selectedImages;
    List<ImageView> imageViews;
    List<String> oldPics;

    ArrayList<String> addedUsers;
    TextView addUser;

    int imageCounter = 0;

    Button submitB;


    String locType;
    String traPurp;


    Spinner  spinnerLocationType;
    Spinner spinnerTravelPurpose;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_form);


        lat = getIntent().getDoubleExtra("lat", .0001);
        longi = getIntent().getDoubleExtra("long", .0001);
        locName = getIntent().getStringExtra("name");
        edit = getIntent().getBooleanExtra("edit", false);


        selectedImages = new ArrayList<Uri>();
        imageViews = new ArrayList<ImageView>();
        oldPics = new ArrayList<String>();
        addedUsers = new ArrayList<String>();


        radioGroup = (RadioGroup) findViewById(R.id.radioBeenOrToGo);
        etDescription = (EditText) findViewById(R.id.etDescription);
        imageToUpload = (ImageView) findViewById(R.id.imageToUpload);
        rating = (RatingBar) findViewById(R.id.rbRating);
        LayerDrawable stars = (LayerDrawable) rating.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

        etCost = (EditText) findViewById(R.id.etCost);
        addUser = (TextView) findViewById(R.id.addUser);
        imageToUpload.setOnClickListener(this);

        submitB = (Button) findViewById(R.id.submit_button);
        Button picB = (Button) findViewById(R.id.add_pic_button);
        Button userB = (Button) findViewById(R.id.add_user_button);

        final EditText name = (EditText) findViewById(R.id.edit_name);
        name.setText(locName);
        final EditText journal = (EditText) findViewById(R.id.edit_journal);
        //final EditText users = (EditText) findViewById(R.id.edit_users);
//        EditText email = (EditText) findViewById(R.id.edit_email);
//        EditText phone = (EditText) findViewById(R.id.edit_phone);
//        EditText comments = (EditText) findViewById(R.id.edit_notes);
        findViewById(R.id.traceroute_rootview).setOnClickListener(this);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        spinnerLocationType = (Spinner) findViewById(R.id.locationTypes);
        spinnerTravelPurpose = (Spinner) findViewById(R.id.TravelPurpose);



        if (edit) {

            name.setText(getIntent().getStringExtra("name"));
            etDescription.setText(getIntent().getStringExtra("desc"));
            journal.setText(getIntent().getStringExtra("journal"));
            etCost.setText((getIntent().getDoubleExtra("cost", 0)) + "");
            float r = getIntent().getFloatExtra("rating", 0);
            rating.setRating(r);

            boolean been = getIntent().getBooleanExtra("been", false);
            if (!been) {
                radioGroup.check(R.id.radioToGo);
            } else {
                radioGroup.check(R.id.radioBeen);
            }

            List<String> pics = null;

            List<Map> locations = (ArrayList<Map>) snap.child("users").child(username).child("locations").getValue();

            for (int i = 0; i < locations.size(); i++) {
                Map loc = locations.get(i);
                if (loc != null) {
                String locName = (String) loc.get("name");
                if (locName.equals(getIntent().getStringExtra("name"))) {

                    Object latitude1 = ((Map) loc.get("coordinates")).get("latitude");
                    lat = ((Number) latitude1).doubleValue();
                    Object longitude1 = ((Map) loc.get("coordinates")).get("longitude");
                    longi = ((Number) longitude1).doubleValue();

                    addedUsers = (ArrayList<String>) loc.get("users");

                    if (addedUsers == null) {
                        addedUsers = new ArrayList<String>();

                    }

                    pics = (List<String>) loc.get("pics");

                    if (pics == null) break;
                    if (pics.size() == 0) break;

                    //display pics
                    for (String im : pics) {
                        String base64Image = im;
                        byte[] imageAsBytes = Base64.decode(base64Image.getBytes(), Base64.DEFAULT);
                        Bitmap b = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                        ImageView iv = new ImageView(LocationFormActivity.this);
                        iv.setImageBitmap(b);
                        imageCounter++;
                        showPic(iv);
                        oldPics.add(im);
                        imageViews.add(iv);
                        final ImageView iv2 = iv;

                        iv.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                // delete
                                iv2.setImageBitmap(null);

                                // get index
                                int toDelete = -1;
                                for (int i = 0; i < imageViews.size(); i++) {
                                    if (imageViews.get(i).equals(iv2)) {
                                        toDelete = i;
                                        break;
                                    }
                                }
                                if (toDelete != -1) {
                                    imageViews.remove(toDelete);
                                    oldPics.remove(toDelete);
                                }
                                imageCounter--;
                            }
                        });
                    }
                    break;
                }
                }
            }
            displayAddedUser();
        }

        //Listener on Submit button
        submitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ln = name.getText().toString();
                if(ln == null || ln.length() == 0){
                    name.setError("Please enter a name");
                    return;
                }
                if (edit) {
                    // get selected radio button from radioGroup
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    // find the radiobutton by returned id
                    radioButton = (RadioButton) findViewById(selectedId);
                    String radioText = radioButton.getText().toString();
                    boolean been = false;
                    if (radioText.equals("I have been here!")) {
                        been = true;
                    }

                    DatabaseReference locRef = database.getReference("logins").child("users").child(username).child("locations");
                    locType = spinnerLocationType.getSelectedItem().toString();
                    traPurp = spinnerTravelPurpose.getSelectedItem().toString();

                    List<Location> td2 = (ArrayList<Location>) snap.child("users").child(username).child("locations").getValue();
                    System.out.println("map2: " + td2);
                    Location l = new Location(name.getText().toString(), etDescription.getText().toString(), lat, longi, been, journal.getText().toString(), locType, traPurp);
                    l.setRating(rating.getRating());
                    l.setNotified();
                    if (etCost.getText() != null) {
                        String c = etCost.getText().toString();
                        if (c != null && c.length() > 0) {
                            l.setCost(Double.parseDouble(c));
                        }
                    }

                    if (selectedImage != null) {
                        for (Uri im : selectedImages) {
                            addBitmap(l);
                        }
                    }

                    for (String im: oldPics) {
                        l.addPic(im);
                    }

                    // remove old
                    List<Map> locations = (ArrayList<Map>) snap.child("users").child(username).child("locations").getValue();
                    int oldIndex = 0;
                    for (int i = 0; i < locations.size(); i++) {

                        Map loc = locations.get(i);
                        if (loc != null) {
                            String name = (String) loc.get("name");
                            if (name.equals(locName)) {
                                oldIndex = i;
                                System.out.println("name " + name);
                                break;
                            }
                        }
                    }

                    td2.remove(oldIndex);

                    for (String u : addedUsers) {
                        List<Location> u_loc = (ArrayList<Location>) snap.child("users").child(u).child("locations").getValue();
                        u_loc.add(l);
                        // remove old
                        List<Map> locations2 = (ArrayList<Map>) snap.child("users").child(u).child("locations").getValue();
                        int oldIndex2 = 0;
                        for (int i = 0; i < locations2.size(); i++) {

                            Map loc = locations2.get(i);
                            String name = (String) loc.get("name");
                            if (name.equals(locName)) {
                                oldIndex2 = i;
                                System.out.println("name " + name);
                                break;
                            }
                        }

                        td2.remove(oldIndex2);


                        LoginActivity.usersRef.child(u).child("locations").setValue(u_loc);
                        l.addUser(u);
                    }

                    Date d = new Date();
                    l.setTimeAdded(d);
                    td2.add(l);
                    LoginActivity.usersRef.child(username).child("locations").setValue(td2);

                } else {

                    // get selected radio button from radioGroup
                    int selectedId = radioGroup.getCheckedRadioButtonId();

                    // find the radiobutton by returned id
                    radioButton = (RadioButton) findViewById(selectedId);
                    String radioText = radioButton.getText().toString();
                    boolean been = false;
                    if (radioText.equals("I have been here!")) {
                        been = true;
                    }


                    DatabaseReference locRef = database.getReference("logins").child("users").child(username).child("locations");
                    locType = spinnerLocationType.getSelectedItem().toString();
                    traPurp = spinnerTravelPurpose.getSelectedItem().toString();

                    List<Location> td2 = (ArrayList<Location>) snap.child("users").child(username).child("locations").getValue();
                    System.out.println("map2: " + td2);
                    Location l = new Location(name.getText().toString(), etDescription.getText().toString(), lat, longi, been, journal.getText().toString(), locType, traPurp);
                    l.setRating(rating.getRating());
                    l.setNotified();
                    if (etCost.getText() != null) {
                        String c = etCost.getText().toString();
                        if (c != null && c.length() > 0) {
                            l.setCost(Double.parseDouble(c));
                        }
                    }

                    if (selectedImage != null) {

                        for (Uri im : selectedImages) {
                            addBitmap(l);
                        }
                    }
                    for (String u : addedUsers) {
                        List<Location> u_loc = (ArrayList<Location>) snap.child("users").child(u).child("locations").getValue();

                        u_loc.add(l);
                        LoginActivity.usersRef.child(u).child("locations").setValue(u_loc);
                        l.addUser(u);
                    }
                    Date d = new Date();
                    l.setTimeAdded(d);
                    td2.add(l);
                    LoginActivity.usersRef.child(username).child("locations").setValue(td2);
                }

                Intent myIntent = new Intent(LocationFormActivity.this, MapsActivity.class);
                LocationFormActivity.this.startActivity(myIntent);
            }
        });


        picB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                imageCounter++;
            }


        });

        userB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(LocationFormActivity.this, UsersListActivity.class);
                myIntent.putExtra("form", true);
                myIntent.putStringArrayListExtra("users", addedUsers);
                LocationFormActivity.this.startActivityForResult(myIntent, 3);

            }


        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.add_pic_button:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                break;
            case R.id.traceroute_rootview:
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                break;

        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData();
            selectedImages.add(selectedImage);
            //imageToUpload.setImageURI(selectedImage);

            ImageView i = new ImageView(LocationFormActivity.this);
            i.setImageURI(selectedImage);
            showPic(i);

            final ImageView iv = i;

            i.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // delete
                    iv.setImageBitmap(null);

                    // get index
                    int toDelete = -1;
                    for (int i = 0; i < imageViews.size(); i++) {
                        if (imageViews.get(i).equals(iv)) {
                            toDelete = i;
                            break;
                        }
                    }
                    if (toDelete != -1) {
                        imageViews.remove(toDelete);
                        selectedImages.remove(toDelete);
                    }
                    imageCounter--;
                }
            });

        }
        if (requestCode == 3 || resultCode == 3) {
            addedUsers = data.getStringArrayListExtra("addedUsers");
            displayAddedUser();
        }

    }

    private Bitmap uriToBitmap(Uri selectedFileUri) {
        Bitmap image = null;
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            image = BitmapFactory.decodeFileDescriptor(fileDescriptor);


            parcelFileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public boolean userExists(String name) {
        return snap.child("users").child(name).getValue() != null;
    }

    private void showPic(ImageView i){
        LinearLayout picLL = new LinearLayout(LocationFormActivity.this);
        picLL.layout(400, 500, 500, 0);
        picLL.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 200);
        params.topMargin = getWindowManager().getDefaultDisplay().getHeight() - 550;
        params.leftMargin = 5 + 220*(imageCounter-1);
        picLL.setLayoutParams(params);
        picLL.setOrientation(LinearLayout.HORIZONTAL);
        addContentView(i, picLL.getLayoutParams());
    }

    private void displayAddedUser(){
        String printUser = "Added User: ";
        for(String s : addedUsers){
            printUser += s + " ";
        }
        addUser.setText(printUser);
    }

    private void addBitmap(Location l){
        String base64Image = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8; // shrink it down otherwise we will use stupid amounts of memory
        Bitmap bitmap = uriToBitmap(selectedImage);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);
        l.addPic(base64Image);
    }




}
