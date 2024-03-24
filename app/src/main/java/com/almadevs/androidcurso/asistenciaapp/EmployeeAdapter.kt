package com.almadevs.androidcurso.asistenciaapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.almadevs.androidcurso.R

    class EmployeeAdapter(private val employeesList: Array<Employee>) :
        RecyclerView.Adapter<EmployeeAdapter.ViewHolder>() {

        private var onItemClickListener: ((Employee) -> Unit)? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_collection_employer_list, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val employee = employeesList[position]
            holder.bind(employee)
        }

        override fun getItemCount(): Int {
            return employeesList.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val idTextView: TextView = itemView.findViewById(R.id.textEmployerId)
            private val nameTextView: TextView = itemView.findViewById(R.id.textNameColaborador)
            private val statusTextView: TextView = itemView.findViewById(R.id.statusEmployer)

            fun bind(employee: Employee) {
                idTextView.text = employee.id_usuario
                nameTextView.text = employee.nombre_completo
                statusTextView.text = employee.getEstado()

                itemView.setOnClickListener {
                    onItemClickListener?.invoke(employee)
                }
            }

        }
        fun setOnItemClickListener(listener: (Employee) -> Unit) {
            this.onItemClickListener = listener
        }
    }

    class EmployeePuntoEncuentro(private val employeeList: List<Employee>) :
        RecyclerView.Adapter<EmployeePuntoEncuentro.EmployeeViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list_punto_encuentro, parent, false)
            return EmployeeViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
            val currentItem = employeeList[position]

            holder.textViewName.text = currentItem.nombre_completo
            holder.textViewPuntoEncuentro.text = currentItem.getPuntoEncuentro()
        }

        override fun getItemCount() = employeeList.size

        class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textViewName: TextView = itemView.findViewById(R.id.textNameEmpl)
            val textViewPuntoEncuentro: TextView = itemView.findViewById(R.id.punto_encuentro)
        }
    }


