package com.financeiro.poupeja.util;

import java.util.Collection;
import java.util.Map;

public class Utils {
	
    private Utils() {
        /* This utility class should not be instantiated */
    }

    public static boolean isEmpty(Object obj) {
        if (obj instanceof String s) {
            return s.trim().isEmpty();
        } else if (obj instanceof Collection<?> c) {
            return c.isEmpty();
        } else if (obj instanceof Map<?, ?> m) {
            return m.isEmpty();
        } else if (obj instanceof Object[] a) {
            return a.length == 0;
        }
        return obj == null;
    }
}
