package dev.silas.rereactor

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import javax.cache.CacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(CacheManager::class)
class CacheWrapperConfiguration {

    @Bean
    fun createWrapper(cacheManager : CacheManager) = CacheWrapper(cacheManager)

}