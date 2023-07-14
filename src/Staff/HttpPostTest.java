package Staff;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
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
                }
                else
                {
                    crc <<= 1;
                }
            }
        }

        return crc;
    }

    private static void binaryPrint(byte[] encoded) {
        for (byte b : encoded) {
            System.out.print(Integer.toBinaryString(b& 0xFF) + " "); // Печать в двоичном формате
        }
        System.out.println();
    }

    public static void getStatus(Varuint address) {
    }
}
