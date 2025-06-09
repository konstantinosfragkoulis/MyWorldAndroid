package com.konstantinos.myworld

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.konstantinos.myworld.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.activity.viewModels
import com.konstantinos.myworld.data.HexDAO
import com.konstantinos.myworld.data.HexDatabase
import com.konstantinos.myworld.data.HexEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.Flow
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: HexAdapter
    private lateinit var hexDao: HexDAO
    private val viewModel: HexViewModel by viewModels()

    private val createFileLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/bin")
    ) { uri: Uri? ->
        uri?.let {
            lifecycleScope.launch {
                val hexes = viewModel.allHexes.first()
                exportHexesToUri(uri, hexes)
            }
        }

    }

    private val importLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            importHexesFromUri(this, it, hexDao)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = HexAdapter()
        binding.hexView.layoutManager = LinearLayoutManager(this)
        binding.hexView.adapter = adapter
        hexDao = HexDatabase.getDatabase(applicationContext).hexDao()

        lifecycleScope.launch {
            viewModel.allHexes.collectLatest { hexList ->
                adapter.submitList(hexList)
            }
        }

        checkAndRequestPermissions()

        binding.startLocation.setOnClickListener {
            if(PermissionManager.hasPermissions(this)) {
                Intent(applicationContext, LocationService::class.java).apply {
                    action = LocationService.ACTION_START
                    startService(this)
                }
            } else {
                checkAndRequestPermissions()
            }
        }
        binding.stopLocation.setOnClickListener {
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                startService(this)
            }
        }
        binding.importHexagons.setOnClickListener {
            importLauncher.launch(arrayOf("*/*"))
        }

        binding.exportHexagons.setOnClickListener {
            createExportFile()
        }
    }

    private fun createExportFile() {
        val timestamp = java.time.LocalDateTime.now().toString().replace(":", "-")
        val defaultFileName = "export - $timestamp.mywrld"
        createFileLauncher.launch(defaultFileName)
    }

    private fun importHexesFromUri(context: Context, uri: Uri, hexDAO: HexDAO) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                context.contentResolver.openInputStream(uri)?.bufferedReader()?.useLines { lines ->
                    lines.forEach { line ->
                        val hex = line.trim().toLongOrNull()
                        if (hex != null && !hexDao.isExplored(hex)) {
                            hexDAO.insertHex(HexEntity(hex))
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun exportHexesToUri(uri: Uri, hexes: List<HexEntity>) {
        try {
            contentResolver.openOutputStream(uri)?.bufferedWriter().use { writer ->
                hexes.forEach { writer?.write("${it.hex}\n") }
        }
            Toast.makeText(this, "Export successful", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun checkAndRequestPermissions() {
        if(!PermissionManager.hasPermissions(this)) {
            if(PermissionManager.shouldShowRationale(this)) {
                PermissionManager.showRationale(this) {
                    PermissionManager.requestPermissions(this, PERMISSION_REQUEST_CODE)
                }
            } else {
                PermissionManager.requestPermissions(this, PERMISSION_REQUEST_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)

        if(requestCode == PERMISSION_REQUEST_CODE) {
            if(grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions required", Toast.LENGTH_LONG).show()
            }
        }
    }
    /**
     * A native method that is implemented by the 'myworld' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'myworld' library on application startup.
        private const val PERMISSION_REQUEST_CODE = 1001
        init {
            System.loadLibrary("myworld")
        }
    }
}