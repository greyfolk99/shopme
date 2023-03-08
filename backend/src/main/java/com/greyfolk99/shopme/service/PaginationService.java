package com.greyfolk99.shopme.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class PaginationService {

    private static final int BAR_LENGTH = 5;

    public List<Integer> getBarNumbers(int currentPageNumber, int totalPages) {
        currentPageNumber--;
        int startNumber = Math.max(currentPageNumber - (BAR_LENGTH / 2), 0) + 1;
        int endNumber = Math.min(startNumber + BAR_LENGTH, totalPages) + 1;

        List<Integer> numbers = IntStream.range(startNumber, endNumber).boxed().collect(Collectors.toList());
        return numbers; //
    }
    public int currentBarLength() {
        return BAR_LENGTH;
    }
}

