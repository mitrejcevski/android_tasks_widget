package com.mitrejcevski.widget.model;

import java.util.Calendar;

import android.os.Parcel;
import android.os.Parcelable;

import com.mitrejcevski.widget.utilities.Constants;

/**
 * Model that is presenting one task.
 * 
 * @author jovche.mitrejchevski
 * 
 */
public class MyTask implements Parcelable {

	private int mId;
	private String mName;
	private boolean mIsFinished = false;
	private Calendar mDateTime;
	private boolean mHasTimeAttached = false;

	// Do this model parcelable.
	public static final Parcelable.Creator<MyTask> CREATOR = new Parcelable.Creator<MyTask>() {
		public MyTask createFromParcel(Parcel in) {
			return new MyTask(in);
		}

		public MyTask[] newArray(int size) {
			return new MyTask[size];
		}
	};

	/**
	 * Constructor from parcel.
	 * 
	 * @param parcel
	 *            Parcel that keeps task object.
	 */
	public MyTask(Parcel parcel) {
		mId = parcel.readInt();
		mName = parcel.readString();
		mIsFinished = parcel.readInt() == 1 ? true : false;
		mDateTime = Calendar.getInstance();
		mDateTime.setTimeInMillis(parcel.readLong());
		mHasTimeAttached = parcel.readInt() == 1 ? true : false;
	}

	/**
	 * Empty constructor.
	 */
	public MyTask() {

	}

	// Getters and Setters.
	public void setId(int id) {
		mId = id;
	}

	public int getId() {
		return mId;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getName() {
		return mName;
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

	public boolean hasTimeAttached() {
		return mHasTimeAttached;
	}

	public void setHasTimeAttached(boolean hasTimeAttached) {
		mHasTimeAttached = hasTimeAttached;
	}

	public String toString() {
		String date = mDateTime == null ? "" : Constants.FORMATTER
				.format(mDateTime.getTime());
		return mName + " " + date;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * Creates parcel from a task model.
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mId);
		dest.writeString(mName);
		dest.writeInt(mIsFinished ? 1 : 0);
		if (mDateTime != null)
			dest.writeLong(mDateTime.getTimeInMillis());
		dest.writeInt(mHasTimeAttached ? 1 : 0);
	}
}
