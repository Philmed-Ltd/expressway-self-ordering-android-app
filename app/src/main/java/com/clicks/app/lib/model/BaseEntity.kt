package com.clicks.app.lib.model

import androidx.room.PrimaryKey

abstract class BaseEntity {
    @PrimaryKey
    open var id: Long = 1

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BaseEntity
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
