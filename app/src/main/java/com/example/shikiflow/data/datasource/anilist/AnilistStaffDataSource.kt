package com.example.shikiflow.data.datasource.anilist

import com.apollographql.apollo.ApolloClient
import com.example.graphql.anilist.StaffDetailsQuery
import com.example.shikiflow.data.datasource.StaffDataSource
import com.example.shikiflow.data.mapper.anilist.AnilistStaffMapper.toDomain
import com.example.shikiflow.domain.model.staff.StaffDetails
import com.example.shikiflow.utils.AnilistUtils.toResult
import javax.inject.Inject

class AnilistStaffDataSource @Inject constructor(
    private val apolloClient: ApolloClient
): StaffDataSource {
    override suspend fun getStaffDetails(staffId: Int): Result<StaffDetails> {
        val staffQuery = StaffDetailsQuery(staffId)

        val response = apolloClient.query(staffQuery).execute()

        return response.toResult().map { data ->
            data.Staff?.toDomain() ?: throw NoSuchElementException("Empty Response")
        }
    }
}