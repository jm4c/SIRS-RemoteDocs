package sirs.remotedocs.gui;


import sirs.remotedocs.ImplementationClient;
import types.Document_t;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;

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
    private JButton settingsButton;
    private JLabel ownDocumentsLabel;
    private JLabel sharedDocumentsLabel;
    private GUIClient formManager;

    public ClientBoxForm(ImplementationClient client, GUIClient formManager){
        this.formManager = formManager;
        setContentPane(mainPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();


        try {
            //get shared documents
            System.out.println("Check if client's bin has new shared docs");
            client.getSharedDocuments();
        } catch (RemoteException e) {
            e.printStackTrace();
        }



        System.out.println(client.getClientBox().getDocumentsIDSet().toString());
        ownDocumentsLabel.setText(client.getClientUsername() + "\'s documents");
        ownDocsList.setListData(getStringArrayFromCollection(client.getClientBox().getDocumentsIDSet()));

        sharedDocumentsLabel.setText("Shared documents with " +client.getClientUsername());
        sharedDocsList.setListData(getStringArrayFromCollection(client.getClientBox().getSharedDocumentsIDSet()));





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


            } catch (Exception e1) {
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

        logoutButton.addActionListener(e -> {
            client.logout();
            formManager.backToLogin();
            dispose();
        });
    }

    public static void main(String[] args) throws Exception {
        ImplementationClient client = new ImplementationClient();
        client.login("Hello","helloworld");
        ClientBoxForm form = new ClientBoxForm(client, null);
        form.setVisible(true);


    }



}
