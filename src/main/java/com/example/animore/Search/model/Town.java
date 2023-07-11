package com.example.animore.Search.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "town")
public class Town {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "town_id")
    private Integer townId;
    @Column(name = "town_name")
    private String townName;
    @Column(name = "city")
    private String city;
    @Column(name = "district")
    private String district;

    /*

    //다대일 관계
    //한 개의 Town이 여러 개의 Store를 가질 수 있지만, 각각의 Store는 하나의 Town에만 속할 수 있는 관계
    @OneToMany(mappedBy = "town")
    @JsonIgnore
    @JsonBackReference
    private List<Store> stores = new ArrayList<>();

    public List<Store> getStores() {
        return stores;
    }


     */
}
