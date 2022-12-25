package com.rajat.booking.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.rajat.booking.Modal.Events
import com.rajat.booking.R
import com.rajat.booking.SeatBooking
import com.squareup.picasso.Picasso

class AddMoreAdapter(val context: Context, val events:ArrayList<Events>): RecyclerView.Adapter<AddMoreAdapter.AddMoreViewHolder>() {

    var onItemClickListner: onItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: onItemClickListener) {
        this.onItemClickListner = onItemClickListener
    }

    class AddMoreViewHolder(view: View): RecyclerView.ViewHolder(view){
        val newImg : ImageView = view.findViewById(R.id.newImg)
        val eventName : TextView = view.findViewById(R.id.eventName)
        val eventType : TextView = view.findViewById(R.id.eventType)
        val time : TextView = view.findViewById(R.id.time)
        val price : TextView = view.findViewById(R.id.price)
        val add : MaterialButton = view.findViewById(R.id.add)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddMoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_item_add_more_event, parent, false)
        return AddMoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddMoreViewHolder, position: Int) {
        val event = events[position]

        holder.eventName.text = event.event_name
        holder.eventType.text = event.event_type
        holder.time.text = event.time
        holder.price.text = event.price

        Picasso.get().load(event.poster).fit().centerCrop().into(holder.newImg)

        holder.add.setOnClickListener{
            val intent = Intent(context, SeatBooking::class.java)
            intent.putExtra("event_id",event.event_id)
            intent.putExtra("fromContext","AddMore")
            intent.putExtra("event_date",event.date)
            (context as Activity).startActivity(intent)
            onItemClickListner?.onClick()
        }


    }

    override fun getItemCount(): Int {
        return events.size
    }

    interface onItemClickListener {
        fun onClick()
    }
}