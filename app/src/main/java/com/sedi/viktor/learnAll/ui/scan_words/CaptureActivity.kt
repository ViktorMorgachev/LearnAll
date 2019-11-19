package com.sedi.viktor.learnAll.ui.scan_words

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.ui.scan_words.ui.CameraSourcePreview
import com.sedi.viktor.learnAll.ui.scan_words.ui.GraphicOverlay

class CaptureActivity : AppCompatActivity() {

    lateinit var cameraSourcePreview: CameraSourcePreview
    lateinit var graphicOverlay: GraphicOverlay<OcrGraphic>

    // Permission request codes need to be < 256
    private val RC_HANDLE_CAMERA_PERMISSION = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.capture_layout)

        cameraSourcePreview = findViewById(R.id.preview)
        graphicOverlay = findViewById(R.id.graphicOverlay)


        val rc = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if (rc != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        }

    }

    private fun requestCameraPermission() {
        val permissions = arrayOf(Manifest.permission.CAMERA)

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERMISSION)
        }

    }
}