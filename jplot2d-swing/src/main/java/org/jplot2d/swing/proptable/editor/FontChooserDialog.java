/*
 * This file is part of Herschel Common Science System (HCSS).
 * Copyright 2001-2010 Herschel Science Ground Segment Consortium
 *
 * HCSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * HCSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with HCSS.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.jplot2d.swing.proptable.editor;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 */
public class FontChooserDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private boolean OK;

    private Font fontValue;

    private JButton btnOK;

    private JButton btnCancel;

    private JScrollPane jScrollPane3;

    private JLabel sizeLabel;

    private JScrollPane jScrollPane2;

    private JLabel previewLabel;

    private JPanel previewPanel;

    private JList familyList;

    private JScrollPane jScrollPane1;

    private JTextField txtFamily;

    private JLabel fontLabel;

    private JList lstStyle;

    private JTextField txtStyle;

    private JLabel styleLabel;

    private JTextField txtSize;

    private JList lstSize;

    public FontChooserDialog(Window parent, boolean modal) {
        super(parent);
        setModal(modal);
        initComponents();
    }

    private static class FontSizeListModel extends AbstractListModel {

        private static final long serialVersionUID = 1L;

        private final List<Integer> sizeList = new ArrayList<>();

        private FontSizeListModel() {
            sizeList.add(3);
            sizeList.add(5);
            sizeList.add(8);
            sizeList.add(10);
            sizeList.add(12);
            sizeList.add(14);
            sizeList.add(18);
            sizeList.add(24);
            sizeList.add(36);
            sizeList.add(48);
        }

        public Object getElementAt(int index) {
            return sizeList.get(index);
        }

        public int getSize() {
            return sizeList.size();
        }

    }

    private static class FontStyleListModel extends AbstractListModel {
        private static final long serialVersionUID = 1L;

        private final List<String> list = new ArrayList<>();

        private FontStyleListModel() {
            list.add("Plain");
            list.add("Bold");
            list.add("Italic");
            list.add("Bold Italic");
        }

        public Object getElementAt(int index) {
            return list.get(index);
        }

        public int getSize() {
            return list.size();
        }
    }

    private static class FontFamilyListModel extends AbstractListModel {
        private static final long serialVersionUID = 1L;

        private final List<String> list = new ArrayList<>();

        private FontFamilyListModel() {
            String[] font_families = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getAvailableFontFamilyNames();
            Collections.addAll(list, font_families);
        }

        public Object getElementAt(int index) {
            return list.get(index);
        }

        public int getSize() {
            return list.size();
        }
    }

    public boolean isOK() {
        return OK;
    }

    public Font getFontValue() {
        return fontValue;
    }

    public void setFontValue(Font f) {
        if (f != null) {
            txtFamily.setText(f.getFamily());
            familyList.setSelectedValue(f.getFamily(), true);
            String style = "Plain";
            switch (f.getStyle()) {
                case Font.PLAIN:
                    style = "Plain";
                    break;
                case Font.BOLD:
                    style = "Bold";
                    break;
                case Font.ITALIC:
                    style = "Italic";
                    break;
                case Font.BOLD | Font.ITALIC:
                    style = "Bold Italic";
                    break;
            }
            txtStyle.setText(style);
            lstStyle.setSelectedValue(style, true);
            txtSize.setText("" + f.getSize());
            lstSize.setSelectedValue(f.getSize(), true);
            previewLabel.setFont(f);
            previewLabel.setText(f.getFamily() + " " + f.getSize() + " " + style);
        }
    }

    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Choose Font");
        setModal(true);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout
                .createSequentialGroup()
                .addContainerGap()
                .addGroup(
                        layout.createParallelGroup(Alignment.TRAILING)
                                .addGroup(
                                        Alignment.LEADING,
                                        layout.createSequentialGroup()
                                                .addGroup(
                                                        layout.createParallelGroup()
                                                                .addComponent(getJScrollPane1())
                                                                .addComponent(getFamilyLabel())
                                                                .addComponent(getTxtFamily()))
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addGroup(
                                                        layout.createParallelGroup()
                                                                .addComponent(getJScrollPane2())
                                                                .addComponent(getStyleLabel())
                                                                .addComponent(getTxtStyle()))
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addGroup(
                                                        layout.createParallelGroup()
                                                                .addComponent(getJScrollPane3())
                                                                .addComponent(getSizeLabel())
                                                                .addComponent(getTxtSize())))
                                .addComponent(getPreviewPanel())
                                .addGroup(
                                        layout.createSequentialGroup()
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(getBtnOK())
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(getBtnCancel()))).addContainerGap());

        layout.linkSize(SwingConstants.HORIZONTAL, btnCancel, btnOK);

        layout.setVerticalGroup(layout
                .createSequentialGroup()
                .addContainerGap()
                .addGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(getFamilyLabel()).addComponent(getStyleLabel())
                                .addComponent(getSizeLabel()))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(getTxtStyle()).addComponent(getTxtFamily())
                                .addComponent(getTxtSize()))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(getJScrollPane2()).addComponent(getJScrollPane1())
                                .addComponent(getJScrollPane3()))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(getPreviewPanel())
                .addGap(18, 18, 18)
                .addGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(getBtnCancel()).addComponent(getBtnOK()))
                .addContainerGap());

        pack();
    }

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {
        OK = false;
        dispose();
    }

    private void btnOKActionPerformed(ActionEvent evt) {
        String family = txtFamily.getText();
        if (family.trim().length() == 0) {
            JOptionPane.showMessageDialog(this, "Please choose a font family!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        String style = txtStyle.getText();
        if (style.trim().length() == 0) {
            JOptionPane.showMessageDialog(this, "Please choose a font style!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        String size = txtSize.getText();
        if (size.trim().length() == 0) {
            JOptionPane.showMessageDialog(this, "Please choose a font size!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int isize = Integer.parseInt(size);
            if (isize < 3) {
                JOptionPane.showMessageDialog(this,
                        "Font size should be an integer bigger than 2!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            int istyle = Font.PLAIN;
            switch (style) {
                case "Plain":
                    istyle = Font.PLAIN;
                    break;
                case "Bold":
                    istyle = Font.BOLD;
                    break;
                case "Italic":
                    istyle = Font.ITALIC;
                    break;
                case "Bold Italic":
                    istyle = Font.ITALIC | Font.BOLD;
                    break;
            }
            OK = true;
            fontValue = new Font(family, istyle, isize);
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Font size should be an integer!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void lstFamilyValueChanged(ListSelectionEvent evt) {
        String family = (String) familyList.getSelectedValue();
        txtFamily.setText(family);
        refreshPreview();
    }

    private void lstStyleValueChanged(ListSelectionEvent evt) {
        String style = (String) lstStyle.getSelectedValue();
        txtStyle.setText(style);
        refreshPreview();
    }

    private void lstSizeValueChanged(ListSelectionEvent evt) {
        String size = (lstSize.getSelectedValue()).toString();
        txtSize.setText(size);
        refreshPreview();
    }

    private void txtSizeActionPerformed(java.awt.event.ActionEvent evt) {
        String tSize = txtSize.getText();
        try {
            int size = Integer.parseInt(tSize);
            if (size < 3) {
                JOptionPane.showMessageDialog(this,
                        "Font size should be an integer bigger than 2!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            lstSize.setSelectedValue(size, true);
            refreshPreview();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Font size should be an integer!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshPreview() {
        String family = txtFamily.getText();
        if (family.trim().length() == 0) {
            return;
        }
        String style = txtStyle.getText();
        if (style.trim().length() == 0) {
            return;
        }
        String size = txtSize.getText();
        if (size.trim().length() == 0) {
            return;
        }
        try {
            int isize = Integer.parseInt(size);
            if (isize < 3) {
                return;
            }
            int istyle = Font.PLAIN;
            switch (style) {
                case "Plain":
                    istyle = Font.PLAIN;
                    break;
                case "Bold":
                    istyle = Font.BOLD;
                    break;
                case "Italic":
                    istyle = Font.ITALIC;
                    break;
                case "Bold Italic":
                    istyle = Font.ITALIC | Font.BOLD;
                    break;
            }
            Font f = new Font(family, istyle, isize);
            previewLabel.setFont(f);
            previewLabel.setText(f.getFamily() + " " + f.getSize() + " " + style);
        } catch (NumberFormatException ignored) {
        }
    }

    private JButton getBtnOK() {
        if (btnOK == null) {
            btnOK = new JButton();
            btnOK.setText("OK");
            btnOK.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    btnOKActionPerformed(evt);
                }
            });
        }
        return btnOK;
    }

    private JButton getBtnCancel() {
        if (btnCancel == null) {
            btnCancel = new JButton();
            btnCancel.setText("Cancel");
            btnCancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    btnCancelActionPerformed(evt);
                }
            });
        }
        return btnCancel;
    }

    private JScrollPane getJScrollPane3() {
        if (jScrollPane3 == null) {
            jScrollPane3 = new JScrollPane();
            jScrollPane3.setViewportView(getLstSize());
        }
        return jScrollPane3;
    }

    @SuppressWarnings("unchecked")
    private JList getLstSize() {
        if (lstSize == null) {
            lstSize = new JList(new FontSizeListModel());
            lstSize.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            lstSize.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent evt) {
                    lstSizeValueChanged(evt);
                }
            });
        }
        return lstSize;
    }

    private JLabel getSizeLabel() {
        if (sizeLabel == null) {
            sizeLabel = new JLabel();
            sizeLabel.setText("Size:");
        }
        return sizeLabel;
    }

    private JTextField getTxtSize() {
        if (txtSize == null) {
            txtSize = new JTextField();
            txtSize.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    txtSizeActionPerformed(evt);
                }
            });
        }
        return txtSize;
    }

    private JLabel getStyleLabel() {
        if (styleLabel == null) {
            styleLabel = new JLabel();
            styleLabel.setText("Style:");
        }
        return styleLabel;
    }

    private JTextField getTxtStyle() {
        if (txtStyle == null) {
            txtStyle = new JTextField();
            txtStyle.setEditable(false);
        }
        return txtStyle;
    }

    private JScrollPane getJScrollPane2() {
        if (jScrollPane2 == null) {
            jScrollPane2 = new JScrollPane();
            jScrollPane2.setViewportView(getLstStyle());
        }
        return jScrollPane2;
    }

    @SuppressWarnings("unchecked")
    private JList getLstStyle() {
        if (lstStyle == null) {
            lstStyle = new JList(new FontStyleListModel());
            lstStyle.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            lstStyle.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent evt) {
                    lstStyleValueChanged(evt);
                }
            });
        }
        return lstStyle;
    }

    private JLabel getFamilyLabel() {
        if (fontLabel == null) {
            fontLabel = new JLabel();
            fontLabel.setText("Font:");
        }
        return fontLabel;
    }

    private JTextField getTxtFamily() {
        if (txtFamily == null) {
            txtFamily = new JTextField();
            txtFamily.setEditable(false);
        }
        return txtFamily;
    }

    private JScrollPane getJScrollPane1() {
        if (jScrollPane1 == null) {
            jScrollPane1 = new JScrollPane();
            jScrollPane1.setViewportView(getLstFamily());
        }
        return jScrollPane1;
    }

    @SuppressWarnings("unchecked")
    private JList getLstFamily() {
        if (familyList == null) {
            familyList = new JList(new FontFamilyListModel());
            familyList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            familyList.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent evt) {
                    lstFamilyValueChanged(evt);
                }
            });
        }
        return familyList;
    }

    private JPanel getPreviewPanel() {
        if (previewPanel == null) {
            previewPanel = new JPanel();
            previewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Preview"));
            previewPanel.setLayout(new java.awt.BorderLayout());
            previewPanel.add(getPreviewLabel(), BorderLayout.CENTER);
        }
        return previewPanel;
    }

    private JLabel getPreviewLabel() {
        if (previewLabel == null) {
            previewLabel = new JLabel();
            previewLabel.setText("Sample Text");
            previewLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        }
        return previewLabel;
    }

}
