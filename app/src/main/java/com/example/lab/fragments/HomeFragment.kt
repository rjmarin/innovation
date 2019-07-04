package com.example.lab.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lab.CredentialsManager

import com.example.lab.R
import kotlinx.android.synthetic.main.fragment_home.*
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.*

class HomeFragment : Fragment() {
    internal lateinit var myLabel: TextView
    internal lateinit var myTextbox: EditText
    internal var mBluetoothAdapter: BluetoothAdapter? = null
    internal lateinit var mmSocket: BluetoothSocket
    internal lateinit var mmDevice: BluetoothDevice
    internal lateinit var mmOutputStream: OutputStream
    internal lateinit var mmInputStream: InputStream
    internal lateinit var workerThread: Thread
    internal lateinit var readBuffer: ByteArray
    internal var readBufferPosition: Int = 0
    internal var counter: Int = 0
    @Volatile
    internal var stopWorker: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCredentials()

        open.setOnClickListener {
            try {
                findBT()
                openBT()
            } catch (ex: IOException) {
            }
        }



        //Close button
        close.setOnClickListener {
            try {
                closeBT()
            } catch (ex: IOException) {
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setCredentials()
    }

    private fun setCredentials() {
        val credentials = CredentialsManager.getInstance(context!!).loadUser()
        val prependPlaceholder = getString(R.string.home_fragment_welcome_prepend)
        val welcomeMessage = "$prependPlaceholder ${credentials!!.first}"
        welcomeMessageTextView.text = welcomeMessage
    }

    internal fun findBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            myLabel.text = "No bluetooth adapter available"
        }

        if (!mBluetoothAdapter!!.isEnabled) {
            val enableBluetooth = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetooth, 0)
        }

        val pairedDevices = mBluetoothAdapter!!.bondedDevices
        if (pairedDevices.size > 0) {
            for (device in pairedDevices) {
                if (device.name == "HC-06") {
                    mmDevice = device
                    break
                }
            }
        }
        myLabel.text = "Bluetooth Device Found"
    }

    @Throws(IOException::class)
    internal fun openBT() {
        val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid)
        mmSocket.connect()
        mmOutputStream = mmSocket.outputStream
        mmInputStream = mmSocket.inputStream

        beginListenForData()

        myLabel.text = "Bluetooth Opened"
    }

    internal fun beginListenForData() {
        val handler = Handler()
        val delimiter: Byte = 10 //This is the ASCII code for a newline character

        stopWorker = false
        readBufferPosition = 0
        readBuffer = ByteArray(1024)
        workerThread = Thread(Runnable {
            while (!Thread.currentThread().isInterrupted && !stopWorker) {
                try {
                    val bytesAvailable = mmInputStream.available()
                    if (bytesAvailable > 0) {
                        val packetBytes = ByteArray(bytesAvailable)
                        mmInputStream.read(packetBytes)
                        for (i in 0 until bytesAvailable) {
                            val b = packetBytes[i]
                            if (b == delimiter) {
                                val encodedBytes = ByteArray(readBufferPosition)
                                System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.size)
                                val data = String(encodedBytes, Charset.forName("US-ASCII"))
                                readBufferPosition = 0

                                handler.post(Runnable { myLabel.text = data })
                            } else {
                                readBuffer[readBufferPosition++] = b
                            }
                        }
                    }
                } catch (ex: IOException) {
                    stopWorker = true
                }

            }
        })

        workerThread.start()
    }

    @Throws(IOException::class)
    internal fun sendData() {
        var msg = myTextbox.text.toString()
        msg += "\n"
        mmOutputStream.write(msg.toByteArray())
        myLabel.text = "Data Sent"
    }

    @Throws(IOException::class)
    internal fun closeBT() {
        stopWorker = true
        mmOutputStream.close()
        mmInputStream.close()
        mmSocket.close()
        myLabel.text = "Bluetooth Closed"
    }
}
