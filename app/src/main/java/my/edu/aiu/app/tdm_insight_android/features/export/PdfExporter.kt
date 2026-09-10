package my.edu.aiu.app.tdm_insight_android.features.export

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import my.edu.aiu.app.tdm_insight_android.viewmodel.PatientViewModel
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

/**
 * OWNER: Benat
 */
object PdfExporter {

    private fun drawPdfContent(canvas: Canvas, viewModel: PatientViewModel) {
        val paint = Paint()
        var y = 50f
        
        // Header
        paint.color = Color.rgb(0, 77, 64) // DeepTeal
        paint.textSize = 24f
        paint.isFakeBoldText = true
        canvas.drawText("TDM Insight - Clinical Report", 50f, y, paint)
        y += 40f

        // Patient Details Section
        paint.color = Color.BLACK
        paint.textSize = 18f
        canvas.drawText("Patient Information", 50f, y, paint)
        y += 25f
        
        paint.textSize = 14f
        paint.isFakeBoldText = false
        canvas.drawText("Case Name: ${viewModel.caseName}", 50f, y, paint)
        y += 20f
        canvas.drawText("Age: ${viewModel.age} years", 50f, y, paint)
        y += 20f
        canvas.drawText("Weight: ${viewModel.weight} kg", 50f, y, paint)
        y += 20f
        canvas.drawText("Serum Creatinine: ${viewModel.scr} mg/dL", 50f, y, paint)
        y += 40f

        // Calculation Results
        paint.isFakeBoldText = true
        paint.textSize = 18f
        canvas.drawText("Pharmacokinetic Results", 50f, y, paint)
        y += 25f
        
        paint.textSize = 14f
        paint.isFakeBoldText = false
        canvas.drawText("Half-life: 6.93 hours (Estimated)", 50f, y, paint)
        y += 20f
        canvas.drawText("Clearance: 5.2 L/hr", 50f, y, paint)
        y += 20f
        canvas.drawText("AUC24: 250 mg·hr/L", 50f, y, paint)
        y += 40f

        // Simulated Therapy
        paint.isFakeBoldText = true
        paint.textSize = 18f
        canvas.drawText("Simulated Therapy Plan", 50f, y, paint)
        y += 25f
        
        paint.textSize = 14f
        paint.isFakeBoldText = false
        canvas.drawText("Planned Dose: ${viewModel.dose} mg", 50f, y, paint)
        y += 20f
        canvas.drawText("Dosing Interval: ${viewModel.interval} h", 50f, y, paint)
        y += 20f
        canvas.drawText("Expected Peak: ${String.format(Locale.US, "%.1f", viewModel.expectedCmax)} mg/L", 50f, y, paint)
        y += 20f
        canvas.drawText("Expected Trough: ${String.format(Locale.US, "%.1f", viewModel.expectedCmin)} mg/L", 50f, y, paint)
        y += 60f

        // Disclaimer
        paint.textSize = 10f
        paint.color = Color.GRAY
        canvas.drawText("DISCLAIMER: This is an academic simulation for educational purposes.", 50f, y, paint)
        y += 15f
        canvas.drawText("Never use this report for real patient care decisions.", 50f, y, paint)
    }

    fun downloadPdf(context: Context, viewModel: PatientViewModel) {
        val fileName = "TDM_Report_${System.currentTimeMillis()}.pdf"
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        
        drawPdfContent(page.canvas, viewModel)
        pdfDocument.finishPage(page)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                    }
                    Toast.makeText(context, "Report ready in Downloads!", Toast.LENGTH_LONG).show()
                }
            } else {
                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
                pdfDocument.writeTo(FileOutputStream(file))
                MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), null, null)
                Toast.makeText(context, "Report saved: ${file.absolutePath}", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }

    fun sharePdf(context: Context, viewModel: PatientViewModel) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        
        drawPdfContent(page.canvas, viewModel)
        pdfDocument.finishPage(page)

        val cacheFile = File(context.cacheDir, "TDM_Clinical_Report.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(cacheFile))
            
            // Generate content URI using FileProvider
            val uri = FileProvider.getUriForFile(
                context, 
                "${context.packageName}.provider", 
                cacheFile
            )
            
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "TDM Clinical Report: ${viewModel.caseName}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            val chooser = Intent.createChooser(shareIntent, "Share Clinical Report")
            if (context !is Activity) {
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
            
            Toast.makeText(context, "Preparing report...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Sharing failed: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            pdfDocument.close()
        }
    }
}
