package com.example.shikiflow.data.mapper.common

import com.example.shikiflow.data.datasource.dto.anilist.CountryOfOriginDto
import com.example.shikiflow.domain.model.media_details.CountryOfOrigin

object CountryOfOriginMapper {
    fun CountryOfOriginDto.toCountryOfOrigin(): CountryOfOrigin {
        return when(this) {
            CountryOfOriginDto.JAPAN -> CountryOfOrigin.JAPAN
            CountryOfOriginDto.SOUTH_KOREA -> CountryOfOrigin.SOUTH_KOREA
            CountryOfOriginDto.CHINA -> CountryOfOrigin.CHINA
            CountryOfOriginDto.TAIWAN -> CountryOfOrigin.TAIWAN
        }
    }

    fun CountryOfOrigin.toDto(): CountryOfOriginDto {
        return when(this) {
            CountryOfOrigin.JAPAN -> CountryOfOriginDto.JAPAN
            CountryOfOrigin.SOUTH_KOREA -> CountryOfOriginDto.SOUTH_KOREA
            CountryOfOrigin.CHINA -> CountryOfOriginDto.CHINA
            CountryOfOrigin.TAIWAN -> CountryOfOriginDto.TAIWAN
        }
    }
}