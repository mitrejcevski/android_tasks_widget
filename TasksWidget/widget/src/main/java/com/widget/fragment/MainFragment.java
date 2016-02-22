package com.widget.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.widget.R;
import com.widget.activity.MainActivity;
import com.widget.adapter.TasksListAdapter;
import com.widget.model.MyTask;

import java.util.List;

public class MainFragment extends Fragment implements View.OnClickListener {

    private MainActivity mActivity;
    private TasksListAdapter mTaskListAdapter;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main_content, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ListView taskListView = (ListView) view.findViewById(R.id.task_list_view);
        taskListView.setEmptyView(view.findViewById(R.id.empty));
        mTaskListAdapter = new TasksListAdapter(mActivity);
        taskListView.setAdapter(mTaskListAdapter);
        setEmptyClickListener(view);
    }

    public void applyData(final List<MyTask> tasks) {
        mTaskListAdapter.setTasks(tasks);
    }

    private void setEmptyClickListener(final View view) {
        Button emptyItem = (Button) view.findViewById(R.id.empty_add_new_button);
        emptyItem.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.empty_add_new_button:
                mActivity.openNewTaskActivity();
                break;
        }
    }
}
