package com.example.hopla.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.example.hopla.R

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
//---------------Custom font for "headers"------------------
val customFontFamilyHeader = FontFamily(
    Font(R.font.headers, FontWeight.Normal)
)

// Custom text style for headers
val headerTextStyle = TextStyle(
    fontFamily = customFontFamilyHeader,
    fontSize = 48.sp,
    fontStyle = FontStyle.Italic,
    fontWeight = FontWeight.Bold
)

val headerTextStyleSmall = TextStyle(
    fontFamily = customFontFamilyHeader,
    fontSize = 32.sp,
    fontStyle = FontStyle.Italic,
    fontWeight = FontWeight.Bold
)

//---------------Custom font for "under headers"------------------
val customFontFamilyUnderHeader = FontFamily(
    Font(R.font.underheaders, FontWeight.Normal)
)

val underheaderTextStyle = TextStyle(
    fontFamily = customFontFamilyUnderHeader,
    fontSize = 22.sp,
    fontWeight = FontWeight.Medium,
)

//---------------Custom text style for "normal" text------------------
val customFontFamilyText = FontFamily(
    Font(R.font.text, FontWeight.Normal)
)

// Custom text style for buttons
val generalTextStyle = TextStyle(
    fontFamily = customFontFamilyText,
    fontSize = 14.sp,
    fontWeight = FontWeight.Normal
)

// Custom text style for buttons
val generalTextStyleDialog = TextStyle(
    fontFamily = customFontFamilyText,
    fontSize = 15.sp,
    fontWeight = FontWeight.Normal
)

val generalTextStyleRed = TextStyle(
    fontFamily = customFontFamilyText,
    fontSize = 12.sp,
    fontWeight = FontWeight.Normal,
    color = HeartColor
)

val generalTextStyleBold = TextStyle(
    fontFamily = customFontFamilyText,
    fontSize = 14.sp,
    fontWeight = FontWeight.Bold
)

// Custom text style for buttons
val buttonTextStyle = TextStyle(
    fontFamily = customFontFamilyText,
    fontSize = 16.sp,
    fontWeight = FontWeight.Normal
)

// Custom text style for TextField labels
val textFieldLabelTextStyle = TextStyle(
    fontFamily = customFontFamilyText,
    fontSize = 14.sp,
    fontWeight = FontWeight.Normal
)

// Custom text style for underlined text (small)
val underlinedTextStyleSmall = TextStyle(
    fontFamily = customFontFamilyText,
    fontSize = 18.sp,
    fontWeight = FontWeight.Normal,
    textDecoration = TextDecoration.Underline
)

// Custom text style for buttons
val dropdownMenuTextStyle = TextStyle(
    fontFamily = customFontFamilyText,
    fontSize = 12.sp,
    fontWeight = FontWeight.ExtraBold,
)