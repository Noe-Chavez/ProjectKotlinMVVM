package com.disoftware.api

import java.util.regex.Matcher
import java.util.regex.Pattern

sealed class ApiResponse<T> {

}

data class ApiSucessResponse<T> (
    val body: T,
    val links: Map<String, String>
): ApiResponse<T> () {
    constructor(body: T, linksHeaders: String?): this(
        body = body,
        links = linksHeaders?.extractLinks()?: emptyMap()
    )

    companion object {
        private val LINK_PATTERN = Pattern.compile("<([^>]*)>[\\s]*;[\\s]*rel=\"([a-zA-Z0-9]+)\"")
        private val PAGE_PATTERN = Pattern.compile("\\bpage=(\\d+)")
        private const val NEXT_LINK = "next"

        private fun String.extractLinks(): Map<String, String> {
            val links: MutableMap<String, String> = mutableMapOf<String, String>()
            val matcher: Matcher = LINK_PATTERN.matcher(this)
            while (matcher.find()) {
                val count: Int = matcher.groupCount()
                if (count == 2)
                    links[matcher.group(2)] = matcher.group(1)
            }
            return links
        }
    }

}