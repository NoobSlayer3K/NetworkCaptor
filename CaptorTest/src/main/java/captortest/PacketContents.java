/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package captortest;

/**
 *
 * @author ayush
 */
import jpcap.PacketReceiver;
import jpcap.packet.Packet;

public class PacketContents implements PacketReceiver {
    
    public void receivePacket(Packet packet){
        CaptorGUI.TA_OUTPUT.append(packet.toString()+"\n-------------------------"+"---------------------------\n\n");
    }
    
}
