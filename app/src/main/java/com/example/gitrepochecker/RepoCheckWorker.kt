package com.example.gitrepochecker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.gitrepochecker.GitTrackerApplication.Companion.CHANNEL_ID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.runBlocking

@HiltWorker
class RepoCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repoRepository: RepoRepository,
    private val gitHelper: GitHelper
) : Worker(context, params) {

    override fun doWork(): Result {
        val repoId = inputData.getLong("repo_id", -1)
        if (repoId == -1L) return Result.failure()

        runBlocking {
            val repo = repoRepository.getRepoById(repoId) ?: return@runBlocking Result.failure()
            val isOutdated = gitHelper.checkIfOutdated(repo.localPath)
            if (isOutdated && !repo.isOutdated) {
                sendNotification(repo.url)
            }
            repo.isOutdated = isOutdated
            repo.lastCheck = System.currentTimeMillis()
            repoRepository.updateRepo(repo)
        }
        return Result.success()
    }

    private fun sendNotification(url: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_outdated)
            .setContentTitle("Repository Outdated")
            .setContentText("The repository at $url is outdated.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(url.hashCode(), builder.build())
    }
}