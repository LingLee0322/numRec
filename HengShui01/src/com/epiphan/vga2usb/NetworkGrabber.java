/****************************************************************************
 *
 * $Id: NetworkGrabber.java 15647 2012-02-10 21:19:04Z monich $
 *
 * Copyright (C) 2009-2012 Epiphan Systems Inc. All rights reserved.
 *
 ****************************************************************************/

package com.epiphan.vga2usb;

/* java.io */
import java.io.IOException;

/* java.net */
import java.net.InetAddress;

/**
 * Native interface to network frame grabbers.
 */
class NetworkGrabber implements NativeGrabber {

    /** Native handle */
    private long handle;

    /**
     * Opens a network frame grabber.
     * @param address IP address of the device to open
     * @throws IOException if the device can't be opened
     */
    public NetworkGrabber(InetAddress address) throws IOException {
        handle = open(address.getAddress());
        if (handle == 0) {
            throw new IOException(address.toString());
        }
    }

    /**
     * Calls down to the native code to open the device.
     * @param address raw IP address in network byte order
     * @return Handle to the device
     * @throws IOException if the device can't be opened
     */
    private static native long open(byte[] address) throws IOException;

    /**
     * Closes the device.
     */
    public synchronized void close() {
        if (handle != 0) {
            close(handle);
            handle = 0;
        }
    }

    /**
     * Calls down to the native code to really close the device.
     * @param handle handle to close
     */
    private static native void close(long handle);

    /**
     * Gets serial number string for this device.
     * @return The serial number string
     * @throws IOException if an I/O error occurs
     */
    public native synchronized String getSN() throws IOException;

    /**
     * Calls down to the native code to detect video mode.
     * @param vm receives video mode description
     * @throws IOException if an I/O error occurs
     */
    public native synchronized void detectVideoMode(int[] vm)
        throws IOException;

    /**
     * Grabs single frame into the provided buffer.
     * @param format desired pixel format
     * @param vm receives video mode description
     * @param buf receives the pixels
     * @return Number of bytes written into the buffer
     * @throws IOException if an I/O error occurs
     */
    public native synchronized int grabFrame(int format, int[] vm, byte[] buf)
        throws IOException;

    /**
     * Gets the device type for this grabber.
     * @return The device type, zero if unknown
     */
    public native synchronized int getDeviceType();

    /**
     * Gets the device name for this grabber.
     * @return The device type, <code>null</code> if unknown
     */
    public native synchronized String getDeviceName();

    /**
     * Gets the value of the specified boolean property.
     * @param key the property key
     * @return The property value
     * @throws IOException if an I/O error occurs
     */
    public native synchronized boolean getBooleanProperty(int key)
        throws IOException;

    /**
     * Gets the value of the specified string property.
     * @param key The property key
     * @return The property value
     * @throws IOException if an I/O error occurs
     */
    public native synchronized String getStringProperty(int key)
        throws IOException;

    /**
     * Sets the value of the specified string property.
     * @param key The property key
     * @param value The property value
     * @throws IOException if an I/O error occurs
     * @since 3.27.1
     */
    public native synchronized void setStringProperty(int key, String value)
        throws IOException;

    /**
     * Gets the value of the specified integer or enum property.
     * @param key The property key
     * @return The property value as an int
     * @throws IOException if an I/O error occurs
     * @since 3.27.1
     */
    public native synchronized int getIntProperty(int key)
        throws IOException;

    /**
     * Sets the value of the specified integer or enum property.
     * @param key The property key
     * @param value The property value
     * @throws IOException if an I/O error occurs
     * @since 3.27.1
     */
    public native synchronized void setIntProperty(int key, int value)
        throws IOException;

    /**
     * Gets the value of the specified size property.
     * @param key The property key
     * @param value Receives the size packed into an array of size 2.
     * @throws IOException if an I/O error occurs
     * @since 3.27.1
     */
    public native synchronized void getSizeProperty(int key, int [] value)
        throws IOException;

    /**
     * Sets the value of the specified size property.
     * @param key The property key
     * @param w Width
     * @param h Height
     * @throws IOException if an I/O error occurs
     * @since 3.27.1
     */
    public native synchronized void setSizeProperty(int key, int w, int h)
        throws IOException;

    /**
     * Sends a PS/2 packet.
     * @param type packet type (keyboard vs mouse)
     * @param data the packet data
     * @throws IOException if an I/O error occurs
     */
    public native synchronized void sendPS2(short type, byte [] data)
        throws IOException;

    /**
     * Gets VGA capture parameters.
     * @param params an int array that receives the current grab parameters
     * @throws IOException if an I/O error occurs
     */
    public native synchronized void getGrabParameters(int [] params)
        throws IOException;

    /**
     * Sets VGA capture parameters.
     * @param params grab parameters packed into an int array
     * @throws IOException if an I/O error occurs
     */
    public native synchronized void setGrabParameters(int [] params)
        throws IOException;

    /**
     * Prepares frame grabber for capture with maximum possible frame rate.
     * Especially useful with network frame grabbers.
     * @throws IOException if an I/O error occurs
     * @since 3.26.2
     */
    public native synchronized void start()
        throws IOException;

    /**
     * Signals the driver that maximum possible frame rate is no longer
     * required. Especially useful with network frame grabbers (reduces
     * network usage).
     * @throws IOException if an I/O error occurs
     * @since 3.26.2
     */
    public native synchronized void stop()
        throws IOException;
}
