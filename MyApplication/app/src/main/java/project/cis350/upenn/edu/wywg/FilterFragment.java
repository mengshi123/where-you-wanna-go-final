package project.cis350.upenn.edu.wywg;

/**
 * Created by ramyarao on 3/16/2017.
 */
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
// ...

public class FilterFragment extends DialogFragment {

    public boolean east;
    public boolean north;
    public boolean filterOn;
    public CheckBox cbEast;
    public CheckBox cbNorth;
    public CheckBox cbFilterOn;
    public CheckBox beenThere;
    public EditText etCostFilter;
    boolean cost = false;
    boolean rating = false;
    boolean hemisphere = false;
    boolean past = false;
    RatingBar rb;
    RadioGroup rg;
    RadioButton radioButton;
    View v;
    Spinner spinnerLocationType;
    Spinner spinnerTravelPurpose;

    public FilterFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static FilterFragment newInstance(String title) {
        FilterFragment frag = new FilterFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_filter, container);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        v = view;
        cbEast = (CheckBox) view.findViewById(R.id.cbEast);
        cbNorth = (CheckBox) view.findViewById(R.id.cbNorth);
        cbFilterOn = (CheckBox) view.findViewById(R.id.cbFilterOn);
        rb = (RatingBar) view.findViewById(R.id.ratingBar);
        rg = (RadioGroup) view.findViewById(R.id.rgFilterBy);
        beenThere = (CheckBox) view.findViewById(R.id.cbBeenHere);
        etCostFilter = (EditText) view.findViewById(R.id.etCostFilter);
        int selectedId = rg.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        radioButton = (RadioButton) view.findViewById(selectedId);
        //String radioText = radioButton.getText().toString();
        spinnerLocationType = (Spinner) view.findViewById(R.id.locationTypes);
        spinnerTravelPurpose = (Spinner) view.findViewById(R.id.travelPurposes);

//        cost = false;
//        rating = false;
//        hemisphere = false;
//        past = false;
//        if (radioText.equals("Cost")) {
//            cost = true;
//        } else if (radioText.equals("Rating")) {
//            rating = true;
//        } else if (radioText.equals("Where You've Been/Want To Go")) {
//            past = true;
//        } else if (radioText.equals("Hemisphere")) {
//            hemisphere = true;
//        }


        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}