package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

public enum LockType implements Serializable{
    WRITE("Write"),
    SHARED("Shared"),
    VM("VM");
    private final String value;
    public String toString() {
        return value;
    }
    LockType(String v) {
        value = v;
    }
    public String value() {
        return value;
    }
    public static LockType fromValue(String v) {
        for (LockType c: LockType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
