package com.isetr.tp5app

import android.app.Application
import com.isetr.tp5app.data.RetrofitInstance
import com.isetr.tp5app.repository.EtudiantRepository

/**
 * Classe Application : chef d'orchestre des singletons.
 * "by lazy" garantit une instanciation unique au premier appel.
 */
class EtudiantApplication : Application() {
    val repository: EtudiantRepository by lazy {
        EtudiantRepository(RetrofitInstance.api)
    }
}
