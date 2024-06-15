package ru.mixail_akulov.a35_permissions

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AlertDialog
import ru.mixail_akulov.a35_permissions.databinding.ActivityMainBinding

/**
 * Example of requesting runtime permission with Activity Result API.
 */
class MainActivity : AppCompatActivity() {

    // вы можете использовать только 1 лаунчер, если логика обработки ответов одинакова для всех разрешений

    private val requestCameraPermissionLauncher = registerForActivityResult(
        RequestPermission(),    // контракт на запрос 1 разрешения
        ::onGotCameraPermissionResult
    )

    private val requestRecordAudioAndLocationPermissionsLauncher = registerForActivityResult(
        RequestMultiplePermissions(),   // контракт на запрос более 1 разрешения
        ::onGotRecordAudioAndLocationPermissionsResult
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.requestCameraPermissionButton.setOnClickListener {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.requestRecordAudioAndLocationPermissionsButton.setOnClickListener {
            requestRecordAudioAndLocationPermissionsLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO)
            )
        }

    }

    private fun onGotCameraPermissionResult(granted: Boolean) {
        if (granted) {
            onCameraPermissionGranted()
        } else {
            // пример обработки выбора пользователя «Отклонить и больше не спрашивать»
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                askUserForOpeningAppSettings()
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onGotRecordAudioAndLocationPermissionsResult(grantResults: Map<String, Boolean>) {
        if (grantResults.entries.all { it.value }) {
            onRecordAudioAndLocationPermissionsGranted()
        }
    }

    private fun askUserForOpeningAppSettings() {
        val appSettingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        if (packageManager.resolveActivity(appSettingsIntent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            Toast.makeText(this, R.string.permissions_denied_forever, Toast.LENGTH_SHORT).show()
        } else {
            AlertDialog.Builder(this)
                .setTitle(R.string.permission_denied)
                .setMessage(R.string.permission_denied_forever_message)
                .setPositiveButton(R.string.open) { _, _ ->
                    startActivity(appSettingsIntent)
                }
                .create()
                .show()
        }
    }

    private fun onRecordAudioAndLocationPermissionsGranted() {
        Toast.makeText(this, R.string.audio_and_location_permissions_granted, Toast.LENGTH_SHORT).show()
    }

    private fun onCameraPermissionGranted() {
        Toast.makeText(this, R.string.camera_permission_granted, Toast.LENGTH_SHORT).show()
    }

}