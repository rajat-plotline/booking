package com.rajat.booking.Fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rajat.booking.Adapter.BookingAdapter
import com.rajat.booking.BookingCancelled
import com.rajat.booking.Modal.Booking
import com.rajat.booking.R

class Bookings : Fragment() {

    var booking = ArrayList<Booking>()
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var adapter: BookingAdapter

    lateinit var fAuth : FirebaseAuth

    lateinit var progress : RelativeLayout
    lateinit var noEvents : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bookings, container, false)

        progress = view.findViewById(R.id.progress)
        noEvents = view.findViewById(R.id.no_events)

        recyclerView = view.findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(context as Activity)
        fAuth = FirebaseAuth.getInstance()

        adapter = BookingAdapter(context as Activity,booking)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        view.findViewById<TextView>(R.id.cancel_bookings).setOnClickListener {
            startActivity(Intent(context,BookingCancelled::class.java))
        }
        update()

        adapter.setOnItemClickListener(object : BookingAdapter.onItemClickListener {
            override fun onClick() {
                update()
            }
        })
        return view
    }

    fun update(){

        booking.clear()
        progress.visibility = View.VISIBLE
        FirebaseDatabase.getInstance().getReference("bookings/${fAuth.currentUser!!.uid}").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (postsnapshot in p0.children)
                {
                    val upload = postsnapshot.getValue(Booking::class.java)
                    if(upload!!.status=="booked") {
                        booking.add(upload)
                    }
                }
                if(booking.size==0){
                    noEvents.visibility = View.VISIBLE
                }else{
                    noEvents.visibility = View.GONE
                }
                adapter.notifyDataSetChanged()
                progress.visibility = View.GONE
            }
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(context as Activity,p0.message, Toast.LENGTH_SHORT).show()
                progress.visibility = View.GONE
            }
        })
    }


}