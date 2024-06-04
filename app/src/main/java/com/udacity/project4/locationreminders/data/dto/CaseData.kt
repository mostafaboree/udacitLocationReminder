package com.udacity.project4.locationreminders.data.dto

import java.lang.Exception


/* a failure with message and statusCode
 */
sealed class CaseData<out T : Any> {
    data class Success<out T : Any>(val data: T) : CaseData<T>()
    data class Error(val message: Exception, val statusCode: Int? = null) :
        CaseData<Nothing>()
}