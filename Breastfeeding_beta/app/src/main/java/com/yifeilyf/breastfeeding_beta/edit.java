package com.yifeilyf.breastfeeding_beta;

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

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class edit extends Activity {
    //Feed object to be edited
    Feed receivedFeed;

    //Setup UI interfaces
    private TextView resultTime1, resultTime2;
    private TextView resultDate1, resultDate2;
    private TextView FeedType;
    private Button btnLeftFeedType;
    private Button btnRightFeedType;
    private Button btnFeedBreast;
    private Button btnFeedExpressed;
    private Button btnFeedSupplement;




    //setup UI flags and config variabls

    private int isEdit = 0; //new feed = 0, edit = 1
    private boolean ignoreWeightWarning = false;

    //Data variables
    private SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy");
    private Date varStartDate;
    private Date varEndDate;
    private Time varStartTime;
    private Time varEndTime;
    private int selectedFeedType = 0; //0,1,2 = breastfeed,expressed, supplementary
    private int selectedFeedSubType = 0; // 0,1 = left,right or expressed,suplementary


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

        FeedType = (TextView) findViewById(R.id.Breast);
        btnLeftFeedType = (Button) findViewById(R.id.btnBreast1);
        btnRightFeedType = (Button) findViewById(R.id.btnBreast2);

        btnFeedBreast = (Button) findViewById(R.id.btnType1);
        btnFeedExpressed = (Button) findViewById(R.id.btnType3);
        btnFeedSupplement = (Button) findViewById(R.id.btnType2);

        //Receive the feed to be edited and load current data to the UI
        Intent intent = getIntent();

        receivedFeed = intent.getParcelableExtra("com.yifeilyf.breastfeeding_beta.newFeed");
        isEdit = intent.getIntExtra("com.yifeilyf.breastfeeding_beta.editRequestCode", 0);
        //if request code is 1 the feed can be loaded and editing disabled until edit option is selected
        if(isEdit == 1){
            //load saved feed details to the page
            resultDate1.setText(receivedFeed.getStartDate());
            resultDate2.setText(receivedFeed.getEndDate());
            resultTime1.setText(receivedFeed.getStartTime());
            resultTime2.setText(receivedFeed.getEndTime());
            EditText wb = (EditText)findViewById(R.id.btnWeight1);
            EditText wa = (EditText)findViewById(R.id.btnWeight2);
            EditText com = (EditText)findViewById(R.id.Comments);
            wb.setText("" + receivedFeed.getWeightBefore());
            wa.setText("" + receivedFeed.getWeightAfter());
            com.setText(receivedFeed.getComment());
            selectedFeedType = receivedFeed.getType();
            selectedFeedSubType = receivedFeed.getSubType();

            //initialise feed type buttons
            initFeedTypeButtons();

            final Button btnDone = (Button) findViewById(R.id.DoneButton);
            btnDone.setText("Edit");

            //iteration through the whole activity and find EditView and Button, then disable them
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.edit);
            for(int i = 0; i<layout.getChildCount(); i++){
                View edit = layout.getChildAt(i);
                if(edit instanceof Button){
                    ((Button)edit).setEnabled(false);
                } else if(edit instanceof EditText){
                    ((EditText)edit).setEnabled(false);
                }
            }
        } else {
            //initialise the feed type buttons with default settings
            initFeedTypeButtons();
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
                    RelativeLayout layout = (RelativeLayout) findViewById(R.id.edit);
                    for(int i = 0; i<layout.getChildCount(); i++){
                        View edit = layout.getChildAt(i);
                        if(edit instanceof Button){
                            ((Button)edit).setEnabled(true);
                        } else if(edit instanceof EditText){
                            ((EditText)edit).setEnabled(true);
                        }
                    }
                    btnDone.setText("Done");
                }else{


                    //TODO: data validation checks need to be added as well
                    boolean validated = true;
                    EditText wb = (EditText)findViewById(R.id.btnWeight1);
                    EditText wa = (EditText)findViewById(R.id.btnWeight2);

                    //setup warning prompt
                    AlertDialog.Builder validationWarning = new AlertDialog.Builder(edit.this);
                    validationWarning.setTitle("Invalid Input");
                    //validationWarning.setMessage("");
                    validationWarning.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    /**
                     * First test all fields are complete
                     * Then check data validations
                     */
                    //date1
                    //is empty
                    if(resultDate1.getText().length() == 0) {
                        resultDate1.setError(getString(R.string.error_field_required));
                        validated = false;
                    }

                    //date2
                    //is empty
                    else if(resultDate2.getText().length() == 0){
                        resultDate2.setError(getString(R.string.error_field_required));
                        validated = false;
                    }
                    //start time
                    //is empty
                    else if(resultTime1.getText().length() == 0){
                        resultTime1.setError(getString(R.string.error_field_required));
                        validated = false;
                    }
                    //end time
                    //is empty
                    else if(resultTime2.getText().length() == 0){
                        resultTime2.setError(getString(R.string.error_field_required));
                        validated = false;
                    }
                    //weight before
                    //is empty

                    else if(wb.getText().toString().length() == 0){
                        wb.setError(getString(R.string.error_field_required));
                        validated = false;
                    }
                    //weight after
                    //is empty


                    else if(wa.getText().toString().length() == 0){
                        wa.setError(getString(R.string.error_field_required));
                        validated = false;
                    }


                    /**
                     * VALIDATE DATA
                     * Only enters this section if all data is entered
                     */
                if(validated) {
                    //format the date into a friendly comparable format
                    String rd1 = resultDate1.getText().toString();
                    String rd2 = resultDate2.getText().toString();
                    int d1 = Integer.parseInt(rd1.substring(6, 10) + rd1.substring(3, 5) + rd1.substring(0, 2));
                    int d2 = Integer.parseInt(rd2.substring(6, 10) + rd2.substring(3, 5) + rd2.substring(0, 2));
                    double wbd = Double.parseDouble(wb.getText().toString());
                    double wad = Double.parseDouble(wa.getText().toString());


                        Date date1 = null;
                        Date date2 = null;
                        double time1 = Double.parseDouble(resultTime1.getText().toString().replace(':', '.'));
                        double time2 = Double.parseDouble(resultTime2.getText().toString().replace(':', '.'));
                        //get the dates
                        try {
                            date1 = format.parse(resultDate1.getText().toString());
                            date2 = format.parse(resultDate2.getText().toString());
                        } catch (Exception e) {
                            //do nothing
                        }

                        //start date is after end
                        if (d1 > d2) {
                            validationWarning.setMessage("The finish date cannot be before the start date.");
                            validationWarning.show();
                            validated = false;
                        }
                        //start time is after end
                        else if (d1 == d2 && time1 > time2) {
                            validationWarning.setMessage("The finish time cannot be before the start time.");
                            validationWarning.show();
                            validated = false;
                        }
                        //same feed longer than 1 hour
                        //include calculation for feeds over midnight

                        else if (((d2 - d1) * 24) + time2 - time1 > 1) {
                            validationWarning.setMessage("The feed duration cannot be longer than one hour.");
                            validationWarning.show();
                            validated = false;
                        }
                        //expression cannot be lower after
                        else if(selectedFeedType == 1 && wbd > wad){
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
                            validationWarning.setMessage("The weight after is lower than the weight before, press OK to ignore this warning.");
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
                    }

                    if (validated) {
                        saveAndReturn(v);
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

    public void deleteFeed(View v) {
        AlertDialog.Builder alertIfDelete = new AlertDialog.Builder(this);
        alertIfDelete.setTitle("Alert!!");
        alertIfDelete.setMessage("Are you sure to delete this feed");
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

        public TimePickerFragment(){
            //default constructor
        }

        public TimePickerFragment(TextView textView){
            mResultText = textView;
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

            //fix minutes less than 10
            String minuteFixed = Integer.toString(minute);
            if(Integer.toString(minute).length() == 1) {
                minuteFixed = "0"+Integer.toString(minute);
            }
            String time = Integer.toString(hourOfDay)+":"+minuteFixed;
            mResultText.setText(time);
        }

    }

    /**
     * The method will be called when user clicks button to select a start time
     * @param v the view of the button that is clicked
     */
    public void showTimePickerDialog1(View v){
        DialogFragment newFragment = new TimePickerFragment(resultTime1);
        newFragment.show(getFragmentManager(), "timePicker");
    }

    /**
     * The method will be called when user clicks button to select a end time
     * @param v the view of the button that is clicked
     */
    public void showTimePickerDialog2(View v){
        DialogFragment newFragment = new TimePickerFragment(resultTime2);
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

        public DatePickerFragment(){
            //default constructor
        }
        //constructor
        public DatePickerFragment(TextView textView){
            mResultText = textView;
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
            // Do something with the date chosen by the user
            String date = Integer.toString(day)+"/"+Integer.toString(month+1)+"/"+Integer.toString(year);
            mResultText.setText(date);
        }
    }

    /**
     * The method will be called when user clicks button to select a start date
     * @param v the view of the button that is clicked
     */
    public void showDatePickerDialog1(View v) {
        DialogFragment newFragment = new DatePickerFragment(resultDate1);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    /**
     * The method will be called when user clicks button to select a end date
     * @param v the view of the button that is clicked
     */
    public void showDatePickerDialog2(View v) {
        DialogFragment newFragment = new DatePickerFragment(resultDate2);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    /**
     * this method is called when user try to select the breast type and it will set focusable
     * and background on the clicked button
     * @param v The view of the button that is clicked
     */


    public void onClick(View v){
        //Clear all buttons
        //Set new focus button
        btnFeedBreast.setBackgroundResource(R.drawable.textview_border);
        btnFeedExpressed.setBackgroundResource(R.drawable.textview_border);
        btnFeedSupplement.setBackgroundResource(R.drawable.textview_border);


        switch (v.getId()){
            //type breastfeed
            case R.id.btnType1:
                //used to set value in feed object
                selectedFeedType = 0;
                btnFeedBreast.setBackgroundResource(R.drawable.btn1);

                FeedType.setText("Breastfeed Type");
                btnLeftFeedType.setText("Left");
                btnRightFeedType.setText("Right");
                break;

            //type supplementary
            case R.id.btnType2:
                //used to set value in feed object
                selectedFeedType = 2;
                btnFeedSupplement.setBackgroundResource(R.drawable.btn1);

                FeedType.setText("Supplementary Type");
                btnLeftFeedType.setText("Expressed");
                btnRightFeedType.setText("Formula");
                break;

            //type expressed
            case R.id.btnType3:
                //used to set value in feed object
                selectedFeedType = 1;
                btnFeedExpressed.setBackgroundResource(R.drawable.btn1);

                FeedType.setText("Expressed Type");
                btnLeftFeedType.setText("Left");
                btnRightFeedType.setText("Right");
                break;
        }
    }


    /**
     * This method will be called when user try to select subtype of breast type and
     * it will set focusable and background on the clicked button
     * @param v the view of the button that is clicked
     */


    public void onClickBreast(View v){
        btnLeftFeedType.setBackgroundResource(R.drawable.textview_border);
        btnRightFeedType.setBackgroundResource(R.drawable.textview_border);

        switch (v.getId()){
            case R.id.btnBreast1:
                //used to set value in feed object
                selectedFeedSubType = 0;
                btnLeftFeedType.setBackgroundResource(R.drawable.btn1);
                break;

            case R.id.btnBreast2:
                selectedFeedSubType = 1;
                btnRightFeedType.setBackgroundResource(R.drawable.btn1);
                break;
        }
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
     * @param v The view of the button that was clicked
     */
    public void saveAndReturn(View v) {
        //This function assumes that all required data has been entered already
        //It will simply save and send to keep things simple

        receivedFeed.putStartDate(resultDate1.getText().toString());

        receivedFeed.putEndDate(resultDate2.getText().toString());

        receivedFeed.putStartTime(resultTime1.getText().toString());

        receivedFeed.putEndTime(resultTime2.getText().toString());

        EditText wb = (EditText)findViewById(R.id.btnWeight1);
        EditText wa = (EditText)findViewById(R.id.btnWeight2);
        EditText com = (EditText)findViewById(R.id.Comments);


        receivedFeed.putWeightBefore(Double.parseDouble(wb.getText().toString()));
        receivedFeed.putWeightAfter(Double.parseDouble(wa.getText().toString()));
        receivedFeed.putComment(com.getText().toString());

        receivedFeed.putType(selectedFeedType);
        receivedFeed.putSubType(selectedFeedSubType);

        Intent intent = new Intent();
        intent.putExtra("com.yifeilyf.breastfeeding_beta.editedFeed", receivedFeed);
        setResult(Activity.RESULT_OK, intent);
        finish();

    }

    /**
     * Method used to initialise the feed type and sub type buttons UI in edit mode
     */
    private void initFeedTypeButtons() {
        //setup feed type buttons
        switch (selectedFeedType){
            case 0:
                btnFeedBreast.setBackgroundResource(R.drawable.btn1);
                FeedType.setText("Breastfeed Type");
                btnLeftFeedType.setText("Left");
                btnRightFeedType.setText("Right");
                break;

            case 2:
                btnFeedSupplement.setBackgroundResource(R.drawable.btn1);
                FeedType.setText("Supplementary Type");
                btnLeftFeedType.setText("Expressed");
                btnRightFeedType.setText("Formula");
                break;

            case 1:
                btnFeedExpressed.setBackgroundResource(R.drawable.btn1);
                FeedType.setText("Expressed Type");
                btnLeftFeedType.setText("Left");
                btnRightFeedType.setText("Right");
                break;
    }

        //Setup sub type buttons
        switch (selectedFeedSubType){
            case 0:
                btnLeftFeedType.setBackgroundResource(R.drawable.btn1);
                break;

            case 1:
                btnRightFeedType.setBackgroundResource(R.drawable.btn1);
                break;
        }
    }
}