/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.Date;
import view.InvoiceGeneratorFrame;

/**
 *
 * @author Adela
 */
public class InvoiceHeader {
    private int num;
    private String name;
    private Date date;
    private ArrayList<InvoiceLine> lines;

    public InvoiceHeader(int num, String name, Date date) {
        this.num = num;
        this.name = name;
        this.date = date;
    }

 
    public double getTotal()
    {
        double total = 0;
        
        for (InvoiceLine line : getLines())
        {
            total += line.getTotal();
        }
        return total;
    }
    
    
    public ArrayList<InvoiceLine> getLines()
    {
        if(lines == null)
        {
            lines = new ArrayList<>();
        }
        return lines;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "InvoiceHeader{" + "num=" + num + ", name=" + name + ", date=" + date + '}';
    }
    
     public String getAsCSV() {
        return num + "," + InvoiceGeneratorFrame.sdf.format(date) + "," + name;
    }
}
