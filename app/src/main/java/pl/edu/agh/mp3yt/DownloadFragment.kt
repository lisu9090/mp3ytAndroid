package pl.edu.agh.mp3yt

import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import utils.APP_AUDO_DATA_DIR
import java.io.File

class DownloadFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val audioDataDir = File(Environment.getExternalStorageDirectory().toString() + APP_AUDO_DATA_DIR)
        if(!audioDataDir.exists()){
            audioDataDir.mkdir()
        }

        //add audio files info and list them
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_download, container, false)
    }
}
