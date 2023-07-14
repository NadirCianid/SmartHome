package Staff;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

public class HttpPostTest {
    private static final byte POLYNOMIAL = 0x07;

    public static void main(String[] args) {
        try {
            // Создание URL-объекта

            URL url = new URL(args[0]);
            //url = new URL("http://localhost:9998");
            Varuint hubAdress = new Varuint(Long.parseLong(args[1], 16));
            //hubAdress = new Varuint(0xef0);

            // Открытие соединения HttpURLConnection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Установка метода запроса на POST
            connection.setRequestMethod("POST");

            // Включение возможности записи данных в соединение
            connection.setDoOutput(true);

            // Создание тела запроса в виде строки
            Packet body = new Packet();

            body.setPayload(new Payload());

            body.getPayload().setSrc(hubAdress);
            body.getPayload().setDst(new Varuint(0x3FFF));
            body.getPayload().setSerial(new Varuint(0x01));
            body.getPayload().setDev_type((byte) 0x01);
            body.getPayload().setCmd((byte) 0x01);
            body.getPayload().setCmd_body(new CmdBody((byte) 0x01,(byte) 0x01, new Device( "SmartHub", new DevProps((byte) 0x01, new byte[0]))));



            body.setLength((byte) body.getPayloadInBytes().length);  // подправить
            body.setCrc8(Compute_CRC8_Simple(body.getPayloadInBytes())); //подправить


            String requestBody = Base64.getUrlEncoder().encodeToString(body.getPacketInBytes());

            // Получение потока для записи данных в соединение
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

            // Запись тела запроса в поток
            outputStream.writeBytes(requestBody);
            outputStream.flush();
            outputStream.close();

            // Получение кода ответа от сервера
            int responseCode = connection.getResponseCode();

            // Чтение ответа сервера
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Вывод ответа сервера
            //System.out.println("Response Code: " + responseCode);
            //System.out.println("Response Body: " + response);

            Decoder.decode(Base64.getUrlDecoder().decode(response.toString()));

            // Закрытие соединения
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                } else {
                    crc <<= 1;
                }
            }
        }

        return crc;
    }

    public static class Packet {
        private byte length;
        private Payload payload;
        private byte crc8;


        public void setLength(byte length) {
            this.length = length;
        }

        public Payload getPayload() {
            return payload;
        }

        public void setPayload(Payload payload) {
            this.payload = payload;
        }

        public byte[] getPayloadInBytes() {
            return payload.toBytes();
        }

        public void setCrc8(byte crc8) {
            this.crc8 = crc8;
        }

        public byte[] getPacketInBytes() {
            byte[] bytes = new byte[getPayloadInBytes().length + 2];

            bytes[0] = length;
            System.arraycopy(getPayloadInBytes(), 0, bytes, 1, length);
            bytes[length + 1] = crc8;

            return bytes;
        }
    }


    public static class Payload {
        private Varuint src;
        private Varuint dst;
        private Varuint serial;
        private byte dev_type;
        private byte cmd;
        private CmdBody cmd_body;

        public Payload() {
        }

        public Payload(byte[] payloadBytes) { //из бинарной формы в объект
            src = new Varuint(Varuint.readULEB128(payloadBytes));
            dst = new Varuint(Varuint.readULEB128(Arrays.copyOfRange(payloadBytes, src.getSize(), payloadBytes.length)));
            serial = new Varuint(Varuint.readULEB128(Arrays.copyOfRange(payloadBytes, src.getSize() + dst.getSize(), payloadBytes.length)));
            dev_type = payloadBytes[src.getSize() + dst.getSize() + serial.getSize()];
            cmd = payloadBytes[src.getSize() + dst.getSize() + serial.getSize() + 1];
            cmd_body = new CmdBody(cmd, dev_type, Arrays.copyOfRange(payloadBytes, src.getSize() + dst.getSize() + serial.getSize() + 2, payloadBytes.length));
        }

        public Varuint getSrc() {
            return src;
        }

        public void setSrc(Varuint src) {
            this.src = src;
        }

        public Varuint getDst() {
            return dst;
        }

        public void setDst(Varuint dst) {
            this.dst = dst;
        }

        public Varuint getSerial() {
            return serial;
        }

        public void setSerial(Varuint serial) {
            this.serial = serial;
        }

        public byte getDev_type() {
            return dev_type;
        }

        public void setDev_type(byte dev_type) {
            this.dev_type = dev_type;
        }

        public byte getCmd() {
            return cmd;
        }

        public void setCmd(byte cmd) {
            this.cmd = cmd;
        }

        public CmdBody getCmd_body() {
            return cmd_body;
        }

        public void setCmd_body(CmdBody cmd_body) {
            this.cmd_body = cmd_body;
        }

        public byte[] toBytes() {

            int srcSize = src.getSize();
            int dstSize = dst.getSize();
            int serialSize = serial.getSize();
            int cmdBodySize = cmd_body.getSize();

            int payloadLength = srcSize + dstSize + serialSize + 2 + cmdBodySize;
            byte[] bytes = new byte[payloadLength];

            System.arraycopy(src.getBytes(), 0, bytes, 0, srcSize);
            System.arraycopy(dst.getBytes(), 0, bytes, srcSize, dstSize);
            System.arraycopy(serial.getBytes(), 0, bytes, srcSize + dstSize, serialSize);
            bytes[srcSize + dstSize + serialSize] = dev_type;
            bytes[srcSize + dstSize + serialSize + 1] = cmd;
            System.arraycopy(cmd_body.getBytes(), 0, bytes, srcSize + dstSize + serialSize + 2, cmdBodySize);

            return bytes;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(src.getValue());
            sb.append(" ");
            sb.append(dst.getValue());
            sb.append(" ");
            sb.append(serial.getValue());
            sb.append(" ");
            sb.append(dev_type);
            sb.append(" ");
            sb.append(cmd);
            sb.append(" ");
            sb.append(cmd_body);

            return sb.toString();
        }
    }

    public static class Decoder {
        public static void decode(byte[] packet) {  //преобразует массив байтов в список payloads
            // Staff.URLBase64.binaryPrint(packet);

            ArrayList<Payload> payloads = new ArrayList<>();
            int pivot = -1; //позиция crc8 на данном шаге
            while (pivot < packet.length - 1) {
                byte payloadLength = packet[pivot + 1];
                pivot += payloadLength + 2;

                byte[] payloadBytes = Arrays.copyOfRange(packet, pivot - payloadLength, pivot);
                payloads.add(new Payload(payloadBytes));

            }

            //payloads.forEach(System.out::println);
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


    public static class CmdBody {
        byte cmd;
        Timestamp timestamp;
        String[] values; //для сенсора
        byte enabled; // для выключателя, розетки и лампы
        private byte devType;
        private Device device;


        public CmdBody(byte cmd, byte devType, Device device) {
            this.cmd = cmd;
            this.devType = devType;
            this.device = device;
        }

        public CmdBody(byte cmd, byte devType, byte[] cmd_body) {
            this.cmd = cmd;
            this.devType = devType;

            switch (cmd) {
                case 0x01, 0x02 -> {
                    String dev_name = Decoder.byteArrayToString(cmd_body);
                    device = new Device(dev_name, new DevProps(devType, Arrays.copyOfRange(cmd_body, cmd_body[0], cmd_body.length)));
                }
                case 0x04 -> {
                    if (devType > 0x02 && devType < 0x06) {
                        enabled = cmd_body[0];
                    }
                }
                case 0x06 -> {
                    if (devType == 0x06) {
                        timestamp = new Timestamp(Varuint.decode(cmd_body));
                    }
                }
                //кинуть ошибку
                default -> {
                }
                //игнорировать
            }
        }

        public int getSize() {
            return device.getSize();
        }

        public byte[] getBytes() {
            return device.getBytes();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (timestamp != null) {
                sb.append(timestamp);
            }
            if (device != null) {
                sb.append(device.getDev_name());
            }
            return sb.toString();
        }
    }


    public static class Varuint {
        private final byte[] value;

        Varuint(long value) {
            this.value = encode(value);
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

        public byte[] getBytes() {
            return value;
        }

        public long getValue() {
            return decode(value);
        }

        public int getSize() {  //метод возвращает количество байт в числе
            return value.length;
        }
    }


    public static class Device {
        private String dev_name;
        private DevProps dev_props;
        private byte[] bytes;

        Device(String dev_name, DevProps dev_props) {
            this.dev_name = dev_name;
            this.dev_props = dev_props;

            int length = dev_name.getBytes().length + 1 + dev_props.getBytes().length;
            bytes = new byte[length];
            bytes[0] = (byte) dev_name.getBytes().length;
            System.arraycopy(dev_name.getBytes(), 0, bytes, 1, dev_name.getBytes().length);
        }

        public String getDev_name() {
            return dev_name;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public int getSize() {
            return bytes.length;
        }
    }


    public static class DevProps {
        byte devType;
        byte[] devProps;

        public DevProps(byte devType, byte[] devProps) {
            this.devType = devType;
            this.devProps = devProps;
        }

        public byte[] getBytes() {
            return devProps;
        }
    }
}
