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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import java.util.Calendar;

public class edit extends Activity {

    private TextView resultTime1, resultTime2;
    private TextView resultDate1, resultDate2;

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

        //Cancel button
        Button btnCancel = (Button) findViewById(R.id.CancelButton);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), list.class);
                startActivity(intent, null);
            }
        });

        //Done Button, pass data to a TextView

        Button btnDone = (Button) findViewById(R.id.DoneButton);


        btnDone.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(v.getContext(), list.class);
                startActivity(intent);
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
                if(btnType == null){
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
                if(btnType == null){
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
                if(btnType == null){
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

}