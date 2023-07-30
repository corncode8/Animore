package umc.animore.model.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

    private Long reviewId;
    private Long petId;
    private Timestamp createdDate;
    private Timestamp modifiedDate;
    private String reviewContent;
    private Double reviewLike;
    private Long storeId;
    private Long userId;
    private List<ImageDTO> images;

    // 추가: 이미지 정보를 삭제된 리뷰의 ID로 설정하는 메소드
    public void setDeletedReviewId(Long reviewId) {
        // 삭제된 리뷰의 ID만 필요하므로, ID만 설정하면 됩니다.
        this.reviewId = reviewId;
    }

}