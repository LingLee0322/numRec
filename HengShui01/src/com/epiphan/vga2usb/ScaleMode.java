/****************************************************************************
 *
 * $Id: ScaleMode.java 15643 2012-02-10 08:12:40Z monich $
 *
 * Copyright (C) 2009-2012 Epiphan Systems Inc. All rights reserved.
 *
 ****************************************************************************/

package com.epiphan.vga2usb;

/* java.util */
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Typesafe enumeration of scaling algorithms supported by the driver.
 * @since 3.27.1
 */
public final class ScaleMode extends Enum.Int {

    /* List of known modes */
    private static List modes;

    /**
     * Not a valid value.
     */
    static final ScaleMode Unknown =
        new ScaleMode(-1, "Unknown");

    /**
     * No scaling
     */
    public static final ScaleMode None =
        new ScaleMode(0, "None");

    /**
     * Nearest neighbor algorithm.
     */
    public static final ScaleMode NearestNeighbor =
        new ScaleMode(1, "NearestNeighbor");

    /**
     * Weighted average algorithm.
     */
    public static final ScaleMode WeightedAverage =
        new ScaleMode(2, "WeightedAverage");

    /**
     * Fast bilinear algorithm.
     */
    public static final ScaleMode FastBilinear =
        new ScaleMode(3, "FastBilinear");

    /**
     * Bilinear algorithm.
     */
    public static final ScaleMode Bilinear =
        new ScaleMode(4, "Bilinear");

    /**
     * Bicubic algorithm.
     */
    public static final ScaleMode Bicubic =
        new ScaleMode(5, "Bicubic");

    /**
     * Experimental algorithm.
     */
    public static final ScaleMode Experimental =
        new ScaleMode(6, "Experimental");

    /**
     * Yet another nearest neighbor algorithm.
     */
    public static final ScaleMode Point =
        new ScaleMode(7, "Point");

    /**
     * Yet another weighted average algorithm.
     */
    public static final ScaleMode Area =
        new ScaleMode(8, "Area");

    /**
     * Luma bicubic, chroma bilinear.
     */
    public static final ScaleMode BicubLin =
        new ScaleMode(9, "BicubLin");

    /**
     * Sinc algorithm.
     */
    public static final ScaleMode Sinc =
        new ScaleMode(10, "Sinc");

    /**
     * Lanczos algorithm.
     */
    public static final ScaleMode Lanczos =
        new ScaleMode(11, "Lanczos");

    /**
     * Natural bicubic spline algorithm.
     */
    public static final ScaleMode Spline =
        new ScaleMode(12, "Spline");

    /**
     * Initializes the type list and pre-fills it with known device types
     */
    static {
        modes = new ArrayList(13);
        modes.add(None);
        modes.add(NearestNeighbor);
        modes.add(WeightedAverage);
        modes.add(FastBilinear);
        modes.add(Bilinear);
        modes.add(Bicubic);
        modes.add(Experimental);
        modes.add(Point);
        modes.add(Area);
        modes.add(BicubLin);
        modes.add(Sinc);
        modes.add(Lanczos);
        modes.add(Spline);
        Collections.sort(modes);
    };

    /**
     * Creates a new <code>ScaleMode</code> instance.
     * @param value integer value that identifies the scale mode
     * @param description description string
     */
    private ScaleMode(int value, String description) {
        super(value, description);
    }

    /**
     * Converts native scale mode value into a <tt>ScaleMode</tt> object.
     * @param value integer value that identifies the scale mode
     * @return corresponding <code>ScaleMode</code> object
     */
    public static ScaleMode getScaleMode(int value) {
        int index = search(modes, value);
        return (index >= 0) ? (ScaleMode)modes.get(index) : Unknown;
    }
}
