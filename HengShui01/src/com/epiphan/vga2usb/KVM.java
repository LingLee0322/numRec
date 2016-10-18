/****************************************************************************
 *
 * $Id: KVM.java 14372 2011-10-08 12:36:28Z monich $
 *
 * Copyright (C) 2009-2011 Epiphan Systems Inc. All rights reserved.
 *
 ****************************************************************************/

package com.epiphan.vga2usb;

/* java.util */
import java.util.Arrays;

/* java.io */
import java.io.IOException;

/* java.awt.event */
import java.awt.event.KeyEvent;

/**
 * KVM wrapper for a KVM-enabled grabber. Keyboard scan codes defined in this
 * class implement a subset of so-called Scan Code Set 2, which is the default
 * scan code set for all modern keyboards.
 *
 * @see <a href="http://www.computer-engineering.org/ps2keyboard"/>
 * http://www.computer-engineering.org/ps2keyboard</a>
 * @since 3.20.15
 */
public class KVM {

    private static final short PS2_KEYBOARD = 1;
    private static final short PS2_MOUSE = 2;

    private static final byte MOUSE_LEFT = 1;
    private static final byte MOUSE_RIGHT = 2;

    private static final boolean debugOut =
        getBooleanProperty("com.epiphan.vga2usb.KVM.debugOut");

    private Grabber grabber;

    /**
     * Creates KVM wrapper for the specified grabber. The grabber must be
     * KVM capable.
     *
     * @param g the grabber for which to create KVM wrapper.
     * @throws IllegalArgumentException if grabber is not KVM capable.
     */
    public KVM(Grabber g) {
        if (!g.isKVMCapable()) {
            throw new IllegalArgumentException(g + " is not KVM capable");
        }
        grabber = g;
    }

    /**
     * Returns the grabber object.
     * @return the grabber object.
     */
    Grabber getGrabber() {
        return grabber;
    }

    /**
     * Generates a single mouse event.
     * @param dx horizontal amount.
     * @param dy vertical amount.
     * @param left <code>true</code> if left button is pressed.
     * @param right <code>true</code> if right button is pressed.
     * @throws IOException if an I/O error occurs.
     */
    public void sendMouseEvent(int dx, int dy, boolean left, boolean right)
    throws IOException {

        if (dx > Byte.MAX_VALUE) {
            dx = Byte.MAX_VALUE;
        } else if (dx < Byte.MIN_VALUE) {
            dx = Byte.MIN_VALUE;
        }

        if (dy > Byte.MAX_VALUE) {
            dy = Byte.MAX_VALUE;
        } else if (dy < Byte.MIN_VALUE) {
            dy = Byte.MIN_VALUE;
        }

        byte flags = 0x08;
        if (left) flags |= MOUSE_LEFT;
        if (right) flags |= MOUSE_RIGHT;
        if (dx < 0) flags |= 0x10;
        if (dy < 0) flags |= 0x20;

        byte [] data = new byte[4];
        data[0] = 3;
        data[1] = flags;
        data[2] = (byte)dx;
        data[3] = (byte)dy;

        if (debugOut) {
            System.out.println("KVM[" + grabber.getSN() +
                "]: sending PS/2 mouse event (" + dx + "," + dy +
                "," + left + "," + right + ")");
        }
        grabber.sendPS2(PS2_MOUSE, data);
    }

    /**
     * Generates a single keyboard event.
     * @param key the key code
     * @param down <code>true</code> if the key is pressed, <code>false</code>
     *             if it was released.
     * @throws IOException if an I/O error occurs
     */
    public void sendKeyEvent(Key key, boolean down) throws IOException {
        if (down) {
            if (debugOut) {
                System.out.println("KVM[" + grabber.getSN() +
                    "]: sending PS/2 key event (" + key + " down)");
            }
            grabber.sendPS2(PS2_KEYBOARD, key.down);
        } else {
            if (debugOut) {
                System.out.println("KVM[" + grabber.getSN() +
                    "]: sending PS/2 key event (" + key + " up)");
            }
            grabber.sendPS2(PS2_KEYBOARD, key.up);
        }
    }

