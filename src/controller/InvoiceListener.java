/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import model.HeaderTableModel;
import model.InvoiceHeader;
import model.InvoiceLine;
import model.ItemTableModel;
import view.InvoiceGeneratorFrame;
import view.NewInv;
import view.NewLine;

/**
 *
 * @author Adela
 */
public class InvoiceListener implements ActionListener , ListSelectionListener{

    private InvoiceGeneratorFrame frame;
    private NewInv headerDialog;
    private NewLine itemDialog;
    
    public InvoiceListener(InvoiceGeneratorFrame frame) {
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        switch (actionCommand)
        {
            case "Load File":
                loadFile(null,null);
                break;
            case "Save File":
                saveFile();
                break;
            case "Create Invoice":
                createInvoice();
                break;
            case "Delete Invoice":
                deleteInvoice();
                break;
            case "Create Item":
                createItem();
                break;
            case "Delete Item":
                deleteItem();
                break;
            case "createOk":
                createOk();
                break;
            case "createCancel":
                createCancel();
                break;
            case "createItemOk":
                createItemOk();
                break;
            case "createItemCancel":
                createItemCancel();
                break;
        }
    }

    public void loadFile(String headerPath, String linePath) {
        File headerFile = null;
        File lineFile = null;
        
        if (headerPath == null && linePath == null)
        {
            JOptionPane.showMessageDialog(frame, "Select header file, then select line file","Invoice files", JOptionPane.WARNING_MESSAGE);
            JFileChooser fc = new JFileChooser();
            int result = fc.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION )
            {
                headerFile = fc.getSelectedFile();
                result = fc.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION)
                {
                    lineFile = fc.getSelectedFile();
                }
            }
        }else
        {
            headerFile = new File(headerPath);
            lineFile = new File(linePath);
        }
        
        if (headerFile != null && lineFile != null)
        {
                try {
                    List<String> headerLines = Files.lines(Paths.get(headerFile.getAbsolutePath())).collect(Collectors.toList());
                    
                    List<String> lineLines = Files.lines(Paths.get(lineFile.getAbsolutePath())).collect(Collectors.toList());
                    frame.getInvoices().clear();
                    for (String headerLine : headerLines)
                    {
                        String[] parts = headerLine.split(",");
                        String numString = parts[0];
                        String dateString = parts[1];
                        String name = parts[2];
                        int num = Integer.parseInt(numString);
                        Date date = frame.sdf.parse(dateString);
                        InvoiceHeader inv = new InvoiceHeader(num, name, date);
                        frame.getInvoices().add(inv);
                    }
                    for (String lineLine : lineLines)
                    {
                        String[] parts = lineLine.split(",");
                        int num = Integer.parseInt(parts[0]);
                        String name = parts[1];
                        double price = Double.parseDouble(parts[2]);
                        int count = Integer.parseInt(parts[3]);
                        InvoiceHeader inv = frame.getInvoiceByNum(num);
                        InvoiceLine line = new InvoiceLine(name, price, count, inv);
                        inv.getLines().add(line);
                    }
                    frame.setHeaderTableModel(new HeaderTableModel(frame.getInvoices()));
                    //frame.getInvoicesTable().setModel(new HeaderTableModel());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
        }

            
    }

    private void saveFile() {
        JFileChooser fc = new JFileChooser();
        File headerFile = null;
        File lineFile = null;
        int result = fc.showSaveDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            headerFile = fc.getSelectedFile();
            result = fc.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                lineFile = fc.getSelectedFile();
            }
        }
        
        if (headerFile != null && lineFile != null) {
            String headerData = "";
            String lineData = "";
            for (InvoiceHeader inv : frame.getInvoices()) {
                headerData += inv.getAsCSV();
                headerData += "\n";
                for (InvoiceLine line : inv.getLines()) {
                    lineData += line.getAsCSV();
                    lineData += "\n";
                }
            }
            try {
                FileWriter headerFW = new FileWriter(headerFile);
                FileWriter lineFW = new FileWriter(lineFile);
                headerFW.write(headerData);
                lineFW.write(lineData);
                headerFW.flush();
                lineFW.flush();
                headerFW.close();
                lineFW.close();
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error while writing file(s)", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        }
    }

    private void createInvoice() {
        headerDialog = new NewInv(frame);
        headerDialog.setLocation(300, 300);
        headerDialog.setVisible(true);
    }

    private void deleteInvoice() {

        int selectedRow = frame.getInvoicesTable().getSelectedRow();
        if (selectedRow > -1) {
            frame.getInvoices().remove(selectedRow);
            frame.getHeaderTableModel().fireTableDataChanged();
        }
    }
    
    private void createOk() {
        String name = headerDialog.getCustomerNameField().getText();
        String dateStr = headerDialog.getInvDateField().getText();
        createCancel();
        try {
            Date date = frame.sdf.parse(dateStr);
            InvoiceHeader inv = new InvoiceHeader(frame.getNextInvNum(), name, date);
            frame.getInvoices().add(inv);
            frame.getHeaderTableModel().fireTableDataChanged();
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid Format", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createCancel() {
        headerDialog.setVisible(false);
        headerDialog.dispose();
        headerDialog = null;
    }

    private void createItem() {
        if (frame.getInvoicesTable().getSelectedRow() > -1) {
            itemDialog = new NewLine(frame);
            itemDialog.setLocation(300, 300);
            itemDialog.setVisible(true);
        }
    }

    private void deleteItem() {
        int selectedInvoice = frame.getInvoicesTable().getSelectedRow();
        int selectedItem = frame.getItemsTable().getSelectedRow();

        if (selectedInvoice > -1 && selectedItem > -1) {
            frame.getInvoices().get(selectedInvoice).getLines().remove(selectedItem);
            frame.getLineTableModel().fireTableDataChanged();
            frame.getHeaderTableModel().fireTableDataChanged();
            frame.getInvoicesTable().setRowSelectionInterval(selectedInvoice, selectedInvoice);
        }
    }

    private void createItemOk() {
        String name = itemDialog.getItemNameTF().getText();
        String countStr = itemDialog.getItemCountTF().getText();
        String priceStr = itemDialog.getItemPriceTF().getText();
        createItemCancel();
        try {
            int count = Integer.parseInt(countStr);
            double price = Double.parseDouble(priceStr);
            int currentInv = frame.getInvoicesTable().getSelectedRow();
            InvoiceHeader inv = frame.getInvoices().get(currentInv);
            InvoiceLine line = new InvoiceLine(name, price, count, inv);
            inv.getLines().add(line);
            frame.getHeaderTableModel().fireTableDataChanged();
            frame.getInvoicesTable().setRowSelectionInterval(currentInv, currentInv);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid Number Format", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createItemCancel() {
        itemDialog.setVisible(false);
        itemDialog.dispose();
        itemDialog = null;
    }


    @Override
    public void valueChanged(ListSelectionEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        int selectedRow = frame.getInvoicesTable().getSelectedRow();
        if (selectedRow > -1) {
            InvoiceHeader inv = frame.getInvoices().get(selectedRow);
            frame.getInvoiceNumLbl().setText("" + inv.getNum());
            frame.getInvoiceDateLbl().setText(InvoiceGeneratorFrame.sdf.format(inv.getDate()));
            frame.getCustmerNameLbl().setText(inv.getName());
            frame.getInvoiceTotalLbl().setText("" + inv.getTotal());
            ArrayList<InvoiceLine> lines = inv.getLines();
            frame.setItemTableModel(new ItemTableModel(lines));
        } else {
            frame.getInvoiceNumLbl().setText("");
            frame.getInvoiceDateLbl().setText("");
            frame.getCustmerNameLbl().setText("");
            frame.getInvoiceTotalLbl().setText("");
            frame.setItemTableModel(new ItemTableModel());
        }
    }
    
    
}
