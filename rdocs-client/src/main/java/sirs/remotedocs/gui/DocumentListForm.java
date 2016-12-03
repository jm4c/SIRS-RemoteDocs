package sirs.remotedocs.gui;


import sirs.remotedocs.ClientImplementation;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static utils.MiscUtils.getStringArrayFromCollection;


public class DocumentListForm extends  JFrame{
    private JPanel mainPanel;
    private JPanel ownDocumentsPanel;
    private JPanel sharedDocumentsPanel;
    private JList<String> ownDocsList;
    private JList sharedDocsList;
    private JPanel buttonsPanel;
    private JButton newButton;
    private JButton selectButton;
    private JButton deleteButton;
    private JButton logoutButton;
    private JLabel ownDocumentsLabel;

    public DocumentListForm(ClientImplementation client){
        setContentPane(mainPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        System.out.println(client.getClientBox().getDocumentsIDSet().toString());
        ownDocumentsLabel.setText(client.getClientUsername() + "\'s documents");
        ownDocsList.setListData(getStringArrayFromCollection(client.getClientBox().getDocumentsIDSet()));






        newButton.addActionListener(e -> {
            String title = (String)JOptionPane.showInputDialog(
                    this,
                    "Document title:",
                    "New document",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "");
            try {
                while(client.createDocument(title)==null){
                    title = (String)JOptionPane.showInputDialog(
                            this,
                            "Document title:",
                            "New document",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            "This title already exists.");
                }
                ownDocsList.setListData(getStringArrayFromCollection(client.getClientBox().getDocumentsIDSet()));

                //TODO open document form

            } catch (NoSuchAlgorithmException | IOException e1) {
                e1.printStackTrace();
            }

        });
        selectButton.addActionListener(e -> {
            System.out.println(ownDocsList.getSelectedValue());
            client.downloadDocument(ownDocsList.getSelectedValue(), client.getClientUsername());

            //TODO open document form
        });
        deleteButton.addActionListener(e -> {
            client.removeDocument(ownDocsList.getSelectedValue());
            ownDocsList.setListData(getStringArrayFromCollection(client.getClientBox().getDocumentsIDSet()));
        });
    }

    public static void main(String[] args) throws Exception {
        ClientImplementation client = new ClientImplementation();
        client.login("Hello","helloworld");
        DocumentListForm form = new DocumentListForm(client);
        form.setVisible(true);


    }



}
