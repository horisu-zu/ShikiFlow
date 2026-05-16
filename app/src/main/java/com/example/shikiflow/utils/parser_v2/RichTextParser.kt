package com.example.shikiflow.utils.parser_v2

import com.fleeksoft.ksoup.Ksoup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ParserConfig(
    val enableAniListLinkBlocks: Boolean = true
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
}