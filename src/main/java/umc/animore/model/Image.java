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
@Table(name = "image")
public class Image {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "img_id")
    private Long imageId;

    @Column(name = "img_name")
    private String imgName;

    @Column(name = "img_ori_name")
    private String imgOriName;

    @Column(name = "img_path")
    private String imgPath;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="store_id")
    @JsonIgnore
    private Store store;

}

