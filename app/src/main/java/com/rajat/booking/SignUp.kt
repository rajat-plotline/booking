package com.rajat.booking

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class SignUp : AppCompatActivity() {

    private val user : HashMap<String, Any> = HashMap()


    lateinit var fAuth: FirebaseAuth
    lateinit var fStore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        fStore = FirebaseFirestore.getInstance()
        fAuth = FirebaseAuth.getInstance()

        regToLogin.setOnClickListener {
            onBackPressed()
        }

        register.setOnClickListener {
            if(etName.text.toString().isEmpty() || etPassword.text.toString().isEmpty() || etEmail.text.toString().isEmpty()
                && etPhone.text.toString().isEmpty()){
                Toast.makeText(this@SignUp,"Invalid Details",Toast.LENGTH_SHORT).show()
            }
            else if(etPhone.text.toString().isDigitsOnly() || etPhone.text.toString().length!=10){
                Toast.makeText(this@SignUp,"Invalid Phone",Toast.LENGTH_SHORT).show()
            }
            else if(etPassword.text.toString().length<6){
                Toast.makeText(this@SignUp,"Password Should have at least 6 characters",Toast.LENGTH_SHORT).show()
            }
            else if(!validEmail(etEmail.text.toString())) {
                Toast.makeText(this@SignUp, "Invalid Email", Toast.LENGTH_SHORT).show()
            }
            else{

                etEmail.clearFocus()
                etPassword.clearFocus()
                etName.clearFocus()
                etPhone.clearFocus()

                progress.visibility = View.VISIBLE

                    fAuth.createUserWithEmailAndPassword(
                        etEmail.text.toString(),
                        etPassword.text.toString()
                    ).addOnSuccessListener {

                        GlobalScope.launch(Dispatchers.IO) {

                            val uid = fAuth.currentUser!!.uid

                            user["user_id"] = uid
                            user["name"] = etName.text.toString()
                            user["email"] = etEmail.text.toString()
                            user["phone"] = etPhone.text.toString()
                            user["created_on"] = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())

                            val document: DocumentReference = fStore.collection("users").document(uid)
                            document.set(user).await()

                            withContext(Dispatchers.Main){
                                fAuth.signOut()
                                Toast.makeText(this@SignUp, "Account Created", Toast.LENGTH_SHORT).show()
                                finish()

                            }
                        }

                    }.addOnFailureListener {
                        progress.visibility = View.GONE
                        Toast.makeText(
                            this@SignUp,
                            "Account creation failed",
                            Toast.LENGTH_SHORT
                        ).show()
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