package kr.khs.oneboard.core.zoom

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kr.khs.oneboard.ui.SessionActivity

class IntegrationActivity : AppCompatActivity() {
    companion object {
        val ACTION_RETURN_TO_CONF = IntegrationActivity::class.java.name + ".action.RETURN_TO_CONF"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(
            Intent(this, SessionActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                action = ACTION_RETURN_TO_CONF
            }
        )
        finish()
    }
}