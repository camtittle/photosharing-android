package com.camtittle.photosharing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.GravityCompat
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.*
import com.camtittle.photosharing.engine.auth.AuthManager
import kotlinx.android.synthetic.main.main_activity.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AuthManager.init(applicationContext)
        AuthManager.setSignOutListener {
            runOnUiThread { onSignOut() }
        }

        setContentView(R.layout.main_activity)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        setupNavigation()

    }

    override fun onSupportNavigateUp(): Boolean {
        return navigateUp(findNavController(this, R.id.nav_host_fragment), drawerLayout)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun setupNavigation() {
        val navController = findNavController(this, R.id.nav_host_fragment)

        val fullScreenFragments = arrayOf(R.id.signUpFragment, R.id.signInFragment)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (fullScreenFragments.contains(destination.id)) {
                toolbar.visibility = View.GONE
            } else {
                toolbar.visibility = View.VISIBLE
            }
        }

        // Update action bar to reflect navigation
        val appBarConfiguration = AppBarConfiguration
            .Builder(
                R.id.feedFragment,
                R.id.signUpFragment,
                R.id.signInFragment)
            .setDrawerLayout(drawerLayout)
            .build()
        setupActionBarWithNavController(this, navController, appBarConfiguration)

        val logoutItem = navigationView.menu.findItem(R.id.sign_out)
        logoutItem.setOnMenuItemClickListener {
            AuthManager.signOut()
            drawerLayout.closeDrawers()
            true
        }

        // Handle nav drawer item clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }

        // Tie nav graph to items in nav drawer
        setupWithNavController(navigationView, navController)
    }

    private fun onSignOut() {
        findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_global_auth_navigation)
    }
}
