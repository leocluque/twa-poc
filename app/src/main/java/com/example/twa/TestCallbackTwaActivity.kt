package com.example.twa

import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.*
import java.util.*


class TestCallbackTwaActivity : AppCompatActivity(){
    private val TAG = "CustomTabs"

    private val URL: Uri = Uri.parse("https://ktor-twa.herokuapp.com/")

    private var mSession: CustomTabsSession? = null
    private var mConnection: CustomTabsServiceConnection? = null

    private var mLaunchButton: Button? = null
    private var mLogView: TextView? = null

    private val mLogs = StringBuilder()

    private fun appendToLog(log: String) {
        Log.d(TAG, log)
        mLogs.append(log)
        mLogs.append("\n")
        mLogView!!.text = mLogs.toString()
    }

    override fun onStart() {
        super.onStart()
        val callback: CustomTabsCallback = object : CustomTabsCallback() {
            override fun onNavigationEvent(navigationEvent: Int, @Nullable extras: Bundle?) {
                appendToLog(eventToString(navigationEvent) + ": " + bundleToString(extras))
            }

            override fun extraCallback(callbackName: String, @Nullable args: Bundle?) {
                appendToLog("Extra: " + callbackName + ": " + bundleToString(args))
            }
        }
        mConnection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(
                name: ComponentName,
                client: CustomTabsClient
            ) {
                mSession = client.newSession(callback)
                client.warmup(0)
                mLaunchButton?.isEnabled = true
            }

            override fun onServiceDisconnected(componentName: ComponentName) {}
        }
        val packageNames: List<String> = Arrays.asList(
            "com.google.android.apps.chrome",
            "com.chrome.canary",
            "com.chrome.dev",
            "com.chrome.beta",
            "com.android.chrome"
        )
        val packageName = CustomTabsClient.getPackageName(this@TestCallbackTwaActivity, packageNames, false)
        if (packageName == null) {
            Toast.makeText(this, "Can't find a Custom Tabs provider.", Toast.LENGTH_SHORT).show()
            return
        }
        CustomTabsClient.bindCustomTabsService(this, packageName, mConnection!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_callback)
        mLogView = findViewById(R.id.logs)
        mLaunchButton = findViewById(R.id.launch)
        mLaunchButton?.setOnClickListener {
            val intent = CustomTabsIntent.Builder(mSession).build()
            intent.launchUrl(this@TestCallbackTwaActivity, URL)
        }
    }

    override fun onStop() {
        super.onStop()
        if (mConnection == null) return
        unbindService(mConnection!!)
        mConnection = null
        mLaunchButton?.isEnabled = false
    }

    private fun eventToString(navigationEvent: Int): String {
        return when (navigationEvent) {
            CustomTabsCallback.NAVIGATION_STARTED -> "Navigation Started"
            CustomTabsCallback.NAVIGATION_FINISHED -> "Navigation Finished"
            CustomTabsCallback.NAVIGATION_FAILED -> "Navigation Failed"
            CustomTabsCallback.NAVIGATION_ABORTED -> "Navigation Aborted"
            CustomTabsCallback.TAB_SHOWN -> "Tab Shown"
            CustomTabsCallback.TAB_HIDDEN -> "Tab Hidden"
            else -> "Unknown Event"
        }
    }

    private fun bundleToString(bundle: Bundle?): String {
        val b = StringBuilder()
        b.append("{")
        if (bundle != null) {
            var first = true
            for (key in bundle.keySet()) {
                if (!first) {
                    b.append(", ")
                }
                first = false
                b.append(key)
                b.append(": ")
                b.append(bundle[key])
            }
        }
        b.append("}")
        return b.toString()
    }
}