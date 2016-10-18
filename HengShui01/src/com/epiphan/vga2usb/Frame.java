/****************************************************************************
 *
 * $Id: Frame.java 14372 2011-10-08 12:36:28Z monich $
 *
 * Copyright (C) 2008-2011 Epiphan Systems Inc. All rights reserved.
 *
 ****************************************************************************/

package com.epiphan.vga2usb;

/* java.awt */
import java.awt.Image;
import java.awt.Transparency;

/* java.awt.image */
import java.awt.image.Raster;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.DataBufferByte;
import java.awt.image.ComponentColorModel;

/* java.awt.color */
import java.awt.color.ColorSpace;


/**
 * A single grabbed frame.
 *
 * @see RawFrame
 * @see Grabber
 */
public final class Frame extends BasicFrame {

    private Image image;

    /**
     * Creates a new <code>Frame</code>.
     * @param width width of the frame
     * @param height height of the frame
     * @param format pixel format
     * @param pix array of pixels
     * @param len length of the valid pixels
     */
    Frame(int width, int height, PixelFormat format, byte[] pix, int len) {
        super(width, height, format, pix, len);
    }

    /**
     * Return an Image representation of this frame
     * @return The Image that represents the contents of this frame
     */
    public Image getImage() {
        if (image == null) {
            image = createImage();
        }
        return image;
    }

    /**
     * Creates a new image from frame pixels
     * @return The created image
     */
    private Image createImage() {
        ColorModel cm;
        if (format == PixelFormat.RGB24) {
           cm = new ComponentColorModel(
                ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] {8,8,8},
                false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        } else {
            throw new UnsupportedOperationException();
        }
        SampleModel sm = cm.createCompatibleSampleModel(width, height);
        DataBuffer db = new DataBufferByte(pixels, length);
        WritableRaster raster = Raster.createWritableRaster(sm, db, null);
        return new BufferedImage(cm, raster, false, null);
    }
}
