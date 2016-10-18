/****************************************************************************
 *
 * $Id: GrabParameters.java 14372 2011-10-08 12:36:28Z monich $
 *
 * Copyright (C) 2009-2011 Epiphan Systems Inc. All rights reserved.
 *
 ****************************************************************************/

package com.epiphan.vga2usb;

/* java.io */
import java.io.Serializable;

/**
 * VGA capture parameters.
 * @since 3.22.2
 */
public final class GrabParameters implements Serializable, Cloneable {

    /** Indicates that hshift field is used */
    public static final int VALID_HSHIFT     = 0x0001;
    /** Indicates that phase field is used */
    public static final int VALID_PHASE      = 0x0002;
    /** Indicates that all gain_{rgb} and offset_{rgb} fields are used */
    public static final int VALID_OFFSETGAIN = 0x0004;
    /** Indicates that vshift field is used */
    public static final int VALID_VSHIFT     = 0x0008;
    /** Indicates that pllshift field is used */
    public static final int VALID_PLLSHIFT   = 0x0010;
    /** Indicates that grab_flags and grab_flags_mask are used */
    public static final int VALID_GRABFLAGS  = 0x0020;

    /**
     * Grab image upside-down
     */
    public static final int GRAB_BMP_BOTTOM_UP    = 0x10000;

    /**
     * Wide mode flag. Sometimes 4:3 and wide modes with the same height
     * are indistinguishable, this flag can force choosing the wide mode.
     */
    public static final int GRAB_PREFER_WIDE_MODE = 0x20000;

    /**
     * Validity flags.
     *
     * @see #VALID_HSHIFT
     * @see #VALID_PHASE
     * @see #VALID_OFFSETGAIN
     * @see #VALID_VSHIFT
     * @see #VALID_PLLSHIFT
     * @see #VALID_GRABFLAGS
     */
    public int flags;

    /** Horizontal shift. */
    public int hshift;

    /** Pixel sampling phase. */
    public int phase;

    /** Gain (contrast) for the red channel. */
    public int gain_r;

    /** Gain (contrast) for the green channel. */
    public int gain_g;

    /** Gain (contrast) for the blue channel. */
    public int gain_b;

    /** Offset (brightness) for the red channel. */
    public int offset_r;

    /** Offset (brightness) for the green channel. */
    public int offset_g;

    /** Offset (brightness) for the blue channel. */
    public int offset_b;

    /** Vertical shift. */
    public int vshift;

    /** PLL adjustment. */
    public int pllshift;

    /**
     * Grab flags.
     * @see #GRAB_BMP_BOTTOM_UP
     * @see #GRAB_PREFER_WIDE_MODE
     */
    public int grab_flags;

    /** Which bits from grab_flags are used */
    public int grab_flags_mask;

    /**
     * Creates an empty <code>GrabParameters</code> object.
     */
    public GrabParameters() {}

    /**
     * Unpacks JNI-style int array into grab parameters.
     * @param params JNI-style grab parameters.
     */
    void setParameters(int [] params) {
        flags = params[0];
        hshift = params[1];
        phase = params[2];
        gain_r = params[3];
        gain_g = params[4];
        gain_b = params[5];
        offset_r = params[6];
        offset_g = params[7];
        offset_b = params[8];
        vshift = params[9];
        pllshift = params[10];
        grab_flags = params[11];
        grab_flags_mask = params[12];
    }

    /**
     * Packs grab parameters into JNI-style int array.
     * @param params preallocated JNI-style int array.
     * @return the input array or a newly allocated one if the input
     * parameter is <code>null</code>.
     */
    int [] getParameters(int [] params) {
        if (params == null) {
            params = new int[13];
        }
        params[0] = flags;
        params[1] = hshift;
        params[2] = phase;
        params[3] = gain_r;
        params[4] = gain_g;
        params[5] = gain_b;
        params[6] = offset_r;
        params[7] = offset_g;
        params[8] = offset_b;
        params[9] = vshift;
        params[10] = pllshift;
        params[11] = grab_flags;
        params[12] = grab_flags_mask;
        return params;
    }

    /**
     * Packs grab parameters into JNI-style int array.
     * @return newly allocated JNI-style int array.
     */
    int [] getParameters() {
        return getParameters(null);
    }

    /**
     * Clones this object.
     * @return The clone of this object
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException x) {
            throw new Error("GrabParameters is Cloneable!");
        }
    }
}
