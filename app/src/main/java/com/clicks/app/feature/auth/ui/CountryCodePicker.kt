package com.clicks.app.feature.auth.ui

import androidx.annotation.StringRes
import com.clicks.app.R

data class CountryCode(
    @param:StringRes
    val nameRes: Int,
    val code: String,       // e.g. "+254"
    val isoCode: String,    // e.g. "KE"
    val flag: String,
    val nationalNumberLength: Int
)

val commonCountryCodes = listOf(
    CountryCode(R.string.txt_country_kenya, "+254", "KE", "🇰🇪", 9),
    CountryCode(R.string.txt_country_united_states, "+1", "US", "🇺🇸", 10),
    CountryCode(R.string.txt_country_united_kingdom, "+44", "GB", "🇬🇧", 10),
    CountryCode(R.string.txt_country_tanzania, "+255", "TZ", "🇹🇿", 9),
    CountryCode(R.string.txt_country_uganda, "+256", "UG", "🇺🇬", 9),
    CountryCode(R.string.txt_country_rwanda, "+250", "RW", "🇷🇼", 9),
    CountryCode(R.string.txt_country_nigeria, "+234", "NG", "🇳🇬", 10),
    CountryCode(R.string.txt_country_south_africa, "+27", "ZA", "🇿🇦", 9),
    CountryCode(R.string.txt_country_canada, "+1", "CA", "🇨🇦", 10),
    CountryCode(R.string.txt_country_india, "+91", "IN", "🇮🇳", 10)
)
