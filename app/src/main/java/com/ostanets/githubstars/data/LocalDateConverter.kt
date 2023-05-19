package com.ostanets.githubstars.data

import androidx.room.TypeConverter
import org.threeten.bp.LocalDateTime

class LocalDateConverter {

    @TypeConverter
    fun fromString(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun toString(date: LocalDateTime?): String? {
        return date?.toString()
    }
}