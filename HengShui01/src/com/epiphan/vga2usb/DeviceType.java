/****************************************************************************
 *
 * $Id: DeviceType.java 15643 2012-02-10 08:12:40Z monich $
 *
 * Copyright (C) 2008-2012 Epiphan Systems Inc. All rights reserved.
 *
 ****************************************************************************/

package com.epiphan.vga2usb;

/* java.util */
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Typesafe enumeration of the device types.
 */
public final class DeviceType extends Enum.Int {

    /* List of known types */
    private static List types;

    /**
     * Placeholder for any unknown device type.
     */
    public static final DeviceType UNKNOWN = new DeviceType(0,"Unknown");

    /**
     * VGA2USB frame grabber.
     * @see <a href="http://www.epiphan.com/products/frame-grabbers/vga2usb">
     * http://www.epiphan.com/products/frame-grabbers/vga2usb</a>
     */
    public static final DeviceType VGA2USB = new DeviceType(1,"VGA2USB");

    /**
     * Old KVM2USB device (discontinued).
     */
    public static final DeviceType KVM2USB = new DeviceType(2,"KVM2USB");

    /**
     * DVI2USB dual-mode (VGA &amp; DVI) frame grabber.
     * @see <a href="http://www.epiphan.com/products/frame-grabbers/dvi2usb">
     * http://www.epiphan.com/products/frame-grabbers/dvi2usb</a>
     */
    public static final DeviceType DVI2USB = new DeviceType(3,"DVI2USB");

    /**
     * VGA2USB Pro frame grabber.
     * @see <a href="http://www.epiphan.com/products/frame-grabbers/vga2usb-pro">
     * http://www.epiphan.com/products/frame-grabbers/vga2usb-pro</a>
     */
    public static final DeviceType VGA2USBPro = new DeviceType(4,"VGA2USB Pro");

    /**
     * VGA2USB LR frame grabber.
     * @see <a href="http://www.epiphan.com/products/frame-grabbers/vga2usb-lr">
     * http://www.epiphan.com/products/frame-grabbers/vga2usb-lr</a>
     */
    public static final DeviceType VGA2USBLR = new DeviceType(5,"VGA2USB LR");

    /**
     * DVI2USB Solo frame grabber.
     * @see <a href="http://www.epiphan.com/products/frame-grabbers/dvi2usb-solo">
     * http://www.epiphan.com/products/frame-grabbers/dvi2usb-solo</a>
     */
    public static final DeviceType DVI2USBSolo = new DeviceType(6,"DVI2USB Solo");

    /**
     * DVI2USB Duo frame grabber.
     * @see <a href="http://www.epiphan.com/products/frame-grabbers/dvi2usb-duo">
     * http://www.epiphan.com/products/frame-grabbers/dvi2usb-duo</a>
     */
    public static final DeviceType DVI2USBDuo = new DeviceType(7,"DVI2USB Duo");

    /**
     * KVM2USB Pro device.
     */
    public static final DeviceType KVM2USBPro = new DeviceType(8,"KVM2USB Pro");

    /**
     * KVM2USB device, revision 2.
     * @see <a href="http://www.epiphan.com/products/frame-grabbers/kvm2usb">
     * http://www.epiphan.com/products/frame-grabbers/kvm2usb</a>
     */
    public static final DeviceType KVM2USBLR = new DeviceType(9,"KVM2USB");

    /**
     * DVI2USB dual-mode (VGA &amp; DVI) frame grabber, revision 2.
     * @see <a href="http://www.epiphan.com/products/frame-grabbers/dvi2usb">
     * http://www.epiphan.com/products/frame-grabbers/dvi2usb</a>
     */
    //public static final DeviceType DVI2USB2 = new DeviceType(10,"DVI2USB");

    /**
     * VGA2USB HR frame grabber.
     * @see <a href="http://www.epiphan.com/products/frame-grabbers/vga2usb-hr">
     * http://www.epiphan.com/products/frame-grabbers/vga2usb-hr</a>
     */
    public static final DeviceType VGA2USBHR = new DeviceType(11,"VGA2USB HR");

