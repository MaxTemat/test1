package com.example.u_requests

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class GoogleActivity : AppCompatActivity() {
    private var progressDialog: ProgressDialog? = null
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google)

        progressDialog = ProgressDialog(this)
        val login = findViewById<Button>(R.id.login_btn)
        auth = FirebaseAuth.getInstance()

        login.setOnClickListener {
            register()
        }
        createRequest()
    }

    private fun createRequest() {
        var gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    private fun register() {
        val emailTv = findViewById<EditText>(R.id.email_Et)
        val passwordTv = findViewById<EditText>(R.id.password_Et)
        val email = emailTv.text.toString()
        val password = passwordTv.text.toString()
        if(email.isNullOrEmpty() || password.isNullOrEmpty()){
            emailTv.error = "This field must not be empty"
            passwordTv.error = "This field must not be empty"
            return
        }
        progressDialog!!.setMessage("Please wait...")
        progressDialog!!.show()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){task ->
                if(task.isSuccessful)
                {
                    progressDialog!!.dismiss()
                    Toast.makeText(this, "Done !!!", Toast.LENGTH_LONG).show()
                    AlertDialog.Builder(this)
                        .setTitle("Done")
                        .setMessage("Done !!!") // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(
                            android.R.string.yes
                        ) { _, _ ->
                            // Continue with delete operation
                        } // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                }
                else
                {
                    progressDialog!!.dismiss()
                    Toast.makeText(this, "${task.exception}", Toast.LENGTH_LONG).show()
                }

            }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            //reload();
        }
    }
    private fun createAccount(email: String, password: String) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
        // [END create_user_with_email]
    }
    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
        // [END sign_in_with_email]
    }
    private fun sendEmailVerification() {
        // [START send_email_verification]
        val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                // Email Verification sent
                Toast.makeText(this, "Email verification has sent", Toast.LENGTH_SHORT)
                    .show()
            }
        // [END send_email_verification]
    }

    private fun updateUI(user: FirebaseUser?) {
        AlertDialog.Builder(this)
            .setTitle("Done")
            .setMessage("Welcome ${user!!.displayName}") // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(android.R.string.yes,
                DialogInterface.OnClickListener { dialog, which ->
                    // Continue with delete operation
                }) // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

}