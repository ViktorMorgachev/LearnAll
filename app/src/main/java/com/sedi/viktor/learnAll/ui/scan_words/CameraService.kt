package com.sedi.viktor.learnAll.ui.scan_words

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager

class CameraService {
    private var cameraID: String
    private var cameraDevice: CameraDevice? = null
    private lateinit var cameraCaptureSession: CameraCaptureSession
    private var cameraManager: CameraManager
    private lateinit var cameraOpenedCallback: CameraOpenedCallback


    constructor(
        cameraID: String,
        cameraManager: CameraManager,
        cameraOpenedCallback: CameraOpenedCallback
    ) {
        this.cameraID = cameraID
        this.cameraManager = cameraManager
        this.cameraOpenedCallback = cameraOpenedCallback
    }

    fun isOpen(): Boolean {
        if (cameraDevice != null)
            return true
        return false
    }

    fun openCamera(context: Context) {
        if (context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            cameraManager.openCamera(cameraID, cameraStateCallback, null)
        }
    }

    fun closeCamera() {
        if (cameraDevice != null) {
            cameraDevice!!.close()
            cameraDevice = null
        }
    }


    private val cameraStateCallback = object : CameraDevice.StateCallback() {

        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            cameraOpenedCallback.createCameraPreviewSession()
        }

        override fun onDisconnected(camera: CameraDevice) {
            if (cameraDevice != null) {
                cameraDevice!!.close()
                cameraDevice = null
            }
        }

        override fun onError(camera: CameraDevice, error: Int) {
        }
    }


    interface CameraOpenedCallback {
        fun createCameraPreviewSession()
    }


}