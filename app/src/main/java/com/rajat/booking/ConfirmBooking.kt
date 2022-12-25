package com.rajat.booking

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rajat.booking.Adapter.AddMoreAdapter
import com.rajat.booking.Adapter.ConfirmBookingAdapter
import com.rajat.booking.Modal.Events
import com.rajat.booking.Database.EventDatabase
import com.rajat.booking.Database.EventEntity
import kotlinx.android.synthetic.main.activity_confirm_booking.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ConfirmBooking : AppCompatActivity() {

    var events= listOf<EventEntity>()
    private val booking : HashMap<String, Any> = HashMap()

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var adapter: ConfirmBookingAdapter

    var eventIdArr = ArrayList<String>()
    var total = 0

    lateinit var fAuth : FirebaseAuth

    var eventDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_booking)

        layoutManager = LinearLayoutManager(this)

        fAuth = FirebaseAuth.getInstance()

        add_more.setOnClickListener {
            progress.visibility=View.VISIBLE
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_events, null)

            val sheetEvents = ArrayList<Events>()
            val close : View = view.findViewById(R.id.close)
            val addMoreRecycler : RecyclerView = view.findViewById(R.id.add_more_recycler)
            val noEvents : TextView = view.findViewById(R.id.no_events)
            val sheetLayoutManager: RecyclerView.LayoutManager
            sheetLayoutManager = LinearLayoutManager(this)
            var sheetAdapter: AddMoreAdapter

            FirebaseDatabase.getInstance().getReference("events/$eventDate").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    for (postsnapshot in p0.children)
                    {
                        val upload = postsnapshot.getValue(Events::class.java)
                        if(!eventIdArr.contains(upload!!.event_id)) {
                            sheetEvents.add(upload)
                        }
                    }

                    sheetAdapter = AddMoreAdapter(this@ConfirmBooking,sheetEvents)
                    addMoreRecycler.adapter = sheetAdapter
                    addMoreRecycler.layoutManager = sheetLayoutManager

                    if (sheetEvents.size==0){
                        noEvents.visibility=View.VISIBLE
                    }else{
                        noEvents.visibility=View.GONE
                    }

                    progress.visibility=View.GONE

                    sheetAdapter.setOnItemClickListener(object : AddMoreAdapter.onItemClickListener {
                        override fun onClick() {
                            dialog.dismiss()
                        }
                    })

                }
                override fun onCancelled(p0: DatabaseError) {
                    Toast.makeText(this@ConfirmBooking,p0.message, Toast.LENGTH_SHORT).show()
                    progress.visibility=View.GONE
                    dialog.dismiss()
                }

            })
            close.setOnClickListener {
                dialog.dismiss()
            }
            dialog.setCancelable(false)
            dialog.setContentView(view)
            dialog.show()
        }

        confirm.setOnClickListener {

            progress.visibility = View.VISIBLE

            GlobalScope.launch(Dispatchers.IO){

                for(event in events) {
                    val seatId = event.seat_id.split(" ")
                    val hashMap = HashMap<String,Boolean>()
                    for(seat in seatId){
                        hashMap[seat] = true
                    }

                    val bookingId = UUID.randomUUID().toString()
                    booking["event_id"] = event.event_id
                    booking["event_name"] = event.event_name
                    booking["event_type"] = event.event_type
                    booking["price"] = event.price
                    booking["date"] = event.date
                    booking["time"] = event.time
                    booking["booking_id"] = bookingId
                    booking["total_seats"] = event.total_seats
                    booking["total_price"] = event.total_price
                    booking["created_on"] = SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.getDefault()).format(Date()).toUpperCase()
                    booking["poster"] = event.poster
                    booking["seat_id"] = event.seat_id
                    booking["seat_no"] = event.seat_no
                    booking["status"] = "booked"

                    FirebaseDatabase.getInstance().getReference("events/$eventDate/${event.event_id}/seats")
                        .updateChildren(hashMap as Map<String, Any>).await()

                    FirebaseDatabase.getInstance().getReference("bookings/${fAuth.currentUser!!.uid}/${bookingId}")
                        .setValue(booking).await()
                }

                withContext(Dispatchers.Main){
                    DBAsyncTask(this@ConfirmBooking).execute().get()
                    progress.visibility = View.GONE
                    startActivity(Intent(this@ConfirmBooking,BookingDone::class.java))
                    finish()
                }
            }

        }

    }

    override fun onStart() {
        super.onStart()

        total=0
        eventIdArr.clear()
        events = RetrieveEvents(this@ConfirmBooking).execute().get()

        for(i in events){
            eventIdArr.add(i.event_id)
            total+=i.total_price.toInt()
            eventDate=i.date
        }
        adapter = ConfirmBookingAdapter(this@ConfirmBooking,events)
        booking_recycler.adapter = adapter
        booking_recycler.layoutManager = layoutManager
        total_price.text = "₹${total}"
    }

    class RetrieveEvents(val context: Context) : AsyncTask<Void, Void, List<EventEntity>>() {

        override fun doInBackground(vararg p0: Void?): List<EventEntity> {
            val db = Room.databaseBuilder(context, EventDatabase::class.java, "event-db").fallbackToDestructiveMigration().build()

            return db.eventDao().getAllEvent()
        }
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

    override fun onBackPressed() {
        val dialog = AlertDialog.Builder(this@ConfirmBooking)
        dialog.setTitle("!! Cancel !!")
        dialog.setMessage("Do you want to Cancel your bookings ?")
        dialog.setPositiveButton("Yes"){ text, which ->
            DBAsyncTask(this@ConfirmBooking).execute().get()
            super.onBackPressed()
        }
        dialog.setNegativeButton("No"){ text, which ->
        }
        val alert = dialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }
}