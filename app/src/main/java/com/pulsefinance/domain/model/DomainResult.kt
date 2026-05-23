package com.pulsefinance.domain.model

sealed interface DomainResult<out T> {
    data class Success<T>(val value: T) : DomainResult<T>
    data class Failure(val error: DomainError) : DomainResult<Nothing>
}

sealed interface DomainError {
    val message: String

    data class Validation(override val message: String) : DomainError
    data class NotFound(override val message: String) : DomainError
    data class Repository(override val message: String, val cause: Throwable? = null) : DomainError
}
