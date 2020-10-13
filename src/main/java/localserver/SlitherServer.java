package localserver;



import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.net.InetSocketAddress;

import de.mat2095.my_slither.*;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 * Creates a slither.io websocket server.
 *
 * DO NOT INITIALIZE IN THE MAIN THREAD! OR YOU WILL CREATE DEADLOCKS!
 * @author Tomás Silva
 */

public class SlitherServer extends WebSocketServer implements SlitherServerProtocol {

    public SlitherServer(InetSocketAddress address) {
        super(address);
    }

    static {
        try {
            messageTypes.put('c', SlitherServer.class.getMethod("preInitRequest", int[].class));
            messageTypes.put('s', SlitherServer.class.getMethod("addRemoveSnake", int[].class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    // Note: decodeSecret() is pointless in local servers thus no verification is done
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("[SERVER] Client connected to the server");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("[DEBUG] Connection to the client closed.");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        conn.send("[SERVER] Roger Roger.");
        System.out.println(message);
    }

    /**
     * Runs when the client sends a message to the server.
     *
     * This methods processes all client and server messages. Check code comments for more details.
     * @param conn
     * @param message
     */
    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        byte[] cMessage = message.array(); // Decodes the header into an array to be able to access each byte header individually

        // Checks if the message is empty.
        if (cMessage.length == 0) { System.out.println("[SERVER] No Data received."); return; }

        int[] data = new int[cMessage.length];

        //Converts cMessage into an int array to simplify overall code
        for(int i = 0; i < cMessage.length; i++) {
            data[i] = cMessage[i] & 0xFF;
        }

        if (cMessage.length == 1) {

            // Checks if packet's data is ping
            if (data[0] == 251) {
                System.out.println("[DEBUG] Server received ping data");
                dataPingPong(data);

                return;
            } else if (data[0] == 253) {
                //Data Boost Start
            } else if (data[0] == 254) {
                //Data Boost Stop
            }



            // Checks for the first packet sent by the client
            // If true: Sends the initial setup. (Note: the pre-init response is skipped)
            try {
                System.out.println("[DEBUG] Server received " + (char) cMessage[0] + " at data length == 1.");
                byte[] messageToClient = (byte[]) messageTypes.get((char) cMessage[0]).invoke(this, data);

                conn.send(messageToClient);

            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
                System.out.println("[ERROR]");
                for(int by : data) {
                    System.out.println(by);
                }
            }


        } else {

            // Checks for pre-init response and answers with a pre init setup
            // Note: This is compulsory to have as the client does not send the s packet until after completing this phase.
            if (cMessage.length == 24 && (char) cMessage[0] == 'i') {
                System.out.println("[DEBUG] Server received preInitResponse");
                conn.send(createInitSetup(data));
                return;
            }

            try {
                System.out.println("[DEBUG] Server received " + (char) cMessage[0]+ " at data length > 0");
                byte[] messageToClient = (byte[]) messageTypes.get((char) cMessage[0]).invoke(this, data);

                if (messageToClient.length > 0) {
                    conn.send(messageToClient);
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public void onStart() {
        System.out.println("[Server] Server started successfully!");
        System.out.println("[Server] Generating Sectors AND Food");
        createSectors();
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.println(ex);
    }
}

