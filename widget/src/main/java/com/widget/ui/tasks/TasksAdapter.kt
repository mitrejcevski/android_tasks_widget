package com.widget.ui.tasks

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.widget.R
import java.util.*

internal class TasksAdapter(val itemClick: (Task) -> Unit) : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    var items: List<Task> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TaskViewHolder? {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.task_list_item_layout, parent, false)
        return TaskViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: TaskViewHolder?, position: Int) {
        holder?.applyItem(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    internal class TaskViewHolder(val view: View, val itemClick: (Task) -> Unit) :
            RecyclerView.ViewHolder(view) {

        fun applyItem(task: Task) {
            itemView.setOnClickListener { itemClick(task) }
        }
    }
}