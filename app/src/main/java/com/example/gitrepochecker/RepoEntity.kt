package com.example.gitrepochecker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repos")
data class RepoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val localPath: String,
    val frequency: String,
    var isOutdated: Boolean = false,
    var lastCheck: Long = 0
)