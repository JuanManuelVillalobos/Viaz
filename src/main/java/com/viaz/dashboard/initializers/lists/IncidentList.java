package com.viaz.dashboard.initializers.lists;

import lombok.Getter;

@Getter
public enum IncidentList {
    RESPUESTA_TARDIA_A_DISPONIBILIDAD("Respuesta tardía a disponibilidad"),
    DOCUMENTACION_O_FOLIO_TARDIO("Documentación o folio tardío"),
    OBTENCION_TARDIA_DE_INFORMACION_DE_UNIDADES_("Obtención tardía de información de unidades"),
    INGRESO_TARDIO_A_CITA_DE_CARGA("Ingreso tardío a cita de carga"),
    SALIDA_TARDE_A_RUTA("Salida tarde a ruta"),
    LLEGADA_TARDIA_A_DESTINO("Llegada tardía a destino"),
    FALLA_MECANICA("Falla mecánica"),
    INCONVENIENTE_EN_TRATO_DEL_OPERADOR("Inconveniente en trato del operador"),
    INTOXICACION("Intoxicación"),
    RETRASO_EN_ENTREGA_DE_VACIO("Retraso en entrega de vacío");


    private final String name;

    IncidentList(String name) {
        this.name = name;
    }
}
