package com.example.hopla.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.hopla.R

// Custom font family for the headers of the app
val customFontFamilyHeader = FontFamily(
    Font(R.font.headers, FontWeight.Normal)
)

// Custom font family for the under headers of the app
val customFontFamilyUnderHeader = FontFamily(
    Font(R.font.underheaders, FontWeight.Normal)
)

val customFontFamilyText = FontFamily(
    Font(R.font.text, FontWeight.Normal)
)

// Custom typography for the app
val customTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

// Custom text style for headers
val headerTextStyle = TextStyle(
    fontFamily = customFontFamilyHeader,
    fontWeight = FontWeight.Bold,
    fontSize = 30.sp,
    lineHeight = 36.sp,
    letterSpacing = 0.sp
)

// Custom text style for under headers
val underHeaderTextStyle = TextStyle(
    fontFamily = customFontFamilyUnderHeader,
    fontWeight = FontWeight.Medium,
    fontSize = 24.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.sp
)