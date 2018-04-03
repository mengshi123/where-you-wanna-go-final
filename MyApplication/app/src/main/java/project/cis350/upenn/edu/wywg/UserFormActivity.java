package project.cis350.upenn.edu.wywg;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileDescriptor;
import java.io.IOException;

//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;

public class UserFormActivity extends AppCompatActivity {

    DataSnapshot snap = LoginActivity.snap;
    String username = LoginActivity.username;

    boolean edit;
    String name;
    String emailAddress, introSt, phoneNumberSt;
    Uri selectedProfileImage;
    Uri outputImage;

    private TextView nameText;
    private EditText email;
    private EditText phoneNumber;
    private EditText intro;
    private Button doneBtn;
    private Button cancelBtn;
    private Button photoBtn;
    private StorageReference mFirebaseStorage;

    private ImageView profilePhoto;

    private static final int RESULT_LOAD_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);
        email = (EditText) findViewById(R.id.edit_Email);
        phoneNumber = (EditText) findViewById(R.id.edit_number);
        intro = (EditText) findViewById(R.id.edit_introduction);
        photoBtn = (Button) findViewById(R.id.edit_pic);
        doneBtn = (Button) findViewById(R.id.doneButton);
        cancelBtn = (Button) findViewById(R.id.cancelButton);
        profilePhoto = (ImageView) findViewById(R.id.profilePhoto);
        nameText = (TextView) findViewById(R.id.profileName);

        mFirebaseStorage = FirebaseStorage.getInstance().getReference().child("Profile_photos_17");

        showAllTextPic();

        photoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_PHOTO);
            }

        });



        doneBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                emailAddress = email.getText().toString();
                introSt = intro.getText().toString();
                phoneNumberSt = phoneNumber.getText().toString();
                LoginActivity.usersRef.child(username).child("email").setValue(emailAddress);
                LoginActivity.usersRef.child(username).child("number").setValue(phoneNumberSt);
                LoginActivity.usersRef.child(username).child("intro").setValue(introSt);
                Intent myIntent = new Intent(UserFormActivity.this, MapsActivity.class);
                UserFormActivity.this.startActivity(myIntent);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserFormActivity.this.finish();
            }
        });


        edit = getIntent().getBooleanExtra("edit", false);

        nameText.setText(username);
        if(edit){

        }else{

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RESULT_LOAD_PHOTO && resultCode == RESULT_OK && data != null) {
//            CropImage.activity()
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .start(this);
            selectedProfileImage = data.getData();
            profilePhoto.setImageURI(selectedProfileImage);
            StorageReference imagePath = mFirebaseStorage.child("Profile_photos_17")
                        .child(selectedProfileImage.getLastPathSegment());
//            imagePath.putFile(selectedProfileImage);
//            LoginActivity.usersRef.child(username).child("photo").setValue(selectedProfileImage.toString());
//            Uri file = selectedProfileImage;
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpeg")
                    .build();
            UploadTask uploadTask = imagePath.putFile(selectedProfileImage, metadata);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UserFormActivity.this, "wocaonimabiyoubuxing", Toast.LENGTH_LONG).show();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    LoginActivity.usersRef.child(username).child("photo").setValue(selectedProfileImage.toString());
                    Toast.makeText(UserFormActivity.this, "Saved!!!!", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public void showAllTextPic(){
//        if(snap.child("users").child(username).hasChild("photo")) {
//
//            mFirebaseStorage.child(snap.child("users").child(username).child("photo").getValue().toString());
//            outputImage = Uri.fromFile(localFile);
//        }
        if(snap.child("users").child(username).hasChild("email")) {
            emailAddress = snap.child("users").child(username).child("email").getValue().toString();
        }
        if(snap.child("users").child(username).hasChild("number")) {
            phoneNumberSt = snap.child("users").child(username).child("number").getValue().toString();
        }
        if(snap.child("users").child(username).hasChild("intro")) {
            introSt = snap.child("users").child(username).child("intro").getValue().toString();
        }
        if(outputImage != null){
            profilePhoto.setImageURI(outputImage);
        }
        if(emailAddress != null && !emailAddress.equals("")){
            email.setText(emailAddress);
        }

        if(phoneNumberSt != null && !phoneNumberSt.equals("")){
            phoneNumber.setText(phoneNumberSt);
        }

        if(introSt != null && !introSt.equals("")){
            intro.setText(introSt);
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
}
