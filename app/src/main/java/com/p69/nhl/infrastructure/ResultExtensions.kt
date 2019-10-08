package com.p69.nhl.infrastructure

inline fun <T, V>Result<T>.flatMap(crossinline transform: (T)->Result<V>): Result<V> = fold(
  onSuccess = { transform(it) },
  onFailure = { Result.failure(it) }
)