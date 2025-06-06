package com.example.syncmycontacts.presentation

import android.net.Uri
import com.example.syncmycontacts.data.model.Contact

data class ContactsUiState(
    val successMsg:String? = null,
    val errorMsg:String? = null,
    val contactList: List<Contact> = emptyList<Contact>(),
    val isLoading: Boolean = false,
    val contactsRestored:Boolean = false,
    val contactsBackedUp:Boolean = false,
    val exportSuccess:Boolean = false,
    val importSuccess:Boolean = false,
    val exportedFileUri:Uri? = null,
    val exportedFileSuccessMsg:String?=null,
    val backupSuccessMsg:String?=null,
    val restoreSuccessMsg:String?=null
)