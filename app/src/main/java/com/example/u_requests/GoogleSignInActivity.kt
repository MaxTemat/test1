package com.example.u_requests

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class GoogleSignInActivity : MainActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    // [END declare_auth]

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // [START config_signin]
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // [END initialize_auth]
        user = auth.currentUser!!

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        // [END config_signin]

        val signinIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signinIntent, RC_SIGN_IN)



    }

    // [START on_start_check_user]
    /*override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }*/
    // [END on_start_check_user]

    // [START onactivityresult]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = auth.currentUser
                    Toast.makeText(this, "${user!!.displayName} is Done !!!", Toast.LENGTH_LONG).show()
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "${task.exception} is Done !!!", Toast.LENGTH_LONG).show()
                    //updateUI(null)
                }
            }
    }
    // [END auth_with_google]

    private fun updateUI(user: FirebaseUser?) {
        Toast.makeText(this, "Done !!!", Toast.LENGTH_LONG).show()
        AlertDialog.Builder(this)
            .setTitle("Done")
            .setMessage("${user!!.displayName}") // Specifying a listener allows you to take an action before dismissing the dialog.
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

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}