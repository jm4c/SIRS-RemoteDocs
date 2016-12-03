package sirs.remotedocs.gui;


import sirs.remotedocs.ClientImplementation;

import javax.swing.*;

public class FormManager {

    public static void main(String[] args) {
        ClientImplementation client = new ClientImplementation();
        LoginForm loginForm = new LoginForm(client);
        loginForm.setVisible(true);
    }

    public static void switchForms(JFrame from, JFrame to){
        from.setVisible(false);
        to.setVisible(true);
    }

    public static DocumentListForm startDocsList(ClientImplementation client){
        DocumentListForm documentListForm = new DocumentListForm(client);
        return documentListForm;
    }

}
