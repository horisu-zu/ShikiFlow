package com.example.shikiflow.utils.parser

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.comment.DescriptionElement
import com.example.shikiflow.domain.model.comment.EntityData
import com.example.shikiflow.domain.model.comment.EntityType
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.TextNode
import org.json.JSONObject

object Parser {
    fun parseDescriptionHtml(
        html: String,
        authType: AuthType,
        linkColor: Color = Color.Blue
    ): List<DescriptionElement> {
        if (html.isBlank()) {
            return emptyList()
        }

        val doc = Ksoup.parse(html)
        val contentElement = doc.selectFirst("div.b-text_with_paragraphs") ?: doc.body()

        return when(authType) {
            AuthType.SHIKIMORI -> parseElementContent(contentElement, linkColor)
            AuthType.ANILIST -> parseElementContent(contentElement, linkColor)
        }
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

        return builder.toAnnotatedString()
    }

    private fun processInlineContent(
        element: Element,
        builder: AnnotatedString.Builder,
        linkColor: Color
    ) {
        element.childNodes().forEach { node ->
            Log.d("HTMLParser", "Node: $node")
            when (node) {
                is TextNode -> {
                    val originalText = node.text()
                    val text = originalText.replace("\n", " ")

                    if (text.isNotBlank()) {
                        val currentText = builder.toAnnotatedString().text
                        val textToAppend = if (currentText.isEmpty() || currentText.endsWith("\n")) {
                            text.trimStart()
                        } else {
                            text
                        }
                        builder.append(textToAppend)
                    }
                }
                is Element -> {
                    if ((node.tagName() == "div" && (node.hasClass("b-spoiler")
                                || node.hasClass("b-spoiler_block")
                                || node.hasClass("b-replies")
                                || node.hasClass("b-video") || node.hasClass("b-quote"))
                                || node.tagName() == "a" && node.hasClass("b-image")
                                )
                    ) { return@forEach }

                    if (node.tagName() == "p") {
                        val currentText = builder.toAnnotatedString().text
                        if (currentText.isNotEmpty() && !currentText.endsWith("\n")) {
                            builder.append("\n")
                        }
                        processInlineContent(node, builder, linkColor)
                        return@forEach
                    }

                    if (node.tagName().lowercase() == "br") {
                        val currentText = builder.toAnnotatedString().text
                        if (!currentText.endsWith("\n\n")) {
                            builder.append("\n")
                        }
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
                            ?: "${BuildConfig.SHIKI_BASE_URL}/${node.attr("href")}"
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