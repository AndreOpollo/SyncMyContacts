package com.example.syncmycontacts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.syncmycontacts.presentation.screens.contacts.ContactsScreen
import com.example.syncmycontacts.presentation.screens.contacts.HomeScreen
import com.example.syncmycontacts.ui.theme.SyncMyContactsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SyncMyContactsTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ){ innerPadding->
                    HomeScreen(modifier = Modifier.padding(innerPadding))

                }
            }
        }
    }
}

