/****************************************************************************
 *
 * $Id: GrabParamEditor.java 14372 2011-10-08 12:36:28Z monich $
 *
 * Copyright (C) 2009-2011 Epiphan Systems Inc. All rights reserved.
 *
 * Frame grabber test application.
 *
 ****************************************************************************/

package com.epiphan.vga2usb.test;

/* java.io */
import java.io.IOException;

/* java.awt */
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/* javax.swing */
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/* javax.swing.border */
import javax.swing.border.EmptyBorder;

/* com.epiphan.vga2usb */
import com.epiphan.vga2usb.GrabParameters;
import com.epiphan.vga2usb.Grabber;

/**
 * Displays and edits grab parameters
 */
class GrabParamEditor {

    private static final int GAP = 5;

    private Grabber grabber;
    private GrabParameters gp;
    private Component parent;
    private JDialog dialog;

    private GrabParamGroup hshiftGroup;
    private GrabParamGroup vshiftGroup;
    private GrabParamGroup offsetGainGroup;

    GrabParamEditor(Grabber g, Component p) {
        grabber = g;
        parent = p;
    }

    private void displayGrabParams() {
        hshiftGroup.setSelected((gp.flags & gp.VALID_HSHIFT) != 0);
        hshiftGroup.fields[0].field.setText(Integer.toString(gp.hshift));
        vshiftGroup.setSelected((gp.flags & gp.VALID_VSHIFT) != 0);
        vshiftGroup.fields[0].field.setText(Integer.toString(gp.vshift));
        int offset = (gp.offset_r + gp.offset_g + gp.offset_b)/3;
        int gain = (gp.gain_r + gp.gain_g + gp.gain_b)/3;
        offsetGainGroup.setSelected((gp.flags & gp.VALID_OFFSETGAIN) != 0);
        offsetGainGroup.fields[0].field.setText(Integer.toString(offset));
        offsetGainGroup.fields[1].field.setText(Integer.toString(gain));
    }

    private void parseGrabParams() {
        gp.hshift = getInt(hshiftGroup.fields[0].field, gp.hshift);
        gp.vshift = getInt(vshiftGroup.fields[0].field, gp.vshift);
        gp.offset_r = gp.offset_g = gp.offset_b = getInt(
            offsetGainGroup.fields[0].field,
            (gp.offset_r + gp.offset_g + gp.offset_b)/3);
        gp.gain_r = gp.gain_g = gp.gain_b = getInt(
            offsetGainGroup.fields[1].field,
            (gp.gain_r + gp.gain_g + gp.gain_b)/3);

        if (hshiftGroup.checkBox.isSelected()) {
            gp.flags |= gp.VALID_HSHIFT;
        } else {
            gp.flags &= ~gp.VALID_HSHIFT;
        }

        if (vshiftGroup.checkBox.isSelected()) {
            gp.flags |= gp.VALID_VSHIFT;
        } else {
            gp.flags &= ~gp.VALID_VSHIFT;
        }

        if (offsetGainGroup.checkBox.isSelected()) {
            gp.flags |= gp.VALID_OFFSETGAIN;
        } else {
            gp.flags &= ~gp.VALID_OFFSETGAIN;
        }
    }

    /**
     * Shows the dialog with the current grab parameters.
     * @throws IOException if an I/O occurs
     */
    void show() throws IOException {
        if (dialog == null) {
            createDialog();
        }
        gp = grabber.getGrabParameters();
        displayGrabParams();
        JComponent content = (JComponent)dialog.getContentPane();
        content.paintImmediately(content.getBounds());
        dialog.show();
    }

    private static int getInt(JTextField field, int defaultValue) {
        int result = defaultValue;
        try { result = Integer.parseInt(field.getText()); }
        catch (Exception x) {}
        return result;
    }

    private void apply() {
        parseGrabParams();
        try { grabber.setGrabParameters(gp); }
        catch (IOException x) {
            x.printStackTrace();
            error("Failed to apply grab parameters. Check the values and try again.");
        }
    }

    private void applyAndRefresh() {
        apply();
        try {
            gp = grabber.getGrabParameters();
            displayGrabParams();
        } catch (IOException x) {
            x.printStackTrace();
            error("Failed to refresh grab parameters");
        }
    }

