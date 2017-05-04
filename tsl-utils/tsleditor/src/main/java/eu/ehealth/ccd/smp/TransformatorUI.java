/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ehealth.ccd.smp;

import eu.ehealth.ccd.exceptions.SignatureCancelledException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class representing the main SMP JFrame. Adds the features of:
 * 1) TSL-to-SMP transformation
 * 2) Signing of SMP files
 * 3) Upload of SMP files to SMP server
 *
 * @author joao.cunha
 */
public class TransformatorUI extends javax.swing.JFrame {
    private static final Logger logger = LoggerFactory.getLogger(TransformatorUI.class);
    private File tslFile;
    private File ismFile;
    private File outputFolder;
    private File keystoreFile;
    private File smpFolder;
    // Variables declaration - do not modify
    private javax.swing.JButton ismChooseButton;
    private javax.swing.JTextField ismFileTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JButton keystoreChooseButton;
    private javax.swing.JTextField keystoreFileTextField;
    private javax.swing.JPasswordField keystorePassPasswordField;
    private javax.swing.JButton outputFolderChooseButton;
    private javax.swing.JTextField outputFolderTextField;
    private javax.swing.JTextField privateKeyAliasTextField;
    private javax.swing.JPasswordField privateKeyPassPasswordField;
    private javax.swing.JCheckBox signCheckbox;
    private javax.swing.JList smpFilesList;
    private javax.swing.JButton smpFolderChooseButton;
    private javax.swing.JTextField smpFolderTextField;
    private javax.swing.JPasswordField smpPasswordPasswordField;
    private javax.swing.JTextField smpServerTextField;
    private javax.swing.JTextField smpUsernameTextField;
    private javax.swing.JButton transformButton;
    private javax.swing.JButton tslChooseButton;
    private javax.swing.JTextField tslFileTextField;
    private javax.swing.JButton uploadButton;

