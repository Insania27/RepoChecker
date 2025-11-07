package com.example.gitrepochecker

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.BranchTrackingStatus
import org.eclipse.jgit.revwalk.RevWalk
import java.io.File
import java.util.UUID
import kotlin.use

class GitHelper(private val context: Context) {

    suspend fun cloneRepo(url: String): String = withContext(Dispatchers.IO) {
        val tag = "GitHelper"
        val parsedName = try {
            Uri.parse(url).lastPathSegment?.removeSuffix(".git")
        } catch (_: Exception) {
            null
        }
        val repoName = parsedName?.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()

        val reposDir = File(context.filesDir, "repos")
        if (!reposDir.exists()) {
            val ok = reposDir.mkdirs()
            if (!ok) throw IllegalStateException("Не удалось создать директорию репозиториев: ${reposDir.absolutePath}")
        }

        val localDir = File(reposDir, repoName)
        if (localDir.exists()) {
            localDir.deleteRecursively()
        }

        try {
            Log.d(tag, "Cloning $url into ${localDir.absolutePath}")
            Git.cloneRepository()
                .setURI(url)
                .setDirectory(localDir)
                .call()
            Log.d(tag, "Clone success: ${localDir.absolutePath}")
            return@withContext localDir.absolutePath
        } catch (e: Exception) {
            try { localDir.deleteRecursively() } catch (_: Exception) {}
            Log.e(tag, "Clone failed for $url: ${e.message}", e)
            throw e
        }
    }


    suspend fun checkIfOutdated(localPath: String): Boolean = withContext(Dispatchers.IO) {
        val tag = "GitHelper"
        try {
            val repoDir = File(localPath)
            if (!repoDir.exists()) {
                Log.e(tag, "Local repo dir does not exist: $localPath")
                return@withContext false
            }

            Git.open(repoDir).use { git ->
                Log.d(tag, "Fetching for repo at $localPath")
                try {
                    git.fetch().call()
                } catch (e: Exception) {
                    Log.e(tag, "Fetch failed: ${e.message}", e)
                    throw e
                }

                val repository = git.repository
                val branch = repository.branch

                val status = BranchTrackingStatus.of(repository, branch)
                if (status != null) {
                    Log.d(tag, "BranchTrackingStatus: ahead=${status.aheadCount}, behind=${status.behindCount}")
                    return@withContext status.behindCount > 0
                }

                val localRef = repository.resolve(branch)
                val remoteRef = repository.resolve("refs/remotes/origin/$branch")

                if (localRef == null || remoteRef == null) {
                    Log.w(tag, "Could not resolve refs: localRef=$localRef remoteRef=$remoteRef")
                    return@withContext false
                }

                val revWalk = RevWalk(repository)
                try {
                    val remoteCommit = revWalk.parseCommit(remoteRef)
                    val localCommit = revWalk.parseCommit(localRef)
                    revWalk.markStart(remoteCommit)
                    revWalk.markUninteresting(localCommit)

                    for (c in revWalk) {
                        Log.d(tag, "Found remote-only commit: ${c.name}")
                        return@withContext true
                    }
                    return@withContext false
                } finally {
                    revWalk.close()
                }
            }
        } catch (e: Exception) {
            Log.e("GitHelper", "checkIfOutdated failed: ${e.message}", e)
            throw e
        }
    }

//    fun getRepoInfo(localPath: String): String {
//        try {
//            Git.open(File(localPath)).use { git ->
//                val lastCommit = git.log().setMaxCount(1).call().iterator().next()
//                return "Last commit: ${lastCommit.name} - ${lastCommit.fullMessage}"
//            }
//        } catch (e: Exception) {
//            return "Error getting info"
//        }
//    }

    fun getRepoInfo(localPath: String): String {
        try {
            Git.open(File(localPath)).use { git ->
                val lastCommit = git.log().setMaxCount(1).call().iterator().next()

                val shortMessage = (lastCommit.shortMessage ?: "").replace("\n", " ").trim()

                return "Последний коммит - $shortMessage"
            }
        } catch (e: Exception) {
            return "Error getting info"
        }
    }

    suspend fun pullRepo(localPath: String): Boolean = withContext(Dispatchers.IO) {
        val tag = "GitHelper"
        try {
            val repoDir = File(localPath)
            if (!repoDir.exists()) {
                Log.e(tag, "Local repo dir does not exist for pull: $localPath")
                return@withContext false
            }

            Git.open(repoDir).use { git ->
                try {
                    git.fetch().call()
                } catch (e: Exception) {
                    Log.w(tag, "Fetch during pull failed: ${e.message}", e)
                }

                try {
                    val pullResult = git.pull().call()
                    Log.d(tag, "Pull completed: $pullResult")
                    return@withContext true
                } catch (e: Exception) {
                    Log.e(tag, "Pull failed: ${e.message}", e)
                    return@withContext false
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "pullRepo failed: ${e.message}", e)
            return@withContext false
        }
    }


}