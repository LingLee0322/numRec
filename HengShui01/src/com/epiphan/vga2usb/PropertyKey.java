/****************************************************************************
 *
 * $Id: PropertyKey.java 15657 2012-02-11 09:23:30Z monich $
 *
 * Copyright (C) 2009-2012 Epiphan Systems Inc. All rights reserved.
 *
 ****************************************************************************/

package com.epiphan.vga2usb;


/**
 * Typesafe enumeration of property keys.
 */
public final class PropertyKey extends Enum.Int {

    /** GET only [uint16] */
    static final int ProductID = 0;
    /** GET only [product_type] */
    static final int ProductType = 1;

    /**
     * Fixed resolution for DirectShow stream (Windows only).
     * <br/>GET/SET  [size]
     */
    public static final PropertyKey DirectShowFixRes =
        new PropertyKey(2, "DirectShowFixRes", PropertyType.SIZE);

    /**
     * DirectShow stream (Windows only).
     * <br/>GET/SET  [uint32]
     *
     * @see VGA2USB#DSHOW_LIMIT_FPS         DSHOW_LIMIT_FPS
     * @see VGA2USB#DSHOW_FLIP_VERTICALLY   DSHOW_FLIP_VERTICALLY
     * @see VGA2USB#DSHOW_FIX_FPS           DSHOW_FIX_FPS
     */
    public static final PropertyKey DirectShowFlags =
        new PropertyKey(3, "DirectShowFlags", PropertyType.INT32);

    /** GET/SET  [wstr], Windows only */
    public static final PropertyKey DirectShowDefaultBmp =
        new PropertyKey(4, "DirectShowDefaultBmp", PropertyType.STRING);

    /** GET only [blob] */
    static final int ModeMeasurmentsDump = 5;

    /**
     * GET/SET  [scale], Windows only
     * @since 3.27.1
     */
    public static final PropertyKey DirectShowScaleMode =
        new PropertyKey(7, "DirectShowScaleMode", PropertyType.ENUM);

    /** GET only [boolean] */
    public static final PropertyKey HardwareCompression =
        new PropertyKey(8, "HardwareCompression", PropertyType.BOOL);

    /** GET only [adj_range] */
    static final int AdjustmentsRange = 9;
    /** GET only [version] */
    static final int Version = 10;
    /** GET/SET  [edid] */
    static final int EDID = 11;

    /** GET/SET  [uint32], Windows only */
    public static final PropertyKey DirectShowMaxFps =
        new PropertyKey(12, "DirectShowMaxFps", PropertyType.INT32);

    /** GET only [boolean] */
    public static final PropertyKey KVMCapable =
        new PropertyKey(13, "KVMCapable", PropertyType.BOOL);

    /** GET/SET  [vgamode] */
    static final int VGAMode = 14;
    /** GET only [int32] (-1) if none */
    static final int CurrentVGAModeIndex = 15;

    /** GET/SET  [uint32] */
    static final PropertyKey ModeMeasureInterval =
        new PropertyKey(16, "ModeMeasureInterval", PropertyType.INT32);

    /** GET only [boolean] */
    static final PropertyKey EDIDSupport =
        new PropertyKey(17, "EDIDSupport", PropertyType.BOOL);
    /** GET only [string] */
    public static final PropertyKey ProductName =
        new PropertyKey(18, "ProductName", PropertyType.STRING);

    /** GET/SET  [uint32] */
    static final PropertyKey TuneInterval =
        new PropertyKey(19, "TuneInterval", PropertyType.INT32);

    /** GET/SET  [userdata] */
    static final int UserData = 20;

    /** GET only [string] */
    public static final PropertyKey SerialNumber =
        new PropertyKey(21, "SerialNumber", PropertyType.STRING);

    /**
     * Type of the input signal.
     * <br/>GET only [uint32].
     *
     * @see VGA2USB#INPUT_ANALOG    INPUT_ANALOG
     * @see VGA2USB#INPUT_DIGITAL   INPUT_DIGITAL
     * @see VGA2USB#INPUT_SOG       INPUT_SOG
     * @see VGA2USB#INPUT_COMPOSITE INPUT_COMPOSITE
     */
    public static final PropertyKey InputSignalType =
        new PropertyKey(22, "InputSignalType", PropertyType.INT32);

    /** GET/SET  [enum] */
    static final int DigitalModeDetect = 23;
    /** GET/SET  [enum] */
    static final int NoiseFilter = 24;
    /** GET/SET  [uint8] */
    static final int HSyncThreshold = 25;
    /** GET/SET  [uint8] */
    static final int VSyncThreshold = 26;

    /**
     * Device capabilities.
     * <br/>GET only [uint32].
     * <br/>The following capability bits are currently defined:
     * <table border="0">
     * <tr>
     * <td>{@link VGA2USB#CAPS_VGA_CAPTURE CAPS_VGA_CAPTURE}</td>
     * <td>Captures VGA signal</td>
     * </tr>
     * <tr>
     * <td>{@link VGA2USB#CAPS_DVI_CAPTURE CAPS_DVI_CAPTURE}</td>
     * <td>Captures DVI single-link</td>
     * </tr>
     * <tr>
     * <td>{@link VGA2USB#CAPS_DVI_DUAL_LINK CAPS_DVI_DUAL_LINK}</td>
     * <td>Captures DVI dual-link</td>
     * </tr>
     * <tr>
     * <td>{@link VGA2USB#CAPS_KVM CAPS_KVM}</td>
     * <td>KVM functionality</td>
     * </tr>
     * <tr>
     * <td>{@link VGA2USB#CAPS_EDID CAPS_EDID}</td>
     * <td>Programmable EDID</td>
     * </tr>
     * <tr>
     * <td>{@link VGA2USB#CAPS_HW_COMPRESSION CAPS_HW_COMPRESSION}&nbsp;</td>
     * <td>On-board compression</td>
     * </tr>
     * <tr>
     * <td>{@link VGA2USB#CAPS_SYNC_THRESHOLD CAPS_SYNC_THRESHOLD}&nbsp;</td>
     * <td>Adjustable sync thresholds</td>
     * </tr>
     * <tr>
     * <td>{@link VGA2USB#CAPS_HW_SCALE CAPS_HW_SCALE}</td>
     * <td>Hardware scale</td>
     * </tr>
     * </table>
     */
    public static final PropertyKey DeviceCaps =
        new PropertyKey(27, "DeviceCaps", PropertyType.INT32);

    /** GET only [enum] */
    static final int BusType = 29;

    /**
     * The type of this property
     */
    private PropertyType type;

    /**
     * Creates a new <code>PropertyKey</code> instance.
     * @param value integer value that identifies the property key
     * @param description description string
     * @param type the property type
     */
    private PropertyKey(int value, String description, PropertyType type) {
        super(value, description);
        this.type = type;
    }

    /**
     * Gets the property type
     * @return the property type
     */
    public PropertyType getType() {
        return type;
    }
}
