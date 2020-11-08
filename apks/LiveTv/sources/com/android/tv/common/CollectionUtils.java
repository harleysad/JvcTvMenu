package com.android.tv.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CollectionUtils {
    public static <T> T[] concatAll(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        int offset2 = offset;
        for (T[] array2 : rest) {
            System.arraycopy(array2, 0, result, offset2, array2.length);
            offset2 += array2.length;
        }
        return result;
    }

    public static <T> List<T> union(Collection<T> originals, Collection<T> toAdds, Comparator<T> comparator) {
        List<T> result = new ArrayList<>(originals);
        Collections.sort(result, comparator);
        List<T> resultToAdd = new ArrayList<>();
        for (T toAdd : toAdds) {
            if (Collections.binarySearch(result, toAdd, comparator) < 0) {
                resultToAdd.add(toAdd);
            }
        }
        result.addAll(resultToAdd);
        return result;
    }

    public static <T> List<T> subtract(Collection<T> originals, T[] toSubtracts, Comparator<T> comparator) {
        List<T> result = new ArrayList<>(originals);
        Collections.sort(result, comparator);
        for (T toSubtract : toSubtracts) {
            int index = Collections.binarySearch(result, toSubtract, comparator);
            if (index >= 0) {
                result.remove(index);
            }
        }
        return result;
    }

    public static <T> boolean containsAny(Collection<T> c1, Collection<T> c2, Comparator<T> comparator) {
        List<T> contains = new ArrayList<>(c1);
        Collections.sort(contains, comparator);
        for (T iterate : c2) {
            if (Collections.binarySearch(contains, iterate, comparator) >= 0) {
                return true;
            }
        }
        return false;
    }
}
