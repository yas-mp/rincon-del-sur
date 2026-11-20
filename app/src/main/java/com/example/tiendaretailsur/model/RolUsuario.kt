package com.example.tiendaretailsur.model

enum class RolUsuario(val id: Long, val codigo: String, val nombre: String,val descripcion: String) {
    JEFA_TIENDA(1, "P-01","Jefa de Tienda", "Encargada de la Tienda"),
    SUBGERENTE(2, "P-02","Subgerente","Subgerente comercial "),
    SUPERVISOR(3, "P-03","Supervisor","Supervisor de piso"),
    OBSERVADOR(4, "P-05","Observador", "Observador de terreno ");

    companion object {
        fun buscarId(id: Long): RolUsuario {
            return entries.find { it.id == id } ?: OBSERVADOR
        }

        fun obtenerListaRoles() : List<RolUsuario> {
            return entries.toList()
        }

    }
}