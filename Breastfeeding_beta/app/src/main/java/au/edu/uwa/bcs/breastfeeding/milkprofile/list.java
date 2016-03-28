package au.edu.uwa.bcs.breastfeeding.milkprofile;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.yifeilyf.breastfeeding_beta.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class list extends Activity {

    //Initialise backing data structure variables
    private int feedCount = 0;
    private ArrayList<Feed> feedBackingArray = new ArrayList<Feed>();
    private String mEmail = "";
    private String mPassword = "";
    private UserLoginTask mAuthTask = null;

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

        Intent intent = getIntent();
        String feedData = intent.getStringExtra("feedData");
        mEmail = intent.getStringExtra("mEmail");
        mPassword = intent.getStringExtra("mPassword");
        Boolean success = initialise(feedData);
        feedListAdapter.notifyDataSetChanged();



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
        try {
            JSONObject jsonObject = formatFeed(newFeed);
            mAuthTask = new UserLoginTask(jsonObject.toString());
            mAuthTask.execute((Void) null);
        } catch (JSONException e) {
            return false;
        }


        return true;
    }

    private JSONObject formatFeed(Feed newFeed) throws JSONException{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        JSONArray feedlist = new JSONArray();
        JSONObject feed = new JSONObject();
        JSONObject before = new JSONObject();
        JSONObject after = new JSONObject();

        //Write before and after times to JSONArrays
        Date date =  newFeed.getStartCal().getTime();
        before.put("date", sdf.format(date));
        before.put("weight", newFeed.getWeightBefore());

        date =  newFeed.getEndCal().getTime();
        after.put("date", sdf.format(date));
        after.put("weight", newFeed.getWeightAfter());

        //Write type and subtype of feed
        if (newFeed.getType() == 0) {
            feed.put("type", "Breastfeed");
        } else if (newFeed.getType() == 1) {
            feed.put("type", "Expressed");
        } else {
            feed.put("type", "Supplementary");
        }

        if (newFeed.getType() != 2) {
            if (newFeed.getSubType() == 0) {
                feed.put("side", "Left");
            } else {
                feed.put("side", "Right");
            }

        } else if (newFeed.getSubType() == 0) {
            feed.put("subtype", "Expressed");
        } else {
            feed.put("subtype", "Formula");
        }

        //Write comment if exists
        if (!newFeed.getComment().equals("")) {
            feed.put("comment", newFeed.getComment());
        }

        //Write times Arrays into feed master Array
        feed.put("before", before);
        feed.put("after", after);
        feedlist.put(feed);
        JSONObject data = new JSONObject();
        data.put("feeds", feedlist);
        System.out.println(data.toString());
        return data;
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

    /**
     * Private method used to initialise the feed list backing array from the server.
     * @param feedData The feed data received from the server (a JSONObject parsed as a String)
     */
    private Boolean initialise(String feedData) {
        System.out.println(feedData);
        try {
            JSONObject jsonObject = new JSONObject(feedData);
            JSONArray feedArray = jsonObject.getJSONArray("feeds");
            for (int i = 0; i < feedArray.length(); i++) {
                Feed feed = new Feed(feedCount++);
                JSONObject data = feedArray.getJSONObject(i);

                //Create and add calendar and time. objects
                Calendar startCal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                startCal.setTime(sdf.parse(data.getJSONObject("before").getString("date")));
                feed.putStartCal(startCal);

                Calendar endCal = Calendar.getInstance();
                endCal.setTime(sdf.parse(data.getJSONObject("after").getString("date")));
                feed.putEndCal(endCal);

                //Add weights
                feed.putWeightBefore(Integer.parseInt(data.getJSONObject("before").getString("weight")));
                System.out.println(data.getJSONObject("before").getString("weight"));
                feed.putWeightAfter(Integer.parseInt(data.getJSONObject("after").getString("weight")));
                System.out.println(data.getJSONObject("after").getString("weight"));
                feed.putComment(data.getString("comment"));

                //Add feed types
                if (data.getString("type").equals("B")) {
                    feed.putType(0);
                } else if (data.getString("type").equals("E")) {
                    feed.putType(1);
                } else {
                    feed.putType(2);
                }

                if (data.getString("subtype").equals("U") || data.getString("subtype").equals("L") || data.getString("subtype").equals("E")) {
                    feed.putSubType(0);
                } else {
                    feed.putSubType(1);
                }

                //Submit feed
                int id = feed.getID();
                for(int j = 0; j < feedBackingArray.size(); j++) {
                    if(feedBackingArray.get(j) != null) {
                        if(feedBackingArray.get(j).getID() == id) {
                            //A feed with the same ID is already contained in the array
                            return false;
                        }
                    }
                }
                feedBackingArray.add(feed);
            }
        } catch (JSONException e) {
            return false;
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {


        private String feedData;

        UserLoginTask(String data) {
            feedData = data;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                //Thread.sleep(2000);
                System.out.println("Starting doInBackground.");
                URL url = new URL("https://breastfeeding.bcs.uwa.edu.au/milk/api/api.php?_action=add_feeds");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                System.out.println("Starting connection.");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("X-Mother-Username", mEmail);
                urlConnection.setRequestProperty("X-Mother-Password", mPassword);

                OutputStreamWriter out = new OutputStreamWriter(new BufferedOutputStream(urlConnection.getOutputStream()));

                StringBuilder sb = new StringBuilder();
                JSONObject jsonObject = new JSONObject(feedData);
                out.write(feedData);
                out.close();
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
                int HttpResult = urlConnection.getResponseCode();
                if(HttpResult == HttpURLConnection.HTTP_OK){
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println("Received: " + sb.toString());
                    jsonObject = new JSONObject(sb.toString());
                } else{

                    System.out.println(urlConnection.getResponseMessage());
                }

                if (sb.toString().contains("error")) {
                    System.out.print("Error ");
                    System.out.println(jsonObject.getJSONObject("code"));
                    System.out.println(jsonObject.getJSONObject("message"));
                    return false;
                }
                urlConnection.disconnect();

            } catch (MalformedURLException e) {
                return false;
            } catch (JSONException e) {
                return false;
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }*/

            // TODO: register the new account here.
            return true;
        }

        /**
         *
         * @param success login successful if password and username are corrected, otherwise show a error message
         */
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                finish();
            } else {

            }
        }

        /**
         *
         */
        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}
