package com.example.shikiflow.domain.model.comment

sealed interface MarkdownFormat {
    val syntax: String
}

enum class ShikimoriFormat(
    override val syntax: String
): MarkdownFormat {
    BOLD("[b][/b]"),
    ITALIC("[i][/i]"),
    STRIKETHROUGH("[s][/s]"),
    UNDERSCORE("[u][/u]"),
    SPOILER("[spoiler][/spoiler]"),
    QUOTE("[quote][/quote]"),
    LINK("[url=][/url]"),
    IMAGE("%s"),
    LIST("[list][*][/list]"),
    CODE("[code][/code]")
}

enum class AniListFormat(
    override val syntax: String
): MarkdownFormat {
    BOLD("____"),
    ITALIC("__"),
    STRIKETHROUGH("~~~~"),
    SPOILER("~!!~"),
    QUOTE("\n> "),
    LINK("[label](link)"),
    IMAGE("img(%s)"),
    YOUTUBE("youtube()"),
    VIDEO("webm(%s)"),
    ORDERED_LIST("\n1."),
    UNORDERED_LIST("\n-"),
    CODE("` `")
}