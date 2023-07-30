package umc.animore.model.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageDTO {

    private Long imageId;
    private String imgName;
    private String imgOriName;
    private String imgPath;
}