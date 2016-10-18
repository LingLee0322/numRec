/****************************************************************************
 *
 * $Id: KVMSwitch.java 14372 2011-10-08 12:36:28Z monich $
 *
 * Copyright (C) 2009-2011 Epiphan Systems Inc. All rights reserved.
 *
 * KVM test. Implements class that toggles KVM mode.
 *
 ****************************************************************************/

package com.epiphan.vga2usb.test;

/* java.awt.event */
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/* javax.swing */
import javax.swing.JFrame;

/**
 * Listens for middle mouse click and switches KVM mode on/off
 */
class KVMSwitch extends MouseAdapter {

    JFrame frame;
    KVMHandler handler;
    boolean kvmMode;

    KVMSwitch(JFrame frame, KVMHandler handler) {
        this.frame = frame;
        this.handler = handler;
        kvmMode = false;
        frame.addMouseListener(this);
    }

    /**
     * Toggles KVM mode on middle mouse click
     * @param e the mouse event to examine
     */
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON2) {
            kvmMode = !kvmMode;
            if (kvmMode) {
                System.out.println("Entering KVM mode");
                handler.reset();
                frame.addKeyListener(handler);
                frame.addMouseListener(handler);
                frame.addMouseMotionListener(handler);
            } else {
                System.out.println("Leaving KVM mode");
                frame.removeKeyListener(handler);
                frame.removeMouseListener(handler);
                frame.removeMouseMotionListener(handler);
            }
        }
    }
}
