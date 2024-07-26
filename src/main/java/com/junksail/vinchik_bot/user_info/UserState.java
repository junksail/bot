package com.junksail.vinchik_bot.user_info;

//перечесление состояний юзера для сбора информации
public enum UserState {
    START,
    WAITING_FOR_GENDER,
    WAITING_FOR_NAME,
    WAITING_FOR_AGE,
    WAITING_FOR_CITY,
    WAITING_FOR_DESCRIPTION,
    WAITING_FOR_PHOTO
}
