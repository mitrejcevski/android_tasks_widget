package com.mitrejcevski.widget.model;

import com.mitrejcevski.widget.utilities.Constants;

import java.util.Calendar;

/**
 * Model that is presenting one task.
 *
 * @author jovche.mitrejchevski
 */
public class MyTask {

    private int mId;
    private String mName;
    private boolean mIsFinished = false;
    private Calendar mDateTime;
    private boolean mHasTimeAttached = false;
    private String mGroup;

    public int getId() {
        return mId;
    }

    // Getters and Setters.
    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public boolean isFinished() {
        return mIsFinished;
    }

    public void setFinished(boolean isFinished) {
        mIsFinished = isFinished;
    }

    public Calendar getDateTime() {
        return mDateTime;
    }

    public void setDateTime(Calendar calendar) {
        mDateTime = calendar;
    }

    public void setDateTime(long timeMillis) {
        if (mDateTime == null)
            mDateTime = Calendar.getInstance();
        mDateTime.setTimeInMillis(timeMillis);
    }

    public String getDateTimeString() {
        return mDateTime == null ? "" : Constants.FORMATTER.format(mDateTime.getTime());
    }

    public boolean hasTimeAttached() {
        return mHasTimeAttached;
    }

    public void setHasTimeAttached(boolean hasTimeAttached) {
        mHasTimeAttached = hasTimeAttached;
    }

    public String getGroup() {
        return mGroup;
    }

    public void setGroup(String group) {
        mGroup = group;
    }

    @Override
    public String toString() {
        return mName + " " + getDateTimeString();
    }
}
