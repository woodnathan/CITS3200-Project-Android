package com.yifeilyf.breastfeeding_beta;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
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

    //Initialise backing data structure variables

    private int feedCount = 0;
    private ArrayList<Feed> feedBackingArray = new ArrayList<Feed>();

    //UI objects
    ListView feedList; //Scrollable list of feeds
    ArrayAdapter<Feed> feedListAdapter;

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

        //Initialise the backing data structure for the feeds list
        feedList = (ListView) findViewById(R.id.feedList);
        feedListAdapter = new ArrayAdapter<Feed>(this,
                android.R.layout.simple_list_item_1,feedBackingArray);
        feedList.setAdapter(feedListAdapter);

        //TODO: List can be initialised with data retrieved from the server

        //Setup the click listener for the feed list, to allow for editing
        feedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                Feed existingFeed = (Feed) adapter.getItemAtPosition(position);
                Intent intent = new Intent(list.this, edit.class);


                //TODO: Should this naming string be a global variable
                intent.putExtra("com.yifeilyf.breastfeeding_beta.newFeed", existingFeed);

                intent.putExtra("com.yifeilyf.breastfeeding_beta.editRequestCode", 1);
                //RequestCode is the identifier sent to the edit activity, 0=newFeed 1=editFeed
                //Open the edit activity with the existing feed for editing
                startActivityForResult(intent, 1);
            }
        });

        //plus button
        Button btnPlus = (Button) findViewById(R.id.PlusButton);

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Feed newFeed= new Feed(feedCount++);
                Intent intent = new Intent ();
                intent.setClass(list.this, edit.class);
                //

                //The Feed object is a parcelable object type so that passing data can be kept simple

                //TODO: Should this naming string be a global variable
                intent.putExtra("com.yifeilyf.breastfeeding_beta.newFeed", newFeed);
                intent.putExtra("com.yifeilyf.breastfeeding_beta.editRequestCode", 0);
                //RequestCode is the identifier sent to the edit activity, 0=newFeed 1=editFeed

                startActivityForResult(intent, 0);
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO: Add code to update the database with new feed data
        //TODO: Make the list sort by date/time
        //requestCode is the identifier sent to the edit activity, 0=newFeed 1=editFeed
        if(resultCode != RESULT_CANCELED) {
            if(requestCode == 0) {
                Feed editedFeed = data.getParcelableExtra("com.yifeilyf.breastfeeding_beta.editedFeed");
                //TODO Error handling?
                addNewFeed(editedFeed);
                feedListAdapter.notifyDataSetChanged();
            } else if(requestCode == 1) {
                Feed editedFeed = data.getParcelableExtra("com.yifeilyf.breastfeeding_beta.editedFeed");
                //TODO Error handling?
                updateFeed(editedFeed);
                feedListAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Private method to be used to update a feed in the feed list
     * @param feedEdited A feed to replace an exiting feed with the same ID
     * @return true on successful update, else false
     */
    private boolean updateFeed(Feed feedEdited) {
        //Performs checks to ensure integrity of the feeds array
        int id = feedEdited.getID();
        for(int i = 0; i < feedBackingArray.size(); i++) {
            if(feedBackingArray.get(i) != null) {
                if(feedBackingArray.get(i).getID() == id) {
                    feedBackingArray.set(i,feedEdited);
                    //feed with same ID is found and updated successfully
                    return true;
                }
            }
        }
        return false;

    }

    /**
     * Private method to be used to add a new feed to the list
     * @param newFeed A feed object to be added to the list
     * @return false if feed with the same ID is already in the list, else true
     */
    private boolean addNewFeed(Feed newFeed) {
        //Performs checks to ensure integrity of the feeds array
        int id = newFeed.getID();
        for(int i = 0; i < feedBackingArray.size(); i++) {
            if(feedBackingArray.get(i) != null) {
                if(feedBackingArray.get(i).getID() == id) {
                    //A feed with the same ID is already contained in the array
                    return false;
                }
            }
        }
        feedBackingArray.add(newFeed);
        return true;
    }
}
