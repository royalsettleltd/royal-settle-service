package africa.royalsettle.common.util;

import africa.royalsettle.common.exception.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtil {
    private static final int MAX_PAGE_SIZE = 100;

    private PageableUtil() {}

    private static void validatePageRequest(int pageNumber, int pageSize) {
        if (pageNumber < 1) {
            throw new BadRequestException("pageNumber must be at least 1");
        }
        if (pageSize < 1 || pageSize > MAX_PAGE_SIZE) {
            throw new BadRequestException("pageSize must be between 1 and " + MAX_PAGE_SIZE);
        }
    }

    public static Pageable buildPageableObject(int pageNumber, int pageSize) {
        validatePageRequest(pageNumber, pageSize);
        return PageRequest.of(
                pageNumber - 1,
                pageSize,
                Sort.by(Sort.Direction.DESC, "id"));
    }
}
