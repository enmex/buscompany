package net.thumbtack.school.buscompany.exception;

public enum ErrorCode {
    USER_ALREADY_ONLINE("Пользователь уже в сети"),
    USER_IS_OFFLINE("Пользователя нет в сети"),
    INVALID_LOGIN_OR_PASSWORD("Неверно указан логин или пароль"),
    INVALID_PASSWORD("Неверно указан пароль"),
    USER_ALREADY_EXISTS("Пользователь с данным логином уже зарегистрирован"),
    USER_NOT_EXISTS("Данного пользователя не существует"),
    UNABLE_TO_OPEN_SESSION("Не получается открыть сессию"),
    UNABLE_TO_CLOSE_SESSION("Не получается закрыть сессию"),
    OPERATION_NOT_ALLOWED("Недостаточно прав для совершения данной операции"),
    DUPLICATE_BUS("Данный автобус уже есть в базе данных"),
    DATABASE_ERROR("Ошибка при обработке базы данных"),
    DATES_AND_SCHEDULE_INVOLVED("В запросе должно быть расписание или список дат, но не все вместе"),
    TRIP_NOT_EXISTS("Данного отправления не существует"),
    EMPTY_SCHEDULE("Расписание не может быть пустым"),
    ONLINE_OPERATION("Для совершения данной операции необходимо войти в сеть"),
    EMPTY_DATES("Список дат не может быть пустым"),
    ORDER_NOT_EXISTS("Данного заказа не существует"),
    OFFLINE_OPERATION("Для совершения данной операции необходимо выйти из сети"),
    PASSENGER_ALREADY_EXISTS("Данный пассажир уже есть в базе данных"),
    CANNOT_CLEAR_DATABASE("Не удалось очистить базу данных");

    private final String message;

    ErrorCode(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
