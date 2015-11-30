/*
* Name: Carlos Gonzalez
* Date:9/28/2015
* CSE 473: Introduction to Computer Networks
* Washington University in St. Louis
* Lab 2
* Inputs: The IP address of the server on which to establish the
* TCP connection.
*         (Optional) The port number of the socket to bind to,
*          default is 31357.
* Output: The response of the server to which the packet was sent to.
*
* MapClient allows the user to establish a TCP connection to any 
* TCP server, although for the purposes of this lab that server should 
* be TcpMapServer. MapClient requires one initial input: the address
* of the server to be connected to. After this, the TCP connection has
* been established and the user can begin to input data to the server. 
* To close the TCP connection, the user should hit "Enter"
* so that nothing is sent to the TCP server.
* No error checking for TcpMapServer is done by TcpMapClient.
*/

import java.io.*;
import java.net.*;

public class TcpMapClient {
    
    public static void main(String args[]) throws Exception
    {
        //The default port number and IP address.
        int portNum = 31357;
        InetAddress serverAdr = InetAddress.getByName(args[0]);
        
        //If specified, establish a custom port to connect to.
        if(args.length == 2)
            portNum = Integer.parseInt(args[1]);
        
        
        //Open socket and connect to server.
        Socket sock = new Socket(args[0], portNum);
        
        //Create buffered reader and writer for a socket's in/out streams
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        sock.getInputStream(), "US-ASCII"));
        BufferedWriter out = new BufferedWriter(
                new OutputStreamWriter(
                        sock.getOutputStream(),"US-ASCII"));
        
        //Create a buffered reader to read the user's input.
        BufferedReader sysin = new BufferedReader(
                new InputStreamReader(System.in));
        
        //Read the users input and send it through the TCP socket.
        String line = sysin.readLine();
        while( !line.equals("")){
            
            out.write(line); // Send the input through the TCP socket.
            out.newLine();//Send new line to delimeter.
            out.flush(); //Flush the stream.
            
            //Read the resposnse from the server and print it.
            System.out.println(in.readLine());
            line = sysin.readLine();//Read the next line.
        }
        sock.close();//Close the socket once the user gives no more inputs.
    }
}
