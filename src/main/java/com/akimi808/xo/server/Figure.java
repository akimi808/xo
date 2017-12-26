package com.akimi808.xo.server;

/**
 * Created by akimi808 on 26/12/2017.
 */
public enum Figure {
    X,
    O;

    public static Figure valueOf(int i) {
        if (i == 0) {
            return O;
        } else {
            return X;
        }
    }

    public Figure opposite() {
        if (this.equals(X)) {
            return O;
        } else {
            return X;
        }
    }
}
