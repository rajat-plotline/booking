package com.rajat.booking

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_admin_add_event.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.progress
import java.util.regex.Pattern

class Login : AppCompatActivity() {

    lateinit var fAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        fAuth = FirebaseAuth.getInstance()

        loginToAdmin.setOnClickListener {
            startActivity(Intent(this@Login,AdminLogin::class.java))
        }

        loginToReg.setOnClickListener {
            startActivity(Intent(this@Login,SignUp::class.java))
        }

        login.setOnClickListener {

            etEmail.clearFocus()
            etPassword.clearFocus()

            if(etEmail.text.toString().isEmpty() || etPassword.text.toString().isEmpty()){
                Toast.makeText(this@Login, "Enter Credentials", Toast.LENGTH_SHORT).show()
            }
            else if(!validEmail(etEmail.text.toString())) {
                Toast.makeText(this@Login, "Invalid Email", Toast.LENGTH_SHORT).show()
            }
            else{

                hideKeyboard()

                progress.visibility = View.VISIBLE

                fAuth.signInWithEmailAndPassword(
                    etEmail.text.toString(),
                    etPassword.text.toString()
                )
                    .addOnCompleteListener{ task ->
                        if (task.isSuccessful) {
                            val firebaseUser = fAuth.currentUser

                            if (firebaseUser != null){
                                startActivity(Intent(this@Login,MainActivity::class.java))
                                finish()
                            }
                            else{
                                Toast.makeText(this@Login, "Login Failed", Toast.LENGTH_SHORT).show()
                                progress.visibility = View.GONE
                            }
                        } else {
                            Toast.makeText(
                                this@Login, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                            progress.visibility = View.GONE
                        }

                    }
                progress.visibility = View.GONE
            }
        }

    }

    private fun validEmail(email: String): Boolean {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
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