package com.example.twa

import android.content.ComponentName
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Browser
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.browser.customtabs.*
import com.example.twa.databinding.ActivityMain2Binding
import com.example.twa.databinding.ActivityMainBinding
import java.util.*

class MainActivity2 : AppCompatActivity() {
    private val URL = Uri.parse("https://ktor-twa.herokuapp.com/")

    private var mSession: CustomTabsSession? = null
    private var mConnection: CustomTabsServiceConnection? = null


    private val binding: ActivityMain2Binding by lazy {
        ActivityMain2Binding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.openBtn.setOnClickListener {
            val intent = constructExtraHeadersIntent(mSession)
            intent.launchUrl(this@MainActivity2, URL)
        }
    }

    override fun onStart() {
        super.onStart()

        // Set up a callback that launches the intent after session was validated.
        val callback: CustomTabsCallback = object : CustomTabsCallback() {
            override fun onRelationshipValidationResult(
                relation: Int, requestedOrigin: Uri,
                result: Boolean, @Nullable extras: Bundle?
            ) {
                // Can launch custom tabs intent after session was validated as the same origin.
                binding.openBtn.isEnabled = true
            }
        }

        // Set up a connection that warms up and validates a session.
        mConnection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(
                name: ComponentName,
                client: CustomTabsClient
            ) {
                // Create session after service connected.
                mSession = client.newSession(callback)
                client.warmup(0)
                // Validate the session as the same origin to allow cross origin headers.
                mSession!!.validateRelationship(
                    CustomTabsService.RELATION_USE_AS_ORIGIN,
                    URL, null
                )
            }

            override fun onServiceDisconnected(componentName: ComponentName) {}
        }

        //Add package names for other browsers that support Custom Tabs and custom headers.
        val packageNames: List<String> = Arrays.asList(
            "com.google.android.apps.chrome",
            "com.chrome.canary",
            "com.chrome.dev",
            "com.chrome.beta",
            "com.android.chrome"
        )
        val packageName = CustomTabsClient.getPackageName(this@MainActivity2, packageNames, false)
        if (packageName == null) {
            Toast.makeText(applicationContext, "Package name is null.", Toast.LENGTH_SHORT)
                .show()
        } else {
            // Bind the custom tabs service connection.
            CustomTabsClient.bindCustomTabsService(
                this, packageName,
                mConnection as CustomTabsServiceConnection
            )
        }
    }

    override fun onStop() {
        super.onStop()

        // Unbind from the service if we connected successfully and clear the session.
        if (mSession != null) {
            unbindService(mConnection!!)
            mConnection = null
            mSession = null
        }
        binding.openBtn.isEnabled = false
    }

    private fun constructExtraHeadersIntent(session: CustomTabsSession?): CustomTabsIntent {
        val intent = CustomTabsIntent.Builder(session).build()

        // Example non-cors-whitelisted headers.
        val headers = Bundle()
        headers.putString("X-Custom-Header", "custom")
        intent.intent.putExtra(Browser.EXTRA_HEADERS, headers)

        return intent
    }
}