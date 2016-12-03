package sirs.remotedocs.gui;

import sirs.remotedocs.ClientImplementation;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginForm extends JFrame {


    private JPanel loginMainPanel;
    private JButton btn_login;
    private JButton btn_register;
    private JTextField tf_username;
    private JPasswordField tf_password;
    private JButton btn_exit;
    private JTextArea ta_status;
    private FormManager formManager;



    public LoginForm(ClientImplementation client, FormManager formManager){
        this.formManager = formManager;
        setContentPane(loginMainPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        pack();

        if(!client.isConnected()){
            ta_status.setText("Server is offline. Click here to try to reconnect.");
        }


        btn_login.addActionListener(e -> {
            try {

                switch (client.login(tf_username.getText(), new String(tf_password.getPassword()))){
                    case 0:
                        FormManager.switchForms(this, formManager.openClientBox());
                        dispose();
                        break;
                    case 1:
                        ta_status.setText("Wrong password.");
                        break;
                    case 2:
                        ta_status.setText("Username does not exist.");
                        break;
                    case -1:
                        ta_status.setText("Server is offline. Click here to try to reconnect.");
                        client.connectToServer();
                        break;

                    default:
                        ta_status.setText("Unknown Exception.");

                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }


        });

        btn_register.addActionListener(e -> {
            try {

                switch (client.register(tf_username.getText(), new String(tf_password.getPassword()))){
                    case 0:
                        ta_status.setText(tf_username.getText() + " successfully registered.");
                        break;
                    case 1:
                        ta_status.setText(tf_username.getText() + " already exists.");
                        break;
                    case 2:
                        ta_status.setText("Username must be between 4 and 20 characters long.");
                        break;
                    case 3:
                        ta_status.setText("Password must be between 8 and 64 characters long.");
                        break;
                    case -1:
                        ta_status.setText("Server is offline. Click here to try to reconnect.");
                        client.connectToServer();
                        break;

                    default:
                        ta_status.setText("Unknown Exception.");

                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        btn_exit.addActionListener(e -> LoginForm.super.dispose());

        ta_status.addMouseListener(new MouseAdapter() {
            private int counter = 0;
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(!client.isConnected()){
                    try {
                        client.connectToServer();

                    } catch (Exception e1) {
                        e1.printStackTrace();
                        counter++;
                        ta_status.setText("Server is offline. Click here to try to reconnect. (" + counter+")");
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        ClientImplementation client = new ClientImplementation();
        LoginForm form = new LoginForm(client, null);

    }

}
