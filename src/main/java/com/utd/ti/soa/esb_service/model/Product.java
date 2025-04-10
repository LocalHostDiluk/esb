package com.utd.ti.soa.esb_service.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product {
    private String nombre;
    private String precio;
    private String stock;
    private String descripcion;
    private String imagen;
    private String categoria_id;
}
