package pl.edu.agh.mp3yt

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import utils.NavigationListener

class SettingsActivity : AppCompatActivity() {
//    private lateinit var mDrawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val navigationView: NavigationView? = findViewById(R.id.nav_view)
        navigationView!!.setNavigationItemSelectedListener(NavigationListener(this, findViewById(R.id.drawer_layout)))
        navigationView!!.setCheckedItem(R.id.nav_settings)
    }
}
