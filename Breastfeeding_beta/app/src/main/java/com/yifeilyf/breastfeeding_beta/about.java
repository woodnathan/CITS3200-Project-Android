package com.yifeilyf.breastfeeding_beta;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by yifei on 10/13/2015.
 */
public class about extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //remove notification bar
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //set content view after above sequence (to avoid crash)
        this.setContentView(R.layout.about);
        setContentView(R.layout.about);

        //Close button, return to login page when it is clicked
        Button btnClose = (Button) findViewById(R.id.tvClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), login.class);
                startActivity(intent);
            }
        });

        //locked screen on portraity
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //This TextView will hold the instruction
        TextView instruction = (TextView) findViewById(R.id.etInstruction);
        instruction.setText("Collecting milk production data\n" +
                "\n" +
                "Method of collecting data:\n" +
                "\n" +
                "Start the first recorded breastfeed at any convenient time. Note that the baby feeding from one breast constitutes one feed. If the baby feeds from the other breast immediately afterwards, this is a separate feed.\n" +
                "Place scales on an even surface. Turn scales on and wait for scales to zero.\n" +
                "Leave baby in normal clothes. For each feed, once the ‘before weight’ is recorded ensure the clothing, including nappy, is not changed until the ‘after weight’ is also completed.\n" +
                "Using Scales:\n" +
                "\n" +
                "Breastfeeding\n" +
                "\n" +
                "First breastfeed\n" +
                "\n" +
                "Place baby on the scales. The reading will lock when the baby is still for a moment.\n" +
                "The online table will automatically display the current date and time. Click the Breastfeed radio button, choose Left or Right breast, and then enter the weight.\n" +
                "Once the baby has finished feeding from this breast, place the baby on the scales. When the reading locks, enter the weight. You may enter a comment if you wish, then click on \"Submit\".\n" +
                "\n" +
                "Subsequent breastfeeds\n" +
                "\n" +
                "Click on the Breastfeed radio button, and choose Left or Right breast. If this feed was immediately following a feed from the other breast, retype the last weight. Otherwise, record the baby's current weight\n" +
                "After the feed, reweigh the baby and click \"Submit\".\n" +
                "Repeat this process for a 24 hour period plus one additional feed from each breast. There will be a popup alert once the 24 hour period with an additional feed is completed for each breast.\n" +
                "\n" +
                "Expressing\n" +
                "\n" +
                "Click on the Expressed radio button and then choose Left or Right breast. Weigh the empty bottle and enter the weight. After expressing weigh the bottle again and enter the weight.\n" +
                "\n" +
                "Supplementary Feeds\n" +
                "\n" +
                "Click on the Supplementary radio button and then choose E for Expressed breast milk or F for Formula. Weigh your baby and enter the weights as for a breastfeed.\n" +
                "\n" +
                "Notes\n" +
                "\n" +
                "If expressing during this 24 hour period, record the time the empty bottle and then the bottle with the expressed breast milk. As usual, the weight gain (quantity of milk) will be calculated automatically in the online table.");

    }


}
