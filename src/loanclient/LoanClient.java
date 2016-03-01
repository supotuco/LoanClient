/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loanclient;

/**
 *
 * @author Diego
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LoanClient extends JFrame{
    
    JLabel jlbRate = new JLabel("Annual Interest Rate");
    JLabel jlbYears = new JLabel("Number Of Years");
    JLabel jlbAmount = new JLabel("Loan Amount");
    JTextArea jtaRate = new JTextArea();
    JTextArea jtaYears = new JTextArea();
    JTextArea jtaAmount = new JTextArea();
    JButton jbtSubmit = new JButton("Submit");
    JTextArea jtaOut = new JTextArea();
    DataInputStream fromServer;
    DataOutputStream toServer;
    JFrame errFrame;
    
    public LoanClient(){
        
        errFrame = new JFrame();
        
        errFrame.setSize( 300, 300);
        errFrame.setLocation(300, 300);
        errFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        errFrame.setVisible(false);
        
        errFrame.add(new JLabel("Input Error"));
        
        JPanel jlbPanel = new JPanel();
        jlbPanel.setLayout(new GridLayout(3, 1, 5, 5));
        jlbPanel.add(jlbRate);
        jlbPanel.add(jlbYears);
        jlbPanel.add(jlbAmount);
        
        JPanel jtaPanel = new JPanel();
        jtaPanel.setLayout(new GridLayout(3, 1, 5, 5));
        jtaPanel.add(jtaRate);
        jtaPanel.add(jtaYears);
        jtaPanel.add(jtaAmount);
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout( 1, 3, 5, 5) );
        topPanel.add( jlbPanel);
        topPanel.add( jtaPanel);
        topPanel.add(jbtSubmit);
        
        setLayout(new BorderLayout());
        add( topPanel, BorderLayout.NORTH);
        add( new JScrollPane(jtaOut), BorderLayout.CENTER );
        
        setTitle("Loan Client");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        
        jbtSubmit.addActionListener(new SubmitListener());
        
        try{
            Socket socket = new Socket("localhost", 8000);
            
            fromServer = new DataInputStream( socket.getInputStream() );
            toServer = new DataOutputStream( socket.getOutputStream() );
            
        }catch(IOException ex){
            System.err.println(ex + "\n");
        }
        
    }
    
    private class SubmitListener implements ActionListener{
        
        @Override
        public void actionPerformed(ActionEvent e){
            try{
                double rate = Double.parseDouble(jtaRate.getText().trim());
                int years = Integer.parseInt(jtaYears.getText().trim());
                double amount = Double.parseDouble(jtaAmount.getText().trim());
                
                toServer.writeDouble(rate);
                toServer.writeInt(years);
                toServer.writeDouble(amount);
                
                double monthlyPayment = fromServer.readDouble();
                double totalPayment = fromServer.readDouble();
                
                jtaOut.append("Annual Interest Rate: " + rate + "\n");
                jtaOut.append("Number Of Years: " + years + "\n");
                jtaOut.append("Loan Amount: " + amount + "\n");
                jtaOut.append("Monthly Payment: " + monthlyPayment + "\n");
                jtaOut.append("Total Payment: " + totalPayment + "\n");
                
            }catch(NumberFormatException nbEx){
                errFrame.setVisible(true);
            }catch(IOException ioEx){
                
            }
        }
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        for(int i = 0; i < 3; i = i + 1){
            new Thread(new Runnable(){
                @Override
                public void run(){
                    new LoanClient();
                }
            }).start();
        }
    }
    
}
