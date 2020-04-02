package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var downloadID: Long = 0
    lateinit var URL: String
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    val handler = Handler()
    lateinit var runnable: Runnable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        custom_button.setOnClickListener {
            if (button_group.checkedRadioButtonId != -1) {
                custom_button.dialogChecked(true)
                when (button_group.checkedRadioButtonId) {
                    R.id.glide_image_loading_button -> URL = URL_1
                    R.id.retrofit_button -> URL = URL_2
                    R.id.load_app_button -> URL = URL_3
                }
                custom_button.buttonClicked()
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
                Toast.makeText(this@MainActivity, "Download Completed", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun download() {
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
        val ImageDownloadQuery = DownloadManager.Query()
        //set the query filter to our previously Enqueued download
        //set the query filter to our previously Enqueued download
        ImageDownloadQuery.setFilterById(downloadID)

        //Query the download manager about downloads that have been requested.

        //Query the download manager about downloads that have been requested.
        val cursor = downloadManager.query(ImageDownloadQuery)
        if (cursor.moveToFirst()) {
            DownloadStatus(cursor, downloadID)

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


    private fun DownloadStatus(cursor: Cursor, DownloadId: Long) {

        //column for download  status
        val columnIndex: Int = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
        val status: Int = cursor.getInt(columnIndex)
        //column for reason code if the download failed or paused
        val columnReason: Int = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
        val reason: Int = cursor.getInt(columnReason)
        //get the download filename
        var statusText = ""
        var reasonText = ""
        when (status) {
            DownloadManager.STATUS_FAILED -> {
                statusText = "STATUS_FAILED"
                when (reason) {
                    DownloadManager.ERROR_CANNOT_RESUME -> reasonText = "ERROR_CANNOT_RESUME"
                    DownloadManager.ERROR_DEVICE_NOT_FOUND -> reasonText = "ERROR_DEVICE_NOT_FOUND"
                    DownloadManager.ERROR_FILE_ALREADY_EXISTS -> reasonText = "ERROR_FILE_ALREADY_EXISTS"
                    DownloadManager.ERROR_FILE_ERROR -> reasonText = "ERROR_FILE_ERROR"
                    DownloadManager.ERROR_HTTP_DATA_ERROR -> reasonText = "ERROR_HTTP_DATA_ERROR"
                    DownloadManager.ERROR_INSUFFICIENT_SPACE -> reasonText = "ERROR_INSUFFICIENT_SPACE"
                    DownloadManager.ERROR_TOO_MANY_REDIRECTS -> reasonText = "ERROR_TOO_MANY_REDIRECTS"
                    DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> reasonText = "ERROR_UNHANDLED_HTTP_CODE"
                    DownloadManager.ERROR_UNKNOWN -> reasonText = "ERROR_UNKNOWN"
                }
            }
            DownloadManager.STATUS_PAUSED -> {
                statusText = "STATUS_PAUSED"
                when (reason) {
                    DownloadManager.PAUSED_QUEUED_FOR_WIFI -> reasonText = "PAUSED_QUEUED_FOR_WIFI"
                    DownloadManager.PAUSED_UNKNOWN -> reasonText = "PAUSED_UNKNOWN"
                    DownloadManager.PAUSED_WAITING_FOR_NETWORK -> reasonText = "PAUSED_WAITING_FOR_NETWORK"
                    DownloadManager.PAUSED_WAITING_TO_RETRY -> reasonText = "PAUSED_WAITING_TO_RETRY"
                }
            }
            DownloadManager.STATUS_PENDING -> statusText = "STATUS_PENDING"
            DownloadManager.STATUS_RUNNING -> statusText = "STATUS_RUNNING"
            DownloadManager.STATUS_SUCCESSFUL -> {
                statusText = "STATUS_SUCCESSFUL"
                reasonText = "Filename:\n${button_group.checkedRadioButtonId}"
            }
        }
        if (DownloadId == downloadID) {
            val toast = Toast.makeText(this@MainActivity,
                    """
                        File Download Status:
                        $statusText
                        $reasonText
                        """.trimIndent(),
                    Toast.LENGTH_LONG)
            toast.setGravity(Gravity.TOP, 25, 400)
            toast.show()
        }
    }

}
