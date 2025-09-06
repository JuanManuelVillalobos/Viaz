package com.viaz.dashboard.initializers.lists;

import lombok.Getter;

@Getter
public enum ChargeList {
    SOBREPESO("Sobrepeso"),
    FLETE_EN_FALSO("Flete en falso"),
    ESTADIA_EN_ORIGEN("Estadía en origen"),
    ESTADIA_EN_DESTINO("Estadía en destino"),
    ESTADIA_DE_VACIOS("Estadía de vacios"),
    MANOBRAS_EN_PISO_EN_MANZANILLO_SUBIR_Y_BAJAR_CONTENEDOR("Manobras en piso en Manzanillo, subir y bajar contenedor"),
    BURRITO_EN_SANTA_ROSA("Burrito en Santa Rosa"),
    BURRITO_EN_MANZANILLO_MANIOBRA_DE_CARGA("Burrito en Manzanillo, maniobra de carga"),
    BURRITO_EN_MANZANILLO_MANIOBRA_DE_DESCARGA("Burrito en Manzanillo, maniobra de vacío");

    private final String name;

    ChargeList(String name) {
        this.name = name;
    }
}