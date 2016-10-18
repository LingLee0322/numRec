/****************************************************************************
 *
 * $Id: PixelFormat.java 15643 2012-02-10 08:12:40Z monich $
 *
 * Copyright (C) 2008-2012 Epiphan Systems Inc. All rights reserved.
 *
 ****************************************************************************/

package com.epiphan.vga2usb;


/**
 * Typesafe enumeration of the pixel formats supported by Epiphan
 * frame grabbers.
 */
public final class PixelFormat extends Enum.Int {

    /**
     * 4 bits per pixel (2 pixels per byte) indexed RGB format. The color
     * table is built according to the following algorithm:
     * <blockquote><pre>
     * RGB4_PAL_INDEX(r,g,b) = (
     *    (((b) >> 4) & 8) |
     *    (((g) >> 5) & 6) |
     *    (((r) >> 7) & 1))
     * </pre></blockquote>
     */
    public static final PixelFormat RGB4  = new PixelFormat(0x0004,4,"RGB4");

    /**
     * 8 bits per pixel indexed RGB format. The color table is built
     * according to the following algorithm:
     * <blockquote><pre>
     * RGB8_PAL_INDEX(r,g,b) = (
     *    (((b) >> 5) & 0x07) |
     *    (((g) >> 2) & 0x38) |
     *    ((r) & 0xc0))
     * </pre></blockquote>
     */
    public static final PixelFormat RGB8  = new PixelFormat(0x0008,8,"RGB8");

    /**
     * 16 bits per pixel RGB format. Each pixel has 5 bits of red and
     * blue components and 6 bits of green component. This is the same
     * as {@link #BGR16} with red and blue component swapped.
     *
     * @see #BGR16
     */
    public static final PixelFormat RGB16 = new PixelFormat(0x0010,16,"RGB16");

    /**
     * 24 bits per pixel RGB format. Each component occupies one byte.
     * The order of the component in memory is red-green-blue. This is the
     * same as {@link #BGR24} with red and blue component swapped.
     *
     * @see #BGR24
     */
    public static final PixelFormat RGB24 = new PixelFormat(0x0018,24,"RGB24");

    /**
     * 16 bits per pixel packed YUV format.
     * This is an 8-bit 4:2:2 Component Y'CbCr format. Each 16 bit pixel
     * is represented by an unsigned eight bit luminance component and two
     * unsigned eight bit chroma components. Each  pair of pixels shares a
     * common set of chroma values. The components are ordered in memory as
     * follows: Y0, U(Cb), Y1, V(Cr).
     * <blockquote><pre>
     *   +-----------------------------------------------+
     *   |                   Pixels 0-1                  |
     *   +-----------------------------------------------+
     *   |       Increasing Address Order -->            |
     *   +-----------+-----------+-----------+-----------+
     *   |  Byte 0   |  Byte 1   |  Byte 2   |  Byte 3   |
     *   +-----------+-----------+-----------+-----------+
     *   | 8-bit Y'0 | 8-bit U   | 8-bit Y'1 | 8-bit V   |
     *   +-----------+-----------+-----------+-----------+
     * </pre></blockquote>
     * The luminance components have a range of [16, 235], while the
     * chroma value has a range of [16, 240]. This format is also known
     * as <code>YUY2</code>.
     *
     * @see <a href="http://www.fourcc.org/">fourcc.org</a>
     */
    public static final PixelFormat YUYV  = new PixelFormat(0x0100,16,"YUYV");

    /**
     * 16 bits per pixel packed YUV format.
     * This is an 8-bit 4:2:2 Component Y'CbCr format. Each 16 bit pixel
     * is represented by an unsigned eight bit luminance component and two
     * unsigned eight bit chroma components. Each  pair of pixels shares a
     * common set of chroma values. The components are ordered in memory as
     * follows: U(Cb), Y0, V(Cr), Y1.
     * <blockquote><pre>
     *   +-----------------------------------------------+
     *   |                   Pixels 0-1                  |
     *   +-----------------------------------------------+
     *   |       Increasing Address Order -->            |
     *   +-----------+-----------+-----------+-----------+
     *   |  Byte 0   |  Byte 1   |  Byte 2   |  Byte 3   |
     *   +-----------+-----------+-----------+-----------+
     *   | 8-bit U   | 8-bit Y'0 | 8-bit V   | 8-bit Y'1 |
     *   +-----------+-----------+-----------+-----------+
     * </pre></blockquote>
     * The  luminance components have a range of [16, 235], while the
     * chroma value has a range of [16, 240]. This format is also known
     * as <code>2VUY</code>.
     *
     * @see <a href="http://www.fourcc.org/">fourcc.org</a>
     */
    public static final PixelFormat UYVY  = new PixelFormat(0x0300,16,"2VUY");

    /**
     * 16 bits per pixel RGB format. Each pixel has 5 bits of red and
     * blue components and 6 bits of green component. This is the same
     * as {@link #RGB16} with red and blue component swapped.
     *
     * @see #RGB16
     */
    public static final PixelFormat BGR16 = new PixelFormat(0x0400,16,"BGR16");

    /**
     * 24 bits per pixel RGB format. Each component occupies one byte.
     * The order of the component in memory is blue-green-red. This is the
     * same as {@link #RGB24} with red and blue component swapped.
     *
     * @see #RGB24
     */
    public static final PixelFormat BGR24 = new PixelFormat(0x0800,24,"BGR24");

    /**
     * Number of bits per pixel.
     */
    private int bpp;

    /**
     * Creates a new PixelFormat instance
     * @param value integer value that identifies the pixel format to
     * the native code.
     * @param bpp number of bits per pixel for this format.
     * @param description description string
     */
    private PixelFormat(int value, int bpp, String description) {
        super(value, description);
        this.bpp = bpp;
    }

    /**
     * Get number of bits per pixel for this pixel format.
     * @return Number of bits per pixel.
     */
    public int getBpp() {
        return bpp;
    }
}
