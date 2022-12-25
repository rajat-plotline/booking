package com.rajat.booking.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rajat.booking.R

class SeatAdapter(val context:Context,val seats:ArrayList<Boolean>):RecyclerView.Adapter<SeatAdapter.SeatViewHolder>() {

    var onItemClickListner: onItemClickListener? = null
    var seatId = ArrayList<Int>()
    var seatNo = ArrayList<String>()

    fun setOnItemClickListener(onItemClickListner: onItemClickListener?) {
        this.onItemClickListner = onItemClickListner
    }

    class SeatViewHolder(view: View):RecyclerView.ViewHolder(view){
        val row : TextView = view.findViewById(R.id.row)
        val col : TextView = view.findViewById(R.id.col)
        val seatLayout : RelativeLayout = view.findViewById(R.id.seat_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_seat, parent, false)
        return SeatViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeatViewHolder, position: Int) {
        val seat = seats[position]

        if(seat){
            holder.seatLayout.setBackgroundResource(R.drawable.seat_occupied)
        }

        val div = (position)/15
        val mod = (position+1)%15

        var selected = false

        val char = 'A'+div
        holder.row.text = char.toString()
        holder.col.text = if(mod!=0) mod.toString() else "15"

        val tempSeat = holder.row.text.toString() + holder.col.text.toString()

        holder.seatLayout.setOnClickListener {
            if(!seat){
                selected = !selected
                if (selected) {
                    holder.seatLayout.setBackgroundResource(R.drawable.seat_selected)
                    holder.row.setTextColor(Color.parseColor("#ffffff"))
                    holder.col.setTextColor(Color.parseColor("#ffffff"))
                    seatId.add(position)
                    seatNo.add(tempSeat)
                    onItemClickListner?.onClick(seatId,seatNo)
                } else {
                    holder.seatLayout.setBackgroundResource(R.drawable.seat_not_selected)
                    holder.row.setTextColor(Color.parseColor("#000000"))
                    holder.col.setTextColor(Color.parseColor("#000000"))
                    seatId.remove(position)
                    seatNo.remove(tempSeat)
                    onItemClickListner?.onClick(seatId,seatNo)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return seats.size
    }

    interface onItemClickListener {
        fun onClick(seatId: ArrayList<Int>,seatNo:ArrayList<String>) //pass your object types.
    }
}