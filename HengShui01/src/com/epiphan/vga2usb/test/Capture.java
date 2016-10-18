/****************************************************************************
 *
 * $Id: Capture.java 14391 2011-10-10 21:48:07Z monich $
 *
 * Copyright (C) 2008-2011 Epiphan Systems Inc. All rights reserved.
 *
 ****************************************************************************/

package com.epiphan.vga2usb.test;

import rec.*;

/* java.awt */
import java.awt.Dimension;
import java.awt.Image;

/* java.io */
import java.io.IOException;


/* javax.swing */
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;


import org.bytedeco.javacpp.opencv_ml.SVM;

/* com.epiphan.vga2usb */
import com.epiphan.vga2usb.Frame;
import com.epiphan.vga2usb.Grabber;
import com.epiphan.vga2usb.VideoMode;

/**
 * Capture thread
 */
class Capture implements Runnable {

    private Grabber grabber;
    private JFrame window;
    private JLabel label;
    private Image nextImage;
    private ImageUpdater imageUpdater;
    
    public static SVM svm=new SVM();
    

    /**
     * Updates the image on the UI thread
     */
    private class ImageUpdater implements Runnable {
        public void run() {
            Image image;
            synchronized (Capture.this) {
                image = nextImage;
                nextImage = null;
                imageUpdater = null;
            }
            if (image != null) {
                label.setText(null);
                label.setIcon(new ImageIcon(image));
            }
        }
    }
    public static void SetSVM(SVM s){
    	svm=s;
    }
    public Image GetImg(){
    	return nextImage;
    }
    /**
     * Updates the text on the UI thread
     */
    private class TextUpdater implements Runnable {
        private String text;
        TextUpdater(String s) {
            text = s;
        }
        public void run() {
            label.setIcon(null);
            label.setText(text);
        }
    }

    /**
     * Handles video mode change on the UI thread
     */
    private class VideoModeUpdater implements Runnable {
        private VideoMode vm;
        VideoModeUpdater(VideoMode videoMode) {
            vm = videoMode;
        }
        public void run() {
            if (vm != null) {
                window.setTitle(grabber + " - " + vm);
                if (label.getIcon() == null) {
                    label.setText("Starting capture...");
                }
                Dimension size = new Dimension(vm.getWidth(), vm.getHeight());
                label.setSize(size);
                label.setPreferredSize(size);
                label.setMaximumSize(size);
                window.pack();
            } else {
                window.setTitle(grabber.toString());
                label.setText("No signal detected");
            }
        }
    }

    /**
     * Creates the capture thread object.
     * @param grabber the grabber to work with
     * @param window the capture window
     * @param label receiver for the capture image
     */
    Capture(Grabber grabber, JFrame window, JLabel label) {
        this.grabber = grabber;
        this.window = window;
        this.label = label;
    }

    /**
     * Updates image
     * @param image the new image
     */
    private void setImage(Image image) {
        ImageUpdater updater = null;
        synchronized (Capture.this) {
            if (imageUpdater == null) {
                imageUpdater = updater = new ImageUpdater();
            }
            nextImage = image;
        }
        if (updater != null) {
            SwingUtilities.invokeLater(updater);
        }
    }

    /**
     * Set text instead of the image
     * @param text the text to set
     */
    private void setText(String text) {
        SwingUtilities.invokeLater(new TextUpdater(text));
    }

    /**
     * Invoked on the capture thread.
     */
    public void run() {
        try {
            Image lastImage = null;
            VideoMode lastVideoMode = null;
            boolean updateVideoMode = true;
            long delay = 0;

            grabber.start();

            while (true) {

                if (updateVideoMode) {
                    boolean videoModeChanged = false;

                    VideoMode vm = grabber.detectVideoMode();
                    if (lastVideoMode == null) {
                        videoModeChanged = (vm != null);
                    } else {
                        videoModeChanged = !lastVideoMode.equals(vm);
                    }

                    lastVideoMode = vm;
                    updateVideoMode = false;

                    if (videoModeChanged) {
                        SwingUtilities.invokeLater(new VideoModeUpdater(vm));
                    }
                }

                if (lastVideoMode != null) {
                    Frame frame = grabber.grabFrame();
                    if (frame != null) {
                        setImage(lastImage = frame.getImage());
                        
                        try{Rec.run(lastImage, svm);}catch(Exception e){e.printStackTrace();}//Ê¶±ð³ÌÐò
                        
                        
                        
                        if (frame.getWidth() != lastVideoMode.getWidth() ||
                            frame.getHeight() != lastVideoMode.getHeight()) {
                            updateVideoMode = true;
                        }
                    } else if (lastImage != null) {
                        lastImage = null;
                        setText("No signal detected");
                        updateVideoMode = true;
                        delay = 500;
                    } else {
                        updateVideoMode = true;
                        delay = 500;
                    }
                } else {
                    updateVideoMode = true;
                    delay = 500;
                }

                if (delay > 0) {
                    try { Thread.sleep(delay); }
                    catch (InterruptedException x) {}
                }
            }
        } catch (IOException io) {
            setText("Device I/O error");
        } finally {
            try { grabber.stop(); }
            catch (IOException io) {}
        }
    }
}
