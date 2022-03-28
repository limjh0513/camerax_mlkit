package kr.hs.dgsw.camera

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class PassActivity : AppCompatActivity() {
    override fun onBackPressed() {
        super.onBackPressed()

        goToMain()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass)

        val button = findViewById<Button>(R.id.backBtn)
        button.setOnClickListener {
            goToMain()
        }
    }

    private fun goToMain(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}