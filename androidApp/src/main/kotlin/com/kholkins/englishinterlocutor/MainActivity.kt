package com.kholkins.englishinterlocutor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kholkins.englishinterlocutor.di.initKoin
import com.kholkins.englishinterlocutor.platform.CurrentActivityHolder
import org.koin.android.ext.koin.androidContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        CurrentActivityHolder.set(this)

        initKoin {
            androidContext(this@MainActivity)
        }

        setContent {
            App()
        }
    }

    override fun onResume() {
        super.onResume()
        CurrentActivityHolder.set(this)
    }

    override fun onPause() {
        CurrentActivityHolder.set(null)
        super.onPause()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}