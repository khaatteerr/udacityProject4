package com.udacity.project4.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity


class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding
    private val loginVM by viewModels<LoginVM>()

    companion object {
        const val TAG = "LoginFragment"
        const val LOGIN_RESULT_CODE = 1001
    }//end companion object

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.authButton.setOnClickListener { launchLogin() }

        loginVM.authenticationState.observe(this) { authenticationState ->
            when (authenticationState) {
                LoginVM.AuthenticationState.AUTHENTICATED -> switchActivities()
                else -> {}
            }//end when()
        }//end observer()
    }//end onCreate()

    private fun launchLogin() {
        //it's an option for the user to sign in/up if they choose sign in with their email will need to create password as well
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // Create and launch login intent. and we listen to the user's response of this activity with the LOGIN_RESULT_CODE code.
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                providers
            ).build(), LOGIN_RESULT_CODE
        )
    }//end launchRegister()

    //to go to another activity with intent when the authenticationState is AUTHENTICATED
    private fun switchActivities() {
        val switchActivityIntent = Intent(this, RemindersActivity::class.java)
        startActivity(switchActivityIntent)
    }//end switchActivities()
}//end class