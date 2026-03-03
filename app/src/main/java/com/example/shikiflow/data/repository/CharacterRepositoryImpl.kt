package com.example.shikiflow.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.shikiflow.data.datasource.CharactersDataSource
import com.example.shikiflow.data.local.source.CharactersPagingSource
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.character.MediaCharacterShort
import com.example.shikiflow.domain.model.character.MediaCharacter
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.CharacterRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterRepositoryImpl @Inject constructor(
    private val shikimoriDataSource: CharactersDataSource,
    private val anilistDataSource: CharactersDataSource,
    private val settingsRepository: SettingsRepository
): CharacterRepository {

    private fun getSource() = runBlocking {
        when(settingsRepository.authTypeFlow.first()) {
            AuthType.SHIKIMORI -> shikimoriDataSource
            AuthType.ANILIST -> anilistDataSource
        }
    }

    override suspend fun getCharacterDetails(
        characterId: Int
    ): Result<MediaCharacter> = getSource().getCharacterDetails(characterId)
        /*val query = CharacterDetailsQuery(
            ids = Optional.presentIfNotNull(listOf(characterId))
        )

        return try {
            val response = apolloClient.query(query).execute()

            response.data?.let { charactersResponse ->
                Result.success(charactersResponse.characters.first())
            } ?: Result.failure(Exception("No data"))
        } catch (e: Exception) {
            Result.failure(e)
        }*/

    override fun getMediaCharacters(
        mediaId: Int,
        mediaType: MediaType
    ): Flow<PagingData<MediaCharacterShort>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = true,
                prefetchDistance = 9,
                initialLoadSize = 15
            ),
            pagingSourceFactory = {
                CharactersPagingSource(
                    charactersDataSource = getSource(),
                    mediaId = mediaId,
                    mediaType = mediaType
                )
            }
        ).flow
    }
}