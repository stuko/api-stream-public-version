package com.stuko.data.api.collector.config

import com.hazelcast.config.Config
import com.hazelcast.config.MapAttributeConfig
import com.hazelcast.config.MapIndexConfig
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import com.stuko.data.api.collector.common.Log
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.session.hazelcast.HazelcastIndexedSessionRepository
import org.springframework.session.hazelcast.PrincipalNameExtractor
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession
import java.util.function.Consumer

@EnableHazelcastHttpSession
@Configuration
class HazelCastHttpSessionConfig {
    companion object : Log

    @Value("\${env.nodeList:localhost}")
    private val dxNodeList: List<String>? = null

    @Bean
    fun hazelcastInstance(): HazelcastInstance {
        dxNodeList!!.forEach(Consumer { node: String? -> logger.debug("dxNodeList= {}", node) })
        val config = Config()
        val networkConfig = config.networkConfig
        networkConfig.port = 5701
        val joinConfig = networkConfig.join
        joinConfig.multicastConfig.isEnabled = false
        joinConfig.tcpIpConfig.setMembers(dxNodeList).isEnabled = true
        val attributeConfig: MapAttributeConfig = MapAttributeConfig()
                .setName(HazelcastIndexedSessionRepository.PRINCIPAL_NAME_ATTRIBUTE)
                .setExtractor(PrincipalNameExtractor::class.java.getName())
        config.getMapConfig(HazelcastIndexedSessionRepository.DEFAULT_SESSION_MAP_NAME)
                .addMapAttributeConfig(attributeConfig).addMapIndexConfig(
                        MapIndexConfig(HazelcastIndexedSessionRepository.PRINCIPAL_NAME_ATTRIBUTE, false))
        return Hazelcast.newHazelcastInstance(config)
    }
}