package com.example.syncmycontacts.presentation.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log.v
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.syncmycontacts.presentation.ContactsViewModel

@Composable
fun ContactsScreen(
    contactsViewModel: ContactsViewModel
){
    val contactUiState = contactsViewModel.contactsUiState.collectAsState()
    var hasPermission by remember{mutableStateOf(false)}
    RequestContactsPermission {
        hasPermission = true
    }
    LaunchedEffect(hasPermission) {
        contactsViewModel.fetchContacts()
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ){
        items(contactUiState.value.contactList){
            contact->
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ){
                Text(text=contact.name?:"Unknown", style = MaterialTheme.typography.titleMedium)
                Text(text=contact.phone?:"Unknown", style = MaterialTheme.typography.titleMedium)
                HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
            }
        }

    }
}

@Composable
fun RequestContactsPermission(
    onPermissionGranted:()->Unit
){
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        isGranted->
        if(isGranted)onPermissionGranted()
    }
    LaunchedEffect(Unit) {
        if(ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CONTACTS)== PackageManager.PERMISSION_GRANTED
        ){
            onPermissionGranted()
        }else{
            permissionLauncher.launch(
                Manifest.permission.READ_CONTACTS
            )
        }
    }

}