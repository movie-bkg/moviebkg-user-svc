package com.sid.moviebkg.user.util;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@Service
public class UserCmnUtils {

    public <T> boolean validateList(List<T> list, Predicate<T> predicate) {
        return list.stream().noneMatch(predicate);
    }

    public <T> List<T> filterList(List<T> incomingList, Predicate<T> predicate) {
        return incomingList.stream()
                .filter(predicate)
                .toList();
    }

    public <T, U> boolean isAlreadyPresent(T incoming, List<U> dbList, BiPredicate<T, U> tuBiPredicate) {
        boolean isPresent = false;
        for (U item : dbList) {
            if (tuBiPredicate.test(incoming, item)) {
                isPresent = true;
                break;
            }
        }
        return isPresent;
    }
}
