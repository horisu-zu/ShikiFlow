package com.example.shikiflow.utils.parser

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import com.example.shikiflow.domain.model.comment.DescriptionElement
import com.example.shikiflow.domain.model.comment.EntityData
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.TextNode

interface HTMLDialect {
    fun getNodeType(element: Element): NodeType?

    fun getSpoiler(node: Element): Pair<String?, Element?>

    fun getVideo(node: Element): DescriptionElement.Video

    fun getImage(node: Element): DescriptionElement.Image

    fun getQuote(node: Element): DescriptionElement.Quote

    fun getReply(node: Element, linkColor: Color): DescriptionElement.Text

    fun getInnerTextNode(node: TextNode, currentText: String): String

    fun getEntityDataForElement(element: Element): EntityData?

    fun appendNewLine(node: Element, builder: AnnotatedString.Builder)

    fun resolveAnnotation(node: Element): Pair<String, String>?

    fun getLinkLabel(node: Element, builder: AnnotatedString.Builder, linkColor: Color): Boolean
}

enum class NodeType {
    SPOILER,
    QUOTE,
    REPLY,
    IMAGE,
    VIDEO
}