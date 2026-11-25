package com.example.core.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

/**
 * Centralized, crash-safe external browser launcher for KID'Z WORLD.
 */
object ExternalLinkOpener {
    private const val TAG = "ExternalLinkOpener"

    const val DEVELOPER_NAME = "Dev. E"
    const val DEVELOPER_WEBSITE_URL = "https://ebrahimjamansumon.blogspot.com"

    /**
     * Opens an http/https URL in the user's external browser.
     * Returns true when launched successfully, or false when no browser is available or URL is invalid.
     */
    fun openExternalUrl(context: Context, url: String): Boolean {
        val trimmed = url.trim()
        if (!isValidWebUrl(trimmed)) {
            Log.w(TAG, "Refused to open invalid URL: $trimmed")
            return false
        }
        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(trimmed)).apply {
                addCategory(Intent.CATEGORY_BROWSABLE)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            Log.w(TAG, "No external browser found to open $trimmed: ${e.message}")
            false
        } catch (e: Exception) {
            Log.w(TAG, "Unable to open URL $trimmed: ${e.message}")
            false
        }
    }

    fun isValidWebUrl(url: String): Boolean {
        val lower = url.trim().lowercase()
        return (lower.startsWith("https://") || lower.startsWith("http://")) && lower.length > 10
    }
}
