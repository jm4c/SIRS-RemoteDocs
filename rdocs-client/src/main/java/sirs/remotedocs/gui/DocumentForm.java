package sirs.remotedocs.gui;

import sirs.remotedocs.ClientImplementation;
import types.Document_t;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static utils.MiscUtils.getStringArrayFromCollection;

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
        //TODO timestamp
        //TODO last editor
        textAreaContent.setText(document.getContent());
        saveButton.addActionListener(e -> {
            document.setContent(textAreaContent.getText());
            client.uploadDocument(document);
        });
        closeButton.addActionListener(e -> {
            dispose();
        });
    }

}
