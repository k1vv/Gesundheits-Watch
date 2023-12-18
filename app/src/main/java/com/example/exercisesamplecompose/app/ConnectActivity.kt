package com.example.exercisesamplecompose.app

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.exercisesamplecompose.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder

class ConnectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)

        val qrCodeImageView: ImageView = findViewById(R.id.qrCodeImageView)

        // Generate QR Code
        try {
            val qrCode = generateQRCode("3")
            qrCodeImageView.setImageBitmap(qrCode)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    private fun generateQRCode(data: String): Bitmap {
        val multiFormatWriter = MultiFormatWriter()
        val bitMatrix = multiFormatWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200)
        val barcodeEncoder = BarcodeEncoder()
        return barcodeEncoder.createBitmap(bitMatrix)
    }
}
