package com.almadevs.androidcurso.asistenciaapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.almadevs.androidcurso.R

    class EmployeePuntoEncuentro(private val employeeList: List<Employee>) :
        RecyclerView.Adapter<EmployeePuntoEncuentro.EmployeeViewHolder>() {

        private var onItemClickListener: ((Employee) -> Unit)? = null

        fun setOnItemClickListener(listener: (Employee) -> Unit) {
            this.onItemClickListener = listener
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list_punto_encuentro, parent, false)
            return EmployeeViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
            val currentItem = employeeList[position]

            holder.textViewName.text = currentItem.nombre_completo
            holder.textViewPuntoEncuentro.text = currentItem.getPuntoEncuentro()

            holder.itemView.setOnClickListener {
                onItemClickListener?.invoke(currentItem)
            }
        }

        override fun getItemCount() = employeeList.size

        class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textViewName: TextView = itemView.findViewById(R.id.textNomEmpl)
            val textViewPuntoEncuentro: TextView = itemView.findViewById(R.id.punto_encuentro)
        }
    }




