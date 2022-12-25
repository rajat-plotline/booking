package com.rajat.booking

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rajat.booking.Fragments.Bookings
import com.rajat.booking.Fragments.Dashboard
import com.rajat.booking.Fragments.Profile
import com.rajat.booking.Database.EventDatabase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DBAsyncTask(this@MainActivity).execute().get()

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, Dashboard()).commit()
        bottom_navigation.setOnNavigationItemSelectedListener(navListener);

        bottom_navigation.setOnNavigationItemReselectedListener(navReListener)

    }

    private val navListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.nav_dashboard -> selectedFragment = Dashboard()
                R.id.nav_bookings -> selectedFragment = Bookings()
                R.id.nav_profile -> selectedFragment = Profile()
            }
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, selectedFragment!!)
                .commit()
            true
        }

    private val navReListener =
        BottomNavigationView.OnNavigationItemReselectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.nav_dashboard -> selectedFragment = Dashboard()
                R.id.nav_bookings -> selectedFragment = Bookings()
                R.id.nav_profile -> selectedFragment = Profile()
            }
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, selectedFragment!!)
                .commit()
        }

    class DBAsyncTask(val context: Context) :
        AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, EventDatabase::class.java, "event-db").fallbackToDestructiveMigration().build()
        override fun doInBackground(vararg p0: Void?): Boolean {
            db.eventDao().deleteAllEvent()
            db.close()
            return true
        }
    }
}