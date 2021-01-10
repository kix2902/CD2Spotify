package es.kix2902.cd2spotify.ui.error

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import es.kix2902.cd2spotify.R
import es.kix2902.cd2spotify.databinding.ActivityErrorBinding

class ErrorActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ERROR = "error"
        private const val ERROR_INVALID = -1
        const val ERROR_PERMISSION = 0
        const val ERROR_INITIALIZATION = 1
        const val ERROR_READING = 2
        const val ERROR_SPOTIFY_AUTH = 3
    }

    private lateinit var binding: ActivityErrorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityErrorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when (intent.getIntExtra(EXTRA_ERROR, ERROR_INVALID)) {
            ERROR_PERMISSION -> binding.errorText.setText(R.string.error_permission)
            ERROR_INITIALIZATION -> binding.errorText.setText(R.string.error_initialization)
            ERROR_READING -> binding.errorText.setText(R.string.error_reading)
            ERROR_SPOTIFY_AUTH -> binding.errorText.setText(R.string.error_spotify_auth)
            ERROR_INVALID -> finish()
        }

        binding.close.setOnClickListener { finish() }
    }
}