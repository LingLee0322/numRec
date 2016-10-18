/****************************************************************************
 *
 * $Id: BasicFrame.java 14372 2011-10-08 12:36:28Z monich $
 *
 * Copyright (C) 2008-2011 Epiphan Systems Inc. All rights reserved.
 *
 ****************************************************************************/

package com.epiphan.vga2usb;


/**
 * Base class for {@link Frame} and {@link RawFrame}.
 */
class BasicFrame {

    /** Frame width. */
    protected int width;

    /** Frame height. */
    protected int height;

    /** Frame pixels. */
    protected byte[] pixels;

    /** Part of the pixels array which is actually occupied by pixels. */
    protected int length;

    /** The pixel format. */
    protected PixelFormat format;

    /**
     * Creates a new <code>BasicFrame</code>.
     * @param w width of the frame
     * @param h height of the frame
     * @param f pixel format
     * @param pix array of pixels
     * @param len length of the valid pixels
     */
    protected BasicFrame(int w, int h, PixelFormat f, byte[] pix, int len) {
        width = w;
        height = h;
        pixels = pix;
        length = len;
        format = f;
    }

    /**
     * Returns the width of the frame (number of columns).
     * @return The frame width.
     */
    public final int getWidth() {
        return width;
    }

    /**
     * Returns the height of the frame (number of rows).
     * @return The frame height.
     */
    public final int getHeight() {
        return height;
    }
}
