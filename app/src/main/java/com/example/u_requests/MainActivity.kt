package com.example.u_requests

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

open class MainActivity : AppCompatActivity() {
    private var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var authPhone: FirebaseAuth? = null
    private lateinit var authMail: FirebaseAuth
    private var progressDialog: ProgressDialog? = null
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Connection with phone number
        val sendPhone = findViewById<Button>(R.id.send_Btn)
        val phoneNumber = findViewById<EditText>(R.id.phone_Et)
        val code = findViewById<EditText>(R.id.code_Et)
        val finishBtn = findViewById<Button>(R.id.finish_btn)
        progressDialog = ProgressDialog(this)
        progressDialog!!.setCanceledOnTouchOutside(false)
        authPhone = FirebaseAuth.getInstance()
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(ContentValues.TAG, "onVerificationCompleted:$credential")
                progressDialog!!.dismiss()
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(ContentValues.TAG, "onVerificationFailed", e)
                progressDialog!!.dismiss()
                Toast.makeText(this@MainActivity, "${e.message}", Toast.LENGTH_SHORT).show()
                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(this@MainActivity, "${e.message}", Toast.LENGTH_SHORT).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(this@MainActivity, "${e.message}", Toast.LENGTH_SHORT).show()
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(ContentValues.TAG, "onCodeSent:$verificationId")
                progressDialog!!.dismiss()
                Toast.makeText(this@MainActivity, "code: $token", Toast.LENGTH_SHORT).show()

                // Save verification ID and resending token so we can use them later
                //val credential = PhoneAuthProvider.getCredential(storedVerificationId, token.toString())
                //signInWithPhoneAuthCredential(credential)
                storedVerificationId = verificationId
                resendToken = token
            }
        }
        sendPhone.setOnClickListener {
            progressDialog!!.setTitle("Verifying...")
            progressDialog!!.show()
            val phoneNumber = phoneNumber.text.toString()
            val options = PhoneAuthOptions.newBuilder(authPhone!!)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(callbacks!!)          // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
        finishBtn.setOnClickListener {
            val verificationCode = code.text.toString()
            val credential = PhoneAuthProvider.getCredential(storedVerificationId, verificationCode)
            signInWithPhoneAuthCredential(credential)
        }

        //Connection with email/password
        val emailBtn = findViewById<Button>(R.id.email_btn)
        emailBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, GoogleActivity::class.java)
            startActivity(intent)
        }

        //Connection with facebook account
        /*val facebook = findViewById<Button>(R.id.facebook_btn)
        facebook.setOnClickListener {
            intent = Intent(this@MainActivity, FacebookActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
        }*/
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        progressDialog!!.setTitle("Verifying...")
        progressDialog!!.show()
        authPhone!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithCredential:success")
                    progressDialog!!.dismiss()

                    val user = task.result?.user
                    Toast.makeText(this, "Logged in as ${user!!.phoneNumber}", Toast.LENGTH_SHORT).show()
                    Toast.makeText(this, "Logged in as Done!!!", Toast.LENGTH_SHORT).show()
                } else {
                    // Sign in failed, display a message and update the UI
                    progressDialog!!.dismiss()
                    Toast.makeText(this, "${task.exception!!.message}", Toast.LENGTH_SHORT).show()
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }
}