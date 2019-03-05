package pl.edu.agh.mp3yt

import android.annotation.SuppressLint
import android.os.Bundle
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import models.SearchResult
import utils.APP_AUDO_DATA_DIR
import utils.YouTubeDownloader
import java.io.InputStream
import java.lang.Exception
import java.net.URL

class DownloadedResultFragment : Fragment() {
    private var mModel: SearchResult? = null
    private lateinit var playButton: ImageButton
    private lateinit var deleteButton: ImageButton
    private var playButtonToggler = false
    private var mMediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_downloaded_result, container, false)
        if(mModel != null){
            try{
                val getImageTask = (@SuppressLint("StaticFieldLeak")
                object : AsyncTask<String, Void, Drawable>(){
                    override fun doInBackground(vararg params: String): Drawable {
                        val imageIS = URL(params[0]).content as InputStream
                        return Drawable.createFromStream(imageIS, "default.jpg")
                    }
                }).execute(mModel!!.mImgSrc)

                mMediaPlayer = MediaPlayer.create(context, Uri.parse(Environment.getExternalStorageDirectory().toString() + APP_AUDO_DATA_DIR + "/" + mModel!!.name))

                (view.findViewById(R.id.downloaded_item_name) as TextView).text = mModel!!.name
                (view.findViewById(R.id.downloaded_item_id) as TextView).text = mModel!!.fileModData!!.toString()
                deleteButton = view.findViewById(R.id.downloaded_button) as ImageButton
                deleteButton.setOnClickListener {
                    Toast.makeText(context,"Usuwam...", Toast.LENGTH_SHORT).show()
                }
                playButton = view.findViewById(R.id.downloaded_play_button) as ImageButton
                playButton.setOnClickListener {
                    switchButtonImage()
                    if(playButtonToggler)
                        mMediaPlayer?.start()
                    else
                        mMediaPlayer?.pause()

                }
                (view.findViewById(R.id.downloaded_item_image) as ImageView).setImageDrawable(getImageTask.get())
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
        fun getInstance(model: SearchResult): DownloadedResultFragment {
            val ret = DownloadedResultFragment()
            ret.mModel = model
            return ret
        }
    }
}
