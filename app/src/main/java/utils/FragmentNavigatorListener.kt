package utils

import android.support.design.widget.NavigationView
import android.support.v4.app.FragmentManager
import android.view.MenuItem
import pl.edu.agh.mp3yt.R
import pl.edu.agh.mp3yt.SearchFragment
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import pl.edu.agh.mp3yt.DownloadFragment
import pl.edu.agh.mp3yt.SettingsFragment

class FragmentNavigatorListener constructor(private val fragmentManager: FragmentManager, private val draverLayout: DrawerLayout): NavigationView.OnNavigationItemSelectedListener {
    private var mFragmentsList: List<Fragment> = listOf(SearchFragment(), DownloadFragment(), SettingsFragment())
    var mCurrFragmentId: Int? = null
        private set

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.isChecked = true
        when(item.itemId){
            R.id.nav_search -> showFragemnt(0)
            R.id.nav_download -> showFragemnt(1)
            R.id.nav_settings -> showFragemnt(2)
            else -> null
        }
        return true
    }

    private fun showFragemnt(fragmentId: Int){
        if(fragmentId != mCurrFragmentId) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            if(mCurrFragmentId != null){
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.remove(mFragmentsList[mCurrFragmentId!!])
            }
            fragmentTransaction.add(R.id.content_frame, mFragmentsList[fragmentId])
            fragmentTransaction.commit()
            mCurrFragmentId = fragmentId
            draverLayout.closeDrawers()
        }
    }

    fun showInitialFragment(){
        showFragemnt(0)
    }

    fun getCurrentFragment(): Fragment?{
        return if(mCurrFragmentId != null) this.mFragmentsList[mCurrFragmentId!!] else null
    }

    fun getFragment(id: Int): Fragment?{
        if(id>=0 && id<=mFragmentsList.count()-1)
            return mFragmentsList[id]
        return null
    }

}