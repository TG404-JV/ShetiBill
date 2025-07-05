package com.example.farmer.home

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import com.example.farmer.fertilizer.FertilizerExpenditure
import java.io.FileOutputStream
import java.io.IOException
import java.lang.String
import kotlin.Array
import kotlin.Int
import kotlin.arrayOf
import kotlin.math.ceil
import kotlin.math.min
import kotlin.toString


class FertilizerExpenditurePrintAdapter(
    private val expenditureList: MutableList<FertilizerExpenditure>?,
    private val logoBitmap: Bitmap?
) : PrintDocumentAdapter() {
    private val pageWidth = 612 // 8.5 inches
    private val pageHeight = 792 // 11 inches
    private val marginLeft = 72 // 1 inch
    private val marginTop = 72 // 1 inch
    private val lineHeight = 24

    override fun onLayout(
        oldAttributes: PrintAttributes?, newAttributes: PrintAttributes?,
        cancellationSignal: CancellationSignal?, callback: LayoutResultCallback, metadata: Bundle?
    ) {
        callback.onLayoutFinished(
            PrintDocumentInfo.Builder("Fertilizer_Expenditure_Summary")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                .build(),
            true
        )
    }

    override fun onWrite(
        pages: Array<PageRange?>?, destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal?, callback: WriteResultCallback
    ) {
        val pdfDocument = PdfDocument()
        val textPaint = Paint()
        textPaint.isAntiAlias = true
        textPaint.textSize = 12f

        val titlePaint = Paint()
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textSize = 18f
        titlePaint.color = -0xb350b0 // Green color for title
        titlePaint.isAntiAlias = true

        val headerPaint = Paint()
        headerPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        headerPaint.textSize = 14f
        headerPaint.color = -0x743cb6 // Lighter green for headers
        headerPaint.setAntiAlias(true)

        val linePaint = Paint()
        linePaint.setColor(-0x371937) // Light green for separator lines
        linePaint.setStrokeWidth(1f)

        var currentY: Int
        val itemsPerPage =
            (pageHeight - (marginTop * 2 + 150)) / lineHeight // Adjusted for title/logo space
        val pageCount = ceil((expenditureList?.size?.toFloat()?.div(itemsPerPage))?.toDouble()?:0.0).toInt()

        for (pageIndex in 0..<pageCount) {
            val pageInfo = PageInfo.Builder(pageWidth, pageHeight, pageIndex + 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.getCanvas()
            currentY = marginTop

            // Logo and Title
            if (logoBitmap != null) {
                canvas.drawBitmap(logoBitmap, marginLeft.toFloat(), currentY.toFloat(), null)
                currentY += 50
            }
            canvas.drawText(
                "Fertilizer Expenditure Summary",
                (marginLeft + 100).toFloat(),
                currentY.toFloat(),
                titlePaint
            )
            currentY += 40

            // Header
            canvas.drawText("Item Name", marginLeft.toFloat(), currentY.toFloat(), headerPaint)
            canvas.drawText(
                "Purchase Date",
                (marginLeft + 150).toFloat(),
                currentY.toFloat(),
                headerPaint
            )
            canvas.drawText("Amount", (marginLeft + 300).toFloat(), currentY.toFloat(), headerPaint)
            canvas.drawText(
                "Payment Mode",
                (marginLeft + 400).toFloat(),
                currentY.toFloat(),
                headerPaint
            )
            currentY += 20

            // Separator line
            canvas.drawLine(
                marginLeft.toFloat(),
                currentY.toFloat(),
                (pageWidth - marginLeft).toFloat(),
                currentY.toFloat(),
                linePaint
            )
            currentY += 10

            // Data Rows
            val startIndex = pageIndex * itemsPerPage
            val endIndex = min(startIndex + itemsPerPage, expenditureList?.size?:0)

            for (i in startIndex..<endIndex) {
                val expenditure = expenditureList?.get(i)
                canvas.drawText(
                    expenditure?.itemName!!,
                    marginLeft.toFloat(),
                    currentY.toFloat(),
                    textPaint
                )
                canvas.drawText(
                    expenditure.purchaseDate!!,
                    (marginLeft + 150).toFloat(),
                    currentY.toFloat(),
                    textPaint
                )
                canvas.drawText(
                    String.valueOf(expenditure.amount),
                    (marginLeft + 300).toFloat(),
                    currentY.toFloat(),
                    textPaint
                )
                canvas.drawText(
                    expenditure.paymentMode!!,
                    (marginLeft + 400).toFloat(),
                    currentY.toFloat(),
                    textPaint
                )
                currentY += lineHeight
            }

            // Footer - Page Number and Contact Info
            canvas.drawLine(
                marginLeft.toFloat(),
                (pageHeight - 50).toFloat(),
                (pageWidth - marginLeft).toFloat(),
                (pageHeight - 50).toFloat(),
                linePaint
            )
            canvas.drawText(
                "Page " + (pageIndex + 1) + " of " + pageCount,
                marginLeft.toFloat(),
                (pageHeight - 30).toFloat(),
                textPaint
            )
            canvas.drawText(
                "FarmApp | Contact: 123-456-7890",
                (pageWidth - 200).toFloat(),
                (pageHeight - 30).toFloat(),
                textPaint
            )

            pdfDocument.finishPage(page)
        }

        // Write the document to the destination
        try {
            FileOutputStream(destination.getFileDescriptor()).use { out ->
                pdfDocument.writeTo(out)
            }
        } catch (e: IOException) {
            callback.onWriteFailed(e.toString())
        } finally {
            pdfDocument.close()
        }

        callback.onWriteFinished(arrayOf<PageRange?>(PageRange.ALL_PAGES))
    }
}
