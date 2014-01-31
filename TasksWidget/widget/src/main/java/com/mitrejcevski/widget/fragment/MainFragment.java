package com.mitrejcevski.widget.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.mitrejcevski.widget.R;
import com.mitrejcevski.widget.activity.MainActivity;
import com.mitrejcevski.widget.adapter.TasksListAdapter;
import com.mitrejcevski.widget.model.MyTask;

import java.util.List;

/**
 * Fragment that represents the tasks list in the main activity.
 *
 * @author jovche.mitrejchevski.
 */
public class MainFragment extends Fragment implements View.OnClickListener {

    private MainActivity mActivity;
    private TasksListAdapter mTaskListAdapter;

    /**
     * Called when creating this fragment.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    /**
     * Called when creating the layout for this fragment.
     *
     * @param inflater           A layout inflater.
     * @param container          ViewGroup where the layout will be attached.
     * @param savedInstanceState The saved instance state bundle.
     * @return This fragment layout packed in a view object.
     */
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_main_content, container, false);
        loadUI(view);
        return view;
    }

    /**
     * Loads the layout components of this fragment.
     *
     * @param view The root view that is loaded in this fragment.
     */
    private void loadUI(final View view) {
        ListView taskListView = (ListView) view.findViewById(R.id.task_list_view);
        taskListView.setEmptyView(view.findViewById(R.id.empty));
        mTaskListAdapter = new TasksListAdapter(mActivity);
        taskListView.setAdapter(mTaskListAdapter);
        setEmptyClickListener(view);
    }

    /**
     * Applies new data into the tasks list adapter.
     *
     * @param tasks List of tasks.
     */
    public void applyData(final List<MyTask> tasks) {
        mTaskListAdapter.setTasks(tasks);
    }

    /**
     * Sets a listener the empty view button for adding new tasks.
     */
    private void setEmptyClickListener(final View view) {
        Button emptyItem = (Button) view.findViewById(R.id.empty_add_new_button);
        emptyItem.setOnClickListener(this);
    }

    /**
     * Called when user clicks on a view component that has registered on click listener from this
     * fragment.
     *
     * @param v The clicked view.
     */
    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.empty_add_new_button:
                mActivity.openNewTaskActivity();
                break;
        }
    }
}
