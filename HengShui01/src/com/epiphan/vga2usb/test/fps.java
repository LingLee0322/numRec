/****************************************************************************
 *
 * $Id: fps.java 14391 2011-10-10 21:48:07Z monich $
 *
 * Copyright (C) 2011 Epiphan Systems Inc. All rights reserved.
 *
 * Frame grabber test application. Capculates fps.
 *
 ****************************************************************************/

package com.epiphan.vga2usb.test;

/* java.text */
import java.text.NumberFormat;

/* java.io */
import java.io.IOException;

/* java.net */
import java.net.InetAddress;

/* com.epiphan.vga2usb */
import com.epiphan.vga2usb.Grabber;
import com.epiphan.vga2usb.Frame;
import com.epiphan.vga2usb.VideoMode;

/**
 * Test application.
 */
public class fps {

    /**
     * Shows a message
     * @param message the error message
     */
    private static void println(String message) {
        System.out.println(message);
    }

    /**
     * Measures fps
     * @param grabber the grabber to test
     * @param time duration of the test in milliseconds
     * @throws IOException in case of I/O problem
     */
    private static void measure(Grabber grabber, long time) throws IOException {

        // Detect video mode
        VideoMode vm = grabber.detectVideoMode();
        if (vm != null) {
            System.out.println("Detected " + vm);
            long start = System.currentTimeMillis();
            long deadline = start + time;
            int frameCount = 0;
            grabber.start();
            do {
                Frame frame = grabber.grabFrame();
                if (frame == null) break;
                System.out.print('.');
                frameCount++;
            } while (System.currentTimeMillis() < deadline);
            grabber.stop();

            long end = System.currentTimeMillis();
            if (frameCount > 1) System.out.println();
            if (frameCount > 1 && end > start) {
                float fps = ((float)frameCount * 1000)/(end-start);
                NumberFormat f = NumberFormat.getNumberInstance();
                f.setMaximumFractionDigits(1);
                println(frameCount + " frames, " + f.format(fps) + " fps");
            } else {
                println("Not enough frames captured");
            }
        } else {
            println("No signal detected");
        }
    }

    /**
     * Entry point
     * @param args command line arguments
     */
    public static void main(String[] args) {

        try {
            // Open the grabber
            Grabber grabber = null;
            if (args.length > 0) {
                if (args[0].indexOf('.') >= 0) {
                    // Assume that this is a network address
                    try {
                        grabber = new Grabber(InetAddress.getByName(args[0]));
                    } catch (IOException x) {
                        println("Can't connect to " + args[0]);
                    }
                } else {
                    // Otherwise assume that it's a serial number
                    try {
                        grabber = new Grabber(args[0]);
                    } catch (IOException x) {
                        println("No such device: " + args[0]);
                    }
                }
            } else {
                try {
                    grabber = new Grabber();
                } catch (IOException x) {
                    println("No frame grabber found");
                }
            }

            // Run the test
            if (grabber != null) {
                try {
                    measure(grabber, 10000);
                } catch (IOException x) {
                    println("Test failed");
                    x.printStackTrace();
                }
                grabber.close();
            }

            System.exit(0);

        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
}
