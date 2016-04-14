package com.jaspersoft.android.jaspermobile.ui.view.widget;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public abstract class WidgetStateDelegate<State extends Parcelable> {
    private final Class<State> type;
    private State state;

    public WidgetStateDelegate(Class<State> type) {
        this.type = type;
    }

    public abstract State provideState();

    @Nullable
    public State retrieveState() {
        return state;
    }

    public Parcelable onSaveInstanceState(Parcelable superState) {
        SavedState saveState = new SavedState(superState);
        saveState.state = provideState();
        saveState.type = type;
        return saveState;
    }

    @SuppressWarnings("unchecked")
    public Parcelable onRestoreInstanceState(Parcelable state) {
        boolean canDelegate = (state instanceof SavedState);
        if (canDelegate) {
            SavedState ss = (SavedState) state;
            this.state = (State) ss.state;
            return ss.getSuperState();
        }
        return state;
    }

    private static class SavedState extends View.BaseSavedState {
        Parcelable state;
        Class<? extends Parcelable> type;

        public SavedState(Parcel source) {
            super(source);
            state = source.readParcelable(type.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeParcelable(state, 0);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
