/****************************************************************************
 *
 * $Id: Size.java 15647 2012-02-10 21:19:04Z monich $
 *
 * Copyright (C) 2012 Epiphan Systems Inc. All rights reserved.
 *
 ****************************************************************************/

package com.epiphan.vga2usb;

/* java.io */
import java.io.Serializable;

/**
 * Image dimensions.
 * @since 3.27.1
 */
public final class Size implements Serializable, Cloneable  {

    private int width;
    private int height;

    /**
     * Creates a <code>Size</code> object.
     *
     * @param width the width in pixels.
     * @param height the height in pixels
     *
     * @throws IllegalArgumentException if either <code>width</code> or
     * <code>height</code> is negative.
     */
    public Size(int width, int height) {
        if (width < 0) throw new IllegalArgumentException("width");
        if (height < 0) throw new IllegalArgumentException("height");
        this.width = width;
        this.height = height;
    }

    /**
     * Gets the width in pixels.
     * @return The width in pixels.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height in pixels.
     * @return The height in pixels.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the hash code for this object.
     * @return The hash code for this object.
     */
    public int hashCode() {
        int sum = width + height;
        return sum * (sum + 1)/2 + width;
    }

    /**
     * Compares this object with another.
     * @param obj the object to compare with.
     * @return <code>true</code> if the objects are equal,
     *         <code>false</code> otherwise.
     */
    public boolean equals(Object obj) {
        if (obj instanceof Size) {
            Size that = (Size)obj;
            return this.width == that.width && this.height == that.height;
        } else {
            return false;
        }
    }

    /**
     * Clones this object.
     * @return The clone of this object
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException x) {
            throw new Error("Size is Cloneable!");
        }
    }

    /**
     * Returns a string representation of this object. This method
     * is intended to be used only for debugging purposes, and the
     * content and format of the returned string may vary between
     * implementations. The returned string may be empty but may not
     * be <code>null</code>.
     *
     * @return  A string representation of this object.
     */
    public String toString() {
        return width + "x" + height;
    }
}
