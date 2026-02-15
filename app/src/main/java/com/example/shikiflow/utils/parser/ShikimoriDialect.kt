package com.example.shikiflow.utils.parser

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.domain.model.comment.DescriptionElement
import com.example.shikiflow.domain.model.comment.EntityData
import com.example.shikiflow.domain.model.comment.EntityType
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.TextNode
import org.json.JSONObject

class ShikimoriDialect: HTMLDialect {
    override fun getNodeType(element: Element): NodeType? {
        return when {
            element.hasClass("b-spoiler") || element.hasClass("b-spoiler_block") -> NodeType.SPOILER
            element.hasClass("b-video") -> NodeType.VIDEO
            element.hasClass("b-image")  -> NodeType.IMAGE
            element.hasClass("b-quote") || element.tagName() == "blockquote" -> NodeType.QUOTE
            element.hasClass("b-replies") -> NodeType.REPLY
            else -> null
        }
    }

    override fun getSpoiler(node: Element): Pair<String?, Element?> {
        val label = node.selectFirst("label")?.text()
            ?: node.selectFirst("span")?.text()

        val contentDiv = node.selectFirst(".content .inner")
            ?: node.selectFirst(".content")
            ?: node.selectFirst("> div")

        return label to contentDiv
    }

    override fun getVideo(node: Element): DescriptionElement.Video {
        val linkElement = node.selectFirst("a.marker")
            ?: node.selectFirst("a.video-link")
        val imgElement = node.selectFirst("img")

        val videoUrl = linkElement?.attr("href")
            ?: linkElement?.attr("data-video")
            ?: ""
        val thumbnailUrl = imgElement?.attr("src")

        return DescriptionElement.Video(
            videoUrl = videoUrl,
            thumbnailUrl = thumbnailUrl
        )
    }

    override fun getImage(node: Element): DescriptionElement.Image {
        val imageUrl = when (node.tagName()) {
            "a" -> node.attr("href")
            "span" -> {
                node.selectFirst("img")?.attr("src") ?: ""
            }
            else -> ""
        }

        val aspectRatio = extractSizeFromUrl(imageUrl)
            ?: extractSizeFromNode(node)
            ?: (16f / 9f)

        return DescriptionElement.Image(
            imageUrl = imageUrl,
            aspectRatio = aspectRatio
        )
    }

    override fun getQuote(node: Element): DescriptionElement.Quote {
        val senderAvatarUrl = node.selectFirst("img")
            ?.attr("srcset") ?: node.selectFirst("img")?.attr("src")
        val senderNickname = node.selectFirst("span")?.text()
        val content = if(!node.selectFirst(".quote-content")?.text().isNullOrEmpty()) {
            node.selectFirst(".quote-content")?.text() ?: ""
        } else { node.text() }

        return DescriptionElement.Quote(
            senderAvatarUrl,
            senderNickname,
            content
        )
    }

    override fun getReply(node: Element, linkColor: Color): DescriptionElement.Text {
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

        return DescriptionElement.Text(annotatedReplies)
    }

    override fun getInnerTextNode(node: TextNode, currentText: String): String {
        return node.text()
    }

    override fun getEntityDataForElement(element: Element): EntityData? {
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

    override fun appendNewLine(node: Element, builder: AnnotatedString.Builder) {
        if (node.tagName().lowercase() == "br") {
            builder.append("\n")
        }
    }

    override fun resolveAnnotation(node: Element): Pair<String, String>? {
        val entity = getEntityDataForElement(node)

        return when {
            entity != null -> entity.type.name to entity.id
            node.hasClass("b-mention") -> EntityType.COMMENT.name to node.attr("href")
                .substringAfter("/comments/")
            node.tagName() == "a" ->
                "URL_LINK" to (
                        node.attr("href").takeIf {
                            it.startsWith("http")
                        } ?: "${BuildConfig.SHIKI_BASE_URL}/${node.attr("href")}"
                )
            else -> null
        }
    }

    override fun getLinkLabel(node: Element, builder: AnnotatedString.Builder, linkColor: Color): Boolean {
        if(node.tagName() == "a" &&
            node.hasClass("b-link") &&
            node.hasAttr("data-attrs")
        ) {
            val hasNameSpans = node.select("span.name-en, span.name-ru").isNotEmpty()
            if (hasNameSpans) {
                val json = JSONObject(node.attr("data-attrs"))
                val display = json.optString("russian")
                    .ifBlank { json.optString("name") }
                builder.append(display)
                return true
            }
        }

        return false
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