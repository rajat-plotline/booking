package com.rajat.booking.Adapter

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.rajat.booking.Modal.Events
import com.rajat.booking.R
import com.rajat.booking.SeatBooking
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_admin_add_event.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EventAdapter(val context: Context, val events:ArrayList<Events>): RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(view: View): RecyclerView.ViewHolder(view){
        val newImg : ImageView = view.findViewById(R.id.newImg)
        val eventName : TextView = view.findViewById(R.id.eventName)
        val eventType : TextView = view.findViewById(R.id.eventType)
        val time : TextView = view.findViewById(R.id.time)
        val price : TextView = view.findViewById(R.id.price)
        val card : RelativeLayout = view.findViewById(R.id.relative)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_item_row_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]

        holder.eventName.text = event.event_name
        holder.eventType.text = event.event_type
        holder.time.text = event.time
        holder.price.text = "₹${event.price}/person"

        Picasso.get().load(event.poster).fit().centerCrop().into(holder.newImg)

        holder.card.setOnClickListener{
            if(context.toString().contains("MainActivity")){
                val intent = Intent(context,SeatBooking::class.java)
                intent.putExtra("event_id",event.event_id)
                intent.putExtra("fromContext","MainActivity")
                intent.putExtra("event_date",event.date)
                (context as Activity).startActivity(intent)
            }
        }


    }

    override fun getItemCount(): Int {
        return events.size
    }
}