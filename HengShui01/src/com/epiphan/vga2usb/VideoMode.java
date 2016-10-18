/****************************************************************************
 *
 * $Id: VideoMode.java 14372 2011-10-08 12:36:28Z monich $
 *
 * Copyright (C) 2008-2011 Epiphan Systems Inc. All rights reserved.
 *
 ****************************************************************************/

package com.epiphan.vga2usb;

/* java.io */
import java.io.Serializable;

/**
 * Video mode descriptor.
 */
public final class VideoMode implements Serializable, Cloneable  {

    private int width;
    private int height;
    private int vfreq;

    /**
     * Creates video mode descriptor.
     *
     * @param width the width of the screen in pixels.
     * @param height the height of the screen in pixels
     * @param vfreq refresh rate (mHz)
     */
    VideoMode(int width, int height, int vfreq) {
        this.width = width;
        this.height = height;
        this.vfreq = vfreq;
    }

    /**
     * Gets the width of the active area of the screen screen in pixels.
     * @return The width of the active area of the screen in pixels.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the active area of the screen screen in pixels.
     * @return The height of the active area of the screen in pixels.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the refresh rate in Hz.
     * @return The refresh rate in Hz.
     */
    public float getRefreshRate() {
        return vfreq/1000.0f;
    }

    /**
     * Gets the hash code for this object.
     * @return The hash code for this object.
     */
    public int hashCode() {
        return (width << 20) ^ (height << 10) ^ vfreq;
    }

    /**
     * Compares this object with another.
     * @param obj the object to compare with.
     * @return <code>true</code> if the objects are equal,
     *         <code>false</code> otherwise.
     */
    public boolean equals(Object obj) {
        if (obj instanceof VideoMode) {
            VideoMode that = (VideoMode)obj;
            return this.width == that.width &&
                   this.height == that.height &&
                   this.vfreq == that.vfreq;
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
            throw new Error("VideoMode is Cloneable!");
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
        return width + "x" + height + " " + ((vfreq+50)/1000) + "." +
            (((vfreq+50)%1000)/100) + " Hz";
    }
}
