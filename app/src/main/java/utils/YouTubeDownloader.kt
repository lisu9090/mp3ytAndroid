package utils

import android.os.AsyncTask
import android.os.Environment
import java.io.BufferedInputStream
import java.lang.Exception
import java.net.URL
import java.net.URLConnection
import com.google.common.io.Flushables.flush
import android.os.Environment.getExternalStorageDirectory
import java.io.File
import java.io.FileOutputStream

const val APP_AUDIO_DATA_DIR = "/AudioData"
const val APP_AUDIO_API_URL_BASE = "https://mp3yt.herokuapp.com"

class YouTubeDownloader internal constructor(): AsyncTask<String, Void, String>() {
    private val baseUrl = "https://mp3yt.herokuapp.com/process/"
    private var mCallbackObject: YouTubeDownloader.DownloadCallback? = null

//    override fun onProgressUpdate(vararg values: Void?) {
//        super.onProgressUpdate(*values)
//    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        if(mCallbackObject != null)
            mCallbackObject!!.onDownloadCompletedCallback(result)
    }

    override fun doInBackground(vararg params: String?): String {
        var result = "Success"
        if(params.count() == 2){
            try{
                var count: Int
                val apiUrl = URL(baseUrl + params[0])
                val connection = apiUrl.openConnection()
                connection.connect()

//            val lenghtOfFile = connection.contentLength

                val outputFile = File(Environment.getExternalStorageDirectory().toString() + APP_AUDIO_DATA_DIR)
                if (!outputFile.exists()){
                    outputFile.mkdir()
                }
                val input = BufferedInputStream(apiUrl.openStream(),8192)

                // Output stream
                val output = FileOutputStream(outputFile.path + "/${params[1]} (${params[0]}).mp3")

                val data = ByteArray(1024)
                var total:Long = 0

                while (input.read(data) != -1){
                    count = data.size
                    total += count

                    //progers: total/lenghtOfFile

                    output.write(data, 0, count)
                }

                output.flush()
                output.flush()

                output.close()
                input.close()
            }
            catch (e: Exception){
                e.printStackTrace()
                result = "Failure"
            }
        }

        return result
    }

    fun setCallbackMethod(callbackObject : DownloadCallback){
        mCallbackObject = callbackObject
    }

    interface DownloadCallback {
        fun onDownloadCompletedCallback(result: String?)
    }


//    override fun onCancelled(result: List<String>?) {
//        super.onCancelled(result)
//    }
//
//    override fun onCancelled() {
//        super.onCancelled()
//    }
//
//    override fun onPreExecute() {
//        super.onPreExecute()
//    }
}