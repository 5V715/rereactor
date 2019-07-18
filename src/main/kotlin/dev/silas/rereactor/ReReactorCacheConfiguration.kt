package dev.silas.rereactor

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.cache.configuration.MutableConfiguration

@ConditionalOnBean(CacheManager::class)
@Configuration
class ReReactorCacheConfiguration {

    @Qualifier(RE_REACTOR_CACHE_DEFINITION)
    @Bean
    fun configureReReactorCache() = JCacheManagerCustomizer { cacheManager ->
        cacheManager.createCache(RE_REACTOR_CACHE_NAME, MutableConfiguration<String, Any>())
    }

    companion object {
        const val RE_REACTOR_CACHE_NAME = "RE_REACTOR_CACHE"
        const val RE_REACTOR_CACHE_DEFINITION = "RE_REACTOR_CACHE"
    }

}