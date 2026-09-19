package com.nxsha

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element

class NxshaProvider : MainAPI() {

    override var mainUrl = "https://web.nxsha.app"

    override var name = "Nxsha"

    override val supportedTypes = setOf(
        TvType.Movie,
        TvType.TvSeries
    )

    override var lang = "en"

    override suspend fun search(query: String): List<SearchResponse> {

        val url = "$mainUrl/search?q=${query.urlEncoded}"

        val document = app.get(url).document

        return document
            .select("a[href*='/movie/']")
            .mapNotNull { element ->
                element.toSearchResponse()
            }
    }

    private fun Element.toSearchResponse(): SearchResponse? {

        val href = attr("href")

        if (!href.contains("/movie/")) {
            return null
        }

        val url = fixUrl(href)

        val image = selectFirst("img")

        val title =
            text().trim().ifEmpty {
                image?.attr("alt") ?: return null
            }

        val poster =
            image?.attr("src")
                ?: image?.attr("data-src")

        return newMovieSearchResponse(
            title,
            url,
            TvType.Movie
        ) {
            this.posterUrl = poster
        }
    }

    override suspend fun load(url: String): LoadResponse {

        val document = app.get(url).document

        val title =
            document.selectFirst("h1")?.text()
                ?: document.title()
                ?: "Nxsha Movie"

        val poster =
            document.selectFirst("img")?.attr("src")

        return newMovieLoadResponse(
            title,
            url,
            TvType.Movie,
            url
        ) {
            this.posterUrl = poster
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {

        val id = Regex("/movie/(\\d+)")
            .find(data)
            ?.groupValues
            ?.getOrNull(1)
            ?: return false

        val embedUrl =
            "$mainUrl/embed/movie/$id"

        callback.invoke(
            ExtractorLink(
                this.name,
                "Nxsha Embed",
                embedUrl,
                mainUrl,
                Qualities.Unknown.value,
                false
            )
        )

        return true
    }
}
