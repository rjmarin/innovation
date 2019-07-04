package com.example.lab.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.lab.R
import com.example.lab.db.AppDatabase
import kotlinx.android.synthetic.main.fragment_complaints_summary.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ComplaintsSummaryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_complaints_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateComplainsMetrics()
    }

    private fun populateComplainsMetrics() {
        val complaintDao = AppDatabase.getDatabase(context!!).complaintDao()
        GlobalScope.launch(Dispatchers.IO) {
            val amountOfComplaints = complaintDao.getAll().size
            launch(Dispatchers.Main) {
                activeComplaintsTextView.text = amountOfComplaints.toString()
            }
        }
    }
}
