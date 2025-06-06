package com.example.syncmycontacts.domain

import android.net.Uri
import com.example.syncmycontacts.data.model.Contact
import com.example.syncmycontacts.util.Resource
import kotlinx.coroutines.flow.Flow

interface ContactsRepository {
    suspend fun fetchContacts():Flow<Resource<List<Contact>>>
    suspend fun backupContactsToJson(contacts:List<Contact>):Flow<Resource<String>>
    suspend fun restoreContactsFromJson():Flow<Resource<String>>
    suspend fun exportContactsAsXls(contacts:List<Contact>):Flow<Resource<Pair<Uri,String>>>
    suspend fun exportContactsAsVcf(contacts:List<Contact>):Flow<Resource<Pair<Uri,String>>>
}