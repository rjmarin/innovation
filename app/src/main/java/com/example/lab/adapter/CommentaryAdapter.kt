package com.example.lab.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.lab.R
import com.example.lab.db.models.Commentary

class CommentaryAdapter(
    context: Context,
    private val dataSource: ArrayList<Commentary>) : BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.list_item_commentary, parent, false)
        rowView.findViewById<TextView>(R.id.ownerTextView).text = dataSource[position].userEmail
        rowView.findViewById<TextView>(R.id.dateTextView).text = dataSource[position].createdAt
        rowView.findViewById<TextView>(R.id.commentaryTextView).text = dataSource[position].commentary
        return rowView
    }
}