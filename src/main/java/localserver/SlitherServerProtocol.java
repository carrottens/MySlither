package localserver;

import com.sun.jmx.remote.internal.ArrayQueue;
import de.mat2095.my_slither.SnakeBodyPart;
import de.mat2095.my_slither.Food;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ArrayList;

/**
 * Slither.io Protocol -> https://github.com/ClitherProject/Slither.io-Protocol/blob/master/Protocol.md
 * The protocol was specifically designed for servers, so do not use it to replace the current client.
 * For more details, please read the provided rep. It explains clearly all of the packets.
 */
interface SlitherServerProtocol {
    int GAME_RADIUS = 21600;
    int MSCPS = 411;
    int SECTOR_SIZE = 300;
    int SECTOR_COUNT_ALONG_EDGE = 144;
    double SPANGDV = 4.8;
    double NSP1 =  5.49;
    double NSP2 = 0.4;
    double NSP3 = 14;
    double MAMU = 0.033;
    double MANU2 = 0.028;
    double CST = 0.43;
    int PROTOCOL_VERSION = 11;

    int[] secret = secret();

    /**
     * Creates the secret message
     *
     */
    static int[] secret() {
       String secret = "IneedanotherstorySomethingtogetoffmychestMylifegetskindaboringNeedsomethingthatIcanconfess'TilallmysleevesarestainedredFromallthetruththatI'vesaidComebyithonestlyIswearThoughtyousawmewink,noI'vebeenonthebrink,soTellmewhatyouwanttohearSomethingthatwilllightthoseearsSickofalltheinsincereSoI'mgonnagiveallmysecretsawayThistimedon'tneedanotherperfectlieDon'tcareifcriticseverjumpinlineI'mgonnagiveallmysecretsawayMyGod,amazinghowwegotthisfarIt'slikewe'rechasingallthosestarsWho'sdrivingshinybigblackcarsAndeverydayIseethenewsAlltheproblemsthatwecouldsolveAndwhenasituationrisesJustwriteitintoanalbumSenditstraighttogoldButIdon'treallylikemyflow,no,soTellmewhatyouwanttohearSomethingthatwilllightthoseearsSickofalltheinsincereSoI'mgonnagiveallmysecretsawayThistime,don'tneedanotherperfectlieDon'tcareifcriticseverjumpinlineI'mgonnagiveallmysecretsawayOh,gotnoreason,gotnoshameGotnofamilyIcanblameJustdon'tletmedisappearI'matellyoueverythingSotellmewhatyouwanttohearSomethingthatwilllightthoseearsSickofalltheinsincereSoI'mgonnagiveallmysecretsawayThistime,don'tneedanotherperfectlieDon'tcareifcriticseverjumpinlineI'mgonnagiveallmysecretsawaySotellmewhatyouwanttohearSomethingthatwilllightthoseearsSickofalltheinsincereSoI'mgonnagiveallmysecretsawayThistime,don'tneedanotherperfectlieDon'tcareifcriticseverjumpinlineI'mgonnagiveallmysecretsawayAllmysecretsaway,allmysecretsaway";

       int[] coded = new int[secret.length()];

       for(int i = 0; i < secret.length(); i++) {
           coded[i] = (int) secret.charAt(i);
       }

       return coded;
    }

    /**
     * Map of all message types and their corresponding functions.
     *
     */
    Map<Character, Method> messageTypes = new HashMap<>();

    ArrayList<Snake> clients = new ArrayList<>();
    ArrayList<Food> foods = new ArrayList<>();
    Sector[][] sectors = new Sector[Sector.IN_ROW][Sector.IN_ROW];

    /**
     * Create Sectors
     *
     * Creates all sectors.
     * Note: The following code was based on <a href= "https://github.com/ClitherProject/Slither.io-Protocol/issues/55 ">issue </a>.
     */
    default void createSectors() {
        for(int r = 0; r < sectors.length; r++) {
            for(int c = 0; c < sectors.length; c++ ) {
                sectors[r][c] = new Sector(r, c);
            }
        }
    }


    /**
     * Message type: 6
     * Originates: packet c
     *
     * Pre-init response
     *
     * Creates a not very secret message that the client needs to decode.
     */
    default byte[] preInitRequest(int [] data) {

        byte[] extraSecretMessage = new byte[secret.length + 3];
        extraSecretMessage[2] = (byte) '6';

        for(int i = 0; i < secret.length; i++) {
            extraSecretMessage[3 + i] = (byte) secret[i];
        }
        System.out.println("[DEBUG] Server sent preInitRequest");
        return extraSecretMessage;
    }

    /**
     * Message type: p
     * Origin: 251
     * Responds to a client's ping with a pong.
     */
    default byte[] dataPingPong(int[] data) {
        byte[] message = new byte[3];
        message[2] = (byte) 'p';

        System.out.println("[DEBUG] Sever sending pong.");
        return message;
    }


