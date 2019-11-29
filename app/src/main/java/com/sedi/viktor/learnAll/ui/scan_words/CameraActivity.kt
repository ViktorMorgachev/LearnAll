package com.sedi.viktor.learnAll.ui.scan_words

import android.Manifest
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.DisplayMetrics
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.*
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.services.NetworkReceiver
import com.sedi.viktor.learnAll.ui.recognizers.TextRecognizer
import kotlinx.android.synthetic.main.capture_layout.*
import kotlinx.android.synthetic.main.preview_text_camera_layout.*
import java.util.concurrent.Executors


class CameraActivity : AppCompatActivity(), LifecycleOwner {


    // Managers
    private lateinit var networkReceiver: NetworkReceiver
    private var textRecognizer: TextRecognizer? = null
    private var detector: FirebaseVisionTextRecognizer? = null

    // Views
    private lateinit var textureView: TextureView

    private lateinit var gestureDetector: GestureDetector
    private lateinit var scaleDetector: ScaleGestureDetector
    private var availableNetwork = false
    private var handler = Handler()
    private lateinit var runnable: Runnable
    private lateinit var preview: Preview
    private val executor = Executors.newSingleThreadExecutor()


    // Callback
    private lateinit var netConnectivityCallback: NetConnectivityCallback
    private lateinit var textRecognizedCallback: TextRecognizedCallback

    companion object {
        const val AutoFocusExtra = "AutoFocus"
        const val UseFlashExtra = "UseFlash"
    }

    // Intent request code to handle updating play services if needed.
    private val RC_HANDLE_GMS = 9001
    // Permission request codes need to be < 256
    private val RC_HANDLE_CAMERA_PERMISSION = 2
    private val RC_HANDLE_INTERNET_PERMISSION = 3;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)


        runnable = Runnable {
            Log.d("LearnAll", "Was Background thread")
        }

        initCallbacks()



        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.capture_layout)

        textureView = findViewById(R.id.textureView)


        textureView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }

        val requestInternet = ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)

        if (requestInternet != PackageManager.PERMISSION_GRANTED) {
            requestInternetPermission()
        }


        val requestCamera = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)

        if (requestCamera != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
        } else {
            initTextRecognizer()
            textureView.post { startCamera() }
        }

        gestureDetector = GestureDetector(this, CaptureGestureListener())
        scaleDetector = ScaleGestureDetector(this, ScaleDetectorListener())


    }


    override fun onResume() {
        super.onResume()
        // Регистрируем сервис следящий за интернетом
        registerConnectivityReceiver()
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, 4000)

    }


    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
        unregisterReceiver(networkReceiver)
    }

    private fun initCallbacks() {
        netConnectivityCallback = object : NetConnectivityCallback {
            override fun onSuccessNetConnected() {
                availableNetwork = true
                swichDataRecognize(true)
            }

            override fun onFaillureNetConnected() {
                availableNetwork = true
                swichDataRecognize(false)
            }
        }
    }

    private fun swichDataRecognize(isNetworkAvailable: Boolean) {


        Log.d("LearnAll", "Available Network: $isNetworkAvailable")


        if (textRecognizer != null) {
            textRecognizer!!.setAvailableNetwork(isNetworkAvailable)
        }


    }

    private fun registerConnectivityReceiver() {
        networkReceiver = NetworkReceiver(netConnectivityCallback, this)
        val intentFilter = IntentFilter().apply {
            addAction("android.net.conn.CONNECTIVITY_CHANGE")
        }
        registerReceiver(networkReceiver, intentFilter)
    }

    private fun initTextRecognizer() {

        textRecognizedCallback = object : TextRecognizedCallback {
            @UiThread
            override fun onImageUnRecognized() {
                layout_preview_text.visibility = View.GONE
            }

            @UiThread
            override fun onImageRecognized(text: String, rect: Rect?) {
                layout_preview_text.visibility = View.VISIBLE
                action_text.post { action_text.text = text }
                // После создадим вьюшку в которую будем перерисовывать с отрисовкой текста, прям на SurfaceText
            }
        }

        detector = if (!availableNetwork)
            FirebaseVision.getInstance().onDeviceTextRecognizer
        else FirebaseVision.getInstance().cloudTextRecognizer

        textRecognizer = TextRecognizer(
            detector!!,
            this,
            textRecognizedCallback,
            availableNetwork
        )
    }

    private fun startCamera() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            val metrics = DisplayMetrics().also { textureView.display.getRealMetrics(it) }
            val screenSize = Size(metrics.widthPixels, metrics.heightPixels)
            val screenAspectRatio = Rational(metrics.widthPixels, metrics.heightPixels)
            val previewConfig = PreviewConfig.Builder().apply {
                setLensFacing(CameraX.LensFacing.BACK)
                setTargetResolution(screenSize)
                setTargetAspectRatio(screenAspectRatio)
                setTargetRotation(textureView.display.rotation)
            }.build()

            preview = Preview(previewConfig)


            preview.setOnPreviewOutputUpdateListener {

                // To update the SurfaceTexture, we have to remove it and re-add it
                val parent = textureView.parent as ViewGroup
                parent.removeView(textureView)
                parent.addView(textureView, 0)
                textureView.surfaceTexture = it.surfaceTexture

                updateTransform()
            }

            val analyzerThread = HandlerThread("TextAnalizer").apply {
                start()
            }

            val imageAnalysisConfig = ImageAnalysisConfig.Builder()
                .setTargetResolution(Size(1280, 720))
                .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_NEXT_IMAGE)
                .setCallbackHandler(
                    Handler(analyzerThread.looper)
                ).build()


            val imageAnalysis = ImageAnalysis(imageAnalysisConfig)

            imageAnalysis.analyzer = textRecognizer


            CameraX.bindToLifecycle(this, preview, imageAnalysis)
        }
    }


    private fun restartCamera() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            CameraX.unbindAll()

            val analyzerThread = HandlerThread("TextAnalizer").apply {
                start()
            }

            val imageAnalysisConfig = ImageAnalysisConfig.Builder()
                .setTargetResolution(Size(1280, 720))
                .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_NEXT_IMAGE)
                .setCallbackHandler(
                    Handler(analyzerThread.looper)
                ).build()

            val imageAnalysis = ImageAnalysis(imageAnalysisConfig)

            imageAnalysis.analyzer = textRecognizer

            CameraX.bindToLifecycle(this, preview, imageAnalysis)
        }
    }


    private fun updateTransform() {
        val matrix = Matrix()
        val centerX = textureView.width / 2f
        val centerY = textureView.height / 2f

        val rotationDegrees = when (textureView.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)
        textureView.setTransform(matrix)
    }


    private fun requestCameraPermission() {
        val permissions = arrayOf(Manifest.permission.CAMERA)

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERMISSION)
        }
    }


    private fun requestInternetPermission() {
        val permissions = arrayOf(Manifest.permission.INTERNET)

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_INTERNET_PERMISSION)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode != RC_HANDLE_CAMERA_PERMISSION && requestCode != RC_HANDLE_INTERNET_PERMISSION)
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == RC_HANDLE_INTERNET_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val autofocus = intent.getBooleanExtra(AutoFocusExtra, false)
                val useFlash = intent.getBooleanExtra(UseFlashExtra, false)
            }
        } else {

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

    interface NetConnectivityCallback {
        fun onSuccessNetConnected()
        fun onFaillureNetConnected()
    }


    interface TextRecognizedCallback {
        fun onImageRecognized(text: String, rect: Rect?)
        fun onImageUnRecognized()
    }

}

