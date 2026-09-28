package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.PracticeSession
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReportExportHelper {

    fun generateAndSharePdfReport(
        context: Context,
        sessions: List<PracticeSession>,
        streakDays: Int,
        userName: String = "قارئ القرآن الكريم"
    ): File? {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size in points
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val paintTitle = Paint().apply {
            color = Color.rgb(6, 78, 59) // Emerald Dark
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        val paintSubtitle = Paint().apply {
            color = Color.rgb(180, 83, 9) // Gold Dark
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
        }

        val paintHeader = Paint().apply {
            color = Color.rgb(15, 23, 42)
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val paintText = Paint().apply {
            color = Color.rgb(51, 65, 85)
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        val paintLine = Paint().apply {
            color = Color.rgb(209, 250, 229)
            strokeWidth = 2f
        }

        val centerX = 595f / 2f
        var y = 60f

        // Title & Header
        canvas.drawText("تقرير أداء التلاوة والتجويد - المقرئ الذكي", centerX, y, paintTitle)
        y += 24f
        canvas.drawText("Al-Muqri' Al-Dhaki - Quran Recitation & Tajweed Progress Report", centerX, y, paintSubtitle)
        y += 20f

        canvas.drawLine(40f, y, 555f, y, paintLine)
        y += 25f

        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        canvas.drawText("تاريخ التقرير: ${dateFormat.format(Date())}", 50f, y, paintText)
        canvas.drawText("المستخدم: $userName", 350f, y, paintText)
        y += 20f
        canvas.drawText("أيام الالتزام المتتالية: $streakDays يوم", 50f, y, paintText)
        val avgScore = if (sessions.isNotEmpty()) sessions.map { it.overallScore }.average().toInt() else 0
        canvas.drawText("متوسط إتقان التجويد العام: $avgScore%", 350f, y, paintText)
        y += 30f

        // Table Header
        paintLine.color = Color.rgb(6, 78, 59)
        paintLine.strokeWidth = 1.5f
        canvas.drawLine(40f, y, 555f, y, paintLine)
        y += 18f

        canvas.drawText("السورة", 50f, y, paintHeader)
        canvas.drawText("المدة", 200f, y, paintHeader)
        canvas.drawText("الدرجة", 300f, y, paintHeader)
        canvas.drawText("المخارج", 380f, y, paintHeader)
        canvas.drawText("المدود", 480f, y, paintHeader)
        y += 10f

        canvas.drawLine(40f, y, 555f, y, paintLine)
        y += 20f

        // Sessions Rows
        val recentSessions = sessions.take(12)
        if (recentSessions.isEmpty()) {
            canvas.drawText("لا توجد جلسات تلاوة مسجلة بعد. ابدأ بالتسجيل في مختبر التلاوة.", 50f, y, paintText)
            y += 25f
        } else {
            for (s in recentSessions) {
                canvas.drawText(s.surahName, 50f, y, paintText)
                canvas.drawText("${s.durationSeconds} ثانية", 200f, y, paintText)
                canvas.drawText("${s.overallScore}%", 300f, y, paintText)
                canvas.drawText("${s.makharijScore}%", 380f, y, paintText)
                canvas.drawText("${s.maddScore}%", 480f, y, paintText)
                y += 20f
                if (y > 760f) break
            }
        }

        // Footer & Signature
        y = 800f
        paintLine.color = Color.rgb(226, 217, 200)
        canvas.drawLine(40f, y - 15f, 555f, y - 15f, paintLine)
        val paintFooter = Paint().apply {
            color = Color.rgb(100, 116, 139)
            textSize = 10f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("تطبيق المقرئ الذكي | تطوير وإشراف: Mahmoud Zakaria Aswad", centerX, y, paintFooter)

        document.finishPage(page)

        val outputFile = File(context.cacheDir, "تقرير_المقرئ_الذكي_${System.currentTimeMillis()}.pdf")
        return try {
            val fos = FileOutputStream(outputFile)
            document.writeTo(fos)
            document.close()
            fos.close()
            outputFile
        } catch (e: Exception) {
            document.close()
            null
        }
    }

    fun sharePdfOrSummary(context: Context, pdfFile: File?, summaryText: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            if (pdfFile != null && pdfFile.exists()) {
                type = "application/pdf"
                try {
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        pdfFile
                    )
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } catch (_: Exception) {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, summaryText)
                }
            } else {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, summaryText)
            }
            putExtra(Intent.EXTRA_SUBJECT, "تقرير أداء تلاوة القرآن الكريم - المقرئ الذكي")
        }
        context.startActivity(Intent.createChooser(intent, "مشاركة تقرير التلاوة"))
    }
}
