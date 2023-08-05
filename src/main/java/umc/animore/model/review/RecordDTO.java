package umc.animore.model.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecordDTO {
    private Long recordId;
    private Timestamp searchCreateAt;
    private StoreDTO storeDTO;
    private Long userId;
}

