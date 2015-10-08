package com.yifeilyf.breastfeeding_beta;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class list extends Activity {

    String startTime;
    String endTime;
    String whatDate;
    ListView lv;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //remove notification bar
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //set content view after above sequence (to avoid crash)
        this.setContentView(R.layout.list);

        setContentView(R.layout.list);

        //plus button
        Button btnPlus = (Button) findViewById(R.id.PlusButton);

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ();
                intent.setClass(list.this, edit.class);
                //startActivityForResult(intent, REQUEST_CODE);
                startActivity(intent);
            }
        });
    }

    /*protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_CODE){
            if(resultCode == edit.RESULT_CODE){
                Bundle bundle = data.getExtras();
                String
            }
        }
    }*/

}
