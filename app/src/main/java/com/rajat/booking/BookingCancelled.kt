package com.rajat.booking

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rajat.booking.Adapter.BookingAdapter
import com.rajat.booking.Modal.Booking
import kotlinx.android.synthetic.main.activity_booking_cancelled.*

class BookingCancelled : AppCompatActivity() {

    var booking = ArrayList<Booking>()
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var adapter: BookingAdapter

    lateinit var fAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_cancelled)

        progress.visibility = View.VISIBLE

        layoutManager = LinearLayoutManager(this@BookingCancelled)
        fAuth = FirebaseAuth.getInstance()

        adapter = BookingAdapter(this@BookingCancelled,booking)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        back.setOnClickListener {
            onBackPressed()
        }


        FirebaseDatabase.getInstance().getReference("bookings/${fAuth.currentUser!!.uid}").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (postsnapshot in p0.children)
                {
                    val upload = postsnapshot.getValue(Booking::class.java)
                    if(upload!!.status=="cancelled") {
                        booking.add(upload)
                    }
                }
                if(booking.size==0){
                    no_events.visibility = View.VISIBLE
                }else{
                    no_events.visibility = View.GONE
                }
                adapter.notifyDataSetChanged()
                progress.visibility = View.GONE
            }
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@BookingCancelled,p0.message, Toast.LENGTH_SHORT).show()
                progress.visibility = View.GONE
            }
        })

    }
}