package com.example.shikiflow.presentation.screen.main.details.anime.watch

interface PlayerEvent {
    fun onPlayToggle()
    fun onSeek(milliseconds: Long)
    fun onSeekTo(positionMs: Long)
    fun onQualityChange(quality: String)
}