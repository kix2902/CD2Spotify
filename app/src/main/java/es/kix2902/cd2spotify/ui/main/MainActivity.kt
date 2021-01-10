package es.kix2902.cd2spotify.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import es.kix2902.cd2spotify.R
import es.kix2902.cd2spotify.databinding.ActivityMainBinding
import es.kix2902.cd2spotify.ui.error.ErrorActivity
import es.kix2902.cd2spotify.ui.qr.QrActivity


class MainActivity : AppCompatActivity() {

    companion object {
        private const val SPOTIFY_CLIENT_ID = "5bf1991a3f384cfa8c330b7174cb703a"
        private const val SPOTIFY_REDIRECT_URL = "cd2spotify://callback"
        private const val SPOTIFY_AUTH_REQUEST_CODE = 2345
    }

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

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
            val request = AuthorizationRequest
                .Builder(
                    SPOTIFY_CLIENT_ID,
                    AuthorizationResponse.Type.TOKEN,
                    SPOTIFY_REDIRECT_URL
                )
                .setScopes(
                    arrayOf(
                        "playlist-modify-public",
                        "user-follow-modify",
                        "user-follow-read",
                        "user-library-modify",
                        "user-read-private",
                        "user-library-read"
                    )
                )
                .setShowDialog(false)
                .build()
            AuthorizationClient.openLoginActivity(this, SPOTIFY_AUTH_REQUEST_CODE, request)
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
            val response = AuthorizationClient.getResponse(resultCode, data)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    viewModel.saveSpotifyToken(response.accessToken)
                }
                AuthorizationResponse.Type.ERROR -> {
                    val intent = Intent(this, ErrorActivity::class.java)
                    intent.putExtra(ErrorActivity.EXTRA_ERROR, ErrorActivity.ERROR_SPOTIFY_AUTH)
                    startActivity(intent)
                }
                else -> {
                }
            }
        }
    }
}