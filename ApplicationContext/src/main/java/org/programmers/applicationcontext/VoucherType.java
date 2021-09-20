package org.programmers.applicationcontext;
import java.util.Arrays;

public enum VoucherType {
    FIXED_VOUCHER("1"),
    PERCENT_VOUCHER("2");

    private final String number;

    VoucherType(String number) {
        this.number = number;
    }

    public static VoucherType of(String str) {
        return Arrays.stream(VoucherType.values())
                .filter(voucherType -> str.equals(voucherType.number))
                .findFirst()
                .orElse(null);
    }
}