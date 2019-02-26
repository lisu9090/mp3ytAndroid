package utils

import android.os.AsyncTask
import android.os.Environment
import java.io.BufferedInputStream
import java.lang.Exception
import java.net.URL
import java.net.URLConnection
import com.google.common.io.Flushables.flush
import android.os.Environment.getExternalStorageDirectory
import java.io.FileOutputStream


class YouTubeDownloader internal constructor(): AsyncTask<String, Void, String>() {
    private val baseUrl = "https://mp3yt.herokuapp.com/process/"

//    override fun onProgressUpdate(vararg values: Void?) {
//        super.onProgressUpdate(*values)
//    }
//
//    override fun onPostExecute(result: List<String>?) {
//        super.onPostExecute(result)
//    }

    override fun doInBackground(vararg params: String?): String {
        var result = "Success"
        try{
            var count: Int
            val apiUrl = URL(baseUrl + params[0])
            val connection = apiUrl.openConnection()
            connection.connect()

//            val lenghtOfFile = connection.contentLength

            val input = BufferedInputStream(apiUrl.openStream(),8192)

            // Output stream
            val output = FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/${params[0]}.mp3")

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

        return result
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