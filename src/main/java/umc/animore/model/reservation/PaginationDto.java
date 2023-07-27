package umc.animore.model.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationDto {
    private int startPage;
    private int endPage;
    private boolean prev;
    private boolean next;

    public PaginationDto(Page<?> page) {
        int currentPage = page.getNumber(); // 현재 페이지
        int totalPages = page.getTotalPages(); // 전체 페이지 수

        int modVal = currentPage % 10;
        startPage = (currentPage / 10) * 10 + 1;
        endPage = startPage + 9;

        if (modVal == 0) {
            startPage -= 10;
            endPage -= 10;
        }

        if (totalPages < endPage) {
            endPage = totalPages;
        }

        prev = startPage != 1;
        next = endPage != totalPages;
    }

    public int getStartPage() {
        return startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public boolean isPrev() {
        return prev;
    }

    public boolean isNext() {
        return next;
    }
}