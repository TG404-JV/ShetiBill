package com.example.farmer.home;// Enhanced FertilizerExpenditurePrintAdapter.java

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;

import com.example.farmer.fertilizer.FertilizerExpenditure;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FertilizerExpenditurePrintAdapter extends PrintDocumentAdapter {

    private Context context;
    private List<FertilizerExpenditure> expenditureList;
    private int pageWidth = 612; // 8.5 inches
    private int pageHeight = 792; // 11 inches
    private int marginLeft = 72; // 1 inch
    private int marginTop = 72; // 1 inch
    private int lineHeight = 24;

    private Bitmap logoBitmap;

    public FertilizerExpenditurePrintAdapter(Context context, List<FertilizerExpenditure> expenditureList, Bitmap logoBitmap) {
        this.context = context;
        this.expenditureList = expenditureList;
        this.logoBitmap = logoBitmap;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle metadata) {
        callback.onLayoutFinished(
                new PrintDocumentInfo.Builder("Fertilizer_Expenditure_Summary")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                        .build(),
                true
        );
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination,
                        CancellationSignal cancellationSignal, WriteResultCallback callback) {

        PdfDocument pdfDocument = new PdfDocument();
        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(12);

        Paint titlePaint = new Paint();
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(18);
        titlePaint.setColor(0xFF4CAF50); // Green color for title
        titlePaint.setAntiAlias(true);

        Paint headerPaint = new Paint();
        headerPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        headerPaint.setTextSize(14);
        headerPaint.setColor(0xFF8BC34A); // Lighter green for headers
        headerPaint.setAntiAlias(true);

        Paint linePaint = new Paint();
        linePaint.setColor(0xFFC8E6C9); // Light green for separator lines
        linePaint.setStrokeWidth(1);

        int currentY;
        int itemsPerPage = (pageHeight - (marginTop * 2 + 150)) / lineHeight; // Adjusted for title/logo space
        int pageCount = (int) Math.ceil((float) expenditureList.size() / itemsPerPage);

        for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex + 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            currentY = marginTop;

            // Logo and Title
            if (logoBitmap != null) {
                canvas.drawBitmap(logoBitmap, marginLeft, currentY, null);
                currentY += 50;
            }
            canvas.drawText("Fertilizer Expenditure Summary", marginLeft + 100, currentY, titlePaint);
            currentY += 40;

            // Header
            canvas.drawText("Item Name", marginLeft, currentY, headerPaint);
            canvas.drawText("Purchase Date", marginLeft + 150, currentY, headerPaint);
            canvas.drawText("Amount", marginLeft + 300, currentY, headerPaint);
            canvas.drawText("Payment Mode", marginLeft + 400, currentY, headerPaint);
            currentY += 20;

            // Separator line
            canvas.drawLine(marginLeft, currentY, pageWidth - marginLeft, currentY, linePaint);
            currentY += 10;

            // Data Rows
            int startIndex = pageIndex * itemsPerPage;
            int endIndex = Math.min(startIndex + itemsPerPage, expenditureList.size());

            for (int i = startIndex; i < endIndex; i++) {
                FertilizerExpenditure expenditure = expenditureList.get(i);
                canvas.drawText(expenditure.getItemName(), marginLeft, currentY, textPaint);
                canvas.drawText(expenditure.getPurchaseDate(), marginLeft + 150, currentY, textPaint);
                canvas.drawText(String.valueOf(expenditure.getPurchaseAmount()), marginLeft + 300, currentY, textPaint);
                canvas.drawText(expenditure.getPaymentMode(), marginLeft + 400, currentY, textPaint);
                currentY += lineHeight;
            }

            // Footer - Page Number and Contact Info
            canvas.drawLine(marginLeft, pageHeight - 50, pageWidth - marginLeft, pageHeight - 50, linePaint);
            canvas.drawText("Page " + (pageIndex + 1) + " of " + pageCount, marginLeft, pageHeight - 30, textPaint);
            canvas.drawText("FarmApp | Contact: 123-456-7890", pageWidth - 200, pageHeight - 30, textPaint);

            pdfDocument.finishPage(page);
        }

        // Write the document to the destination
        try (FileOutputStream out = new FileOutputStream(destination.getFileDescriptor())) {
            pdfDocument.writeTo(out);
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
        } finally {
            pdfDocument.close();
        }

        callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
    }
}
