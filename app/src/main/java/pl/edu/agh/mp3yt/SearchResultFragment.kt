package pl.edu.agh.mp3yt


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import models.SearchResult
import utils.YouTubeDownloader
import java.lang.Exception
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.support.annotation.UiThread
import android.widget.Toast
import java.io.InputStream
import java.net.URL
import java.time.Duration


class SearchResultFragment: Fragment() {
    private lateinit var mModel: SearchResult
    private val mDownloadManager = YouTubeDownloader()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_result, container, false)

        if(mModel != null){
            try{
                val getImageTask = (object : AsyncTask<Void, Void, Drawable>(){
                        override fun doInBackground(vararg params: Void): Drawable {
                            val imageIS = URL(mModel.mImgSrc).getContent() as InputStream
                            return Drawable.createFromStream(imageIS, "default.jpg")
                        }
                    }).execute()

                (view.findViewById(R.id.item_name) as TextView).text = mModel.name
                (view.findViewById(R.id.item_id) as TextView).text = mModel.itemId
                (view.findViewById(R.id.download_button) as Button).setOnClickListener {
                    mDownloadManager.execute(mModel.itemId)
//                    Toast.makeText(activity,"Pobieram...", Toast.LENGTH_SHORT) nie dziala :(
                }
                (view.findViewById(R.id.play_button) as Button).setOnClickListener {
                    Log.d("SearchResultFragment","SearchResultFragment.play_button clicked!")
                }
                (view.findViewById(R.id.item_image) as ImageView).setImageDrawable(getImageTask.get())
            }
            catch (e: Exception){
                e.printStackTrace()
            }
        }
        return view
    }

    companion object {
        fun getInstance(model: SearchResult): SearchResultFragment {
            val ret = SearchResultFragment()
            ret.mModel = model
            return ret
        }
    }
}
