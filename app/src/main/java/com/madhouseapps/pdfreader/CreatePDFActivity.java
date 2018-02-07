package com.madhouseapps.pdfreader;

import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreatePDFActivity extends AppCompatActivity {

    private TextView titleTV, contentTV;
    private EditText titleInput, contentInput;
    private FloatingActionButton fabSavePDF;
    private Typeface avenir;

    private DisplayMetrics displayMetrics;
    private int width, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pdf);

        avenir = Typeface.createFromAsset(getAssets(), "fonts/Avenir-Book.ttf");
        titleTV = findViewById(R.id.pdf_title_label);
        titleTV.setTypeface(avenir);
        contentTV = findViewById(R.id.pdf_content_label);
        contentTV.setTypeface(avenir);
        titleInput = findViewById(R.id.pdf_title_input);
        titleInput.setTypeface(avenir);
        contentInput = findViewById(R.id.pdf_content_input);
        contentInput.setTypeface(avenir);
        fabSavePDF = findViewById(R.id.fab_save_pdf);

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        fabSavePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PdfDocument document = new PdfDocument();
                int pageNumber = 1;
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width,
                        height - 20, pageNumber).create();

                PdfDocument.Page page = document.startPage(pageInfo);

                Canvas canvas = page.getCanvas();
                contentInput.draw(canvas);

                //TODO This code block serves black screen in the output. Please debug asap!
                /*
                Paint paint = new Paint();
                canvas.drawPaint(paint);
                paint.setColor(Color.BLACK);
                paint.setTextSize(16);
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setTypeface(avenir);
                int titlePosHor = width / 2;
                canvas.drawText(titleInput.getText().toString(), titlePosHor, 48, paint);
                canvas.drawText(contentInput.getText().toString(), 48, 96, paint);
                */

                document.finishPage(page);

                SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhhmmss");
                String pdfName = titleInput.getText().toString() + sdf.format(Calendar.getInstance().getTime()) + ".pdf";
                createFile(document, pdfName);
            }
        });
    }

    private void createFile(PdfDocument document, String pdfName) {
        FileOutputStream fos = null;
        try {
            File folder = new File(Environment.getExternalStorageDirectory(), "PDFReader MadHouse");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File outputFile = new File(folder, pdfName);
            outputFile.createNewFile();
            fos = new FileOutputStream(outputFile);
            document.writeTo(fos);
            Toast.makeText(CreatePDFActivity.this, "File created successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (IOException e) {
            Toast.makeText(this, "File creation failed!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
}
