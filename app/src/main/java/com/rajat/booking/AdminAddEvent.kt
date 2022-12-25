package com.rajat.booking

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_admin_add_event.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.sql.Time
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*

class AdminAddEvent : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    var imageUri : Uri? = null

    private val event : HashMap<String, Any> = HashMap()
    private val seats = ArrayList<Boolean>()

    var row = 10
    var col = 15

    private var mDay = 0
    private var mMonth = 0
    private var mYear = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_event)

        addImg.setOnClickListener {
            openFileChooser()
        }

        val mCurrentDate = Calendar.getInstance()
        mYear = mCurrentDate[Calendar.YEAR]
        mMonth = mCurrentDate[Calendar.MONTH]
        mDay = mCurrentDate[Calendar.DAY_OF_MONTH]

        etDate.setOnClickListener {
            val mDatePicker = DatePickerDialog(
                this@AdminAddEvent,
                { datepicker, selectedyear, selectedmonth, selectedday ->
                    val myCalendar = Calendar.getInstance()
                    myCalendar[Calendar.YEAR] = selectedyear
                    myCalendar[Calendar.MONTH] = selectedmonth
                    myCalendar[Calendar.DAY_OF_MONTH] = selectedday

                    val myFormat = "dd-MM-yyyy" //Change as you need
                    val sdf = SimpleDateFormat(myFormat, Locale.ENGLISH)

                    etDate.text = sdf.format(myCalendar.time)

                    mDay = selectedday
                    mMonth = selectedmonth
                    mYear = selectedyear

                }, mYear, mMonth, mDay
            )
            mDatePicker.show()
        }


        etTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMin = calendar.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(this@AdminAddEvent,
                { view, hourOfDay, minute ->
                    val tme = Time(hourOfDay, minute, 0)
                    etTime.text = SimpleDateFormat("hh:mm a",Locale.getDefault()).format(tme).toUpperCase()
                },currentHour,currentMin,false)
            timePickerDialog.show()
        }

        add.setOnClickListener {

            hideKeyboard()

            if(imageUri == null){
                Toast.makeText(this@AdminAddEvent,"Add Poster",Toast.LENGTH_SHORT).show()
            }

            else if(etEventName.text.toString().isEmpty() || etEventType.text.toString().isEmpty()
                || etPrice.text.toString().isEmpty() || etTime.text.toString().isEmpty() || etDate.text.toString().isEmpty()){
                Toast.makeText(this@AdminAddEvent,"Invalid Details",Toast.LENGTH_SHORT).show()
            }
            else{

                etEventName.clearFocus()
                etPrice.clearFocus()
                etEventType.clearFocus()

                progress.visibility = View.VISIBLE

                for(i in 1..row*col){
                    seats.add(false)
                }

                val eventId = UUID.randomUUID().toString()

                event["event_id"] = eventId
                event["event_name"] = etEventName.text.toString()
                event["event_type"] = etEventType.text.toString()
                event["price"] = etPrice.text.toString()
                event["date"] = etDate.text.toString()
                event["time"] = etTime.text.toString()
                event["row"] = row.toString()
                event["col"] = col.toString()
                event["created_on"] = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault()).format(Date()).toUpperCase()
                event["seats"] = seats

                GlobalScope.launch(Dispatchers.IO){

                    val profile = FirebaseStorage.getInstance().getReference("uploads").child(
                        System.currentTimeMillis().toString() + "." + getFileExtension(
                        imageUri!!
                        )
                    )
                    profile.putFile(imageUri!!).await()
                    profile.downloadUrl.addOnSuccessListener {
                        event["poster"] = it.toString()
                        FirebaseDatabase.getInstance().getReference("events/${etDate.text}/$eventId").setValue(event)
                    }.await()

                    withContext(Dispatchers.Main){
                        Toast.makeText(this@AdminAddEvent,"Added Successfully",Toast.LENGTH_SHORT).show()
                        onBackPressed()
                    }
                }
            }
        }

        back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
            && data != null && data.data != null){

            imageUri = data.data!!

            Picasso.get().load(imageUri).into(addImg)

        }
    }

    private fun getFileExtension(uri: Uri) : String?{
        val cr : ContentResolver = this.contentResolver
        val mime : MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(uri))
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}