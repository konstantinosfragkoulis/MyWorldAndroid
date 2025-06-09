package com.konstantinos.myworld

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionManager {
    private val requiredPermissions = listOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.POST_NOTIFICATIONS,
        android.Manifest.permission.INTERNET,
    )

    fun hasPermissions(context: Context): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestPermissions(activity: Activity, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity,
            requiredPermissions.toTypedArray(),
            requestCode
        )
    }

    fun shouldShowRationale(activity: Activity): Boolean {
        return requiredPermissions.any {
            ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
        }
    }

    fun showRationale(activity: Activity, onProceed: () -> Unit) {
        AlertDialog.Builder(activity)
            .setTitle("Location Permission")
            .setMessage("This app requires location permissions to function properly.")
            .setPositiveButton("Grant") { _, _ -> onProceed() }
            .setNegativeButton("Deny") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}