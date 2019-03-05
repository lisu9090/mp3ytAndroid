package pl.edu.agh.mp3yt

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException

import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff

import com.google.api.services.youtube.YouTubeScopes

import android.Manifest
import android.accounts.AccountManager
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import models.SearchResult

import java.io.IOException
import java.util.ArrayList
import java.util.Arrays

import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import utils.FragmentNavigatorListener
import utils.YouTubeDownloader

//import utils.NavigationListener

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private lateinit var mCredential: GoogleAccountCredential
    private var mSearchFragment:SearchFragment? = null
    internal lateinit var mProgress: ProgressDialog
    private val mDownloadManager = YouTubeDownloader()

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private val isDeviceOnline: Boolean
        get() {
            val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private val isGooglePlayServicesAvailable: Boolean
        get() {
            val apiAvailability = GoogleApiAvailability.getInstance()
            val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this)
            return connectionStatusCode == ConnectionResult.SUCCESS
        }

    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access storage system to save downloaded files (mp3)",
                    REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }

        val navigationView: NavigationView? = findViewById(R.id.nav_view)
        val navigator = FragmentNavigatorListener(supportFragmentManager, findViewById(R.id.drawer_layout))
        navigator.showInitialFragment()
        if(navigator.getFragment(0) is SearchFragment)
            mSearchFragment = navigator.getFragment(0) as SearchFragment
        navigationView!!.setNavigationItemSelectedListener(navigator)

        mProgress = ProgressDialog(this)
        mProgress.setMessage("Szukam...")

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
            applicationContext, Arrays.asList(*SCOPES)
        )
            .setBackOff(ExponentialBackOff())
    }

    override fun onStart() {
        super.onStart()
        mSearchFragment?.mSearchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(!query.isNullOrEmpty()){
                    getResultsFromApi()
                    (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .hideSoftInputFromWindow(currentFocus.windowToken, 0)
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private fun getResultsFromApi() {
        if(!mSearchFragment?.mSearchView!!.query.isNullOrEmpty()){
            if (!isGooglePlayServicesAvailable) {
                acquireGooglePlayServices()
            } else if (mCredential.selectedAccountName == null) {
                chooseAccount()
            } else if (!isDeviceOnline) {
                mSearchFragment?.mNoResults!!.text = "No network connection available."
            } else {
                MakeRequestTask(mCredential).execute()
            }
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private fun chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS
            )
        ) {
            val accountName = getPreferences(Context.MODE_PRIVATE)
                .getString(PREF_ACCOUNT_NAME, null)
            if (accountName != null) {
                mCredential.selectedAccountName = accountName
                getResultsFromApi()
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                    mCredential.newChooseAccountIntent(),
                    REQUEST_ACCOUNT_PICKER
                )
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                this,
                "This app needs to access your Google account (via Contacts).",
                REQUEST_PERMISSION_GET_ACCOUNTS,
                Manifest.permission.GET_ACCOUNTS
            )
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     * activity result.
     * @param data Intent (containing result data) returned by incoming
     * activity result.
     */
    override fun onActivityResult(
        requestCode: Int, resultCode: Int, data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_GOOGLE_PLAY_SERVICES -> if (resultCode != Activity.RESULT_OK) {
                mSearchFragment?.mNoResults!!.text =
                    ("This app requires Google Play Services. Please install " + "Google Play Services on your device and relaunch this app.")
            } else {
                getResultsFromApi()
            }
            REQUEST_ACCOUNT_PICKER -> if (resultCode == Activity.RESULT_OK && data != null &&
                data!!.extras != null
            ) {
                val accountName = data!!.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                if (accountName != null) {
                    val settings = getPreferences(Context.MODE_PRIVATE)
                    val editor = settings.edit()
                    editor.putString(PREF_ACCOUNT_NAME, accountName)
                    editor.apply()
                    mCredential.selectedAccountName = accountName
                    getResultsFromApi()
                }
            }
            REQUEST_AUTHORIZATION -> if (resultCode == Activity.RESULT_OK) {
                getResultsFromApi()
            }
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     * requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     * which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     * permission
     * @param list The requested permission list. Never null.
     */
    override fun onPermissionsGranted(requestCode: Int, list: List<String>) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     * permission
     * @param list The requested permission list. Never null.
     */
    override fun onPermissionsDenied(requestCode: Int, list: List<String>) {
        // Do nothing.
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private fun acquireGooglePlayServices() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode)
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     * Google Play Services on this device.
     */
    internal fun showGooglePlayServicesAvailabilityErrorDialog(
        connectionStatusCode: Int
    ) {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val dialog = apiAvailability.getErrorDialog(
            this@MainActivity,
            connectionStatusCode,
            REQUEST_GOOGLE_PLAY_SERVICES
        )
        dialog.show()
    }

    /**
     * An asynchronous task that handles the YouTube Data API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private inner class MakeRequestTask internal constructor(credential: GoogleAccountCredential) :
        AsyncTask<Void, Void, List<SearchResult>>() {
        private var mService: com.google.api.services.youtube.YouTube? = null
        private var mLastError: Exception? = null

        /**
         * Fetch information about the "GoogleDevelopers" YouTube channel.
         * @return List of Strings containing information about the channel.
         * @throws IOException
         */
        private val dataFromApi: List<SearchResult>
            @Throws(IOException::class)
            get() {
                val channelInfo = ArrayList<SearchResult>()

                val test = mSearchFragment?.mSearchView!!.query.toString()
                val result = mService!!.search().list("id,snippet").setMaxResults(10).setQ(test)
                    .execute()

                val videos = result.items
                if (videos != null) {
                    for(video in videos) {
                        channelInfo.add(SearchResult(video.id.videoId, video.snippet.title))
                    }
                }
                return channelInfo
            }

        init {
            val transport = AndroidHttp.newCompatibleTransport()
            val jsonFactory = JacksonFactory.getDefaultInstance()
            mService = com.google.api.services.youtube.YouTube.Builder(transport, jsonFactory, credential)
                .setApplicationName("mp3yt")
                .build()
        }

        /**
         * Background task to call YouTube Data API.
         * @param params no parameters needed for this task.
         */
        override fun doInBackground(vararg params: Void): List<SearchResult>? {
            try {
                return dataFromApi
            } catch (e: Exception) {
                mLastError = e
                cancel(true)
                return null
            }
        }

        override fun onPreExecute() {
            mSearchFragment?.mNoResults!!.text = ""
            mProgress.show()
        }

        override fun onPostExecute(output: List<SearchResult>?) {
            if (output == null || output.isEmpty()) {
                mSearchFragment?.mNoResults!!.visibility = View.VISIBLE
                mSearchFragment?.mNoResults!!.text = "No results returned."
            } else {
                mSearchFragment?.mNoResults!!.visibility = View.INVISIBLE

                val fragmentTransaction = supportFragmentManager.beginTransaction()
                for(result in output){
                    fragmentTransaction.add(R.id.search_result_container, SearchResultFragment.getInstance(result))
                }
                fragmentTransaction.commit()
            }
            mProgress.hide()
        }

        override fun onCancelled() {
            mProgress.hide()
            if (mLastError != null) {
                if (mLastError is GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                        (mLastError as GooglePlayServicesAvailabilityIOException)
                            .connectionStatusCode
                    )
                } else if (mLastError is UserRecoverableAuthIOException) {
                    startActivityForResult(
                        (mLastError as UserRecoverableAuthIOException).intent,
                        MainActivity.REQUEST_AUTHORIZATION
                    )
                } else {
                    mSearchFragment?.mNoResults!!.text = ("The following error occurred:\n" + mLastError!!.message)
                }
            } else {
                mSearchFragment?.mNoResults!!.text = "Request cancelled."
            }
        }
    }

    companion object {
        internal const val REQUEST_ACCOUNT_PICKER = 1000
        internal const val REQUEST_AUTHORIZATION = 1001
        internal const val REQUEST_GOOGLE_PLAY_SERVICES = 1002
        internal const val REQUEST_PERMISSION_GET_ACCOUNTS = 1003
        internal const val REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 1004


        private const val PREF_ACCOUNT_NAME = "accountName"
        private val SCOPES = arrayOf(YouTubeScopes.YOUTUBE_READONLY)
    }
}
