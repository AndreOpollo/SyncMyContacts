package com.example.syncmycontacts.presentation.screen

import android.Manifest
import android.R.attr.mimeType
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.syncmycontacts.presentation.ContactsViewModel
import com.example.syncmycontacts.presentation.components.ContactsList
import com.example.syncmycontacts.presentation.components.FormatOption
import com.example.syncmycontacts.presentation.components.FormatOptionsBottomSheet
import com.example.syncmycontacts.presentation.components.SpeedDialItem


@SuppressLint("QueryPermissionsNeeded")
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
            icon = Icons.Default.Backup,
            label = "Backup Contacts",
            onClick = {
                contactsViewModel.backupContactsToJson()
            }
        ),
        SpeedDialItem(
            icon = Icons.Default.Restore,
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
            icon = Icons.Default.FileUpload,
            label = "Export Contacts",
            onClick = {
                selectedAction = "Export Contacts"
            }
        ),
    )
    val sheetItems = listOf<FormatOption>(
        FormatOption(
            name = "XLS",
            icon = Icons.Default.TableChart,
            description = "Excel SpreadSheet",
            onClick = {
                selectedAction = null
                contactsViewModel.exportContactsAsXls()
            }
        ),
        FormatOption(
            name = "VCF",
            icon = Icons.Default.ContactPage,
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
        ContactsList()
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
                description = contactsUiState.value.restoreSuccessMsg?:
                "Contacts Restored Successfully",
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
                description = contactsUiState.value.backupSuccessMsg?:
                "Contacts Backed Up Successfully",
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
                        val mimeType =  context.contentResolver.getType(uri)
                        Log.d("FileOpen", "URI: $uri")
                        Log.d("FileOpen", "Detected MIME type: $mimeType")

                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri,
                                mimeType)
                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        if(intent.resolveActivity(context.packageManager)!=null){
                            context.startActivity(intent)
                        }else{
                            Toast.makeText(
                                context,
                                "No app found to open this type of file",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                },
                title = "Export Contacts",
                description = contactsUiState.value.exportedFileSuccessMsg?:
                "Contacts Exported Successfully",
                confirmButtonText = "Open File",
                dismissButton = true
            )
        }
        contactsUiState.value.errorMsg!=null->{
            Toast.makeText(
                context,
                "Something went wrong",
                Toast.LENGTH_LONG
            ).show()
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
    dismissButton:Boolean = false
){
        AlertDialog(
            onDismissRequest = { onDismiss() },
            dismissButton = {
                if(dismissButton){
                TextButton(onClick = onDismiss) {
                    Text(text = "Close")
                }
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
