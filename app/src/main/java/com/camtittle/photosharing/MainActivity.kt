package com.camtittle.photosharing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.Navigation.findNavController
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        setupNavigation()

        AWSMobileClient.getInstance().initialize(applicationContext, object : Callback<UserStateDetails?> {
            override fun onResult(result: UserStateDetails?) {
                Log.d("AWSINIT", "onResult: " + result?.userState)
            }

            override fun onError(e: Exception?) {
                Log.e("AWSINIT", "onError: ", e)
            }
        })
    }

    private fun setupNavigation() {
        val navController = findNavController(this, R.id.nav_host_fragment)

        // TODO set up action bar with nav controller here

        // TODO handle nav drawer item click

        // setupWithNavController(navigationView, navController)

    }
}
