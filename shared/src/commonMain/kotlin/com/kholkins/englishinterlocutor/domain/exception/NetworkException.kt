package com.kholkins.englishinterlocutor.domain.exception

sealed class NetworkException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause) {

    abstract val userMessage: String

    /**
     * Ошибка подключения к серверу.
     * Пользователь видит: "Не удалось подключиться к серверу"
     */
    class ConnectionException(
        cause: Throwable? = null,
    ) : NetworkException(
        message = "Connection failed",
        cause = cause,
    ) {
        override val userMessage: String = "Не удалось подключиться к серверу. Проверьте интернет-соединение"
    }

    /**
     * Таймаут запроса.
     * Пользователь видит: "Сервер не отвечает"
     */
    class TimeoutException(
        cause: Throwable? = null,
    ) : NetworkException(
        message = "Request timed out",
        cause = cause,
    ) {
        override val userMessage: String = "Сервер не отвечает. Попробуйте позже"
    }

    /**
     * Ошибка сервера (5xx).
     * Пользователь видит: "Сервер временно недоступен"
     */
    class ServerException(
        val statusCode: Int,
        cause: Throwable? = null,
    ) : NetworkException(
        message = "Server error: $statusCode",
        cause = cause,
    ) {
        override val userMessage: String = when (statusCode) {
            502, 503, 504 -> "Сервер временно недоступен. Попробуйте позже"
            else -> "Внутренняя ошибка сервера. Попробуйте позже"
        }
    }

    /**
     * Ошибка клиента (4xx).
     * Пользователь видит: "Запрос не удалось выполнить"
     */
    class ClientException(
        val statusCode: Int,
        cause: Throwable? = null,
    ) : NetworkException(
        message = "Client error: $statusCode",
        cause = cause,
    ) {
        override val userMessage: String = "Не удалось выполнить запрос. Попробуйте ещё раз"
    }

    /**
     * Ошибка перевода.
     * Пользователь видит: "Не удалось перевести текст"
     */
    class TranslationException(
        cause: Throwable? = null,
    ) : NetworkException(
        message = "Translation failed",
        cause = cause,
    ) {
        override val userMessage: String = "Не удалось перевести текст. Попробуйте позже"
    }

    /**
     * Ошибка AI ответа.
     * Пользователь видит: "Не удалось получить ответ"
     */
    class AiResponseException(
        cause: Throwable? = null,
    ) : NetworkException(
        message = "AI response failed",
        cause = cause,
    ) {
        override val userMessage: String = "Не удалось получить ответ от учителя. Попробуйте позже"
    }

    /**
     * Ошибка неизвестного типа.
     * Пользователь видит: "Что-то пошло не так"
     */
    class UnknownException(
        cause: Throwable? = null,
    ) : NetworkException(
        message = "Unknown error",
        cause = cause,
    ) {
        override val userMessage: String = "Что-то пошло не так. Попробуйте ещё раз"
    }
}

fun Throwable.toNetworkException(): NetworkException {
    val message = message?.lowercase() ?: ""

    return when {
        message.contains("timeout") || message.contains("timed out") ||
                message.contains("timedout") -> NetworkException.TimeoutException(this)

        message.contains("connection") || message.contains("connect") ||
                message.contains("unreachable") || message.contains("no connection") ||
                message.contains("unknown host") -> NetworkException.ConnectionException(this)

        message.contains("ssl") || message.contains("tls") || message.contains("certificate") ||
                message.contains("handshake") -> NetworkException.ConnectionException(this)

        else -> {
            val statusCode = extractHttpStatusCode(message)

            when {
                statusCode != null && statusCode in 500..599 -> NetworkException.ServerException(statusCode, this)
                statusCode != null && statusCode in 400..499 -> NetworkException.ClientException(statusCode, this)
                else -> NetworkException.UnknownException(this)
            }
        }
    }
}

private fun extractHttpStatusCode(message: String): Int? {
    val parts = message.split(' ')
    for (i in parts.indices) {
        if (parts[i] == "http" && i + 1 < parts.size) {
            return parts[i + 1].toIntOrNull()
        }
        if (parts[i].length == 3 && parts[i].all { it.isDigit() }) {
            val code = parts[i].toInt()
            if (code in 400..599) return code
        }
    }
    return null
}

fun Throwable.isNetworkError(): Boolean = this is NetworkException
