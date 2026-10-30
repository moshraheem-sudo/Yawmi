package com.example.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat
import java.util.Locale

object CurrencyFormatterUtils {
    private val decimalFormat = DecimalFormat("#,###")

    fun format(amount: Double): String {
        return decimalFormat.format(amount)
    }

    fun format(amount: Long): String {
        return decimalFormat.format(amount)
    }

    /**
     * Converts raw numbers into readable Arabic denomination words,
     * e.g., 800000 -> "800 ألف د.ع", 1500000 -> "1.5 مليون د.ع", 2750000 -> "2.75 مليون د.ع"
     * Helping users immediately understand how many zeros they typed!
     */
    fun toArabicDenomination(amount: Long): String {
        if (amount <= 0) return "0 د.ع"
        return when {
            amount >= 1_000_000_000 -> {
                val b = amount / 1_000_000_000.0
                String.format(Locale.ENGLISH, "%.2f", b).trimEnd('0').trimEnd('.') + " مليار د.ع"
            }
            amount >= 1_000_000 -> {
                val m = amount / 1_000_000.0
                String.format(Locale.ENGLISH, "%.2f", m).trimEnd('0').trimEnd('.') + " مليون د.ع"
            }
            amount >= 1_000 -> {
                val k = amount / 1_000.0
                if (amount % 1_000 == 0L) {
                    "${amount / 1_000} ألف د.ع"
                } else {
                    String.format(Locale.ENGLISH, "%.1f", k).trimEnd('0').trimEnd('.') + " ألف د.ع"
                }
            }
            else -> "$amount د.ع"
        }
    }
}

/**
 * VisualTransformation that dynamically displays commas every 3 digits (thousands separators)
 * as the user types numbers in any TextField, without altering the underlying raw numeric state.
 */
class ThousandsSeparatorVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val cleanDigits = originalText.filter { it.isDigit() }
        if (cleanDigits.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val out = StringBuilder()
        val remainder = cleanDigits.length % 3

        for (i in cleanDigits.indices) {
            if (i > 0 && (i % 3 == remainder || (remainder == 0 && i % 3 == 0))) {
                out.append(',')
            }
            out.append(cleanDigits[i])
        }

        val transformedString = out.toString()

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                val digitsBefore = originalText.take(offset).count { it.isDigit() }
                if (digitsBefore == 0) return 0
                var commaCount = 0
                var currentDigits = 0
                for (ch in transformedString) {
                    if (ch.isDigit()) {
                        currentDigits++
                        if (currentDigits == digitsBefore) {
                            return (currentDigits + commaCount).coerceIn(0, transformedString.length)
                        }
                    } else if (ch == ',') {
                        commaCount++
                    }
                }
                return transformedString.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                val safeOffset = offset.coerceIn(0, transformedString.length)
                val digitsBefore = transformedString.take(safeOffset).count { it.isDigit() }
                return digitsBefore.coerceIn(0, originalText.length)
            }
        }

        return TransformedText(AnnotatedString(transformedString), offsetMapping)
    }
}
