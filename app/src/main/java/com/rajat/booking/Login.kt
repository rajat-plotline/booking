package com.rajat.booking

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Pattern


class Login : AppCompatActivity() {

    lateinit var fAuth : FirebaseAuth

    private val PERMISSION_REQUEST_CODE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        fAuth = FirebaseAuth.getInstance()

        if (checkPermission()) {
        } else {
            requestPermission();
        }

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

    private fun checkPermission(): Boolean {
        val permission1 =
            ContextCompat.checkSelfPermission(applicationContext, WRITE_EXTERNAL_STORAGE)
        val permission2 =
            ContextCompat.checkSelfPermission(applicationContext, READ_EXTERNAL_STORAGE)
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty()) {
                val writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
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