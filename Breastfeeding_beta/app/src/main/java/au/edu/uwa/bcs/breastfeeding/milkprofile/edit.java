package au.edu.uwa.bcs.breastfeeding.milkprofile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.yifeilyf.breastfeeding_beta.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
 * lwb means left breast feeds weight before
 * lwa means left breast feeds weight after
 * wb means right breast feeds weight before
 * wa means right breast feeds weight after
 */

public class edit extends Activity {
    //Feed object to be edited
    Feed receivedFeed;

    //Setup UI interfaces
    private TextView resultTime1, resultTime2;
    private TextView resultDate1, resultDate2;
    //private TextView FeedType;
    //private Button btnLeftFeedType;
    //private Button btnRightFeedType;
    private TextView WeightBefore;
    private TextView WeightAfter;
    private Button btnFeedBreast;
    private Button btnFeedExpressed;
    private Button btnFeedSupplement;
    private Button btnRight;
    private Button btnLeft;
    private Button btnBoth;




    //setup UI flags and config variabls

    private int isEdit = 0; //new feed = 0, edit = 1
    private boolean ignoreWeightWarning = false;
    private long minDate;
    private long maxDate;
    private boolean studyPeriodWarning = false;
    private AlertDialog diag = null;
    //used to reset warnings


    //Data variables
    private SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm");
    private Calendar varStartDate = Calendar.getInstance();
    private Calendar varEndDate = Calendar.getInstance();
    private String selectedFeedType = ""; //0,1,2 = breastfeed,expressed, supplementary
    private String selectedFeedSubType = ""; // 0,1 = left/right/both or expressed/formula


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.setContentView(R.layout.edit);
        setContentView(R.layout.edit);

        //Assign views to UI interface variables
        //Timepicker and Datepicker shows on TextView
        resultTime1 = (TextView) findViewById(R.id.SelectTime1);
        resultTime2 = (TextView) findViewById(R.id.SelectTime2);
        resultDate1 = (TextView) findViewById(R.id.SelectDate1);
        resultDate2 = (TextView) findViewById(R.id.SelectDate2);

        WeightBefore = (TextView) findViewById(R.id.WeightBefore);
        WeightAfter = (TextView) findViewById(R.id.WeightAfter);

        //btnLeftFeedType = (Button) findViewById(R.id.btnBreast1);
        //btnRightFeedType = (Button) findViewById(R.id.btnBreast2);

        btnFeedBreast = (Button) findViewById(R.id.btnType1);
        btnFeedExpressed = (Button) findViewById(R.id.btnType3);
        btnFeedSupplement = (Button) findViewById(R.id.btnType2);
        btnLeft = (Button) findViewById(R.id.btnLeft);
        btnRight = (Button) findViewById(R.id.btnRight);
        btnBoth = (Button) findViewById(R.id.btnBoth);

        //Receive the feed to be edited and load current data to the UI
        Intent intent = getIntent();

        //Extract data from the intent
        minDate = intent.getLongExtra("com.yifeilyf.breastfeeding_beta.minDate",-1);
        maxDate = intent.getLongExtra("com.yifeilyf.breastfeeding_beta.maxDate", -1);
        receivedFeed = intent.getParcelableExtra("com.yifeilyf.breastfeeding_beta.newFeed");
        isEdit = intent.getIntExtra("com.yifeilyf.breastfeeding_beta.editRequestCode", 0);

        System.out.println(receivedFeed.getWeightBefore());

        //if request code is 1 the feed can be loaded and editing disabled until edit option is selected
        if(isEdit == 1){
            //load saved feed details to the page
            loadFeed(receivedFeed);

            //initialise feed type buttons
            displayFeedTypeButtons();

            final Button btnDone = (Button) findViewById(R.id.DoneButton);
            btnDone.setText("Edit");



            //disable editing of the ui
            toggleEdit(false);
        } else {
            //initialise the feed type buttons with default settings
            displayFeedTypeButtons();
        }

