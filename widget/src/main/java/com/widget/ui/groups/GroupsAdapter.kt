package com.widget.ui.groups

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.widget.R
import java.util.*

internal class GroupsAdapter : RecyclerView.Adapter<GroupsAdapter.GroupViewHolder>() {

    var items: List<Group> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): GroupViewHolder? {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.group_grid_item, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: GroupViewHolder?, position: Int) {
        viewHolder?.applyItem(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    internal class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val label: TextView

        init {
            label = itemView.findViewById(R.id.groupItemTitleTextView) as TextView
        }

        fun applyItem(group: Group) {
            label.text = group.title
        }
    }
}