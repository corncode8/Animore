package umc.animore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "review_image")
public class ReviewImage {

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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    @JsonIgnore
    private Review review;

    // 이미지와 Review를 연결하는 메서드
    public void setReview(Review review) {
        this.review = review;
    }

}
