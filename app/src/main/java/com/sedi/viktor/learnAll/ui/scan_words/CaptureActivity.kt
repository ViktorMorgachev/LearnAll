package com.sedi.viktor.learnAll.ui.scan_words

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.ui.scan_words.ui.CameraSourcePreview
import com.sedi.viktor.learnAll.ui.scan_words.ui.GraphicOverlay

class CaptureActivity : AppCompatActivity() {

    lateinit var cameraSourcePreview: CameraSourcePreview
    lateinit var graphicOverlay: GraphicOverlay.Graphic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.capture)

        cameraSourcePreview = findViewById(R.id.preview)
        graphicOverlay = findViewById(R.id.graphicOverlay)

    }
}