package com.ragabuza.personalreminder.util

/**
 * Created by diego.moyses on 2/2/2018.
 */

class timeGet(val time: Long){
    fun milis(): Long{
        return time
    }
    fun seconds(): Long{
        return time * 1000
    }
    fun minutes(): Long{
        return time * 1000 * 60
    }
    fun hours(): Long{
        return time * 1000 * 60 * 60
    }
}
