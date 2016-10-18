/****************************************************************************
 *
 * $Id: KVMHandler.java 14372 2011-10-08 12:36:28Z monich $
 *
 * Copyright (C) 2009-2011 Epiphan Systems, Inc. All rights reserved.
 *
 * KVM test. Implements class that handles keyboard and mouse events and
 * forwards them to the KVM device.
 *
 ****************************************************************************/

package com.epiphan.vga2usb.test;

/* java.io */
import java.io.IOException;

/* java.awt.event */
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/* com.epiphan.vga2usb */
import com.epiphan.vga2usb.KVM;

/**
 * Handles keyboard and mouse events and forwards them to the KVM device
 */
class KVMHandler implements KeyListener, MouseListener, MouseMotionListener {

    private static int UNDEF = Integer.MIN_VALUE;
    private static int LEFT_BUTTON = MouseEvent.BUTTON1;
    private static int RIGHT_BUTTON = MouseEvent.BUTTON3;

    private KVM kvm;
    private int xlast;
    private int ylast;
    private boolean leftDown;
    private boolean rightDown;

    KVMHandler(KVM kvm) {
        this.kvm = kvm;
        xlast = UNDEF;
        ylast = UNDEF;
    }

    void reset() {
        try {
            kvm.reset();
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    private void sendMouseEvent(int x, int y) {
        try {
            kvm.sendMouseEvent(x, y, leftDown, rightDown);
        } catch (Exception oops) {
            oops.printStackTrace();
        }
    }

    private boolean sendKeyEvent(int vk, boolean down) {
        try {
            return kvm.sendKeyEvent(vk, down);
        } catch (Exception oops) {
            oops.printStackTrace();
            return false;
        }
    }

    // KeyListener
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
        if (!sendKeyEvent(e.getKeyCode(), true)) {
            System.out.println("KVM: unhandled key event " + e);
        }
    }
    public void keyReleased(KeyEvent e) {
        sendKeyEvent(e.getKeyCode(), false);
    }

    // MouseListener
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {
        int b = e.getButton();
        leftDown = (b == LEFT_BUTTON);
        rightDown = (b == RIGHT_BUTTON);
        if (leftDown || rightDown) {
            sendMouseEvent(0, 0);
        }
    }
    public void mouseReleased(MouseEvent e) {
        if (leftDown || rightDown) {
            int b = e.getButton();
            if (b == LEFT_BUTTON) {
                leftDown = false;
                sendMouseEvent(0, 0);
            } else if (b == RIGHT_BUTTON) {
                rightDown = false;
                sendMouseEvent(0, 0);
            }
        }
    }
    public void mouseEntered(MouseEvent e) {
        if (xlast == UNDEF) {
            xlast = e.getX();
        }
        if (ylast == UNDEF) {
            ylast = e.getY();
        }
    }
    public void mouseExited(MouseEvent e) {}

    // MouseMotionListener
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }
    public void mouseMoved(MouseEvent e) {
        int b = e.getButton();
        int x = e.getX();
        int y = e.getY();
        if (xlast == UNDEF) {
            xlast = x;
        }
        if (ylast == UNDEF) {
            ylast = y;
        }
        sendMouseEvent(x - xlast, ylast - y);
        xlast = x;
        ylast = y;
    }
}
