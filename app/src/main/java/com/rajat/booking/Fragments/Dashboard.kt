package com.rajat.booking.Fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rajat.booking.Adapter.EventAdapter
import com.rajat.booking.Modal.Events
import com.rajat.booking.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Dashboard : Fragment() {

    var events = ArrayList<Events>()
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var adapter: EventAdapter

    lateinit var progress : RelativeLayout

    val today = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    private var mDay = 0
    private var mMonth = 0
    private var mYear = 0

    lateinit var txtDate : TextView
    lateinit var txtEvent : TextView
    lateinit var noEvents : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        txtDate = view.findViewById(R.id.txtDate)
        txtEvent = view.findViewById(R.id.txtEvent)
        noEvents = view.findViewById(R.id.no_events)
        progress = view.findViewById(R.id.progress)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        layoutManager = GridLayoutManager(context as Activity,2)

        txtDate.text = today.toString()

        val mCurrentDate = Calendar.getInstance()
        mYear = mCurrentDate[Calendar.YEAR]
        mMonth = mCurrentDate[Calendar.MONTH]
        mDay = mCurrentDate[Calendar.DAY_OF_MONTH]

        adapter = EventAdapter(context as Activity,events)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        updateDashboard(today, "All Events")

        txtDate.setOnClickListener {
            val mDatePicker = DatePickerDialog(
                context as Activity,
                { datepicker, selectedyear, selectedmonth, selectedday ->
                    val myCalendar = Calendar.getInstance()
                    myCalendar[Calendar.YEAR] = selectedyear
                    myCalendar[Calendar.MONTH] = selectedmonth
                    myCalendar[Calendar.DAY_OF_MONTH] = selectedday

                    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)

                    val myFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(myCalendar.time)
                    val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                    if(myFormat>=todayDate) {
                        txtDate.text = sdf.format(myCalendar.time)
                        updateDashboard(txtDate.text.toString(), "All Events")
                    }else{
                        Toast.makeText(context as Activity,"Past Shows not Available", Toast.LENGTH_SHORT).show()
                    }

                    mDay = selectedday
                    mMonth = selectedmonth
                    mYear = selectedyear

                }, mYear, mMonth, mDay
            )
            mDatePicker.show()
        }

        txtEvent.setOnClickListener {

            val customDialog = Dialog(context as Activity)
            customDialog.setContentView(R.layout.dialog_dashboard_event_type)
            val allEvent: TextView = customDialog.findViewById(R.id.all)
            val play: TextView = customDialog.findViewById(R.id.play)
            val comedy: TextView = customDialog.findViewById(R.id.comedy)
            val movie: TextView = customDialog.findViewById(R.id.movie)
            val back: ImageView = customDialog.findViewById(R.id.back)

            allEvent.setOnClickListener {
                updateDashboard(txtDate.text.toString(),"All Events")
                customDialog.dismiss()
            }

            play.setOnClickListener {
                updateDashboard(txtDate.text.toString(),"Play")
                customDialog.dismiss()
            }

            comedy.setOnClickListener {
                updateDashboard(txtDate.text.toString(),"Comedy Show")
                customDialog.dismiss()
            }

            movie.setOnClickListener {
                updateDashboard(txtDate.text.toString(),"Movie")
                customDialog.dismiss()
            }

            back.setOnClickListener {
                customDialog.onBackPressed()
            }

            customDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            customDialog.setCanceledOnTouchOutside(false)
            customDialog.show()

        }

        return view
    }

    private fun updateDashboard(date : String, type:String){

        events.clear()
        progress.visibility = View.VISIBLE
        FirebaseDatabase.getInstance().getReference("events/${date}").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (postsnapshot in p0.children)
                {
                    val upload = postsnapshot.getValue(Events::class.java)
                    if(type=="All Events") {
                        events.add(upload!!)
                    }
                    else{
                        if(type==upload!!.event_type){
                            events.add(upload)
                        }
                    }
                }

                if(events.size==0){
                    noEvents.visibility = View.VISIBLE
                }else{
                    noEvents.visibility = View.GONE
                }

                txtEvent.text = type
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