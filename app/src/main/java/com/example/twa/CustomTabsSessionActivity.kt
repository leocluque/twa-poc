package com.example.twa

import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection


class CustomTabsSessionActivity :AppCompatActivity(){

    private val URL: Uri = Uri.parse("https://ktor-twa.herokuapp.com/")
    private val UPDATED_URL: Uri = Uri.parse("https://ktor-twa.herokuapp.com/validateHeader")

    private var mSession: androidx.browser.customtabs.CustomTabsSession? = null
    private var mConnection: CustomTabsServiceConnection? = null

    private var mExtraButton: Button? = null

    protected override fun onStart() {
        super.onStart()
        mConnection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(
                name: ComponentName,
                client: CustomTabsClient
            ) {
                mSession = client.newSession(null)
                client.warmup(0)
                mExtraButton?.isEnabled = true
            }

            override fun onServiceDisconnected(componentName: ComponentName) {}
        }
        val packageName = CustomTabsClient.getPackageName(this@CustomTabsSessionActivity, null)
        if (packageName == null) {
            Toast.makeText(this, "Can't find a Custom Tabs provider.", Toast.LENGTH_SHORT).show()
            return
        }
        CustomTabsClient.bindCustomTabsService(this, packageName, mConnection!!)
    }

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabs_session)
        mExtraButton = findViewById(R.id.launch)
        mExtraButton?.setOnClickListener {
            val intent = CustomTabsIntent.Builder(mSession).build()
            intent.launchUrl(this@CustomTabsSessionActivity, URL)
            Handler(Looper.getMainLooper()).postDelayed({
                val updateIntent = CustomTabsIntent.Builder(mSession).build()
                updateIntent.launchUrl(this@CustomTabsSessionActivity, UPDATED_URL)
            }, 5000)
        }
    }

    protected override fun onStop() {
        super.onStop()
        if (mConnection == null) return
        unbindService(mConnection!!)
        mConnection = null
        mExtraButton?.isEnabled = false
    }
}