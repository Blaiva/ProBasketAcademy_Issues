.
Documentación Técnica y Funcional: ProBasket Academy App

1. Introducción
ProBasket Academy App es un sistema integral de gestión deportiva y administrativa diseñado exclusivamente para academias de baloncesto. Su objetivo principal es centralizar y automatizar las operaciones diarias de la academia, separando lógicamente el rendimiento deportivo de la administración financiera, pero manteniendo ambas áreas conectadas en una sola herramienta móvil.
2. Arquitectura y Tecnologías Clave
La aplicación está construida utilizando el estado del arte del desarrollo nativo en Android, garantizando rendimiento, escalabilidad y mantenibilidad.
Lenguaje: Kotlin.
Interfaz de Usuario: Jetpack Compose (UI declarativa).
Arquitectura: Clean Architecture combinada con MVVM (Model-View-ViewModel). Separa la lógica en capas de Presentación, Dominio (Casos de Uso) y Datos (Repositorios).
Base de Datos Local: Room Database (SQLite) para un funcionamiento ágil y offline, estructurada mediante DAOs (Data Access Objects).
Autenticación: Firebase Authentication integrado con Google Credential Manager (Inicio de sesión con un toque).
Inyección de Dependencias: Dagger Hilt.
Gestión de Imágenes: Librería Coil (manejo asíncrono de fotos locales y URLs de Google).
Navegación: Compose Navigation con un sistema de NavBackStack basado en rutas selladas (Screen).
3. Estructura de la Base de Datos (Core)
El corazón de la aplicación se divide en 5 tablas principales altamente relacionadas entre sí:
Jugadores (JugadorEntity): Almacena la biometría, información de contacto, datos del tutor legal, evidencias documentales (acta de nacimiento) y actúa como ancla para los datos financieros mediante campos ocultos de suscripción y deuda.
Categorías (CategoriaEntity): Gestiona los distintos equipos o niveles (ej. U-18, U-20). Se relaciona de uno a muchos con los jugadores.
Asistencias (AsistenciaEntity): Registra el estado de presencia (asistió/faltó) asociado a un jugador, a una categoría y a una fecha específica en formato de marca de tiempo (Epoch).
Pagos (PagoEntity): Un libro mayor (Ledger) que documenta cada transacción financiera de un jugador (monto total, abonado, deuda, concepto y estado).
Eventos (EventoEntity): Agenda de la academia que almacena entrenamientos, partidos, cobros o reuniones, vinculados a una fecha y hora.
4. Módulos y Funcionalidades Principales
La aplicación se divide en seis secciones principales accesibles a través de la barra de navegación inferior (BottomBar), más un módulo de autenticación de entrada.
4.1. Módulo de Autenticación (AuthScreen)
Funcionamiento: Es la puerta de entrada a la aplicación. Verifica si existe una sesión activa. Si no la hay, solicita al usuario que inicie sesión exclusivamente mediante su cuenta de Google.
Seguridad: Vincula toda la base de datos local al userId (UID de Firebase) del usuario autenticado. Esto garantiza que la información sea privada y exclusiva de la cuenta que inició sesión.
4.2. Dashboard / Inicio (HomeScreen)
Funcionamiento: Es el panel de control (Overview). Muestra un resumen gerencial en tiempo real.
Características:
KPIs (Indicadores Clave): Muestra el total de jugadores activos, el porcentaje de asistencia mensual promedio y los ingresos generados en el año actual.
Notificaciones de Cobro: Despliega una lista de pagos urgentes o "Pendientes por Cobrar", destacando a los jugadores que mantienen deudas para tomar acción inmediata.
4.3. Directorio de Jugadores (JugadoresListScreen y JugadorEditScreen)
Funcionamiento: Es el CRM deportivo de la academia. Permite añadir nuevos atletas, buscar por nombre, y ver sus perfiles.
Ficha del Jugador (Modo Lectura y Edición):
Datos Deportivos: Número de camiseta, estatura, peso, talla de ropa y asignación a un equipo/categoría.
Documentación: Permite cargar y recortar fotos de perfil y fotos del acta de nacimiento directamente desde la galería del dispositivo.
Responsables: Almacena los datos del padre o tutor legal para casos de emergencia o cobranza.
Gestión de Estado: Permite marcar a un jugador como "Inactivo" para que deje de aparecer en los pases de lista sin tener que borrar su historial.
4.4. Gestión de Categorías (CategoriasListScreen y CategoriaDetalleScreen)
Funcionamiento: Administra los grupos de entrenamiento.
Características: Permite crear nuevas categorías y ver cuántos jugadores pertenecen a cada una. Al entrar al detalle de una categoría, el administrador puede asignar masivamente a jugadores que actualmente están "Sin Categoría" o remover a aquellos que suben de nivel.
4.5. Calendario y Eventos (EventosScreen)
Funcionamiento: Un planificador visual integrado.
Características:
Posee un calendario horizontal deslizable por meses.
Los días con actividades programadas muestran un pequeño indicador azul.Permite registrar la duración, lugar, tipo de evento (Partido, Entrenamiento, Reunión, etc.) y hora exacta. El diseño de las tarjetas de evento cambia de color e ícono según el tipo de actividad elegida.
4.6. Control de Asistencias (AsistenciasScreen)
Funcionamiento: Automatiza el pase de lista diario.
Características:
El administrador selecciona una fecha en el calendario y filtra por "Categoría" (para no ver una lista interminable de 100+ jugadores).
El sistema carga el roster de ese equipo.
Si la fecha seleccionada es el Día de Hoy, permite marcar casillas de verificación verdes para los presentes. Si es una fecha pasada, la pantalla entra en "Modo Lectura" inhabilitando la edición para auditar el historial.
4.7. Finanzas y Pagos (PagosListScreen y PagosDetalleScreen)
Funcionamiento: Es el módulo contable, diseñado con campos separados de la ficha deportiva para mantener la privacidad de los datos.
Vista General: Muestra la lista de jugadores junto a un resumen global de "Total Generado", "Total Pagado" y "Deuda Global de la Academia".
Estado de Cuenta (Detalle):
Suscripciones: Configura si el jugador paga un plan "Mensual" o "Semanal" y calcula automáticamente las fechas de vencimiento.
Libro de Transacciones: Muestra un historial detallado de cobros.
Lógica de Abonos: Si un jugador tiene una cuota de $500 pero solo trae $250, el sistema permite registrar un "Abono". El estado cambia a "ABONADO" (color naranja) y mantiene la deuda restante visible.
Saldar: Permite liquidar deudas antiguas con un solo botón, ajustando los balances históricos sin perder el rastro del dinero.
5. Menús Universales y Experiencia de Usuario (UX)
Barra de Navegación Inferior: Presente en todos los módulos principales, permite cambiar de contexto (Ej: de Finanzas a Calendario) sin perder el estado de la aplicación.
Perfil Global: En la esquina superior derecha de cada pantalla, aparece la foto de la cuenta de Google del usuario. Al tocarla, se despliega un cuadro de diálogo (ProfileDialog) que muestra el nombre, el correo institucional utilizado y permite cerrar la sesión de forma segura para proteger la base de datos.
Validaciones: Todas las entradas de texto cuentan con reglas de negocio estrictas (Ej: no permitir edades negativas, exigir números telefónicos válidos, requerir que los nombres tengan más de 3 letras) lanzando mensajes de error en tiempo real (color rojo) debajo de cada campo antes de permitir guardar a la base de datos.

