package sirs.remotedocs.gui;

import sirs.remotedocs.ImplementationClient;

import javax.swing.*;

public class LoginForm extends JFrame {


    private JPanel loginMainPanel;
    private JButton btn_login;
    private JButton btn_register;
    private JTextField tf_username;
    private JPasswordField tf_password;
    private JButton btn_exit;
    private ImplementationClient client;


    public LoginForm(ImplementationClient inputClient, GUIClient formManager){
        client = inputClient;
        setContentPane(loginMainPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle("Login");
        pack();



        btn_login.addActionListener(e -> {
            try {

                switch (client.login(tf_username.getText(), new String(tf_password.getPassword()))){
                    case 0:
                        GUIClient.switchForms(this, formManager.openClientBox());
                        dispose();
                        break;
                    case 1:
                        JOptionPane.showMessageDialog(null,"Wrong password.");
                        break;
                    case 2:
                        JOptionPane.showMessageDialog(null,"Username does not exist.");
                        break;
                    case -1:
                        JOptionPane.showMessageDialog(null,"Server is offline. Click here to try to reconnect.");
                        client.connectToServer();
                        break;

                    default:
                        JOptionPane.showMessageDialog(null,"Unknown Exception.");

                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }


        });

        btn_register.addActionListener(e -> {
            try {
                JTextField username = new JTextField();
                JTextField password = new JPasswordField();
                JTextField repeatPassword = new JPasswordField();
                Object[] message = {
                        "Username:", username,
                        "Password:", password,
                        "Repeat Password:", repeatPassword
                };

                int option = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    if(!password.getText().equals(repeatPassword.getText())){
                        JOptionPane.showMessageDialog(null, "Passwords don't match.");
                    }else{
                        switch (client.register(username.getText(), password.getText())){
                            case 0:
                                JOptionPane.showMessageDialog(null,username.getText() + " successfully registered.");
                                break;
                            case 1:
                                JOptionPane.showMessageDialog(null,username.getText() + " already exists.");
                                break;
                            case 2:
                                JOptionPane.showMessageDialog(null,"Username must be between 4 and 20 characters long.");
                                break;
                            case 3:
                                JOptionPane.showMessageDialog(null,"Password must be between 8 and 64 characters long.");
                                break;
                            case -1:
                                JOptionPane.showMessageDialog(null,"Server is offline. Click OK to try to reconnect.");
                                client = new ImplementationClient();
                                break;

                            default:
                                JOptionPane.showMessageDialog(null,"Unknown Exception.");

                        }
                    }

                }


            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        btn_exit.addActionListener(e -> LoginForm.super.dispose());

    }

    public static void main(String[] args) {
        ImplementationClient client = new ImplementationClient();
        LoginForm form = new LoginForm(client, null);
        form.setVisible(true);

    }

}
