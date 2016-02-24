package com.widget.ui.groups

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.widget.R
import java.util.*

internal class GroupsAdapter(val onItemClick: (Group) -> Unit) : RecyclerView.Adapter<GroupsAdapter.GroupViewHolder>() {

    var items: List<Group> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): GroupViewHolder? {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.group_grid_item, parent, false)
        return GroupViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(viewHolder: GroupViewHolder?, position: Int) {
        viewHolder?.applyItem(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    internal class GroupViewHolder(val view: View, val itemClick: (Group) -> Unit) :
            RecyclerView.ViewHolder(view) {

        val label: TextView

        init {
            label = itemView.findViewById(R.id.groupItemTitleTextView) as TextView
        }

        fun applyItem(group: Group) {
            label.text = group.title
            itemView.setOnClickListener { itemClick(group) }
        }
    }

    private class EmptyClick : (Group) -> Unit {
        override fun invoke(group: Group) {

        }
    }
}
