package pl.mb.warehouse.domain;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

public final class DomainAsserts {

    public static void assertArgumentNotEmpty(String value, String message) {
        assertArgumentNotNull(value, message);
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentDomainException(message);
        }
    }

    public static void assertRange(Comparable a, Comparable b, String message) {
        assertArgumentNotNull(a, message);
        assertArgumentNotNull(b, message);
        if (a.compareTo(b) > 0) {
            throw new IllegalArgumentDomainException(message);
        }
    }

    public static void assertArgumentNotNull(Object value, String message) {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentDomainException(message);
        }
    }

    public static void assertCollectionNotEmpty(Collection<?> collection, String message) {
        assertArgumentNotNull(collection, message);
        if (collection.size() == 0) {
            throw new IllegalArgumentDomainException(message);
        }
    }

    public static <T> void assertArrayNotEmpty(T[] array, String message) {
        assertArgumentNotNull(array, message);
        if (array.length == 0) {
            throw new IllegalArgumentDomainException(message);
        }
    }

    public static void assertTrue(boolean b, String message) {
        if (!b) {
            throw new IllegalArgumentDomainException(message);
        }
    }
}
