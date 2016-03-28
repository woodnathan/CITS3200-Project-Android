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
    private int type = -1; //(0,1,2) breastfeed expressed supplementary
    private int subType = -1; // (0,1) left,right or expressed,supplementary
    private int weightBefore = -1;
    private int weightAfter = -1;
    private String comment = "";
    private Calendar startCal = Calendar.getInstance();
    private Calendar endCal = Calendar.getInstance();

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
    public int getType() {return type;}
    /**
     *
     * @return
     */
    public void putType(int type) {this.type = type;}
    /**
     *
     * @return
     */
    public int getSubType() {return subType;}
    /**
     *
     * @return
     */
    public void putSubType(int type) {subType = type;}
    /**
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
        return formatDateTime.format(getStartCal().getTime());
    }


    /**
     Parcelable stuff below

     */

    private Feed(Parcel in) {
        ID = in.readInt();
        type= in.readInt();
        subType= in.readInt();
        weightBefore= in.readInt();
        weightAfter= in.readInt();
        comment= in.readString();
        startCal.setTimeInMillis(in.readLong());
        endCal.setTimeInMillis(in.readLong());

    }
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(ID);
        out.writeInt(type);
        out.writeInt(subType);
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