    /**
     * Creates new form TransformatorUI
     */
    public TransformatorUI() {
        initComponents();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TransformatorUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TransformatorUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TransformatorUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TransformatorUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TransformatorUI().setVisible(true);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        tslChooseButton = new javax.swing.JButton();
        tslFileTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        ismFileTextField = new javax.swing.JTextField();
        ismChooseButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        outputFolderTextField = new javax.swing.JTextField();
        outputFolderChooseButton = new javax.swing.JButton();
        signCheckbox = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel10 = new javax.swing.JLabel();
        smpFolderTextField = new javax.swing.JTextField();
        smpFolderChooseButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        smpFilesList = new javax.swing.JList();
        jLabel11 = new javax.swing.JLabel();
        uploadButton = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        keystoreFileTextField = new javax.swing.JTextField();
        keystoreChooseButton = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        keystorePassPasswordField = new javax.swing.JPasswordField();
        jLabel9 = new javax.swing.JLabel();
        privateKeyAliasTextField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        privateKeyPassPasswordField = new javax.swing.JPasswordField();
        transformButton = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        smpServerTextField = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        smpUsernameTextField = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        smpPasswordPasswordField = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tslChooseButton.setText("Choose...");
        tslChooseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tslChooseButtonActionPerformed(evt);
            }
        });

        tslFileTextField.setEditable(false);

        jLabel1.setText("Use this GUI to transform a TSL file into the multiple SMP files and upload them.");

        jLabel2.setText("You'll need to choose the search mask in order to include it in its SMP file.");

        jLabel3.setText("TSL file:");

        jLabel4.setText("Inter. Search Mask: ");

        ismFileTextField.setEditable(false);

        ismChooseButton.setText("Choose...");
        ismChooseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ismChooseButtonActionPerformed(evt);
            }
        });

        jLabel5.setText("Output folder:");

        outputFolderTextField.setEditable(false);

        outputFolderChooseButton.setText("Choose...");
        outputFolderChooseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputFolderChooseButtonActionPerformed(evt);
            }
        });

        signCheckbox.setText("Sign SMP files");
        signCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signCheckboxActionPerformed(evt);
            }
        });

        jLabel10.setText("SMP files folder:");

        smpFolderTextField.setEditable(false);

        smpFolderChooseButton.setText("Choose...");
        smpFolderChooseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smpFolderChooseButtonActionPerformed(evt);
            }
        });

        smpFilesList.setCellRenderer(new FileListCellRenderer());
        smpFilesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                smpFilesListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(smpFilesList);

        jLabel11.setText("Select the desired SMP files:");

        uploadButton.setText("Upload");
        uploadButton.setEnabled(false);
        uploadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uploadButtonActionPerformed(evt);
            }
        });

        jLabel6.setText("Keystore:");

        keystoreFileTextField.setEditable(false);
        keystoreFileTextField.setEnabled(false);

        keystoreChooseButton.setText("Choose...");
        keystoreChooseButton.setEnabled(false);
        keystoreChooseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keystoreChooseButtonActionPerformed(evt);
            }
        });

        jLabel7.setText("Keystore password:");

        keystorePassPasswordField.setEnabled(false);
        keystorePassPasswordField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keystorePassPasswordFieldActionPerformed(evt);
            }
        });

        jLabel9.setText("Private key alias:");

        privateKeyAliasTextField.setEnabled(false);

        jLabel8.setText("Private key password:");

        privateKeyPassPasswordField.setEnabled(false);

        transformButton.setText("Transform");
        transformButton.setEnabled(false);
        transformButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transformButtonActionPerformed(evt);
            }
        });

        jLabel14.setText("SMP server:");

        smpServerTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smpServerTextFieldActionPerformed(evt);
            }
        });

        jLabel12.setText("Username:");

        jLabel13.setText("Password:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jLabel8)
                                                                        .addComponent(jLabel9))
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(privateKeyAliasTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 429, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                                .addGap(6, 6, 6)
                                                                                .addComponent(privateKeyPassPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 429, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jLabel7)
                                                                        .addComponent(jLabel6))
                                                                .addGap(18, 18, Short.MAX_VALUE)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                        .addComponent(keystorePassPasswordField, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                                                                        .addComponent(keystoreFileTextField))))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(keystoreChooseButton))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(signCheckbox, javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 675, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(transformButton))
                                                        .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                                                .addComponent(jLabel11)
                                                                                .addGap(18, 18, 18)
                                                                                .addComponent(jScrollPane1))
                                                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                                                .addComponent(jLabel10)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(smpFolderTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 459, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(jLabel14)
                                                                                        .addComponent(jLabel12)
                                                                                        .addComponent(jLabel13))
                                                                                .addGap(42, 42, 42)
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(smpServerTextField)
                                                                                        .addComponent(smpUsernameTextField)
                                                                                        .addComponent(smpPasswordPasswordField))))
                                                                .addGap(18, 18, 18)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(uploadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(smpFolderChooseButton))))
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel4)
                                                        .addComponent(jLabel3)
                                                        .addComponent(jLabel5))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                .addComponent(outputFolderTextField)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(outputFolderChooseButton))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                .addComponent(ismFileTextField)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(ismChooseButton))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                .addComponent(tslFileTextField)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(tslChooseButton)))))
                                .addGap(32, 32, 32))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(tslFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel3)
                                        .addComponent(tslChooseButton))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(ismFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel4)
                                        .addComponent(ismChooseButton))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(outputFolderTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel5)
                                        .addComponent(outputFolderChooseButton))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(signCheckbox)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel6)
                                        .addComponent(keystoreFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(keystoreChooseButton))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel7)
                                        .addComponent(keystorePassPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel9)
                                        .addComponent(privateKeyAliasTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(12, 12, 12)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel8)
                                        .addComponent(privateKeyPassPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(11, 11, 11)
                                .addComponent(transformButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel10)
                                        .addComponent(smpFolderTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(smpFolderChooseButton))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel11)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel14)
                                                        .addComponent(smpServerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(smpUsernameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel12))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(smpPasswordPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel13))
                                                .addGap(28, 28, 28))
                                        .addComponent(uploadButton, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(4, 4, 4))
        );

        pack();
    }// </editor-fold>

    private void tslChooseButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        final JFileChooser tslFileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TSL files", "xml");
        tslFileChooser.setFileFilter(filter);
        tslFileChooser.setAcceptAllFileFilterUsed(false);
        tslFileChooser.setDialogTitle("Choose the TSL file");
        int returnTslFileChooser = tslFileChooser.showOpenDialog(rootPane);
        if (returnTslFileChooser == JFileChooser.APPROVE_OPTION) {
            tslFile = tslFileChooser.getSelectedFile();
            String tslFileLocation = tslFile.getAbsolutePath();
            tslFileTextField.setText(tslFileLocation);
            this.enableTransformButton();
        }
    }

    private void ismChooseButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        final JFileChooser ismFileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("ISM files", "xml");
        ismFileChooser.setFileFilter(filter);
        ismFileChooser.setAcceptAllFileFilterUsed(false);
        ismFileChooser.setDialogTitle("Choose the International Search Mask file");
        int returnIsmFileChooser = ismFileChooser.showOpenDialog(rootPane);
        if (returnIsmFileChooser == JFileChooser.APPROVE_OPTION) {
            ismFile = ismFileChooser.getSelectedFile();
            String ismFileLocation = ismFile.getAbsolutePath();
            ismFileTextField.setText(ismFileLocation);
            this.enableTransformButton();
        }
    }

    private void outputFolderChooseButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        final JFileChooser outputFolderChooser = new JFileChooser();
        outputFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        outputFolderChooser.setAcceptAllFileFilterUsed(false);
        outputFolderChooser.setDialogTitle("Choose the output folder");
        int returnOutputFolderChooser = outputFolderChooser.showOpenDialog(rootPane);
        if (returnOutputFolderChooser == JFileChooser.APPROVE_OPTION) {
            outputFolder = outputFolderChooser.getSelectedFile();
            String outputFolderLocation = outputFolder.getAbsolutePath();
            outputFolderTextField.setText(outputFolderLocation);
            this.enableTransformButton();
        }
    }

    private void signCheckboxActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        boolean showSignatureFields = signCheckbox.isSelected();
        keystoreFileTextField.setEnabled(showSignatureFields);
        keystoreChooseButton.setEnabled(showSignatureFields);
        keystorePassPasswordField.setEnabled(showSignatureFields);
        privateKeyAliasTextField.setEnabled(showSignatureFields);
        privateKeyPassPasswordField.setEnabled(showSignatureFields);
        if (!showSignatureFields) {
            keystoreFileTextField.setText(null);
            keystoreFile = null;
            keystorePassPasswordField.setText(null);
            privateKeyAliasTextField.setText(null);
            privateKeyPassPasswordField.setText(null);
        }
    }

    private void transformButtonActionPerformed(java.awt.event.ActionEvent evt) {
        //         TODO add your handling code here:
        try {
            //            TransformerFactory factory = TransformerFactory.newInstance();
            //            TransformerFactory factory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
            TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();
            //            Source xslt = new StreamSource(new File("/resources/tsl2smp.xsl"));
            Source xslt = new StreamSource(TransformatorUI.class.getResourceAsStream("/xslt/tsl2smp.xsl"));
            Transformer transformer = factory.newTransformer(xslt);
            transformer.setErrorListener(new TslSmpErrorListener());
            transformer.setParameter("ism", ismFile != null ? ismFile.toURI() : null);
            transformer.setParameter("ismCreationDate", ismFile != null ? GenericUtils.getCreationTime(ismFile.getAbsolutePath()) : null);
            transformer.setParameter("outputFolder", outputFolder.getAbsolutePath());

            logger.info("Going to transform...\nISM URI: " + (ismFile != null ? ismFile.toURI() : "null") +
                    "\n" + (ismFile != null ? GenericUtils.convertFileToString(ismFile) : "null") +
                    "\nISM CreationDate: " + (ismFile != null ? GenericUtils.getCreationTime(ismFile.getAbsolutePath()) : "null"));

            Source text = new StreamSource(tslFile);
            // transform TSL file into SMP files. XSLT is the artifact that generates the files
            transformer.transform(text, new DOMResult());

            // check if we should sign SMP files
            if (signCheckbox.isSelected()) {
                // At this moment the XSLT already generated the unsigned SMP files in the SMP output folder
                String country = tslFile.getName().substring(tslFile.getName().length() - 7, tslFile.getName().length() - 5);
                File smpOutputFolder = new File(outputFolder.getAbsolutePath(), country);

                // show signature confirmation dialog
                SignatureConfirmationDialog signatureConfirmationDialog = new SignatureConfirmationDialog(this, true, smpOutputFolder);
                signatureConfirmationDialog.setVisible(true);
                boolean userConfirm = signatureConfirmationDialog.isUserConfirm();
                if (!userConfirm) {
                    throw new SignatureCancelledException("Signature cancelled by user (scheme operator)!");
                }

                // get signing information
                char[] keystorePasswordArray = keystorePassPasswordField.getPassword();
                String keystorePassword = new String(keystorePasswordArray);
                String privateKeyAlias = privateKeyAliasTextField.getText();
                char[] privateKeyPasswordArray = privateKeyPassPasswordField.getPassword();
                String privateKeyPassword = new String(privateKeyPasswordArray);

                for (File file : smpOutputFolder.listFiles()) {
                    try (final InputStream fileInputStream = new FileInputStream(file)) {
//                    SignatureUtils.output(SignatureUtils.sign(fileInputStream, keystoreFile, keystorePassword, privateKeyAlias, privateKeyPassword), file.getAbsolutePath());

                        // obtain reference to <Extension> and sign it
                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        dbf.setNamespaceAware(true);
                        DocumentBuilder db = dbf.newDocumentBuilder();
                        Document smpRecord = db.parse(fileInputStream);
                        // find the pointer.
                        final String ns = "http://busdox.org/serviceMetadata/publishing/1.0/";
                        NodeList elements = smpRecord.getElementsByTagNameNS(ns, "ServiceInformation");
                        Node serviceInformation = elements.item(0);
                        Node n = serviceInformation.getLastChild();
                        Element xtPointer = (Element) n;

                        SignatureUtils.sign(xtPointer, keystoreFile, keystorePassword, privateKeyAlias, privateKeyPassword);
                        // Output the resulting document.
                        TransformerFactory tf = TransformerFactory.newInstance();
                        Transformer trans = tf.newTransformer();
                        trans.transform(new DOMSource(smpRecord), new StreamResult(file));
                    }
                }
                // Zero out the possible passwords, for security.
                Arrays.fill(keystorePasswordArray, '0');
                Arrays.fill(privateKeyPasswordArray, '0');
            }

            JOptionPane.showMessageDialog(this, "Transformation successfully made. SMP files have been generated at " +
                    outputFolder.getAbsolutePath(), "Info", JOptionPane.INFORMATION_MESSAGE);

        } catch (SignatureCancelledException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Signature Cancelled", JOptionPane.ERROR_MESSAGE);
        } catch (TransformerException e) {
            String stackTrace = GenericUtils.printExceptionStackTrace(e);
            JOptionPane.showMessageDialog(this, createScrollablePane(stackTrace), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            String stackTrace = GenericUtils.printExceptionStackTrace(e);
            JOptionPane.showMessageDialog(this, createScrollablePane(stackTrace), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            String stackTrace = GenericUtils.printExceptionStackTrace(e);
            JOptionPane.showMessageDialog(this, createScrollablePane(stackTrace), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void keystoreChooseButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        final JFileChooser keystoreFileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JKS files", "jks");
        keystoreFileChooser.setFileFilter(filter);
        keystoreFileChooser.setAcceptAllFileFilterUsed(false);
        keystoreFileChooser.setDialogTitle("Choose the keystore (JKS) file");
        int returnKeystoreFileChooser = keystoreFileChooser.showOpenDialog(rootPane);
        if (returnKeystoreFileChooser == JFileChooser.APPROVE_OPTION) {
            keystoreFile = keystoreFileChooser.getSelectedFile();
            String keystoreFileLocation = keystoreFile.getAbsolutePath();
            keystoreFileTextField.setText(keystoreFileLocation);
        }
    }

    private void keystorePassPasswordFieldActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void smpFolderChooseButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        final JFileChooser smpFolderChooser = new JFileChooser();
        smpFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        smpFolderChooser.setAcceptAllFileFilterUsed(false);
        smpFolderChooser.setDialogTitle("Choose the SMP files folder");
        int returnSmpFolderChooser = smpFolderChooser.showOpenDialog(rootPane);
        if (returnSmpFolderChooser == JFileChooser.APPROVE_OPTION) {
            smpFolder = smpFolderChooser.getSelectedFile();
            String smpFolderLocation = smpFolder.getAbsolutePath();
            smpFolderTextField.setText(smpFolderLocation);
            DefaultListModel smpListModel = new DefaultListModel();
            for (File smpFile : smpFolder.listFiles()) {
                smpListModel.addElement(smpFile);
            }
            smpFilesList.setModel(smpListModel);
        }
    }

    private void smpFilesListValueChanged(javax.swing.event.ListSelectionEvent evt) {
        // TODO add your handling code here:
        uploadButton.setEnabled(!smpFilesList.isSelectionEmpty());
    }

    private void uploadButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        try {
            List<File> smpFiles = new ArrayList<File>();
            int[] selectedIndexes = smpFilesList.getSelectedIndices();
            ListModel smpListModel = smpFilesList.getModel();
            for (int i = 0; i < selectedIndexes.length; i++) {
                File smpFile = (File) smpListModel.getElementAt(selectedIndexes[i]);
                logger.info(smpFile.getAbsolutePath());
                smpFiles.add(smpFile);
            }
            // SMP upload workflow...
            String smpServerUri = smpServerTextField.getText();
            String smpUsername = smpUsernameTextField.getText();
            char[] smpPasswordArray = smpPasswordPasswordField.getPassword();
            String smpPassword = new String(smpPasswordArray);
            SMPConnection smpConnection = new SMPConnection(smpServerUri, smpUsername, smpPassword);
            SMP smp = new SMP(smpConnection);
            SMPParticipantInformation smpParticipantInformation = smp.uploadSMPFiles(smpFiles);

            JOptionPane.showMessageDialog(this, createScrollablePane("Upload successfully made. Check your SMP information at: \n\n" +
                    smpParticipantInformation), "Info", JOptionPane.INFORMATION_MESSAGE);
            smpFilesList.clearSelection();
        } catch (Exception e) {
            String stackTrace = GenericUtils.printExceptionStackTrace(e);
            JOptionPane.showMessageDialog(this, createScrollablePane(stackTrace), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void smpServerTextFieldActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private JScrollPane createScrollablePane(String text) {
        JTextArea jta = new JTextArea(text);
        JScrollPane jsp = new JScrollPane(jta) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(680, 320);
            }
        };
        return jsp;
    }

    private void enableTransformButton() {
        boolean enabled = (this.tslFileTextField.getText() != null && !this.tslFileTextField.getText().isEmpty()) &&
                (this.ismFileTextField.getText() != null && !this.ismFileTextField.getText().isEmpty()) &&
                (this.outputFolderTextField.getText() != null && !this.outputFolderTextField.getText().isEmpty());
        transformButton.setEnabled(enabled);
    }
    // End of variables declaration                   
}
