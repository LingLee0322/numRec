/****************************************************************************
 *
 * $Id: Grabber.java 15647 2012-02-10 21:19:04Z monich $
 *
 * Copyright (C) 2008-2012 Epiphan Systems Inc. All rights reserved.
 *
 ****************************************************************************/

package com.epiphan.vga2usb;

/* java.awt */
import java.awt.Dimension;

/* java.io */
import java.io.IOException;

/* java.net */
import java.net.InetAddress;

/**
 * Java interface to Epiphan frame grabbers.
 */
public class Grabber {

    /** Native implementation */
    private NativeGrabber nativeGrabber;

    /** Last known video mode */
    private VideoMode videoMode;

    /** Whether this device is some flavor of KVM2USB */
    private boolean kvmCapable;

    /** Device type */
    private DeviceType deviceType;

    /** Bottom-up flag */
    private static final int GRABFRAME_BOTTOM_UP_FLAG  = 0x80000000;

    /** Static initializer */
    static {
        System.loadLibrary(VGA2USB.LIBRARY);
    }

    /**
     * Opens any available VGA2USB device.
     * @throws IOException if the device can't be opened
     */
    public Grabber() throws IOException {
        nativeGrabber = new LocalGrabber(-1);
        init();
    }

    /**
     * Opens a VGA2USB device.
     * @param id which device to open.
     * @throws IOException if the device can't be opened
     */
    public Grabber(int id) throws IOException {
        nativeGrabber = new LocalGrabber(id);
        init();
    }

    /**
     * Open VGA2USB device with the specified serial number.
     * @param sn serial number of the device to open.
     * @throws IOException if the device can't be opened
     */
    public Grabber(String sn) throws IOException {
        nativeGrabber = new LocalGrabber(sn);
        init();
    }

    /**
     * Open network frame grabber at the specified address.
     * @param address IP address of the device to open.
     * @throws IOException if the device can't be opened
     * @since 3.20.18
     */
    public Grabber(InetAddress address) throws IOException {
        nativeGrabber = new NetworkGrabber(address);
        init();
    }

    /**
     * Final stage of initialization. Invoked from all constructors.
     * @throws IOException if an I/O error occurs
     */
    private void init() throws IOException {
        kvmCapable = getBooleanProperty(PropertyKey.KVMCapable);
        int typeId = nativeGrabber.getDeviceType();
        deviceType = DeviceType.getDeviceType(typeId);
        if (deviceType == DeviceType.UNKNOWN) {
            String deviceName = nativeGrabber.getDeviceName();
            deviceType = DeviceType.getDeviceType(typeId, deviceName);
        }
    }

    /**
     * Tests whether this grabber is some kind of KVM2USB.
     * @return <code>true</code> if this grabber supports KVM functionality,
     *         <code>false</code> otherwise.
     * @since 3.20.15
     */
    public boolean isKVMCapable() {
        return kvmCapable;
    }

    /**
     * Closes the device.
     */
    public void close() {
        nativeGrabber.close();
    }

    /**
     * Gets serial number string for this device.
     * @return The serial number string.
     * @throws IOException if an I/O error occurs.
     */
    public String getSN() throws IOException {
        return nativeGrabber.getSN();
    }

    /**
     * Get the type of this frame grabber.
     * @return The type of this frame grabber.
     */
    public DeviceType getDeviceType() {
        return deviceType;
    }

    /**
     * Gets the value of a boolean property.
     * @param key The property key.
     * @return The value of the requested property.
     * @throws IOException if an I/O error occurs.
     * @since 3.20.15
     */
    public boolean getBooleanProperty(PropertyKey key) throws IOException {
        if (key != null && key.getType() == PropertyType.BOOL) {
            return nativeGrabber.getBooleanProperty(key.getValue());
        } else {
            throw new IllegalArgumentException(String.valueOf(key));
        }
    }

    /**
     * Gets the value of a string property.
     * @param key The property key.
     * @return The value of the requested property.
     * @throws IOException if an I/O error occurs.
     * @since 3.22.2
     */
    public String getStringProperty(PropertyKey key) throws IOException {
        if (key != null && key.getType() == PropertyType.STRING) {
            return nativeGrabber.getStringProperty(key.getValue());
        } else {
            throw new IllegalArgumentException(String.valueOf(key));
        }
    }

    /**
     * Sets the value of the specified string property.
     * @param key The property key
     * @param value The property value
     * @throws IOException if an I/O error occurs
     * @since 3.27.1
     */
    public void setStringProperty(PropertyKey key, String value)
        throws IOException {
        if (key != null && key.getType() == PropertyType.STRING) {
            nativeGrabber.setStringProperty(key.getValue(), value);
        } else {
            throw new IllegalArgumentException(String.valueOf(key));
        }
    }

    /**
     * Gets the value of the specified integer property.
     * @param key The property key
     * @return The current property value.
     * @throws IOException if an I/O error occurs.
     * @since 3.27.1
     */
    public int getIntProperty(PropertyKey key) throws IOException {
        if (key != null) {
            // Native code will check the property type
            return nativeGrabber.getIntProperty(key.getValue());
        } else {
            throw new IllegalArgumentException(String.valueOf(key));
        }
    }

