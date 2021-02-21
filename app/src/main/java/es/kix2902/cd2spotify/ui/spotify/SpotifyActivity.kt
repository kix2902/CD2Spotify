package es.kix2902.cd2spotify.ui.spotify

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import es.kix2902.cd2spotify.R
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

        viewModel.error.observe(this, {
            val intent = Intent(this, ErrorActivity::class.java)
            intent.putExtra(ErrorActivity.EXTRA_ERROR, it)
            startActivity(intent)
            finish()
        })
        viewModel.album.observe(this, { album ->
            if (album == null) {
                binding.loading.visibility = View.VISIBLE
                binding.albumInfo.visibility = View.GONE

            } else {
                binding.loading.visibility = View.GONE
                binding.albumInfo.visibility = View.VISIBLE

                Picasso.get().load(album.image).fit().centerInside().into(binding.albumImage)
                binding.albumTitle.text = album.title
                binding.albumArtist.text = album.artist.map { it.name }.joinToString()
                if (album.favorite) {
                    binding.albumFavorite.setImageResource(R.drawable.ic_favorite_filled)
                } else {
                    binding.albumFavorite.setImageResource(R.drawable.ic_favorite_outline)
                }
            }
        })
    }
}