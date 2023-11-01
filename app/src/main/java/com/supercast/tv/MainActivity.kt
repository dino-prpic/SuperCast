package com.supercast.tv

import android.content.Intent
import android.net.nsd.NsdServiceInfo
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.supercast.Discovery
import com.supercast.DiscoveryCallback
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)


class MainActivity : FragmentActivity(), DiscoveryCallback {
    private lateinit var discoveredServicesLayout: LinearLayout
    private lateinit var discovery: Discovery

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showInput()
        discovery = Discovery(this, this)
        discovery.start()
        discoveredServicesLayout = findViewById(R.id.discovered_services)
    }

    // Implement the callback method to update the UI
    override fun onServiceDiscovered(serviceInfo: NsdServiceInfo) {
        runOnUiThread {
            val button = Button(this)
            button.text = serviceInfo.serviceName
            button.setOnClickListener {
                showWebActivity(serviceInfo.host.hostAddress, serviceInfo.port.toString())
            }

            val discoveredServicesLayout = findViewById<LinearLayout>(R.id.discovered_services)
            discoveredServicesLayout.addView(button)
        }
    }

    override fun onServiceLost(serviceInfo: NsdServiceInfo) {
        runOnUiThread {
            val discoveredServicesLayout = findViewById<LinearLayout>(R.id.discovered_services)
            for (i in 0 until discoveredServicesLayout.childCount) {
                val button = discoveredServicesLayout.getChildAt(i) as Button
                if (button.text == serviceInfo.serviceName) {
                    discoveredServicesLayout.removeView(button)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        discovery.stop()
    }

    private fun showInput() {
        setContentView(R.layout.main)
        val ipAddress = findViewById<EditText>(R.id.ip_address)
        val portNum = findViewById<EditText>(R.id.port)
        val connectBtn = findViewById<Button>(R.id.connect)
        connectBtn.setOnClickListener {
            val ip = ipAddress.text.toString()
            val port = portNum.text.toString()
            showWebActivity(ip, port)
        }
    }

    private fun showWebActivity(ip: String, port: String) {
        val intent = Intent(this, BrowserActivity::class.java)
        intent.putExtra("ip", ip)
        intent.putExtra("port", port)
        startActivity(intent)
    }
}
