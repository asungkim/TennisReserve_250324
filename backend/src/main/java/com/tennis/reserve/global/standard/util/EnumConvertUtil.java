package com.tennis.reserve.global.standard.util;

import com.tennis.reserve.global.exception.ServiceException;

public class EnumConvertUtil {

    public static <T extends Enum<T>> T convertOrThrow(String value, Class<T> enumClass, String errorMessage) {
        if (value == null || value.isBlank()) {
            throw new ServiceException("400-1", errorMessage + " (입력값 없음)");
        }

        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ServiceException("400-2", errorMessage + " (입력값: " + value + ")");
        }
    }
}