    /**
     * Generates a single keyboard event.
     * @param vk virtual key code from a <code>KeyEvent</code>
     * @param down <code>true</code> if the key is pressed,
     *   <code>false</code> if it was released
     * @return <code>true</code> if PS/2 scancode was found for this virtual
     *   key code, <code>false</code> otherwise.
     * @throws IOException if an I/O error occurs
     * @see java.awt.event.KeyEvent
     */
    public boolean sendKeyEvent(int vk, boolean down) throws IOException {
        Key key = findKey(vk);
        if (key != null) {
            sendKeyEvent(key, down);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Generates mouse reset sequence.
     * @throws IOException if an I/O error occurs.
     */
    public void resetMouse() throws IOException {
        grabber.sendPS2(PS2_MOUSE, new byte [] {0x02, (byte)0xAA, 0x00});
        try { Thread.sleep(1000); } catch (InterruptedException x) {}
    }

    /**
     * Generates keyboard reset sequence.
     * @throws IOException if an I/O error occurs.
     */
    public void resetKeyboard() throws IOException {
        grabber.sendPS2(PS2_KEYBOARD, new byte [] {0x01, (byte)0xAA});
        try { Thread.sleep(1000); } catch (InterruptedException x) {}
    }

    /**
     * Generates mouse and keyboard reset sequence.
     * @throws IOException if an I/O error occurs.
     */
    public void reset() throws IOException {
        resetKeyboard();
        resetMouse();
    }

    /**
     * Checks system property and interprets it as a boolean value.
     * @param key the property key.
     * @return <code>true</code> if the value of the specified system
     *  property is "true", "yes" or "1", <code>false</code> otherwise.
     */
    private static boolean getBooleanProperty(String key) {
        String value = System.getProperty(key);
        if (value != null) {
        return value.equalsIgnoreCase("true") ||
            value.equalsIgnoreCase("yes") ||
            value.equals("1");
        }
        return false;
    }

    //======================================================================
    //              P S / 2    S C A N    C O D E S
    //======================================================================

    /**
     * Standard PS/2 scancode descriptor. It represents a single byte
     * PRESS sequence, and a two byte RELEASE sequence that starts
     * with <code>0xF0</code>, for example <code>{0x5A}</code> and
     * <code>{0xF0,0x5A}</code>.
     *
     * @see <a href="http://www.computer-engineering.org/ps2keyboard">
     * http://www.computer-engineering.org/ps2keyboard</a>
     * @since 3.20.15
     */
    public static class Key extends Enum.Int {

        byte[] down;
        byte[] up;

        /**
         * Creates a scancode descriptor.
         *
         * @param vk Java virtual key code.
         * @param text key description.
         * @param down the PRESS sequence.
         * @param up the RELEASE sequence.
         */
        private Key(int vk, String text, byte[] down, byte[] up) {
            super(vk, text);
            this.down = down;
            this.up = up;
        }

        /**
         * Creates a standard scancode descriptor. It represents a single
         * byte PRESS sequence, and a two byte RELEASE sequence that starts
         * with <code>0xF0</code>. For example, if the <code>code</code>
         * argument equals <code>0x5A</code>, then the PRESS sequence will
         * be <code>{0x5A}</code> and the RELEASE sequence will be
         * <code>{0xF0,0x5A}</code>.
         *
         * @param vk Java virtual key code.
         * @param text key description.
         * @param code the last byte of the scancode sequence.
         *
         * @see java.awt.event.KeyEvent
         */
        public Key(int vk, String text, int code) {
            this(vk, text, new byte[] {1, (byte)code},
                  new byte[] {2, (byte)0xf0, (byte)code});
        }

        /**
         * Extended (E0-based) PS/2 scancode descriptor. It represents
         * a two byte PRESS sequence that starts with <code>0xE0</code>,
         * and a three byte RELEASE sequence that starts with
         * <code>0xE0,0xF0</code>, for example <code>{0xE0,0x7D}</code>
         * and <code>{0xE0,0xF0,0x7D}</code>.
         *
         * @see <a href="http://www.computer-engineering.org/ps2keyboard"/>
         * http://www.computer-engineering.org/ps2keyboard</a>
         * @since 3.20.15
         */
        public static class E0 extends Key {

            /**
             * Creates an extended E0-based scancode descriptor. It represents
             * a two byte PRESS sequence that starts with <code>0xE0</code>,
             * and a three byte RELEASE sequence that starts with
             * <code>0xE0,0xF0</code>. For example, if the <code>code</code>
             * argument equals  <code>0x7D</code>, then the PRESS sequence
             * will be <code>{0xE0,0x7D}</code> and the RELEASE sequence
             * will be <code>{0xE0,0xF0,0x7D}</code>.
             *
             * @param vk Java virtual key code.
             * @param text key description.
             * @param code the last byte of the scancode sequence.
             *
             * @see java.awt.event.KeyEvent
             */
            public E0(int vk, String text, int code) {
                super(vk, text, new byte[] {2, (byte)0xe0, (byte)code},
                      new byte[] {3, (byte)0xe0, (byte)0xf0, (byte)code});
            }
        }
    }

    /** <code>0x5A - 0xF0,0x5A</code> */
    public static final Key KEY_ENTER = new Key(KeyEvent.VK_ENTER,"ENTER",0x5A);
    /** <code>0x66 - 0xF0,0x66</code> */
    public static final Key KEY_BACK_SPACE = new Key(KeyEvent.VK_BACK_SPACE,"BACK_SPACE",0x66);
    /** <code>0x0D - 0xF0,0x0D</code> */
    public static final Key KEY_TAB = new Key(KeyEvent.VK_TAB,"TAB",0x0D);
    /** <code>0x12 - 0xF0,0x12</code> */
    public static final Key KEY_SHIFT = new Key(KeyEvent.VK_SHIFT,"SHIFT",0x12);
    /** <code>0x14 - 0xF0,0x14</code> */
    public static final Key KEY_CONTROL = new Key(KeyEvent.VK_CONTROL,"CONTROL",0x14);
    /** <code>0x11 - 0xF0,0x11</code> */
    public static final Key KEY_ALT = new Key(KeyEvent.VK_ALT,"ALT",0x11);
    /** <code>0x58 - 0xF0,0x58</code> */
    public static final Key KEY_CAPS_LOCK = new Key(KeyEvent.VK_CAPS_LOCK,"CAPS_LOCK",0x58);
    /** <code>0x76 - 0xF0,0x76</code> */
    public static final Key KEY_ESCAPE = new Key(KeyEvent.VK_ESCAPE,"ESCAPE",0x76);
    /** <code>0x29 - 0xF0,0x29</code> */
    public static final Key KEY_SPACE = new Key(KeyEvent.VK_SPACE,"SPACE",0x29);
    /** <code>0xE0,0x7D - 0xE0,0xF0,0x7D</code> */
    public static final Key KEY_PAGE_UP = new Key.E0(KeyEvent.VK_PAGE_UP,"PAGE_UP",0x7D);
    /** <code>0xE0,0x7A - 0xE0,0xF0,0x7A</code> */
    public static final Key KEY_PAGE_DOWN = new Key.E0(KeyEvent.VK_PAGE_DOWN,"PAGE_DOWN",0x7A);
    /** <code>0xE0,0x69 - 0xE0,0xF0,0x69</code> */
    public static final Key KEY_END = new Key.E0(KeyEvent.VK_END,"END",0x69);
    /** <code>0xE0,0x6C - 0xE0,0xF0,0x6C</code> */
    public static final Key KEY_HOME = new Key.E0(KeyEvent.VK_HOME,"HOME",0x6C);
    /** <code>0xE0,0x6B - 0xE0,0xF0,0x6B</code> */
    public static final Key KEY_LEFT = new Key.E0(KeyEvent.VK_LEFT,"LEFT",0x6B);
    /** <code>0xE0,0x75 - 0xE0,0xF0,0x75</code> */
    public static final Key KEY_UP = new Key.E0(KeyEvent.VK_UP,"UP",0x75);
    /** <code>0xE0,0x74 - 0xE0,0xF0,0x74</code> */
    public static final Key KEY_RIGHT = new Key.E0(KeyEvent.VK_RIGHT,"RIGHT",0x74);
    /** <code>0xE0,0x72 - 0xE0,0xF0,0x72</code> */
    public static final Key KEY_DOWN = new Key.E0(KeyEvent.VK_DOWN,"DOWN",0x72);
    /** <code>0xE0,0x70 - 0xE0,0xF0,0x70</code> */
    public static final Key KEY_INSERT = new Key.E0(KeyEvent.VK_INSERT,"INSERT",0x70);
    /** <code>0xE0,0x71 - 0xE0,0xF0,0x71</code> */
    public static final Key KEY_DELETE = new Key.E0(KeyEvent.VK_DELETE,"DELETE",0x71);
    /** <code>0x41 - 0xF0,0x41</code> */
    public static final Key KEY_COMMA = new Key(KeyEvent.VK_COMMA,"COMMA",0x41);
    /** <code>0x4E - 0xF0,0x4E</code> */
    public static final Key KEY_MINUS = new Key(KeyEvent.VK_MINUS,"MINUS",0x4E);
    /** <code>0x49 - 0xF0,0x49</code> */
    public static final Key KEY_PERIOD = new Key(KeyEvent.VK_PERIOD,"PERIOD",0x49);
    /** <code>0x4A - 0xF0,0x4A</code> */
    public static final Key KEY_SLASH = new Key(KeyEvent.VK_SLASH,"SLASH",0x4A);
    /** <code>0x55 - 0xF0,0x55</code> */
    public static final Key KEY_EQUALS = new Key(KeyEvent.VK_EQUALS,"EQUALS",0x55);
    /** <code>0x52 - 0xF0,0x52</code> */
    public static final Key KEY_QUOTE = new Key(KeyEvent.VK_QUOTE,"QUOTE",0x52);
    /** <code>0x0E - 0xF0,0x0E</code> */
    public static final Key KEY_BACK_QUOTE = new Key(KeyEvent.VK_BACK_QUOTE,"BACK_QUOTE",0x0E);
    /** <code>0x4C - 0xF0,0x4C</code> */
    public static final Key KEY_SEMICOLON = new Key(KeyEvent.VK_SEMICOLON,"SEMICOLON",0x4C);
    /** <code>0x54 - 0xF0,0x54</code> */
    public static final Key KEY_OPEN_BRACKET = new Key(KeyEvent.VK_OPEN_BRACKET,"OPEN_BRACKET",0x54);
    /** <code>0x5B - 0xF0,0x5B</code> */
    public static final Key KEY_CLOSE_BRACKET = new Key(KeyEvent.VK_CLOSE_BRACKET,"CLOSE_BRACKET",0x5B);
    /** <code>0x5D - 0xF0,0x5D</code> */
    public static final Key KEY_BACK_SLASH = new Key(KeyEvent.VK_BACK_SLASH,"BACK_SLASH",0x5D);

    /** <code>0x45 - 0xF0,0x45</code> */
    public static final Key KEY_0 = new Key(KeyEvent.VK_0,"'0'",0x45);
    /** <code>0x16 - 0xF0,0x16</code> */
    public static final Key KEY_1 = new Key(KeyEvent.VK_1,"'1'",0x16);
    /** <code>0x1E - 0xF0,0x1E</code> */
    public static final Key KEY_2 = new Key(KeyEvent.VK_2,"'2'",0x1E);
    /** <code>0x26 - 0xF0,0x26</code> */
    public static final Key KEY_3 = new Key(KeyEvent.VK_3,"'3'",0x26);
    /** <code>0x25 - 0xF0,0x25</code> */
    public static final Key KEY_4 = new Key(KeyEvent.VK_4,"'4'",0x25);
    /** <code>0x2E - 0xF0,0x2E</code> */
    public static final Key KEY_5 = new Key(KeyEvent.VK_5,"'5'",0x2E);
    /** <code>0x36 - 0xF0,0x36</code> */
    public static final Key KEY_6 = new Key(KeyEvent.VK_6,"'6'",0x36);
    /** <code>0x3D - 0xF0,0x3D</code> */
    public static final Key KEY_7 = new Key(KeyEvent.VK_7,"'7'",0x3D);
    /** <code>0x3E - 0xF0,0x3E</code> */
    public static final Key KEY_8 = new Key(KeyEvent.VK_8,"'8'",0x3E);
    /** <code>0x46 - 0xF0,0x46</code> */
    public static final Key KEY_9 = new Key(KeyEvent.VK_9,"'9'",0x46);

    /** <code>0x1C - 0xF0,0x1C</code> */
    public static final Key KEY_A = new Key(KeyEvent.VK_A,"'A'",0x1C);
    /** <code>0x32 - 0xF0,0x32</code> */
    public static final Key KEY_B = new Key(KeyEvent.VK_B,"'B'",0x32);
    /** <code>0x21 - 0xF0,0x21</code> */
    public static final Key KEY_C = new Key(KeyEvent.VK_C,"'C'",0x21);
    /** <code>0x23 - 0xF0,0x23</code> */
    public static final Key KEY_D = new Key(KeyEvent.VK_D,"'D'",0x23);
    /** <code>0x24 - 0xF0,0x24</code> */
    public static final Key KEY_E = new Key(KeyEvent.VK_E,"'E'",0x24);
    /** <code>0x2B - 0xF0,0x2B</code> */
    public static final Key KEY_F = new Key(KeyEvent.VK_F,"'F'",0x2B);
    /** <code>0x34 - 0xF0,0x34</code> */
    public static final Key KEY_G = new Key(KeyEvent.VK_G,"'G'",0x34);
    /** <code>0x33 - 0xF0,0x33</code> */
    public static final Key KEY_H = new Key(KeyEvent.VK_H,"'H'",0x33);
    /** <code>0x43 - 0xF0,0x43</code> */
    public static final Key KEY_I = new Key(KeyEvent.VK_I,"'I'",0x43);
    /** <code>0x3B - 0xF0,0x3B</code> */
    public static final Key KEY_J = new Key(KeyEvent.VK_J,"'J'",0x3B);
    /** <code>0x42 - 0xF0,0x42</code> */
    public static final Key KEY_K = new Key(KeyEvent.VK_K,"'K'",0x42);
    /** <code>0x4B - 0xF0,0x4B</code> */
    public static final Key KEY_L = new Key(KeyEvent.VK_L,"'L'",0x4B);
    /** <code>0x3A - 0xF0,0x3A</code> */
    public static final Key KEY_M = new Key(KeyEvent.VK_M,"'M'",0x3A);
    /** <code>0x31 - 0xF0,0x31</code> */
    public static final Key KEY_N = new Key(KeyEvent.VK_N,"'N'",0x31);
    /** <code>0x44 - 0xF0,0x44</code> */
    public static final Key KEY_O = new Key(KeyEvent.VK_O,"'O'",0x44);
    /** <code>0x4D - 0xF0,0x4D</code> */
    public static final Key KEY_P = new Key(KeyEvent.VK_P,"'P'",0x4D);
    /** <code>0x15 - 0xF0,0x15</code> */
    public static final Key KEY_Q = new Key(KeyEvent.VK_Q,"'Q'",0x15);
    /** <code>0x2D - 0xF0,0x2D</code> */
    public static final Key KEY_R = new Key(KeyEvent.VK_R,"'R'",0x2D);
    /** <code>0x1B - 0xF0,0x1B</code> */
    public static final Key KEY_S = new Key(KeyEvent.VK_S,"'S'",0x1B);
    /** <code>0x2C - 0xF0,0x2C</code> */
    public static final Key KEY_T = new Key(KeyEvent.VK_T,"'T'",0x2C);
    /** <code>0x3C - 0xF0,0x3C</code> */
    public static final Key KEY_U = new Key(KeyEvent.VK_U,"'U'",0x3C);
    /** <code>0x2A - 0xF0,0x2A</code> */
    public static final Key KEY_V = new Key(KeyEvent.VK_V,"'V'",0x2A);
    /** <code>0x1D - 0xF0,0x1D</code> */
    public static final Key KEY_W = new Key(KeyEvent.VK_W,"'W'",0x1D);
    /** <code>0x22 - 0xF0,0x22</code> */
    public static final Key KEY_X = new Key(KeyEvent.VK_X,"'X'",0x22);
    /** <code>0x35 - 0xF0,0x35</code> */
    public static final Key KEY_Y = new Key(KeyEvent.VK_Y,"'Y'",0x35);
    /** <code>0x1A - 0xF0,0x1A</code> */
    public static final Key KEY_Z = new Key(KeyEvent.VK_Z,"'Z'",0x1A);

    /** <code>0x05 - 0xF0,0x05</code> */
    public static final Key KEY_F1 = new Key(KeyEvent.VK_F1,"F1",0x05);
    /** <code>0x06 - 0xF0,0x06</code> */
    public static final Key KEY_F2 = new Key(KeyEvent.VK_F2,"F2",0x06);
    /** <code>0x04 - 0xF0,0x04</code> */
    public static final Key KEY_F3 = new Key(KeyEvent.VK_F3,"F3",0x04);
    /** <code>0x0C - 0xF0,0x0C</code> */
    public static final Key KEY_F4 = new Key(KeyEvent.VK_F4,"F4",0x0C);
    /** <code>0x03 - 0xF0,0x03</code> */
    public static final Key KEY_F5 = new Key(KeyEvent.VK_F5,"F5",0x03);
    /** <code>0x0B - 0xF0,0x0B</code> */
    public static final Key KEY_F6 = new Key(KeyEvent.VK_F6,"F6",0x0B);
    /** <code>0x83 - 0xF0,0x83</code> */
    public static final Key KEY_F7 = new Key(KeyEvent.VK_F7,"F7",0x83);
    /** <code>0x0A - 0xF0,0x0A</code> */
    public static final Key KEY_F8 = new Key(KeyEvent.VK_F8,"F8",0x0A);
    /** <code>0x01 - 0xF0,0x01</code> */
    public static final Key KEY_F9 = new Key(KeyEvent.VK_F9,"F9",0x01);
    /** <code>0x09 - 0xF0,0x09</code> */
    public static final Key KEY_F10 = new Key(KeyEvent.VK_F10,"F10",0x09);
    /** <code>0x78 - 0xF0,0x78</code> */
    public static final Key KEY_F11 = new Key(KeyEvent.VK_F11,"F11",0x78);
    /** <code>0x07 - 0xF0,0x07</code> */
    public static final Key KEY_F12 = new Key(KeyEvent.VK_F12,"F12",0x07);

    /** <code>0x77 - 0xF0,0x77</code> */
    public static final Key KEY_NUM_LOCK = new Key(KeyEvent.VK_NUM_LOCK,"NUM_LOCK",0x77);
    /** <code>0x70 - 0xF0,0x70</code> */
    public static final Key KEY_NUMPAD0 = new Key(KeyEvent.VK_NUMPAD0,"NUMPAD-0",0x70);
    /** <code>0x69 - 0xF0,0x69</code> */
    public static final Key KEY_NUMPAD1 = new Key(KeyEvent.VK_NUMPAD1,"NUMPAD-1",0x69);
    /** <code>0x72 - 0xF0,0x72</code> */
    public static final Key KEY_NUMPAD2 = new Key(KeyEvent.VK_NUMPAD2,"NUMPAD-2",0x72);
    /** <code>0x7A - 0xF0,0x7A</code> */
    public static final Key KEY_NUMPAD3 = new Key(KeyEvent.VK_NUMPAD3,"NUMPAD-3",0x7A);
    /** <code>0x6B - 0xF0,0x6B</code> */
    public static final Key KEY_NUMPAD4 = new Key(KeyEvent.VK_NUMPAD4,"NUMPAD-4",0x6B);
    /** <code>0x73 - 0xF0,0x73</code> */
    public static final Key KEY_NUMPAD5 = new Key(KeyEvent.VK_NUMPAD5,"NUMPAD-5",0x73);
    /** <code>0x74 - 0xF0,0x74</code> */
    public static final Key KEY_NUMPAD6 = new Key(KeyEvent.VK_NUMPAD6,"NUMPAD-6",0x74);
    /** <code>0x6C - 0xF0,0x6C</code> */
    public static final Key KEY_NUMPAD7 = new Key(KeyEvent.VK_NUMPAD7,"NUMPAD-7",0x6C);
    /** <code>0x75 - 0xF0,0x75</code> */
    public static final Key KEY_NUMPAD8 = new Key(KeyEvent.VK_NUMPAD8,"NUMPAD-8",0x75);
    /** <code>0x7D - 0xF0,0x7D</code> */
    public static final Key KEY_NUMPAD9 = new Key(KeyEvent.VK_NUMPAD9,"NUMPAD-9",0x7D);
    /** <code>0x71 - 0xF0,0x71</code> */
    public static final Key KEY_DECIMAL = new Key(KeyEvent.VK_DECIMAL,"DECIMAL",0x71);
    /** <code>0xE0,0x4A - 0xE0,0xF0,0x4A</code> */
    public static final Key KEY_DIVIDE = new Key.E0(KeyEvent.VK_DIVIDE,"DIVIDE",0x4A);
    /** <code>0x7C - 0xF0,0x7C</code> */
    public static final Key KEY_MULTIPLY = new Key(KeyEvent.VK_MULTIPLY,"MULTIPLY",0x7C);
    /** <code>0x7B - 0xF0,0x7B</code> */
    public static final Key KEY_SUBTRACT = new Key(KeyEvent.VK_SUBTRACT,"SUBTRACT",0x7B);
    /** <code>0x79 - 0xF0,0x79</code> */
    public static final Key KEY_ADD = new Key(KeyEvent.VK_ADD,"ADD",0x79);

    /* All known scancodes */
    private static final Key [] ALL_KEYS = new Key [] {
        KEY_ENTER, KEY_BACK_SPACE, KEY_TAB, KEY_SHIFT, KEY_CONTROL, KEY_ALT,
        KEY_CAPS_LOCK, KEY_ESCAPE, KEY_SPACE, KEY_PAGE_UP, KEY_PAGE_DOWN,
        KEY_END, KEY_HOME, KEY_LEFT, KEY_UP, KEY_RIGHT, KEY_DOWN, KEY_INSERT,
        KEY_DELETE, KEY_COMMA, KEY_MINUS, KEY_PERIOD, KEY_SLASH, KEY_EQUALS,
        KEY_QUOTE, KEY_BACK_QUOTE, KEY_SEMICOLON, KEY_OPEN_BRACKET,
        KEY_CLOSE_BRACKET, KEY_BACK_SLASH,

        KEY_0, KEY_1, KEY_2, KEY_3, KEY_4, KEY_5, KEY_6, KEY_7, KEY_8, KEY_9,

        KEY_A, KEY_B, KEY_C, KEY_D, KEY_E, KEY_F, KEY_G, KEY_H, KEY_I, KEY_J,
        KEY_K, KEY_L, KEY_M, KEY_N, KEY_O, KEY_P, KEY_Q, KEY_R, KEY_S, KEY_T,
        KEY_U, KEY_V, KEY_W, KEY_X, KEY_Y, KEY_Z,

        KEY_F1, KEY_F2, KEY_F3, KEY_F4, KEY_F5, KEY_F6, KEY_F7, KEY_F8,
        KEY_F9, KEY_F10, KEY_F11, KEY_F12,

        KEY_NUM_LOCK, KEY_NUMPAD0, KEY_NUMPAD1, KEY_NUMPAD2, KEY_NUMPAD3,
        KEY_NUMPAD4, KEY_NUMPAD5, KEY_NUMPAD6, KEY_NUMPAD7, KEY_NUMPAD8,
        KEY_NUMPAD9, KEY_DECIMAL, KEY_DIVIDE, KEY_MULTIPLY, KEY_SUBTRACT,
        KEY_ADD
    };

    static {
        // Make sure that ALL_KEYS array is sorted, findKey relies on that.
        Arrays.sort(ALL_KEYS);
    }

    /**
     * Finds the scancode descriptor for the specified virtual key code.
     * @param vk virtual key code.
     * @return matching Key object, <code>null</code> if not found.
     */
    public static Key findKey(int vk) {
        // Array is sorted, we can use binary search
        int low = 0;
        int high = ALL_KEYS.length-1;
        while (low <= high) {
            int mid =(low + high)/2;
            int midVal = ALL_KEYS[mid].getValue();
            if (midVal < vk) {
                low = mid + 1;
            } else if (midVal > vk) {
                high = mid - 1;
            } else {
                return ALL_KEYS[mid]; // key found
            }
        }
        return null;  // key not found.
    }
}
