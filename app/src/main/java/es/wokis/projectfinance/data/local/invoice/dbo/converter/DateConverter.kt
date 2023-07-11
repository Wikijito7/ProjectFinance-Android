package es.wokis.projectfinance.data.local.invoice.dbo.converter

import androidx.room.TypeConverter
import java.util.Date

class DateConverter {
    @TypeConverter
    fun toDate(date: Long): Date = Date(date)

    @TypeConverter
    fun fromDate(date: Date): Long = date.time
}