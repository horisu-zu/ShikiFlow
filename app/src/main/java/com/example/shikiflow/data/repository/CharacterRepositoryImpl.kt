package com.example.shikiflow.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.shikiflow.data.datasource.CharactersDataSource
import com.example.shikiflow.data.local.source.GenericPagingSource
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.browse.Browse
import com.example.shikiflow.domain.model.character.MediaCharacterShort
import com.example.shikiflow.domain.model.character.MediaCharacter
import com.example.shikiflow.domain.model.common.MediaRole
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.CharacterRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterRepositoryImpl @Inject constructor(
    private val shikimoriDataSource: CharactersDataSource,
    private val anilistDataSource: CharactersDataSource,
    private val settingsRepository: SettingsRepository,
    private val scope: CoroutineScope
): CharacterRepository {

    val dataSource = settingsRepository.authTypeFlow.filterNotNull()
        .map { authType ->
            when(authType) {
                AuthType.SHIKIMORI -> shikimoriDataSource
                AuthType.ANILIST -> anilistDataSource
            }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.Lazily,
            initialValue = null
        )

    override suspend fun getCharacterDetails(
        characterId: Int
    ): Flow<DataResult<MediaCharacter>> = dataSource.filterNotNull().flatMapLatest { dataSource ->
        dataSource.getCharacterDetails(characterId)
    }

    override fun getCharacterMediaRoles(
        characterId: Int,
        mediaType: MediaType,
        sort: Sort<MediaSort>
    ): Flow<PagingData<MediaRole>> = dataSource.filterNotNull().flatMapLatest { dataSource ->
        dataSource.getCharacterMediaRoles(characterId, mediaType, sort)
    }

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
                GenericPagingSource(
                    method = { page, limit ->
                        dataSource
                            .filterNotNull()
                            .first()
                            .loadMediaCharacters(page, limit, mediaId, mediaType)
                    }
                )
            }
        ).flow
    }

    override fun searchCharacters(search: String): Flow<PagingData<Browse.Character>> {
        return Pager(
            config = PagingConfig(
                pageSize = 24,
                enablePlaceholders = true,
                prefetchDistance = 12,
                initialLoadSize = 24
            ),
            pagingSourceFactory = {
                GenericPagingSource(
                    method = { page, limit ->
                        dataSource
                            .filterNotNull()
                            .first()
                            .searchCharacters(page, limit, search)
                    }
                )
            }
        ).flow
    }
}