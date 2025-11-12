package com.example.shikiflow.utils

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.example.graphql.type.AnimeRatingEnum
import com.example.graphql.type.MangaKindEnum
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.comment.CommentType
import com.example.shikiflow.domain.model.mapper.UserRateMapper.Companion.simpleMapUserRateStatusToString
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.UserRate
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.TextNode
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.time.Clock
import kotlin.time.Instant

object Converter {
    fun formatInstant(
        instant: Instant?,
        includeTime: Boolean = false,
        includeDayOfWeek: Boolean = false
    ): String {
        return instant?.let { instant ->
            val date = Date(instant.toEpochMilliseconds())
            val currentYear = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault()).year
            val instantYear = instant
                .toLocalDateTime(TimeZone.currentSystemDefault()).year

            val pattern = buildString {
                if(includeDayOfWeek) {
                    append("EEE, ")
                }
                append("dd MMM")
                if (instantYear != currentYear) {
                    append(" yyyy")
                }
                if (includeTime && instantYear == currentYear) {
                    append(", HH:mm")
                }
            }

            SimpleDateFormat(pattern, Locale.getDefault()).format(date)
        } ?: "Not available"
    }

    fun formatDate(
        date: LocalDate,
        includeTime: Boolean = false,
        locale: Locale = Locale.getDefault()
    ): String {
        date.let { date ->
            val currentYear = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault()).year
            val instantYear = date.year

            val pattern = buildString {
                append ("dd MMM")
                if (instantYear != currentYear) {
                    append(" yyyy")
                }
                if (includeTime) {
                    append(", HH:mm")
                }
            }

            val formatter = DateTimeFormatter.ofPattern(pattern, locale)
            return date.toJavaLocalDate().format(formatter)
        }
    }

    /*fun convertInstantToString(context: Context, lastSeenInstant: Instant?): String {
        val currentTimeInstant = Clock.System.now()
        val duration = currentTimeInstant - lastSeenInstant!!
        val diffMillis = duration.inWholeMilliseconds

        return when {
            diffMillis < 60000 -> {
                context.getString(R.string.status_now)
            }

            diffMillis < 3600000 -> {
                val minutes = duration.inWholeMinutes
                if (minutes == 1L) {
                    context.getString(R.string.status_minute_ago, minutes)
                } else {
                    context.getString(R.string.status_minutes_ago, minutes)
                }
            }

            diffMillis < 86400000 -> {
                val hours = duration.inWholeHours
                when (hours) {
                    1L -> context.getString(R.string.status_hour_ago, hours)
                    in 2..4 -> context.getString(R.string.status_hours_ago, hours)
                    else -> context.getString(R.string.status_hours_ago_v2, hours)
                }
            }

            else -> {
                val date = Date(lastSeenInstant.toEpochMilliseconds())
                val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val formattedDate = formatter.format(date)

                context.getString(R.string.status_days, formattedDate)
            }
        }
    }*/

    fun formatFileSize(size: Double): String {
        return when {
            size < 1024 -> "%.0f B".format(size)
            size < 1024 * 1024 -> "%.2f KB".format(size / 1024)
            size < 1024 * 1024 * 1024 -> "%.2f MB".format(size / (1024 * 1024))
            else -> "%.2f GB".format(size / (1024 * 1024 * 1024))
        }
    }

    fun formatDuration(durationMs: Long): String {
        if (durationMs <= 0) return "00:00"

        val totalSeconds = durationMs / 1000
        val seconds = totalSeconds % 60
        val minutes = (totalSeconds / 60) % 60
        val hours = totalSeconds / 3600

        return if (hours > 0) {
            "%02d:%02d:%02d".format(hours, minutes, seconds)
        } else {
            "%02d:%02d".format(minutes, seconds)
        }
    }

    fun MangaKindEnum.isManga(): Boolean {
        return this in setOf(MangaKindEnum.manga, MangaKindEnum.manhwa,
            MangaKindEnum.manhua, MangaKindEnum.one_shot, MangaKindEnum.doujin)
    }

    fun convertRatingToString(rating: AnimeRatingEnum): Int {
        return when(rating) {
            AnimeRatingEnum.none -> R.string.rating_none
            AnimeRatingEnum.g -> R.string.rating_g
            AnimeRatingEnum.pg -> R.string.rating_pg
            AnimeRatingEnum.pg_13 -> R.string.rating_pg_13
            AnimeRatingEnum.r -> R.string.rating_r_17
            AnimeRatingEnum.r_plus -> R.string.rating_r_plus
            AnimeRatingEnum.rx -> R.string.rating_rx
            else -> R.string.common_unknown
        }
    }

    fun List<UserRate?>.groupAndSortByStatus(
        contentType: MediaType
    ): Map<Int, Int> {
        val order = UserRateStatusEnum.knownEntries

        return this
            .groupBy { it?.status ?: UserRateStatusEnum.UNKNOWN__ }
            .mapValues { it.value.size }
            .toSortedMap(compareBy { order.indexOf(it) })
            .mapKeys { simpleMapUserRateStatusToString(it.key, contentType) }
    }

    fun String.toAbbreviation(maxLetters: Int = 2): String {
        val words = this.split(Regex("\\s+")).filter { it.isNotBlank() }

        return when {
            words.size == 1 -> {
                val capitals = words[0].filter { it.isUpperCase() }
                when {
                    capitals.length >= maxLetters -> capitals.take(maxLetters)
                    capitals.isNotEmpty() -> capitals + words[0]
                        .filter { it.isLetter() && !it.isUpperCase() }
                        .take(maxLetters - capitals.length)
                        .map { it.uppercaseChar() }
                        .joinToString("").uppercase()
                    else -> words[0].take(maxLetters).uppercase()
                }
            }
            else -> words.take(maxLetters).map { it.first().uppercaseChar() }.joinToString("")
        }
    }

    fun parseChapterNumber(chapterNumber: String): Float {
        return chapterNumber.split("-", "–", "—")
            .first()
            .trim()
            .toFloatOrNull() ?: 0f
    }

    /*fun formatText(
        text: String,
        linkColor: Color
    ): AnnotatedString {
        val builder = AnnotatedString.Builder()
        formatRecursive(text, linkColor, builder)
        return builder.toAnnotatedString()
    }

    private fun formatRecursive(
        text: String,
        linkColor: Color,
        builder: AnnotatedString.Builder
    ) {
        val regex = Regex("(\\[(\\w+)(?:=(\\w+))?](.*?)\\[/\\2])|(\\[br])|(\\r\\n)")

        var lastIndex = 0

        regex.findAll(text).forEach { matchResult ->
            val fullMatch = matchResult.value
            val start = matchResult.range.first
            val end = matchResult.range.last + 1

            if (lastIndex < start) {
                builder.append(text.substring(lastIndex, start))
            }

            when {
                matchResult.groups[1]?.value != null -> {
                    val tagName = matchResult.groups[2]?.value ?: ""
                    val param = matchResult.groups[3]?.value ?: ""
                    val content = matchResult.groups[4]?.value ?: ""

                    if (param.isNotEmpty()) {
                        val annotationTag = when (tagName) {
                            "character" -> "CHARACTER_ID"
                            "spoiler" -> "SPOILER"
                            else -> "LINK"
                        }
                        val annotationValue = when (tagName) {
                            "character" -> param
                            "spoiler" -> param
                            else -> "$tagName:$param"
                        }

                        builder.pushStringAnnotation(tag = annotationTag, annotation = annotationValue)
                        builder.withStyle(SpanStyle(color = linkColor)) {
                            formatRecursive(content, linkColor, builder)
                        }
                        builder.pop()

                    } else {
                        when (tagName.lowercase()) {
                            "i" -> builder.withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { formatRecursive(content, linkColor, builder) }
                            "b" -> builder.withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { formatRecursive(content, linkColor, builder) }
                            "u" -> builder.withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) { formatRecursive(content, linkColor, builder) }
                            "s" -> builder.withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) { formatRecursive(content, linkColor, builder) }
                            else -> {
                                formatRecursive(content, linkColor, builder)
                            }
                        }
                    }
                }
                matchResult.groups[5]?.value != null || matchResult.groups[6]?.value != null -> {
                    builder.append("\n")
                }
                else -> {
                    builder.append(fullMatch)
                }
            }

            lastIndex = end
        }

        if (lastIndex < text.length) {
            builder.append(text.substring(lastIndex))
        }
    }*/

    sealed class DescriptionElement {
        data class Text(val annotatedString: AnnotatedString) : DescriptionElement()
        data class Spoiler(val label: String, val content: List<DescriptionElement>) : DescriptionElement()
        data class Image(val imageUrl: String, val aspectRatio: Float) : DescriptionElement()
        data class Video(val videoUrl: String, val thumbnailUrl: String) : DescriptionElement()
        data class Quote(
            val senderAvatarUrl: String?,
            val senderNickname: String?,
            val content: String
        ) : DescriptionElement()
    }

    enum class EntityType {
        CHARACTER,
        PERSON,
        ANIME,
        MANGA,
        RANOBE,
        COMMENT
    }

    data class EntityData(
        val id: String,
        val type: EntityType
    )

    fun parseDescriptionHtml(html: String, linkColor: Color = Color.Blue): List<DescriptionElement> {
        if (html.isBlank()) {
            return emptyList()
        }

        val doc = Ksoup.parse(html)
        val contentElement = doc.selectFirst("div.b-text_with_paragraphs") ?: doc.body()

        return parseElementContent(contentElement, linkColor)
    }

    private fun parseElementContent(containerElement: Element, linkColor: Color): List<DescriptionElement> {
        val elements = mutableListOf<DescriptionElement>()
        val textHtmlBuffer = StringBuilder()

        fun processTextBuffer() {
            if (textHtmlBuffer.isNotBlank()) {
                val annotatedString = parseInnerHtml(textHtmlBuffer.toString(), linkColor)
                if (annotatedString.isNotBlank()) {
                    elements.add(DescriptionElement.Text(annotatedString))
                }
                textHtmlBuffer.clear()
            }
        }

        containerElement.childNodes().forEach { node ->
            if (node !is Element) {
                textHtmlBuffer.append(node.outerHtml())
                return@forEach
            }

            val isSpoiler = node.tagName() == "div" && (node.hasClass("b-spoiler")
                    || node.hasClass("b-spoiler_block"))
            val isVideo = node.tagName() == "div" && node.hasClass("b-video")
            val isImage = (node.tagName() == "a" || node.tagName() == "span") && node.hasClass("b-image")
            val isQuote = (node.tagName() == "div" && node.hasClass("b-quote")
                    || node.tagName() == "blockquote")
            val isReply = node.tagName() == "div" && node.hasClass("b-replies")

            when {
                isSpoiler -> {
                    processTextBuffer()

                    val label = node.selectFirst("label")?.text()
                        ?: node.selectFirst("span")?.text()
                        ?: ""

                    val contentDiv = node.selectFirst(".content .inner")
                        ?: node.selectFirst(".content")
                        ?: node.selectFirst("> div")

                    val spoilerContent = if (contentDiv != null) {
                        parseElementContent(contentDiv, linkColor)
                    } else { emptyList() }

                    elements.add(DescriptionElement.Spoiler(label, spoilerContent))
                }
                isVideo -> {
                    processTextBuffer()

                    val linkElement = node.selectFirst("a.marker")
                        ?: node.selectFirst("a.video-link")
                    val imgElement = node.selectFirst("img")

                    val videoUrl = linkElement?.attr("href")
                        ?: linkElement?.attr("data-video") ?: ""
                    val thumbnailUrl = imgElement?.attr("src") ?: ""

                    elements.add(DescriptionElement.Video(videoUrl, thumbnailUrl))
                }
                isImage -> {
                    processTextBuffer()

                    val imageUrl = when (node.tagName()) {
                        "a" -> node.attr("href")
                        "span" -> {
                            node.selectFirst("img")?.attr("src") ?: ""
                        } else -> ""
                    }

                    val aspectRatio = extractSizeFromUrl(imageUrl)
                        ?: extractSizeFromNode(node)
                        ?: (16f / 9f)

                    elements.add(DescriptionElement.Image(imageUrl, aspectRatio))
                }
                isQuote -> {
                    processTextBuffer()

                    val senderAvatarUrl = node.selectFirst("img")
                        ?.attr("srcset") ?: node.selectFirst("img")?.attr("src")
                    val senderNickname = node.selectFirst("span")?.text()
                    val content = if(!node.selectFirst(".quote-content")?.text().isNullOrEmpty()) {
                        node.selectFirst(".quote-content")?.text() ?: ""
                    } else { node.text() }

                    elements.add(DescriptionElement.Quote(senderAvatarUrl, senderNickname, content))
                }
                isReply -> {
                    processTextBuffer()

                    val mentions = node.select("a.b-mention")
                    val annotatedReplies = buildAnnotatedString {
                        if (mentions.size > 1) { append("Replies: ") } else {
                            append("Reply: ")
                        }

                        mentions.forEachIndexed { i, mention ->
                            val username = mention.selectFirst("span")?.text() ?: ""
                            val commentId = mention.attr("href").substringAfter("/comments/")

                            pushStringAnnotation(EntityType.COMMENT.name, commentId)
                            withStyle(SpanStyle(color = linkColor)) {
                                append("@$username")
                            }
                            pop()

                            if (i < mentions.lastIndex) append(", ")
                        }
                    }

                    elements.add(DescriptionElement.Text(annotatedReplies))
                }
                else -> {
                    textHtmlBuffer.append(node.outerHtml())
                }
            }
        }

        processTextBuffer()
        return elements
    }

    private fun parseInnerHtml(html: String, linkColor: Color): AnnotatedString {
        if (html.isBlank()) return AnnotatedString("")

        val doc = Ksoup.parseBodyFragment(html)
        val builder = AnnotatedString.Builder()

        processInlineContent(doc.body(), builder, linkColor)

        /*Log.d("ParserAnnotations", "Final Annotations: ${builder.toAnnotatedString().getStringAnnotations(0, builder.length)}")
        Log.d("ParseAnnotations", "Content: ${builder.toAnnotatedString()}")*/
        return builder.toAnnotatedString()
    }

    private fun processInlineContent(
        element: Element,
        builder: AnnotatedString.Builder,
        linkColor: Color
    ) {
        element.childNodes().forEach { node ->
            when (node) {
                is TextNode -> {
                    builder.append(node.text())
                }
                is Element -> {
                    if ((node.tagName() == "div" && (node.hasClass("b-spoiler")
                                || node.hasClass("b-spoiler_block")
                                || node.hasClass("b-replies")
                                || node.hasClass("b-video") || node.hasClass("b-quote"))
                                || node.tagName() == "a" && node.hasClass("b-image")
                            )
                        ) { return@forEach }

                    if (node.tagName().lowercase() == "br") {
                        builder.append("\n")
                        return@forEach
                    }

                    val spanStyle = getSpanStyleForElement(node, linkColor)

                    val entityData = getEntityDataForElement(node)

                    val annotationTag = when {
                        entityData != null -> entityData.type.name
                        node.hasClass("b-mention") -> EntityType.COMMENT.name
                        node.tagName() == "a" -> "URL_LINK"
                        else -> null
                    }

                    val annotationValue = when {
                        entityData != null -> entityData.id
                        node.hasClass("b-mention") -> {
                            node.attr("href").substringAfter("/comments/")
                        }
                        node.tagName() == "a" -> node.attr("href").takeIf { it.startsWith("http") }
                            ?: "${BuildConfig.BASE_URL}/${node.attr("href")}"
                        else -> null
                    }

                    val hasAnnotation = annotationTag != null && annotationValue != null

                    if (hasAnnotation) {
                        Log.d("Annotation", "Push - Tag: $annotationTag, Value: $annotationValue")
                        builder.pushStringAnnotation(tag = annotationTag, annotation = annotationValue)
                    }

                    builder.pushStyle(spanStyle)

                    if (node.tagName() == "a" && node.hasClass("b-link") && node.hasAttr("data-attrs")) {
                        val hasNameSpans = node.select("span.name-en, span.name-ru").isNotEmpty()

                        if (hasNameSpans) {
                            val json = JSONObject(node.attr("data-attrs"))
                            val display = json.optString("russian").ifBlank { json.optString("name") }
                            builder.append(display)
                        } else {
                            processInlineContent(node, builder, linkColor)
                        }
                    } else {
                        processInlineContent(node, builder, linkColor)
                    }

                    builder.pop()

                    if (hasAnnotation) {
                        Log.d("Annotation", "Pop - Tag: $annotationTag, Value: $annotationValue")
                        builder.pop()
                    }
                }
            }
        }
    }

    private fun getSpanStyleForElement(element: Element, linkColor: Color): SpanStyle {
        return when (element.tagName()) {
            "b", "strong" -> SpanStyle(fontWeight = FontWeight.Bold)
            "i", "em" -> SpanStyle(fontStyle = FontStyle.Italic)
            "u" -> SpanStyle(textDecoration = TextDecoration.Underline)
            "strike", "del" -> SpanStyle(textDecoration = TextDecoration.LineThrough)
            "a" -> SpanStyle(color = linkColor)
            else -> SpanStyle()
        }
    }

    private fun getEntityDataForElement(element: Element): EntityData? {
        if (element.tagName() != "a" || !element.hasClass("b-link") || !element.hasAttr("data-attrs")) {
            return null
        }

        try {
            val dataAttrsJson = element.attr("data-attrs")
            val jsonObject = JSONObject(dataAttrsJson)
            Log.d("DataAttrs", "Parsed data-attrs JSON: $jsonObject")

            val id = jsonObject.optString("id", "unknown")
            val typeStr = jsonObject.optString("type", "unknown")

            val entityType = EntityType.valueOf(typeStr.uppercase())

            return EntityData(id, entityType)
        } catch (e: Exception) {
            Log.e("ParseError", "Failed to parse data-attrs JSON: ${e.message}")
            return null
        }
    }

    fun getCommentStringAnnotations(
        annotatedList: List<DescriptionElement>
    ): Map<CommentType, List<String>> {
        val result = mutableMapOf<CommentType, MutableList<String>>()

        annotatedList.forEach { element ->
            if(element is DescriptionElement.Text) {
                val annotatedString = element.annotatedString

                val commentAnnotations = annotatedString.getStringAnnotations(
                    tag = EntityType.COMMENT.name,
                    start = 0,
                    end = annotatedString.length
                )
                Log.d("CommentAnnotations", "Found annotations: $commentAnnotations")

                val text = annotatedString.text
                when {
                    text.startsWith("Reply:") || text.startsWith("Replies:") -> {
                        commentAnnotations.forEach { annotation ->
                            result.getOrPut(CommentType.REPLIES) { mutableListOf() }.add(annotation.item)
                        }
                    }
                    else -> {
                        commentAnnotations.forEach { annotation ->
                            result.getOrPut(CommentType.REPLIED_TO) { mutableListOf() }.add(annotation.item)
                        }
                    }
                }
            }
        }

        return result
    }

    private fun extractSizeFromUrl(url: String): Float? {
        val regex = """name=(\d+)x(\d+)""".toRegex()
        val match = regex.find(url)

        return match?.let {
            val width = it.groupValues[1].toFloatOrNull()
            val height = it.groupValues[2].toFloatOrNull()

            if (width != null && height != null && height > 0) {
                width / height
            } else null
        }
    }

    private fun extractSizeFromNode(node: Element): Float? {
        val img = node.selectFirst("img") ?: return null

        val dataWidth = img.attr("data-width").toFloatOrNull()
        val dataHeight = img.attr("data-height").toFloatOrNull()

        if (dataWidth != null && dataHeight != null && dataHeight > 0) {
            return dataWidth / dataHeight
        }

        val width = img.attr("width").toFloatOrNull()
        val height = img.attr("height").toFloatOrNull()

        return if (width != null && height != null && height > 0) {
            width / height
        } else null
    }
}