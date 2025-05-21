package com.example.syncmycontacts.data.repository

import android.content.Context
import android.provider.ContactsContract
import com.example.syncmycontacts.data.model.Contact
import com.example.syncmycontacts.domain.ContactsRepository
import com.example.syncmycontacts.util.Resource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File

class ContactsRepositoryImpl(
    private val context: Context
): ContactsRepository {
    override suspend fun fetchContacts(): Flow<Resource<List<Contact>>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val contacts = mutableListOf<Contact>()
                val queryUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                val projection = arrayOf(
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                )
                val cursor = context.contentResolver.query(
                    queryUri,
                    projection,
                    null,
                    null,
                    "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
                )
                cursor?.use {
                        cursor->
                    val nameColumn = cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                    )
                    val phoneColumn = cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                    )
                    while(cursor.moveToNext()){
                        val name = cursor.getString(nameColumn)?:"Unknown"
                        val phone = cursor.getString(phoneColumn)?:"No number"
                        contacts.add(
                            Contact(
                                name = name,
                                phone = phone
                            )
                        )
                    }
                }
                emit(Resource.Success(data = contacts.filter {
                    !it.name.isNullOrBlank()
                }.distinctBy{it.name?.trim()?.lowercase()}))
                emit(Resource.Loading(false))

            }catch (e: Exception){
                e.printStackTrace()
                emit(Resource.Error(message = e.localizedMessage?:"Something went wrong"))
                emit(Resource.Loading(false))

            }
        }

    }

    override suspend fun backupContactsToJson(contacts: List<Contact>): Flow<Resource<Boolean>> {
       return flow{
           emit(Resource.Loading(true))
           try {
               val gson = Gson()
               val jsonString = gson.toJson(contacts)

               val file = File(context.filesDir,"contacts_backup.json")
               file.writeText(jsonString)
               emit(Resource.Success(true))
               emit(Resource.Loading(false))
           }   catch (e: Exception){
               e.printStackTrace()
               emit(Resource.Error(message = e.localizedMessage?:"Something went wrong"))
               emit(Resource.Loading(false))
           }
       }
    }
}