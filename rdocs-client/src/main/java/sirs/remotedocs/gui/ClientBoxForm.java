package sirs.remotedocs.gui;


import sirs.remotedocs.ClientImplementation;
import types.Document_t;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static utils.MiscUtils.getStringArrayFromCollection;


public class ClientBoxForm extends  JFrame{
    private JPanel mainPanel;
    private JPanel ownDocumentsPanel;
    private JPanel sharedDocumentsPanel;
    private JList<String> ownDocsList;
    private JList sharedDocsList;
    private JPanel buttonsPanel;
    private JButton newButton;
    private JButton openButton;
    private JButton deleteButton;
    private JButton logoutButton;
    private JLabel ownDocumentsLabel;
    private FormManager formManager;

    public ClientBoxForm(ClientImplementation client, FormManager formManager){
        this.formManager = formManager;
        setContentPane(mainPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        System.out.println(client.getClientBox().getDocumentsIDSet().toString());
        ownDocumentsLabel.setText(client.getClientUsername() + "\'s documents");
        ownDocsList.setListData(getStringArrayFromCollection(client.getClientBox().getDocumentsIDSet()));






        newButton.addActionListener((ActionEvent e) -> {
            String title = (String)JOptionPane.showInputDialog(
                    this,
                    "Document title:",
                    "New document",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "");
            try {
                Document_t document = client.createDocument(title);
                while(document==null){
                    title = (String)JOptionPane.showInputDialog(
                            this,
                            "Document title:",
                            "New document",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            "This title already exists.");
                    document = client.createDocument(title);
                }
                ownDocsList.setListData(getStringArrayFromCollection(client.getClientBox().getDocumentsIDSet()));
                DocumentForm documentForm = formManager.openDocument(document);


            } catch (NoSuchAlgorithmException | IOException e1) {
                e1.printStackTrace();
            }

        });
        openButton.addActionListener(e -> {
            System.out.println(ownDocsList.getSelectedValue());
            Document_t document = client.downloadDocument(ownDocsList.getSelectedValue(), client.getClientUsername());

            formManager.openDocument(document);
        });
        deleteButton.addActionListener(e -> {
            client.removeDocument(ownDocsList.getSelectedValue());
            ownDocsList.setListData(getStringArrayFromCollection(client.getClientBox().getDocumentsIDSet()));
        });
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.logout();
                formManager.backToLogin();
                dispose();
            }
        });
    }

    public static void main(String[] args) throws Exception {
        ClientImplementation client = new ClientImplementation();
        client.login("Hello","helloworld");
        ClientBoxForm form = new ClientBoxForm(client, null);
        form.setVisible(true);


    }



}
