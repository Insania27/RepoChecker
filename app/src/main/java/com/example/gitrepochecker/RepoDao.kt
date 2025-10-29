package com.example.gitrepochecker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoDao {
    @Query("SELECT * FROM repos")
    fun getAll(): Flow<List<RepoEntity>>

    @Query("SELECT * FROM repos WHERE id = :id")
    suspend fun getById(id: Long): RepoEntity?

    @Query("SELECT * FROM repos WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<RepoEntity?>

    @Insert
    suspend fun insert(repo: RepoEntity): Long

    @Update
    suspend fun update(repo: RepoEntity)

    @Delete
    suspend fun delete(repo: RepoEntity)
}