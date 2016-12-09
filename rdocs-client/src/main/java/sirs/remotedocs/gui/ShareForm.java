package sirs.remotedocs.gui;


import sirs.remotedocs.ImplementationClient;
import types.Document_t;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class ShareForm extends JFrame{
    private ListTransferHandler arrayListHandler =
            new ListTransferHandler();

    private JList notAllowedUsers;
    private JList allowedUsers;
    private Set<String> oldNotAllowedUsers;
    private Set<String> oldAllowedUsers;


    public ShareForm(Document_t document, ImplementationClient client){
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setupLists(document, client);
        getContentPane().add(getContent(document, client));
        setTitle("SIRS Remote Docs - Permissions - " + client.getUsername());
        setSize(400,200);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private JPanel getContent(Document_t document, ImplementationClient client) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel listsPanel = new JPanel(new GridLayout(1,0));
        JPanel buttonsPanel = new JPanel(new FlowLayout());


        listsPanel.add(getListComponent(notAllowedUsers, "Do not allow"));
        listsPanel.add(getListComponent(allowedUsers, "Allow"));

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);

        mainPanel.add(listsPanel);
        mainPanel.add(buttonsPanel);

        saveButton.addActionListener(actionEvent -> savePermissions(document, client));
        cancelButton.addActionListener(e -> {
            dispose();
        });

        return mainPanel;
    }

    private void savePermissions(Document_t document, ImplementationClient client) {
        System.out.println("\n------------STARTING SAVE BUTTON------------------");

        ArrayList<String> notAllowedUsers = convertJList2ArrayList(this.notAllowedUsers);
        ArrayList<String> allowedUsers = convertJList2ArrayList(this.allowedUsers);

        System.out.println("All not allowed users: " + notAllowedUsers.toString());
        System.out.println("New not allowed users: " + allowedUsers.toString());
        System.out.println("\n------------------------------");

        System.out.println("All allowed users: " + notAllowedUsers.toString());

        //remove old elements from list
        notAllowedUsers.removeAll(oldNotAllowedUsers);

        // if there are new elements in not allowed list
        if(!notAllowedUsers.isEmpty()){
            //remove users from doc info permissions
            notAllowedUsers.forEach(user ->{
                client.getClientBox().getDocumentInfo(document.getDocID()).removePermission(user);
            });

            //reset document key
            client.changeDocumentKey(document);

            // send doc info to all users with permission in list (due to new doc key)
            allowedUsers.forEach(user->{
                shareDocInfo(user, document, client);
            });
        }else{
            // send doc info only to new clients with permission
            allowedUsers.removeAll(oldAllowedUsers);
            System.out.println("New allowed users: " + allowedUsers.toString());
            allowedUsers.forEach(user->{
                shareDocInfo(user, document, client);
            });
            System.out.println("------------END SAVE BUTTON------------------\n");
        }

        client.uploadDocument(document, !allowedUsers.isEmpty());
        dispose();
    }

    private void shareDocInfo(String user, Document_t document, ImplementationClient client){
        try {
            client.shareDocument(document.getDocID(), user);
        } catch ( NoSuchPaddingException | SignatureException | IOException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }


    private ArrayList<String> convertJList2ArrayList(JList list) {

        ArrayList output = new ArrayList();

        // Get number of items in the list
        int size = list.getModel().getSize();

        // Get all item objects
        for (int i = 0; i < size; i++) {
            output.add(list.getModel().getElementAt(i));
        }

        return output;
    }

    private void setupLists(Document_t document, ImplementationClient client) {
        //converting from vector to ArrayList
        String[] registeredUsers = client.getRegisteredUsers();
        Set<String> notAllowedUsersSet = new HashSet<>();
        Collections.addAll(notAllowedUsersSet, registeredUsers);

        //remove owner from permissions table
        notAllowedUsersSet.remove(client.getUsername());

        Set<String> allowedUsersSet = client.getClientBox().getDocumentInfo(document.getDocID()).getPermissions().keySet();

        //removing users with permission from list
        notAllowedUsersSet.removeAll(allowedUsersSet);


        System.out.println("\n------------STARTING SETUP LISTS------------------");
        System.out.println("Registered Users: " + registeredUsers.length);
        System.out.println("Allowed Users: " + allowedUsersSet.toString());
        System.out.println("Not Allowed Users: " + notAllowedUsersSet.toString());
        System.out.println("------------END SETUP LISTS------------------\n");


        //Saving list before drag-and-drop operations
        oldNotAllowedUsers = notAllowedUsersSet;
        oldAllowedUsers = allowedUsersSet;

        //converting to JList
        DefaultListModel modelNoPermission = new DefaultListModel();
        notAllowedUsersSet.forEach(modelNoPermission::addElement);
        notAllowedUsers = new JList(modelNoPermission);


        DefaultListModel modelWithPermission= new DefaultListModel();
        allowedUsersSet.forEach(modelWithPermission::addElement);
        allowedUsers= new JList(modelWithPermission);


    }


    private JScrollPane getListComponent(JList list, String borderTitle){
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setTransferHandler(arrayListHandler);
        list.setDragEnabled(true);

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(new TitledBorder(borderTitle));
        return scrollPane;
    }



    public static void main(String[] args) {
        ShareForm f = new ShareForm(null, null);
        f.setVisible(true);
    }
}