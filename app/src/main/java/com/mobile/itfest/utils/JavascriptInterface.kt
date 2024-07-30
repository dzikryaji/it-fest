package com.mobile.itfest.utils

import android.content.Context
import android.webkit.JavascriptInterface


class JavascriptInterface(
    context: Context,
    type: String,
    label: String?,
    data: String,
    backgroundColor: String,
    labelTitle: String
) {
    companion object {
        const val TAG_HANDLER = "Android"
    }
    private val mType = type
    private val mLabel = label
    private val mData = data
    private val mBackgroundColor = backgroundColor
    private val mLabelTitle = labelTitle


    @JavascriptInterface
    fun getType(): String {
        return mType
    }

    @JavascriptInterface
    fun getLabel(): String {
        return mLabel.toString()
    }

    @JavascriptInterface
    fun getData(): String {
        return mData
    }

    @JavascriptInterface
    fun getBackgroundColor(): String {
        return mBackgroundColor
    }

    @JavascriptInterface
    fun getLabelTitle(): String {
        return mLabelTitle
    }


}