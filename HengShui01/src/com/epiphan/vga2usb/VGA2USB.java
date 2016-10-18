/****************************************************************************
 *
 * $Id: VGA2USB.java 15657 2012-02-11 09:23:30Z monich $
 *
 * Copyright (C) 2008-2012 Epiphan Systems Inc. All rights reserved.
 *
 ****************************************************************************/

package com.epiphan.vga2usb;


/**
 * VGA2USB constants.
 */
public abstract class VGA2USB {

    /* DirectShowFlags bits */

    /**
     * {@link PropertyKey#DirectShowFlags DirectShowFlags} bit which indicates
     * that DirectShow fps limit is on. The actual fps limit is defined by
     * {@link PropertyKey#DirectShowMaxFps DirectShowMaxFps} property.
     */
    public static final int DSHOW_LIMIT_FPS       = 0x0200;
    /**
     * {@link PropertyKey#DirectShowFlags DirectShowFlags} bit which indicates
     * that DirectShow stream should be flopped vertically.
     */
    public static final int DSHOW_FLIP_VERTICALLY = 0x0400;
    /**
     * {@link PropertyKey#DirectShowFlags DirectShowFlags} bit which
     * indicates that DirectShow fps must be constant. If frames are
     * captured slower than required, duplicate frames will be inserted
     * into the stream. The actual fps value is defined by
     * {@link PropertyKey#DirectShowMaxFps DirectShowMaxFps} property.
     */
    public static final int DSHOW_FIX_FPS         = 0x0800;

    /* InputSignalType bits */

    /**
     * {@link PropertyKey#InputSignalType InputSignalType} bit which indicates
     * that analog input signal is detected by the grabber
     */
    public static final int INPUT_ANALOG          = 0x0001;
    /**
     * {@link PropertyKey#InputSignalType InputSignalType} bit which indicates
     * that digital input signal is detected by the grabber
     */
    public static final int INPUT_DIGITAL         = 0x0002;
    /**
     * {@link PropertyKey#InputSignalType InputSignalType} bit which indicates
     * that SoG input signal is detected by the grabber
     */
    public static final int INPUT_SOG             = 0x0004;
    /**
     * {@link PropertyKey#InputSignalType InputSignalType} bit which indicates
     * that composite signal is detected by the grabber
     */
    public static final int INPUT_COMPOSITE       = 0x0008;
    /**
     * Number of frames per second used by
     * {@link PropertyKey#DirectShowMaxFps DirectShowMaxFps} property is
     * multiplied by 100 to allow for fractional
     * numbers and yet avoid using floating point.
     */
    public static final int FPS_DENOMINATOR       = 100;

    /* DeviceCaps bits */

    /**
     * {@link PropertyKey#DeviceCaps DeviceCaps} bit which indicates that
     * grabber supports VGA capture
     */
    public static final int CAPS_VGA_CAPTURE      = 0x0001;

    /**
     * {@link PropertyKey#DeviceCaps DeviceCaps} bit which indicates that
     * grabber supports DVI single-link capture.
     */
    public static final int CAPS_DVI_CAPTURE      = 0x0002;
    /**
     * {@link PropertyKey#DeviceCaps DeviceCaps} bit which indicates that
     * grabber supports DVI dual-link capture.
     */
    public static final int CAPS_DVI_DUAL_LINK    = 0x0004;
    /**
     * {@link PropertyKey#DeviceCaps DeviceCaps} bit which indicates that
     * grabber supports KVM functionality.
     */
    public static final int CAPS_KVM              = 0x0008;
    /**
     * {@link PropertyKey#DeviceCaps DeviceCaps} bit which indicates that
     * hardware has programmable EDID.
     */
    public static final int CAPS_EDID             = 0x0010;
    /**
     * {@link PropertyKey#DeviceCaps DeviceCaps} bit which indicates that
     * grabber supports on-board compression.
     */
    public static final int CAPS_HW_COMPRESSION   = 0x0020;
    /**
     * {@link PropertyKey#DeviceCaps DeviceCaps} bit which indicates that
     * grabber supoprts adjustable sync thresholds.
     */
    public static final int CAPS_SYNC_THRESHOLD   = 0x0040;
    /**
     * {@link PropertyKey#DeviceCaps DeviceCaps} bit which indicates that
     * grabber supports hardware scaling.
     */
    public static final int CAPS_HW_SCALE         = 0x0080;

    /**
     * The name of VGA2USB native library
     */
    static final String LIBRARY = "v2ujni";

    /**
     * Constructor is disabled.
     */
    private VGA2USB() {
        throw new Error("VGA2USB is a collection of static constants");
    }
}