    /**
     * Sets the value of the specified integer property.
     * @param key The property key
     * @param value The desired property value
     * @throws IOException if an I/O error occurs.
     * @since 3.27.1
     */
    public void setIntProperty(PropertyKey key, int value) throws IOException {
        if (key != null) {
            // Native code will check the property type
            nativeGrabber.setIntProperty(key.getValue(), value);
        } else {
            throw new IllegalArgumentException(String.valueOf(key));
        }
    }

    /**
     * Gets the value of the specified enum property.
     * @param key The property key
     * @return The current property value.
     * @throws IOException if an I/O error occurs.
     * @since 3.27.1
     */
    public int getEnumProperty(PropertyKey key) throws IOException {
        if (key != null) {
            // Native code will check the property type
            return nativeGrabber.getIntProperty(key.getValue());
        } else {
            throw new IllegalArgumentException(String.valueOf(key));
        }
    }

    /**
     * Sets the value of the specified enum property.
     * @param key The property key
     * @param value The desired property value
     * @throws IOException if an I/O error occurs.
     * @since 3.27.1
     */
    public void setEnumProperty(PropertyKey key, Enum.Int value)
        throws IOException {
        if (key != null) {
            // Native code will check the property type
            nativeGrabber.setIntProperty(key.getValue(), value.getValue());
        } else {
            throw new IllegalArgumentException(String.valueOf(key));
        }
    }

    /**
     * Gets the value of the specified enum property.
     * @param key The property key
     * @return The current property value.
     * @throws IOException if an I/O error occurs.
     * @since 3.27.1
     */
    public Size getSizeProperty(PropertyKey key) throws IOException {
        if (key != null && key.getType() == PropertyType.SIZE) {
            int [] size = new int[2];
            nativeGrabber.getSizeProperty(key.getValue(), size);
            return new Size(size[0], size[1]);
        } else {
            throw new IllegalArgumentException(String.valueOf(key));
        }
    }

    /**
     * Sets the value of the specified enum property.
     * @param key The property key
     * @param value The desired property value
     * @throws IOException if an I/O error occurs.
     * @since 3.27.1
     */
    public void setSizeProperty(PropertyKey key, Size value)
        throws IOException {
        if (key != null && key.getType() == PropertyType.SIZE) {
            int w = value.getWidth();
            int h = value.getHeight();
            nativeGrabber.setSizeProperty(key.getValue(), w, h);
        } else {
            throw new IllegalArgumentException(String.valueOf(key));
        }
    }

    /**
     * Detects video mode.
     * @return the video mode descriptor, or <code>null</code> if no signal
     * was detected.
     * @throws IOException if an I/O error occurs.
     */
    public VideoMode detectVideoMode() throws IOException {
        int [] vm = new int[3];
        nativeGrabber.detectVideoMode(vm);
        if (vm[0] != 0 && vm[1] != 0) {
            videoMode = new VideoMode(vm[0], vm[1], vm[2]);
        } else {
            videoMode = null;
        }
        return videoMode;
    }

    /**
     * Grabs a single frame.
     * @return The captured frame, or <code>null</code> if there's no signal.
     * @throws IOException if an I/O error occurs.
     *
     * @see #grabRawFrame(PixelFormat,boolean)
     */
    public Frame grabFrame() throws IOException {
        // Note: Frame class only supports RGB24
        return grabFrame(PixelFormat.RGB24);
    }

    /**
     * Grabs a single frame.
     * @param format the desired pixel format.
     * @return The captured frame, or <code>null</code> if there's no signal.
     * @throws IOException if an I/O error occurs
     */
    private Frame grabFrame(PixelFormat format) throws IOException {
        int[] vm = new int[3];
        Frame frame = grabFrame(format, vm);
        if (frame == null || (
            vm[0] != 0 &&
            vm[1] != 0 &&
            videoMode != null &&
            videoMode.getWidth() != vm[0] &&
            videoMode.getHeight() != vm[1])) {

            // Possibly, video mode has changed. Force video mode detection
            // and try again
            videoMode = null;
            frame = grabFrame(format, vm);
        }
        return frame;
    }

    /**
     * Grabs a single frame.
     * @param format the desired pixel format.
     * @param vm receives the video mode information
     * @return The captured frame, or <code>null</code> if there's no signal.
     * @throws IOException if an I/O error occurs.
     */
    private Frame grabFrame(PixelFormat format, int[] vm) throws IOException {

        // Detect video mode to get an idea how much memory to allocate
        VideoMode mode = videoMode;
        if (mode == null) {
            mode = detectVideoMode();
            if (mode == null) {
                // No signal
                return null;
            }
        }

        // Allocate buffers
        int pixelCount = mode.getWidth() * mode.getHeight();
        byte[] pixels = new byte[pixelCount*format.getBpp()/8];
        int len = nativeGrabber.grabFrame(format.getValue(), vm, pixels);

        if (len > 0 && pixelCount >= (vm[0]*vm[1])) {
            return new Frame(vm[0], vm[1], format, pixels, len);
        }
        return null;
    }

