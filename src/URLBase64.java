import java.util.Arrays;
import java.util.Base64;

public class URLBase64 {
    public static void main(String[] args) {
        // Исходная строка для кодирования
        String originalString = "DbMG_38BBgbw9NH1lDGnD7MG_38CBgIHVElNRVIwMV8";



        // Декодирование URL-закодированной, неполной base64-строки
        byte[] decodedBytes = Base64.getUrlDecoder().decode(originalString);
        binaryPrint(decodedBytes);
        Decoder.decode(decodedBytes);



        System.out.println((Varuint.readULEB128(Arrays.copyOfRange(decodedBytes, 1, 14))));

        /*// Кодирование в URL-закодированную, неполную base64-строку
        String encodedString = Base64.getUrlEncoder().encodeToString(decodedBytes);
        System.out.println(encodedString);

        binaryPrint(Base64.getUrlDecoder().decode(originalString + "="));
       binaryPrint(decodedBytes);

       binaryPrint(Arrays.copyOfRange(decodedBytes, 1, decodedBytes.length-1));

        System.out.println(Integer.toBinaryString(Compute_CRC8_Simple(Arrays.copyOfRange(decodedBytes, 1, decodedBytes.length-1)) & 0xFF));*/

    }

    public  static void binaryPrint(byte[] encoded) {
        for (byte b : encoded) {
            System.out.print(Integer.toBinaryString(b& 0xFF) + " "); // Печать в двоичном формате
        }
        System.out.println();
    }

    public static byte Compute_CRC8_Simple(byte[] bytes)
    {
        byte generator = 0x1D;
        byte crc = 0; /* start with 0 so first byte can be 'xored' in */

        for(byte currByte : bytes)
        {
            crc ^= currByte; /* XOR-in the next input byte */

            for (int i = 0; i < 8; i++)
            {
                if ((crc & 0x80) != 0)
                {
                    crc = (byte)((crc << 1) ^ generator);
                }
                else
                {
                    crc <<= 1;
                }
            }
        }

        return crc;
    }
}
