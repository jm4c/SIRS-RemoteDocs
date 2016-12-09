package sirs.remotedocs.gui;


import exceptions.DocumentIntegrityCompromisedException;
import sirs.remotedocs.ImplementationClient;
import types.Document_t;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.rmi.RemoteException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import static utils.MiscUtils.getStringArrayFromCollection;


public class ClientBoxForm extends JFrame{
    private JPanel mainPanel;
    private JList<String> ownDocsList;
    private JList<String> sharedDocsList;
    private JPanel buttonsPanel;
    private JButton newButton;
    private JButton openButton;
    private JButton deleteButton;
    private JButton logoutButton;
    private JScrollPane ownListScrollPane;
    private JScrollPane sharedListScrollPane;
    private JLabel ownDocumentsLabel;
    private JLabel sharedDocumentsLabel;

    public ClientBoxForm(ImplementationClient client, GUIClient formManager){
        setContentPane(mainPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        pack();

        sharedDocumentsLabel.setText("Shared documents with " +client.getUsername());

        getSharedDocuments(client);

        javax.swing.Timer timer = new javax.swing.Timer(10000, e -> getSharedDocuments(client));
        timer.start();


        System.out.println(client.getClientBox().getDocumentsIDSet().toString());
        ownDocumentsLabel.setText(client.getUsername() + "\'s documents");
        ownDocsList.setListData(getStringArrayFromCollection(client.getClientBox().getDocumentsIDSet()));


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
            Boolean isShared = false;
            try {
                if(!ownDocsList.isSelectionEmpty()){
                    isShared = false;
                    document = client.downloadDocument(
                            ownDocsList.getSelectedValue(),
                            client.getUsername(),
                            client.getClientBox().getDocumentKey(ownDocsList.getSelectedValue()));
                }else{
                    isShared = true;
                    document = client.downloadDocument(
                            sharedDocsList.getSelectedValue(),
                            client.getClientBox().getSharedDocumentInfo(sharedDocsList.getSelectedValue()).getOwner(),
                            client.getClientBox().getSharedDocumentKey(sharedDocsList.getSelectedValue()));
                }
                formManager.openDocument(document, isShared);
            } catch (IOException | NoSuchAlgorithmException | ClassNotFoundException ex) {
                ex.printStackTrace();
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog (null,
                        "This document ("+ sharedDocsList.getSelectedValue() + ") was removed by the owner." +
                                "Removing " + sharedDocsList.getSelectedValue() + " from shared documents list." ,
                        "Revoked key",
                        JOptionPane.WARNING_MESSAGE);
                client.getClientBox().removeSharedDocument(sharedDocsList.getSelectedValue());
                getSharedDocuments(client);

            } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException ex) {
                ex.printStackTrace();
                if(isShared){
                    System.out.println("DocKey was revoked. Removing document from list.");
                    JOptionPane.showMessageDialog (null,
                            "Your permission to write " + sharedDocsList.getSelectedValue() + " was revoked\n" +
                                    "Removing " + sharedDocsList.getSelectedValue() + " from shared documents list." ,
                            "Revoked key",
                            JOptionPane.WARNING_MESSAGE);
                    client.getClientBox().removeSharedDocument(sharedDocsList.getSelectedValue());
                    getSharedDocuments(client);

                }
            } catch (SignatureException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        sharedDocsList.getSelectedValue() + "'s signature is not valid.\n" +
                                "Not allowed user could have edited the content.",
                        "Invalid Signature",
                        JOptionPane.WARNING_MESSAGE);
            } catch (DocumentIntegrityCompromisedException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        sharedDocsList.getSelectedValue() + "'s hash is not valid.\n" +
                                "Not allowed user could have tampered the content.",
                        "Document Integrity Compromised",
                        JOptionPane.WARNING_MESSAGE);
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

    private void getSharedDocuments(ImplementationClient client) {
        try {
            System.out.println("Check if client's bin has new shared docs");
            client.getSharedDocuments();
            sharedDocsList.setListData(getStringArrayFromCollection(client.getClientBox().getSharedDocumentsIDSet()));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        ImplementationClient client = new ImplementationClient();
        client.login("Hello","helloworld");
        ClientBoxForm form = new ClientBoxForm(client, null);
        form.setVisible(true);


    }



}
