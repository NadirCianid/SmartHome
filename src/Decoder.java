import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class Decoder {
    public static void decode(byte[] packet) {  //преобразует массив байтов в список payloads
       // URLBase64.binaryPrint(packet);

        ArrayList<Payload> payloads = new ArrayList<>();
        int pivot = -1; //позиция crc8 на данном шаге
        while (pivot < packet.length-1) {
            byte payloadLength = packet[pivot + 1];
            pivot += payloadLength + 2;

            byte[] payloadBytes = Arrays.copyOfRange(packet, pivot - payloadLength, pivot);
            payloads.add(new Payload(payloadBytes));

        }

        payloads.forEach(System.out::println);
    }

    public static String byteArrayToString(byte[] bytes) {
        if (bytes.length == 0) {
            return "";
        }

        int length = bytes[0];
        if (length >= bytes.length - 1) {
            length = bytes.length - 1;
        }

        byte[] stringBytes = new byte[length];
        System.arraycopy(bytes, 1, stringBytes, 0, length);

        return new String(stringBytes, StandardCharsets.UTF_8);
    }


}
