package com.example.tiendaretailsur.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.tiendaretailsur.model.AccesoTienda
import com.example.tiendaretailsur.model.ConfiguracionPerfil
import com.example.tiendaretailsur.model.DatoGrafico
import com.example.tiendaretailsur.model.FormularioConteoUiState
import com.example.tiendaretailsur.model.FormularioRegistroUiState
import com.example.tiendaretailsur.model.HashUtils
import com.example.tiendaretailsur.model.SolicitudCorreo
import com.example.tiendaretailsur.model.Usuario

class UsuarioDbHelper(context: Context)
    : SQLiteOpenHelper(context, NOMBRE_BD,null, VERSION_DB){

    companion object {
        private const val NOMBRE_BD = "tiendaretailsur.db"
        private const val VERSION_DB = 16

        private const val TABLA_ROLES = "roles"
        private const val ID_ROL = "id_rol"
        private const val CODIGO_ROL = "codigo_rol"
        private const val NOMBRE_ROL = "nombre_rol"
        private const val DESCRIPCION_ROL = "descripcion_rol"

        private const val TABLA_USUARIO = "usuario"
        private const val ID_USUARIO = "id_usuario"
        private const val RUT_USUARIO = "rut_usuario"
        private const val NOMBRE_USUARIO = "nombre_usuario"
        private const val CORREO_USUARIO = "correo_usuario"
        private const val TELEFONO = "telefono"
        private const val CONTRASENA = "contrasena"
        private const val DEBE_CAMBIAR_CONTRASENA = "debe_cambiar_contrasena"
        private const val ROL_USUARIO = "id_rol"
        private const val PREGUNTA_SEG =  "pregunta_seguridad"
        private const val RESPUESTA_SEG = "respuesta_seguridad"
        private const val AVATAR_USUARIO = "avatar"
        private const val TURNO_USUARIO = "turno"
        private const val ACTIVO_USUARIO = "activo"


        private const val TABLA_PUERTAS = "puertas"
        private const val ID_PUERTA = "id_puerta"
        private const val NOMBRE_PUERTA = "nombre_puerta"
        private const val DESCRIPCION_PUERTA = "descripcion_puerta"
        private const val HABILITADA_PUERTA = "habilitada"

        private const val TABLA_CONTEO = "formulario_conteo"
        private const val ID_CONTEO = "id_conteo"
        private const val PUERTA_ACCESO = "puerta_acceso"
        private const val CANT_TRANSITO = "cantidad_transito"
        private const val CANT_CLIENTES = "cantidad_clientes"
        private const val RANGO_ETARIO = "rango_etario"
        private const val COMPANIA = "compania"
        private const val OBSERVADOR = "observador"
        private const val HORA = "hora"
        private const val ID_USUARIO_FK = "id_usuario"

        private const val TABLA_SOLICITUDES_CORREO = "solicitudes_correo"
        private const val ID_SOLICITUD = "id_solicitud"
        private const val SOL_RUT = "rut_usuario"
        private const val SOL_NOMBRE = "nombre_usuario"
        private const val SOL_CORREO_ACTUAL = "correo_actual"
        private const val SOL_CORREO_NUEVO = "correo_nuevo"
        private const val SOL_MOTIVO = "motivo"
        private const val SOL_ESTADO = "estado"

        private const val SQL_CREAR_ROLES =
            "CREATE TABLE $TABLA_ROLES (" +
                    "$ID_ROL INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$NOMBRE_ROL TEXT NOT NULL UNIQUE, " +
                    "$CODIGO_ROL TEXT NOT NULL UNIQUE, " +
                    "$DESCRIPCION_ROL TEXT NOT NULL) "

        private const val SQL_CREAR_PUERTAS =
            "CREATE TABLE $TABLA_PUERTAS (" +
                    "$ID_PUERTA TEXT PRIMARY KEY, " +
                    "$NOMBRE_PUERTA TEXT NOT NULL, " +
                    "$DESCRIPCION_PUERTA TEXT NOT NULL, " +
                    "$HABILITADA_PUERTA INTEGER NOT NULL DEFAULT 1)"

        private const val SQL_CREAR_USUARIOS =
            "CREATE TABLE $TABLA_USUARIO (" +
                    "$ID_USUARIO INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$RUT_USUARIO TEXT NOT NULL UNIQUE, " +
                    "$NOMBRE_USUARIO TEXT NOT NULL, " +
                    "$CORREO_USUARIO TEXT NOT NULL, " +
                    "$TELEFONO TEXT, " +
                    "$CONTRASENA TEXT NOT NULL, " +
                    "$PREGUNTA_SEG TEXT, " +
                    "$RESPUESTA_SEG TEXT, " +
                    "$AVATAR_USUARIO BLOB, " +
                    "$TURNO_USUARIO TEXT DEFAULT 'N/A', " +
                    "$DEBE_CAMBIAR_CONTRASENA INTEGER NOT NULL DEFAULT 0, " +
                    "$ACTIVO_USUARIO INTEGER NOT NULL DEFAULT 1, " +
                    "$ROL_USUARIO INTEGER NOT NULL REFERENCES $TABLA_ROLES($ID_ROL))"

        private const val SQL_CREAR_CONTEOS =
            "CREATE TABLE $TABLA_CONTEO (" +
                    "$ID_CONTEO INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$PUERTA_ACCESO TEXT NOT NULL, " +
                    "$CANT_TRANSITO INTEGER NOT NULL, " +
                    "$CANT_CLIENTES INTEGER NOT NULL, " +
                    "$RANGO_ETARIO TEXT NOT NULL, " +
                    "$COMPANIA TEXT NOT NULL, " +
                    "$OBSERVADOR TEXT NOT NULL, " +
                    "$HORA TEXT NOT NULL, " +
                    "$ID_USUARIO_FK INTEGER REFERENCES $TABLA_USUARIO($ID_USUARIO))"

        private const val SQL_CREAR_SOLICITUDES_CORREO =
            "CREATE TABLE $TABLA_SOLICITUDES_CORREO (" +
                    "$ID_SOLICITUD INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$SOL_RUT TEXT NOT NULL, " +
                    "$SOL_NOMBRE TEXT NOT NULL, " +
                    "$SOL_CORREO_ACTUAL TEXT NOT NULL, " +
                    "$SOL_CORREO_NUEVO TEXT NOT NULL, " +
                    "$SOL_MOTIVO TEXT NOT NULL, " +
                    "$SOL_ESTADO TEXT NOT NULL DEFAULT 'PENDIENTE')"

        private const val SQL_AGREGAR_AVATAR =
            "ALTER TABLE $TABLA_USUARIO ADD COLUMN $AVATAR_USUARIO BLOB"

        private const val SQL_PRECARGAR_ROLES =
        "INSERT INTO $TABLA_ROLES ($ID_ROL,$CODIGO_ROL, $NOMBRE_ROL, $DESCRIPCION_ROL) VALUES " +
                "(1, 'P-01', 'JEFA_TIENDA', 'Encargada de la Tienda'), " +
                "(2, 'P-02', 'SUBGERENTE', 'Subgerente comercial'), " +
                "(3, 'P-03', 'SUPERVISOR', 'Supervisor de piso'), " +
                "(4, 'P-05', 'OBSERVADOR', 'Observador de terreno')"

        private val SQL_PRECARGAR_USUARIOS by lazy {
            val claveHash = HashUtils.generarHash("1234")
            val respPerroHash = HashUtils.generarHashRespuesta("perro")
            val respGatoHash = HashUtils.generarHashRespuesta("gato")

            "INSERT INTO $TABLA_USUARIO ($RUT_USUARIO, $NOMBRE_USUARIO, $CORREO_USUARIO, $TELEFONO, $CONTRASENA, $TURNO_USUARIO, $PREGUNTA_SEG, $RESPUESTA_SEG, $ROL_USUARIO) VALUES " +
                    "('11111111-1', 'Carolina Méndez', 'jefa@rincondelsur.cl', '911111111', '$claveHash', 'N/A', '¿Cuál es el nombre de tu primera mascota?', '$respPerroHash', 1), " +
                    "('12345678-9', 'Gabriel Gómez', 'admin@rincondelsur.cl', '912345678', '$claveHash', 'N/A', '¿Cuál es el nombre de tu primera mascota?', '$respPerroHash', 2), " +
                    "('15555666-7', 'Daniela Navarro', 'supervisora@rincondelsur.cl', '955556666', '$claveHash', 'Turno Diurno', '¿Cuál es el nombre de tu primera mascota?', '$respGatoHash', 3), " +
                    "('98765432-1', 'Juan Pérez', 'juan@rincondelsur.cl', '987654321', '$claveHash', 'Turno Diurno', '¿Cuál es el nombre de tu primera mascota?', '$respGatoHash', 4), " +
                    "('18111222-3', 'Camila Soto', 'c.soto@rincondelsur.cl', '911223344', '$claveHash', 'Turno Vespertino', '¿Cuál es el nombre de tu primera mascota?', '$respPerroHash', 4), " +
                    "('19222333-4', 'Mateo Silva', 'm.silva@rincondelsur.cl', '955667788', '$claveHash', 'Turno Diurno', '¿Cuál es el nombre de tu primera mascota?', '$respGatoHash', 4), " +
                    "('17333444-5', 'Valentina Rojas', 'v.rojas@rincondelsur.cl', '999887766', '$claveHash', 'Turno Vespertino', '¿Cuál es el nombre de tu primera mascota?', '$respPerroHash', 4)"
        }
        private const val SQL_PRECARGAR_PUERTAS =
            "INSERT INTO $TABLA_PUERTAS ($ID_PUERTA, $NOMBRE_PUERTA, $DESCRIPCION_PUERTA, $HABILITADA_PUERTA) VALUES " +
                    "('A1', 'Acceso Principal', 'Patio de comidas del mall / Pasillo central', 1), " +
                    "('A2', 'Acceso Lateral', 'Pasillo del mall, lado norte', 1), " +
                    "('A3', 'Acceso Estacionamiento', 'Estacionamiento subterráneo (señal de red débil)', 1), " +
                    "('A4', 'Acceso Conexión', 'Conexión directa con tienda vecina de mejoramiento del hogar', 1)"

        private const val SQL_PRECARGAR_CONTEOS =
            "INSERT INTO $TABLA_CONTEO (" +
                    "$PUERTA_ACCESO, $CANT_TRANSITO, $CANT_CLIENTES, $RANGO_ETARIO, $COMPANIA, $OBSERVADOR, $HORA) VALUES " +
                    "('A1', 45, 12, '26–40', 'Solo', 'Juan Pérez', '10h'), " +
                    "('A1', 65, 28, '18–25', 'Pareja', 'Juan Pérez', '12h'), " +
                    "('A2', 80, 35, '26–40', 'Familia', 'Camila Soto', '14h'), " +
                    "('A3', 50, 18, '41–60', 'Solo', 'Mateo Silva', '16h'), " +
                    "('A1', 95, 42, '26–40', 'Pareja', 'Valentina Rojas', '18h')"


        private const val SQL_SELECT_USUARIOS =
            "SELECT u.*, r.$NOMBRE_ROL AS $NOMBRE_ROL " +
                    "FROM $TABLA_USUARIO u " +
                    "INNER JOIN $TABLA_ROLES r ON u.$ROL_USUARIO = r.$ID_ROL"

    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREAR_ROLES)
        db.execSQL(SQL_CREAR_USUARIOS)
        db.execSQL(SQL_CREAR_PUERTAS)
        db.execSQL(SQL_CREAR_CONTEOS)
        db.execSQL(SQL_CREAR_SOLICITUDES_CORREO)

        db.execSQL(SQL_PRECARGAR_ROLES)
        db.execSQL(SQL_PRECARGAR_PUERTAS)
        db.execSQL(SQL_PRECARGAR_USUARIOS)
        db.execSQL(SQL_PRECARGAR_CONTEOS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if(oldVersion < 2){
            db.execSQL(SQL_AGREGAR_AVATAR)
        }
        if(oldVersion < 3){
            db.execSQL(SQL_CREAR_PUERTAS)
            db.execSQL(SQL_PRECARGAR_PUERTAS)
        }
        if (oldVersion < 16) {
            db.execSQL("DROP TABLE IF EXISTS $TABLA_SOLICITUDES_CORREO")
            db.execSQL("DROP TABLE IF EXISTS $TABLA_CONTEO")
            db.execSQL("DROP TABLE IF EXISTS $TABLA_USUARIO")
            db.execSQL("DROP TABLE IF EXISTS $TABLA_PUERTAS")
            db.execSQL("DROP TABLE IF EXISTS $TABLA_ROLES")
            onCreate(db)
        }
    }
    fun actualizarEstadoUsuario(rut: String, activo: Boolean): Int {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put(ACTIVO_USUARIO, if (activo) 1 else 0)
        }
        return db.update(
            TABLA_USUARIO,
            valores,
            "$RUT_USUARIO = ?",
            arrayOf(rut)
        )
    }

    fun listarUsuarios(): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        val sql = "SELECT u.*, r.$NOMBRE_ROL AS $NOMBRE_ROL " +
                "FROM $TABLA_USUARIO u " +
                "INNER JOIN $TABLA_ROLES r ON u.$ROL_USUARIO = r.$ID_ROL " +
                "ORDER BY u.$ID_USUARIO ASC"
        readableDatabase.rawQuery(sql, null).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(leerUsuario(cursor))
            }
        }
        return lista
    }

    fun actualizarUsuario(rut: String, nombre: String, correo: String, telefono: String): Int {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put(NOMBRE_USUARIO, nombre)
            put(CORREO_USUARIO, correo)
            put(TELEFONO, telefono)
        }
        return db.update(
            TABLA_USUARIO,
            valores,
            "$RUT_USUARIO = ?",
            arrayOf(rut)
        )
    }
    fun listarPuertas(): List<AccesoTienda> {
        val lista = mutableListOf<AccesoTienda>()
        val sql = "SELECT * FROM $TABLA_PUERTAS ORDER BY $ID_PUERTA ASC "
        readableDatabase.rawQuery(sql, null).use { cursor ->
            while (cursor.moveToNext()) {
                val estaHabilitada = cursor.getInt(cursor.getColumnIndexOrThrow(HABILITADA_PUERTA)) == 1
                lista.add(
                    AccesoTienda(
                        id = cursor.getString(cursor.getColumnIndexOrThrow(ID_PUERTA)),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow(NOMBRE_PUERTA)),
                        descripcion = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPCION_PUERTA)),
                        estaHabilitado = estaHabilitada
                    )
                )
            }
        }
        return lista
    }
    fun actualizarEstadoPuerta(idPuerta: String, habilitada: Boolean) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(HABILITADA_PUERTA, if (habilitada) 1 else 0)
        }
        db.update(TABLA_PUERTAS, values, "$ID_PUERTA = ?", arrayOf(idPuerta))
    }
    fun listarPerfiles(): List<ConfiguracionPerfil> {
        val lista = mutableListOf<ConfiguracionPerfil>()
        val sql = "SELECT * FROM $TABLA_ROLES ORDER BY $ID_ROL ASC"
        readableDatabase.rawQuery(sql, null).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(
                    ConfiguracionPerfil(
                        codigo = cursor.getString((cursor.getColumnIndexOrThrow(CODIGO_ROL))),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow(NOMBRE_ROL)),
                        descripcion = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPCION_ROL)),
                        habilitado = true
                    )
                )
            }
        }
        return lista
    }

    fun actualizarAvatarPorCorreo(correo: String, avatar: ByteArray): Int {
        val valores = ContentValues().apply { put(AVATAR_USUARIO, avatar) }
        return writableDatabase.update(
            TABLA_USUARIO,
            valores,
            "$CORREO_USUARIO = ? OR $RUT_USUARIO = ?",
            arrayOf(correo, correo)
        )
    }

    fun obtenerUsuarioPorCorreo(correo: String): Usuario? {
        val sql = "SELECT u.*, r.$NOMBRE_ROL AS $NOMBRE_ROL " +
                "FROM $TABLA_USUARIO u " +
                "INNER JOIN $TABLA_ROLES r ON u.$ROL_USUARIO = r.$ID_ROL " +
                "WHERE u.$CORREO_USUARIO = ? OR u.$RUT_USUARIO = ?"
        readableDatabase.rawQuery(sql, arrayOf(correo, correo)).use { cursor ->
            if (cursor.moveToFirst()) {
                return leerUsuario(cursor)
            }
        }
        return null
    }

    fun registroUsuarioFormularioRegistro(uiState: FormularioRegistroUiState, contrasenaTemporal: String): Long {
        val db = writableDatabase

        val valores = ContentValues().apply {
            put(RUT_USUARIO, uiState.rut)
            put(NOMBRE_USUARIO, uiState.nombre)
            put(CORREO_USUARIO, uiState.correo)
            put(TELEFONO, uiState.telefono)
            put(CONTRASENA, HashUtils.generarHash(contrasenaTemporal))
            put(ROL_USUARIO, uiState.rolSeleccionado.id)
            put(DEBE_CAMBIAR_CONTRASENA, 1)
            put(TURNO_USUARIO, "Turno Diurno")
        }
        return db.insert(TABLA_USUARIO, null, valores)
    }

    fun completarPrimerIngreso(
        rutOCorreo: String,
        nuevaContrasena: String,
        preguntaSeguridad: String,
        respuestaSeguridad: String
    ): Int {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put(CONTRASENA, HashUtils.generarHash(nuevaContrasena))
            put(PREGUNTA_SEG, preguntaSeguridad)
            put(RESPUESTA_SEG, HashUtils.generarHashRespuesta(respuestaSeguridad))
            put(DEBE_CAMBIAR_CONTRASENA, 0)
        }
        return db.update(
            TABLA_USUARIO,
            valores,
            "$RUT_USUARIO = ? OR $CORREO_USUARIO = ?",
            arrayOf(rutOCorreo, rutOCorreo)
        )
    }
    fun obtenerPreguntaSeguridad(rutOCorreo: String): String? {
        val db = readableDatabase
        val sql = "SELECT $PREGUNTA_SEG FROM $TABLA_USUARIO WHERE $RUT_USUARIO = ? OR $CORREO_USUARIO = ?"
        db.rawQuery(sql, arrayOf(rutOCorreo, rutOCorreo)).use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow(PREGUNTA_SEG))
            }
        }
        return null
    }
    fun verificarRespuesta(rutOCorreo: String, respuestaIngresada: String): Boolean {
        val db = readableDatabase
        val sql = "SELECT $RESPUESTA_SEG FROM $TABLA_USUARIO WHERE $RUT_USUARIO = ? OR $CORREO_USUARIO = ?"
        db.rawQuery(sql, arrayOf(rutOCorreo, rutOCorreo)).use { cursor ->
            if (cursor.moveToFirst()) {
                val respuestaGuardada = cursor.getString(cursor.getColumnIndexOrThrow(RESPUESTA_SEG))
                val hashIngresado = HashUtils.generarHashRespuesta(respuestaIngresada)
                return hashIngresado == respuestaGuardada
            }
        }
        return false
    }
    fun actualizarContrasena(rutOCorreo: String, nuevaContrasena: String): Int {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put(CONTRASENA, HashUtils.generarHash(nuevaContrasena))
        }
        return db.update(
            TABLA_USUARIO,
            valores,
            "$RUT_USUARIO = ? OR $CORREO_USUARIO = ?",
            arrayOf(rutOCorreo, rutOCorreo)
        )
    }

    fun obtenerFlujoPorHora() : List<DatoGrafico> {
        val lista = mutableListOf<DatoGrafico>()
        val sql = "SELECT $HORA, SUM($CANT_CLIENTES) AS total " +
                "FROM $TABLA_CONTEO " +
                "GROUP BY $HORA " +
                "ORDER BY $ID_CONTEO ASC"

        readableDatabase.rawQuery(sql, null).use { cursor ->
            while (cursor.moveToNext()) {
                val hora = cursor.getString(cursor.getColumnIndexOrThrow(HORA))
                val total = cursor.getInt(cursor.getColumnIndexOrThrow("total"))
                lista.add(DatoGrafico(hora,total))

            }
        }
        return lista
    }
    fun obtenerSegmentacion() : List<DatoGrafico> {
        val lista = mutableListOf<DatoGrafico>()
        val sql = "SELECT SUM($CANT_TRANSITO) AS total_transito, " +
                "SUM($CANT_CLIENTES) AS total_clientes " +
                "FROM $TABLA_CONTEO"

        readableDatabase.rawQuery(sql, null).use { cursor ->
            if(cursor.moveToFirst()) {
                val totalTransito = cursor.getInt(cursor.getColumnIndexOrThrow("total_transito"))
                val totalClientes = cursor.getInt(cursor.getColumnIndexOrThrow("total_clientes"))

                lista.add(DatoGrafico("Transito (Pasillo)", totalTransito))
                lista.add(DatoGrafico("Clientes / Visita", totalClientes))
            }
        }
        return lista
    }

    fun obtenerFlujoPorTurno() : List<DatoGrafico>{
        val lista = mutableListOf<DatoGrafico>()
        val sql = """
            SELECT 
                CASE 
                    WHEN CAST(REPLACE($HORA, 'h', '') AS INTEGER) < 14 THEN 'Turno Mañana'
                    ELSE 'Turno Tarde'
                END AS turno,
                SUM($CANT_CLIENTES) AS total_clientes
            FROM $TABLA_CONTEO
            GROUP BY turno
            ORDER BY turno ASC
        """.trimIndent()

        readableDatabase.rawQuery(sql, null).use { cursor ->
            while (cursor.moveToNext()){
                val turno = cursor.getString(cursor.getColumnIndexOrThrow("turno"))
                val total = cursor.getInt(cursor.getColumnIndexOrThrow("total_clientes"))
                lista.add(DatoGrafico(turno,total))
            }
        }
        return lista
    }

    fun registrarFormularioConteo(
        uiState: FormularioConteoUiState,
        nombreObservador: String,
        turnoHora: String,
        idUsuario: Long = 0
    ): Long {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put(PUERTA_ACCESO, uiState.puertaAcceso)
            put(CANT_TRANSITO, uiState.cantidadTransito)
            put(CANT_CLIENTES, uiState.cantidadClientes)
            put(RANGO_ETARIO, uiState.rangoEtario)
            put(COMPANIA, uiState.compania)
            put(OBSERVADOR, nombreObservador)
            put(HORA, turnoHora)
            if (idUsuario > 0) put(ID_USUARIO_FK, idUsuario)
        }
        return db.insert(TABLA_CONTEO, null, valores)
    }

    fun listarConteos(): List<FormularioConteoUiState> {
        val lista = mutableListOf<FormularioConteoUiState>()
        val sql = "SELECT * FROM $TABLA_CONTEO ORDER BY $ID_CONTEO DESC"

        readableDatabase.rawQuery(sql, null).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(leerConteo(cursor))
            }
        }
        return lista
    }
    fun listarConteosPorObservador(nombreObservador: String): List<FormularioConteoUiState> {
        val lista = mutableListOf<FormularioConteoUiState>()
        val sql = "SELECT * FROM $TABLA_CONTEO WHERE $OBSERVADOR = ? ORDER BY $ID_CONTEO DESC"

        readableDatabase.rawQuery(sql, arrayOf(nombreObservador)).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(leerConteo(cursor))
            }
        }
        return lista
    }
    private fun leerConteo(cursor: Cursor): FormularioConteoUiState {
        return FormularioConteoUiState(
            puertaAcceso = cursor.getString(cursor.getColumnIndexOrThrow(PUERTA_ACCESO)),
            cantidadTransito = cursor.getInt(cursor.getColumnIndexOrThrow(CANT_TRANSITO)),
            cantidadClientes = cursor.getInt(cursor.getColumnIndexOrThrow(CANT_CLIENTES)),
            rangoEtario = cursor.getString(cursor.getColumnIndexOrThrow(RANGO_ETARIO)),
            compania = cursor.getString(cursor.getColumnIndexOrThrow(COMPANIA)),
            observador = cursor.getString(cursor.getColumnIndexOrThrow(OBSERVADOR)),
            hora = cursor.getString(cursor.getColumnIndexOrThrow(HORA)),
            envioExitoso = true
        )
    }

    fun autenticarUsuario(correo: String, contrasena: String): Usuario? {
        val hashIngresado = HashUtils.generarHash(contrasena)
        val inicioSesion = "SELECT u.*, r.$NOMBRE_ROL AS $NOMBRE_ROL " +
                "FROM $TABLA_USUARIO u " +
                "INNER JOIN $TABLA_ROLES r ON u.$ROL_USUARIO = r.$ID_ROL " +
                "WHERE (u.$CORREO_USUARIO = ? OR u.$RUT_USUARIO = ?) AND u.$CONTRASENA = ?"

        readableDatabase.rawQuery(inicioSesion, arrayOf(correo, correo, hashIngresado)).use { cursor ->
            if (cursor.moveToFirst()) {
                return leerUsuario(cursor)
            }
        }
        return null
    }
    fun crearSolicitudCorreo(
        rutUsuario: String,
        nombreUsuario: String,
        correoActual: String,
        correoNuevo: String,
        motivo: String
    ): Long {
        val valores = ContentValues().apply {
            put(SOL_RUT, rutUsuario)
            put(SOL_NOMBRE, nombreUsuario)
            put(SOL_CORREO_ACTUAL, correoActual)
            put(SOL_CORREO_NUEVO, correoNuevo)
            put(SOL_MOTIVO, motivo)
            put(SOL_ESTADO, "PENDIENTE")
        }
        return writableDatabase.insert(TABLA_SOLICITUDES_CORREO, null, valores)
    }
    fun listarSolicitudesCorreoPendientes(): List<SolicitudCorreo> {
        val lista = mutableListOf<SolicitudCorreo>()
        val sql = "SELECT * FROM $TABLA_SOLICITUDES_CORREO WHERE $SOL_ESTADO = 'PENDIENTE' ORDER BY $ID_SOLICITUD DESC"
        readableDatabase.rawQuery(sql, null).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(
                    SolicitudCorreo(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(ID_SOLICITUD)),
                        rutUsuario = cursor.getString(cursor.getColumnIndexOrThrow(SOL_RUT)),
                        nombreUsuario = cursor.getString(cursor.getColumnIndexOrThrow(SOL_NOMBRE)),
                        correoActual = cursor.getString(cursor.getColumnIndexOrThrow(SOL_CORREO_ACTUAL)),
                        correoNuevo = cursor.getString(cursor.getColumnIndexOrThrow(SOL_CORREO_NUEVO)),
                        motivo = cursor.getString(cursor.getColumnIndexOrThrow(SOL_MOTIVO)),
                        estado = cursor.getString(cursor.getColumnIndexOrThrow(SOL_ESTADO))
                    )
                )
            }
        }
        return lista
    }
    fun obtenerSolicitudPendientePorRut(rutUsuario: String): SolicitudCorreo? {
        val sql = "SELECT * FROM $TABLA_SOLICITUDES_CORREO WHERE $SOL_RUT = ? AND $SOL_ESTADO = 'PENDIENTE' ORDER BY $ID_SOLICITUD DESC LIMIT 1"
        readableDatabase.rawQuery(sql, arrayOf(rutUsuario)).use { cursor ->
            if (cursor.moveToFirst()) {
                return SolicitudCorreo(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(ID_SOLICITUD)),
                    rutUsuario = cursor.getString(cursor.getColumnIndexOrThrow(SOL_RUT)),
                    nombreUsuario = cursor.getString(cursor.getColumnIndexOrThrow(SOL_NOMBRE)),
                    correoActual = cursor.getString(cursor.getColumnIndexOrThrow(SOL_CORREO_ACTUAL)),
                    correoNuevo = cursor.getString(cursor.getColumnIndexOrThrow(SOL_CORREO_NUEVO)),
                    motivo = cursor.getString(cursor.getColumnIndexOrThrow(SOL_MOTIVO)),
                    estado = cursor.getString(cursor.getColumnIndexOrThrow(SOL_ESTADO))
                )
            }
        }
        return null
    }

    fun resolverSolicitudCorreo(solicitud: SolicitudCorreo, aprobar: Boolean) {
        val db = writableDatabase
        val nuevoEstado = if (aprobar) "APROBADA" else "RECHAZADA"
        val valoresSol = ContentValues().apply { put(SOL_ESTADO, nuevoEstado) }
        db.update(TABLA_SOLICITUDES_CORREO, valoresSol, "$ID_SOLICITUD = ?", arrayOf(solicitud.id.toString()))

        if (aprobar) {
            val valoresUser = ContentValues().apply { put(CORREO_USUARIO, solicitud.correoNuevo) }
            db.update(TABLA_USUARIO, valoresUser, "$RUT_USUARIO = ?", arrayOf(solicitud.rutUsuario))
        }
    }

    private fun leerUsuario(cursor: Cursor): Usuario {
        val idxDebeCambiar = cursor.getColumnIndex(DEBE_CAMBIAR_CONTRASENA)
        val debeCambiar = if (idxDebeCambiar != -1) cursor.getInt(idxDebeCambiar) == 1 else false

        val idxActivo = cursor.getColumnIndex(ACTIVO_USUARIO)
        val estaActivo = if (idxActivo != -1) cursor.getInt(idxActivo) == 1 else true

        return Usuario(
            nombreCompleto = cursor.getString(cursor.getColumnIndexOrThrow(NOMBRE_USUARIO)),
            rut = cursor.getString(cursor.getColumnIndexOrThrow(RUT_USUARIO)),
            correo = cursor.getString(cursor.getColumnIndexOrThrow(CORREO_USUARIO)),
            telefono = cursor.getColumnIndexOrThrow(TELEFONO).let { col ->
                if (cursor.isNull(col)) "" else cursor.getString(col)
            },
            passwordHash = cursor.getString(cursor.getColumnIndexOrThrow(CONTRASENA)),
            perfil = cursor.getString(cursor.getColumnIndexOrThrow(NOMBRE_ROL)),
            activo = estaActivo,
            debeCambiarContrasena = debeCambiar,
            avatar = cursor.getColumnIndexOrThrow(AVATAR_USUARIO).let { columna ->
                if (cursor.isNull(columna)) null else cursor.getBlob(columna)
            }
        )
    }



}

