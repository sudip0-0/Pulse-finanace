package com.pulsefinance.data.local.database

import androidx.room.TypeConverter
import com.pulsefinance.data.local.entity.KeywordMatchType
import com.pulsefinance.data.local.entity.RecurringFrequency
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth

class PulseTypeConverters {
    @TypeConverter
    fun localDateToString(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun stringToLocalDate(value: String?): LocalDate? = value?.let(LocalDate::parse)

    @TypeConverter
    fun instantToString(value: Instant?): String? = value?.toString()

    @TypeConverter
    fun stringToInstant(value: String?): Instant? = value?.let(Instant::parse)

    @TypeConverter
    fun yearMonthToString(value: YearMonth?): String? = value?.toString()

    @TypeConverter
    fun stringToYearMonth(value: String?): YearMonth? = value?.let(YearMonth::parse)

    @TypeConverter
    fun keywordMatchTypeToString(value: KeywordMatchType?): String? = value?.storageValue

    @TypeConverter
    fun stringToKeywordMatchType(value: String?): KeywordMatchType? {
        return value?.let { stored ->
            KeywordMatchType.entries.first { it.storageValue == stored }
        }
    }

    @TypeConverter
    fun recurringFrequencyToString(value: RecurringFrequency?): String? = value?.storageValue

    @TypeConverter
    fun stringToRecurringFrequency(value: String?): RecurringFrequency? {
        return value?.let { stored ->
            RecurringFrequency.entries.first { it.storageValue == stored }
        }
    }
}
