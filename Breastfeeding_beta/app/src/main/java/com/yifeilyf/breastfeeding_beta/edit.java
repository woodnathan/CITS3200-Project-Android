package com.yifeilyf.breastfeeding_beta;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import java.util.Calendar;

public class edit extends Activity {
    //Feed object to be edited
    Feed receivedFeed;

    //Setup UI interfaces
    private TextView resultTime1, resultTime2;
    private TextView resultDate1, resultDate2;
    private int selectedFeedType = 0; //0,1,2 = breastfeed,expressed, supplementary
    private int selectedFeedSubType = 0; // 0,1 = left,right or expressed,suplementary
    private int isEdit = 0; //new feed = 0, edit = 1


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //remove notification bar
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //set content view after above sequence (to avoid crash)
        this.setContentView(R.layout.edit);
        setContentView(R.layout.edit);

        //Timepicker and Datepicker shows on TextView
        resultTime1 = (TextView) findViewById(R.id.SelectTime1);
        resultTime2 = (TextView) findViewById(R.id.SelectTime2);
        resultDate1 = (TextView) findViewById(R.id.SelectDate1);
        resultDate2 = (TextView) findViewById(R.id.SelectDate2);

        //Receive the feed to be edited and load current data to the UI

        Intent intent = getIntent();

        receivedFeed = intent.getParcelableExtra("com.yifeilyf.breastfeeding_beta.newFeed");
        isEdit = intent.getIntExtra("com.yifeilyf.breastfeeding_beta.editRequestCode", 0);
        //if request code is 1 the feed can be loaded and editing disabled until edit option is selected
        if(isEdit == 1){
            //TODO: DISABLE EDITING OF PAGE

            //load saved feed details to the page
            resultDate1.setText(receivedFeed.getStartDate());
            resultDate2.setText(receivedFeed.getEndDate());
            resultTime1.setText(receivedFeed.getStartTime());
            resultTime2.setText(receivedFeed.getEndTime());
            EditText wb = (EditText)findViewById(R.id.btnWeight1);
            EditText wa = (EditText)findViewById(R.id.btnWeight2);
            TextView com = (TextView)findViewById(R.id.Coments);
            wb.setText("" + receivedFeed.getWeightBefore());
            wa.setText("" + receivedFeed.getWeightAfter());
            com.setText(receivedFeed.getComment());
            selectedFeedType = receivedFeed.getType();
            selectedFeedSubType = receivedFeed.getSubType();

            //initialise feed type buttons
            initFeedTypeButtons();

            final Button btnDone = (Button) findViewById(R.id.DoneButton);
            btnDone.setText("Edit");

            RelativeLayout layout = (RelativeLayout) findViewById(R.id.edit);
            for(int i = 0; i<layout.getChildCount(); i++){
                View edit = layout.getChildAt(i);
                if(edit instanceof Button){
                    ((Button)edit).setEnabled(false);
                } else if(edit instanceof EditText){
                    ((EditText)edit).setEnabled(false);
                }
            }

        }

