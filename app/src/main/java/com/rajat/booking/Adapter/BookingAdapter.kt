package com.rajat.booking.Adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.rajat.booking.Modal.Booking
import com.rajat.booking.R
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class BookingAdapter (val context: Context, val events:List<Booking>): RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    var onItemClickListner: onItemClickListener? = null

    fun setOnItemClickListener(onItemClickListner: onItemClickListener?) {
        this.onItemClickListner = onItemClickListner
    }

    class BookingViewHolder(view: View): RecyclerView.ViewHolder(view){
        val newImg : ImageView = view.findViewById(R.id.newImg)
        val eventName : TextView = view.findViewById(R.id.eventName)
        val eventType : TextView = view.findViewById(R.id.eventType)
        val date : TextView = view.findViewById(R.id.date)
        val time : TextView = view.findViewById(R.id.time)
        val price : TextView = view.findViewById(R.id.price)
        val seat : TextView = view.findViewById(R.id.seat)
        val totalPersonPrice : TextView = view.findViewById(R.id.total_person_price)
        val totalSeats : TextView = view.findViewById(R.id.total_seats)
        val totalPrice : TextView = view.findViewById(R.id.total_price)
        val bookedOn : TextView = view.findViewById(R.id.booked_on)
        val download : TextView = view.findViewById(R.id.download_ticket)
        val cancel : TextView = view.findViewById(R.id.cancel_ticket)
        val footer : LinearLayout = view.findViewById(R.id.footer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_item_row_bookings, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val event = events[position]

        holder.eventName.text = event.event_name
        holder.eventType.text = event.event_type
        holder.date.text = event.date
        holder.time.text = event.time
        holder.price.text = "₹${event.price}"
        holder.seat.text = event.seat_no.toString().split(" ").joinToString(",")
        holder.totalSeats.text = event.total_seats
        holder.totalPersonPrice.text = "(${event.total_seats} x ${event.price})"
        holder.totalPrice.text = "₹${event.total_price}"
        holder.bookedOn.text = event.created_on

        if(event.status=="cancelled"){
            holder.footer.visibility = View.GONE
        }


        Picasso.get().load(event.poster).fit().centerCrop().into(holder.newImg)

        holder.download.setOnClickListener {

            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.download_ticket_layout, null)

            val newImg : ImageView = view.findViewById(R.id.newImg)
            val eventName : TextView = view.findViewById(R.id.eventName)
            val eventType : TextView = view.findViewById(R.id.eventType)
            val date : TextView = view.findViewById(R.id.date)
            val time : TextView = view.findViewById(R.id.time)
            val price : TextView = view.findViewById(R.id.price)
            val seat : TextView = view.findViewById(R.id.seat)
            val totalPersonPrice : TextView = view.findViewById(R.id.total_person_price)
            val totalSeats : TextView = view.findViewById(R.id.total_seats)
            val totalPrice : TextView = view.findViewById(R.id.total_price)
            val bookedOn : TextView = view.findViewById(R.id.booked_on)

            eventName.text = event.event_name
            eventType.text = event.event_type
            date.text = event.date
            time.text = event.time
            price.text = "₹${event.price}"
            seat.text = event.seat_no.toString().split(" ").joinToString(",")
            totalSeats.text = event.total_seats
            totalPersonPrice.text = "(${event.total_seats} x ${event.price})"
            totalPrice.text = "₹${event.total_price}"
            bookedOn.text = event.created_on

            newImg.setImageBitmap(convertImageViewToBitmap(holder.newImg))


            val displayMetrics = DisplayMetrics()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.display?.getRealMetrics(displayMetrics)
                displayMetrics.densityDpi
            }
            else{
                (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
            }
            view.measure(
                View.MeasureSpec.makeMeasureSpec(
                    displayMetrics.widthPixels, View.MeasureSpec.AT_MOST
                ),
                View.MeasureSpec.makeMeasureSpec(
                    displayMetrics.heightPixels, View.MeasureSpec.AT_MOST
                )
            )

            view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
            val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)

            Bitmap.createScaledBitmap(bitmap, view.measuredWidth, view.measuredHeight, true)
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(view.measuredWidth, view.measuredHeight, 1).create()

            val page = pdfDocument.startPage(pageInfo)
            page.canvas.drawBitmap(bitmap, 0F, 0F, null)
            pdfDocument.finishPage(page)

            val pdfName  = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val dir = File("$path")
            dir.mkdirs()
            val filePath = File(dir, "booking_$pdfName.pdf")
            val out : OutputStream

            try {
                out = FileOutputStream(filePath)
                pdfDocument.writeTo(out)
                pdfDocument.close()
                out.flush()
                out.close()

                Toast.makeText(context,"Ticket saved to Downloads",Toast.LENGTH_LONG).show()

            }
            catch (e : Exception){
                Toast.makeText(context,"Failed to save",Toast.LENGTH_LONG).show()
            }


        }

        holder.cancel.setOnClickListener {
            val dialog = AlertDialog.Builder(context as Activity)
            dialog.setTitle("!! Cancel !!")
            dialog.setMessage("Do you want to cancel your ticket booking ?")
            dialog.setPositiveButton("Yes"){ text, which ->

                GlobalScope.launch(Dispatchers.IO){
                    val fAuth = FirebaseAuth.getInstance()
                    val booking : HashMap<String, Any> = HashMap()
                    booking["status"]="cancelled"

                    val seatId = event.seat_id.toString().split(" ")
                    val hashMap = HashMap<String,Boolean>()
                    for(seat in seatId){
                        hashMap[seat] = false
                    }

                    FirebaseDatabase.getInstance().getReference("bookings/${fAuth.currentUser!!.uid}/${event.booking_id}")
                        .updateChildren(booking).await()

                    FirebaseDatabase.getInstance().getReference("events/${event.date}/${event.event_id}/seats")
                        .updateChildren(hashMap as Map<String, Any>).await()

                    withContext(Dispatchers.Main){
                        onItemClickListner?.onClick()
                    }
                }

            }
            dialog.setNegativeButton("No"){ text, which ->
            }
            val alert = dialog.create()
            alert.setCanceledOnTouchOutside(false)
            alert.show()
        }

    }

    override fun getItemCount(): Int {
        return events.size
    }

    interface onItemClickListener {
        fun onClick()
    }

    private fun convertImageViewToBitmap(v: ImageView): Bitmap? {
        return (v.drawable as BitmapDrawable).bitmap
    }

}