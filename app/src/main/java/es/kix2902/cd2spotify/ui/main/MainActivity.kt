package es.kix2902.cd2spotify.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
    }
}