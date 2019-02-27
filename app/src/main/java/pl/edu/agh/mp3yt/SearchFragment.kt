package pl.edu.agh.mp3yt

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.RelativeLayout
//import android.widget.Button
//import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView

class SearchFragment : Fragment() {
//    var mSearchEditText: EditText? = null
//        private set
//    var mSearchButton: Button? = null
//        private set
//    var mSearchResultTextView: TextView? = null
//        private set
    var mSearchView: SearchView? = null
        private set
//    var mSearch_result_container: RelativeLayout? = null
//        private set
    var mNoResults: TextView? = null
        private set

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
//        mSearchEditText = view.findViewById(R.id.search_input)
//        mSearchButton = view.findViewById(R.id.search_button)

//        val myWebView: WebView = view.findViewById(R.id.web_view)
//        myWebView.loadUrl("https://i.ytimg.com/vi/BlBvrgxnj64/hqdefault.jpg")
//        mSearchResultTextView = view.findViewById(R.id.search_results)
        mSearchView = view.findViewById(R.id.search_view)
//        mSearch_result_container = view.findViewById(R.id.search_result_container)
        mNoResults = view.findViewById(R.id.no_results)

        return view
    }

}
