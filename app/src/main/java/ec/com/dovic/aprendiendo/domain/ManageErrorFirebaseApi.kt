package ec.com.dovic.aprendiendo.domain

import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.StorageException
import java.lang.Exception

class ManageErrorFirebaseApi {
    companion object {

        fun getMessageErrorStorage(it: Exception): String {
            if (it is StorageException) {
                when (it.errorCode) {
                    StorageException.ERROR_OBJECT_NOT_FOUND -> {
                        return "No existe ningún objeto en la referencia deseada."
                    }
                    StorageException.ERROR_UNKNOWN -> {
                        return "Ocurrió un error desconocido."
                    }
                    StorageException.ERROR_BUCKET_NOT_FOUND -> {
                        return "No se configuró ningún depósito para Cloud Storage."
                    }
                    StorageException.ERROR_PROJECT_NOT_FOUND -> {
                        return "No se configuró ningún proyecto para Cloud Storage"
                    }
                    StorageException.ERROR_QUOTA_EXCEEDED -> {
                        return "Se superó la cuota del depósito de Cloud Storage. Si estás en el nivel gratuito, deberás actualizar a un plan pago. Si estás en un plan pago, comunícate con el personal de asistencia de Firebase."
                    }
                    StorageException.ERROR_NOT_AUTHENTICATED -> {
                        return "El usuario no se autenticó. Vuelve a intentarlo después de realizar la autenticación."
                    }
                    StorageException.ERROR_NOT_AUTHORIZED -> {
                        return "No se configuró ningún depósito para Cloud Storage."
                    }
                    StorageException.ERROR_RETRY_LIMIT_EXCEEDED -> {
                        return "Se superó el límite de tiempo máximo permitido para una operación (de carga, descarga, eliminación, etc.). Vuelve a intentarlo."
                    }
                    StorageException.ERROR_INVALID_CHECKSUM -> {
                        return "El archivo del cliente no coincide con la suma de verificación del archivo que recibió el servidor. Intenta volver a subirlo."
                    }
                    StorageException.ERROR_CANCELED -> {
                        return "El usuario canceló la operación"
                    }
                }
            }
            return "Posible problema de conexion, intentelo nuevamente"
        }

        fun getMessageErrorFirebaseAuth(it: Exception): String {
            if (it is FirebaseAuthException) {

                when (it.errorCode) {
                    "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> {
                        return "Ya existe una cuenta con sus credenciales"
                    }
                    "ERROR_CREDENTIAL_ALREADY_IN_USE" -> {
                        return "Las credenciales proporcionados ya estan registradas"
                    }
                    "ERROR_EMAIL_ALREADY_IN_USE" -> {
                        return "El email proporcionado ya se encuentra registrado"
                    }
                    "ERROR_USER_DISABLED" -> {
                        return "Usuario desactivado"
                    }
                    "ERROR_USER_NOT_FOUND" -> {
                        return "Usuario no encontrado"
                    }
                    "ERROR_USER_TOKEN_EXPIRED" -> {
                        return "Token del usuario caducado"
                    }
                    "ERROR_INVALID_USER_TOKEN" -> {
                        return "Token del usuario invalido"
                    }
                }
            }
            return "Posible problema de conexion, intentelo nuevamente"
        }

        fun getMessageErrorFirebaseFirestore(it: Exception): String {
            if (it is FirebaseFirestoreException) {
                when (it.code) {
                    FirebaseFirestoreException.Code.ABORTED -> {
                        //La operación fue abortada, típicamente debido a un problema de concurrencia como transacción se anula, etc
                        return "Operacion abortada"
                    }
                    FirebaseFirestoreException.Code.ALREADY_EXISTS -> {
                        //Algún documento que se intentó crear ya existe.
                        return "Documento ya existe"
                    }
                    FirebaseFirestoreException.Code.CANCELLED -> {
                        //La operación fue cancelada (típicamente por la persona que llama).
                        return "Operacion cancelada"
                    }
                    FirebaseFirestoreException.Code.DATA_LOSS -> {
                        //pérdida de datos irrecuperable o corrupción.
                        return "Informacion corrupta o perdida"
                    }
                    FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> {
                        //Plazo expiró antes de la operación podría completarse. Para las operaciones que cambian el estado del sistema, este error puede ser devuelto incluso si la operación se ha completado con éxito. Por ejemplo, una respuesta exitosa de un servidor podría haber sido retrasado lo suficiente para que el plazo expire.
                        return "Tiempo de espera exedido"
                    }
                    FirebaseFirestoreException.Code.FAILED_PRECONDITION -> {
                        //Operación fue rechazada debido a que el sistema no está en un estado requerido para la ejecución de la operación

                        return "Operacion rechadaza"
                    }
                    FirebaseFirestoreException.Code.INVALID_ARGUMENT -> {
                        //Cliente especifica un argumento no válido. Tenga en cuenta que esto difiere de FAILED_PRECONDITION. INVALID_ARGUMENT indica argumentos que son problemáticas, independientemente del estado del sistema (por ejemplo, un nombre de campo no válido).

                        return "Informacion incorrecta o no válida"
                    }
                    FirebaseFirestoreException.Code.OK -> {
                        //La operación se completó correctamente. FirebaseFirestoreException nunca tendrá un estado de OK.

                        return "Operacion completada correctamente"
                    }
                    FirebaseFirestoreException.Code.OUT_OF_RANGE -> {
                        //La operación se ha intentado más allá del rango válido.
                        return "Operacion fuera de rango"
                    }
                    FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> {
                        //Algunos recursos se ha agotado, tal vez una cuota por usuario, o tal vez todo el sistema de archivos está fuera del espacio.
                        return "Problemas al almacenar su información, intentelo nuevamente"
                    }
                    FirebaseFirestoreException.Code.UNAUTHENTICATED -> {
                        //La solicitud no tiene credenciales de autenticación válidas para la operación.
                        return "Debe iniciar sessión"
                    }
                    FirebaseFirestoreException.Code.UNAVAILABLE -> {
                        //El servicio no está disponible actualmente. Esta es una muy probablemente una condición transitoria y puede ser corregido por volver a intentar con un retardo de envío.
                        return "Posible problema de conexion intentelo nuevamente"
                    }
                    FirebaseFirestoreException.Code.UNIMPLEMENTED -> {
                        //La operación no se lleva a cabo o no es compatible / activada.
                        return "No pudimos completar su operación"
                    }
                    FirebaseFirestoreException.Code.UNKNOWN -> {
                        //error desconocido o un error de un dominio de error diferente.
                        return "Error desconocido, intetelo nuevamente"
                    }

                    FirebaseFirestoreException.Code.NOT_FOUND -> {
                        //no se encontró algún documento solicitado.
                        return "Documento o información no encontrada"
                    }
                    FirebaseFirestoreException.Code.PERMISSION_DENIED -> {
                        //El llamador no tiene permiso para ejecutar la operación especificada.
                        return "Permiso denegado"
                    }
                    FirebaseFirestoreException.Code.INTERNAL -> {
                        // //Los errores internos. Significa que algunos invariantes esperados por el sistema subyacente se ha roto. Si ve uno de estos errores, algo está muy roto.
                        return "Estamos experimentando problemas, intetelo en unos segundos"
                    }
                }
            }
            return "Posible problema de conexion, intentelo nuevamente"
        }
    }
}