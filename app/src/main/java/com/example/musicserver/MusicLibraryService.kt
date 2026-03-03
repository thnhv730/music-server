package com.example.musicserver

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession

@UnstableApi
class MusicLibraryService : MediaLibraryService() {

    private lateinit var player: ExoPlayer
    private lateinit var session: MediaLibrarySession

    override fun onCreate() {
        super.onCreate()

        val songs = LocalMusicRepository(this).loadSongs()

        player = ExoPlayer.Builder(this).build()

        val callback = LibraryCallback(songs)

        session = MediaLibrarySession.Builder(this, player, callback)
            .setId("music_library_session")
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession {
        return session
    }

    override fun onDestroy() {
        session.release()
        player.release()
        super.onDestroy()
    }
}