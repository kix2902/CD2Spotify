package es.kix2902.cd2spotify.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.kix2902.cd2spotify.BuildConfig
import es.kix2902.cd2spotify.R
import es.kix2902.cd2spotify.databinding.ActivityMainBinding
import es.kix2902.cd2spotify.ui.qr.QrActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.readBarcode.setOnClickListener {
            startActivity(Intent(this, QrActivity::class.java))
        }
        binding.showHistory.setOnClickListener {

        }
        binding.changeTheme.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.theme_dialog_title)
                .setItems(R.array.theme_entries) { dialog, which ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        when (which) {
                            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                            3 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        }
                    } else {
                        when (which) {
                            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        }
                    }
                }
                .show()
        }
    }
}