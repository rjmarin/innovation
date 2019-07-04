package com.example.lab.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.lab.R
import com.example.lab.db.models.Complaint
import com.example.lab.db.models.Product

class ComplaintAdapter(
    context: Context,
    private val dataSource: ArrayList<Product>) : BaseAdapter() {

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

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.list_item_complaint, parent, false)
        rowView.findViewById<TextView>(R.id.nameTextView).text = dataSource[position].name
        rowView.findViewById<TextView>(R.id.dateTextView).text = dataSource[position].price.toString() + "/Kg"
        if(dataSource[position].weight > 0) {
            rowView.findViewById<TextView>(R.id.complaintTextView).text = dataSource[position].weight.toString()
        }else {
            rowView.findViewById<TextView>(R.id.complaintTextView).text = ""
        }

        return rowView
    }
}