package com.example.shikiflow.presentation.viewmodel.more.about

interface AboutEvent {
    fun checkForUpdates()

    fun downloadRelease(fileName: String, url: String)

    fun installRelease(fileName: String)

    fun setAutoInstall(autoInstall: Boolean)
}