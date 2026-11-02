package com.clicks.app.feature.auth.ui

import androidx.annotation.StringRes
import com.clicks.app.R

data class CountryCode(
    @param:StringRes
    val nameRes: Int,
    val code: String,       // e.g. "+254"
    val isoCode: String,    // e.g. "KE"
    val flag: String
)

val commonCountryCodes = listOf(
    CountryCode(R.string.txt_country_kenya, "+254", "KE", "🇰🇪"),
    CountryCode(R.string.txt_country_united_states, "+1", "US", "🇺🇸"),
    CountryCode(R.string.txt_country_united_kingdom, "+44", "GB", "🇬🇧"),
    CountryCode(R.string.txt_country_tanzania, "+255", "TZ", "🇹🇿"),
    CountryCode(R.string.txt_country_uganda, "+256", "UG", "🇺🇬"),
    CountryCode(R.string.txt_country_rwanda, "+250", "RW", "🇷🇼"),
    CountryCode(R.string.txt_country_nigeria, "+234", "NG", "🇳🇬"),
    CountryCode(R.string.txt_country_south_africa, "+27", "ZA", "🇿🇦"),
    CountryCode(R.string.txt_country_canada, "+1", "CA", "🇨🇦"),
    CountryCode(R.string.txt_country_india, "+91", "IN", "🇮🇳")
)
