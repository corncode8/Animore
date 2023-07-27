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
    private List<ImageDTO> newImages;

}