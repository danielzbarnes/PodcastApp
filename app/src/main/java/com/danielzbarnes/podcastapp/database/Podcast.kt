package com.danielzbarnes.podcastapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Podcast(@PrimaryKey var id: String = "",
                   var title: String = "",
                   var series: String = "",
                   var author: String = "",
                   var date: String = "",
                   @ColumnInfo(name = "reference")
                   var reference: String = "",
                   var url: String = "",
                   var duration: String = ""): Serializable { }