package com.example.shikiflow.utils.parser

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.domain.model.comment.DescriptionElement
import com.example.shikiflow.domain.model.comment.EntityData
import com.example.shikiflow.domain.model.comment.EntityType.Companion.getAnilistEntityType
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.TextNode
import org.json.JSONObject
import java.net.URI

class AnilistDialect: HTMLDialect {
    override fun getNodeType(element: Element): NodeType? {
        return when {
            element.hasClass("markdown_spoiler") -> NodeType.SPOILER
            element.tagName() == "video" || element.hasClass("youtube") -> NodeType.VIDEO
            element.tagName() == "img" -> NodeType.IMAGE
            element.tagName() == "blockquote" -> NodeType.QUOTE
            else -> null
        }
    }

    override fun getSpoiler(node: Element): Pair<String?, Element?> {
        val contentNode = node.selectFirst("span")

        return null to contentNode
    }

    override fun getVideo(node: Element): DescriptionElement.Video {
        val source = node.selectFirst("source")

        val videoUrl = source?.attr("src") ?: ""

        return DescriptionElement.Video(
            videoUrl = videoUrl,
            thumbnailUrl = null
        )
    }

    override fun getImage(node: Element): DescriptionElement.Image {
        val img = when (node.tagName()) {
            "img" -> node
            else -> node.selectFirst("img")
        }

        val aspectRatio = (16f / 9f)

        return DescriptionElement.Image(
            imageUrl = img?.attr("src"),
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

    override fun getReply(
        node: Element,
        linkColor: Color
    ): DescriptionElement.Text {
        TODO("Doesn't exist in Anilist API")
    }

    override fun getInnerTextNode(node: TextNode, currentText: String): String {
        val originalText = node.text()
        val text = originalText.replace("\n", " ")

        return if (text.isNotBlank()) {
            val textToAppend = if (currentText.isEmpty() || currentText.endsWith("\n")) {
                text.trimStart()
            } else {
                text
            }
            textToAppend
        } else ""
    }

    override fun getEntityDataForElement(element: Element): EntityData? {
        if (element.tagName() != "a" || !element.attr("href").contains(BuildConfig.ANILIST_BASE_URL)) {
            return null
        }

        val href = element.attr("href")
        val path = URI(href).path.trim('/')
        val parts = path.split('/')

        val type = parts[0]
        val id = parts[1]

        val entityType = type.getAnilistEntityType()

        return entityType?.let { type ->
            EntityData(
                id = id,
                type = type
            )
        }
    }

    override fun appendNewLine(
        node: Element,
        builder: AnnotatedString.Builder
    ) {
        if (node.tagName().lowercase() == "br") {
            val currentText = builder.toAnnotatedString().text
            if (!currentText.endsWith("\n\n")) {
                builder.append("\n")
            }
        }
    }

    override fun resolveAnnotation(node: Element): Pair<String, String>? {
        val entity = getEntityDataForElement(node)
        return when {
            entity != null -> entity.type.name to entity.id
            node.tagName() == "a" ->
                "URL_LINK" to (
                        node.attr("href").takeIf {
                            it.startsWith("http")
                        } ?: "${BuildConfig.ANILIST_BASE_URL}/${node.attr("href")}"
                )
            else -> null
        }
    }

    override fun getLinkLabel(
        node: Element,
        builder: AnnotatedString.Builder,
        linkColor: Color
    ): Boolean {
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
}