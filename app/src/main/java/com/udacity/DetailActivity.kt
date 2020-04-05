package com.udacity

import android.app.DownloadManager
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*


class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        if (intent.extras != null) {
            Log.i("DownloadId", "Id is:" + intent.extras!!.getLong("downloadId"))
        }

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        val q = DownloadManager.Query()
        q.setFilterById(intent.extras!!.getLong("downloadId"))
        val c: Cursor = downloadManager.query(q)
        if (c.moveToFirst()) {
            val status: Int = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                title = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE))
                fileNameText.text = title
                downloadStatusTextView.text = getStatusMessage(status)
            }
        }


        okButton.setOnClickListener {
            finish()
        }
    }

    private fun getStatusMessage(status: Int): String {
        var message = "PENDING"
        when (status) {
            DownloadManager.STATUS_PAUSED -> {
                message = "PAUSED"
            }
            DownloadManager.STATUS_PENDING -> {
                message = "PENDING"
            }
            DownloadManager.STATUS_RUNNING -> {
                message = "RUNNING"
            }
            DownloadManager.STATUS_SUCCESSFUL -> {
                message = "SUCCESSFUL"
            }
            DownloadManager.STATUS_FAILED -> {
                message = "FAILED"
            }
        }

        return message
    }
}
