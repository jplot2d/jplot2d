/*
 * Copyright 2010 Jingjing Li.
 *
 * This file is part of jplot2d.
 *
 * jplot2d is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * jplot2d is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with jplot2d. If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.env;

/**
 * A token to represent a batch operation. When beginning a batch, a token is created.
 * Later when ending a batch, the token must be verified. This make nested batch possible.
 *
 * @author Jingjing Li
 */
public class BatchToken {

    private final int batchDepth;

    private final int[] batchSND;

    /**
     * Create a batch token.
     *
     * @param batchDepth the nesting depth
     * @param batchSND   batch SN in depth array
     */
    BatchToken(int batchDepth, int[] batchSND) {
        this.batchDepth = batchDepth;
        this.batchSND = batchSND;
    }

    /**
     * Verify this batch token with the given condition.
     *
     * @param batchDepth the nesting depth
     * @param batchSND   batch SN in depth array
     * @return <code>true</code> if this token matches the given condition
     */
    boolean match(int batchDepth, int[] batchSND) {
        if (this.batchDepth != batchDepth) {
            return false;
        } else {
            for (int depth = 0; depth < batchDepth; depth++) {
                if (this.batchSND[depth] != batchSND[depth]) {
                    return false;
                }
            }
            return true;
        }
    }

    public String toString() {
        if (batchDepth == 0) {
            throw new Error("Not in batch block.");
        }
        String result = "(" + batchSND[0];
        for (int i = 1; i < batchDepth; i++) {
            result += "-" + batchSND[i];
        }
        return result + ")";
    }

}
