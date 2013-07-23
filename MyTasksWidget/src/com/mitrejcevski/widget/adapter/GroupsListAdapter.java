package com.mitrejcevski.widget.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.mitrejcevski.widget.R;
import com.mitrejcevski.widget.activity.GroupsListActivity;
import com.mitrejcevski.widget.database.DBManipulator;
import com.mitrejcevski.widget.model.Group;

import java.util.ArrayList;

/**
 * Adapter for the groups list.
 *
 * @author jovche.mitrejchevski
 */
public class GroupsListAdapter extends BaseAdapter {

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private ArrayList<Group> mGroups = new ArrayList<Group>();

    /**
     * Constructor.
     *
     * @param context Context of the activity.
     */
    public GroupsListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    /**
     * Used to add tasks in the list.
     *
     * @param tasks An array list of tasks.
     */
    public void addGroups(ArrayList<Group> tasks) {
        mGroups = tasks;
        notifyDataSetChanged();
    }

    /**
     * Returns all the groups marked for deleting.
     *
     * @return
     */
    public ArrayList<Group> getMarkedGroups() {
        ArrayList<Group> groups = new ArrayList<Group>();
        for (Group group : mGroups) {
            if (group.shouldDelete())
                groups.add(group);
        }
        return groups;
    }

    @Override
    public int getCount() {
        return mGroups.size();
    }

    @Override
    public Group getItem(int position) {
        return mGroups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mGroups.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Group task = getItem(position);
        Holder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.groups_list_item, null);
            holder = new Holder();
            initializeHolder(convertView, holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        setupHolder(holder, task);
        return convertView;
    }

    /**
     * Initializes the holder of one row item.
     *
     * @param convertView The view representing one row item.
     * @param holder      Model class that keeps instances of the views needed in one
     *                    particular row item.
     */
    private void initializeHolder(View convertView, Holder holder) {
        holder.mLabel = (TextView) convertView.findViewById(R.id.group_title_label);
        holder.mEditButton = (Button) convertView.findViewById(R.id.button_group_edit);
        holder.mMarkButton = (Button) convertView.findViewById(R.id.button_group_remark);
        convertView.setTag(holder);
    }

    /**
     * Puts data in the particular row.
     *
     * @param holder The holder that is representing the row.
     * @param group  The group that should be presented in this row.
     */
    private void setupHolder(Holder holder, Group group) {
        holder.mLabel.setText(group.shouldDelete() ? getStrokedText(group.getGroupTitle()) : group.getGroupTitle());
        setEditAction(holder, group);
        setRemarkAction(holder, group);
    }

    private void setEditAction(final Holder holder, final Group group) {
        holder.mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GroupsListActivity) mContext).openGroupEditor(group);
            }
        });
    }

    private void setRemarkAction(final Holder holder, final Group group) {
        holder.mMarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                group.setShoudlDelete(!group.shouldDelete());
                DBManipulator.INSTANCE.saveGroup(mContext, group);
                notifyDataSetChanged();
                ((GroupsListActivity) mContext).notifyWidget();
            }
        });
    }

    private SpannableString getStrokedText(final String text) {
        SpannableString stroked = new SpannableString(text);
        stroked.setSpan(new StrikethroughSpan(), 0, stroked.length(), Spanned.SPAN_PARAGRAPH);
        return stroked;
    }

    /**
     * Model class that keeps instances of the views needed in one particular
     * row of the list view.
     *
     * @author jovche.mitrejchevski
     */
    class Holder {
        private TextView mLabel;
        private Button mEditButton;
        private Button mMarkButton;
    }
}
