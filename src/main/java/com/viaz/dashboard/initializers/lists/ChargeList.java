package com.viaz.dashboard.initializers.lists;

import lombok.Getter;

@Getter
public enum ChargeList {
    SOBREPESO("Sobrepeso", 2, "carrier"),
    FLETE_EN_FALSO("Flete en falso", 2, "carrier"),
    ESTADIA_EN_ORIGEN("Estadía en origen", 2, "carrier"),
    ESTADIA_EN_DESTINO("Estadía en destino", 2, "carrier"),
    ESTADIA_DE_VACIOS("Estadía de vacios", 2, "carrier"),
    MANIOBRAS_EN_PISO_EN_MANZANILLO_SUBIR_Y_BAJAR_CONTENEDOR("Maniobras en piso en Manzanillo, subir y bajar contenedor", 2, "carrier"),
    BURRITO_EN_SANTA_ROSA("Burrito en Santa Rosa", 2, "carrier"),
    BURRITO_EN_MANZANILLO_MANIOBRA_DE_CARGA("Burrito en Manzanillo, maniobra de carga", 2, "carrier"),
    BURRITO_EN_MANZANILLO_MANIOBRA_DE_DESCARGA("Burrito en Manzanillo, maniobra de vacío", 2, "carrier"),
    OTRO("Otro", 2, "carrier");

    private final String name;
    private final double penalty;
    private final String typeOfEntityRelation;

    ChargeList(String name, double penalty, String typeOfEntityRelation) {
        this.name = name;
        this.penalty = penalty;
        this.typeOfEntityRelation = typeOfEntityRelation;
    }

    public enum TypeOfEntityRelation {
        CARRIER, CLIENT
    }
}