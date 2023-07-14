package Staff;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class Varuint {
    private final byte[] value;

    Varuint(long value) {
        this.value = encode(value);
    }

    public byte[] getBytes() {
        return value;
    }

    public long getValue() {
        return decode(value);
    }

    public int getSize() {  //метод возвращает количество байт в числе
        return value.length;
    }

    public static byte[] encode(long value) { //кодирование десятичного числа в Staff.ULEB128
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

    public static long decode(byte[] encoded) { //декодирование числа в формате Staff.ULEB128 в  десятичное число
        long value = 0;
        long shift = 0;

        for (byte b : encoded) {
            long digit = b & 0x7F; // Получение младших 7 битов из байта

            value |= digit << shift; // Добавление 7 битов в число на правильную позицию
            shift += 7; // Сдвиг для следующего 7-битного числа
        }

        return value;
    }

    public static long readULEB128(byte[] bytes) {
        long result = 0;
        int shift = 0;
        int index = 0;
        byte currentByte;

        do {
            currentByte = bytes[index];
            result |= (long) (currentByte & 0x7F) << shift;
            shift += 7;
            index++;
        } while ((currentByte & 0x80) != 0);

        return result;
    }

    public static Varuint[] decodeArray(byte[] bytes) {
        ArrayList<Varuint> varuints = new ArrayList<>();
        int pivot = 0;
        while(pivot < bytes.length) {
            Varuint currElem = new Varuint(readULEB128(Arrays.copyOfRange(bytes, pivot, bytes.length)));
            varuints.add(currElem);
            pivot += currElem.getSize();
        }

        return varuints.toArray(new Varuint[0]);
    }
}
