package com.harleysad.jvctvmenu

// import androidx.appcompat.app.AppCompatActivity

import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.content.Intent
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
        Log.d("RecorderService", "tela caraio ************")
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

    override  fun  onAccessibilityEvent(event: AccessibilityEvent){}

    private fun launchApp(packageName: String) {
        // Get an instance of PackageManager
        val pm = applicationContext.packageManager
        if (!pm.canRequestPackageInstalls())
            Log.d(TAG, "Não pode não pode ------")
        // Initialize a new Intent
        val intent:Intent? = pm.getLaunchIntentForPackage(packageName)

        // Add category to intent
        // intent?.addCategory(Intent.CATEGORY_LAUNCHER)

        // If intent is not null then launch the app
        if(intent!=null){
            applicationContext.startActivity(intent)
        }else{
            Log.d("RecorderService", "não achou")
        }
    }

    private fun makeKork() {
               val intent = Intent(android.provider.Settings.ACTION_SETTINGS)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        val action: Int = event.getAction()
        val keyCode: Int = event.getKeyCode()
        if (action  == 0 && keyCode == 251){
            try {
//                val className = "com.android.tv.settings"
//                launchApp(className)
                makeKork()
            } catch (err: Exception){
                Log.d(TAG, "DEU RIM !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + err.message)
            }
        }
        Log.d(TAG, "acao $action -- codigo $keyCode")
        return false;
    }

    @Throws(java.lang.Exception::class)
    private fun getActivityClass(target: String): Class<out Activity?>? {
        val classLoader: ClassLoader = getClassLoader()
        return classLoader.loadClass(target) as Class<out Activity?>
    }

    override fun onInterrupt() {
        val text = "JvcTvControl foi interrompido!"
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
    }
}