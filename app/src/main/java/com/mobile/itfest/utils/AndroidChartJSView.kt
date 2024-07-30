package com.mobile.itfest.utils

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.LinearLayout
import com.dewakoding.androidchartjs.model.BubbleEntity
import com.google.gson.Gson
import com.mobile.itfest.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class AndroidChartJSView  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {
    private val webView: WebView
    internal var jsi: JavascriptInterface? = null

    init {
        orientation = VERTICAL
        webView = WebView(context)
    }

    fun setChart(type: String,label: Array<String>, data: Array<Int>, backgroundColor: Array<String>, labelTitle: String) {
        jsi = JavascriptInterface(getContext(), type, Gson().toJson(label) , Gson().toJson(data), Gson().toJson(backgroundColor), labelTitle)
        render(jsi)
    }

    fun setBubbleChart(data: Array<BubbleEntity>, backgroundColor: Array<String>, labelTitle: String) {
        jsi = JavascriptInterface(context, "bubble", null, Gson().toJson(data), Gson().toJson(backgroundColor), labelTitle)
        render(jsi)
    }

    private fun render(jsi: JavascriptInterface?) {
        val webSettings: WebSettings = webView.getSettings()
        webSettings.javaScriptEnabled = true
        webView.addJavascriptInterface(jsi!!, JavascriptInterface.TAG_HANDLER)
        webView.setWebChromeClient(WebChromeClient())

        val rawResourceId = R.raw.chart2023
        val inputStream = resources.openRawResource(rawResourceId)
        val htmlData = convertStreamToString(inputStream)
        val baseUrl = "file:///android_res/raw/"
        val dataUri = Uri.parse(baseUrl + rawResourceId)


        webView.loadDataWithBaseURL(baseUrl, htmlData, "text/html", "UTF-8", null)

        removeView(webView)
        addView(webView)
    }

    private fun convertStreamToString(inputStream: InputStream): String {
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line: String?
        try {
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append("\n")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return stringBuilder.toString()
    }
}
