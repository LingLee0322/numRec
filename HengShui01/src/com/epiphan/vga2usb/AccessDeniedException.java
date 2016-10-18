/****************************************************************************
 *
 * $Id: AccessDeniedException.java 14368 2011-10-08 12:06:41Z monich $
 *
 * Copyright (C) 2011 Epiphan Systems Inc. All rights reserved.
 *
 ****************************************************************************/

package com.epiphan.vga2usb;

/* java.io */
import java.io.IOException;

/**
 * Exception thrown if network grabber is protected with a vewer password.
 *
 * @since 3.26.1.26
 */
public class AccessDeniedException extends IOException {
    /**
     * Constructs an <code>AccessDeniedException</code> with
     * <code>null</code> as its error detail message.
     */
    public AccessDeniedException() {
    }

    /**
     * Constructs an <code>AccessDeniedException</code> with the
     * specified detail message.
     *
     * @param   s   the detail message.
     */
    public AccessDeniedException(String s) {
        super(s);
    }
}
