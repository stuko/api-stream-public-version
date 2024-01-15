package com.stuko.data.api.collector.dao

import com.stuko.data.api.collector.vo.UserVO
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<UserVO, String> {

}