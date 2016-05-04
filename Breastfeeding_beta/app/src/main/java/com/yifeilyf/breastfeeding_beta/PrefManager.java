package com.yifeilyf.breastfeeding_beta;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by yifei on 13/04/16.
 * This class is created to manager data on SharedPreference.
 */
public class PrefManager {

    Context context;

    PrefManager(Context context) {
        this.context = context;
    }

    /**
     *  This method stored account detail on LoginDetails file.
     */
    public void saveLoginDetails(String email, String password) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Email", email);
        editor.putString("Password", password);
        editor.commit();
    }

    /**
     *  This method gets account number from SharePreference.
     */
    public String getEmail() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Email", "");
    }

    /**
     *  This method gets password from SharePreference.
     */
    public String getPassword(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_APPEND);
        return sharedPreferences.getString("Password", "");
    }

}
