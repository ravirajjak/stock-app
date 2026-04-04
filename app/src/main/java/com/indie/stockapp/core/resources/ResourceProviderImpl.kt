package com.indie.stockapp.core.resources

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject

class ResourceProviderImpl @Inject constructor(
@ApplicationContext private val context: Context
) : ResourceProvider {

    override fun getString(resId: Int): String {
        return context.getString(resId)
    }
}