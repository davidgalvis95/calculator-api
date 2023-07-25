package com.calculator.calculatorapi.util;

import com.calculator.calculatorapi.config.UserDetailsInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Utils {
    public static String convertCurrentUriToPageUri(final HttpServletRequest request,
                                                    final int pageNumber,
                                                    final int pageSize,
                                                    final List<String> additionalParams,
                                                    final boolean isNextPage) {

        final StringBuilder additionalParamsFormatted = new StringBuilder();
        additionalParams.stream()
                .filter(param -> !Objects.equals(param, ""))
                .forEach(param -> additionalParamsFormatted.append("&").append(param));
        return request.getRequestURI() +
                "?pageNumber=" + (isNextPage ? pageNumber + 1 : pageNumber - 1) +
                "&pageSize=" + pageSize +
                additionalParamsFormatted;
    }

    public static UUID getLoggedUserId() {
        final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsInfo) {
            return ((UserDetailsInfo) principal).getId();
        }
        return null;
    }
}

