package com.sedi.viktor.learnAll.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.sedi.viktor.learnAll.ui.scan_words.CameraActivity


/**
 * Привязывается к одной активности, используя Lifecycle возвращает или не возвращает значения
 * Работает только с одной активностью
 * */
class NetworkReceiver(
    val netConnectivityCallback: CameraActivity.NetConnectivityCallback,
    val lifecycleOwner: LifecycleOwner
) : BroadcastReceiver() {

    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    override fun onReceive(context: Context?, intent: Intent?) {
        val conectivityManager =
            context!!.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequestBuilder = NetworkRequest.Builder()

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                    netConnectivityCallback.onFaillureNetConnected()
                }

            }

            override fun onAvailable(network: Network) {
                if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                    netConnectivityCallback.onSuccessNetConnected()
                }
            }
        }


        conectivityManager.registerNetworkCallback(networkRequestBuilder.build(), networkCallback)

        if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
            // Передаём в зависимости от значений либо есть интернет, либо нет
        }

        Toast.makeText(context, "Изменённо состояние сети", Toast.LENGTH_LONG).show()
    }


}