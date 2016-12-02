package sirs.remotedocs.gui;

import sirs.remotedocs.ClientImplementation;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {


    private JPanel loginMainPanel;
    private JButton btn_login;
    private JButton btn_register;
    private JTextField tf_username;
    private JPasswordField tf_password;
    private JButton btn_exit;
    private JTextArea ta_status;



    public LoginForm(ClientImplementation client){
        setContentPane(loginMainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);

        btn_login.addActionListener(e -> {
            try {

                switch (client.login(tf_username.getText(), new String(tf_password.getPassword()))){
                    case 0:
                        //TODO open new form
                        break;
                    case 1:
                        ta_status.setText("Wrong password.");
                        break;
                    case 2:
                        ta_status.setText("Username does not exist.");
                        break;
                    case -1:
                        ta_status.setText("Server is offline.");
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
                        ta_status.setText("Server is offline.");
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
    }

    public static void main(String[] args) {
        ClientImplementation client = new ClientImplementation();
        LoginForm form = new LoginForm(client);

    }

}
