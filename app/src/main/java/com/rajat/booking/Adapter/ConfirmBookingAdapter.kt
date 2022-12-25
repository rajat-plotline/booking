package com.rajat.booking.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rajat.booking.R
import com.rajat.booking.Database.EventEntity
import com.squareup.picasso.Picasso

class ConfirmBookingAdapter(val context: Context, val events:List<EventEntity>): RecyclerView.Adapter<ConfirmBookingAdapter.ConfirmBookingViewHolder>() {

    class ConfirmBookingViewHolder(view: View): RecyclerView.ViewHolder(view){
        val newImg : ImageView = view.findViewById(R.id.newImg)
        val eventName : TextView = view.findViewById(R.id.eventName)
        val eventType : TextView = view.findViewById(R.id.eventType)
        val time : TextView = view.findViewById(R.id.time)
        val price : TextView = view.findViewById(R.id.price)
        val seat : TextView = view.findViewById(R.id.seat)
        val totalPersonPrice : TextView = view.findViewById(R.id.total_person_price)
        val totalSeats : TextView = view.findViewById(R.id.total_seats)
        val totalPrice : TextView = view.findViewById(R.id.total_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfirmBookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_item_row_confirm, parent, false)
        return ConfirmBookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConfirmBookingViewHolder, position: Int) {
        val event = events[position]

        holder.eventName.text = event.event_name
        holder.eventType.text = event.event_type
        holder.time.text = event.time
        holder.price.text = "₹${event.price}"
        holder.seat.text = event.seat_no.split(" ").joinToString(",")
        holder.totalSeats.text = event.total_seats
        holder.totalPersonPrice.text = "(${event.total_seats} x ${event.price})"
        holder.totalPrice.text = "₹${event.total_price}"

        Picasso.get().load(event.poster).fit().centerCrop().into(holder.newImg)


    }

    override fun getItemCount(): Int {
        return events.size
    }
}