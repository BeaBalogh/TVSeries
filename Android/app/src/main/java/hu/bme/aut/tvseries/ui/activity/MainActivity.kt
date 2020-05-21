package hu.bme.aut.tvseries.ui.activity

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import hu.bme.aut.tvseries.R

class MainActivity : BaseActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_nav)
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_search,
                R.id.navigation_userinfo
            )
        )
        toolbar.setupWithNavController(navController, appBarConfiguration)
//        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNav.setupWithNavController(navController)
        toolbar.setBackgroundColor(resources.getColor(R.color.colorBackground))

        navController.addOnDestinationChangedListener { _, destination: NavDestination, _ ->
            when (destination.id) {
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_userinfo -> {
                    toolbar.visibility = View.GONE
                }
                R.id.detailsFragment -> {
                    toolbar.visibility = View.VISIBLE
                    toolbar.title = ""
                }
                R.id.newSeriesFragment, R.id.newEpisodeFragment -> {
                    toolbar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setToolbarColor(colorId: Int) {
        supportActionBar!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    colorId
                )
            )
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
