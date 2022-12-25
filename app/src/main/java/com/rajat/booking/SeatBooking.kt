package com.rajat.booking

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rajat.booking.Adapter.SeatAdapter
import com.rajat.booking.Modal.Events
import com.rajat.booking.Database.EventDatabase
import com.rajat.booking.Database.EventEntity
import kotlinx.android.synthetic.main.activity_seat_booking.*

class SeatBooking : AppCompatActivity() {

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var adapter: SeatAdapter
    lateinit var event : Events
    var eventId : String? = ""

    var fromContext : String? = ""

    var totalSeats = ArrayList<Boolean>()

    var seatIdFinal = ArrayList<Int>()
    var seatNoFinal = ArrayList<String>()

    var eventDate :String?= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_booking)

        if(intent!=null){
            eventId=intent.getStringExtra("event_id")
            fromContext=intent.getStringExtra("fromContext")
            eventDate=intent.getStringExtra("event_date")
        }

        progress.visibility = View.VISIBLE
        book_layout.visibility = View.GONE

        layoutManager = GridLayoutManager(this,15)

        FirebaseDatabase.getInstance().getReference("events/${eventDate}/$eventId").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                event = p0.getValue(Events::class.java) as Events
                totalSeats = event.seats

                eventName.text = event.event_name
                date.text = event.date
                time.text = event.time
                price.text = "₹ ${event.price}/person"

                adapter = SeatAdapter(this@SeatBooking, totalSeats)
                seat_recycler.adapter = adapter
                seat_recycler.layoutManager = layoutManager

                progress.visibility = View.GONE

                adapter.setOnItemClickListener(object : SeatAdapter.onItemClickListener {
                    override fun onClick(seatId: ArrayList<Int>, seatNo: ArrayList<String>) {
                        seatIdFinal = seatId
                        seatNoFinal = seatNo
                        if(seatId.size==0){
                            seat.text = seatNo.toString()
                            book_layout.visibility = View.GONE
                        }
                        else{
                            seat.text = seatNo.toString().removePrefix("[").removeSuffix("]")
                            book_layout.visibility = View.VISIBLE
                        }
                    }
                })
            }
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@SeatBooking,p0.message, Toast.LENGTH_SHORT).show()
                progress.visibility = View.GONE
            }

        })

        proceed.setOnClickListener {

            val tempSeatId = seatIdFinal.toString().removePrefix("[").removeSuffix("]").split(", ").joinToString(" ")
            val tempSeatNo = seatNoFinal.toString().removePrefix("[").removeSuffix("]").split(", ").joinToString(" ")

            val totalSeatsBooked = seatIdFinal.size
            val totalPrice = totalSeatsBooked * event.price.toString().toInt()

            val details = EventEntity(
                event.event_id!!,
                event.event_name!!,
                event.event_type!!,
                event.price!!,
                event.date!!,
                event.time!!,
                tempSeatId,
                tempSeatNo,
                event.poster!!,
                totalPrice.toString(),
                totalSeatsBooked.toString()
            )
            if(DBAsyncTask(this@SeatBooking,details).execute().get()){
                if(fromContext=="MainActivity"){
                    startActivity(Intent(this@SeatBooking,ConfirmBooking::class.java))
                    finish()
                }
                else{
                    onBackPressed()
                }
            }
        }
    }

    class DBAsyncTask(val context: Context, val eventEntity: EventEntity) :
        AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, EventDatabase::class.java, "event-db").fallbackToDestructiveMigration().build()
        override fun doInBackground(vararg p0: Void?): Boolean {
            db.eventDao().insertEvent(eventEntity)
            db.close()
            return true
        }
    }
}