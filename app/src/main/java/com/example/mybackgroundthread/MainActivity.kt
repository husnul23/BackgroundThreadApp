package com.example.mybackgroundthread

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity(), MyAsyncCallback {

    companion object {
        private const val INPUT_STRING = "Halo Ini Demo AsyncTask!!"
        private const val LOG_ASYNC = "DemoAsync"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val demoAsync = DemoAsync(this)
        demoAsync.execute(INPUT_STRING)

        tv_status.setText(R.string.status_pre)
        tv_desc.text = INPUT_STRING

        GlobalScope.launch(Dispatchers.IO) {
            //background thread
            val input = INPUT_STRING

            var output: String? = null

            Log.d(LOG_ASYNC, "status : doInBackground")
            try {
                //input stringnya ditambah dengan string 'happy code'
                output = "$input Happy code"

                delay(2000)

                //pindah ke main thread untuk update ui
                withContext(Dispatchers.Main) {
                    tv_status.setText(R.string.status_post)
                    tv_desc.text = output
                }

            } catch (e: Exception) {
                Log.d(LOG_ASYNC, e.message.toString())
            }
        }

    }


    override fun onPreExecute() {
    }

    override fun onPostExecute(result: String) {
    }

    private class DemoAsync(myListener: MyAsyncCallback) : AsyncTask<String, Void, String>() {

        private val myListener: WeakReference<MyAsyncCallback>
        init {
            this.myListener = WeakReference(myListener)
        }

        companion object {
            private val LOG_ASYNC = "DemoAsync"
        }

        override fun onPreExecute() {
            super.onPreExecute()
            Log.d(LOG_ASYNC, "status : onPreExecute")

            val myListener = this.myListener.get()
            myListener?.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): String {
            Log.d(LOG_ASYNC, "status : doInBackground")

            var output: String? = null

            try {
                val input = params[0]
                output = "$input Selamat Belajar!!"
                Thread.sleep(2000)

            } catch (e: Exception) {
                Log.d(LOG_ASYNC, e.message.toString())
            }

            return output.toString()
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            Log.d(LOG_ASYNC, "status : onPostExecute")

            val myListener = this.myListener.get()
            myListener?.onPostExecute(result)
        }
    }
}

internal interface MyAsyncCallback {
    fun onPreExecute()
    fun onPostExecute(text: String)
}