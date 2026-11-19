package com.isetr.tp5app.ui.liste

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.isetr.tp5app.data.Etudiant
import com.isetr.tp5app.databinding.ItemEtudBinding

class EtudiantAdapter(
    private val etudiants: MutableList<Etudiant>,
    private val onItemClicked: (Etudiant) -> Unit
) : RecyclerView.Adapter<EtudiantAdapter.EtudiantViewHolder>() {

    inner class EtudiantViewHolder(private val binding: ItemEtudBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(etudiant: Etudiant) {
            binding.tvNom.text = "${etudiant.nom.orEmpty()} ${etudiant.prenom.orEmpty()}".trim()
            binding.tvId.text = "CIN : ${etudiant.cin}"
            binding.tvClasse.text = "Classe : ${etudiant.classe.orEmpty()}"
            binding.tvMail.text = "Mail : ${etudiant.mail.orEmpty()}"
            binding.root.setOnClickListener { onItemClicked(etudiant) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EtudiantViewHolder {
        val binding = ItemEtudBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EtudiantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EtudiantViewHolder, position: Int) {
        holder.bind(etudiants[position])
    }

    override fun getItemCount() = etudiants.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Etudiant>) {
        etudiants.clear()
        etudiants.addAll(newList)
        notifyDataSetChanged()
    }
}
