package com.jaspersoft.android.jaspermobile.ui.page;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public class BasePageState implements Parcelable {
    private boolean mShouldExit;

    public void setShouldExit(boolean shouldExit) {
        mShouldExit = shouldExit;
    }

    public boolean shouldExit() {
        return mShouldExit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(mShouldExit ? (byte) 1 : (byte) 0);
    }

    public BasePageState() {
    }

    protected BasePageState(Parcel in) {
        this.mShouldExit = in.readByte() != 0;
    }

    public static final Parcelable.Creator<BasePageState> CREATOR = new Parcelable.Creator<BasePageState>() {
        public BasePageState createFromParcel(Parcel source) {
            return new BasePageState(source);
        }

        public BasePageState[] newArray(int size) {
            return new BasePageState[size];
        }
    };
}
