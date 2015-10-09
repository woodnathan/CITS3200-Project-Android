package com.yifeilyf.breastfeeding_beta;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Josh on 20/09/2015.
 * Data object to record feeds
 */
public class Feed implements Parcelable {
    //TODO: declare all the required data variables
    private int data;
    private int ID;
    public Feed(int ID) {
        this.ID = ID;
    }

    /**
     *
     * @return Integer - The ID of the Feed object
     */
    public int getID(){return ID;}
    public void setData(int data) {
        this.data = data;

    }
    public int getData(){
        return data;
    }
    public String toString() {
        return "Feed " + getData();

    }


    /**
     Parcelable stuff below

     */

    private Feed(Parcel in) {
        ID = in.readInt();
        data = in.readInt();

    }
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(ID);
        out.writeInt(data);

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
