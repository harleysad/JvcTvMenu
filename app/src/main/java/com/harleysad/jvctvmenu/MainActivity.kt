package com.harleysad.jvctvmenu

// import androidx.appcompat.app.AppCompatActivity

import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast


class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        val text = "aplicativo";
        val duration = Toast.LENGTH_LONG;
        val toast = Toast.makeText(applicationContext, text, duration);
        toast.show();
        finish()
    }
}

class AcessaMenu : AccessibilityService() {
    override fun onServiceConnected() {
        super.onServiceConnected()
        val text = "Serviço!"
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
    }

    val TAG = "RecorderService"

    override  fun  onAccessibilityEvent(event : AccessibilityEvent){}

    override fun onKeyEvent(event: KeyEvent): Boolean {
        val action: Int = event.getAction()
        val keyCode: Int = event.getKeyCode()
        Log.d(TAG, "acao $action -- codigo $keyCode")
        return false;
    }

    override fun onInterrupt() {
        val text = "JvcTvControl foi interrompido!"
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
    }
}