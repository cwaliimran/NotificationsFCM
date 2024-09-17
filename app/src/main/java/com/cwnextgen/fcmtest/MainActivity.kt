package com.cwnextgen.fcmtest

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    var tvToken: TextView? = null
    var tvPayload: TextView? = null
    var button: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvToken = findViewById(R.id.tvToken)
        tvPayload = findViewById(R.id.tvPayload)
        button = findViewById(R.id.button)

        button?.setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("token", tvToken?.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Token copied", Toast.LENGTH_SHORT).show()
        }


        // Handle the intent if the app is opened from a notification
        handleIntent(intent)

        getMessagingToken()
        askNotificationPermission()

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle the intent if the app is already running
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        intent?.extras?.let {
            val title = it.getString("title", "")
            val body = it.getString("body", "")
            val dataPayload = it.getString("dataPayload", "")

            // Display the payload in the TextView
            tvPayload?.text = "Title: $title\nBody: $body\nData Payload: $dataPayload"
        }
    }

    private fun getMessagingToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d(TAG, "getMessagingToken: not success")
                tvToken?.text = task.exception.toString()
            } else {
                val fcmToken = task.result
                tvToken?.text = fcmToken
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission granted
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // Show rationale
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted
        } else {
            // Permission denied
        }
    }
}
