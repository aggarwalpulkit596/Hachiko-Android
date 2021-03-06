package com.hachiko.hachiko

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.hachiko.hachiko.login.LoginActivity
import com.hachiko.hachiko.sharedPreferences.PrefManager
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class MainActivity : BaseActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var prefManager:PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        otpLoginBtn.setOnClickListener {
            startActivity<LoginActivity>()
        }

        mAuth = FirebaseAuth.getInstance()

        prefManager= PrefManager(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        googleLoginBtn.setOnClickListener {
            googleSignIn()
        }

        guestLoginBtn.setOnClickListener{
            signInAnonymously()
        }

        linkAcct.setOnClickListener{
            linkAccount()
        }

    }

    private fun signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("GUESTLOGIN", "signInAnonymously:success")
                        toast("signInAnonymously:success")
                       // linkAccount()
                    } else {
                        Log.w("GUESTLOGIN", "signInAnonymously:failure", task.exception)
                        toast("Anonymous Authentication failed")
                    }
                }
    }

    private fun linkAccount() {

        val googleIdToken=""
        val credential = GoogleAuthProvider.getCredential(googleIdToken, null)

        Log.d("GUESTLOGIN","credential: "+credential+"\nLINKING ACCOUNT with google now")

        mAuth.currentUser?.linkWithCredential(credential)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        toast("linkWithCredential:success")
                    } else {
                        Log.w("GUESTLOGIN", "linkWithCredential:failure", task.exception)
                        toast("linkWithCredential:failure")
                    }
                }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Log.w("GOOGLE LOGIN", "Google sign in failed", e)
            }

        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("GOOGLE LOGIN", "firebaseAuthWithGoogle:" + acct.id!!)
        //    showProgressDialog()

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("GOOGLE LOGIN", "signInWithCredential:success")
                        toast("GOOGLE LOGIN SUCCESS")
                        prefManager.saveString(PrefManager.SIGN_IN_METHOD,getString(R.string.google_sign_in))
                        val user = mAuth.currentUser
//                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("GOOGLE LOGIN", "signInWithCredential:failure", task.exception)
                        showToast("GOOGLE LOGIN FAILURE")
//                        Snackbar.make(main_layout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
//                        updateUI(null)
                    }

//                    hideProgressDialog()
                }
    }

    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    companion object {
        private val RC_SIGN_IN = 9001
        private val LINK_ACCT=1
    }

    private fun showToast(message: String) {
        toast(message)
    }

}
