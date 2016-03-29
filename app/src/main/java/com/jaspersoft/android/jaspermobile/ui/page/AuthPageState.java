package com.jaspersoft.android.jaspermobile.ui.page;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public class AuthPageState implements Parcelable {
    private boolean mLoading;

    public boolean isLoading() {
        return mLoading;
    }

    public void setLoading(boolean loading) {
        mLoading = loading;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(mLoading ? (byte) 1 : (byte) 0);
    }

    public AuthPageState() {
    }

    protected AuthPageState(Parcel in) {
        this.mLoading = in.readByte() != 0;
    }

    public static final Creator<AuthPageState> CREATOR = new Creator<AuthPageState>() {
        public AuthPageState createFromParcel(Parcel source) {
            return new AuthPageState(source);
        }

        public AuthPageState[] newArray(int size) {
            return new AuthPageState[size];
        }
    };
}
