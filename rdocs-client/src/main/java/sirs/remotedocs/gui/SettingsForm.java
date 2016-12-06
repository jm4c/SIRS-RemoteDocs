package sirs.remotedocs.gui;

import javax.swing.*;

/**
 * Created by joaod on 05-Dec-16.
 */
public class SettingsForm extends JFrame{
    private JCheckBox trustThisDeviceCheckBox;
    private JPanel settingsPanel;
    private JButton changePasswordButton;

    public SettingsForm(){
        setContentPane(settingsPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();


    }

    public static void main(String[] args) {
        SettingsForm form = new SettingsForm();
        form.setVisible(true);
    }
}