        //Cancel button, return to list page when it is clicked
        Button btnCancel = (Button) findViewById(R.id.CancelButton);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), list.class);
                //result cancelled tells the list page not to change the list or feeds
                setResult(Activity.RESULT_CANCELED,intent);
                finish();
            }
        });


        //Done Button, pass data to a TextView
        final Button btnDone = (Button) findViewById(R.id.DoneButton);

        btnDone.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                //Calls function to save all data to the feed and return it to the list screen
                //if a feed is being edited
                if(isEdit == 1){
                    isEdit = 0; //set isEdit to 0 so that code runs as if it is a new feed
                    Button delete = (Button) findViewById(R.id.btnDelete);
                    delete.setVisibility(View.VISIBLE);
                    //enable editing of the ui
                    toggleEdit(true);
                    btnDone.setText("Done");
                }else{
                    //remove all warning flags from the UI
                    resetErrors();
                    //Run data validation until all data is valid
                    if (validateFeed()) {
                        System.out.println("FEED VALIDATED");
                        saveAndReturn();
                    }
                }
            }
        });


        //locked screen on portraity
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //hide the keyboard when click outside the EditView
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.edit);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent ev) {
                hideKeyboard(view);
                return false;
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        //result cancelled tells the list page not to change the list or feeds
        setResult(Activity.RESULT_CANCELED,intent);
        super.finish();
    }

    /**
     * Method to toggle editing of the feed
     * @param toggle true to enable false to disable
     */
    private void toggleEdit(boolean toggle){
        //iteration through the whole activity and find EditView and Button, then disable them
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.edit);
        for(int i = 0; i<layout.getChildCount(); i++){
            View edit = layout.getChildAt(i);
            if(edit instanceof Button){
                ((Button)edit).setEnabled(toggle);
            } else if(edit instanceof EditText){
                ((EditText)edit).setEnabled(toggle);
            }
        }
    }

    /**
     * method used to reset all error flags on the UI
     */
    private void resetErrors(){
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.edit);
        for(int i = 0; i<layout.getChildCount(); i++){
            View edit = layout.getChildAt(i);
            if(edit instanceof Button){
                ((Button) edit).setError(null);
            } else if(edit instanceof EditText){
                ((EditText)edit).setError(null);
            }
        }
    }
    /**
     * The validation method for the feed, will generate prompts if the feed is not valid
     * @return true if feed is valid else false
     */
    private boolean validateFeed(){
        boolean validated = true;
        EditText wb = (EditText)findViewById(R.id.TextWB);
        EditText wa = (EditText)findViewById(R.id.TextWA);

        //setup warning prompt
        final AlertDialog.Builder validationWarning = new AlertDialog.Builder(edit.this);
        //validationWarning.setTitle("Invalid Input");
        validationWarning.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        /*
         * First test all fields are complete
         * Then check data validations
         */
        //date1
        if(resultDate1.getText().length() == 0) {
            resultDate1.setError(getString(R.string.error_field_required));
            validated = false;
        }
        //date2
        else if(resultDate2.getText().length() == 0){
            resultDate2.setError(getString(R.string.error_field_required));
            validated = false;
        }
        //start time
        else if(resultTime1.getText().length() == 0){
            resultTime1.setError(getString(R.string.error_field_required));
            validated = false;
        }
        //end time
        else if(resultTime2.getText().length() == 0){
            resultTime2.setError(getString(R.string.error_field_required));
            validated = false;
        }
        //weight before
        else if(wb.getText().toString().length() == 0){
            wb.setError(getString(R.string.error_field_required));
            validated = false;
        }
        //weight after
        else if(wa.getText().toString().length() == 0){
            wa.setError(getString(R.string.error_field_required));
            validated = false;
        }


        /**
         * VALIDATE DATA
         * Only enters this section if all data is entered
         */
        if(validated) {
            int wbd = Integer.parseInt(wb.getText().toString());
            int wad = Integer.parseInt(wa.getText().toString());


            long st = varStartDate.getTimeInMillis();
            long et = varEndDate.getTimeInMillis();

            double millisHour = 3600000;
            double millisDay = millisHour*24;

            //end is before start
            if(et < st) {
                //end date is before start date, checks that its within a day
                if (st - et > millisDay || (st - et > millisDay && varStartDate.get(Calendar.DAY_OF_MONTH) != varEndDate.get(Calendar.DAY_OF_MONTH))) {
                    validationWarning.setMessage("The end date is before start date.");
                    validationWarning.show();
                    validated = false;
                } else { //End time before start
                    validationWarning.setMessage("The end time is before start time.");
                    validationWarning.show();
                    validated = false;
                }
            }
            //More than a day difference
            else if(et-st > millisDay){
                validationWarning.setMessage("The feed length is more that a day.");
                validationWarning.show();
                validated = false;

            }
            //more than 1 hour
            else if(et-st > millisHour){
                validationWarning.setMessage("The feed length is more than an hour.");
                validationWarning.show();
                validated = false;
            }
            //type not selected
            else if(selectedFeedType == "") {
                validationWarning.setMessage("A feed type has not been selected.");
                validationWarning.show();
                validated = false;
            }
            //subType not selected
            else if(selectedFeedSubType == "") {
                validationWarning.setMessage("The feed sub type has not been selected.");
                validationWarning.show();
                validated = false;
            }
            //expression cannot be lower after
            else if(selectedFeedType == "E" && wbd > wad){
                validationWarning.setMessage("The weight after expression cannot be lower than the weight before.");
                validationWarning.show();
                validated = false;

            }
            //Weight after is more than 20g lower
            else if (wbd - wad > 0 && wbd - wad <= 20 && !ignoreWeightWarning) {
                validationWarning.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ignoreWeightWarning = true;
                        dialog.dismiss();
                    }
                });
                validationWarning.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ignoreWeightWarning = false;
                        dialog.dismiss();
                    }
                });
                validationWarning.setMessage("The weight after is lower than the weight before but withing 20g, press OK to ignore this warning.");
                validationWarning.show();
                validated = false;
            }
            //weight is more than 20g less
            else if (wbd - wad > 20) {
                ignoreWeightWarning = false; //reset this warning to ensure the correct value is entered
                validationWarning.setMessage("The weight after is more than 20g lower than the weight before.");
                validationWarning.show();
                validated = false;
            }

            //warn if this feed is outside the 24h period
            else if(minDate != -1 && !studyPeriodWarning) {
                if (maxDate - st > millisDay) {
                    studyPeriodWarning = true;
                } else if (st - minDate > millisDay) {
                    studyPeriodWarning = true;
                }
            }
            //notify of period over 24 hours
            //give option to accept and save or cancel save and modify or cancel the feed
            if(studyPeriodWarning == true) {
                validationWarning.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (diag != null && diag.isShowing()) {
                            diag.dismiss();
                            saveAndReturn();
                        }


                    }
                });
                validationWarning.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        studyPeriodWarning = false;
                        diag.dismiss();
                    }
                });
                validationWarning.setMessage("You have now been entering data for more than 24 hours and may stop weighing.");


                diag = validationWarning.create();
                //validationWarning.show();
                if (diag != null) {
                    diag.show();
                }
                validated = false;
            }
        }
        return validated;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (diag != null) {
            diag.dismiss();
        }
    }

    /**
     * Prompts the user to comfirm deletion of feed, the feed will then be deleted
     * @param v
     */
    public void deleteFeed(View v) {
        AlertDialog.Builder alertIfDelete = new AlertDialog.Builder(this);
        alertIfDelete.setTitle("Warning!");
        alertIfDelete.setMessage("Are you sure you want to delete this feed?");
        alertIfDelete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("com.yifeilyf.breastfeeding_beta.editedFeed", receivedFeed);
                setResult(-100, intent);
                finish();
            }
        });
        alertIfDelete.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertIfDelete.show();
    }

    //set Time picker
    /**
     * the method will be called when user try to select time and it will set a Time picker
     * To display a TimePickerDialog using DialogFragment, you need to define a fragment class that extends
     * DialogFragment and return a TimePickerDialog from the fragment's onCreateDialog() method.
     */
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener{

        private TextView mResultText;
        private Calendar thisCal;

        public TimePickerFragment(){
            //default constructor
        }

        public TimePickerFragment(TextView textView, Calendar cal){
            mResultText = textView;
            thisCal = cal;
        }

        public Dialog onCreateDialog(Bundle savedInstanceState){
            //use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            //create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }

        /**
         * the method will be called when user have selected the time and the selected time will be showed on a TextView
         * @param view the view of button that is clicked
         * @param hourOfDay the hour that user selects
         * @param minute the minute that user selects
         */
        public void onTimeSet(TimePicker view, int hourOfDay, int minute){
            //pass value to mResultText
            thisCal.set(Calendar.HOUR_OF_DAY,hourOfDay);
            thisCal.set(Calendar.MINUTE,minute);
            SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
            mResultText.setText(formatTime.format(thisCal.getTime()));
        }

    }

    /**
     * The method will be called when user clicks button to select a start time
     * @param v the view of the button that is clicked
     */
    public void showTimePickerDialog1(View v){
        DialogFragment newFragment = new TimePickerFragment(resultTime1,varStartDate);
        newFragment.show(getFragmentManager(), "timePicker");

    }

    /**
     * The method will be called when user clicks button to select a end time
     * @param v the view of the button that is clicked
     */
    public void showTimePickerDialog2(View v){
        DialogFragment newFragment = new TimePickerFragment(resultTime2,varEndDate);
        newFragment.show(getFragmentManager(), "timePicker");
    }

    //set Date picker

    /**
     * the method will be called when user try to select date and it will set a Date picker
     * To display a DatePickerDialog using DialogFragment, you need to define a fragment class that extends
     * DialogFragment and return a DatePickerDialog from the fragment's onCreateDialog() method.
     */
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private TextView mResultText;
        private Calendar thisCal;
        public DatePickerFragment(){
            //default constructor
        }
        //constructor
        public DatePickerFragment(TextView textView, Calendar cal){
            mResultText = textView;
            thisCal = cal;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        /**
         * the method will be called when user have selected the date and the selected date will be showed on a TextView
         * @param view the view of button that is clicked
         * @param year the year that user selects
         * @param month the month that user selects
         * @param day the day that user selects
         */
        public void onDateSet(DatePicker view, int year, int month, int day) {
            //Display the date chosen in the ui and save to the calendar variable
            thisCal.set(year,month,day);
            SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
            mResultText.setText(formatDate.format(thisCal.getTime()));
        }
    }

    /**
     * The method will be called when user clicks button to select a start date
     * @param v the view of the button that is clicked
     */
    public void showDatePickerDialog1(View v) {
        DialogFragment newFragment = new DatePickerFragment(resultDate1, varStartDate);
        newFragment.show(getFragmentManager(), "datePicker");
    }


    /**
     * The method will be called when user clicks button to select a end date
     * @param v the view of the button that is clicked
     */
    public void showDatePickerDialog2(View v) {
        DialogFragment newFragment = new DatePickerFragment(resultDate2,varEndDate);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    /**
     * this method is called when user try to select the breast type and it will set focusable
     * and background on the clicked button
     * @param v The view of the button that is clicked
     */
    public void onClick(View v){

        //Assign value to selectedFeedType
        //reset the subtypeselection
        switch (v.getId()){
            //type breastfeed
            case R.id.btnType1:
                selectedFeedType = "B";
                //reset the subtype selection
                //selectedFeedSubType = -1;
                break;

            //type supplementary
            case R.id.btnType2:
                selectedFeedType = "S";
                //reset the subtype selection
                //selectedFeedSubType = -1;
                break;

            //type expressed
            case R.id.btnType3:
                selectedFeedType = "E";
                //reset the subtype selection
                //selectedFeedSubType = -1;
                break;

        }
        //update the UI
        displayFeedTypeButtons();
    }


    /**
     * This method will be called when user try to select subtype of breast type and
     * it will set focusable and background on the clicked button
     * @param v the view of the button that is clicked
     */


    public void onClickSubType(View v){
       //Switch on left or right button clicked
        //set the selectedFeedSubType variable accordingly
        switch (v.getId()){

            //subtype left OR expressed
            case R.id.btnLeft:
                selectedFeedSubType = "L";
                break;

            //subtype right
            case R.id.btnRight:
                selectedFeedSubType = "R";
                break;

            //subtype both OR formula
            case R.id.btnBoth:
                selectedFeedSubType = "B";
                break;
        }
        //update UI
        displayFeedTypeButtons();
    }

    /**
     * The method will hide the soft keyboard
     * @param view the view that is not a EditView is clicked
     */
    protected void hideKeyboard(View view)
    {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * Function used to handle the onClick for the eventual submit button
     * This collects the data from the page, loads in into the feed object and sends it back to the calling activity
     */
    public void saveAndReturn() {
        //This function assumes that all required data has been entered already
        //It will simply save and send to keep things simple

        EditText wb = (EditText)findViewById(R.id.TextWB);
        EditText wa = (EditText)findViewById(R.id.TextWA);
        EditText com = (EditText)findViewById(R.id.Comments);

        receivedFeed.putWeightBefore(Integer.parseInt(wb.getText().toString()));
        receivedFeed.putWeightAfter(Integer.parseInt(wa.getText().toString()));
        //receivedFeed.putWeightBefore(Integer.parseInt(wb.getText().toString()));
        //receivedFeed.putWeightAfter(Integer.parseInt(wa.getText().toString()));

        receivedFeed.putComment(com.getText().toString());

        receivedFeed.putType(selectedFeedType);

        if (selectedFeedType == "S") {
            receivedFeed.putSubType(selectedFeedSubType);
        } else {
            receivedFeed.putSide(selectedFeedSubType);
        }



        receivedFeed.putStartCal(varStartDate);
        receivedFeed.putEndCal(varEndDate);

        Intent intent = new Intent();
        intent.putExtra("com.yifeilyf.breastfeeding_beta.editedFeed", receivedFeed);
        setResult(Activity.RESULT_OK, intent);
        finish();

    }

    /**
     * Method used to update the UI to display the selected feed type and sub type buttons UI
     */
    private void displayFeedTypeButtons() {
        //reset all to blank the highlight the selected button
        btnFeedBreast.setBackgroundResource(R.drawable.textview_border);
        btnFeedExpressed.setBackgroundResource(R.drawable.textview_border);
        btnFeedSupplement.setBackgroundResource(R.drawable.textview_border);
        btnLeft.setBackgroundResource(R.drawable.textview_border);
        btnRight.setBackgroundResource(R.drawable.textview_border);
        btnBoth.setBackgroundResource(R.drawable.textview_border);

        EditText TextWB = (EditText)findViewById(R.id.TextWB);
        EditText TextWA = (EditText)findViewById(R.id.TextWA);
        TextView WeightBefore = (TextView)findViewById(R.id.WeightBefore);
        TextView WeightAfter = (TextView)findViewById(R.id.WeightAfter);

        //setup feed type buttons
        switch (selectedFeedType){
            case "": //initialise
                //FeedType.setText("Breastfeed Type");
                //btnLeftFeedType.setText("Left");
                //btnRightFeedType.setText("Right");
                break;
            case "B":
                btnFeedBreast.setBackgroundResource(R.drawable.btn1);
                WeightBefore.setText("BABY WEIGHT BEFORE");
                WeightAfter.setText("BABY WEIGHT AFTER");

                btnLeft.setText("Left");
                btnRight.setText("Right");
                btnBoth.setText("Both");
                btnLeft.setVisibility(View.VISIBLE);
                btnRight.setVisibility(View.VISIBLE);
                btnBoth.setVisibility(View.VISIBLE);
                TextWB.setVisibility(View.VISIBLE);
                TextWA.setVisibility(View.VISIBLE);
                WeightBefore.setVisibility(View.VISIBLE);
                WeightAfter.setVisibility(View.VISIBLE);
                break;

            case "S":
                btnFeedSupplement.setBackgroundResource(R.drawable.btn1);

                WeightBefore.setText("\nBABY WEIGHT BEFORE");
                WeightAfter.setText("\nBABY WEIGHT AFTER");

                btnLeft.setText("Expressed");
                btnBoth.setText("Formula");
                btnLeft.setVisibility(View.VISIBLE);
                btnRight.setVisibility(View.INVISIBLE);
                btnBoth.setVisibility(View.VISIBLE);
                TextWB.setVisibility(View.VISIBLE);
                TextWA.setVisibility(View.VISIBLE);
                WeightBefore.setVisibility(View.VISIBLE);
                WeightAfter.setVisibility(View.VISIBLE);
                break;

            case "E":
                btnFeedExpressed.setBackgroundResource(R.drawable.btn1);
                WeightBefore.setText("BOTTLE WEIGHT BEFORE");
                WeightAfter.setText("BOTTLE WEIGHT AFTER");

                btnLeft.setText("Left");
                btnRight.setText("Right");
                btnBoth.setText("Both");
                btnLeft.setVisibility(View.VISIBLE);
                btnRight.setVisibility(View.VISIBLE);
                btnBoth.setVisibility(View.VISIBLE);
                TextWB.setVisibility(View.VISIBLE);
                TextWA.setVisibility(View.VISIBLE);
                WeightBefore.setVisibility(View.VISIBLE);
                WeightAfter.setVisibility(View.VISIBLE);
                break;
    }

        //btnLeftFeedType.setBackgroundResource(R.drawable.textview_border);
        //btnRightFeedType.setBackgroundResource(R.drawable.textview_border);
        //Setup sub type buttons
        switch (selectedFeedSubType){
            case "L":
                btnLeft.setBackgroundResource(R.drawable.btn1);
                break;

            case "R":
                btnRight.setBackgroundResource(R.drawable.btn1);
                break;

            case "B":
                btnBoth.setBackgroundResource(R.drawable.btn1);
                break;

            case "E":
                btnLeft.setBackgroundResource(R.drawable.btn1);
                break;

            case "F":
                btnBoth.setBackgroundResource(R.drawable.btn1);
                break;
        }
    }

    /**
     * utility to load the page data and assign variables from a received feed
     * @param receivedFeed feed to be loaded
     */
    private void loadFeed(Feed receivedFeed) {

        resultDate1.setText(formatDate.format(receivedFeed.getStartCal().getTime()));
        resultDate2.setText(formatDate.format(receivedFeed.getEndCal().getTime()));
        resultTime1.setText(formatTime.format(receivedFeed.getStartCal().getTime()));
        resultTime2.setText(formatTime.format(receivedFeed.getEndCal().getTime()));

        EditText wb = (EditText)findViewById(R.id.TextWB);
        EditText wa = (EditText)findViewById(R.id.TextWA);
        EditText com = (EditText)findViewById(R.id.Comments);

        //System.out.println(receivedFeed.getLeftWeightBefore());
        wb.setText("" + receivedFeed.getWeightBefore());
        wa.setText("" + receivedFeed.getWeightAfter());

        com.setText(receivedFeed.getComment());
        //This only sets teh current values, It does not initialise the page with these values
        selectedFeedType = receivedFeed.getType();
        if (selectedFeedType.equals("S")) {
            selectedFeedSubType = receivedFeed.getSubType();
        } else {
            selectedFeedSubType = receivedFeed.getSide();
        }

        varStartDate = receivedFeed.getStartCal();
        varEndDate = receivedFeed.getEndCal();
    }
}