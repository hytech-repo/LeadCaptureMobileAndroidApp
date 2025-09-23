package com.eva.lead.capture.domain.model


open class AppException(
    override val message: String,
    val code: Int? = null
) : Exception(message)


class WebServiceFailure {
    class NoNetworkFailure(
        msg: String = "No network connection",
    ) : AppException(msg)

    class ConnectionFailure(
        msg: String = "Server down or unreachable"
    ) : AppException(msg)

    class NetworkTimeOutFailure(
        msg: String = "Network Timeout"
    ) : AppException(msg)

    class NetworkDataFailure(
        msg: String = "Error parsing data"
    ) : AppException(msg)

    class InvalidRequestFailure(
        msg: String = "Invalid Request"
    ) : AppException(msg)

    class UnknownNetworkFailure(
        msg: String = "Unknown Network Failure"
    ) : AppException(msg)
}