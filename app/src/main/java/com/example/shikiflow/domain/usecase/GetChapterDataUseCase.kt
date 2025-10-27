package com.example.shikiflow.domain.usecase

import coil3.network.HttpException
import com.example.shikiflow.domain.model.mangadex.chapter_metadata.ChapterMetadata
import com.example.shikiflow.domain.model.mangadex.chapter_metadata.ChapterMetadata.Companion.toDomain
import com.example.shikiflow.domain.repository.MangaDexRepository
import com.example.shikiflow.utils.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetChapterDataUseCase @Inject constructor(
    private val mangaDexRepository: MangaDexRepository
) {
    operator fun invoke(chapterIds: List<String>): Flow<Resource<List<ChapterMetadata>>> = flow {
        try {
            emit(Resource.Loading())

            val chapters = coroutineScope {
                chapterIds.map { chapterId ->
                    async {
                        val response = mangaDexRepository.getChapterMetadata(chapterId)
                        response.data.let { chapterMetadata ->
                            val scanlationGroupIds = chapterMetadata.relationships
                                .filter { it.type == "scanlation_group" }
                                .map { it.id }

                            if(scanlationGroupIds.isNotEmpty()) {
                                val scanlationGroups = scanlationGroupIds.map { groupId ->
                                    async { mangaDexRepository.getScanlationGroup(groupId) }
                                }.awaitAll()

                                chapterMetadata.toDomain(scanlationGroups = scanlationGroups)
                            } else {
                                val uploaderId = chapterMetadata.relationships
                                    .firstOrNull { it.type == "user" }?.id

                                uploaderId?.let {
                                    val user = mangaDexRepository.getUser(uploaderId)
                                    chapterMetadata.toDomain(uploader = user)
                                } ?: chapterMetadata.toDomain()
                            }
                        }
                    }
                }.awaitAll()
            }

            val sortedChapters = chapters.sortedBy { chapter ->
                chapter.publishAt
            }

            emit(Resource.Success(sortedChapters))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(Resource.Error("An unexpected error occurred: ${e.message}"))
        }
    }
}