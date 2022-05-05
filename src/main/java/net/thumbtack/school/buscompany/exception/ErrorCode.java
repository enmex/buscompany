package net.thumbtack.school.buscompany.exception;

public enum ErrorCode {
    INVALID_DATE("Неверно указана дата", "date"),
    USER_IS_OFFLINE("Пользователя нет в сети", "user"),
    INVALID_LOGIN_OR_PASSWORD("Неверно указан логин или пароль", "login"),
    INVALID_PASSWORD("Неверно указан пароль", "password"),
    USER_ALREADY_EXISTS("Пользователь с данным логином уже зарегистрирован", "user"),
    USER_NOT_EXISTS("Данного пользователя не существует", "user"),
    UNABLE_TO_OPEN_SESSION("Не получается открыть сессию", "session"),
    UNABLE_TO_CLOSE_SESSION("Не получается закрыть сессию", "session"),
    OPERATION_NOT_ALLOWED("Недостаточно прав для совершения данной операции", "operation"),
    DATABASE_ERROR("Ошибка при обработке базы данных", "database"),
    TRIP_NOT_EXISTS("Данного отправления не существует", "trip"),
    ONLINE_OPERATION("Для совершения данной операции необходимо войти в сеть", "operation"),
    ORDER_NOT_EXISTS("Данного заказа не существует", "order"),
    OFFLINE_OPERATION("Для совершения данной операции необходимо выйти из сети", "operation"),
    PASSENGER_ALREADY_EXISTS("Данный пассажир уже есть в базе данных", "passenger"),
    TRIP_IS_REGULAR("Данная поездка регулярная, имеет расписание", "trip"),
    NO_TRIP_ON_THIS_DATE("В указанную дату поездка не воспроизводится", "date"),
    TRIP_NOT_APPROVED("Искомая поездка не подтверждена администратором", "trip"),
    NO_FREE_PLACES("Отсутствуют свободные места", "place"),
    INVALID_ID("Неверно указан ID", "id"),
    PLACE_IS_OCCUPIED("Данное место уже занято другим пассажиром", "place"),
    PLACE_IS_ALREADY_TAKEN_BY_THIS_PASSENGER("Данное место уже занято данным пассажиром", "place"),
    NO_DATES_ON_THIS_SCHEDULE("Некорректное расписание: отсутствуют даты", "schedule"),
    NOT_CLIENT_ORDER("Заказ не является заказом данного клиента", "order"),
    METHOD_NOT_ALLOWED("Данный запрос не может быть обработан", "method"),
    NOT_FOUND("Указанный ресурс не найден", "recourse"),
    WRONG_JSON_FORMAT("Некорректно введен json-запрос", "JSON");

    private final String message;
    private final String field;

    ErrorCode(String message, String field){
        this.message = message;
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public String getField() {
        return field;
    }
}
