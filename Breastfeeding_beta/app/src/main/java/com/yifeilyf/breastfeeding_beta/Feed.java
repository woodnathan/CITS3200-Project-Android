package com.yifeilyf.breastfeeding_beta;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Josh on 20/09/2015.
 * Data object to record feeds
 */
public class Feed implements Parcelable {

    private int ID;
    private String startDate = "";
    private String endDate = "";
    private String startTime = "";
    private String endTime =  "";
    private int type = -1; //(0,1,2) breastfeed expressed supplementary
    private int subType = -1; // (0,1) left,right or expressed,supplementary
    private double weightBefore = -1;
    private double weightAfter = -1;
    private String comment = "";

    /**
     *
     *
     */
    public Feed(int ID) {
        this.ID = ID;
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
