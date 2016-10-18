/****************************************************************************
 *
 * $Id: NativeGrabber.java 15647 2012-02-10 21:19:04Z monich $
 *
 * Copyright (C) 2008-2012 Epiphan Systems Inc. All rights reserved.
 *
 ****************************************************************************/

package com.epiphan.vga2usb;

/* java.io */
import java.io.IOException;

/**
 * Native interface to Epiphan frame grabbers.
 */
interface NativeGrabber {

    /**
     * Calls down to the native code to really close the device.
     */
    void close();

    /**
     * Gets serial number string for this device.
     * @return The serial number string
     * @throws IOException if an I/O error occurs
     */
    String getSN() throws IOException;

    /**
     * Calls down to the native code to detect video mode.
     * @param vm receives video mode description
     * @throws IOException if an I/O error occurs
     */
    void detectVideoMode(int[] vm) throws IOException;

    /**
     * Grabs single frame into the provided buffer.
     * @param format desired pixel format
     * @param vm receives video mode description
     * @param pixbuf receives the pixels
     * @return Number of bytes written into the buffer
     * @throws IOException if an I/O error occurs
     */
    int grabFrame(int format, int[] vm, byte[] pixbuf) throws IOException;

    /**
     * Gets the device type for this grabber.
     * @return The device type, zero if unknown
     */
    int getDeviceType();

    /**
     * Gets the device name for this grabber.
     * @return The device type, <code>null</code> if unknown
     */
    String getDeviceName();

    /**
     * Gets the value of the specified boolean property.
     * @param key The property key
     * @return The property value
     * @throws IOException if an I/O error occurs
     */
    boolean getBooleanProperty(int key) throws IOException;

    /**
     * Gets the value of the specified string property.
     * @param key The property key
     * @return The property value
     * @throws IOException if an I/O error occurs
     */
    String getStringProperty(int key) throws IOException;

    /**
     * Sets the value of the specified string property.
     * @param key The property key
     * @param value The property value
     * @throws IOException if an I/O error occurs
     * @since 3.27.1
     */
    void setStringProperty(int key, String value) throws IOException;

    /**
     * Gets the value of the specified integer or enum property.
     * @param key The property key
     * @return The property value as an int
     * @throws IOException if an I/O error occurs
     * @since 3.27.1
     */
    int getIntProperty(int key) throws IOException;

    /**
     * Sets the value of the specified integer or enum property.
     * @param key the property key
     * @param value the property value
     * @throws IOException if an I/O error occurs
     * @since 3.27.1
     */
    void setIntProperty(int key, int value) throws IOException;

    /**
     * Gets the value of the specified size property.
     * @param key The property key
     * @param value Receives the size packed into an array of size 2.
     * @throws IOException if an I/O error occurs
     * @since 3.27.1
     */
    void getSizeProperty(int key, int [] value) throws IOException;

    /**
     * Sets the value of the specified size property.
     * @param key The property key
     * @param w Width
     * @param h Height
     * @throws IOException if an I/O error occurs
     * @since 3.27.1
     */
    void setSizeProperty(int key, int w, int h) throws IOException;

    /**
     * Sends a PS/2 packet.
     * @param type Packet type (keyboard vs mouse)
     * @param data The packet data
     * @throws IOException if an I/O error occurs
     */
    void sendPS2(short type, byte [] data) throws IOException;

    /**
     * Gets VGA capture parameters.
     * @param params An int array that receives the current grab parameters
     * @throws IOException if an I/O error occurs
     */
    void getGrabParameters(int [] params) throws IOException;

    /**
     * Sets VGA capture parameters.
     * @param params Grab parameters packed into an int array
     * @throws IOException if an I/O error occurs
     */
    void setGrabParameters(int [] params) throws IOException;

    /**
     * Prepares frame grabber for capture with maximum possible frame rate.
     * Especially useful with network frame grabbers.
     * @throws IOException if an I/O error occurs
     * @since 3.26.2
     */
    void start() throws IOException;

    /**
     * Signals the driver that maximum possible frame rate is no longer
     * required. Especially useful with network frame grabbers (reduces
     * network usage).
     * @throws IOException if an I/O error occurs
     * @since 3.26.2
     */
    void stop() throws IOException;
}
