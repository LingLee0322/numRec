/****************************************************************************
 *
 * $Id: RawFrame.java 14372 2011-10-08 12:36:28Z monich $
 *
 * Copyright (C) 2008-2011 Epiphan Systems Inc. All rights reserved.
 *
 ****************************************************************************/

package com.epiphan.vga2usb;


/**
 * A single grabbed frame in arbitrary format supported by VGA2USB
 * hardware. Gives the client direct access to the pixel buffer. Unlike
 * the {@link Frame} class, it doesn't provide a convenient way to
 * transform pixels into an {@link java.awt.Image}. It's up to the client
 * to decide what to do with the raw pixels.
 *
 * @see Frame
 * @see Grabber
 */
public final class RawFrame extends BasicFrame {

    /**
     * Creates a new <code>RawFrame</code>.
     * @param width width of the frame
     * @param height height of the frame
     * @param format pixel format
     * @param pix array of pixels
     * @param len length of the valid pixels
     */
    RawFrame(int width, int height, PixelFormat format, byte[] pix, int len) {
        super(width, height, format, pix, len);
    }

    /**
     * Get the pixel format.
     * @return The pixel format.
     */
    public PixelFormat getPixelFormat() {
        return format;
    }

    /**
     * Gives the caller direct access to the pixel buffer. Note that the
     * pixels may occupy only part of the array under certain (fairly rare)
     * conditions. The number of bytes actually containing the pixels is
     * obtained by calling {@link #getPixelBufferLength} method. The rest
     * of the array should be ignored.
     *
     * @return Reference to the pixel buffer.
     * @see #getPixelBufferLength
     */
    public byte[] getPixelBuffer() {
        return pixels;
    }

    /**
     * Returns number of bytes in the pixel buffer that are actually occupied
     * by pixel data. The rest of the buffer should be ignored.
     *
     * @return The actual size of the frame.
     * @see #getPixelBuffer
     */
    public int getPixelBufferLength() {
        return length;
    }
}
