package com.example.nike.googleAuthentication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import com.example.nike.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class GoogleSignInManager(context: Context) {

    private val appContext = context.applicationContext

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference =
        FirebaseDatabase.getInstance().reference

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(appContext.getString(R.string.web_client_id))
            .requestEmail()
            .build()

        GoogleSignIn.getClient(appContext, gso)
    }

    fun signIn(
        launcher: ActivityResultLauncher<Intent>
    ) {
        launcher.launch(googleSignInClient.signInIntent)
    }

    fun handleSignInResult(
        result: ActivityResult,
        onSuccess: (FirebaseAuth) -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d("SIGN_IN", "ResultCode: ${result.resultCode}")
        if (result.resultCode != Activity.RESULT_OK) {
            onError("Sign-in cancelled")
            return
        }

        try {
            val account =
                GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    .getResult(ApiException::class.java)

            val idToken = account.idToken
                ?: return onError("ID Token missing")

            firebaseAuthWithGoogle(idToken, onSuccess, onError)

        } catch (e: ApiException) {
            onError("Google Sign-In failed: ${e.statusCode}")
        }
    }

    private fun firebaseAuthWithGoogle(
        idToken: String,
        onSuccess: (FirebaseAuth) -> Unit,
        onError: (String) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                createUserIfNeeded()
                onSuccess(auth)
            }
            .addOnFailureListener {
                onError("Authentication failed")
            }
    }

    private fun createUserIfNeeded() {
        val user = auth.currentUser ?: return
        val userID = user.uid

        val userRef = database.child("Users").child(userID)
        userRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                val data = mapOf(
                    "name" to user.displayName,
                    "mail" to user.email,
                    "photoUrl" to user.photoUrl?.toString()?.replace("s96-c", "s400-c")
                )
                userRef.setValue(data)
            }
        }
    }

    fun signOut(onDone: () -> Unit) {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            onDone()
        }
    }
}