package com.example.farmer.home;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
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

    public FertilizerExpenditurePrintAdapter(Context context, List<FertilizerExpenditure> expenditureList) {
        this.context = context;
        this.expenditureList = expenditureList;
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        PdfDocument pdfDocument = new PdfDocument();
        int pageWidth = 612; // 8.5 inches
        int pageHeight = 792; // 11 inches
        int marginLeft = 72; // 1 inch
        int marginTop = 72; // 1 inch

        Paint textPaint = new Paint();
        textPaint.setTextSize(14);
        textPaint.setAntiAlias(true);

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Draw title
        canvas.drawText("Fertilizer Expenditure Summary", marginLeft, marginTop, textPaint);

        // Draw expenditure details
        int currentY = marginTop + 50;
        for (FertilizerExpenditure expenditure : expenditureList) {
            canvas.drawText("Item: " + expenditure.getItemName(), marginLeft, currentY, textPaint);
            currentY += 20;
            canvas.drawText("Purchase Date: " + expenditure.getPurchaseDate(), marginLeft, currentY, textPaint);
            currentY += 20;
            canvas.drawText("Amount: " + expenditure.getPurchaseAmount(), marginLeft, currentY, textPaint);
            currentY += 20;
            canvas.drawText("Payment Mode: " + expenditure.getPaymentMode(), marginLeft, currentY, textPaint);
            currentY += 20;
            if (expenditure.getReceiptImagePath() != null) {
                canvas.drawText("Receipt Image: " + expenditure.getReceiptImagePath(), marginLeft, currentY, textPaint);
                currentY += 20;
            }
            currentY += 20;
        }

        pdfDocument.finishPage(page);

        try (FileOutputStream out = new FileOutputStream(destination.getFileDescriptor())) {
            pdfDocument.writeTo(out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pdfDocument.close();
        }

        callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle metadata) {
        callback.onLayoutFinished(new PrintDocumentInfo.Builder("Fertilizer Expenditure Summary")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(1)
                .build(), true);
    }
}