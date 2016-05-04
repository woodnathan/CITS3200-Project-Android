package au.edu.uwa.bcs.breastfeeding.milkprofile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.yifeilyf.breastfeeding_beta.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by yifei on 9/17/2015.
 */
public class login extends Activity{

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //remove notification bar
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //set content view after above sequence (to avoid crash)
        this.setContentView(R.layout.login);

        setContentView(R.layout.login);

        mEmailView = (EditText) findViewById(R.id.tvLogin);
        mPasswordView = (EditText) findViewById(R.id.tvPassword);

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
                //Intent intent = new Intent(v.getContext(), list.class);
                //startActivity(intent);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        //locked screen on portraity
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //About button, return to instruction page when it is clicked
        Button aboutButton = (Button) findViewById(R.id.btnAbout);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), about.class);
                startActivity(intent);
            }
        });

        //hide the keyboard when click outside the EditView
        LinearLayout layout = (LinearLayout) findViewById(R.id.login);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent ev) {
                hideKeyboard(view);
                return false;
            }
        });

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
     *  This method include login validation
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_user));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * This method is validation of email
     * @param email the email is typed by user
     * @return true if length of email is larger than 3, otherwise false
     */
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.length() > 0;
    }

    /**
     * This method is validation of password
     * @param password the password is typed by user
     * @return true is length of password is larger than 3, otherwise false
     */
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 0;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     *
     */
    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    public static class UserLoginTaskResult {
        public static UserLoginTaskResult Success = new UserLoginTaskResult("Success");
        public static UserLoginTaskResult IncorrectPassword = new UserLoginTaskResult("Incorrect Password");

        private final String mMessage;

        public UserLoginTaskResult(String message) {
            mMessage = message;
        }

        public String getMessage() {
            return mMessage;
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, UserLoginTaskResult> {

        private final String mEmail;
        private final String mPassword;
        private String feedData;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected UserLoginTaskResult doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                //Thread.sleep(2000);
                System.out.println("Starting doInBackground.");
                URL url = new URL("https://breastfeeding.bcs.uwa.edu.au/milk/api/api.php?_action=user_info");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    System.out.println("Starting connection.");

                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("Accept", "application/json");
                    urlConnection.setRequestProperty("X-Mother-Username", mEmail);
                    urlConnection.setRequestProperty("X-Mother-Password", mPassword);
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);

                    StringBuilder sb = new StringBuilder();
                    JSONObject jsonObject = new JSONObject();
                    int HttpResult = urlConnection.getResponseCode();
                    if(HttpResult == HttpURLConnection.HTTP_OK){
                        BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }

                        br.close();

                        System.out.println("Received: " + sb.toString());
                        jsonObject = new JSONObject(sb.toString());
                        sb = new StringBuilder();
                    }else{
                        System.out.println(urlConnection.getResponseMessage());
                    }

                    if (sb.toString().contains("error")) {
                        System.out.print("Error ");
                        System.out.println(jsonObject.getJSONObject("code"));
                        System.out.println(jsonObject.getJSONObject("message"));
                        return UserLoginTaskResult.IncorrectPassword;
                    }

                    boolean collectingSamples = false;
                    boolean acceptedConsentForm = false;
                    try {
                        JSONObject userObj = jsonObject.getJSONObject("user");
//                        collectingSamples = userObj.getBoolean("collecting_samples");
                        acceptedConsentForm = userObj.getBoolean("accepted_consent_form");
                    } catch (JSONException e) {
                        System.out.println(e.getMessage());
                        return UserLoginTaskResult.IncorrectPassword;
                    }
                    urlConnection.disconnect();

                    if (acceptedConsentForm)
                    {
                        url = new URL("https://breastfeeding.bcs.uwa.edu.au/milk/api/api.php?_action=get_feeds");
                        urlConnection = (HttpURLConnection) url.openConnection();
                        System.out.println("Starting get_feeds.");

                        urlConnection.setRequestMethod("POST");
                        urlConnection.setRequestProperty("Content-Type", "application/json");
                        urlConnection.setRequestProperty("Accept", "application/json");
                        urlConnection.setRequestProperty("X-Mother-Username", mEmail);
                        urlConnection.setRequestProperty("X-Mother-Password", mPassword);
                        urlConnection.setDoInput(true);
                        urlConnection.setDoOutput(true);

                        HttpResult = urlConnection.getResponseCode();
                        if(HttpResult == HttpURLConnection.HTTP_OK){
                            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
                            String line = null;
                            while ((line = br.readLine()) != null) {
                                sb.append(line + "\n");
                            }

                            br.close();

                            System.out.println("Received: " + sb.toString());
                            feedData = sb.toString();
                        } else {
                            System.out.println(urlConnection.getResponseMessage());
                        }
                    }
//                    else if (!collectingSamples) {
//                        return new UserLoginTaskResult("");
//                    }
                    else
                    {
                        return new UserLoginTaskResult("You have not accepted the consent form. Please login to the website first and accept the form before logging in here.");
                    }
                } finally {
                    urlConnection.disconnect();
                }

            } catch (MalformedURLException e) {
                return UserLoginTaskResult.IncorrectPassword;
            } catch (IOException e) {
                return UserLoginTaskResult.IncorrectPassword;
            } catch (JSONException e) {
                return UserLoginTaskResult.IncorrectPassword;
            }

            // TODO: register the new account here.
            return UserLoginTaskResult.Success;
        }

        /**
         *
         * @param result login successful if password and username are corrected, otherwise show a error message
         */
        @Override
        protected void onPostExecute(final UserLoginTaskResult result) {
            mAuthTask = null;
            showProgress(false);

            if (result == UserLoginTaskResult.Success) {
                Intent intent = new Intent(login.this, list.class);
                intent.putExtra("mEmail", mEmail);
                intent.putExtra("mPassword", mPassword);
                intent.putExtra("feedData", feedData);
                startActivity(intent);
                finish();
            } else {
                String errorString = result.getMessage();
                if (result == UserLoginTaskResult.IncorrectPassword)
                    errorString = getString(R.string.error_incorrect_password);

                mPasswordView.setError(errorString);
                mPasswordView.requestFocus();
            }
        }

        /**
         *
         */
        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}
