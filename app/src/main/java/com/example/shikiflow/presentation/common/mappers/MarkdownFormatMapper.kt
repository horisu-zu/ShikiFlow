package com.example.shikiflow.presentation.common.mappers

import com.example.shikiflow.R
import com.example.shikiflow.domain.model.comment.AniListFormat
import com.example.shikiflow.domain.model.comment.MarkdownFormat
import com.example.shikiflow.domain.model.comment.ShikimoriFormat
import com.example.shikiflow.utils.IconResource

object MarkdownFormatMapper {
    fun MarkdownFormat.iconResource(): IconResource {
        return when (this) {
            AniListFormat.BOLD, ShikimoriFormat.BOLD -> IconResource.Drawable(R.drawable.ic_format_bold)
            AniListFormat.ITALIC, ShikimoriFormat.ITALIC -> IconResource.Drawable(R.drawable.ic_format_italic)
            AniListFormat.STRIKETHROUGH, ShikimoriFormat.STRIKETHROUGH -> IconResource.Drawable(R.drawable.ic_format_strikethrough)
            AniListFormat.SPOILER, ShikimoriFormat.SPOILER -> IconResource.Drawable(R.drawable.ic_format_spoiler)
            AniListFormat.QUOTE, ShikimoriFormat.QUOTE -> IconResource.Drawable(R.drawable.ic_quote)
            AniListFormat.LINK, ShikimoriFormat.LINK -> IconResource.Drawable(R.drawable.ic_link)
            AniListFormat.IMAGE, ShikimoriFormat.IMAGE -> IconResource.Drawable(R.drawable.ic_image)
            AniListFormat.YOUTUBE -> IconResource.Drawable(R.drawable.ic_youtube)
            AniListFormat.VIDEO -> IconResource.Drawable(R.drawable.ic_videocam)
            AniListFormat.ORDERED_LIST -> IconResource.Drawable(R.drawable.ic_format_ordered_list)
            AniListFormat.UNORDERED_LIST, ShikimoriFormat.LIST -> IconResource.Drawable(R.drawable.ic_format_list)
            AniListFormat.CODE, ShikimoriFormat.CODE -> IconResource.Drawable(R.drawable.ic_format_code)
            ShikimoriFormat.UNDERSCORE -> IconResource.Drawable(R.drawable.ic_format_underscore)
        }
    }
}