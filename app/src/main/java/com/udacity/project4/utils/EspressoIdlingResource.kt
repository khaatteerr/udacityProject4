package com.udacity.project4.utils

import androidx.test.espresso.idling.CountingIdlingResource
import com.udacity.project4.utils.Constants.RESOURCES

object EspressoIdlingResource {

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCES)

    fun increment() {
        countingIdlingResource.increment()
    }//end increment()

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }//end if()
    }//end decrement()
}//end object

inline fun <T> wrapEspressoIdlingResource(function: () -> T): T {
    EspressoIdlingResource.increment() // Set app as busy.
    return try {
        function()
    } finally {
        EspressoIdlingResource.decrement() // Set app as idle.
    }//end finally
}//end wrapEspressoIdlingResource()