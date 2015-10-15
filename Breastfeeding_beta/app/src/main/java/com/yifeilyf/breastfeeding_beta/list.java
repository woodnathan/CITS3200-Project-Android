package com.yifeilyf.breastfeeding_beta;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Comparator;

public class list extends Activity {

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

        //feedListAdapter.notifyDataSetChanged();



        //Setup the click listener for the feed list, to allow for editing
        feedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                Feed existingFeed = (Feed) adapter.getItemAtPosition(position);
                Intent intent = new Intent(list.this, edit.class);

                //used to warn against exceeding the 24h period

                if(!feedListAdapter.isEmpty()){
                    intent.putExtra("com.yifeilyf.breastfeeding_beta.minDate",feedListAdapter.getItem(0).getStartCal().getTimeInMillis());
                    intent.putExtra("com.yifeilyf.breastfeeding_beta.maxDate",feedListAdapter.getItem(feedListAdapter.getCount()-1).getStartCal().getTimeInMillis());
                } else {
                    intent.putExtra("com.yifeilyf.breastfeeding_beta.minDate", -1);
                    intent.putExtra("com.yifeilyf.breastfeeding_beta.maxDate", -1);
                }
                //TODO: Should this naming string be a global variable
                intent.putExtra("com.yifeilyf.breastfeeding_beta.newFeed", existingFeed);

                intent.putExtra("com.yifeilyf.breastfeeding_beta.editRequestCode", 1);
                //RequestCode is the identifier sent to the edit activity, 0=newFeed 1=editFeed
                //Open the edit activity with the existing feed for editing
                startActivityForResult(intent, 1);
            }
        });

        //plus button and it will go through the edit page
        Button btnPlus = (Button) findViewById(R.id.PlusButton);

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Feed newFeed = new Feed(feedCount++);
                Intent intent = new Intent();
                intent.setClass(list.this, edit.class);
                if(!feedListAdapter.isEmpty()){
                    intent.putExtra("com.yifeilyf.breastfeeding_beta.minDate",feedListAdapter.getItem(0).getStartCal().getTimeInMillis());
                    intent.putExtra("com.yifeilyf.breastfeeding_beta.maxDate",feedListAdapter.getItem(feedListAdapter.getCount()-1).getStartCal().getTimeInMillis());
                } else {
                    intent.putExtra("com.yifeilyf.breastfeeding_beta.minDate", -1);
                    intent.putExtra("com.yifeilyf.breastfeeding_beta.maxDate", -1);
                }

                //The Feed object is a parcelable object type so that passing data can be kept simple
                //TODO: Should this naming string be a global variable
                intent.putExtra("com.yifeilyf.breastfeeding_beta.newFeed", newFeed);
                intent.putExtra("com.yifeilyf.breastfeeding_beta.editRequestCode", 0);
                //RequestCode is the identifier sent to the edit activity, 0=newFeed 1=editFeed

                startActivityForResult(intent, 0);
            }
        });

        //locked screen on portraity
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Logout button, return to Login page
        Button btnLogout = (Button) findViewById(R.id.tvLogout);
        btnLogout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    /**
     * This method will get the result back from edit page
     * @param requestCode a feed will be delete if resultCode is equal to -100
     * @param resultCode a new feed is added if resultCode is equal to 0
     *                   a edited feed is uploaded if requestCode is equal to 1
     * @param data it holds the details that is added by user such as start time and start date
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO: Add code to update the database with new feed data

        //requestCode is the identifier sent to the edit activity, 0=newFeed 1=editFeed
        Feed editedFeed = data.getParcelableExtra("com.yifeilyf.breastfeeding_beta.editedFeed");
        if (resultCode == -100) {
            //TODO: delete feed
            deleteFeed(editedFeed);
        } else if(resultCode != RESULT_CANCELED) {
            if(requestCode == 0) {
                //TODO Error handling?
                addNewFeed(editedFeed);
            } else if(requestCode == 1) {
                //TODO Error handling?
                updateFeed(editedFeed);
            }
        }

        //sort and update the list
        feedListAdapter.sort(new Comparator<Feed>() {
            @Override
            public int compare(Feed lhs, Feed rhs) {
                return lhs.compareTo(rhs);
            }
        });
        feedListAdapter.notifyDataSetChanged();
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

    /**
     * Private method to delete a feed from the backing array
     * @param deleted the feed to be deleted from the list
     * @return true is successfully deleted the feed, false if feed not found
     */
    private boolean deleteFeed(Feed deleted) {
        int id = deleted.getID();
        for(int i = 0; i < feedBackingArray.size(); i++) {
            if(feedBackingArray.get(i) != null) {
                if(feedBackingArray.get(i).getID() == id) {
                    //delete the feed from the backing array
                    feedBackingArray.remove(i);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * The method will be called when user clicks Logout button and it will return to Login page and
     * clean up all activities
     */
    private void signOut(){
        Intent intent = new Intent(this, login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
