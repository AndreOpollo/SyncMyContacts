package com.example.syncmycontacts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.syncmycontacts.presentation.ContactsViewModel
import com.example.syncmycontacts.presentation.screens.ContactsScreen
import com.example.syncmycontacts.ui.theme.SyncMyContactsTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SyncMyContactsTheme {
                val viewModel by viewModels<ContactsViewModel>()
                ContactsScreen(contactsViewModel = viewModel)
            }
        }
    }
}

