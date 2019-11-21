package com.sedi.viktor.learnAll.ui.scan_words

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.ui.scan_words.ui.CameraSourcePreview
import com.sedi.viktor.learnAll.ui.scan_words.ui.GraphicOverlay

class CaptureActivity : AppCompatActivity() {

    lateinit var cameraSourcePreview: CameraSourcePreview
    lateinit var graphicOverlay: GraphicOverlay<OcrGraphic>
    lateinit var gestureDetector: GestureDetector
    lateinit var scaleDetector: ScaleGestureDetector

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

        gestureDetector = GestureDetector(this, CaptureGestureListener())
        scaleDetector = ScaleGestureDetector(this, ScaleDetectorListener())

    }

    private fun requestCameraPermission() {
        val permissions = arrayOf(Manifest.permission.CAMERA)

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERMISSION)
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        val b = scaleDetector.onTouchEvent(event)

        val c = gestureDetector.onTouchEvent(event)

        return super.onTouchEvent(event)
    }

    /**
     * onTap is called to speak the tapped TextBlock, if any, out loud.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the tap was on a TextBlock
     */
    private fun onTap(rawX: Float, rawY: Float): Boolean {
        // TODO: Speak the text when the user taps on screen.
        return false
    }


    private inner class CaptureGestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            return onTap(e.rawX, e.rawY) || super.onSingleTapConfirmed(e)
        }
    }


    private inner class ScaleDetectorListener : ScaleGestureDetector.OnScaleGestureListener {


        override fun onScale(detector: ScaleGestureDetector): Boolean {
            return false
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            // if (cameraSource != null) {
            //   cameraSource.doZoom(detector.scaleFactor)
            // }
        }
    }

}