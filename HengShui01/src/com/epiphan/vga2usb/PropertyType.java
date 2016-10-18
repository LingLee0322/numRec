/****************************************************************************
 *
 * $Id: PropertyType.java 15643 2012-02-10 08:12:40Z monich $
 *
 * Copyright (C) 2009-2012 Epiphan Systems Inc. All rights reserved.
 *
 ****************************************************************************/

package com.epiphan.vga2usb;


/**
 * Typesafe enumeration of the property types.
 */
public final class PropertyType extends Enum.Int {

    /**
     * Signed byte property.
     * @since 3.22.2
     */
    public static final PropertyType INT8 = new PropertyType(0,"Int8");

    /**
     * Signed short property.
     * @since 3.22.2
     */
    public static final PropertyType INT16 = new PropertyType(1,"Int16");

    /**
     * Signed int property.
     * @since 3.22.2
     */
    public static final PropertyType INT32 = new PropertyType(2,"Int32");

    /**
     * Boolean property.
     */
    public static final PropertyType BOOL = new PropertyType(3,"Bool");

    /**
     * Size property.
     * @since 3.22.2
     */
    public static final PropertyType SIZE = new PropertyType(4,"Size");

    /**
     * Rectangle property.
     * @since 3.22.2
     */
    public static final PropertyType RECT = new PropertyType(5,"Rect");

    /**
     * Version property.
     * @since 3.22.2
     */
    public static final PropertyType VERSION = new PropertyType(6,"Version");

    /**
     * Binary property.
     * @since 3.22.2
     */
    public static final PropertyType BINARY = new PropertyType(7,"Binary");

    /**
     * EDID property.
     */
    public static final PropertyType EDID = new PropertyType(8,"EDID");

    /**
     * Adjust range property.
     * @since 3.22.2
     */
    public static final PropertyType ADJUST_RANGE = new PropertyType(9,"AdjustRange");

    /**
     * VGA mode property.
     * @since 3.22.2
     */
    public static final PropertyType VGA_MODE = new PropertyType(10,"VGAMode");

    /**
     * String property.
     * @since 3.22.2
     */
    public static final PropertyType STRING = new PropertyType(11,"String");

    /**
     * Alias to INT32.
     */
    public static final PropertyType INT = INT32;

    /**
     * Alias to INT32.
     * @since 3.27.1
     */
    public static final PropertyType ENUM = INT32;

    /**
     * Creates a new <code>PropertyType</code> instance.
     * @param value integer value that identifies the property type
     * @param description description string
     */
    private PropertyType(int value, String description) {
        super(value, description);
    }
}
