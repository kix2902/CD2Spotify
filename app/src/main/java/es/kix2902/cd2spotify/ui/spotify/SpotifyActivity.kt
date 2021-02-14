package es.kix2902.cd2spotify.ui.spotify

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import es.kix2902.cd2spotify.databinding.ActivitySpotifyBinding
import es.kix2902.cd2spotify.ui.error.ErrorActivity

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

        viewModel.error.observe(this, Observer {
            val intent = Intent(this, ErrorActivity::class.java)
            intent.putExtra(ErrorActivity.EXTRA_ERROR, it)
            startActivity(intent)
            finish()
        })
    }
}