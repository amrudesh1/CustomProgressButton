package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var downloadID: Long = 0
    lateinit var URL: String
    private lateinit var notificationManager: NotificationManager
    val handler = Handler()
    lateinit var runnable: Runnable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        notificationManager = ContextCompat.getSystemService(this, NotificationManager::class.java
        ) as NotificationManager
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))



        custom_button.setOnClickListener {
            if (button_group.checkedRadioButtonId != -1) {
                when (button_group.checkedRadioButtonId) {
                    R.id.glide_image_loading_button -> URL = URL_1
                    R.id.retrofit_button -> URL = URL_2
                    R.id.load_app_button -> URL = URL_3
                }
                custom_button.startAnimation()
                download()
            } else {
                Toast.makeText(this, "Please Select a Option", Toast.LENGTH_LONG).show()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                Log.i("DownloadStatus", "DownloadComplete")
                handler.removeCallbacks(runnable)
                notificationManager.sendNotification(this@MainActivity.getString(R.string.NOTIFICATION_MESSAGE), this@MainActivity, downloadID)
                Toast.makeText(this@MainActivity, "Download Completed", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun download() {
        notificationManager.cancelAll()
        Log.i("DownloadURL", URL)

        val request =
                DownloadManager.Request(Uri.parse(URL))
                        .setTitle(getString(R.string.app_name))
                        .setDescription(getString(R.string.app_description))
                        .setRequiresCharging(false)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        Log.i("Download", "DownloadStarted")
        val FileDowloadQuery = DownloadManager.Query()
        FileDowloadQuery.setFilterById(downloadID)
        val cursor = downloadManager.query(FileDowloadQuery)
        if (cursor.moveToFirst()) {
            runnable = Runnable {
                val bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                Log.i("DownloadData", "DownloadData:$bytes_downloaded")
                handler.postDelayed(runnable, 200)
            }
            handler.postDelayed(runnable, 200)


        }


    }

    companion object {
        private const val URL_1 = "https://codeload.github.com/bumptech/glide/zip/master"
        private const val URL_2 = "https://codeload.github.com/square/retrofit/zip/master"

        private const
        val URL_3 =
                "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }


}
