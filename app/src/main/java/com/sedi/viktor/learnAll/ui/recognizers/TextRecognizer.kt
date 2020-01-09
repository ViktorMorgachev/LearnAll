package com.sedi.viktor.learnAll.ui.recognizers

import android.media.Image
import android.os.Handler
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import com.sedi.viktor.learnAll.ui.scan_words.CameraActivity
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit


class TextRecognizer(
    private var detector: FirebaseVisionTextRecognizer,
    private val lifecycleOwner: LifecycleOwner,
    private val textRecognizedCallback: CameraActivity.TextRecognizedCallback,
    private var availableNetwork: Boolean
) :
    ImageAnalysis.Analyzer {

    private val handler = Handler()
    private var firebaseImage: FirebaseVisionImage? = null

    private var lastTimeStampAnalized = System.currentTimeMillis()

    // Improve perfomance
    private var latestImage: ByteBuffer? = null


    override fun analyze(imageProxy: ImageProxy?, rotationDegrees: Int) {

        if (imageProxy == null || imageProxy.image == null) return


        val currenTimeStamp = System.currentTimeMillis()
        if (currenTimeStamp - lastTimeStampAnalized < TimeUnit.SECONDS.toMillis(1)) return

        lastTimeStampAnalized = currenTimeStamp


        if (!availableNetwork) {
            analizeDevice(imageProxy.image!!, rotationDegrees)
        } else {
            analizeDevice(imageProxy.image!!, rotationDegrees)
            // analizeCloud(imageProxy.image!!, rotationDegrees)
        }

    }

    private fun analizeCloud(image: Image, rotationDegrees: Int) {


        val options = FirebaseVisionCloudTextRecognizerOptions.Builder()
            .setLanguageHints(listOf("ru", "en")).build()


        var rotation = degreesToFireBaseRotarion(rotationDegrees)

        firebaseImage = FirebaseVisionImage.fromMediaImage(image, rotation)

        detector.processImage(firebaseImage!!)
            .addOnSuccessListener {
                for (block in it.textBlocks) {
                    for (line in block.lines) {
                        val lineFrame = line.boundingBox
                        val text = line.text

                        when (lifecycleOwner.lifecycle.currentState) {
                            Lifecycle.State.RESUMED -> textRecognizedCallback.onImageRecognized(
                                text,
                                lineFrame
                            )
                        }
                    }
                }
            }
            .addOnFailureListener {
                when (lifecycleOwner.lifecycle.currentState) {
                    Lifecycle.State.RESUMED -> textRecognizedCallback.onImageUnRecognized()
                }
            }


        Log.d("LearnAll", "Web analise")

    }


    private fun analizeDevice(image: Image, rotationDegrees: Int) {


        Log.d("LearnAll", "Device analise")


        var rotation = degreesToFireBaseRotarion(rotationDegrees)

        firebaseImage = FirebaseVisionImage.fromMediaImage(image, rotation)

        detector.processImage(firebaseImage!!)
            .addOnSuccessListener {

                for (block in it.textBlocks) {
                    for (line in block.lines) {
                        val lineFrame = line.boundingBox
                        val text = line.text

                        when (lifecycleOwner.lifecycle.currentState) {
                            Lifecycle.State.RESUMED -> textRecognizedCallback.onImageRecognized(
                                text,
                                lineFrame
                            )
                        }
                    }
                }

            }
            .addOnFailureListener {
                when (lifecycleOwner.lifecycle.currentState) {
                    Lifecycle.State.RESUMED -> textRecognizedCallback.onImageUnRecognized()
                }
            }
    }

    private fun degreesToFireBaseRotarion(degrees: Int): Int {
        return when (degrees) {
            0 -> FirebaseVisionImageMetadata.ROTATION_0
            90 -> FirebaseVisionImageMetadata.ROTATION_90
            180 -> FirebaseVisionImageMetadata.ROTATION_180
            270 -> FirebaseVisionImageMetadata.ROTATION_270
            else -> throw IllegalArgumentException(
                "Rotation must be 0, 90, 180, or 270."
            )
        }
    }


    fun setAvailableNetwork(isNetworkAvailable: Boolean) {
        availableNetwork = isNetworkAvailable

        when (isNetworkAvailable) {
            true -> detector = FirebaseVision.getInstance().onDeviceTextRecognizer
            else -> detector = FirebaseVision.getInstance().onDeviceTextRecognizer
        }

    }


}