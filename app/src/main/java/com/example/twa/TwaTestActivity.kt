package com.example.twa

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Browser
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.*
import java.util.*


public class TwaTestActivity : AppCompatActivity() {
    private val WEB_URL = "https://google.com"

    @Nullable
    private var mServiceConnection: TwaCustomTabsServiceConnection? = null
    private var mTwaWasLaunched = false
    private var sChromeVersionChecked = false

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val packageNames: List<String> = Arrays.asList(
            "com.google.android.apps.chrome",
            "com.chrome.canary",
            "com.chrome.dev",
            "com.chrome.beta",
            "com.android.chrome"
        )
        val chromePackage = CustomTabsClient.getPackageName(this@TwaTestActivity, packageNames, false)
        if (chromePackage == null) {
//            TrustedWebUtils.showNoPackageToast(this)
            this.finish()
        } else {
            if (!sChromeVersionChecked) {
                if (chromeNeedsUpdate(this.getPackageManager(), chromePackage)) {
                    showToastIfResourceExists(this, "string/update_chrome_toast")
                    this.finish()
                    return
                }
                sChromeVersionChecked = true
            }
            if (savedInstanceState != null && savedInstanceState.getBoolean("android.support.customtabs.trusted.TWA_WAS_LAUNCHED_KEY")) {
                this.finish()
            } else {
                mServiceConnection = TwaCustomTabsServiceConnection(this)
                CustomTabsClient.bindCustomTabsService(this, chromePackage, mServiceConnection!!)
            }
        }
    }

    protected override fun onRestart() {
        super.onRestart()
        if (mTwaWasLaunched) {
            this.finish()
        }
    }

    protected override fun onDestroy() {
        super.onDestroy()
        if (mServiceConnection != null) {
            this.unbindService(mServiceConnection!!)
        }
    }

    protected override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(
            "android.support.customtabs.trusted.TWA_WAS_LAUNCHED_KEY",
            mTwaWasLaunched
        )
    }


    fun getSession(client: CustomTabsClient): CustomTabsSession? {
        return client.newSession(null as CustomTabsCallback?, 96375)
    }

    fun getCustomTabsIntent(session: CustomTabsSession?): CustomTabsIntent? {
        val builder = CustomTabsIntent.Builder(session)
        return builder.build()
    }

     fun getLaunchingUrl(): Uri? {
        var WebUrl = "https://ktor-twa.herokuapp.com/validateHeader"
        if (WebUrl == null || WebUrl.isEmpty()) {
            Log.d("Web Url: ", "is not set")
            WebUrl = WEB_URL
        }
        return Uri.parse(WebUrl)
    }


    private class TwaCustomTabsServiceConnection(val context: Context) : CustomTabsServiceConnection() {
        override fun onCustomTabsServiceConnected(
            componentName: ComponentName,
            client: CustomTabsClient
        ) {
            val session: CustomTabsSession = TwaTestActivity().getSession(client)!!
            val intent: CustomTabsIntent = TwaTestActivity().getCustomTabsIntent(session)!!
            val headers = Bundle()
            headers.putString("bearer-token", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJ1c2VycyIsImlzcyI6Imh0dHA6Ly8wLjAuMC4wOjgwODAiLCJleHAiOjE2OTY5NzYxNzYsInVzZXJJZCI6IjYzNDA0ZWY3ZmFhMWE5Nzc2N2Y5YzFjNSJ9.7Lvea81qGfU8O1xfkwYlZ4ch3nnUYJAEwwZHNj1zqEM")
            intent.intent.putExtra(Browser.EXTRA_HEADERS, headers)
            intent.intent.putExtras(headers)
            val url: Uri = TwaTestActivity().getLaunchingUrl()!!
            Log.d("TWALink", url.toString())
            // build twa
            TrustedWebUtils.launchAsTrustedWebActivity(context, intent, url)
            TwaTestActivity().mTwaWasLaunched = true
        }

        override fun onServiceDisconnected(componentName: ComponentName) {}
    }

    private fun chromeNeedsUpdate(pm: PackageManager, chromePackage: String): Boolean {
        try {
            val packageInfo = pm.getPackageInfo(chromePackage, 0)
            val firstDotIndex = packageInfo.versionName.indexOf(".")
            val majorVersion = packageInfo.versionName.substring(0, firstDotIndex)
            return majorVersion.toInt() < 72
        } catch (var3: PackageManager.NameNotFoundException) {
        }
        return false
    }

    private fun showToastIfResourceExists(context: Context, resource: String) {
        val stringId: Int = context.getResources()
            .getIdentifier(resource, null as String?, context.getPackageName())
        if (stringId != 0) {
            Toast.makeText(context, stringId, Toast.LENGTH_LONG).show()
        }
    }
}