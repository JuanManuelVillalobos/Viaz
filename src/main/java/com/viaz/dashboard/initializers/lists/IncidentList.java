package com.viaz.dashboard.initializers.lists;

import lombok.Getter;

@Getter
public enum IncidentList {
    RESPUESTA_TARDIA_A_DISPONIBILIDAD("Respuesta tardía a disponibilidad", 1, "carrier"),
    DOCUMENTACION_O_FOLIO_TARDIO("Documentación o folio tardío", 1, "carrier"),
    OBTENCION_TARDIA_DE_INFORMACION_DE_UNIDADES_("Obtención tardía de información de unidades", 1, "carrier"),
    INGRESO_TARDIO_A_CITA_DE_CARGA("Ingreso tardío a cita de carga", 1, "carrier"),
    SALIDA_TARDE_A_RUTA("Salida tarde a ruta",1 , "carrier"),
    LLEGADA_TARDIA_A_DESTINO("Llegada tardía a destino",1 , "carrier"),
    FALLA_MECANICA("Falla mecánica",1 , "carrier"),
    INCONVENIENTE_EN_TRATO_DEL_OPERADOR("Inconveniente en trato del operador", 1, "carrier"),
    INTOXICACION("Intoxicación", 1, "carrier"),
    RETRASO_EN_ENTREGA_DE_VACIO("Retraso en entrega de vacío", 1, "carrier"),
    LIBERACION_DE_DESPACHO_FALLIDA("Liberación de despacho fallida", 2, "carrier"),
    ENVIO_TARDIO_DE_MANIOBRAS_DE_CARGA("Envío tardío de maniobras de carga",1,"client"),
    ENVIO_TARDIDO_DE_MANIOBRAS_DE_VACIOS("Envío tardío de maniobras de vacíos",1,"client"),
    RETRASO_EN_DESCARGA("Retraso en descarga",1,"client"),
    NO_NOTIFICAR_REQUISITO_DE_LAVADO("No notificar requisito de lavado", 1, "client"),
    CONTENEDOR_VACIO_DANADO("Contenedor vacío/dañado",1,"client");


    private final String name;
    private final double penalty;
    private final String typeOfEntityRelation;

    IncidentList(String name, double penalty, String typeOfEntityRelation) {
        this.name = name;
        this.penalty = penalty;
        this.typeOfEntityRelation = typeOfEntityRelation;
    }

    public enum TypeOfEntityRelation {
        CARRIER, CLIENT
    }
}