    /**
     * Message type: s
     *
     * Adds or removes snake from the server.
     *
     * In the initial parts of the client-server interaction, the client sends an add snake process.
     * @return byte array
     */
    default byte[] addRemoveSnake(int[] data) {

        String snakeName = "";
        for (int i = 4; i < data.length; i++) {
            snakeName += (char) data[i];
        }
        clients.add(new Snake(clients.size(), snakeName,
            100, 100, 16074184, 48, 5790, 16074184, 0, new LinkedList<>()));
        byte[] message = new byte[snakeName.length() + 34];

        message[0] = 0;
        message[1] = 0;
        message[2] = (byte) 's';


        message[3] = 0;
        message[4] = 1;

        message[5] = (byte) 0x5e;
        message[6] = (byte) 0xf6;
        message[7] = (byte) 0xab;

        message[8] = (byte) 0x30;

        message[9] = (byte) 0x5e;
        message[10] = (byte) 0xf6;
        message[11] = (byte) 0xab;

        message[12] = (byte) 0x16;
        message[13] = (byte) 0x9e;

        message[14] = 0;
        message[15] = 0;
        message[16] = 0;

        message[17] = 0;

        message[18] = (byte) 0x01;
        message[19] = (byte) 0x81;
        message[20] = (byte) 0xce;

        message[21] = (byte) 0x02;
        message[22] = (byte) 0x13;
        message[23] = (byte) 0x40;

        message[24] = (byte) snakeName.length();

        for(int i = 0; i < snakeName.length(); i++) {
            message[25 + i] = (byte) snakeName.charAt(i);
        }
        message[25 + snakeName.length()] = 0;

        //Custom-skin-data is skipped

        message[26 + snakeName.length()] = (byte) 0x01;
        message[27 + snakeName.length()] = (byte) 0x82;
        message[28 + snakeName.length()] = (byte) 0x5e;

        message[29 + snakeName.length()] = (byte) 0x02;
        message[30 + snakeName.length()] = (byte) 0x12;
        message[31 + snakeName.length()] = (byte) 0xa8;

        message[32 + snakeName.length()] = (byte) 0x45;

        message[33 + snakeName.length()] = (byte) 0xbb;

        System.out.println("[DEBUG] Server sent s");
        return message;
    }

    /**
     * Message Type: a
     *
     * Creates the initial game setup for the client.
     * Check processInitResponse() to see how the client processes this.
     */
    default byte[] createInitSetup(int[] data) {
        byte[] message = new byte[26]; // The data length must be 26.
        //Byte 0-1 -> Time since last message to client
        message[0] = 0;
        message[1] = 0;

        //Byte 2 -> Message Type
        message[2] = (byte) 'a';

        //Byte 3-5 -> Game Radius
        message[3] = (byte) (GAME_RADIUS >> 16) & 0xFF;
        message[4] = (byte) (GAME_RADIUS >> 8) & 0xFF;
        message[5] = (byte) (GAME_RADIUS & 0xFF);

        //Byte 6-7 -> mscps
        message[6] = (byte) (MSCPS >> 8) & 0xFF;
        message[7] = (byte) (MSCPS & 0xFF);

        //Byte 8-9 -> sector_size
        message[8] = (byte) (SECTOR_SIZE >> 8) & 0xFF;
        message[9] = (byte) (SECTOR_SIZE & 0xFF);

        //Byte 10-11 -> sector_count
        message[10] = (byte) (SECTOR_COUNT_ALONG_EDGE >> 8) & 0xFF;
        message[11] = (byte) (SECTOR_COUNT_ALONG_EDGE & 0xFF);

        //Byte 12 -> spangdv
        message[12] = (byte) ((int)(SPANGDV * 10)) & 0xFF;

        //Byte 13-14 -> nsp1
        message[13] = (byte) ((int)(NSP1 * 100) >> 8) & 0xFF;
        message[14] = (byte) ((int)(NSP1 * 100)) & 0xFF;

        //Byte 15-16 -> nsp2
        message[15] = (byte) ((int)(NSP2 * 100) >> 8) & 0xFF;
        message[16] = (byte) ((int)(NSP2 * 100) & 0xFF);

        //Byte 17-18 -> nsp3
        message[17] = (byte) ((int)(NSP3 * 100) >> 8) & 0xFF;
        message[18] = (byte) ((int)(NSP3 * 100) >> 8) & 0xFF;

        //Byte 19-20 -> mamu
        message[19] = (byte) ((int)(MAMU * 1000) >> 8) & 0xFF;
        message[20] = (byte) ((int)(MAMU) & 0xFF);

        //Byte 21-22 -> manu2
        message[21] = (byte) ((int)(MANU2 * 1000) >> 8) & 0xFF;
        message[22] = (byte) ((int)(MANU2 * 1000) & 0xFF);

        //Byte 23-24 -> cst
        message[23] = (byte) ((int)(CST * 1000) >> 8) & 0xFF;
        message[24] = (byte) ((int)(CST * 1000) & 0xFF);

        //Byte 25 -> protocol
        message[25] = (byte) PROTOCOL_VERSION & 0xFF;

        System.out.println("[DEBUG] Server sent inital game setup package");
        return message;
    }

    /**
     * NOT MY CODE
     *
     * Exact Copy from MySlitherWebSocketClient's decodeSecret method.
     *
     * @param secret
     * @return
     */
    default byte[] secretAnswer(int[] secret) {
        byte[] result = new byte[24];

        int globalValue = 0;
        for (int i = 0; i < 24; i++) {
            int value1 = secret[17 + i * 2];
            if (value1 <= 96) {
                value1 += 32;
            }
            value1 = (value1 - 98 - i * 34) % 26;
            if (value1 < 0) {
                value1 += 26;
            }

            int value2 = secret[18 + i * 2];
            if (value2 <= 96) {
                value2 += 32;
            }
            value2 = (value2 - 115 - i * 34) % 26;
            if (value2 < 0) {
                value2 += 26;
            }

            int interimResult = (value1 << 4) | value2;
            int offset = interimResult >= 97 ? 97 : 65;
            interimResult -= offset;
            if (i == 0) {
                globalValue = 2 + interimResult;
            }
            result[i] = (byte) ((interimResult + globalValue) % 26 + offset);
            globalValue += 3 + interimResult;
        }

        return result;
    }
}
