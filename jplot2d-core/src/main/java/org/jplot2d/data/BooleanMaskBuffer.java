/*
 * Copyright 2010-2013 Jingjing Li.
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
package org.jplot2d.data;

/**
 * This class extends ImageMaskBuffer and stores data internally as booleans.
 * It can optionally take an offset, so that mask in an existing array can be used
 * even if the interesting mask doesn't start at array location zero.
 *
 * @author Jingjing Li
 */
public interface BooleanMaskBuffer extends ImageMaskBuffer {

    public static class Array implements BooleanMaskBuffer {
        private final boolean[] mask;
        private final int offset;

        public Array(boolean[] mask) {
            this(mask, 0);
        }

        public Array(boolean[] mask, int offset) {
            this.mask = mask;
            this.offset = offset;
        }

        public boolean isMasked(int x, int y) {
            return mask[offset + x + y];
        }
    }

    public static class Array2D implements BooleanMaskBuffer {
        private final boolean[][] mask;
        private final int xoffset, yoffset;

        public Array2D(boolean[][] mask) {
            this(mask, 0, 0);
        }

        public Array2D(boolean[][] mask, int xoffset, int yoffset) {
            this.mask = mask;
            this.xoffset = xoffset;
            this.yoffset = yoffset;
        }

        public boolean isMasked(int x, int y) {
            return mask[yoffset + y][xoffset + x];
        }
    }

}