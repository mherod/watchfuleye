package com.example.app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.myunidays.watchfuleye.WatchfulEye

class LaunchActivity : AppCompatActivity(), WatchfulEye.Callbacks {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        WatchfulEye.registerCallbacks(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(findViewById(R.id.toolbar))

        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onActivityFromCold() {
        Log.d(javaClass.simpleName, "onActivityFromCold")
    }

    override fun onActivityFromColdOrBackground() {
        Log.d(javaClass.simpleName, "onActivityFromColdOrBackground")
    }

    override fun onActivityFromBackground() {
        Log.d(javaClass.simpleName, "onActivityFromBackground")
    }

    override fun onApplicationBackgrounded() {
        Log.d(javaClass.simpleName, "onApplicationBackgrounded")
    }
}