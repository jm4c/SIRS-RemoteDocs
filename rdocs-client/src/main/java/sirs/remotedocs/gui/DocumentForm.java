package sirs.remotedocs.gui;

import sirs.remotedocs.ClientImplementation;
import types.Document_t;

import javax.swing.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static utils.MiscUtils.getDateFormatted;

public class DocumentForm extends JFrame{
    private Document_t document;
    private final ClientImplementation client;
    private final FormManager formManager;
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

    public DocumentForm(Document_t document, ClientImplementation client, FormManager formManager) {
        this.document = document;
        this.client = client;
        this.formManager = formManager;
        setContentPane(mainPanel);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);


        //get info from document
        textFieldTitle.setText(document.getDocID());
        textFieldOwner.setText(document.getOwner());
        textFieldTimestamp.setText(getDateFormatted(document.getTimestamp()));

        //TODO last editor
        textFieldLastEditor.setText(document.getOwner());

        textAreaContent.setText(document.getContent());
        saveButton.addActionListener(e -> {
            try {
                document.setContent(textAreaContent.getText());
                textFieldTimestamp.setText(getDateFormatted(document.getTimestamp()));
            } catch (IOException | NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }
            client.uploadDocument(document);
        });
        closeButton.addActionListener(e -> {
            dispose();
        });
    }

}
