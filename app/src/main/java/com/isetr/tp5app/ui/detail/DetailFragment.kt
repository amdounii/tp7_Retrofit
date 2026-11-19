package com.isetr.tp5app.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.isetr.tp5app.R
import com.isetr.tp5app.data.Etudiant
import com.isetr.tp5app.databinding.FragmentDetailBinding
import com.isetr.tp5app.viewModel.EtudiantViewModel
import com.isetr.tp5app.viewModel.etudiantViewModelFactory

class DetailFragment : Fragment() {

    private val etudiantViewModel: EtudiantViewModel by activityViewModels { etudiantViewModelFactory() }

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private var etudiantCourant: Etudiant? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Affichage des informations de l'étudiant sélectionné dans des champs modifiables
        etudiantViewModel.selectedEtudiant.observe(viewLifecycleOwner) { etudiant ->
            if (etudiant != null) {
                etudiantCourant = etudiant
                binding.tvTitre.text = "${etudiant.nom.orEmpty()} ${etudiant.prenom.orEmpty()}".trim()
                binding.etCin.setText(etudiant.cin.toString())
                binding.etNom.setText(etudiant.nom.orEmpty())
                binding.etPrenom.setText(etudiant.prenom.orEmpty())
                binding.etMail.setText(etudiant.mail.orEmpty())
                binding.etPassword.setText(etudiant.password.orEmpty())
                binding.etClasse.setText(etudiant.classe.orEmpty())
            }
        }

        binding.btnUpdate.setOnClickListener { mettreAJour() }
        binding.btnDelete.setOnClickListener { confirmerSuppression() }

        // Résultat de la mise à jour
        etudiantViewModel.updateResult.observe(viewLifecycleOwner) { succes ->
            if (succes == null) return@observe
            setEnCours(false)
            etudiantViewModel.resetUpdateResult()
            val msg = if (succes) "Étudiant mis à jour avec succès" else "Erreur lors de la mise à jour"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        // Résultat de la suppression : retour vers la liste
        etudiantViewModel.deleteResult.observe(viewLifecycleOwner) { succes ->
            if (succes == null) return@observe
            setEnCours(false)
            etudiantViewModel.resetDeleteResult()
            if (succes) {
                Toast.makeText(requireContext(), "Étudiant supprimé", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_nav_detail_to_nav_liste)
            } else {
                Toast.makeText(requireContext(), "Erreur lors de la suppression", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mettreAJour() {
        val courant = etudiantCourant ?: return
        val nom = binding.etNom.text.toString().trim()
        val prenom = binding.etPrenom.text.toString().trim()
        val mail = binding.etMail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val classe = binding.etClasse.text.toString().trim()

        binding.tilNom.error = if (nom.isEmpty()) "Nom obligatoire" else null
        binding.tilPrenom.error = if (prenom.isEmpty()) "Prénom obligatoire" else null
        binding.tilMail.error = if (mail.isEmpty()) "Email obligatoire" else null
        binding.tilClasse.error = if (classe.isEmpty()) "Classe obligatoire" else null
        if (nom.isEmpty() || prenom.isEmpty() || mail.isEmpty() || classe.isEmpty()) return

        val modifie = courant.copy(nom = nom, prenom = prenom, mail = mail, password = password, classe = classe)
        setEnCours(true)
        etudiantViewModel.updateEtudiant(modifie)
    }

    private fun confirmerSuppression() {
        val courant = etudiantCourant ?: return
        AlertDialog.Builder(requireContext())
            .setTitle("Suppression")
            .setMessage("Voulez-vous vraiment supprimer l'étudiant ${courant.nom.orEmpty()} (CIN ${courant.cin}) ?")
            .setPositiveButton("Supprimer") { _, _ ->
                setEnCours(true)
                etudiantViewModel.deleteEtudiant(courant.cin)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun setEnCours(enCours: Boolean) {
        binding.btnUpdate.isEnabled = !enCours
        binding.btnDelete.isEnabled = !enCours
        binding.progressDetail.visibility = if (enCours) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
