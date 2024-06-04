package com.udacity.project4.authentication

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthenticationActivity.Companion.SIGN_IN_RESULT_CODE
import com.udacity.project4.locationreminders.RemindersActivity
import kotlinx.android.synthetic.main.activity_authentication.*

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    companion object {
        const val SIGN_IN_RESULT_CODE = 1001
    }
    private val lunchIntent=registerForActivityResult(
        FirebaseAuthUIActivityResultContract()){result ->
        this.onSingResult(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        login_button.setOnClickListener {
            startLogin() }
    }

    private fun startLogin() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build(),AuthUI.IdpConfig.GitHubBuilder().build()
        )


            val singIntent=AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.AppTheme)
                .setLogo(R.drawable.map)
                .build()
            lunchIntent.launch(singIntent)

    }
    private fun onSingResult(res:FirebaseAuthUIAuthenticationResult){
        val response=res.idpResponse
        if(res.resultCode== RESULT_OK){
            val user=FirebaseAuth.getInstance().currentUser!!.displayName
            Toast.makeText(this,"success${user}",Toast.LENGTH_LONG).show()
            val intent = Intent(this, RemindersActivity::class.java)
            startActivity(intent)
        }else{
            Toast.makeText(this,"faild",Toast.LENGTH_LONG).show()

        }

    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in user.
                Log.i(
                    TAG,
                    "Successfully signed in user " +
                        "${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )

                val intent = Intent(this, RemindersActivity::class.java)
                startActivity(intent)
            } else {
                // Sign in failed. If response is null the user canceled the sign-in flow using
                // the back button. Otherwise check response.getError().getErrorCode() and handle
                // the error.
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }*/

}
