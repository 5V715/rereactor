package dev.silas.rereactor

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.create
import java.util.*
import java.util.function.Supplier
import javax.cache.CacheManager

class CacheWrapper(private val cacheManager: CacheManager) {

    fun <T> wrap(keyBuilder: Supplier<String>, result: Mono<T>): Mono<T> {
        return create { sink ->
            val cache = cacheManager.getCache<String, T>(ReReactorCacheConfiguration.RE_REACTOR_CACHE_NAME)
            val key = keyBuilder.get()
            val wrapper = Optional.ofNullable<T>(cache.get(key))
            when {
                wrapper.isPresent -> {
                    sink.success(wrapper.get())
                }
                else -> {
                    result.subscribe({ value ->
                        sink.success(value)
                        cache.put(key, value)

                    }, sink::error)
                }
            }
        }
    }


    fun <T> wrap(keyBuilder: Supplier<String>, result: Flux<T>): Flux<T> {
        return Flux.create { sink ->
            val cache = cacheManager.getCache<String, List<T>>(ReReactorCacheConfiguration.RE_REACTOR_CACHE_NAME)
            val key = keyBuilder.get()
            val cacheResponse = Optional.ofNullable(cache.get(key))
            when {
                cacheResponse.isPresent -> {
                    cacheResponse.get().forEach { cacheValue ->
                        sink.next(cacheValue)
                    }
                    sink.complete()
                }
                else -> {
                    val values = mutableListOf<T>()
                    result.subscribe({ value ->
                        sink.next(value)
                        values.add(value)
                    }, sink::error, {
                        sink.complete()
                        cache.put(key, values)
                    })
                }
            }
        }
    }

}