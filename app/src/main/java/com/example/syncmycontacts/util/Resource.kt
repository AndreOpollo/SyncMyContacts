package com.example.syncmycontacts.util


sealed class Resource<T>(
    val data:T? =null,
    val message:String? =null
){
    class Success<T>(data:T): Resource<T>(data)
    class Error<T>(data:T?=null,message: String): Resource<T>(data,message=message)
    class Loading<T>(val isLoading:Boolean = false): Resource<T>(null)
}