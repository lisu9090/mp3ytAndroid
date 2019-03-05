package pl.edu.agh.mp3yt

import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import models.SearchResult
import utils.APP_AUDO_DATA_DIR
import java.io.File
import java.io.FileFilter
import java.util.*
import java.util.regex.Pattern

class DownloadFragment : Fragment() {
    private val audioDataDir = File(Environment.getExternalStorageDirectory().toString() + APP_AUDO_DATA_DIR)
    private val audioList = ArrayList<DownloadedResultFragment>()
    private val pattern = Pattern.compile("[(][A-Za-z0-9_\\-]{11}[)]")

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//
//
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_download, container, false)
        val ft = fragmentManager!!.beginTransaction()

        if(!audioDataDir.exists()){
            audioDataDir.mkdir()
        }
        //move to background

        for(file in audioDataDir.listFiles(FileFilter {!it.isDirectory && it.extension == "mp3" && it.canRead()})){
            val vidMatcher = pattern.matcher(file.name)
            var model: SearchResult? = null

            if(vidMatcher.find())
                model = SearchResult(file.name.substring(vidMatcher.start()+1, vidMatcher.end()-1), file.name, Date(file.lastModified()))

            if(model != null){
                audioList.add(DownloadedResultFragment.getInstance(model))
            }
        }

        for(fragment in audioList){
            ft.add(R.id.downloaded_result_container, fragment)
        }

        ft.commit()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if(!audioList.isEmpty()) {
            val ft = fragmentManager!!.beginTransaction()

            for (fragment in audioList) {
                ft.remove(fragment)
            }
            audioList.clear()

            ft.commit()
        }
    }
}
