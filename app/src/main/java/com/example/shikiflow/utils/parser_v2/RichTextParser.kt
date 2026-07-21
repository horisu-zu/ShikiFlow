package com.example.shikiflow.utils.parser_v2

import com.example.shikiflow.domain.model.comment.CommentType
import com.fleeksoft.ksoup.Ksoup

data class ParserConfig(
    val enableAniListLinkBlocks: Boolean = false
)

//Heavily influenced (90-95% of the code is copy-pasted) by AniSync HTML Parser
object RichTextParser {
    fun parse(
        html: String,
        config: ParserConfig = ParserConfig()
    ): ParsedRichText {
        if (html.isBlank()) return ParsedRichText(emptyList(), emptyList())

        return try {
            val normalized = RichTextNormalizer.normalize(html)
            val document = Ksoup.parseBodyFragment(normalized)
            document.outputSettings().prettyPrint(false)

            val inlineParser = RichTextInlineParser(config)
            val htmlParser = RichTextHtmlParser(inlineParser)
            val rawResult = htmlParser.parse(document.body())

            val groupedBlocks = RichTextPostProcessor.groupInlineBlocks(rawResult.blocks)
            val imageUrls = RichTextPostProcessor.extractImageUrls(groupedBlocks)

            ParsedRichText(
                blocks = groupedBlocks,
                imageUrls = imageUrls,
                warnings = rawResult.warnings
            )
        } catch (e: Exception) {
            ParsedRichText(
                blocks = listOf(
                    RichTextBlock.Text(
                        inlines = listOf(RichTextInline.Text(html))
                    )
                ),
                imageUrls = emptyList(),
                warnings = listOf(ParseWarning("Parse failed: ${e.message}", "RichTextParser.parse"))
            )
        }
    }

    fun getReplies(
        blocks: List<RichTextBlock>
    ): Map<CommentType, List<String>> {
        val tailSize = blocks.reversed().takeWhile { isLinksBlock(it) }.size
        val tailBlocks = blocks.takeLast(tailSize)
        val headBlocks = blocks.dropLast(tailSize)

        return mapOf(
            CommentType.REPLIED_TO to headBlocks.flatMap { extractCommentIds(it) },
            CommentType.REPLIES to tailBlocks.flatMap { extractCommentIds(it) }
        )
    }

    private fun isLinksBlock(block: RichTextBlock): Boolean {
        if (block !is RichTextBlock.Text) return false
        if (block.inlines.none { it is RichTextInline.Link }) return false

        return block.inlines.all { inline ->
            when (inline) {
                is RichTextInline.Link -> COMMENT_URL_REGEX.containsMatchIn(inline.url)
                is RichTextInline.Text -> inline.value.isBlank() || inline.value.all { it == ',' || it.isWhitespace() }
                is RichTextInline.LineBreak -> true
                else -> false
            }
        }
    }

    private fun extractCommentIds(block: RichTextBlock): List<String> {
        if (block !is RichTextBlock.Text) return emptyList()

        return block.inlines
            .filterIsInstance<RichTextInline.Link>()
            .mapNotNull { link -> COMMENT_URL_REGEX.find(link.url)?.groupValues?.get(1) }
    }

    private val COMMENT_URL_REGEX = Regex("""/comments/(\d+)""")
}