        //Cancel button
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
                //TODO: Add checks to ensure all data has been entered.
                //TODO: data validation checks need to be added as well

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
                    saveAndReturn(v);
                }
            }
        });






        //locked screen on portraity
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //hide the keyboard
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
        //TODO: add conformation popup
        //if yes
        Intent intent = new Intent();
        intent.putExtra("com.yifeilyf.breastfeeding_beta.editedFeed", receivedFeed);
        setResult(-100, intent);
        finish();

    }

    //set Time picker
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

        public void onTimeSet(TimePicker view, int hourOfDay, int minute){
            //pass value to mResultText
            String time = Integer.toString(hourOfDay)+":"+Integer.toString(minute);
            mResultText.setText(time);
        }

    }

    public void showTimePickerDialog1(View v){
        DialogFragment newFragment = new TimePickerFragment(resultTime1);
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showTimePickerDialog2(View v){
        DialogFragment newFragment = new TimePickerFragment(resultTime2);
        newFragment.show(getFragmentManager(), "timePicker");
    }

    //set Date picker
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private TextView mResultText;

        public DatePickerFragment(){
            //default constructor
        }

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

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            String date = Integer.toString(day)+"/"+Integer.toString(month)+"/"+Integer.toString(year);
            mResultText.setText(date);
        }
    }

    public void showDatePickerDialog1(View v) {
        DialogFragment newFragment = new DatePickerFragment(resultDate1);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void showDatePickerDialog2(View v) {
        DialogFragment newFragment = new DatePickerFragment(resultDate2);
        newFragment.show(getFragmentManager(), "datePicker");
    }


    //set color change when click Type button
    Button btnType, btnLeftFeedType, btnRightFeedType;
    TextView FeedType;


    public void onClick(View v){

        switch (v.getId()){
            case R.id.btnType1:
                //used to set value in feed object
                selectedFeedType = 0;
                if(btnType == null){ //Breastfeed layout
                    btnType = (Button) findViewById(v.getId());
                    btnType.setBackgroundResource(R.drawable.btn1);
                } else if(btnType != findViewById(v.getId())) {
                    btnType.setBackgroundResource(R.drawable.textview_border);
                    btnType = (Button) findViewById(v.getId());
                    btnType.setBackgroundResource(R.drawable.btn1);
                }
                FeedType = (TextView) findViewById(R.id.Breast);
                FeedType.setText("Breastfeed Type");
                btnLeftFeedType = (Button) findViewById(R.id.btnBreast1);
                btnLeftFeedType.setText("Left");
                btnRightFeedType = (Button) findViewById(R.id.btnBreast2);
                btnRightFeedType.setText("Right");
                break;

            case R.id.btnType2:
                //used to set value in feed object
                selectedFeedType = 2;
                if(btnType == null){ //Supplementary layout
                    btnType = (Button) findViewById(v.getId());
                    btnType.setBackgroundResource(R.drawable.btn1);
                } else if(btnType != findViewById(v.getId())) {
                    btnType.setBackgroundResource(R.drawable.textview_border);
                    btnType = (Button) findViewById(v.getId());
                    btnType.setBackgroundResource(R.drawable.btn1);
                }
                FeedType = (TextView) findViewById(R.id.Breast);
                FeedType.setText("Supplementary Type");
                btnLeftFeedType = (Button) findViewById(R.id.btnBreast1);
                btnLeftFeedType.setText("Expressed");
                btnRightFeedType = (Button) findViewById(R.id.btnBreast2);
                btnRightFeedType.setText("Formula");
                break;

            case R.id.btnType3:
                //used to set value in feed object
                selectedFeedType = 1;
                if(btnType == null){ //Expressed layout
                    btnType = (Button) findViewById(v.getId());
                    btnType.setBackgroundResource(R.drawable.btn1);
                } else if(btnType != findViewById(v.getId())){
                    btnType.setBackgroundResource(R.drawable.textview_border);
                    btnType = (Button) findViewById(v.getId());
                    btnType.setBackgroundResource(R.drawable.btn1);
                }
                FeedType = (TextView) findViewById(R.id.Breast);
                FeedType.setText("Expressed Type");
                btnLeftFeedType = (Button) findViewById(R.id.btnBreast1);
                btnLeftFeedType.setText("Left");
                btnRightFeedType = (Button) findViewById(R.id.btnBreast2);
                btnRightFeedType.setText("Right");
                break;
        }
    }


    //set color change when click Breast button
    Button btnBreast;
    public void onClickBreast(View v){
        switch (v.getId()){
            case R.id.btnBreast1:
                //used to set value in feed object
                selectedFeedSubType = 0;
                if(btnBreast == null){
                    btnBreast = (Button) findViewById(v.getId());
                    btnBreast.setBackgroundResource(R.drawable.btn1);
                } else if(btnBreast != findViewById(v.getId())) {
                    btnBreast.setBackgroundResource(R.drawable.textview_border);
                    btnBreast = (Button) findViewById(v.getId());
                    btnBreast.setBackgroundResource(R.drawable.btn1);
                }
                break;

            case R.id.btnBreast2:
                selectedFeedSubType = 1;
                if(btnBreast == null){
                    btnBreast = (Button) findViewById(v.getId());
                    btnBreast.setBackgroundResource(R.drawable.btn1);
                } else if(btnBreast != findViewById(v.getId())) {
                    btnBreast.setBackgroundResource(R.drawable.textview_border);
                    btnBreast = (Button) findViewById(v.getId());
                    btnBreast.setBackgroundResource(R.drawable.btn1);
                }
                break;
        }
    }

    //hide the keyboard
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
        TextView com = (TextView)findViewById(R.id.Coments);
        //TODO: assign Type 0,1,2 = breastfeed,expressed,supplementary
        receivedFeed.putType(1);
        //TODO: assign SubType 0,1 = left,right or expressed,supplementary
        receivedFeed.putSubType(1);


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


                    btnType = (Button) findViewById(R.id.btnType1);
                    btnType.setBackgroundResource(R.drawable.textview_border);
                    btnType.setBackgroundResource(R.drawable.btn1);

                FeedType = (TextView) findViewById(R.id.Breast);
                FeedType.setText("Breastfeed Type");
                btnLeftFeedType = (Button) findViewById(R.id.btnBreast1);
                btnLeftFeedType.setText("Left");
                btnRightFeedType = (Button) findViewById(R.id.btnBreast2);
                btnRightFeedType.setText("Right");
                break;

            case 2:

                    btnType = (Button) findViewById(R.id.btnType2);
                    btnType.setBackgroundResource(R.drawable.textview_border);

                    btnType.setBackgroundResource(R.drawable.btn1);

                FeedType = (TextView) findViewById(R.id.Breast);
                FeedType.setText("Supplementary Type");
                btnLeftFeedType = (Button) findViewById(R.id.btnBreast1);
                btnLeftFeedType.setText("Expressed");
                btnRightFeedType = (Button) findViewById(R.id.btnBreast2);
                btnRightFeedType.setText("Formula");
                break;

            case 1:

                    btnType = (Button) findViewById(R.id.btnType3);
                    btnType.setBackgroundResource(R.drawable.textview_border);

                    btnType.setBackgroundResource(R.drawable.btn1);

                FeedType = (TextView) findViewById(R.id.Breast);
                FeedType.setText("Expressed Type");
                btnLeftFeedType = (Button) findViewById(R.id.btnBreast1);
                btnLeftFeedType.setText("Left");
                btnRightFeedType = (Button) findViewById(R.id.btnBreast2);
                btnRightFeedType.setText("Right");
                break;
    }

        //Setup sub type buttons
        switch (selectedFeedSubType){
            case 0:

                    btnBreast = (Button) findViewById(R.id.btnBreast1);
                    btnBreast.setBackgroundResource(R.drawable.textview_border);

                    btnBreast.setBackgroundResource(R.drawable.btn1);

                break;

            case 1:
                    btnBreast = (Button) findViewById(R.id.btnBreast2);
                    btnBreast.setBackgroundResource(R.drawable.textview_border);

                    btnBreast.setBackgroundResource(R.drawable.btn1);

                break;
        }
    }
}