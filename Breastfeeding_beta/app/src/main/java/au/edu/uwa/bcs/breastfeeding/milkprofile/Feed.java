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
    private String subType = "U"; // (0,1) left/right/both or expressed/supplementary
    private String side = "U";
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
    /**d
     *
     * @return
     */
    public int getWeightBefore() {return weightBefore;}
    /**
     *
     * @return
     */
    public void putWeightBefore(int weight) {weightBefore = weight;}
    /**
     *
     * @return
     */
    public int getWeightAfter() {return weightAfter;}
    /**
     *
     * @return
     */
    public void putWeightAfter(int weight) {weightAfter = weight;}
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
