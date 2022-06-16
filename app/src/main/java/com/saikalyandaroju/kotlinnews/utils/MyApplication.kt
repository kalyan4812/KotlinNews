package com.saikalyandaroju.kotlinnews.utils

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp // it takes care of generation of appcomponent.
class MyApplication:Application() {
}