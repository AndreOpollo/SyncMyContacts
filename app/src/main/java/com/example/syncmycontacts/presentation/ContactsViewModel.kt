package com.example.syncmycontacts.presentation


import android.util.Log.e
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncmycontacts.data.model.Contact
import com.example.syncmycontacts.domain.ContactsRepository
import com.example.syncmycontacts.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val contactsRepository: ContactsRepository
): ViewModel() {

    private val _contactsUiState = MutableStateFlow(ContactsUiState())
    val contactsUiState = _contactsUiState.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()
    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())

    val filteredContacts = _searchText.combine(
        _contacts
    ){text,contacts->
        if(text.isBlank()){
            contacts
        } else {
            contacts.filter {
                it.doesMatchSearchQuery(text)
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _contacts.value
    )


    fun onSearchTextChanged(text:String){
        _searchText.value = text
    }


    fun fetchContacts(){
        viewModelScope.launch {
            _contactsUiState.update {
                it.copy(
                    successMsg = null,
                    errorMsg = null,
                    isLoading = true
                )
            }
            contactsRepository.fetchContacts().collectLatest {
                result->
                when(result){
                    is Resource.Error<*> -> {
                        _contactsUiState.update {
                            it.copy(
                                isLoading = false,
                                errorMsg = result.message,
                                successMsg = null

                            )
                        }
                    }
                    is Resource.Loading<*> -> {
                        _contactsUiState.update {
                            it.copy(
                                isLoading = result.isLoading
                            )
                        }
                    }
                    is Resource.Success<*> -> {
                        val contacts = result.data?:emptyList()
                        _contacts.value = contacts
                        _contactsUiState.update {
                            it.copy(
                                isLoading = false,
                                errorMsg = null,
                                successMsg = "Loading contacts successfull",
                                contactList = contacts
                            )
                        }
                    }
                }
            }


        }
    }
    fun backupContactsToJson(){
        viewModelScope.launch {
            _contactsUiState.update {
                it.copy(
                    errorMsg = null,
                    isLoading = true
                )
            }
            contactsRepository
                .backupContactsToJson(_contactsUiState.value.contactList).collectLatest {
                    result->
                    when(result){
                        is Resource.Error<*> -> {
                            _contactsUiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMsg = result.message,
                                    contactsBackedUp = false
                                )
                            }
                        }
                        is Resource.Loading<*> -> {
                            _contactsUiState.update {
                                it.copy(
                                    isLoading = result.isLoading
                                )
                            }
                        }
                        is Resource.Success<*> -> {
                            _contactsUiState.update {
                                it.copy(
                                    isLoading = false,
                                    contactsBackedUp = result.data==true,
                                    errorMsg = null
                                )
                            }
                        }
                    }
                }
        }
    }
    fun restoreContactsFromJson(){
        viewModelScope.launch {
            _contactsUiState.update {
                it.copy(
                    successMsg = null,
                    errorMsg = null,
                    isLoading = true
                )
            }
            contactsRepository.restoreContactsFromJson().collectLatest {
                result->
                when(result){
                    is Resource.Error<*> -> {
                        _contactsUiState.update {
                            it.copy(
                                isLoading = false,
                                errorMsg = result.message,
                                contactsRestored = false
                            )
                        }
                    }
                    is Resource.Loading<*> -> {
                        _contactsUiState.update {
                            it.copy(
                                isLoading = result.isLoading
                            )
                        }
                    }
                    is Resource.Success<*> -> {
                        _contactsUiState.update{
                            it.copy(
                                isLoading = false,
                                contactsRestored = true,
                                contactList = result.data!!
                            )
                        }
                    }
                }
            }
        }
    }
    fun exportContactsAsXls(){
        viewModelScope.launch {
            _contactsUiState.update {
                it.copy(
                    isLoading = true,
                    exportSuccess = false
                )
            }
            contactsRepository
                .exportContactsAsXls(contactsUiState
                    .value
                    .contactList).collectLatest {
                        result->
                        when(result){
                            is Resource.Error<*> -> {
                                _contactsUiState.update {
                                    it.copy(
                                        isLoading = false,
                                        errorMsg = result.message
                                    )
                                }
                            }
                            is Resource.Loading<*> -> {
                                _contactsUiState.update {
                                    it.copy(
                                        isLoading = result.isLoading
                                    )
                                }
                            }
                            is Resource.Success<*> -> {
                                _contactsUiState.update {
                                    it.copy(
                                        isLoading = false,
                                        exportSuccess = true,
                                        exportedFileUri = result.data?.first,
                                        exportedFileSuccessMsg = result.data?.second
                                    )
                                }
                            }
                        }
                }
        }
    }
    fun exportContactsAsVcf(){
        viewModelScope.launch {
            _contactsUiState.update {
                it.copy(
                    isLoading = true,
                    errorMsg = null,
                    exportSuccess = false
                )
            }
            contactsRepository
                .exportContactsAsVcf(contactsUiState
                    .value
                    .contactList).collectLatest {
                        result->
                        when(result){
                            is Resource.Error<*> -> {
                                _contactsUiState.update {
                                    it.copy(
                                        isLoading = false,
                                        errorMsg = result.message
                                    )
                                }
                            }
                            is Resource.Loading<*> -> {
                                _contactsUiState.update {
                                    it.copy(
                                        isLoading = result.isLoading
                                    )
                                }
                            }
                            is Resource.Success<*> -> {
                                _contactsUiState.update {
                                    it.copy(
                                        isLoading = false,
                                        exportSuccess = true,
                                        exportedFileUri = result.data?.first,
                                        exportedFileSuccessMsg = result.data?.second
                                    )
                                }
                            }
                        }
                }
        }
    }
    fun resetContactsRestoredFlag(){
        viewModelScope.launch {
            _contactsUiState.update {
                it.copy(
                    contactsRestored = false
                )
            }
        }
    }
    fun resetContactsBackedUpFlag(){
        viewModelScope.launch {
            _contactsUiState.update {
                it.copy(
                    contactsBackedUp = false
                )
            }
        }
    }
    fun resetExportSuccessFlag(){
        viewModelScope.launch {
            _contactsUiState.update {
                it.copy(
                    exportSuccess = false
                )
            }
        }
    }
}