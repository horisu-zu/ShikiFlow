package com.example.shikiflow.data.local.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo.ApolloClient
import com.example.graphql.anilist.UserFavoriteAnimeQuery
import com.example.graphql.anilist.UserFavoriteCharactersQuery
import com.example.graphql.anilist.UserFavoriteMangaQuery
import com.example.graphql.anilist.UserFavoriteStaffQuery
import com.example.graphql.anilist.UserFavoriteStudiosQuery
import com.example.shikiflow.data.mapper.anilist.AnilistUserMapper.toUserFavorite
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.model.user.UserFavorite

class FavoritesPagingSource(
    private val apolloClient: ApolloClient,
    private val userId: Int,
    private val favoriteCategory: FavoriteCategory
): PagingSource<Int, UserFavorite>() {

    override fun getRefreshKey(state: PagingState<Int, UserFavorite>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserFavorite> {
        val currentPage = params.key ?: 1
        val pageSize = params.loadSize

        return try {
            val nodes = when (favoriteCategory) {
                FavoriteCategory.ANIME -> loadAnime(currentPage, pageSize)
                FavoriteCategory.MANGA -> loadManga(currentPage, pageSize)
                FavoriteCategory.CHARACTER -> loadCharacters(currentPage, pageSize)
                FavoriteCategory.STAFF -> loadStaff(currentPage, pageSize)
                FavoriteCategory.STUDIO -> loadStudios(currentPage, pageSize)
                else -> emptyList()
            }

            LoadResult.Page(
                data = nodes,
                prevKey = if (currentPage > 1) currentPage - 1 else null,
                nextKey = if (nodes.size < pageSize) null else currentPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun loadAnime(page: Int, size: Int): List<UserFavorite> {
        val response = apolloClient.query(UserFavoriteAnimeQuery(page, size, userId)).execute()

        val animeNodes = response.data?.User?.favourites?.anime?.nodes

        val result = animeNodes?.mapNotNull { favoriteMedia ->
            favoriteMedia?.aLFavoriteMediaShort?.toUserFavorite(favoriteCategory)
        } ?: emptyList()

        return result
    }

    private suspend fun loadManga(page: Int, size: Int): List<UserFavorite> {
        val response = apolloClient.query(UserFavoriteMangaQuery(page, size, userId)).execute()
        return response.data?.User?.favourites?.manga?.nodes?.mapNotNull { favoriteMedia ->
            favoriteMedia?.aLFavoriteMediaShort?.toUserFavorite(favoriteCategory)
        } ?: emptyList()
    }

    private suspend fun loadCharacters(page: Int, size: Int): List<UserFavorite> {
        val response = apolloClient.query(UserFavoriteCharactersQuery(page, size, userId)).execute()
        return response.data?.User?.favourites?.characters?.nodes?.mapNotNull { favoriteMedia ->
            favoriteMedia?.aLFavoriteCharacterShort?.toUserFavorite()
        } ?: emptyList()
    }

    private suspend fun loadStaff(page: Int, size: Int): List<UserFavorite> {
        val response = apolloClient.query(UserFavoriteStaffQuery(page, size, userId)).execute()
        return response.data?.User?.favourites?.staff?.nodes?.mapNotNull { favoriteMedia ->
            favoriteMedia?.aLFavoriteStaffShort?.toUserFavorite()
        } ?: emptyList()
    }

    private suspend fun loadStudios(page: Int, size: Int): List<UserFavorite> {
        val response = apolloClient.query(UserFavoriteStudiosQuery(page, size, userId)).execute()
        return response.data?.User?.favourites?.studios?.nodes?.mapNotNull { favoriteMedia ->
            favoriteMedia?.aLFavoriteStudioShort?.toUserFavorite()
        } ?: emptyList()
    }
}