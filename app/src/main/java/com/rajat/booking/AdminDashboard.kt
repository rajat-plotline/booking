package com.rajat.booking

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rajat.booking.Adapter.EventAdapter
import com.rajat.booking.Modal.Events
import kotlinx.android.synthetic.main.activity_admin_dashboard.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdminDashboard : AppCompatActivity() {

    var events = ArrayList<Events>()
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var adapter: EventAdapter

    val today = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    private var mDay = 0
    private var mMonth = 0
    private var mYear = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        progress.visibility = View.VISIBLE

        recyclerView.setHasFixedSize(true)
        layoutManager = GridLayoutManager(this@AdminDashboard,2)

        txtDate.text = today.toString()

        val mCurrentDate = Calendar.getInstance()
        mYear = mCurrentDate[Calendar.YEAR]
        mMonth = mCurrentDate[Calendar.MONTH]
        mDay = mCurrentDate[Calendar.DAY_OF_MONTH]


        adapter = EventAdapter(this@AdminDashboard,events)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager


        txtDate.setOnClickListener {
            val mDatePicker = DatePickerDialog(
                this@AdminDashboard,
                { datepicker, selectedyear, selectedmonth, selectedday ->
                    val myCalendar = Calendar.getInstance()
                    myCalendar[Calendar.YEAR] = selectedyear
                    myCalendar[Calendar.MONTH] = selectedmonth
                    myCalendar[Calendar.DAY_OF_MONTH] = selectedday

                    val myFormat = "dd-MM-yyyy" //Change as you need
                    val sdf = SimpleDateFormat(myFormat, Locale.ENGLISH)

                    txtDate.text = sdf.format(myCalendar.time)

                    updateDashboard(txtDate.text.toString())

                    mDay = selectedday
                    mMonth = selectedmonth
                    mYear = selectedyear

                }, mYear, mMonth, mDay
            )
            mDatePicker.show()
        }

        add.setOnClickListener {
            startActivity(Intent(this@AdminDashboard,AdminAddEvent::class.java))
        }
    }

    private fun updateDashboard(date : String){

        progress.visibility = View.VISIBLE

        events.clear()
        FirebaseDatabase.getInstance().getReference("events/${date}").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (postsnapshot in p0.children)
                {
                    val upload = postsnapshot.getValue(Events::class.java)
                    events.add(upload!!)
                }

                if(events.size==0){
                    no_events.visibility = View.VISIBLE
                }else{
                    no_events.visibility = View.GONE
                }

                adapter.notifyDataSetChanged()
                progress.visibility = View.GONE

            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@AdminDashboard,p0.message, Toast.LENGTH_SHORT).show()
                progress.visibility = View.GONE
            }

        })

    }

    override fun onStart() {
        super.onStart()

        updateDashboard(today)
    }
}