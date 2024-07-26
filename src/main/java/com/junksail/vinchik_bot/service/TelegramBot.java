package com.junksail.vinchik_bot.service;

import com.junksail.vinchik_bot.config.BotConfig;
import com.junksail.vinchik_bot.user_info.User;
import com.junksail.vinchik_bot.user_info.UserState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;

    private Map<Long, UserState> userStates = new ConcurrentHashMap<>();
    private Map<Long, User> users = new ConcurrentHashMap<>();

    private static final String HELP_TEXT = "Данный бот поможет Вам в поиске новых знакомств!" +
            "\nКак пользоваться ботом? Всё просто:" +
            "\nПервым делом заполни свою анкету, а затем можно выдвигаться на поиски людей!";

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/create_profile", "Начать работу"));
        listOfCommands.add(new BotCommand("/help", "Подробнее о функционале бота"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            System.out.println("Здесь типо лог исключение TelegramBot");
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            UserState userState = userStates.getOrDefault(chatId, UserState.START);
            User user = users.getOrDefault(chatId, new User());

            switch (userState) {
                case START:
                    switch (messageText) {
                        case "/create_profile":
                            sendMessage(chatId, "Введите свой гендер (мужской/женский):");
                            userStates.put(chatId, UserState.WAITING_FOR_GENDER);
                            break;
                        case "/help":
                            sendMessage(chatId, HELP_TEXT);
                            break;
                        default:
                            sendMessage(chatId, "Команда не распознана");
                    }
                    break;
                case WAITING_FOR_GENDER:
                    user.setGender(messageText);
                    sendMessage(chatId, "Введите ваше имя:");
                    userStates.put(chatId, UserState.WAITING_FOR_NAME);
                    break;
                case WAITING_FOR_NAME:
                    user.setName(messageText);
                    sendMessage(chatId, "Введите ваш возраст:");
                    userStates.put(chatId, UserState.WAITING_FOR_AGE);
                    break;
                case WAITING_FOR_AGE:
                    try {
                        user.setAge(messageText);
                        sendMessage(chatId, "Введите ваш город:");
                        userStates.put(chatId, UserState.WAITING_FOR_CITY);
                    } catch (NumberFormatException e) {
                        sendMessage(chatId, "Некорректный формат возраста. Пожалуйста, введите число.");
                    }
                    break;
                case WAITING_FOR_CITY:
                    user.setCity(messageText);
                    sendMessage(chatId, "Введите описание вашего профиля:");
                    userStates.put(chatId, UserState.WAITING_FOR_DESCRIPTION);
                    break;
                case WAITING_FOR_DESCRIPTION:
                    user.setDescription(messageText);
                    sendMessage(chatId, "Отправьте фотографию вашего профиля:");
                    userStates.put(chatId, UserState.WAITING_FOR_PHOTO);
                    break;
            }
        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
            long chatId = update.getMessage().getChatId();
            User user = users.getOrDefault(chatId, new User());
            if (userStates.get(chatId) == UserState.WAITING_FOR_PHOTO) {
                PhotoSize photoSize = update.getMessage().getPhoto().get(update.getMessage().getPhoto().size() - 1);
                user.setPhotoURL(photoSize.getFileId());
                sendMessage(chatId, "Профиль создан!");
                users.put(chatId, user);
                userStates.put(chatId, UserState.START);
            }
        }
    }


    private void startCommandReceived(long chatId, String name) {
        String answer = "Добрый день, " + name + "!" + "\nДанный бот является лучшим ботом для поиска людей во всём Telegram!" +
                "\nДля начала необходимо создать анкету! " +
                "\nРодион ест козявки";
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
        }
    }
}