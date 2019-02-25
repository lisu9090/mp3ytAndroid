package utils

import android.support.design.widget.NavigationView
import android.support.v4.app.FragmentManager
import android.view.MenuItem
import pl.edu.agh.mp3yt.R
import pl.edu.agh.mp3yt.SearchFragment
import android.support.v4.app.Fragment
import pl.edu.agh.mp3yt.DownloadFragment
import pl.edu.agh.mp3yt.SettingsFragment

class FragmentNavigatorListener constructor(fragmentManager: FragmentManager): NavigationView.OnNavigationItemSelectedListener {
    val mFragmentManager = fragmentManager
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.isChecked = true
        when(item.itemId){
            R.id.nav_search -> showFragemnt(SearchFragment())
            R.id.nav_download -> showFragemnt(DownloadFragment())
            R.id.nav_settings -> showFragemnt(SettingsFragment())
            else -> null
        }
        return true
    }

    fun showFragemnt(fragment: Fragment): Unit{
        val fragmentManager = mFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.content_frame, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}