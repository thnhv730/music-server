package com.example.musicserver

import android.content.Context
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionError
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

@UnstableApi
class LibraryCallback(
    private val songs: List<Song>
) : MediaLibraryService.MediaLibrarySession.Callback {

    companion object {
        private const val ROOT = "root"
        private const val SONGS = "songs"
    }

    override fun onGetLibraryRoot(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<MediaItem>> {

        val root = MediaItem.Builder()
            .setMediaId(ROOT)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle("Local Player Server")
                    .setIsBrowsable(true)
                    .setIsPlayable(false)
                    .build()
            )
            .build()

        return Futures.immediateFuture(LibraryResult.ofItem(root, params))
    }

    override fun onGetChildren(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {

        return when (parentId) {
            ROOT -> {
                val node = MediaItem.Builder()
                    .setMediaId(SONGS)
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle("Songs")
                            .setIsBrowsable(true)
                            .setIsPlayable(false)
                            .build()
                    )
                    .build()

                Futures.immediateFuture(
                    LibraryResult.ofItemList(ImmutableList.of(node), params)
                )
            }

            SONGS -> {
                val items = songs.map { it.toMediaItem() }
                Futures.immediateFuture(
                    LibraryResult.ofItemList(ImmutableList.copyOf(items), params)
                )
            }

            else -> Futures.immediateFuture(
                LibraryResult.ofError(SessionError.ERROR_BAD_VALUE)
            )
        }
    }

    override fun onSetMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: List<MediaItem>,
        startIndex: Int,
        startPositionMs: Long
    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition?>? {

        val resolved = mediaItems.map { item ->
            val id = item.mediaId.toLongOrNull()
            val song = id?.let { sid -> songs.firstOrNull { it.id == sid } }
            song?.toMediaItem() ?: item
        }

        return Futures.immediateFuture(
            MediaSession.MediaItemsWithStartPosition(resolved, startIndex, startPositionMs)
        )
    }

    private fun Song.toMediaItem(): MediaItem {
        val extras = Bundle().apply { putLong("albumId", albumId) }
        return MediaItem.Builder()
            .setMediaId(id.toString())
            .setUri(contentUri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setAlbumTitle(album)
                    .setIsPlayable(true)
                    .setIsBrowsable(false)
                    .setExtras(extras)
                    .build()
            )
            .build()
    }
}