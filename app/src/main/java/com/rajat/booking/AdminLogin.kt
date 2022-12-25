package com.rajat.booking

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_admin_login.*

class AdminLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        adminToUser.setOnClickListener {
            onBackPressed()
        }

        login.setOnClickListener {
            hideKeyboard()
            if(etUser.text.toString() == "admin" && etPassword.text.toString() == "admin")
            {
                val intent = Intent(
                    this@AdminLogin,
                    AdminDashboard::class.java
                )
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(this@AdminLogin,"Invalid Credentials", Toast.LENGTH_SHORT).show()
            }
        }
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