/*
* Name: Carlos Gonzalez
* Date:9/28/2015
* CSE 473: Introduction to Computer Networks
* Washington University in St. Louis
* Lab 2
*
* Inputs: (Optional) An Ip Address to be used by the listening socket.
*         (Optional) A port number to be used instead of
*                    the default 31357.
* Outputs: None.
*
* MapServer creates a TCP Server, which allows the user to input both
* the IP address and the port number to be used when other
* applications bind to its socket. If no port number is selected,
* a default port number of 31357 is used. Similarly, if no Ip address
* is specified, then a wildcar adress is used. The server itself 
* stores (key,value) pairs, which can be modified by the
* clients accessing the server.
* There are four commands that the server can interpret:
*
* get(k):  Returns the value part of the pair whose key is k. 
* A get command should be formatted as
* get:this is the key string
*
* put(k,v): Adds the pair (k,v) to the set, possibly replacing
* some other pair (k,x). A put command's format is
* put:another key string:and the corresponding  value
*
* swap(k1,k2): Swaps the values stored with the two keys. 
* If either k1 or k2 is not found then the operation does nothing.
* A swap command should
* be formatted as
* swap:key string 1:key string 2
*
* remove(k): Deletes the pair (k,v) from the server.
* A remove command should be formatted as
* remove:this is the key string
*
* get all(): Requests all of the key-value pairs stored in the map.
* All pairs are returned on a single line, with each key-value 
* separated by a pair of colons. A get all command
* should be formatted as:
* get all
*/

import java.io.*;
import java.net.*;
import java.util.*;

public class TcpMapServer {
    
    //The HashMap container that will be used to store keys and values.
    private static HashMap<String, String> hmap =
            new HashMap<String, String>();
    
    public static void main(String args[]) throws Exception {
        String stringOutput = ""; // The returning data in String type
        byte[] bytesOutput; // The returning data in a bytes array.
        
        //The default port number and IP address.
        int portNum = 31357;
        InetAddress serverAdr = null;
        
        if (args.length == 1) {
            //The Ip Address on which to bind the socket if specified.
            serverAdr = InetAddress.getByName(args[0]);
        }
        if (args.length == 2) {
            //The Ip Address and port number on which to bind the
            //socket if specified.
            serverAdr = InetAddress.getByName(args[0]);
            portNum = Integer.parseInt(args[1]);
        }
        
        //Create the listen socket and the buffer.
        ServerSocket listenSock =
                new ServerSocket(portNum, 0, serverAdr);
        byte[] buf = new byte[1000];
        
        while (true) {
            
            //Wait for the connection request, 
            //then create the connection socket
            Socket connSock = listenSock.accept();
            
            //Create buffered versions of socket's input in/out streams
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            connSock.getInputStream(), "US-ASCII"));
            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(
                            connSock.getOutputStream(), "US-ASCII"));
            String rawData = in.readLine(); // Read the socket input.
            while (rawData != null) {
                
                stringOutput = "";
                String[] splitData = rawData.split(":");
                
                //Using a switch statement identify the 
                //command and execute it.
                switch (splitData[0]) {
                    
                    // If the get command is given in the correct format,
                    // get the value of the given key.
                    case "get":
                        if (splitData.length < 2) {
                            stringOutput =
                                    "error: unrecognizable input: " +
                                    rawData;
                        } else if (hmap.containsKey((splitData[1]))) {
                            stringOutput = "success:" + hmap.
                                    get((splitData[1]));
                        } else {
                            stringOutput = "no match";
                        }
                        break;
                        
                    // If the put command is given in the 
                    //correct format, store the (key,value) pair.
                    case "put":
                        if (splitData.length < 3) {
                            stringOutput =
                                    "error: unrecognizable input: " +
                                    rawData;
                        } else {
                            if (hmap.containsKey((splitData[1]))) {
                                stringOutput = "updated:" +
                                        splitData[1];
                            } else {
                                stringOutput = "success";
                            }
                            hmap.put((splitData[1]), splitData[2]);
                        }
                        break;
                        
                    // If the remove command is given 
                    //in the correct format, remove the 
                    //(key,value) pair.
                    case "remove":
                        if (splitData.length < 2) {
                            stringOutput =
                                    "error: unrecognizable input: " +
                                    rawData;
                        } else if (hmap.containsKey((splitData[1]))) {
                            hmap.remove((splitData[1]));
                            stringOutput = "success";
                        } else {
                            stringOutput = "no match";
                        }
                        break;
                        
                    // If the swap command is given in the correct
                    //format, swap the values of the two given keys.
                    case "swap":
                        if (splitData.length < 3) {
                            stringOutput =
                                    "error: unrecognizable input: " +
                                    rawData;
                        } else {
                            if (hmap.containsKey((splitData[1])) &&
                                    hmap.containsKey((splitData[2]))) {
                                String t = hmap.get((splitData[1]));
                                hmap.put((splitData[1]), hmap.
                                        get((splitData[2])));
                                hmap.put((splitData[2]), t);
                                stringOutput = "success";
                            } else {
                                stringOutput = "no match";
                            }
                        }
                        break;
                        
                    // If the get all command is given in the 
                    //correct format, get all the <key,value> pairs.
                    case "get all":
                        if (splitData.length > 1) {
                            stringOutput =
                                    "error: unrecognizable input: " +
                                    rawData;
                        } else {
                            
                            for (Map.Entry<String, String> entry
                                    : hmap.
                                            entrySet()) {
                                String key = entry.getKey();
                                String value = entry.getValue();
                                stringOutput += "key:" + key + "::" +
                                        value + " ";
                            }
                        }
                        break;
                        
                    // If the input is not identified as 
                    //a valid command, display an error.
                    default:
                        stringOutput =
                                "error: unrecognizable input: " +
                                rawData;
                }
                
                out.write(stringOutput); //Return the server response.
                out.newLine();//Return a new line for delimeter.
                out.flush(); //Flush the stream.
                
                rawData = in.readLine();//Read the next command.
            }
            connSock.close();//Close the socket if null stream.
        }
    }
}
