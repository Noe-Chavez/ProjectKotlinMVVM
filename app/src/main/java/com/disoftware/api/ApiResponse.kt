package com.disoftware.api

import retrofit2.Response
import java.lang.NumberFormatException
import java.util.regex.Matcher
import java.util.regex.Pattern

sealed class ApiResponse<T> {
    companion object {
        fun<T> create(error: Throwable): ApiErrorResponse<T> {
            // Cuando halla un error...
            return ApiErrorResponse(error.message?: "unknown error")
        }

        // En caso de que llege una respuesta.
        fun<T> create(response: Response<T>): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body:T? = response.body()
                if (body == null || response.code() == 204) {
                    ApiEnptyResponse()
                } else {
                    ApiSucessResponse(body = body, linksHeaders = response.headers()?.get("link"))
                }
            } else {
                val msg: String? = response.errorBody()?.string()
                val errorMsg: String = if (msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                }
                ApiErrorResponse(errorMsg?: "unknow error")
            }
        }
    }
}

// Para el error 104, cuando se procesa una petición, pero no devuelve ningún body.
class ApiEnptyResponse<T>: ApiResponse<T>()

data class ApiSucessResponse<T> (
    val body: T,
    val links: Map<String, String>
): ApiResponse<T> () {
    constructor(body: T, linksHeaders: String?): this (
        body = body,
        links = linksHeaders?.extractLinks()?: emptyMap()
    )

    // Recuperar la siguinete página
    val nextPage: Int? by lazy (LazyThreadSafetyMode.NONE) {
        links[NEXT_LINK]?.let {
            next ->
            val matcher: Matcher = PAGE_PATTERN.matcher(next)
            if (!matcher.find() || matcher.groupCount() != 1) { //nexy sólo llega un unico valor por lo que este valor es un error.
                null
            } else {
              try {
                  Integer.parseInt(matcher.group(1))
              }  catch (ex: NumberFormatException) {
                  null
              }
            }
        }
    }

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

// Clase para los errores.
data class ApiErrorResponse<T> (
    val errorMessage: String
): ApiResponse<T>()