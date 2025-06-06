package com.example.syncmycontacts.presentation.screens.contacts

import android.Manifest
import android.R.attr.description
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheetDefaults.properties
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.syncmycontacts.presentation.ContactsViewModel
import com.example.syncmycontacts.presentation.FormatOption
import com.example.syncmycontacts.presentation.FormatOptionsBottomSheet
import com.example.syncmycontacts.presentation.SpeedDialItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    contactsViewModel: ContactsViewModel = hiltViewModel<ContactsViewModel>()
){
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true)
    var selectedAction by remember { mutableStateOf<String?>(null) }
    val contactsUiState = contactsViewModel.contactsUiState.collectAsState()
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        isGranted->
        if(isGranted){
            contactsViewModel.restoreContactsFromJson()
        }
    }

    val items = listOf<SpeedDialItem>(
        SpeedDialItem(
            icon = Icons.Default.Call,
            label = "Backup Contacts",
            onClick = {
                contactsViewModel.backupContactsToJson()
            }
        ),
        SpeedDialItem(
            icon = Icons.Default.Call,
            label = "Restore Contacts",
            onClick = {
               if(ContextCompat.checkSelfPermission(context,
                       Manifest.permission.WRITE_CONTACTS)== PackageManager
                           .PERMISSION_GRANTED){
                   contactsViewModel.restoreContactsFromJson()
            }else{
                permissionLauncher.launch(
                    Manifest.permission.WRITE_CONTACTS
                )
               }

            }
        ),
        SpeedDialItem(
            icon = Icons.Default.Settings,
            label = "Export Contacts",
            onClick = {
                selectedAction = "Export Contacts"
            }
        ),
        SpeedDialItem(
            icon = Icons.Default.Search,
            label = "Import Contacts",
            onClick = {
                selectedAction = "Import Contacts"
            }
        ),

    )
    val sheetItems = listOf<FormatOption>(
        FormatOption(
            name = "XLS",
            icon = Icons.Default.DateRange,
            description = "Excel SpreadSheet",
            onClick = {
                selectedAction = null
                contactsViewModel.exportContactsAsXls()
            }
        ),
        FormatOption(
            name = "VCF",
            icon = Icons.Default.DateRange,
            description = "vCard Contact File",
            onClick = {
                selectedAction = null
                contactsViewModel.exportContactsAsVcf()
            }
        )
    )

    when{
        contactsUiState.value.isLoading->{
            Box(modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center){
                CircularProgressIndicator()
            }

        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ){
        ContactsScreen()
        SpeedDialItem(
            items = items,
            modifier = Modifier.fillMaxSize()
        )
        if(selectedAction!=null){
            FormatOptionsBottomSheet(
                title = selectedAction!!,
                formats = sheetItems,
                onDismiss = {selectedAction=null},
                sheetState = bottomSheetState
            )
        }
    }
    when{
        contactsUiState.value.contactsRestored->{
            CustomAlertDialog(
                onDismiss = {
                    contactsViewModel.resetContactsRestoredFlag()
                },
                onClickOk = {
                    contactsViewModel.fetchContacts()
                    contactsViewModel.resetContactsRestoredFlag()
                },
                title = "Restore Contacts",
                description = "Contacts Restored Successfully",
                confirmButtonText = "OK",
            )            
        }
        contactsUiState.value.contactsBackedUp->{
            CustomAlertDialog(
                onDismiss = {
                    contactsViewModel
                        .resetContactsBackedUpFlag()
                },
                onClickOk = {
                    contactsViewModel
                        .resetContactsBackedUpFlag()
                },
                title = "Backup Contacts",
                description = "Contacts Backed Up Successfully",
                confirmButtonText = "OK",
            )            
        }
        contactsUiState.value.exportSuccess->{
            CustomAlertDialog(
                onDismiss = {
                    contactsViewModel.resetExportSuccessFlag()
                },
                onClickOk = {
                    contactsViewModel.resetExportSuccessFlag()
                    contactsUiState.value.exportedFileUri?.let {
                        uri->
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri,
                                context.contentResolver.getType(uri))
                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    }

                },
                title = "Export Contacts",
                description = contactsUiState.value.exportedFileSuccessMsg?:
                "Contacts Exported Successfully",
                confirmButtonText = "Go to File",
            )
        }
    }
    
}

@Composable
fun CustomAlertDialog(
    onDismiss:()->Unit,
    onClickOk:()->Unit,
    title:String,
    description:String,
    confirmButtonText:String,
){


        AlertDialog(
            onDismissRequest = { onDismiss() },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = "Cancel")
                }
            },
            confirmButton = {
                TextButton(onClick = onClickOk) {
                    Text(text = confirmButtonText)
                }
            },
            title = {
                Text(text = title)
            },
            text = {
                Text(text = description)
            },

        )


}
