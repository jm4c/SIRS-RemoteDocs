package sirs.remotedocs.gui;


import sirs.remotedocs.ImplementationClient;
import types.Document_t;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class ShareForm extends JFrame{
    private ListTransferHandler arrayListHandler =
            new ListTransferHandler();

    private JList usersWithoutPermission;
    private JList usersWithPermission;

    public ShareForm(Document_t document, ImplementationClient client){
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setupLists(document, client);
        getContentPane().add(getContent());
        setSize(400,200);
        setLocationRelativeTo(null);
    }

    private JPanel getContent() {
        JPanel panel = new JPanel(new GridLayout(2,2));
        JButton saveButton = new JButton();
        JButton cancelButton = new JButton();
        saveButton.setText("Save");
        cancelButton.setText("Cancel");
        panel.add(getListComponent("testR"));
        panel.add(getListComponent("testL"));

//        panel.add(getListComponent(usersWithoutPermission));
//        panel.add(getListComponent(usersWithPermission));
        panel.add(saveButton);
        panel.add(cancelButton);

//        saveButton.addActionListener();

        return panel;
    }

    private void setupLists(Document_t document, ImplementationClient client) {
        //converting from vector to ArrayList
        String[] allRegisteredUsers = client.getRegisteredUsers();
        Set<String> usersWithoutPermissionSet = new HashSet<>();
        Collections.addAll(usersWithoutPermissionSet, allRegisteredUsers);

        Set<String> usersWithPermissionSet = client.getClientBox().getDocumentInfo(document.getDocID()).getPermissions().keySet();

        //removing users with permission from list
        usersWithoutPermissionSet.forEach(user -> {
            if(usersWithPermissionSet.contains(user))
                usersWithoutPermissionSet.remove(user);
        });



        DefaultListModel modelNoPermission = new DefaultListModel();
        usersWithoutPermissionSet.forEach(user ->{
            modelNoPermission.addElement(user);
        });
        usersWithoutPermission = new JList(modelNoPermission);
        usersWithoutPermission.setName("No permission");

        DefaultListModel modelWithPermission= new DefaultListModel();
        usersWithPermissionSet.forEach(user ->{
            modelWithPermission.addElement(user);
        });
        usersWithPermission= new JList(modelWithPermission);
        usersWithPermission.setName("RW");

    }


    private JScrollPane getListComponent(JList list){
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setTransferHandler(arrayListHandler);
        list.setDragEnabled(true);
        return new JScrollPane(list);
    }

    private JScrollPane getListComponent(String s) {
        DefaultListModel model = new DefaultListModel();
        for(int j = 0; j < 5; j++)
            model.addElement(s + " " + (j+1));
        JList list = new JList(model);
        list.setName(s);
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setTransferHandler(arrayListHandler);
        list.setDragEnabled(true);
        return new JScrollPane(list);
    }




    public static void main(String[] args) {
        ShareForm f = new ShareForm(null, null);
        f.setVisible(true);
    }
}