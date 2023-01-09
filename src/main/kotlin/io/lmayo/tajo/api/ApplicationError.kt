package io.lmayo.tajo.api

data class ApplicationError(
    val errors: List<ApiError>,
)

data class ApiError(
    val message: String
)
