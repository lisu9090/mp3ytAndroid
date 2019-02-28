package pl.edu.agh.mp3yt


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import models.SearchResult
import utils.YouTubeDownloader
import java.lang.Exception
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.view.ContextMenu
import android.widget.*
import java.io.InputStream
import java.net.URL

class SearchResultFragment: Fragment() {
    private var mModel: SearchResult? = null
    private val mDownloadManager = YouTubeDownloader()
    private lateinit var playButton: ImageButton
    private lateinit var downloadButton: ImageButton
    private var playButtonToggler = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDownloadManager.setCallbackMethod(object: YouTubeDownloader.DownloadCallback{
            override fun onDownloadCompletedCallback(result: String?) {
                when(result){
                    "Success" -> {
                        Toast.makeText(context,"Pobieranie zakończone", Toast.LENGTH_SHORT).show()
                        downloadButton.setImageResource(android.R.drawable.ic_menu_delete)
                    }
                    "Failure" -> Toast.makeText(context,"Pobieranie zakończyło się niepowodzeniem", Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(context,"Nieznany błąd aplikacji...", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_result, container, false)

        if(mModel != null){
            try{
                val getImageTask = (@SuppressLint("StaticFieldLeak")
                object : AsyncTask<String, Void, Drawable>(){
                        override fun doInBackground(vararg params: String): Drawable {
                            val imageIS = URL(params[0]).content as InputStream
                            return Drawable.createFromStream(imageIS, "default.jpg")
                        }
                    }).execute(mModel!!.mImgSrc)

                (view.findViewById(R.id.item_name) as TextView).text = mModel!!.name
                (view.findViewById(R.id.item_id) as TextView).text = mModel!!.itemId
                downloadButton = view.findViewById(R.id.download_button) as ImageButton
                downloadButton.setOnClickListener {
                    mDownloadManager.execute(mModel!!.itemId, mModel!!.name)
                    Toast.makeText(context,"Pobieram...", Toast.LENGTH_SHORT).show()
                }
                playButton = view.findViewById(R.id.play_button) as ImageButton
                playButton.setOnClickListener {
                    switchButtonImage()
                }
                (view.findViewById(R.id.item_image) as ImageView).setImageDrawable(getImageTask.get())
            }
            catch (e: Exception){
                e.printStackTrace()
            }
        }
        return view
    }

    private fun switchButtonImage(){
        if (playButtonToggler)
            playButton.setImageResource(android.R.drawable.ic_media_play)
        else
            playButton.setImageResource(android.R.drawable.ic_media_pause)

        playButtonToggler = !playButtonToggler
    }

    companion object {
        fun getInstance(model: SearchResult): SearchResultFragment {
            val ret = SearchResultFragment()
            ret.mModel = model
            return ret
        }
    }
}
