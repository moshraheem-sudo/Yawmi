package com.example.ui.screens.notes.pdf

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import com.example.data.local.entity.NoteItem
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object NotePdfExporter {

    fun exportAndSharePdf(context: Context, note: NoteItem, language: String): Boolean {
        return try {
            val pdfDocument = PdfDocument()
            val pageWidth = 595 // A4 standard width in points
            val pageHeight = 842 // A4 standard height in points
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            val margin = 40f
            val contentWidth = pageWidth - (margin * 2)

            // Background
            val bgPaint = Paint().apply { color = Color.WHITE }
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), bgPaint)

            var currentY = margin

            // App Header Bar
            val headerPaint = Paint().apply {
                color = Color.parseColor("#7C3AED") // Purple Accent
                isAntiAlias = true
            }
            canvas.drawRoundRect(margin, currentY, margin + 80f, currentY + 24f, 12f, 12f, headerPaint)

            val headerTextPaint = Paint().apply {
                color = Color.WHITE
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText("يومي • Yawmi", margin + 40f, currentY + 16f, headerTextPaint)

            currentY += 40f

            // Note Title
            val titleText = note.title.ifBlank { if (language == "ar") "ملاحظة بدون عنوان" else "Untitled Note" }
            val titlePaint = TextPaint().apply {
                color = Color.parseColor("#0F172A") // Navy Dark
                textSize = 22f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val titleLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(titleText, 0, titleText.length, titlePaint, contentWidth.toInt())
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .build()
            } else {
                @Suppress("DEPRECATION")
                StaticLayout(titleText, titlePaint, contentWidth.toInt(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false)
            }

            canvas.save()
            canvas.translate(margin, currentY)
            titleLayout.draw(canvas)
            canvas.restore()

            currentY += titleLayout.height + 12f

            // Date & Metadata
            val sdf = SimpleDateFormat("EEEE, d MMMM yyyy - hh:mm a", if (language == "ar") Locale("ar") else Locale.ENGLISH)
            val dateStr = sdf.format(Date(note.updatedAt))

            val datePaint = Paint().apply {
                color = Color.parseColor("#64748B") // Slate Gray
                textSize = 10f
                typeface = Typeface.DEFAULT
                isAntiAlias = true
            }
            canvas.drawText(dateStr, margin, currentY, datePaint)

            currentY += 16f

            // Divider line
            val dividerPaint = Paint().apply {
                color = Color.parseColor("#E2E8F0")
                strokeWidth = 1.5f
            }
            canvas.drawLine(margin, currentY, pageWidth - margin, currentY, dividerPaint)

            currentY += 24f

            // Body Content
            val bodyText = note.content.ifBlank { if (language == "ar") "(لا يوجد محتوى)" else "(Empty note)" }
            val bodyPaint = TextPaint().apply {
                color = Color.parseColor("#1E293B")
                textSize = 14f
                typeface = Typeface.DEFAULT
                isAntiAlias = true
            }

            val bodyLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(bodyText, 0, bodyText.length, bodyPaint, contentWidth.toInt())
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(4f, 1.15f)
                    .build()
            } else {
                @Suppress("DEPRECATION")
                StaticLayout(bodyText, bodyPaint, contentWidth.toInt(), Layout.Alignment.ALIGN_NORMAL, 1.15f, 4f, false)
            }

            canvas.save()
            canvas.translate(margin, currentY)
            bodyLayout.draw(canvas)
            canvas.restore()

            // Footer
            val footerPaint = Paint().apply {
                color = Color.parseColor("#94A3B8")
                textSize = 9f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText(
                "تم الإنشاء بواسطة تطبيق يومي (Yawmi) • تصدير احترافي",
                pageWidth / 2f,
                pageHeight - 30f,
                footerPaint
            )

            pdfDocument.finishPage(page)

            // Save PDF to cache directory
            val pdfDir = File(context.cacheDir, "notes_pdf").apply { mkdirs() }
            val sanitizedTitle = note.title.filter { it.isLetterOrDigit() }.take(15)
            val fileName = "Yawmi_Note_${if (sanitizedTitle.isNotBlank()) sanitizedTitle else "${note.id}"}.pdf"
            val file = File(pdfDir, fileName)

            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDocument.close()

            // Share Intent
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, note.title)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }

            val chooser = Intent.createChooser(shareIntent, if (language == "ar") "مشاركة الملاحظة كملف PDF" else "Share Note as PDF").apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(chooser)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun shareAsFormattedText(context: Context, note: NoteItem, language: String) {
        val sdf = SimpleDateFormat("d MMMM yyyy", if (language == "ar") Locale("ar") else Locale.ENGLISH)
        val dateStr = sdf.format(Date(note.updatedAt))

        val text = buildString {
            if (note.title.isNotBlank()) {
                append("📌 ")
                append(note.title)
                append("\n\n")
            }
            append(note.content)
            append("\n\n---\n")
            append("📅 ")
            append(dateStr)
            append("\n")
            append(if (language == "ar") "مفكرتي اليومية • يومي (Yawmi)" else "Daily Organizer • Yawmi")
        }

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, note.title)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val chooser = Intent.createChooser(sendIntent, if (language == "ar") "مشاركة نص الملاحظة" else "Share Note Text").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(chooser)
    }
}
