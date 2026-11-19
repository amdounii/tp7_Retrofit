package com.isetr.tp5app.ui.liste

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.isetr.tp5app.R
import com.isetr.tp5app.databinding.FragmentListeEtudiantsBinding
import com.isetr.tp5app.viewModel.EtudiantViewModel
import com.isetr.tp5app.viewModel.etudiantViewModelFactory

class ListeEtudiantsFragment : Fragment() {

    // ViewModel partagé, créé via la Factory (injection du Repository)
    private val etudiantViewModel: EtudiantViewModel by activityViewModels { etudiantViewModelFactory() }

    private var _binding: FragmentListeEtudiantsBinding? = null
    private val binding get() = _binding!!

    private lateinit var etudiantAdapter: EtudiantAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListeEtudiantsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillRecyclerView()
        observeLiveData()
        binding.btnRefresh.setOnClickListener { etudiantViewModel.getAllEtudiants() }
    }

    private fun fillRecyclerView() {
        binding.recyclerViewEtudiants.layoutManager = LinearLayoutManager(requireContext())
        etudiantAdapter = EtudiantAdapter(
            etudiants = mutableListOf(),
            onItemClicked = { etudiant ->
                etudiantViewModel.selectEtudiant(etudiant)
                findNavController().navigate(R.id.action_nav_liste_to_nav_detail)
            }
        )
        binding.recyclerViewEtudiants.adapter = etudiantAdapter
    }

    private fun observeLiveData() {
        // Observation des données (RecyclerView)
        etudiantViewModel.etudiants.observe(viewLifecycleOwner, Observer { etudiants ->
            etudiantAdapter.updateList(etudiants)
            // Si on a des données, on affiche la liste et on masque le texte d'erreur
            if (etudiants.isNotEmpty()) {
                binding.recyclerViewEtudiants.visibility = View.VISIBLE
                binding.textError.visibility = View.GONE
            } else if (etudiantViewModel.errorMessage.value == null) {
                // Si la liste est vide, afficher le message "Aucun étudiant trouvé"
                binding.textError.text = "Aucun étudiant trouvé."
                binding.textError.visibility = View.VISIBLE
                binding.recyclerViewEtudiants.visibility = View.GONE
            }
        })
        // Observation des messages d'erreur
        etudiantViewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMsg ->
            if (errorMsg != null) {
                binding.textError.text = errorMsg
                binding.textError.visibility = View.VISIBLE
                binding.recyclerViewEtudiants.visibility = View.GONE
            } else { // Masquer le texte d'erreur si l'erreur a été effacée
                binding.textError.visibility = View.GONE
            }
        })
        // Indicateur de chargement
        etudiantViewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
