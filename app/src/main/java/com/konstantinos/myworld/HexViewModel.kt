package com.konstantinos.myworld

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.konstantinos.myworld.data.HexDatabase
import com.konstantinos.myworld.data.HexEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class HexViewModel(application: Application) : AndroidViewModel(application) {
    private val hexDao = HexDatabase.getDatabase(application).hexDao()
    val allHexes: Flow<List<HexEntity>> = hexDao.getAll().distinctUntilChanged()
    val hexCount: Flow<Long> = hexDao.getHexCount()
}