package com.mitrejcevski.widget.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model that is presenting one task.
 * 
 * @author jovche.mitrejchevski
 * 
 */
public class MyTask implements Parcelable {

	private int mId;
	private String mName;
	private boolean mShouldDelete = false;

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

	public void setShouldDelete(boolean shouldDelete) {
		mShouldDelete = shouldDelete;
	}

	public boolean shouldDelete() {
		return mShouldDelete;
	}

	public String toString() {
		return mName;
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
	}
}
