package com.clicks.app.feature.auth.ui

data class CountryCode(
    val name: String,
    val code: String,       // e.g. "+254"
    val isoCode: String,    // e.g. "KE"
    val flag: String
)

val commonCountryCodes = listOf(
    CountryCode("Kenya", "+254", "KE", "🇰🇪"),
    CountryCode("United States", "+1", "US", "🇺🇸"),
    CountryCode("United Kingdom", "+44", "GB", "🇬🇧"),
    CountryCode("Tanzania", "+255", "TZ", "🇹🇿"),
    CountryCode("Uganda", "+256", "UG", "🇺🇬"),
    CountryCode("Rwanda", "+250", "RW", "🇷🇼"),
    CountryCode("Nigeria", "+234", "NG", "🇳🇬"),
    CountryCode("South Africa", "+27", "ZA", "🇿🇦"),
    CountryCode("Canada", "+1", "CA", "🇨🇦"),
    CountryCode("India", "+91", "IN", "🇮🇳")
)
