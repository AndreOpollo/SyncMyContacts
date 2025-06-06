package com.example.syncmycontacts.data.repository

import android.R.id.message
import android.content.ContentProvider
import android.content.ContentProviderOperation
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.ContactsContract
import android.provider.MediaStore
import com.example.syncmycontacts.data.model.Contact
import com.example.syncmycontacts.domain.ContactsRepository
import com.example.syncmycontacts.util.Resource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.apache.poi.hssf.usermodel.HSSFWorkbook
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

    override suspend fun restoreContactsFromJson(): Flow<Resource<List<Contact>>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val file = File(context.filesDir,"contacts_backup.json")
                if(!file.exists()){
                    emit(Resource.Error(message = "Backup Not Found"))
                } else {
                    val json = file.readText()
                    val contacts = Gson()
                        .fromJson(json,
                            Array<Contact>::class.java).toList()
                    contacts.forEach {
                        contact->
                        if(!contactExists(contact.name!!,contact.phone!!)){
                            insertContactToDevice(contact)
                        }
                    }
                    emit(Resource.Success(contacts))
                }

            }catch (e: Exception){
                e.printStackTrace()
                emit(Resource.Error(message = e.localizedMessage?:"Something went wrong"))

            }finally {
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun exportContactsAsXls(contacts: List<Contact>): Flow<Resource<Pair<Uri, String>>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val resolver = context.contentResolver
                val fileName = "contacts_export.xls"
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME,fileName)
                    put(MediaStore.Downloads.MIME_TYPE,"application/vnd.ms-excel")
                    put(MediaStore.Downloads.RELATIVE_PATH,
                        Environment.DIRECTORY_DOWNLOADS)
                    put(MediaStore.Downloads.IS_PENDING,1)
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    values)
                if(uri!=null){
                    resolver.openOutputStream(uri)?.use {
                        outputStream->
                        val workbook = HSSFWorkbook()
                        val sheet = workbook.createSheet("Contacts")
                        val header = sheet.createRow(0)
                        header.createCell(0).setCellValue("Name")
                        header.createCell(1).setCellValue("Phone")
                        contacts.forEachIndexed {
                            index,contact->
                            val row = sheet.createRow(index+1)
                            row.createCell(0).setCellValue(contact.name)
                            row.createCell(1).setCellValue(contact.phone)
                        }
                        workbook.write(outputStream)
                        workbook.close()
                    }
                    values.clear()
                    values.put(MediaStore.Downloads.IS_PENDING,0)
                    resolver.update(uri,values,null,null)
                    emit(Resource.Success(uri to "${contacts.size} contacts exported to XLS file"))
                }else{
                    emit(Resource.Error(message = "Failed to create XLS file"))
                }


            }catch (e: Exception){
                e.printStackTrace()
                emit(Resource.Error(message = e.localizedMessage?:"Something went wrong"))
            } finally {
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun exportContactsAsVcf(contacts: List<Contact>): Flow<Resource<Pair<Uri, String>>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val resolver = context.contentResolver
                val filename = "contacts_export.vcf"
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME,filename)
                    put(MediaStore.Downloads.MIME_TYPE,"text/x-vcard")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    put(MediaStore.Downloads.IS_PENDING,1)
                }
                val uri = resolver
                    .insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI,values)
                if(uri!=null){
                    resolver.openOutputStream(uri)?.use{
                        outputStream->
                        contacts.forEach {
                            contact->
                            val vcard = """
                                BEGIN:VCARD
                                VERSION:3.0
                                FN:${contact.name}
                                TEL:${contact.phone}
                                END:VCARD
                            """.trimIndent()
                            outputStream.write(vcard.toByteArray())
                        }
                    }
                    values.clear()
                    values.put(MediaStore.Downloads.IS_PENDING,0)
                    resolver.update(uri,values,null,null)
                    emit(Resource.Success(uri to "${contacts.size} contacts exported to VCF file"))
                }else{
                    emit(Resource.Error(message = "Failed to create VCF file"))
                }
            }catch (e: Exception){
                e.printStackTrace()
                emit(Resource.Error(message = e.localizedMessage?:"Something went wrong"))
            } finally {
                emit(Resource.Loading(false))
            }
        }
    }

    private fun insertContactToDevice(contact:Contact){
        try {
            val ops = ArrayList<ContentProviderOperation>()
            ops.add(
                ContentProviderOperation
                    .newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE,null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME,null)
                    .build()
            )
            ops.add(
                ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        contact.name)
                    .build()
            )
            ops.add(
                ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,contact.phone)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build()
            )
            context.contentResolver.applyBatch(ContactsContract.AUTHORITY,ops)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
    private fun contactExists(name:String,phone:String):Boolean{
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        val selection = "${ContactsContract
            .CommonDataKinds.Phone.DISPLAY_NAME}=? AND ${ContactsContract
                .CommonDataKinds.Phone.NUMBER}=?"
        val selectionArgs = arrayOf(name,phone)
        val cursor = context.contentResolver
            .query(uri, projection,selection,selectionArgs,null)
        cursor?.use {
            if(it.moveToFirst()){
                return true
            }
        }
        return false
    }
}