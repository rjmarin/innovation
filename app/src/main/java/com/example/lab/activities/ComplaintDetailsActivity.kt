package com.example.lab.activities

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import com.example.lab.CredentialsManager
import com.example.lab.R
import com.example.lab.adapter.CommentaryAdapter
import com.example.lab.db.AppDatabase
import com.example.lab.db.models.Commentary
import com.example.lab.db.models.ComplaintVote
import com.example.lab.db.models.VoteType
import kotlinx.android.synthetic.main.activity_complaint_details.*


import kotlinx.android.synthetic.main.list_item_complaint.complaintTextView
import kotlinx.android.synthetic.main.list_item_complaint.dateTextView
import kotlinx.android.synthetic.main.list_item_complaint.nameTextView

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;



class ComplaintDetailsActivity : AppCompatActivity() {


    // Will store at loading so that we don't have to fetch again
    // if the user creates a commentary
    private var currentComplaintId: Int = 0
    private var currentComplaintVote: ComplaintVote? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complaint_details)
        getCurrentComplaintValues()
    }

    override fun onResume() {
        super.onResume()
        setViewContent()
        setListeners()
    }

    private fun getCurrentComplaintValues() {
        currentComplaintId = intent.getIntExtra("COMPLAINT_ID", 1)
    }

    private fun setViewContent() {
        GlobalScope.launch(Dispatchers.IO) {
            val currentUserEmail = CredentialsManager.getInstance(baseContext).loadUser()!!.first
            val appDatabase = AppDatabase.getDatabase(baseContext)
            val complaintDao = appDatabase.productDao()
            val selectedComplaint = complaintDao.getProduct(currentComplaintId)



            launch(Dispatchers.Main) {
                nameTextView.text = selectedComplaint.name
                dateTextView.text = selectedComplaint.price.toString()
                complaintTextView.text = selectedComplaint.weight.toString()

                // A post sent to the view in order for it to wait until the view has been rendered (draw)


            }
        }
    }





    private fun setListeners() {

        //setCreateCommentaryButtonListener(sendCommentaryButton)

    }



    private fun setCreateCommentaryButtonListener(view: View) {
        view.setOnClickListener {
            // userEmail could be cached at the CredentialsManager when logging in
            val currentUserEmail = CredentialsManager.getInstance(baseContext).loadUser()!!.first

            val calendar = Calendar.getInstance()
            val currentDate = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DATE)}"
            //val newCommentary = Commentary(currentComplaintId, currentUserEmail, commentaryText.toString(), currentDate)
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    //AppDatabase.getDatabase(baseContext).commentaryDao().insertAll(newCommentary)
                    launch(Dispatchers.Main) {
                        Toast.makeText(baseContext, "Commentary created!", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    launch(Dispatchers.Main) {
                        Log.d("ERROR", "Error storing complaint ${e.message}")
                        Toast.makeText(baseContext, "Error storing complaint ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

}
