package com.example.lab.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore

import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.lab.CredentialsManager

import com.example.lab.R
import com.example.lab.RequestCode
import com.example.lab.db.AppDatabase
import com.example.lab.db.models.Complaint
import com.example.lab.db.models.User
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_complaint_details.*
import kotlinx.android.synthetic.main.fragment_make_complaint.*
import kotlinx.android.synthetic.main.fragment_make_complaint.commentaryEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class MakeComplaintFragment : Fragment() {
    // Aux map to manage months
    private val MONTHS = mapOf(0 to "01", 1 to "02", 2 to "03", 3 to "04", 4 to "05",
        5 to "06", 6 to "07", 7 to "08", 8 to "09", 9 to "10", 10 to "11", 11 to "12"
    )
    private lateinit var datePickerDialog: DatePickerDialog
    private var currentPhotoPath: String? = null // temp store of photo path
    private var latitude: String = ""
    private var longitude: String = ""

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val RECORD_REQUEST_CODE = 101
    private lateinit var locationCallback: LocationCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_make_complaint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDatePickerDialog()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        val permission = ContextCompat.checkSelfPermission(activity!!,android.Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission== PackageManager.PERMISSION_GRANTED){
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null){
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                    }
                }

        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    // Update UI with location data
                    // ...
                }
            }
        }

        addListeners()
    }

    @SuppressLint("SetTextI18n")
    private fun initDatePickerDialog() {
        // Initial date setup
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        // Setting initial value that will get displayed
        val zeroPaddedDayOfMonth = if (currentDay.toString().length == 1) "0$currentDay" else "$currentDay"
        selectedDateTextView.text = "$currentYear-${MONTHS[currentMonth]}-$zeroPaddedDayOfMonth"

        // Setting DatePickerDialog Config
        datePickerDialog = DatePickerDialog(context!!,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val zeroPaddedDayOfMonth = if (dayOfMonth.toString().length == 1) "0$dayOfMonth" else "$dayOfMonth"
                selectedDateTextView.text = "$year-${MONTHS[monthOfYear]}-$zeroPaddedDayOfMonth"
            }, currentYear, currentMonth, currentDay)

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel") { dialog, which ->
            if (which == DialogInterface.BUTTON_NEGATIVE) {
                Log.d("Cancel", "OK")
            }
        }
    }

    private fun addListeners() {
        createComplaintButton.setOnClickListener {
            createComplaint()
        }

        takePictureImageButton.setOnClickListener {
            if (hasCamera()) {
                dispatchToCameraActivity()
            } else {
                Toast.makeText(context!!, "Your device doesn't have a camera built in", Toast.LENGTH_LONG).show()
            }
        }

        calendarButton.setOnClickListener {
            datePickerDialog.show()
        }

        selectedDateTextView.setOnClickListener {
            datePickerDialog.show()
        }
    }

    private fun createComplaint() {
        val complaintObject = createComplaintObject()
        storeComplaintObject(complaintObject)
    }

    @SuppressLint("MissingPermission")
    private fun createComplaintObject(): Complaint {
        // Should validate entries
        val name = nameEditText.text.toString()
        val date = selectedDateTextView.text.toString()
        val commentary = commentaryEditText.text.toString()

        val currentUserEmail = CredentialsManager.getInstance(context!!).loadUser()!!.first
        return Complaint(name, date, commentary, currentUserEmail, currentPhotoPath, 1.0, 1.0)
    }

    private fun storeComplaintObject(complaint: Complaint) {
        val complaintDao = AppDatabase.getDatabase(context!!).complaintDao()
        GlobalScope.launch(Dispatchers.IO) {  // replaces doAsync (runs on another thread)
            try {
                complaintDao.insertAll(complaint)
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "Saved successfully", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    Log.d("ERROR", "Error storing complaint ${e.message}")
                    Toast.makeText(context, "Error storing complaint ${e.message}" , Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    /**
    Check if device has camera at runtime (since the camera is optional)
     */
    private fun hasCamera(): Boolean {
        return context!!.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    /**
     *  Starting activity with the correct fileURI in which the photo will be stored
     */
    private fun dispatchToCameraActivity() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(context!!.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Log.d("ERROR", "Error creating file: ${ex.message}")
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context!!,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, RequestCode.REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    /**
     *  Creating temporary File in which the image will be stored
     */
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    /**
     *  Handle response from ACTION_IMAGE_CAPTURE Intent
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode.REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            setPic()
        }
    }

    /**
     *  Load Picture into ImageButton
     */
    private fun setPic() {
        // Get the dimensions of the View
        takePictureImageButton.setPadding(0, 0, 0, 0)
        val targetW: Int = takePictureImageButton.width
        val targetH: Int = takePictureImageButton.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(currentPhotoPath, this)
            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            takePictureImageButton.setImageBitmap(bitmap)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()

    }
    private fun stopLocationUpdates() {
        fusedLocationClient!!.removeLocationUpdates(locationCallback)

    }


}
