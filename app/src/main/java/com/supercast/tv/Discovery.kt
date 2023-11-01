package com.supercast

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)

class Discovery(context: Context, private val callback: DiscoveryCallback) {
    val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    val serviceType = "_dinocast._tcp"
    val foundServices = mutableListOf<NsdServiceInfo>()

    fun start() {
        nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }
    fun stop() {
        nsdManager.stopServiceDiscovery(discoveryListener)
    }

    val discoveryListener = object : NsdManager.DiscoveryListener {
        override fun onDiscoveryStarted(serviceType: String) {
            Log.d("Discovery", "Discovery started for $serviceType")
        }

        override fun onServiceFound(serviceInfo: NsdServiceInfo) {
            Log.d("Discovery", "Service found: ${serviceInfo}")

            nsdManager.resolveService(serviceInfo, object : NsdManager.ResolveListener {
                override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                    Log.e("Discovery", "Resolve failed: $errorCode - ${serviceInfo}")
                }

                override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                    // The service is resolved
                    Log.d("Discovery", "Service resolved: ${serviceInfo}")
                    Log.d("Discovery", " - Name: ${serviceInfo.serviceName}")
                    Log.d("Discovery", " - Type: ${serviceInfo.serviceType}")
                    Log.d("Discovery", " - Host: ${serviceInfo.host}")
                    Log.d("Discovery", " - IP: ${serviceInfo.host.hostAddress}")
                    Log.d("Discovery", " - Port: ${serviceInfo.port}")
                    Log.d("Discovery", " - Attributes:")
                    serviceInfo.attributes.forEach { (key, value) ->
                        Log.d("Discovery", "   - $key: ${String(value)}")
                    }

                    callback.onServiceDiscovered(serviceInfo)

                }
            })
        }

        override fun onServiceLost(serviceInfo: NsdServiceInfo) {
            // A service is no longer available on the network
            Log.d("Discovery", "Service lost: ${serviceInfo.serviceName}")
            callback.onServiceLost(serviceInfo)
        }

        override fun onDiscoveryStopped(serviceType: String) {
            // The discovery process has stopped
            Log.d("Discovery", "Discovery stopped for $serviceType")
        }

        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            // The discovery process has failed
            Log.e("Discovery", "Discovery failed for $serviceType: $errorCode")
        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            // The discovery process has failed
            Log.e("Discovery", "Discovery failed for $serviceType: $errorCode")
        }
    }

}

interface DiscoveryCallback {
    fun onServiceDiscovered(serviceInfo: NsdServiceInfo)
    fun onServiceLost(serviceInfo: NsdServiceInfo)
}


