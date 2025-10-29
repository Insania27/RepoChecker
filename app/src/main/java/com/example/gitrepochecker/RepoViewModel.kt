package com.example.gitrepochecker


import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltViewModel
class RepoViewModel @Inject constructor(
    private val repoRepository: RepoRepository,
    private val gitHelper: GitHelper,
    private val workManager: WorkManager
) : ViewModel() {

    val repos: Flow<List<RepoEntity>> = repoRepository.getAllRepos()

    private val _currentRepo = mutableStateOf<RepoEntity?>(null)
    val currentRepo: State<RepoEntity?> = _currentRepo

    fun loadRepo(id: Long) {
        viewModelScope.launch {
            repoRepository.getRepoByIdFlow(id).collect { repo ->
                _currentRepo.value = repo
            }
        }
    }

    fun addRepo(url: String, frequency: String, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val localPath = gitHelper.cloneRepo(url)
                if (localPath == null) {
                    callback(false, "Ошибка клонирования репозитория")
                    return@launch
                }
                val repo = RepoEntity(url = url, localPath = localPath, frequency = frequency)
                val id = repoRepository.insertRepo(repo)
                scheduleCheck(id, frequency, replace = false)
                callback(true, "")
            } catch (e: Exception) {
                Log.e("RepoViewModel", "addRepo failed", e)
                callback(false, "Ошибка: ${e.message ?: e::class.simpleName}")
            }
        }
    }

    fun checkNow(repo: RepoEntity) {
        viewModelScope.launch {
            try {
                Log.d("RepoViewModel", "Starting manual check for id=${repo.id}")
                val isOutdated = gitHelper.checkIfOutdated(repo.localPath)
                val updated = repo.copy(isOutdated = isOutdated, lastCheck = System.currentTimeMillis())

                Log.d("RepoViewModel", "checkNow result: id=${repo.id} isOutdated=$isOutdated")

                repoRepository.updateRepo(updated) // suspend

                _currentRepo.value = updated

                val fromDb = repoRepository.getRepoById(repo.id)
                Log.d("RepoViewModel", "After update DB row: $fromDb")
            } catch (e: Exception) {
                Log.e("RepoViewModel", "checkNow failed for id=${repo.id}: ${e.message}", e)
            }
        }
    }

    fun updateRepoFrequency(repo: RepoEntity, newFrequency: String) {
        viewModelScope.launch {
            try {
                val updated = repo.copy(frequency = newFrequency)
                repoRepository.updateRepo(updated)
                scheduleCheck(updated.id, newFrequency, replace = true)
                _currentRepo.value = updated
            } catch (e: Exception) {
                Log.e("RepoViewModel", "updateRepoFrequency failed", e)
            }
        }
    }

    fun deleteRepo(repo: RepoEntity) {
        viewModelScope.launch {
            try {
                workManager.cancelUniqueWork("check_${repo.id}")
                File(repo.localPath).deleteRecursively()
                repoRepository.deleteRepo(repo)
            } catch (e: Exception) {
                Log.e("RepoViewModel", "deleteRepo failed", e)
            }
        }
    }

    private fun scheduleCheck(repoId: Long, frequency: String, replace: Boolean) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val period = if (frequency == "HOURLY") 1L else 24L
        val request = PeriodicWorkRequestBuilder<RepoCheckWorker>(period, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setInputData(workDataOf("repo_id" to repoId))
            .build()

        val policy = if (replace) ExistingPeriodicWorkPolicy.REPLACE else ExistingPeriodicWorkPolicy.KEEP
        workManager.enqueueUniquePeriodicWork("check_$repoId", policy, request)
    }

    fun enqueueImmediateChecksForAll() {
        viewModelScope.launch {
            try {
                val list = repos.first()
                list.forEach { repo ->
                    val request = OneTimeWorkRequestBuilder<RepoCheckWorker>()
                        .setInputData(workDataOf("repo_id" to repo.id))
                        .setConstraints(
                            Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build()
                        )
                        .build()
                    workManager.enqueue(request)
                }
            } catch (e: Exception) {
                Log.e("RepoViewModel", "enqueueImmediateChecksForAll failed", e)
            }
        }
    }

    fun runImmediateChecksInApp() {
        viewModelScope.launch {
            try {
                val list = repos.first()
                list.forEach { repo ->
                    checkNow(repo)
                }
            } catch (e: Exception) {
                Log.e("RepoViewModel", "runImmediateChecksInApp failed", e)
            }
        }
    }
}
