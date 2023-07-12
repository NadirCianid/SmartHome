import java.io.ByteArrayOutputStream;


public class ULEB128 {
    public static byte[] encode(long value) { //кодирование десятичного числа в ULEB128
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        do {
            byte b = (byte) (value & 0x7F); // Младшие 7 битов значения
            value >>= 7; // Сдвиг вправо на 7 битов
            if (value != 0) {
                b |= 0x80; // Установка старшего бита продолжения
            }
            out.write(b); // Запись байта в поток
        } while (value != 0);

        return out.toByteArray();
    }

    public static void main(String[] args) {
        long value = 138; // Пример беззнакового целого числа

        byte[] encoded = encode(value);
        binaryPrint(encoded);

        System.out.println(decode("111111111111111"));
    }

    private static void binaryPrint(byte[] encoded) {
        for (byte b : encoded) {
            System.out.print(Integer.toBinaryString(b& 0xFF) + "|"); // Печать в двоичном формате
        }
        System.out.println();
    }

    private static long decode(String ulebNum) { //декодирование числа в формате ULEB128 в  десятичное число
        byte[] encoded = bitStringToByteArray(ulebNum);
        binaryPrint(encoded);

        int value = 0;
        int shift = 0;

        for (int i = 0; i < encoded.length; i++) {
            byte b = encoded[i];
            int digit = b & 0x7F; // Получение младших 7 битов из байта

            value |= digit << shift; // Добавление 7 битов в число на правильную позицию
            shift += 7; // Сдвиг для следующего 7-битного числа
        }

        return value;
    }

    public static byte[] bitStringToByteArray(String bitString) {
        int len = bitString.length();
        byte[] byteArray = new byte[(len + 7) / 8];

        for (int i = 0; i < len; i += 8) {
            int endIndex = Math.min(i + 8, len);
            String byteString = bitString.substring(i, endIndex);
            byte byteValue = (byte) Integer.parseInt(byteString, 2);
            byteArray[i / 8] = byteValue;
        }

        return byteArray;
    }
}
