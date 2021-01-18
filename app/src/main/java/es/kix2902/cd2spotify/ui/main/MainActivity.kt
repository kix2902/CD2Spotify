package es.kix2902.cd2spotify.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.kix2902.cd2spotify.R
import es.kix2902.cd2spotify.databinding.ActivityMainBinding
import es.kix2902.cd2spotify.ui.qr.QrActivity
import net.openid.appauth.AuthorizationService


class MainActivity : AppCompatActivity() {

    companion object {
        private const val SPOTIFY_AUTH_REQUEST_CODE = 2345
    }

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    private val authService: AuthorizationService by lazy { AuthorizationService(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.spotifyInit.observe(this, { init ->
            if (init) {
                binding.connectContainer.isVisible = false
                binding.menuContainer.isVisible = true
            } else {
                binding.connectContainer.isVisible = true
                binding.menuContainer.isVisible = false
            }
        })

        binding.authorize.setOnClickListener {
            val authIntent = authService.getAuthorizationRequestIntent(viewModel.authRequest)
            startActivityForResult(authIntent, SPOTIFY_AUTH_REQUEST_CODE)
        }
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

    override fun onStart() {
        super.onStart()

        viewModel.checkSpotify()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SPOTIFY_AUTH_REQUEST_CODE) {
            viewModel.requestAccessToken(data!!, authService)
        }
    }
}