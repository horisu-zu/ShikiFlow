package com.example.shikiflow.utils.parser

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.example.shikiflow.domain.model.comment.CommentType
import com.example.shikiflow.domain.model.comment.DescriptionElement
import com.example.shikiflow.domain.model.comment.EntityType
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode

class HTMLParser(private val strategy: HTMLDialect) {

    fun parseHtmlString(
        htmlString: String,
        linkColor: Color = Color.Transparent
    ): List<DescriptionElement>? {
        if (htmlString.isBlank()) {
            Log.d("HTMLParser", "String is empty")
            return null
        }

        val doc = Ksoup.parse(htmlString)
        val contentElement = doc.selectFirst("div.b-text_with_paragraphs") ?: doc.body()

        return parseElementContent(contentElement, linkColor)
    }

    private fun parseElementContent(
        contentElement: Element,
        linkColor: Color
    ): List<DescriptionElement> {
        val elements = mutableListOf<DescriptionElement>()
        val textHtmlBuffer = StringBuilder()

        processNodes(
            nodes = contentElement.childNodes(),
            elements = elements,
            htmlBuffer = textHtmlBuffer,
            linkColor = linkColor
        )

        processTextBuffer(textHtmlBuffer, elements, linkColor)

        return elements
    }

    private fun processNodes(
        nodes: List<Node>,
        elements: MutableList<DescriptionElement>,
        htmlBuffer: StringBuilder,
        linkColor: Color
    ) {
        nodes.forEach { node ->
            when(node) {
                is TextNode -> {
                    htmlBuffer.append(node.outerHtml())
                }
                is Element -> {
                    val nodeType = strategy.getNodeType(node)

                    when(nodeType) {
                        NodeType.SPOILER -> {
                            processTextBuffer(htmlBuffer, elements, linkColor)

                            val spoilerData = strategy.getSpoiler(node)
                            val spoilerContent = spoilerData.second?.let {
                                parseElementContent(it, linkColor)
                            } ?: emptyList()

                            elements.add(DescriptionElement.Spoiler(spoilerData.first, spoilerContent))
                        }
                        NodeType.QUOTE -> {
                            processTextBuffer(htmlBuffer, elements, linkColor)
                            elements.add(strategy.getQuote(node))
                        }
                        NodeType.REPLY -> {
                            processTextBuffer(htmlBuffer, elements, linkColor)
                            elements.add(strategy.getReply(node, linkColor))
                        }
                        NodeType.IMAGE -> {
                            processTextBuffer(htmlBuffer, elements, linkColor)
                            elements.add(strategy.getImage(node))
                        }
                        NodeType.VIDEO -> {
                            processTextBuffer(htmlBuffer, elements, linkColor)
                            elements.add(strategy.getVideo(node))
                        }
                        null -> {
                            //Anilist-only handling
                            if (node.tagName() == "p" || node.tagName() == "center") {
                                processNodes(node.childNodes(), elements, htmlBuffer, linkColor)

                                htmlBuffer.append("<br><br>")
                            } else {
                                htmlBuffer.append(node.outerHtml())
                            }
                        }
                    }
                }
            }
        }
    }

    fun processTextBuffer(
        htmlBuffer: StringBuilder,
        elements: MutableList<DescriptionElement>,
        linkColor: Color
    ) {
        val rawHtml = htmlBuffer.toString().trim()
        if (rawHtml.isNotBlank()) {
            val annotatedString = parseInnerHtml(rawHtml, linkColor)

            val trimmed = annotatedString.text.trimEnd()
            val finalString = if (trimmed.length != annotatedString.length) {
                annotatedString.subSequence(0, trimmed.length)
            } else {
                annotatedString
            }

            if (finalString.isNotEmpty()) {
                elements.add(DescriptionElement.Text(finalString))
            }
            htmlBuffer.clear()
        }
    }

    private fun parseInnerHtml(htmlBuffer: String, linkColor: Color): AnnotatedString {
        if (htmlBuffer.isBlank()) return AnnotatedString("")

        val doc = Ksoup.parseBodyFragment(htmlBuffer)
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
            when(node) {
                is TextNode -> {
                    val lastChar = if (builder.length > 0) {
                        builder.toAnnotatedString().text.last()
                    } else null

                    val innerText = strategy.getInnerTextNode(
                        node = node,
                        currentText = if (lastChar == '\n') "\n" else ""
                    )

                    Log.d("HTMLParser", "Inner Text: $innerText")
                    builder.append(innerText)
                }
                is Element -> {
                    Log.d("HTMLParser", "Inline Node: $node")
                    processInlineElement(node, builder, linkColor)
                }
            }
        }
    }

    private fun processInlineElement(
        node: Element,
        builder: AnnotatedString.Builder,
        linkColor: Color
    ) {
        if(strategy.getNodeType(node) != null) { return }

        strategy.appendNewLine(node, builder)

        val spanStyle = getSpanStyleForElement(node, linkColor)

        val annotation = strategy.resolveAnnotation(node)

        annotation?.let { stringAnnotation ->
            Log.d("HTMLParser", "String Annotation: $stringAnnotation")
            builder.pushStringAnnotation(stringAnnotation.first, stringAnnotation.second)
        }

        builder.pushStyle(spanStyle)

        val linkLabelHandle = strategy.getLinkLabel(node, builder, linkColor)

        if(!linkLabelHandle) { processInlineContent(node, builder, linkColor) }

        builder.pop()

        annotation?.let { builder.pop() }
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
}