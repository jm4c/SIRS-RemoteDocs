package sirs.remotedocs.gui;

import sirs.remotedocs.ImplementationClient;
import types.Document_t;

import javax.swing.*;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import static utils.MiscUtils.getDateFormatted;

public class DocumentForm extends JFrame{
    private Document_t document;
    private final ImplementationClient client;
    private final GUIClient formManager;
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

    public DocumentForm(Document_t document, ImplementationClient client, GUIClient formManager, boolean isSharedDocument) {
        this.document = document;
        this.client = client;
        this.formManager = formManager;
        setContentPane(mainPanel);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        pack();
        setVisible(true);


        //get info from document
        this.setTitle(document.getDocID());
        textFieldTitle.setText(document.getDocID());
        textFieldOwner.setText(document.getOwner());
        textFieldTimestamp.setText(getDateFormatted(document.getTimestamp()));
        textFieldLastEditor.setText(document.getLastEditor());

        textAreaContent.setText(document.getContent());
        shareButton.setEnabled(!isSharedDocument);

        saveButton.addActionListener(e -> {
            try {
                document.setContent(textAreaContent.getText(),client.getClientBox().getOwnerID(), client.getClientBox().getPrivateKey());
                textFieldTimestamp.setText(getDateFormatted(document.getTimestamp()));
                textFieldLastEditor.setText(document.getLastEditor());


            } catch (IOException | NoSuchAlgorithmException | SignatureException | InvalidKeyException e1) {
                e1.printStackTrace();
            }

            client.uploadDocument(document, isSharedDocument);
        });
        closeButton.addActionListener(e -> {
            dispose();
        });
        shareButton.addActionListener(e -> {
            ShareForm shareForm = new ShareForm(document, client);
            shareForm.setVisible(true);

        });
    }


}
