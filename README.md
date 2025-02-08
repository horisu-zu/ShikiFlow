# ShikiFlow
(NON-OFFICIAL) [Shikimori](https://shikimori.one/) Mobile App

Development status: On Hold

## About the Project
ShikiFlow is a mobile app that provides a comprehensive experience for users of the Shikimori anime/manga community platform. 

## Tech Stack
Kotlin - Jetpack Compose - MVVM - Hilt - GraphQL (Apollo) - Coil

## API keys
First of all, you need to obtain and add the required keys for [Shikimori](https://shikimori.one/oauth)

The app is using OAuth authentication and currently has `shikiflow://oauth/shikimori` as its OAuth redirect uri.

As a temporary measure, I'm using `BuildConfig.kt` to set such keys.
