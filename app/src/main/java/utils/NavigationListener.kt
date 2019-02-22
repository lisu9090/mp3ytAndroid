package utils

import android.content.Context
import android.content.Intent
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.view.MenuItem
import pl.edu.agh.mp3yt.*
import pl.edu.agh.mp3yt.R

class NavigationListener constructor(context: Context, draverLayout: DrawerLayout): NavigationView.OnNavigationItemSelectedListener {
    private val mContext = context
    private val mDraverLayout = draverLayout

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.isChecked = true
        val intent: Intent? = when(item.itemId){
            R.id.nav_search -> Intent(mContext, MainActivity::class.java)
            R.id.nav_download -> Intent(this.mContext, DownloadActivity::class.java)
            R.id.nav_settings -> Intent(this.mContext, SettingsActivity::class.java)
            else -> null
        }
        mDraverLayout.closeDrawers()
        mContext.startActivity(intent)
        return true
    }
}