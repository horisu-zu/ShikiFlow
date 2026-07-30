package com.example.shikiflow.presentation.viewmodel.comment.editor

sealed interface EditorEvent<T> {
    data class Published<T>(val entry: T, val parentEntryId: Int?): EditorEvent<T>
    data class Updated<T>(val entry: T): EditorEvent<T>
    data class Deleted<T>(val entryId: Int): EditorEvent<T>
}