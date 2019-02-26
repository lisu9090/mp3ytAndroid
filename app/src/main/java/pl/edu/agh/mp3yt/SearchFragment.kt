package pl.edu.agh.mp3yt

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class SearchFragment : Fragment() {
    var mSearchEditText: EditText? = null
        private set
    var mSearchButton: Button? = null
        private set
    var mSearchResultTextView: TextView? = null
        private set

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        mSearchEditText = view.findViewById(R.id.search_input)
        mSearchButton = view.findViewById(R.id.search_button)
        mSearchResultTextView = view.findViewById(R.id.search_results)
        return view
    }

}
