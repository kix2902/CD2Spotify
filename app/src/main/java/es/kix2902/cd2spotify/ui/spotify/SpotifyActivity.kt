package es.kix2902.cd2spotify.ui.spotify

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import es.kix2902.cd2spotify.databinding.ActivitySpotifyBinding

class SpotifyActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_BARCODE = "barcode"
    }

    private lateinit var binding: ActivitySpotifyBinding

    private val viewModel: SpotifyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpotifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val barcode = intent.getStringExtra(EXTRA_BARCODE)!!
        viewModel.findBarcode(barcode)
    }
}