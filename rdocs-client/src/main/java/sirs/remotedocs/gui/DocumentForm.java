package sirs.remotedocs.gui;

import sirs.remotedocs.ImplementationClient;
import types.Document_t;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import static utils.MiscUtils.getDateFormatted;

public class DocumentForm extends JFrame{
    private JTextArea textAreaContent;
    private JPanel mainPanel;
    private JTextField textFieldTitle;
    private JTextField textFieldOwner;
    private JTextField textFieldTimestamp;
    private JTextField textFieldLastEditor;
    private JButton shareButton;
    private JButton closeButton;
    private JButton saveButton;
    private JPanel PanelButtons;

    public DocumentForm(Document_t document, ImplementationClient client, ClientBoxForm clientBoxForm, boolean isSharedDocument) {
        setContentPane(mainPanel);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        pack();
        setVisible(true);


        //get info from document
        this.setTitle("Editing " + document.getDocID() + " - " + client.getUsername());
        textFieldTitle.setText(document.getDocID());
        textFieldOwner.setText(document.getOwner());
        textFieldTimestamp.setText(getDateFormatted(document.getTimestamp()));
        textFieldLastEditor.setText(document.getLastEditor());

        textAreaContent.setText(document.getContent());
        shareButton.setEnabled(!isSharedDocument);

        //check document flags
        if(document.hasSignatureFaultFlag()){
            textFieldLastEditor.setBackground(Color.RED);
            textFieldLastEditor.setText("???");
        }

        if (document.hasIntegrityFaultFlag()) {
            textAreaContent.setBackground(Color.RED);
        }

        saveButton.addActionListener(e -> {
            try {
                document.setContent(textAreaContent.getText(),client.getClientBox().getOwnerID(), client.getClientBox().getPrivateKey());
                textFieldTimestamp.setText(getDateFormatted(document.getTimestamp()));
                textFieldLastEditor.setText(document.getLastEditor());
                textFieldLastEditor.setBackground(null);


            } catch (IOException | NoSuchAlgorithmException | SignatureException | InvalidKeyException e1) {
                e1.printStackTrace();
            }

            if(!client.uploadDocument(document, isSharedDocument) && isSharedDocument){
                JOptionPane.showMessageDialog (this,
                        "Your permission to write in " + document.getDocID() + " was revoked. \n" +
                                "Removing " + document.getDocID() + " from shared documents list." ,
                        "Revoked key",
                        JOptionPane.WARNING_MESSAGE);
                clientBoxForm.removeSharedDocument(client, document.getDocID());
                dispose();
            }
        });
        closeButton.addActionListener(e -> dispose());
        shareButton.addActionListener(e -> {
            ShareForm shareForm = new ShareForm(document, client);
            shareForm.setLocationRelativeTo(this);
            shareForm.setVisible(true);

        });

        textAreaContent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                textAreaContent.setBackground(Color.WHITE);
            }
        });
    }


}
