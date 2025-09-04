package com.example.shikiflow.di.annotations

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GithubOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KodikOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MangaDexRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GithubRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KodikRetrofit