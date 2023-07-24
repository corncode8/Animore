package umc.animore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Pet_info")
public class Pet {

    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column(name = "pet_id")
    private Long petId;

    @Column(name = "pet_name")
    private String petName;

    @Column(name = "pet_type")
    private String petType;

    @Column(name = "pet_gender")
    private String petGender;

    @Column(name = "pet_weight")
    private double petWeight;

    @Column(name = "pet_age")
    private int petAge;

    @Column(name = "pet_specials")
    private String petSpecials;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}
