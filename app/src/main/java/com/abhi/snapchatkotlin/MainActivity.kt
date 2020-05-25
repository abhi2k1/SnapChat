package com.abhi.snapchatkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    var emailTextView:EditText? = null;
    var passwordTextView:EditText? = null;
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()
        emailTextView = findViewById(R.id.editText);
        passwordTextView = findViewById(R.id.editText2);
    }

    override fun onStart() {
        super.onStart()
        if(mAuth.currentUser!=null){
            login()
        }
    }
    fun goClicked(view: View) {

        mAuth.signInWithEmailAndPassword(
            emailTextView?.text.toString(),
            passwordTextView?.text.toString()
        )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.i("TAG", "signInWithEmail:success")
                    val user = mAuth.currentUser
                    login()
                } else {
                    createEmail()
                }
            }
    }

        fun createEmail(){
            mAuth.createUserWithEmailAndPassword(emailTextView?.text.toString(), passwordTextView?.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.i("TAG", "createUserWithEmail:success")
                        val user = mAuth.currentUser
                        //Adding firebase database
                        if (user != null) {
                            FirebaseDatabase.getInstance().getReference().child("users").child(user.uid).child("email").setValue(emailTextView?.text.toString())
                        }
                        login()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.i("TAG", "createUserWithEmail:failure")
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }

                    // ...
                }
        }
    fun login(){
        //moving to next activity
        val intent = Intent(this,SnapsActivity::class.java)
        finish()
        startActivity(intent)

    }
}
