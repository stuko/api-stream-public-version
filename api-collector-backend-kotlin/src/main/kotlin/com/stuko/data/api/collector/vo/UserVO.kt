package com.stuko.data.api.collector.vo

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity(name="member")
@Table(name="member")
class UserVO{
    @Id
    var id : String = ""
        get() = field
        set(value) {
            field = value
        }
    var seqno : Int = 0
        get() = field
        set(value) {
            field = value
        }
    var password : String = ""
        get() = field
        set(value) {
            field = value
        }
    var phone_no : String = ""
        get() = field
        set(value) {
            field = value
        }
}

