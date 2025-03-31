package com.kLagan.challenge.application.util;

public final class AssetConstants {

    private AssetConstants() {
        throw new IllegalStateException("Utility class");
    }

    // Estados
    public static final String STATUS_PROCESSED = "PROCESSED";
    public static final String STATUS_FAILED_PREFIX = "FAILED: ";
    
    // Mensajes de log
    public static final String LOG_START_PROCESSING = "Iniciando procesamiento del asset: {}";
    public static final String LOG_UPLOAD_SUCCESS = "Subida exitosa para asset {}, URL: {}";
    public static final String LOG_PROCESSING_COMPLETED = "Procesamiento completado para asset {}";
    public static final String LOG_PROCESSING_ERROR = "Error procesando asset {}: {}";
    public static final String LOG_MARK_FAILED = "Marcando asset {} como fallido: {}";
    public static final String LOG_UPDATE_FAILED_STATUS = "Estado actualizado a FAILED para asset {}";
    public static final String LOG_UPDATE_FAILED_ERROR = "Error al actualizar estado a FAILED";
    public static final String LOG_UPLOADING_FILE = "Subiendo archivo {} ({} bytes) a almacenamiento...";

    // Mensajes de error
    public static final String ERROR_UPLOAD_FAILURE = "Error en subida a almacenamiento: ";
    public static final String ERROR_FILE_TOO_LARGE = "Tamaño de archivo excede el límite permitido";
    public static final String ERROR_UNEXPECTED = "Error inesperado en el flujo de procesamiento";
    public static final String ERROR_NULL_FILE_CONTENT = "Contenido del archivo es nulo";
    public static final String ERROR_INTERNAL_PREFIX = "Error interno: ";

    // URL de almacenamiento
    public static final String STORAGE_URL_BASE = "https://storage.example.com/files/";
    //kafka
    public static final String KAFKA_TOPIC_ASSET_UPLOADS = "asset-uploads";

   //...
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_FILENAME = "filename";
    public static final String FIELD_CONTENT_TYPE = "contentType";
    public static final String FIELD_UPLOAD_DATE = "uploadDate";
    public static final String FIELD_ID = "id";
    public static final String FIELD_URL = "url";
    public static final String SORT_DESC = "DESC";
}