    /**
     * Grabs a single frame. Raw frame is essentially nothing mode than a
     * byte array. It's up to the caller what to do with it.
     *
     * @param format the desired pixel format.
     * @param bottomUp if <code>true</code>, then image lines will be in
     * bottom-up order (default is up-down).
     * @return Raw captured frame in the requested format,
     *   or <code>null</code> if there's no signal.
     * @throws IOException if an I/O error occurs.
     *
     * @see #grabFrame()
     */
    public RawFrame grabRawFrame(PixelFormat format, boolean bottomUp)
        throws IOException {
        int[] vm = new int[3];
        RawFrame frame = grabRawFrame(format, bottomUp, vm);
        if (frame == null || (
            vm[0] != 0 &&
            vm[1] != 0 &&
            videoMode != null &&
            videoMode.getWidth() != vm[0] &&
            videoMode.getHeight() != vm[1])) {

            // Possibly, video mode has changed. Force video mode detection
            // and try again
            videoMode = null;
            frame = grabRawFrame(format, bottomUp, vm);
        }
        return frame;
    }

    /**
     * Grabs a single frame.
     * @param format the desired pixel format.
     * @param bottomUp if <code>true</code>, then image lines will be in
     * bottom-up order (default is up-down).
     * @param vm receives the video mode information
     * @return Raw captured frame in the requested format,
     *   or <code>null</code> if there's no signal.
     * @throws IOException if an I/O error occurs.
     */
    private RawFrame grabRawFrame(PixelFormat format, boolean bottomUp,
                                  int[] vm) throws IOException {

        // Detect video mode to get an idea how much memory to allocate
        VideoMode mode = videoMode;
        if (mode == null) {
            mode = detectVideoMode();
            if (mode == null) {
                // No signal
                return null;
            }
        }

        int grabFormat = format.getValue();
        if (bottomUp) grabFormat |= GRABFRAME_BOTTOM_UP_FLAG;

        // Allocate buffers
        int pixelCount = mode.getWidth() * mode.getHeight();
        byte[] pixels = new byte[pixelCount*format.getBpp()/8];
        int len = nativeGrabber.grabFrame(grabFormat, vm, pixels);

        if (len > 0 && pixelCount >= (vm[0]*vm[1])) {
            return new RawFrame(vm[0], vm[1], format, pixels, len);
        }
        return null;
    }

    /**
     * Sends a PS/2 packet.
     * @param type packet type (keyboard vs mouse)
     * @param data the packet data
     * @throws IOException if an I/O error occurs.
     * @since 3.20.15
     */
    void sendPS2(short type, byte [] data) throws IOException {
        nativeGrabber.sendPS2(type, data);
    }

    /**
     * Gets VGA capture parameters.
     * @return the current grab parameters
     * @throws IOException if an I/O error occurs
     * @since 3.22.2
     */
    public GrabParameters getGrabParameters() throws IOException {
        GrabParameters gp = new GrabParameters();
        int [] params = gp.getParameters();
        nativeGrabber.getGrabParameters(params);
        gp.setParameters(params);
        return gp;
    }

    /**
     * Sets VGA capture parameters.
     * @param gp grab parameters
     * @throws IOException if an I/O error occurs
     * @since 3.22.2
     */
    public void setGrabParameters(GrabParameters gp) throws IOException {
        nativeGrabber.setGrabParameters(gp.getParameters());
    }

    /**
     * Prepares frame grabber for capture with maximum possible frame rate.
     * Especially useful for network frame grabbers.
     * @throws IOException if an I/O error occurs
     * @since 3.26.2
     */
    public void start() throws IOException {
        try {
            nativeGrabber.start();
        } catch (UnsatisfiedLinkError e) {
            // For backward compatibility with older JNI binaries
        }
    }

    /**
     * Signals the driver that maximum possible frame rate is no longer
     * required. Especially useful for network frame grabbers (reduces
     * network usage).
     * @throws IOException if an I/O error occurs
     * @since 3.26.2
     */
    public void stop() throws IOException {
        try {
            nativeGrabber.stop();
        } catch (UnsatisfiedLinkError e) {
            // For backward compatibility with older JNI binaries
        }
    }

    /**
     * Invoked when this object is being garbage collected
     * @throws Throwable any exception that occurs during finalization
     */
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    /**
     * Returns a string representation of this object. This method
     * is intended to be used only for debugging purposes, and the
     * content and format of the returned string may vary between
     * implementations. The returned string may be empty but may not
     * be <code>null</code>.
     *
     * @return  A string representation of this object.
     */
    public String toString() {
        String sn = null;
        try { sn = getSN(); }
        catch (IOException x) {}
        if (sn != null) {
            return getDeviceType() + " [" + sn + "]";
        } else {
            return getDeviceType().toString();
        }
    }
}
