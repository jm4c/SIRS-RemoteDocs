package sirs.remotedocs.gui;


import sirs.remotedocs.ImplementationClient;
import types.Document_t;

import javax.swing.*;

public class GUIClient {
    private ImplementationClient client;
    private LoginForm loginForm;
    private ClientBoxForm clientBoxForm;
    private DocumentForm documentForm;

    public GUIClient() {
        client= new ImplementationClient();
        loginForm = new LoginForm(client, this);
        loginForm.setVisible(true);
    }


    public static void switchForms(JFrame from, JFrame to){
        from.setVisible(false);
        to.setVisible(true);
    }

    public ClientBoxForm openClientBox(){
        clientBoxForm = new ClientBoxForm(client, this);
        return clientBoxForm;
    }

    public DocumentForm openDocument(Document_t document){
        documentForm = new DocumentForm(document, client, this);
        return documentForm;
    }

    public LoginForm backToLogin(){
        loginForm = new LoginForm(client, this);
        switchForms(clientBoxForm, loginForm);
        return loginForm;
    }



    public static void main(String[] args) {
        GUIClient formManager = new GUIClient();
    }

}
