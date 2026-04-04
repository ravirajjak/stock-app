package com.indie.stockapp.core.resources

interface ResourceProvider {
    fun getString(resId: Int): String
}