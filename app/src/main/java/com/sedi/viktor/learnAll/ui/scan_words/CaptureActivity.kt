package com.sedi.viktor.learnAll.ui.scan_words

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.ui.scan_words.ui.CameraSource
import com.sedi.viktor.learnAll.ui.scan_words.ui.CameraSourcePreview
import com.sedi.viktor.learnAll.ui.scan_words.ui.GraphicOverlay
import java.io.IOException

class CaptureActivity : AppCompatActivity() {

    // Views
    private lateinit var cameraSourcePreview: CameraSourcePreview
    private lateinit var graphicOverlay: GraphicOverlay<OcrGraphic>

    // Managers
    private lateinit var gestureDetector: GestureDetector
    private lateinit var scaleDetector: ScaleGestureDetector
    private lateinit var cameraManager: CameraManager


    // Data
    private lateinit var myCameras: List<String>
    private lateinit var cameraServices: ArrayList<CameraService>

    //var cameraSource: CameraSource? = null


    // Callbacks
    lateinit var cameraOpenedCallback: CameraService.CameraOpenedCallback


    companion object {
        const val AutoFocusExtra = "AutoFocus"
        const val UseFlashExtra = "UseFlash"
    }

    // Intent request code to handle updating play services if needed.
    private val RC_HANDLE_GMS = 9001
    // Permission request codes need to be < 256
    private val RC_HANDLE_CAMERA_PERMISSION = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.capture_layout)

        cameraSourcePreview = findViewById(R.id.preview)
        graphicOverlay = findViewById(R.id.graphicOverlay)


        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager



        initCallbacks()

        val rc = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if (rc != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
        } else {
            myCameras = cameraManager.cameraIdList.asList()

            cameraServices.add(CameraService(myCameras[0], cameraManager, cameraOpenedCallback))
        }

        gestureDetector = GestureDetector(this, CaptureGestureListener())
        scaleDetector = ScaleGestureDetector(this, ScaleDetectorListener())

    }

    private fun initCallbacks() {
        cameraOpenedCallback = object : CameraService.CameraOpenedCallback {
            override fun createCameraPreviewSession() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }
    }

    private fun requestCameraPermission() {
        val permissions = arrayOf(Manifest.permission.CAMERA)

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode != RC_HANDLE_CAMERA_PERMISSION)
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val autofocus = intent.getBooleanExtra(AutoFocusExtra, false)
            val useFlash = intent.getBooleanExtra(UseFlashExtra, false)

            myCameras = cameraManager.cameraIdList.asList()


            // createCameraSource(autofocus, useFlash)
        }


    }

    private fun startCameraSource() {

        val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
            applicationContext
        )

        if (code != ConnectionResult.SUCCESS)
            GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS).show()
        else {
           /* if (cameraSource != null) {
                try {
                    //  preview.start(cameraSource, graphicOverlay)
                } catch (e: IOException) {
                    cameraSource!!.release()
                    cameraSource = null

                }
            }*/
        }
    }

    override fun onResume() {
        super.onResume()
        if (cameraServices.size > 0) {
            cameraServices[0].openCamera(this)
        }
    }

    override fun onPause() {
        super.onPause()
        if (cameraServices.size > 0) {
            cameraServices[0].closeCamera()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (cameraServices.size > 0) {
            cameraServices[0].closeCamera()
            cameraServices = ArrayList();
        }
    }

    private fun createCameraSource(autofocus: Boolean, useFlash: Boolean) {
        val context = applicationContext


        // TODO: Create the TextRecognizer
        // TODO: Set the TextRecognizer's Processor
        // TODO: Check if the TextRecognizer is operational.
        // TODO: Create the cameraSource using the TextRecognizer

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


    enum class CameraType(val type: Int) {
        CAMERA_BACK(0),
        CAMERA_FRONT(1)
    }

}