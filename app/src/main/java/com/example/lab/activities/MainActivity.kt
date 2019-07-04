package com.example.lab.activities

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.view.MenuItem
import com.example.lab.CredentialsManager
import com.example.lab.R
import com.example.lab.RequestCode
import com.example.lab.db.AppDatabase
import com.example.lab.db.models.User
import com.example.lab.fragments.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var currentLoadedFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setToolbar()
        loadUserDataOrSendToLoginActivity()
    }

    private fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)
        }
        setupNavViewListener()
    }

    private fun setupNavViewListener() {
        navView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            drawerLayout.closeDrawers()

            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here
            val transaction = supportFragmentManager.beginTransaction()

            when (menuItem.itemId) {
                R.id.home -> {
                    val homeFragment = supportFragmentManager.findFragmentByTag("homeFrag")
                    if (homeFragment != null) {
                        transaction.replace(R.id.contentFrameLayout, homeFragment)
                    } else {
                        transaction.replace(R.id.contentFrameLayout, HomeFragment(), "homeFrag")
                    }
                    supportActionBar!!.title = getString(R.string.action_bar_home_title)
                }



                R.id.complaints -> {
                    val complaintsFragment = supportFragmentManager.findFragmentByTag("complaintsFrag")
                    if (complaintsFragment != null) {
                        transaction.replace(R.id.contentFrameLayout, complaintsFragment)
                    } else {
                        transaction.replace(R.id.contentFrameLayout, ComplaintsFragment(), "complaintsFrag")
                    }
                    supportActionBar!!.title = "Product User"
                }

                R.id.othersComplaints -> {
                    val othersComplaintsFragment = supportFragmentManager.findFragmentByTag("othersComplaintsFrag")
                    if (othersComplaintsFragment != null) {
                        transaction.replace(R.id.contentFrameLayout, othersComplaintsFragment)
                    } else {
                        transaction.replace(R.id.contentFrameLayout, OthersComplaintsFragment(), "othersComplaintsFrag")
                    }
                    supportActionBar!!.title = "Product Corner"
                }

                R.id.complaintsSummary -> {
                    val complaintsSummaryFragment = supportFragmentManager.findFragmentByTag("complaintsSummaryFrag")
                    if (complaintsSummaryFragment != null) {
                        transaction.replace(R.id.contentFrameLayout, complaintsSummaryFragment)
                    } else {
                        transaction.replace(R.id.contentFrameLayout, ComplaintsSummaryFragment(), "complaintsSummaryFrag")
                    }
                    supportActionBar!!.title = "Cart"
                }

                R.id.signOut -> {
                    onSignOut()
                }
            }

            transaction.commit()
            true
        }
    }

    // Called when navDrawer is opened (used to display the menu)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadUserDataOrSendToLoginActivity() {
        val userData = loadUserData()
        if (userData != null) {
            initializeHomeFragment()
        } else {
            goToLoginActivity()
        }
    }

    private fun loadUserData(): Pair<String, String>? {
        return CredentialsManager.getInstance(baseContext).loadUser()
    }

    private fun initializeHomeFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contentFrameLayout, HomeFragment(), "homeFrag")
        transaction.commit()
        navView.menu.getItem(0).isChecked = true
        supportActionBar!!.title = getString(R.string.action_bar_home_title)
    }

    private fun onSignOut() {
        CredentialsManager.getInstance(baseContext).deleteUser()
        goToLoginActivity()
    }

    private fun goToLoginActivity() {
        // Clear current fragment to release memory.
        if (currentLoadedFragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.remove(currentLoadedFragment!!).commit()
        }
        // Initialize LogIn Activity
        startActivityForResult(
            Intent(this, LoginActivity::class.java),
            RequestCode.GO_TO_LOGIN_FROM_MAIN_ACTIVITY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RequestCode.GO_TO_LOGIN_FROM_MAIN_ACTIVITY -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        storeUserCredentials(data.extras!!)
                        initializeHomeFragment()
                    }
                }
            }
        }
    }

    private fun storeUserCredentials(bundle: Bundle) {
        val userEmail = bundle.getString("EMAIL")!!
        val userPassword = bundle.getString("PASSWORD")!!
        CredentialsManager.getInstance(baseContext).saveUser(userEmail, userPassword)
        GlobalScope.launch(Dispatchers.IO) {
            // Observation:
            // Since the user info is not coming from the API, (we are just storing it locally)
            // we have to previously check for it's existence so that we comply with UNIQUE
            // constraint "users.email"
            val user : User? = AppDatabase.getDatabase(baseContext).userDao().getUser(userEmail)
            if (user == null) {
                AppDatabase.getDatabase(baseContext).userDao().insertAll(User(userEmail))// Storing user,
                                                                                         // this should come from an API
            }
        }
    }
}
