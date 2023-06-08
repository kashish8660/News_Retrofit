package com.androiddevs.mvvmnewsapp.util

//We'll handle the response in ViewModel and send it to "View" as an object of these(Success, Error and Loading) classes
//so that "View" can react according to the type of Object Received

sealed class Resource<T>( //"T" will be initialised to be of "NewsResponse" data-type
    val data: T?=null, //means data is nullable and it's initial value is null
    val message : String?= null //"message" is for error message
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T?=null) : Resource<T>(data, message)
    class Loading<T>: Resource<T>()
}