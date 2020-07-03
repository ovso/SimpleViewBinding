package io.github.ovso.viewbinding

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.github.ovso.viewbinding.databinding.MainActivityBinding
import io.github.ovso.viewbinding.ui.main.MainFragment
import io.github.ovso.viewbinding.ui.main.viewBinding

class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(MainActivityBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnMain.setOnClickListener {
            Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show()
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }
}