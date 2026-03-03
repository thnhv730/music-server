package com.example.musicserver

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import androidx.media3.common.util.UnstableApi

@UnstableApi
class MusicIpcService : Service() {
    private val binder = object : IMusicServer.Stub() {
        override fun getServerVersion(): String = "1.0.0"

        override fun getServerInfo(): Bundle {
            val songs = LocalMusicRepository(applicationContext).loadSongs()
            return Bundle().apply {
                putString("packageName", applicationContext.packageName)
                putString("mediaServiceClass", MusicLibraryService::class.java.name)
                putInt("songCount", songs.size)
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder = binder
}