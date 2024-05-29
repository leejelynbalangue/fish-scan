package com.ccs.fish.scan.data.capture

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "captures")
data class Capture(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Capture timestamp (in milliseconds)
    @ColumnInfo(name = "timestamp") //
    val timestamp: Long,

    // Capture date in "yyyy-MM-dd" format; used to query by DATE
    // To prevent inconsistency, this value is derived from timestamp
    @ColumnInfo(name = "date_string")
    val dateString: String ,

    // TODO: Make unique
    // File URI
    @ColumnInfo(name = "file_uri")
    val fileURI: String,

    // with extension, eg. test.png
    @ColumnInfo(name = "file_name")
    val fileName: String,

    // The output of AI model
    @ColumnInfo(name = "ladyfish_count")
    val ladyfishCount: Int,

    // The output of AI model
    @ColumnInfo(name = "milkfish_count")
    val milkfishCount: Int,

    @ColumnInfo(name = "bounding_boxes")
    val boundingBoxes: String,

    // Capture mode
    @ColumnInfo(name = "capture_mode")
    val captureMode: Int, // 1: photo, 2: video

    // capture time in milliseconds
    @ColumnInfo(name = "capture_time")
    val captureTime: Long
)