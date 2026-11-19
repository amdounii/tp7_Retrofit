package com.isetr.tp5app.ui.ajout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.isetr.tp5app.R
import com.isetr.tp5app.data.Etudiant
import com.isetr.tp5app.databinding.FragmentAjoutBinding
import com.isetr.tp5app.viewModel.EtudiantViewModel
import com.isetr.tp5app.viewModel.etudiantViewModelFactory

class AjoutEtudiantFragment : Fragment() {

    private val etudiantViewModel: EtudiantViewModel by activityViewModels { etudiantViewModelFactory() }

    private var _binding: FragmentAjoutBinding? = null
    private val binding get() = _binding!!

    // true entre le clic sur "Enregistrer" et la réponse de l'API
    private var ajoutEnCours = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAjoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEnregistrer.setOnClickListener { ajouterEtudiant() }

        // Succès de l'ajout : message, on vide le formulaire et on navigue vers la liste
        etudiantViewModel.addResult.observe(viewLifecycleOwner) { etudiantCree ->
            if (etudiantCree != null && ajoutEnCours) {
                setEnCours(false)
                etudiantViewModel.resetAddResult()
                Toast.makeText(requireContext(), "Étudiant ${etudiantCree.nom.orEmpty()} ajouté avec succès", Toast.LENGTH_SHORT).show()
                viderChamps()
                findNavController().navigate(R.id.action_nav_ajout_to_nav_liste)
            }
        }

        // Échec de l'ajout : on affiche l'erreur renvoyée par le ViewModel
        etudiantViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg != null && ajoutEnCours) {
                setEnCours(false)
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun ajouterEtudiant() {
        // 1. Récupérer les données du formulaire
        val cin = binding.etCin.text.toString().trim().toIntOrNull()
        val nom = binding.etNom.text.toString().trim()
        val prenom = binding.etPrenom.text.toString().trim()
        val mail = binding.etMail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val classe = binding.etClasse.text.toString().trim()

        // Validation
        binding.tilCin.error = if (cin == null) "CIN numérique obligatoire" else null
        binding.tilNom.error = if (nom.isEmpty()) "Nom obligatoire" else null
        binding.tilPrenom.error = if (prenom.isEmpty()) "Prénom obligatoire" else null
        binding.tilMail.error = if (mail.isEmpty()) "Email obligatoire" else null
        binding.tilClasse.error = if (classe.isEmpty()) "Classe obligatoire" else null
        if (cin == null || nom.isEmpty() || prenom.isEmpty() || mail.isEmpty() || classe.isEmpty()) return

        // 2. Appeler l'API via le ViewModel
        val nouvelEtudiant = Etudiant(cin, nom, prenom, mail, password, classe)
        setEnCours(true)
        etudiantViewModel.addNewEtudiant(nouvelEtudiant)
    }

    private fun setEnCours(enCours: Boolean) {
        ajoutEnCours = enCours
        binding.btnEnregistrer.isEnabled = !enCours
        binding.progressAjout.visibility = if (enCours) View.VISIBLE else View.GONE
    }

    private fun viderChamps() {
        binding.etCin.text?.clear()
        binding.etNom.text?.clear()
        binding.etPrenom.text?.clear()
        binding.etMail.text?.clear()
        binding.etPassword.text?.clear()
        binding.etClasse.text?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
