/****************************************************************************
 *
 * $Id: Test.java 14368 2011-10-08 12:06:41Z monich $
 *
 * Copyright (C) 2008-2011 Epiphan Systems Inc. All rights reserved.
 *
 * Frame grabber test application.
 *
 ****************************************************************************/

package com.epiphan.vga2usb.test;

/* java.io */
import rec.*;

import java.io.IOException;

/* java.awt */
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

/* java.awt.event */
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/* java.net */
import java.net.InetAddress;


/* javax.swing */
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;


import org.bytedeco.javacpp.opencv_ml.SVM;

/* com.epiphan.vga2usb */
import com.epiphan.vga2usb.AccessDeniedException;
import com.epiphan.vga2usb.Grabber;
import com.epiphan.vga2usb.KVM;
import com.epiphan.vga2usb.VideoMode;

/**
 * Test application.
 */
public class Test {

    private static class AdjustAction extends AbstractAction {
        private Grabber grabber;
        private JFrame window;
        private GrabParamEditor editor;
        AdjustAction(Grabber g, JFrame f) {
            super("Adjustments...");
            grabber = g;
            window = f;
        }
        public void actionPerformed(ActionEvent e) {
            if (editor == null) {
                editor = new GrabParamEditor(grabber, window);
            }
            try {
                editor.show();
            } catch (IOException x) {
                error(window, "Failed to query grab parameters");
            }
        }
    }

    private static class ExitAction extends AbstractAction {
        ExitAction() { super("Exit"); }
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    /**
     * Popup menu support
     */
    private static class PopupHandler extends MouseAdapter {
        private JPopupMenu menu;
        PopupHandler(JPopupMenu popup) { menu = popup; }
        public void mousePressed(MouseEvent e) { handleMouseEvent(e); }
        public void mouseReleased(MouseEvent e) { handleMouseEvent(e); }
        private void handleMouseEvent(MouseEvent e) {
            if (e.isPopupTrigger()) {
                menu.show(e.getComponent(),e.getX(),e.getY());
                e.consume();
            }
        }
    }

    /**
     * Shows an error message
     * @param message the error message
     */
    private static void error(String message) {
        System.out.println(message);
        JOptionPane.showMessageDialog(null, message, "VGA2USB",
        JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows an error message
     * @param parent the parent component for the error dialog
     * @param message the error message
     */
    private static void error(Component parent, String message) {
        System.out.println(message);
        JOptionPane.showMessageDialog(parent, message, "VGA2USB",
        JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Runs the test.
     * @param grabber the grabber to test
     * @throws IOException if an I/O error occurs
     */
    private static void test(Grabber grabber) throws IOException {
        System.out.println("Found " + grabber.getDeviceType() +
            " device, s/n " + grabber.getSN());

        // Detect video mode
        String title, status;
        int width, height;
        VideoMode vm = grabber.detectVideoMode();
        if (vm != null) {
            System.out.println("Detected " + vm);
            title = grabber + " - " + vm;
            status = "Starting capture...";
            width = vm.getWidth();
            height = vm.getHeight();
        } else {
            title = grabber.toString();
            status = "No signal detected";
            width = 300;
            height = 200;
        }

        // Show the window
        JFrame frame = new JFrame(title);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
					Rec.result.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out.println("result close");
					e1.printStackTrace();
				}
            	System.exit(0);
            }
        });

        if (grabber.isKVMCapable()) {
            System.out.println("Detected KVM device");
            new KVMSwitch(frame, new KVMHandler(new KVM(grabber)));
        }

        JLabel label = new JLabel(status, JLabel.CENTER);
        Dimension size = new Dimension(width, height);
        label.setSize(size);
        label.setPreferredSize(size);
        label.setMaximumSize(size);

        // Actions
        Action adjustAction = new AdjustAction(grabber, frame);
        Action exitAction = new ExitAction();

        // Menubar
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Test");
        menu.add(adjustAction);
        menu.addSeparator();
        menu.add(exitAction);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        // Popup menu
        JPopupMenu popup = new JPopupMenu();
        popup.add(adjustAction);
        popup.add(exitAction);
        label.addMouseListener(new PopupHandler(popup));

        // Layout
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(label);

        frame.pack();
       // frame.set
        frame.setBounds(0,0, 0, 0);
        
        frame.setVisible(true);

        // Run capture on the main thread, but we could create a
        // separate thread if necessary
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        new Capture(grabber, frame, label).run();
    }

    public static void main(String[] args) {

    	SVM svm=new SVM();
    	try{
    		svm=Rec.init();
    		Rec.readParam();
 
    	}catch(Exception e){e.printStackTrace();}//初始化相关参数，后续修改参数方法，在此添加
    	
    	Capture.SetSVM(svm);
    	
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
        catch (Throwable x) {}

        try {
            // Open the grabber
            Grabber grabber = null;
            if (args.length > 0) {
                if (args[0].indexOf('.') >= 0) {
                    // Assume that this is a network address
                    try {
                        grabber = new Grabber(InetAddress.getByName(args[0]));
                    } catch (AccessDeniedException x) {
                        error("Access denied");
                    } catch (IOException x) {
                        error("Can't connect to " + args[0]);
                    }
                } else {
                    // Otherwise assume that it's a serial number
                    try {
                        grabber = new Grabber(args[0]);
                    } catch (IOException y) {
                        error("No such device: " + args[0]);
                    }
                }
            } else {
                try {
                    grabber = new Grabber();
                } catch (IOException x) {
                    error("No frame grabber found");
                }
            }

            // Run the test
            if (grabber != null) {
                test(grabber);
                grabber.close();
            }

            System.exit(0);

        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
        try{Rec.result.close();}catch(Exception e){e.printStackTrace();}
        
    }
}
