package es.kix2902.cd2spotify.ui.history

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import es.kix2902.cd2spotify.data.models.Release
import es.kix2902.cd2spotify.databinding.ActivityHistoryBinding
import es.kix2902.cd2spotify.ui.spotify.SpotifyActivity

class HistoryActivity : AppCompatActivity() {

    private val viewModel: HistoryViewModel by viewModels()

    private lateinit var binding: ActivityHistoryBinding

    private val adapter = HistoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.adapter = adapter
        adapter.setOnClickListener(object : HistoryAdapter.OnClickListener {
            override fun onClick(item: Release) {
                val intent = Intent(this@HistoryActivity, SpotifyActivity::class.java)
                intent.putExtra(SpotifyActivity.EXTRA_BARCODE, item.barcode)
                startActivity(intent)
            }
        })

        viewModel.releases.observe(this, { releases ->
            adapter.setItems(releases)
        })
    }
}