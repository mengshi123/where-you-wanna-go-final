package project.cis350.upenn.edu.wywg;

        import android.content.Intent;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.RadioButton;
        import android.widget.RadioGroup;
        import android.widget.TextView;

        import com.google.firebase.database.DataSnapshot;

        import static project.cis350.upenn.edu.wywg.LoginActivity.usersRef;

public class SettingsActivity extends AppCompatActivity {

    DataSnapshot snap = LoginActivity.snap;
    String username = LoginActivity.username;

    private TextView askEmail;
    private TextView askNumber;
    private TextView showBlackout;
    private RadioGroup emailGroup;
    private RadioButton emailYesButton;
    private RadioButton emailNoButton;
    private RadioGroup numberGroup;
    private RadioButton numberYesButton;
    private RadioButton numberNoButton;
    private boolean showEmail = true;
    private boolean showNumber = true;
    private Button doneBtnSetting;
    private Button cancelBtnSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        askEmail = (TextView) findViewById(R.id.Email_layout);
        emailGroup = (RadioGroup) findViewById(R.id.setting_email);
        emailYesButton=(RadioButton)findViewById(R.id.emailyes);
        emailNoButton=(RadioButton)findViewById(R.id.emailno);
        emailGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void  onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == emailYesButton.getId()) {
                    showEmail = true;
                } else if (checkedId == emailNoButton.getId()) {
                    showEmail = false;
                }
            }
        });

        askNumber = (TextView) findViewById(R.id.number_layout);
        numberGroup = (RadioGroup) findViewById(R.id.setting_phone);
        numberYesButton=(RadioButton)findViewById(R.id.numberyes);
        numberNoButton=(RadioButton)findViewById(R.id.numberno);
        numberGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void  onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId ==numberYesButton.getId()) {
                    showNumber = true;
                } else if (checkedId == numberNoButton.getId()) {
                    showNumber = false;
                }
            }
        });

        showBlackout = (TextView) findViewById(R.id.setting_black_out);
        doneBtnSetting = (Button) findViewById(R.id.setting_doneButton);
        cancelBtnSetting = (Button) findViewById(R.id.setting_cancelButton);

        doneBtnSetting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                LoginActivity.usersRef.child(username).child("showEmail").setValue(showEmail);
                LoginActivity.usersRef.child(username).child("showNumber").setValue(showNumber);
                Intent myIntent = new Intent(SettingsActivity.this, MapsActivity.class);
                SettingsActivity.this.startActivity(myIntent);
            }
        });

        cancelBtnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.this.finish();
            }
        });
    }
}
