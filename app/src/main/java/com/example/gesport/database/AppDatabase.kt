package com.example.gesport.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gesport.models.Booking
import com.example.gesport.models.Court
import com.example.gesport.models.CourtType
import com.example.gesport.models.Team
import com.example.gesport.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(
    entities  = [User::class, Court::class, Team::class, Booking::class],
    version   = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun courtDao(): CourtDao
    abstract fun teamDao(): TeamDao
    abstract fun bookingDao(): BookingDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gesport_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                populateDatabase(getDatabase(context))
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateDatabase(db: AppDatabase) {

            val users = listOf(
                User(nombre = "Administrador",   email = "admin@gesport.com",    password = "123", rol = "ADMIN"),
                User(nombre = "Juan Pérez",       email = "juan@test.com",        password = "123", rol = "JUGADOR"),
                User(nombre = "María García",     email = "maria@test.com",       password = "123", rol = "JUGADOR"),
                User(nombre = "Ana Martínez",     email = "ana@test.com",         password = "123", rol = "JUGADOR"),
                User(nombre = "Luis Fernández",   email = "luis@test.com",        password = "123", rol = "JUGADOR"),
                User(nombre = "Pablo Sanz",       email = "pablo@test.com",       password = "123", rol = "JUGADOR"),
                User(nombre = "Lucía Gómez",      email = "lucia@test.com",       password = "123", rol = "JUGADOR"),
                User(nombre = "Roberto Díaz",     email = "roberto@test.com",     password = "123", rol = "JUGADOR"),
                User(nombre = "Sonia Vega",       email = "sonia@test.com",       password = "123", rol = "JUGADOR"),
                User(nombre = "Miguel Torres",    email = "miguel@test.com",      password = "123", rol = "JUGADOR"),
                User(nombre = "Carmen López",     email = "carmen@test.com",      password = "123", rol = "JUGADOR"),
                User(nombre = "Sergio Moreno",    email = "sergio@test.com",      password = "123", rol = "JUGADOR"),
                User(nombre = "David Jiménez",    email = "david@test.com",       password = "123", rol = "JUGADOR"),
                User(nombre = "Laura Núñez",      email = "laura@test.com",       password = "123", rol = "JUGADOR"),
                User(nombre = "Raúl Castillo",    email = "raul@test.com",        password = "123", rol = "JUGADOR"),
                User(nombre = "Marta Iglesias",   email = "marta@test.com",       password = "123", rol = "JUGADOR"),
                User(nombre = "Carlos Ruiz",      email = "carlos@test.com",      password = "123", rol = "ENTRENADOR"),
                User(nombre = "Elena Blanco",     email = "elena@test.com",       password = "123", rol = "ENTRENADOR"),
                User(nombre = "Marcos Gil",       email = "marcos@test.com",      password = "123", rol = "ENTRENADOR"),
                User(nombre = "Sofía Herrera",    email = "sofia@test.com",       password = "123", rol = "ENTRENADOR"),
                User(nombre = "Javier Ramos",     email = "javier@test.com",      password = "123", rol = "ENTRENADOR"),
                User(nombre = "Nuria Peña",       email = "nuria@test.com",       password = "123", rol = "ENTRENADOR"),
                User(nombre = "Pedro Árbitro",    email = "pedro@test.com",       password = "123", rol = "ARBITRO"),
                User(nombre = "Rosa Arbitraje",   email = "rosa@test.com",        password = "123", rol = "ARBITRO"),
            )
            users.forEach { db.userDao().insert(it) }

            val courts = listOf(
                Court(nombre = "Pista Pádel 1",    tipo = CourtType.PADEL.name,      descripcion = "Pista exterior",          activa = true,  precioPorHora = 12.0),
                Court(nombre = "Pista Pádel 2",    tipo = CourtType.PADEL.name,      descripcion = "Pista interior cubierta", activa = true,  precioPorHora = 15.0),
                Court(nombre = "Pista Pádel 3",    tipo = CourtType.PADEL.name,      descripcion = "Pista VIP",               activa = true,  precioPorHora = 18.0),
                Court(nombre = "Cancha Fútbol 7",  tipo = CourtType.FUTBOL.name,     descripcion = "Campo de fútbol 7",       activa = true,  precioPorHora = 40.0),
                Court(nombre = "Cancha Fútbol 11", tipo = CourtType.FUTBOL.name,     descripcion = "Campo reglamentario",     activa = true,  precioPorHora = 60.0),
                Court(nombre = "Cancha Fútbol 5",  tipo = CourtType.FUTBOL.name,     descripcion = "Campo pequeño cubierto",  activa = true,  precioPorHora = 25.0),
                Court(nombre = "Pista Tenis 1",    tipo = CourtType.TENIS.name,      descripcion = "Tierra batida",           activa = true,  precioPorHora = 10.0),
                Court(nombre = "Pista Tenis 2",    tipo = CourtType.TENIS.name,      descripcion = "Pista dura",              activa = true,  precioPorHora = 12.0),
                Court(nombre = "Pista Tenis 3",    tipo = CourtType.TENIS.name,      descripcion = "Hierba artificial",       activa = true,  precioPorHora = 14.0),
                Court(nombre = "Pabellón A",       tipo = CourtType.BALONCESTO.name, descripcion = "Pabellón principal",      activa = true,  precioPorHora = 30.0),
                Court(nombre = "Pabellón B",       tipo = CourtType.BALONCESTO.name, descripcion = "Pabellón secundario",     activa = true,  precioPorHora = 25.0),
                Court(nombre = "Pista Exterior",   tipo = CourtType.BALONCESTO.name, descripcion = "Pista al aire libre",     activa = false, precioPorHora = 0.0),
            )
            courts.forEach { db.courtDao().insert(it) }

            val teams = listOf(
                Team(nombre = "Los Ases",         descripcion = "Equipo pádel competición",  sport = CourtType.PADEL.name,      entrenadorId = 17, entrenadorNombre = "Carlos Ruiz",   jugadoresIds = "2,3,4,5"),
                Team(nombre = "Raqueta de Oro",   descripcion = "Equipo pádel amateur",       sport = CourtType.PADEL.name,      entrenadorId = 18, entrenadorNombre = "Elena Blanco",  jugadoresIds = "6,7,8,9"),
                Team(nombre = "Pádel Élite",      descripcion = "Equipo pádel élite",         sport = CourtType.PADEL.name,      entrenadorId = 19, entrenadorNombre = "Marcos Gil",    jugadoresIds = "10,11,12,2"),
                Team(nombre = "Atlético Gesport", descripcion = "Equipo fútbol principal",    sport = CourtType.FUTBOL.name,     entrenadorId = 20, entrenadorNombre = "Sofía Herrera", jugadoresIds = "13,14,15,16,2,3"),
                Team(nombre = "Gesport FC",       descripcion = "Equipo fútbol juvenil",      sport = CourtType.FUTBOL.name,     entrenadorId = 21, entrenadorNombre = "Javier Ramos",  jugadoresIds = "4,5,6,7,13,14"),
                Team(nombre = "Los Meteoros",     descripcion = "Equipo fútbol sala",         sport = CourtType.FUTBOL.name,     entrenadorId = 22, entrenadorNombre = "Nuria Peña",    jugadoresIds = "8,9,10,11,15,16"),
                Team(nombre = "Tenis Club A",     descripcion = "Club tenis senior",          sport = CourtType.TENIS.name,      entrenadorId = 17, entrenadorNombre = "Carlos Ruiz",   jugadoresIds = "2,4,6,8"),
                Team(nombre = "Raqueteros FC",    descripcion = "Club tenis amateur",         sport = CourtType.TENIS.name,      entrenadorId = 18, entrenadorNombre = "Elena Blanco",  jugadoresIds = "3,5,7,9"),
                Team(nombre = "Slam Masters",     descripcion = "Club tenis competición",     sport = CourtType.TENIS.name,      entrenadorId = 19, entrenadorNombre = "Marcos Gil",    jugadoresIds = "10,11,12,14"),
                Team(nombre = "Basket Gesport",   descripcion = "Equipo baloncesto senior",   sport = CourtType.BALONCESTO.name, entrenadorId = 20, entrenadorNombre = "Sofía Herrera", jugadoresIds = "2,3,4,5,6,7"),
                Team(nombre = "Los Triples",      descripcion = "Equipo baloncesto juvenil",  sport = CourtType.BALONCESTO.name, entrenadorId = 21, entrenadorNombre = "Javier Ramos",  jugadoresIds = "8,9,10,11,12,13"),
                Team(nombre = "Canastas FC",      descripcion = "Equipo baloncesto femenino", sport = CourtType.BALONCESTO.name, entrenadorId = 22, entrenadorNombre = "Nuria Peña",    jugadoresIds = "14,15,16,3,5,7"),
            )
            teams.forEach { db.teamDao().insert(it) }

            val hoy = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0);      set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            fun slotMs(dayOffset: Int, hour: Int, minute: Int = 0): Long =
                Calendar.getInstance().apply {
                    timeInMillis = hoy
                    add(Calendar.DAY_OF_MONTH, dayOffset)
                    set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, minute)
                }.timeInMillis

            fun dayMs(offset: Int): Long = Calendar.getInstance().apply {
                timeInMillis = hoy; add(Calendar.DAY_OF_MONTH, offset)
            }.timeInMillis

            val bookings = listOf(
                // Juan Pérez (userId=2) — individual pádel HOY
                Booking(
                    tipo = "INDIVIDUAL", usuarioId = 2, usuarioNombre = "Juan Pérez",
                    pistaId = 1, pistaNombre = "Pista Pádel 1",
                    fecha = hoy, horaInicio = slotMs(0, 10), horaFin = slotMs(0, 11, 30)
                ),
                // Carlos Ruiz (userId=17, entrenador) — reserva de equipo "Los Ases" (equipoId=1) MAÑANA
                Booking(
                    tipo = "EQUIPO", usuarioId = 17, usuarioNombre = "Carlos Ruiz",
                    pistaId = 2, pistaNombre = "Pista Pádel 2",
                    fecha = dayMs(1), horaInicio = slotMs(1, 16), horaFin = slotMs(1, 17, 30),
                    equipoId = 1, equipoNombre = "Los Ases"
                ),
                // Partido: Atlético Gesport(4) vs Gesport FC(5) — Juan(2) y Ana(4) están en ambos
                Booking(
                    tipo = "PARTIDO",
                    pistaId = 4, pistaNombre = "Cancha Fútbol 7",
                    fecha = dayMs(2), horaInicio = slotMs(2, 18), horaFin = slotMs(2, 19, 30),
                    equipoLocalId = 4, equipoLocalNombre = "Atlético Gesport",
                    equipoVisitanteId = 5, equipoVisitanteNombre = "Gesport FC",
                    arbitroId = 23, arbitroNombre = "Pedro Árbitro"
                ),
                // Cancelada — no debe aparecer
                Booking(
                    tipo = "INDIVIDUAL", usuarioId = 3, usuarioNombre = "María García",
                    pistaId = 1, pistaNombre = "Pista Pádel 1",
                    fecha = dayMs(-1), horaInicio = slotMs(-1, 9), horaFin = slotMs(-1, 10, 30),
                    cancelada = true, notas = "Cancelada por lluvia"
                )
            )
            bookings.forEach { db.bookingDao().insert(it) }
        }
    }
}