package com.example.trikesafe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRdisplay extends AppCompatActivity {

    private ImageView qrImageView;
    private Button btnDone;
    private static final String APP_PREFIX = "TSAFE_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrdisplay);

        qrImageView = findViewById(R.id.qrImageView);
        btnDone = findViewById(R.id.btnDone);

        String rawData = getIntent().getStringExtra("qr_data");

        if (rawData == null || rawData.isEmpty()) {
            Toast.makeText(this, "No QR data received", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if prefix already exists to avoid "TSAFE_TSAFE_"
        String secureData = rawData.startsWith(APP_PREFIX) ? rawData : APP_PREFIX + rawData;

        generateQRCode(secureData);

        btnDone.setOnClickListener(v -> {
            Intent intent = new Intent(QRdisplay.this, AdminHomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void generateQRCode(String data) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix matrix = writer.encode(data, BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            qrImageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to generate QR Code", Toast.LENGTH_SHORT).show();
        }
    }
}