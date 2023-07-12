import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class HttpPostTest {
    public static void main(String[] args) {
        try {
            // Создание URL-объекта
            URL url = new URL("http://localhost:9998");

            // Открытие соединения HttpURLConnection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Установка метода запроса на POST
            connection.setRequestMethod("POST");

            // Включение возможности записи данных в соединение
            connection.setDoOutput(true);

            // Создание тела запроса в виде строки
            String requestBody = "";

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
            System.out.println("Response Code: " + responseCode);
            System.out.println("Response Body: " + response.toString());

            // Закрытие соединения
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
