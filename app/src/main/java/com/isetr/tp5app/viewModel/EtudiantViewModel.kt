package com.isetr.tp5app.viewModel

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isetr.tp5app.EtudiantApplication
import com.isetr.tp5app.data.Etudiant
import com.isetr.tp5app.repository.EtudiantRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EtudiantViewModel(private val repository: EtudiantRepository) : ViewModel() {

    // Liste des étudiants
    private val _etudiants = MutableLiveData<MutableList<Etudiant>>()
    val etudiants: LiveData<MutableList<Etudiant>> = _etudiants

    // Message d'erreur lors de l'appel de l'API (null = pas d'erreur)
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Chargement en cours (affichage d'un ProgressBar)
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Étudiant sélectionné (fragment de détails)
    private val _selectedEtudiant = MutableLiveData<Etudiant?>()
    val selectedEtudiant: LiveData<Etudiant?> = _selectedEtudiant

    // Résultat de l'ajout : l'objet Etudiant créé en cas de succès, ou null
    private val _addResult = MutableLiveData<Etudiant?>()
    val addResult: LiveData<Etudiant?> = _addResult

    // Résultats de la mise à jour / suppression : true = succès, false = échec, null = rien
    private val _updateResult = MutableLiveData<Boolean?>()
    val updateResult: LiveData<Boolean?> = _updateResult

    private val _deleteResult = MutableLiveData<Boolean?>()
    val deleteResult: LiveData<Boolean?> = _deleteResult

    init {
        getAllEtudiants()
    }

    fun getAllEtudiants() {
        _errorMessage.postValue(null)
        _isLoading.postValue(true)
        // Lancer la coroutine pour le travail réseau sur le thread I/O
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getEtudiants()
                val errorMsg = "Erreur de serveur: ${response.code()}"
                if (response.isSuccessful && response.body() != null) {
                    val etudiantsList = response.body()!!
                    if (etudiantsList.isNotEmpty()) { // Succès avec données
                        Log.d("API_CALL", "liste étudiant trouvée.")
                        // Mettre à jour la liste dans le LiveData
                        _etudiants.postValue(etudiantsList.toMutableList())
                        _errorMessage.postValue(null) // S'assurer que le message d'erreur est masqué
                    } else { // Succès mais liste vide
                        Log.d("API_CALL", "Aucun étudiant trouvé.")
                        _etudiants.postValue(mutableListOf())
                        _errorMessage.postValue("Aucun étudiant trouvé.")
                    }
                } else { // Erreur HTTP ou serveur non disponible
                    _errorMessage.postValue(errorMsg)
                    _etudiants.postValue(mutableListOf())
                    Log.e("API_CALL", errorMsg)
                }
            } catch (e: Exception) { // Erreur réseau ou parsing
                val errorMsg = "Erreur de connexion. Vérifiez l'URL de l'API."
                _errorMessage.postValue(errorMsg)
                _etudiants.postValue(mutableListOf())
                Log.e("API_CALL", "Erreur réseau/parsing: ${e.message}", e)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    // Effectue l'appel API pour ajouter un nouvel étudiant.
    fun addNewEtudiant(newEtudiant: Etudiant) {
        _errorMessage.postValue(null)
        _addResult.postValue(null)
        viewModelScope.launch(Dispatchers.IO) {
            try { // Appel de la fonction addEtudiant via le repository
                val response = repository.addEtudiant(newEtudiant)
                if (response.isSuccessful && response.body() != null) { // Si l'ajout est réussi
                    val createdEtudiant = response.body()
                    _addResult.postValue(createdEtudiant)
                    // Après l'ajout, on rafraîchit la liste pour inclure le nouvel étudiant
                    getAllEtudiants()
                } else {
                    val errorMsg = "Erreur lors de l'ajout (Code: ${response.code()})."
                    _errorMessage.postValue(errorMsg)
                    _addResult.postValue(null)
                    Log.e("API_CALL", errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Erreur réseau lors de l'ajout."
                _errorMessage.postValue(errorMsg)
                _addResult.postValue(null)
                Log.e("API_CALL", "Erreur réseau/parsing (POST): ${e.message}", e)
            }
        }
    }

    // Sélection d'un étudiant depuis la liste, puis rechargement depuis l'API (GET api/etudiant/{cin})
    fun selectEtudiant(etudiant: Etudiant) {
        _selectedEtudiant.value = etudiant
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getEtudiant(etudiant.cin)
                if (response.isSuccessful && response.body() != null) {
                    _selectedEtudiant.postValue(response.body())
                }
            } catch (e: Exception) {
                // On garde les données venant de la liste si l'appel échoue
                Log.e("API_CALL", "Erreur GET étudiant ${etudiant.cin}: ${e.message}", e)
            }
        }
    }

    // Mise à jour d'un étudiant (PUT api/update)
    fun updateEtudiant(etudiant: Etudiant) {
        _updateResult.postValue(null)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.updateEtudiant(etudiant)
                if (response.isSuccessful) {
                    _selectedEtudiant.postValue(etudiant)
                    _updateResult.postValue(true)
                    getAllEtudiants() // rafraîchir la liste
                } else {
                    Log.e("API_CALL", "Erreur lors de la mise à jour (Code: ${response.code()}).")
                    _updateResult.postValue(false)
                }
            } catch (e: Exception) {
                Log.e("API_CALL", "Erreur réseau/parsing (PUT): ${e.message}", e)
                _updateResult.postValue(false)
            }
        }
    }

    // Suppression d'un étudiant (DELETE api/delete/{cin})
    fun deleteEtudiant(cin: Int) {
        _deleteResult.postValue(null)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.deleteEtudiant(cin)
                if (response.isSuccessful) {
                    _deleteResult.postValue(true)
                    getAllEtudiants() // rafraîchir la liste
                } else {
                    Log.e("API_CALL", "Erreur lors de la suppression (Code: ${response.code()}).")
                    _deleteResult.postValue(false)
                }
            } catch (e: Exception) {
                Log.e("API_CALL", "Erreur réseau/parsing (DELETE): ${e.message}", e)
                _deleteResult.postValue(false)
            }
        }
    }

    // Les résultats sont des "événements" : on les remet à null une fois traités
    // pour qu'ils ne soient pas rejoués quand on revient sur le fragment.
    fun resetAddResult() { _addResult.value = null }
    fun resetUpdateResult() { _updateResult.value = null }
    fun resetDeleteResult() { _deleteResult.value = null }
}

/** Factory partagée par tous les fragments : by activityViewModels { etudiantViewModelFactory() } */
fun Fragment.etudiantViewModelFactory(): EtudiantViewModelFactory =
    EtudiantViewModelFactory((requireActivity().application as EtudiantApplication).repository)
