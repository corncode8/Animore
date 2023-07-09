package com.example.animore.Search.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "search_history")
public class SearchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "search_id")
    private int searchId;
    @Column(name = "user_idx")
    private int userIdx;
    @Column(name = "search_query")
    private String searchQuery;
    @Column(name = "search_create_at")
    private String searchCreateAt;
}
