package com.webstore.demo.models;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "product")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "units_per_carton")
    private Integer unitsPerCarton;

    @Column(name = "img_path")
    private String imgPath;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Double price;

    public Product() {
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                '}';
    }
}
