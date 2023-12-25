/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package captortest;

/**
 *
 * @author ayush
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import jpcap.*;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class CaptorGUI {

//Global
    NetworkInterface[] NETWORK_INT;
    JpcapCaptor CAP;
    CaptureThread CAPTHREAD;
    int INDEX=0;
    int COUNTER=0;
    boolean CaptureState=false;
    

    //Gui Global
    JFrame MainWindow = new JFrame("---Packet Sniffer--- ");
    public static JTextArea TA_OUTPUT = new JTextArea();
    JScrollPane SP_OUTPUT = new JScrollPane();
    JButton B_CAPTURE = new JButton("CAPTURE");
    JButton B_STOP = new JButton("STOP");
    JButton B_SELECT = new JButton("SELECT");
    JButton B_LIST = new JButton("LIST");
    JButton B_EXIT = new JButton("EXIT");

    JTextField TF_SelectInterface = new JTextField();

//-----------
    
    public static void main(String args[]){
        new CaptorGUI();
    }
//---------    
    public CaptorGUI(){
        BuildGUI();
        DisableButtons();
    }
//-----------
    public void BuildGUI(){
        MainWindow.setSize(800,480);
        MainWindow.setLocation(200,200);
        MainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        MainWindow.getContentPane().setLayout(null);
        
        //----------------------
        
        TA_OUTPUT.setEditable(false);
        TA_OUTPUT.setFont(new Font("Monospaced",0,12));
        TA_OUTPUT.setForeground(new Color(0,0,153));
        TA_OUTPUT.setLineWrap(true);
        
        SP_OUTPUT.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        SP_OUTPUT.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        SP_OUTPUT.setViewportView(TA_OUTPUT);
        
        MainWindow.getContentPane().add(SP_OUTPUT);
        SP_OUTPUT.setBounds(10,16,740,290);
        
        //----------------------------------------------------
        
        B_CAPTURE.setBackground(Color.red);
        B_CAPTURE.setForeground(Color.white);
        B_CAPTURE.setMargin(new Insets(0,0,0,0));
        B_CAPTURE.addActionListener(new ActionListener()
        { 
            public void actionPerformed(ActionEvent X){
                Action_B_CAPTURE(X);
            }
        });
        MainWindow.getContentPane().add(B_CAPTURE);
        B_CAPTURE.setBounds(10,310,130,25);
        
        //----------------------
        
        B_STOP.setBackground(Color.black);
        B_STOP.setForeground(Color.white);
        B_STOP.setMargin(new Insets(0,0,0,0));
        B_STOP.addActionListener(new ActionListener()
        { 
            public void actionPerformed(ActionEvent X){
                Action_B_STOP(X);
            }
        });
        MainWindow.getContentPane().add(B_STOP);
        B_STOP.setBounds(145,310,110,25);
        
        //---------------------
        
        B_SELECT.setBackground(Color.black);
        B_SELECT.setForeground(Color.white);
        B_SELECT.setMargin(new Insets(0,0,0,0));
        B_SELECT.addActionListener(new ActionListener()
        { 
            public void actionPerformed(ActionEvent X){
                Action_B_SELECT(X);
            }
        });
        MainWindow.getContentPane().add(B_SELECT);
        B_SELECT.setBounds(0,388,75,20);
        
        //------------------------
        
        B_LIST.setBackground(Color.black);
        B_LIST.setForeground(Color.white);
        B_LIST.setMargin(new Insets(0,0,0,0));
        B_LIST.addActionListener(new ActionListener()
        { 
            public void actionPerformed(ActionEvent X){
                Action_B_LIST(X);
            }
        });
        MainWindow.getContentPane().add(B_LIST);
        B_LIST.setBounds(0,410,75,20);
        
        //---------------------
        
        B_EXIT.setBackground(Color.black);
        B_EXIT.setForeground(Color.white);
        B_EXIT.setMargin(new Insets(0,0,0,0));
        B_EXIT.addActionListener(new ActionListener()
        { 
            public void actionPerformed(ActionEvent X){
                Action_B_EXIT(X);
            }
        });
        MainWindow.getContentPane().add(B_EXIT);
        B_EXIT.setBounds(180,400,75,25);
        
        //---------------------
        TF_SelectInterface.setForeground(Color.red);
        TF_SelectInterface.setHorizontalAlignment(JTextField.CENTER);
        MainWindow.getContentPane().add(TF_SelectInterface);
        TF_SelectInterface.setBounds(3,364,70,20);
        
        //----------------------------
        
        MainWindow.setVisible(true);
    }

//-----------------------------------------    
    public void Action_B_CAPTURE(ActionEvent X){
        TA_OUTPUT.setText("");
        CaptureState = true;
        CapturePackets();
        
    }
    public void Action_B_STOP(ActionEvent X){
        CaptureState = false;
        CAPTHREAD.finished();
        
    }
    public void Action_B_SELECT(ActionEvent X){
        ChooseInterface();
    }
    public void Action_B_LIST(ActionEvent X){
        ListNetworkInterfaces();
        B_SELECT.setEnabled(true);
        TF_SelectInterface.requestFocus();
    }
    public void Action_B_EXIT(ActionEvent X){
        MainWindow.setVisible(false);
        MainWindow.dispose();
    }
    public void DisableButtons(){
        B_CAPTURE.setEnabled(false);
        B_STOP.setEnabled(false);
        B_SELECT.setEnabled(false);

    }
    public void EnableButtons(){
        B_CAPTURE.setEnabled(true);
        B_STOP.setEnabled(true);
        B_SELECT.setEnabled(true);
    }
//---------------------------------------
    
    public void CapturePackets(){
        CAPTHREAD = new CaptureThread() {
            @Override
            public Object construct() {
                TA_OUTPUT.setText("\n NOW CAPTURING on INTERFACE "+INDEX+"......"+"\n--------------------------------"+"---------------\n\n");
                
                try{
                    CAP=JpcapCaptor.openDevice(NETWORK_INT[INDEX],65535, false, 20);
                    while(CaptureState){
                        
                        
                        CAP.processPacket(1,new PacketContents());
                    }
                    CAP.close();
                }
                catch(Exception X){
                    System.out.println("X");
                }
                return 0;
            }
            
            public void finished(){
                this.interrupt();
            }
        };
        CAPTHREAD.start();
    }
    
    
//---------------------------------------
    
    public void ListNetworkInterfaces(){
        
        //array of interfaces
        NETWORK_INT = JpcapCaptor.getDeviceList();
        TA_OUTPUT.setText("");
        
        for (int i = 0; i < NETWORK_INT.length; i++) {
            TA_OUTPUT.append(
            "\n\n------------------------------- Interface "+ i + " INFO -----------------------------------------");
            TA_OUTPUT.append("\nInterface Number: "+ i);
            TA_OUTPUT.append("\nDescription: " + NETWORK_INT[i].name + "(" + NETWORK_INT[i].description + ")");
            TA_OUTPUT.append("\nDatalink Name: " + NETWORK_INT[i].datalink_name + "(" + NETWORK_INT[i].datalink_description + ")");
            
            TA_OUTPUT.append("\nMac Address : ");
            for(byte X : NETWORK_INT[i].mac_address){
                TA_OUTPUT.append(Integer.toHexString(X & 0xff) + ":");
            }
            
            for(NetworkInterfaceAddress INTF : NETWORK_INT[i].addresses){
                TA_OUTPUT.append("\nIP Address: " + INTF.address);
                TA_OUTPUT.append("\nSubnet Mask: " + INTF.subnet);
                TA_OUTPUT.append("\nBroadcast Address: " + INTF.broadcast);
                
            }
            COUNTER++;
        }
        
    }
//-------------------------
    public void ChooseInterface(){
        int TEMP = Integer.parseInt(TF_SelectInterface.getText());
        if (TEMP > -1 && TEMP < COUNTER){
            INDEX=TEMP;
            EnableButtons();
        }else{
            JOptionPane.showMessageDialog(null,"Outside Range interface");
        }
        TF_SelectInterface.setText("");
    }
//--------------------------------
    
    
    
}
