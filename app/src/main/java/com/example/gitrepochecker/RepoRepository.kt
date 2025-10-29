package com.example.gitrepochecker

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RepoRepository @Inject constructor(
    private val repoDao: RepoDao
) {
    fun getAllRepos(): Flow<List<RepoEntity>> = repoDao.getAll()

    suspend fun getRepoById(id: Long): RepoEntity? = repoDao.getById(id)

    fun getRepoByIdFlow(id: Long): Flow<RepoEntity?> = repoDao.getByIdFlow(id)

    suspend fun insertRepo(repo: RepoEntity): Long = repoDao.insert(repo)

    suspend fun updateRepo(repo: RepoEntity) = repoDao.update(repo)

    suspend fun deleteRepo(repo: RepoEntity) = repoDao.delete(repo)
}