package com.stuko.data.api.collector.service.impl

import com.stuko.data.api.collector.dao.BatchInfoDAO
import com.stuko.data.api.collector.service.BatchInfoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BatchInfoServiceImpl : BatchInfoService{

    @Autowired
    private lateinit var batchInfoDAO: BatchInfoDAO

    override fun read(param: Map<String,Any>): Map<String,Any> {
        return batchInfoDAO.read(param)
    }

    override fun create(param: Map<String,Any>):Map<String,Any> {
        return batchInfoDAO.create(param)
    }

    override fun update(param: Map<String,Any>):Map<String,Any> {
        return batchInfoDAO.update(param)
    }

    override fun delete(param: Map<String,Any>):Map<String,Any> {
        return batchInfoDAO.delete(param)
    }

    override fun list(page: Int): List<Map<String,Any>> {
        return batchInfoDAO.list(page)
    }

}