    private void error(String message) {
        System.out.println(message);
        JOptionPane.showMessageDialog(dialog, message, "VGA2USB",
        JOptionPane.ERROR_MESSAGE);
    }

    //======================================================================
    //          L A Y O U T
    //======================================================================

    private GrabParamGroup addParamGroup(Container parent, int n) {
        GrabParamGroup group = new GrabParamGroup(n);
        JPanel groupPanel = new JPanel(new GridBagLayout());

        GridBagConstraints c1 = new GridBagConstraints();
        c1.anchor = GridBagConstraints.WEST;
        c1.insets.right = c1.insets.bottom = GAP;

        GridBagConstraints c2 = (GridBagConstraints)c1.clone();
        c2.weightx = 1;

        GridBagConstraints c3 = new GridBagConstraints();
        c3.anchor = GridBagConstraints.EAST;
        c3.gridwidth = GridBagConstraints.REMAINDER;
        c3.insets.bottom = GAP;

        groupPanel.add(group.checkBox, c1);
        for (int i=0; i<n; i++) {
            if (i > 0) groupPanel.add(new JLabel());
            groupPanel.add(group.fields[i].label, c2);
            groupPanel.add(group.fields[i].field, c3);
        }

        parent.add(groupPanel);
        return group;
    }

    private void createDialog() {
        dialog = new JDialog(JOptionPane.getFrameForComponent(parent),
            "Capture Parameters", false);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(Box.createGlue());
        buttonPanel.add(new JButton(new OKAction()));
        buttonPanel.add(Box.createHorizontalStrut(GAP));
        buttonPanel.add(new JButton(new CancelAction()));
        buttonPanel.add(Box.createHorizontalStrut(GAP));
        buttonPanel.add(new JButton(new ApplyAction()));

        // Adjustment panel
        Box adjustPane = Box.createVerticalBox();

        // Horizontal shift
        hshiftGroup = addParamGroup(adjustPane, 1);
        hshiftGroup.fields[0].label.setText("Horizontal shift:");

        // Vertical shift
        vshiftGroup = addParamGroup(adjustPane, 1);
        vshiftGroup.fields[0].label.setText("Vertical shift:");

        // Offset/gain
        offsetGainGroup = addParamGroup(adjustPane, 2);
        offsetGainGroup.fields[0].label.setText("Offset (brightness):");
        offsetGainGroup.fields[1].label.setText("Gain (contrast):");

        // Add all this stuff to the dialog
        JPanel contentPane = new JPanel(new BorderLayout(GAP, GAP));
        contentPane.add(adjustPane, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        contentPane.setBorder(new EmptyBorder(GAP, GAP, GAP, GAP));
        dialog.setContentPane(contentPane);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
    }

    //======================================================================
    //          I N N E R    C L A S S E S
    //======================================================================

    private static class GrabParamField {
        JLabel label;
        JTextField field;
        GrabParamField() {
            label = new JLabel();
            field = new JTextField(5);
            label.setLabelFor(field);
        }
    }

    private static class GrabParamGroup {
        JCheckBox checkBox;
        GrabParamField[] fields;
        GrabParamGroup(int n) {
            checkBox = new JCheckBox();
            fields = new GrabParamField[n];
            for (int i=0; i<n; i++) {
                fields[i] = new GrabParamField();
            }
            updateFields();
            checkBox.addActionListener(new CheckBoxListener());
        }
        void setSelected(boolean select) {
            checkBox.setSelected(select);
            updateFields();
        }
        private void updateFields() {
            boolean enable = checkBox.isSelected();
            for (int i=0; i<fields.length; i++) {
                fields[i].label.setEnabled(enable);
                fields[i].field.setEnabled(enable);
            }
        }

        private class CheckBoxListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                updateFields();
            }
        }
    }

    private class ApplyAction extends AbstractAction {
        ApplyAction() { super("Apply"); }
        public void actionPerformed(ActionEvent e) {
            applyAndRefresh();
        }
    }

    private class OKAction extends AbstractAction {
        OKAction() { super("OK"); }
        public void actionPerformed(ActionEvent e) {
            applyAndRefresh();
            dialog.dispose();
        }
    }

    private class CancelAction extends AbstractAction {
        CancelAction() { super("Cancel"); }
        public void actionPerformed(ActionEvent e) {
            dialog.dispose();
        }
    }
}
