package com.stuko.data.api.collector.service.impl

import com.stuko.data.api.collector.common.Log
import com.stuko.data.api.collector.dao.UserDAO
import com.stuko.data.api.collector.dao.UserRepository
import com.stuko.data.api.collector.service.UserService
import com.stuko.data.api.collector.vo.UserVO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.util.*
import javax.annotation.PostConstruct

@Service
class UserServiceImpl : UserService {

    companion object : Log
    @Autowired
    lateinit var dao: UserRepository

    override fun read(param: String): Map<String, Any> {
        TODO("Not yet implemented")
    }

    override fun create(param: UserVO): Int {
        TODO("Not yet implemented")
    }

    override fun update(param: UserVO): Int {
        TODO("Not yet implemented")
    }

    override fun delete(param: String): Int {
        TODO("Not yet implemented")
    }

    override fun login(id: String, pw: String):String? {
        var loginResult : String? = null
        logger.info("##### id from parameter : {} " , id)
        logger.info("##### pw from parameter : {} " , pw)
        dao.findById(id).ifPresent { x->
            logger.info("###### id : {} " , x.id)
            logger.info("###### password : {} " , x.password)
            logger.info("###### seqno : {} " , x.seqno)
            val seed = x.id + x.seqno + pw
            logger.info("###### seed : {} " , seed )
            val digest:MessageDigest = MessageDigest.getInstance("SHA-256")
            val result: ByteArray = digest.digest(seed.toByteArray(Charsets.UTF_8))
            logger.info("###### seed to sha-256 {} " , result )
            val inputPassword = Base64.getEncoder().encodeToString(result)
            logger.info("###### {} vs {} " , inputPassword, x.password)
            if(x.password == inputPassword){
                loginResult = x.id
            }
        }
        logger.info("##### find result is {}" , loginResult)
        return loginResult
    }

}