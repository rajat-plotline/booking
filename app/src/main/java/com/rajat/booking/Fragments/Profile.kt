package com.rajat.booking.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.rajat.booking.Login
import com.rajat.booking.R
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.w3c.dom.Text

class Profile : Fragment() {

    val uid = FirebaseAuth.getInstance().currentUser!!.uid

    lateinit var progress : RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        progress = view.findViewById(R.id.progress)
        progress.visibility = View.VISIBLE

        GlobalScope.launch(Dispatchers.IO) {
            val it = FirebaseFirestore.getInstance().document("users/$uid").get().await()

            withContext(Dispatchers.Main){
                if(it.exists()){
                    view.findViewById<TextView>(R.id.user_name).text = it.getString("name")
                    view.findViewById<TextView>(R.id.user_email).text = it.getString("email")
                    view.findViewById<TextView>(R.id.phone).text = it.getString("phone")

                }else{
                    Toast.makeText(context,"User not exists", Toast.LENGTH_SHORT).show()
                }
                progress.visibility = View.GONE
            }
        }

        view.findViewById<MaterialButton>(R.id.logout).setOnClickListener {

            val dialog = AlertDialog.Builder(context as Activity)
            dialog.setTitle("!! Log Out !!")
            dialog.setMessage("Do you want to logout ?")
            dialog.setPositiveButton("Yes"){ text, which ->
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(context, Login::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                (context as Activity).finishAffinity()
            }
            dialog.setNegativeButton("No"){ text, which ->
            }
            val alert = dialog.create()
            alert.setCanceledOnTouchOutside(false)
            alert.show()

        }
        return view
    }

}