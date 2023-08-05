package umc.animore.model;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

// ORM - Object Relation Mapping

@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {
    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "store_id")
    private Store store;            // 업체_idx

    private String username;
    private String password;
    private String email;
    private String role; //ROLE_USER, ROLE_ADMIN
    private String address;
    private String phone;
    private String gender;
    private String nickname;
    private String aboutMe;
    private String nationality;
    private String birthday;

    @OneToOne(mappedBy = "user",cascade=CascadeType.ALL)
    private Image image;

    @OneToMany(mappedBy ="user", cascade=CascadeType.ALL)
    private List<Pet> pets = new ArrayList<Pet>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<Review>();

    // OAuth를 위해 구성한 추가 필드 2개
    private String provider;
    private String providerId;

    @CreationTimestamp
    private Timestamp createDate;

    @UpdateTimestamp
    private Timestamp updateTime;

    public void setUserId(Long id) {
        this.id=id;
    }
}
