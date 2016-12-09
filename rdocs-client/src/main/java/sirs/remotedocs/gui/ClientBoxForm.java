package sirs.remotedocs.gui;


import sirs.remotedocs.ImplementationClient;
import types.Document_t;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.rmi.RemoteException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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

    public ClientBoxForm(ImplementationClient client, GUIClient formManager){
        setContentPane(mainPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setTitle("SIRS Remote Docs - " + client.getUsername());
        pack();

        sharedListScrollPane.setBorder(new TitledBorder("Shared documents"));


        getSharedDocuments(client);

        javax.swing.Timer timer = new javax.swing.Timer(10000, e -> getSharedDocuments(client));
        timer.start();


        System.out.println(client.getClientBox().getDocumentsIDSet().toString());
        ownListScrollPane.setBorder(new TitledBorder(client.getUsername() + "\'s documents"));
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
            Boolean isShared;
            String docID;
            String docOwner;
            SecretKey docKey;

            if(!ownDocsList.isSelectionEmpty()){
                isShared = false;
                docID = ownDocsList.getSelectedValue();
                docOwner = client.getUsername();
                docKey = client.getClientBox().getDocumentKey(docID);

            }else{
                isShared = true;
                docID = sharedDocsList.getSelectedValue();
                docOwner = client.getClientBox().getSharedDocumentInfo(docID).getOwner();
                docKey = client.getClientBox().getSharedDocumentKey(docID);
            }
            try {
                Document_t document = client.downloadDocument(docID, docOwner, docKey);
                if(document.hasIntegrityFaultFlag())
                    JOptionPane.showMessageDialog(null,
                            docID + "'s hash is not valid.\n" +
                                    "Not allowed user could have tampered the content.",
                            "Document Integrity Compromised",
                            JOptionPane.WARNING_MESSAGE);

                if(document.hasSignatureFaultFlag())
                    JOptionPane.showMessageDialog(null,
                            docID + "'s signature is not valid.\n" +
                                    "Not allowed user could have edited the content.",
                            "Invalid Signature",
                            JOptionPane.WARNING_MESSAGE);

                formManager.openDocument(document, isShared);
            } catch (IOException | NoSuchAlgorithmException | ClassNotFoundException ex) {
                ex.printStackTrace();
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
                if (isShared) {
                    JOptionPane.showMessageDialog (this,
                            "This document ("+ docID + ") was removed by the owner.\n " +
                                    "Removing " + docID + " from shared documents list." ,
                            "Revoked key",
                            JOptionPane.WARNING_MESSAGE);
                    removeSharedDocument(client, docID);
                }

            } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException ex) {
                ex.printStackTrace();
                if(isShared){
                    JOptionPane.showMessageDialog (this,
                            "Your permission to write " + sharedDocsList.getSelectedValue() + " was revoked. \n" +
                                    "Removing " + sharedDocsList.getSelectedValue() + " from shared documents list." ,
                            "Revoked key",
                            JOptionPane.WARNING_MESSAGE);
                    removeSharedDocument(client, sharedDocsList.getSelectedValue());

                }
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

    void removeSharedDocument(ImplementationClient client, String docID) {
        client.getClientBox().removeSharedDocument(docID);
        getSharedDocuments(client);
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
