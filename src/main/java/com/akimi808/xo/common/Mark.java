package com.akimi808.xo.common;

/**
 * Created by akimi808 on 26/12/2017.
 */
public enum Mark {
    X,
    O;

    public static Mark valueOf(int i) {
        if (i == 0) {
            return O;
        } else {
            return X;
        }
    }

    public Mark opposite() {
        if (this.equals(X)) {
            return O;
        } else {
            return X;
        }
    }
}
