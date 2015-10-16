package com.yifeilyf.breastfeeding_beta;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Josh on 20/09/2015.
 * Data object to record feeds
 */
public class Feed implements Parcelable, Comparable<Feed> {

    private int ID;
    private String startDate = "";
    private String endDate = "";
    private String startTime = "";
    private String endTime = "";
    private int type = -1; //(0,1,2) breastfeed expressed supplementary
    private int subType = -1; // (0,1) left,right or expressed,supplementary
    private double weightBefore = -1;
    private double weightAfter = -1;
    private String comment = "";
    private Calendar startCal = Calendar.getInstance();
    private Calendar endCal = Calendar.getInstance();


    /**
     *
     *
     */
    public Feed(int ID) {
        this.ID = ID;
    }

    //compares based on start date
    public int compareTo(Feed f) {
        //if same date
        if (f.getStartCal().get(Calendar.DAY_OF_YEAR) == (getStartCal().get(Calendar.DAY_OF_YEAR))) {
            //compare time
            int ft = f.getStartCal().get(Calendar.HOUR_OF_DAY) * 60 + f.getStartCal().get(Calendar.MINUTE);
            int tt = getStartCal().get(Calendar.HOUR_OF_DAY) * 60 + f.getStartCal().get(Calendar.MINUTE);
            return Integer.compare(tt, ft);
        } else {
            //compare dates
            /*SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy");
            try {
                Date fDate = format.parse(f.getStartDate());
                Date thisDate = format.parse(getStartDate());
                return thisDate.compareTo(fDate);
            } catch (Exception e) {
                //do nothing
            }*/
            return Integer.compare(getStartCal().get(Calendar.DAY_OF_YEAR),f.getStartCal().get(Calendar.DAY_OF_YEAR));
        }
        //something went wrong assume equal
        //return 0;
    }




    public Calendar getStartCal() {
        return startCal;
    }

    public Calendar getEndCal() {
        return endCal;
    }

    public void putStartCal(Calendar cal) {
        startCal = cal;

    }

    public void putEndCal(Calendar cal){
        endCal = cal;
    }


    /**
     *
     * @return
     */
    public String getStartDate() {return startDate;}
    /**
     *
     * @return
     */
    public void putStartDate(String date) {startDate = date;}
    /**
     *
     * @return
     */
    public String getEndDate() {return endDate;}
    /**
     *
     * @return
     */
    public void putEndDate(String date) {endDate = date;}
    /**
     *
     * @return
     */
    public String getStartTime() {return startTime;}
    /**
     *
     * @return
     */
    public void putStartTime(String time) {startTime = time;}
    /**
     *
     * @return
     */
    public String getEndTime() {return endTime;}
    /**
     *
     * @return
     */
    public void putEndTime(String time) {endTime = time;}
    /**
     *
     * @return
     */
    public int getType() {return type;}
    /**
     *
     * @return
     * @param type
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
     * @param type
     */
    public void putSubType(int type) {subType = type;}
    /**
     *
     * @return
     */
    public double getWeightBefore() {return weightBefore;}
    /**
     *
     * @return
     */
    public void putWeightBefore(double weight) {weightBefore = weight;}
    /**
     *
     * @return
     */
    public double getWeightAfter() {return weightAfter;}
    /**
     *
     * @return
     */
    public void putWeightAfter(double weight) {weightAfter = weight;}
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


    public String toString() {
        return getStartDate() + " " + getStartTime();
    }


    /**
     Parcelable stuff below

     */

    private Feed(Parcel in) {
        ID = in.readInt();
        startDate = in.readString();
        endDate = in.readString();
        startTime= in.readString();
        endTime= in.readString();
        type= in.readInt();
        subType= in.readInt();
        weightBefore= in.readDouble();
        weightAfter= in.readDouble();
        comment= in.readString();
        startCal.setTimeInMillis(in.readLong());
        endCal.setTimeInMillis(in.readLong());

    }
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(ID);
        out.writeString(startDate);
        out.writeString(endDate);
        out.writeString(startTime);
        out.writeString(endTime);
        out.writeInt(type);
        out.writeInt(subType);
        out.writeDouble(weightBefore);
        out.writeDouble(weightAfter);
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