    /**
     * VGA2USB LR frame grabber, revision 2.
     * @see <a href="http://www.epiphan.com/products/frame-grabbers/vga2usb-lr">
     * http://www.epiphan.com/products/frame-grabbers/vga2usb-lr</a>
     */
    public static final DeviceType VGA2USBLR2 = new DeviceType(12,"VGA2USB LR");

    /**
     * VGA2USB HR frame grabber, revision 2.
     * @see <a href="http://www.epiphan.com/products/frame-grabbers/vga2usb-hr">
     * http://www.epiphan.com/products/frame-grabbers/vga2usb-hr</a>
     */
    public static final DeviceType VGA2USBHR2 = new DeviceType(13,"VGA2USB HR");

    /**
     * VGA2USB Pro frame grabber, revision 2.
     * @see <a href="http://www.epiphan.com/products/frame-grabbers/vga2usb-pro">
     * http://www.epiphan.com/products/frame-grabbers/vga2usb-pro</a>
     */
    public static final DeviceType VGA2USBPro2 = new DeviceType(14,"VGA2USB Pro");

    /**
     * VGA2PCIe frame grabber.
     * @see <a href="http://www.epiphan.com/products/frame-grabbers/vga2pcie">
     * http://www.epiphan.com/products/frame-grabbers/vga2pcie</a>
     */
    public static final DeviceType VGA2PCI = new DeviceType(19,"VGA2PCIe");

    /**
     * DVI2PCIe frame grabber.
     * @see <a href="http://www.epiphan.com/products/dvi-frame-grabbers/dvi2pcie">
     * http://www.epiphan.com/products/dvi-frame-grabbers/dvi2pcie</a>
     */
    public static final DeviceType DVI2PCI = new DeviceType(21,"DVI2PCIe");

    /**
     * KVM2USB device, revision 3.
     * @see <a href="http://www.epiphan.com/products/frame-grabbers/kvm2usb">
     * http://www.epiphan.com/products/frame-grabbers/kvm2usb</a>
     */
    public static final DeviceType KVM2USBLR2 = new DeviceType(22,"KVM2USB");

    /**
     * Initializes the type list and pre-fills it with known device types
     */
    static {
        types = new ArrayList(18);
        types.add(UNKNOWN);
        types.add(VGA2USB);
        types.add(KVM2USB);
        types.add(DVI2USB);
        types.add(VGA2USBPro);
        types.add(VGA2USBLR);
        types.add(DVI2USBSolo);
        types.add(DVI2USBDuo);
        types.add(KVM2USBPro);
        types.add(KVM2USBLR);
        //types.add(DVI2USB2);
        types.add(VGA2USBHR);
        types.add(VGA2USBLR2);
        types.add(VGA2USBHR2);
        types.add(VGA2USBPro2);
        types.add(VGA2PCI);
        types.add(DVI2PCI);
        types.add(KVM2USBLR2);
        Collections.sort(types);
    };

    /**
     * Creates a new <code>DeviceType</code> instance.
     * @param value integer value that identifies the device type
     * @param description description string
     */
    private DeviceType(int value, String description) {
        super(value, description);
    }

    /**
     * Converts native device type value into a <tt>DeviceType</tt> object.
     * @param value integer value that identifies the device type
     * @return The <code>DeviceType</code> object that identifies this device
     *   type, or {@link #UNKNOWN} if the device type is unknown.
     */
    public static DeviceType getDeviceType(int value) {
        DeviceType type = UNKNOWN;
        synchronized (types) {
            int index = search(types, value);
            if (index >= 0) {
                type = (DeviceType)types.get(index);
            }
        }
        return type;
    }

    /**
     * Converts native device type value into a <tt>DeviceType</tt> object.
     * Creates a new device type if doesn't find the existing one.
     * @param value integer value that identifies the device type
     * @param name device type string
     * @return The <code>DeviceType</code> object that identifies this device
     */
    public static DeviceType getDeviceType(int value, String name) {
        DeviceType type;
        synchronized (types) {
            int index = search(types, value);
            if (index >= 0) {
                type = (DeviceType)types.get(index);
            } else {
                type = new DeviceType(value, name);
                types.add(-(index+1), type);
            }
        }
        return type;
    }
}
