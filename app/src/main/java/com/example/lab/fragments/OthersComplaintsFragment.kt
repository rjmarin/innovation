package com.example.lab.fragments


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lab.CredentialsManager

import com.example.lab.R
import com.example.lab.db.AppDatabase

import com.example.lab.activities.ComplaintDetailsActivity
import com.example.lab.adapter.ComplaintAdapter
import com.example.lab.db.models.Complaint
import com.example.lab.db.models.Product
import kotlinx.android.synthetic.main.fragment_complaints.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class OthersComplaintsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_complaints, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadComplaints()
        setListOnClickListener()
    }


    private fun loadComplaints() {
        val productDao = AppDatabase.getDatabase(context!!).productDao()
        GlobalScope.launch(Dispatchers.IO) { // replaces doAsync (runs on another thread)
            val p1= Product(1,"Platano",2200,(1.2).toFloat())
            productDao.insertAll(p1)
            var p2= Product(2,"Palta",4000,(1.2).toFloat())
            productDao.insertAll(p2)
            var p3 = Product(3,"Pera",1500,(1.2).toFloat())
            productDao.insertAll(p3)
            var p4 = Product(4,"Manzana",1800,(1.2).toFloat())
            productDao.insertAll(p4)

            val complaints = mutableListOf<Product>()
            complaints.add(p1)
            complaints.add(p2)
            complaints.add(p3)
            complaints.add(p4)



            launch(Dispatchers.Main) {// replaces uiThread (runs on UIThread)
                val itemsAdapter = ComplaintAdapter(context!!, ArrayList(complaints))
                complaintsListView.adapter = itemsAdapter
            }
        }
    }

    private fun setListOnClickListener() {
        complaintsListView.setOnItemClickListener { _, _, position, _ ->
            val selectedComplaint = (complaintsListView.adapter).getItem(position) as Complaint
            startActivity(
                Intent(context, ComplaintDetailsActivity::class.java).
                    putExtra("COMPLAINT_ID", selectedComplaint.id))
        }
    }

}
