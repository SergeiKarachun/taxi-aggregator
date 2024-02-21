package by.sergo.driverservice.util;

import by.sergo.driverservice.service.exception.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.util.Arrays;

public class PageRequestUtil {
    public static <T> PageRequest getPageRequest(Integer page, Integer size, String orderBy, Class<T> clazz) {
        if (page < 1 || size < 1) {
            throw new BadRequestException(ExceptionMessageUtil.getInvalidRequestMessage(page, size));
        } else if (orderBy != null) {
            Arrays.stream(clazz.getDeclaredFields())
                    .map(Field::getName)
                    .filter(s -> s.contains(orderBy.toLowerCase()))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException(ExceptionMessageUtil.getInvalidSortingParamRequestMessage(orderBy)));
            return PageRequest.of(page - 1, size).withSort(Sort.by(Sort.Order.asc(orderBy.toLowerCase())));
        } else {
            return PageRequest.of(page - 1, size);
        }
    }
}
