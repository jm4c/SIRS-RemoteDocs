package sirs.remotedocs.gui;


import sirs.remotedocs.ImplementationClient;
import types.Document_t;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;

import static utils.MiscUtils.getStringArrayFromCollection;


public class ClientBoxForm extends  JFrame{
    private JPanel mainPanel;
    private JList<String> ownDocsList;
    private JList<String> sharedDocsList;
    private JPanel buttonsPanel;
    private JButton newButton;
    private JButton openButton;
    private JButton deleteButton;
    private JButton logoutButton;
    private JButton settingsButton;
    private JScrollPane ownListScrollPane;
    private JScrollPane sharedListScrollPane;
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
        ownDocsList.setName(client.getClientUsername() + "\'s documents");
        ownDocsList.setListData(getStringArrayFromCollection(client.getClientBox().getDocumentsIDSet()));

        sharedDocsList.setName("Shared documents with " +client.getClientUsername());
        sharedDocsList.setListData(getStringArrayFromCollection(client.getClientBox().getSharedDocumentsIDSet()));

        openButton.setEnabled(false);
        deleteButton.setEnabled(false);

        newButton.addActionListener((ActionEvent e) -> {
            openButton.setEnabled(false);
            deleteButton.setEnabled(false);
            String title = (String)JOptionPane.showInputDialog(
                    this,
                    "Document title:",
                    "New document",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "");
            if (title == null)
                return;
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
                    if (title == null)
                        return;
                    document = client.createDocument(title);
                }
                ownDocsList.setListData(getStringArrayFromCollection(client.getClientBox().getDocumentsIDSet()));
                DocumentForm documentForm = formManager.openDocument(document, false);


            } catch (Exception e1) {
                e1.printStackTrace();
            }

        });



        openButton.addActionListener(e -> {
            if(ownDocsList.isSelectionEmpty() && sharedDocsList.isSelectionEmpty()) {
                openButton.setEnabled(false);
                deleteButton.setEnabled(false);
                return;
            }
            Document_t document;
            if(!ownDocsList.isSelectionEmpty()){
                document = client.downloadDocument(
                        ownDocsList.getSelectedValue(),
                        client.getClientUsername(),
                        client.getClientBox().getDocumentKey(ownDocsList.getSelectedValue()));
                formManager.openDocument(document, false);
            }else{
                document = client.downloadDocument(
                        sharedDocsList.getSelectedValue(),
                        client.getClientBox().getSharedDocumentInfo(sharedDocsList.getSelectedValue()).getOwner(),
                        client.getClientBox().getSharedDocumentKey(sharedDocsList.getSelectedValue()));
                formManager.openDocument(document, true);
            }

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

        ownDocsList.addListSelectionListener(e->{
            openButton.setEnabled(true);
            deleteButton.setEnabled(true);
            sharedDocsList.clearSelection();

        });

        sharedDocsList.addListSelectionListener(e -> {
            openButton.setEnabled(true);
            deleteButton.setEnabled(false);
            ownDocsList.clearSelection();
        });

    }

    public static void main(String[] args) throws Exception {
        ImplementationClient client = new ImplementationClient();
        client.login("Hello","helloworld");
        ClientBoxForm form = new ClientBoxForm(client, null);
        form.setVisible(true);


    }



}
