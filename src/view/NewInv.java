/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

/**
 *
 * @author Eman
 */
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class NewInv extends JDialog {

    private JTextField customerNameField;
    private JTextField inDateF;
    private JLabel customerNameL;
    private JLabel invDateL;
    private JButton okButton;
    private JButton canselButton;

    public NewInv(InvoiceGeneratorFrame frame) {
        customerNameL = new JLabel("Customer Name : ");
        customerNameField = new JTextField(20);
        invDateL = new JLabel("Invoice Date : ");
        inDateF = new JTextField(20);
        okButton = new JButton("Ok");
        canselButton = new JButton("Cancel");

        okButton.setActionCommand("createOk");
        canselButton.setActionCommand("createCancel");

        okButton.addActionListener(frame.getListener());
        canselButton.addActionListener(frame.getListener());

        setLayout(new GridLayout(3, 2));
        setSize(200, 200);
        setLocation(200, 300);

        add(customerNameL);
        add(customerNameField);
        add(invDateL);
        add(inDateF);
        add(okButton);
        add(canselButton);

        pack();

    }

    public JTextField getCustomerNameField() {
        return customerNameField;
    }

    public JTextField getInvDateField() {
        return inDateF;
    }

}
