package com.example.syncmycontacts.domain

import com.example.syncmycontacts.data.model.Contact
import com.example.syncmycontacts.util.Resource
import kotlinx.coroutines.flow.Flow

interface ContactsRepository {
    suspend fun fetchContacts():Flow<Resource<List<Contact>>>
    suspend fun backupContactsToJson(contacts:List<Contact>):Flow<Resource<Boolean>>
    suspend fun restoreContactsFromJson():Flow<Resource<List<Contact>>>
    suspend fun exportContactsAsXls(contacts:List<Contact>):Flow<Resource<Boolean>>
    suspend fun exportContactsAsVcf(contacts:List<Contact>):Flow<Resource<Boolean>>
}