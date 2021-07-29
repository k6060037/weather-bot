import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class WeatherBot extends TelegramLongPollingBot {
    private String inputMessage;
    private SendMessage message = new SendMessage();

    @Override
    public String getBotUsername() {
        return "lucash_weather_bot";
    }

    @Override
    public String getBotToken() {
        return Token.TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        String startMessage = "Введите название города. Если города не существует, бот об этом сообщит.";
        System.out.println(update.getMessage().getText());
        inputMessage = update.getMessage().getText();
        if (inputMessage.equals("/start") || inputMessage.equals("start")) {
            inputMessage = startMessage;
        } else {
            showResult();
        }
        message.setText(inputMessage);
        message.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage();
    }

    private String getJson(String urlAddress){
        StringBuffer content = new StringBuffer();
        try {
            URL url = new URL(urlAddress);
            URLConnection urlConnection = url.openConnection();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;

            while((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            System.out.println("Город не найден");
            return "fail";

        }
        return content.toString();
    }

    private void showResult(){
        String output = getJson("http://api.openweathermap.org/data/2.5/weather?q=" + inputMessage + "&units=metric&appid=25b892d8aa25887ea3e92b9aba5f27f0");
        if (output.equals("fail")) {
            inputMessage = ("Такого города не существует. Для получения инструкции введите /start или start");
        } else if (!output.isEmpty()) {
            JSONObject obj = new JSONObject(output);
            JSONObject main = obj.getJSONObject("main");
            inputMessage = "Температура: " + main.getDouble("temp") + " С," +
                    " давление: " + main.getDouble("pressure") + " ГПа," +
                    " влажность: " + main.getDouble("humidity") + " %";

        }
    }

    private void sendMessage(){
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
