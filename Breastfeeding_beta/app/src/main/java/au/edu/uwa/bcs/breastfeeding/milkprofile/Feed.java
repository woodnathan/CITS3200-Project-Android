package au.edu.uwa.bcs.breastfeeding.milkprofile;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Josh on 20/09/2015.
 * Data object to record feeds
 */
public class Feed implements Parcelable, Comparable<Feed> {

    private int ID;
    private String type = ""; //(0,1,2) breastfeed expressed supplementary
    private String subType = ""; // (0,1) left,right or expressed,supplementary
    private String side = "";
    private int leftWeightBefore = -1;
    private int leftWeightAfter = -1;
    private int rightWeightBefore = -1;
    private int rightWeightAfter = -1;
    private int weightBefore = -1;  //redundant
    private int weightAfter = -1;   //redundant
    private String comment = "";
    private Calendar startCal = Calendar.getInstance();
    private Calendar endCal = Calendar.getInstance();
    private long feedTime = 0;


    private SimpleDateFormat formatDateTime = new SimpleDateFormat("dd/MM/yyyy  HH:mm");
    //displays as below
    //    16/21/2015  13:21
    //    16/21/2015  03:21


    /**
     * Creates a feed with no data.
     * @param ID Unique ID to assign to this feed object
     */
    public Feed(int ID) {
        this.ID = ID;
    }

    public int compareTo(Feed f) {
        return startCal.compareTo(f.getStartCal());
    }


    /**
     * Get start time of feed
     * @return
     */
    public Calendar getStartCal() {
        return startCal;
    }
    /**
     * Get end time of feed
     * @return
     */
    public Calendar getEndCal() {
        return endCal;
    }
    /**
     * Overwrite the current start date and time
     * @param cal calender object to write to the feed
     */
    public void putStartCal(Calendar cal) {
        startCal = cal;
    }
    /**
     *Overwrite the current end date and time
     * @param cal calender object to write to the feed
     */
    public void putEndCal(Calendar cal){
        endCal = cal;
    }



    /**
     *
     * @return
     */
    public String getType() {return type;}
    /**
     *
     * @return
     */
    public void putType(String type) {this.type = type;}
    /**
     *
     * @return
     */
    public String getSubType() {return subType;}
    /**
     *
     * @return
     */
    public void putSubType(String type) {subType = type;}
    /**
     *
     * @return
     */
    public String getSide() {return side;}
    /**
     *
     * @return
     */
    public void putSide(String side) {this.side = side;}
    /**
     *
     * @return
     */
    public int getRightWeightBefore() {return rightWeightBefore;}
    /**
     *
     * @return
     */
    public void putRightWeightBefore(int weight) {rightWeightBefore = weight; weightBefore = weight;}
    /**
     *
     * @return
     */
    public int getRightWeightAfter() {return rightWeightAfter;}
    /**
     *
     * @return
     */
    public void putRightWeightAfter(int weight) {rightWeightAfter = weight; weightAfter = weight;}
    /**d
     *
     * @return
     */
    public int getLeftWeightBefore() {return leftWeightBefore;}
    /**
     *
     * @return
     */
    public void putLeftWeightBefore(int weight) {leftWeightBefore = weight; weightBefore = weight;}
    /**
     *
     * @return
     */
    public int getLeftWeightAfter() {return leftWeightAfter;}
    /**
     *
     * @return
     */
    public void putLeftWeightAfter(int weight) {leftWeightAfter = weight; weightAfter = weight;}
    /**
     *
     * @return
     */
    public String getComment() {return comment;}
    /**
     *
     * @return
     */
    public void putComment(String comment) {this.comment = comment;}

    /**
     *
     * @return
     */
    public int getID(){return ID;}


    public String toString(){
        feedTime = (getEndCal().getTimeInMillis() - getStartCal().getTimeInMillis()) / 60000;
        return (formatDateTime.format(getStartCal().getTime()) + "\t\t(" + String.valueOf(feedTime)) + " mins)";
    }


    /**
     Parcelable stuff below

     */

    private Feed(Parcel in) {
        ID = in.readInt();
        type= in.readString();
        subType= in.readString();
        side= in.readString();
        weightBefore= in.readInt();
        weightAfter= in.readInt();
        comment= in.readString();
        startCal.setTimeInMillis(in.readLong());
        endCal.setTimeInMillis(in.readLong());

        if (side == "L") {
            leftWeightBefore = weightBefore;
            leftWeightAfter = weightAfter;
        } else {
            rightWeightBefore = weightBefore;
            rightWeightAfter = weightAfter;
        }
    }
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(ID);
        out.writeString(type);
        out.writeString(subType);
        out.writeString(side);
        out.writeInt(weightBefore);
        out.writeInt(weightAfter);
        out.writeString(comment);
        out.writeLong(startCal.getTimeInMillis());
        out.writeLong(endCal.getTimeInMillis());

    }

    @Override
    public int describeContents() {
        return 0;
    }
    public static final Parcelable.Creator<Feed> CREATOR
            = new Parcelable.Creator<Feed>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public Feed createFromParcel(Parcel in) {
            return new Feed(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public Feed[] newArray(int size) {
            return new Feed[size];
        }
    };

}
