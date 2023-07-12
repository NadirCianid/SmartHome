import java.util.Base64;

public class URLBase64 {
    public static void main(String[] args) {
        // Исходная строка для кодирования
        String originalString = "DbMG_38BBgaI0Kv6kzGK";



        // Декодирование URL-закодированной, неполной base64-строки
        byte[] decodedBytes = Base64.getUrlDecoder().decode(originalString);


        // Кодирование в URL-закодированную, неполную base64-строку
        String encodedString = Base64.getUrlEncoder().encodeToString(decodedBytes);
        System.out.println(encodedString);

       binaryPrint(decodedBytes);
    }

    private static void binaryPrint(byte[] encoded) {
        for (byte b : encoded) {
            System.out.print(Integer.toBinaryString(b& 0xFF) + "|"); // Печать в двоичном формате
        }
        System.out.println();
    }
}
