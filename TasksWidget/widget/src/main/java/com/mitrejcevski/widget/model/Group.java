package com.mitrejcevski.widget.model;

/**
 * Class used for representation for the groups.
 * 
 * @author jovche.mitrejchevski
 */
public class Group {

	private int mId;
	private String mGroupTitle;
	private boolean mShouldDelete;

	/* Getters and Setters */
	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

	public String getGroupTitle() {
		return mGroupTitle;
	}

	public void setGroupTitle(String groupTitle) {
		mGroupTitle = groupTitle;
	}

	public void setShoudlDelete(boolean shoudlDelete) {
		mShouldDelete = shoudlDelete;
	}

	public boolean shouldDelete() {
		return mShouldDelete;
	}

	@Override
	public String toString() {
		return mGroupTitle;
	}
}
