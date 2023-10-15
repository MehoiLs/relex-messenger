<div align="center">
        <h1 align="center"> Тестовое задание для стажировки в РЕЛЭКС </h1>
</div>

<details>
  <summary>Содержание</summary>
  <ol>
    <li><a href="#описание-проекта">Описание проекта</a></li>
    <li><a href="#описание-проекта">Основные возможности</a></li>
    <li><a href="#дополнительные-задания">Дополнительные задания</a></li>
    <li><a href="#примеры-rest-запросов">Примеры REST-запросов</a></li>
    <li><a href="#пример-использования-websocket-чата">Пример использования WebSocket чата</a></li>
    <li><a href="#демонстрационное-видео">Демонстрационное видео</a></li>
  </ol>
</details>

# Описание проекта
Данный проект представляет собой упрощённый месседжер, взаимодействие с которым происходит с помощью REST-запросов и WebSocket'ов.
Реализован на языке Java с использованием фреймворка Spring Boot и базы данных PostgreSQL. Большинство данных, отправляемых и 
получаемых, представлены в формате JSON. Однако, для определенных функций, таких как отправка изображений (аватарки пользователей) 
и файлов переписки (.txt), также предусмотрены методы, поддерживающие передачу файлов в бинарном формате.

# Основные возможности
## Регистрация и авторизация
* Регистрация
  * Пользователь может зарегистрироваться в системе при помощь email, логина и пароля
  * Реализована проверка уникальности логина и email
  * Отправка подтверждения по почте с использованием ссылки в письме
* Авторизация
  * Возможность входа в систему при помощи логина и пароля
  * Используется Spring Security и JWT токены для хранения информации о сессии
  * Возможность выхода из системы с инвалидацией сессии
 
## Профиль пользователя
* Обновление данных профиля
  * Пользователь может обновлять данные своего профиля, такие как никнейм, аватарка, имя, фамилия, статус, описание и другие дополнительные данные
  * Изменение email
    * Реализовано с помощью отправки ссылки для подтверждения на новую почту
  * Изменение пароля
    * Реализовано с помощью подтверждения старого пароля
  * Изменение аватарки
* Удаление аккаунта
  * Возможность удаления аккаунта с последующей возможностью восстановления в течение определенного времени (7 дней)
 
## Социальная часть
* Отправка сообщений
  * **Обмен и просмотр сообщений реализован с использованием веб-сокетов**
  * Пользователь может отправлять сообщения пользователям по их никнеймам
  * Реализована проверка на существование пользователя
  * Реализована проверка на то, ограничил ли пользователь получение личных сообщений списком своих друзей
* История сообщений
  * Возможность просмотреть и скачать историю сообщений с другим пользователем
* Дополнительные возможности
  * Добавление других пользователей в список друзей
  * Просмотр списка друзей
  * Просмотр новых сообщений и новых запросов в друзья
  * Возможность ограничивать получение сообщений только своим списком друзей
  * Возможнсть скрывать свой список друзей от других пользователей
 
## Дополнительный функционал
* Автоматическая очистка базы данных
  * Очистка БД от просроченных инвалидированных JWT токенах
  * Очистка БД от просроченных токенов для смены email
  * Очистка БД от просроченных токенов регистрации
  * Очистка БД от удалённых пользователей (с момента удаления должно пройти 7 дней)
* Пользователи с ролью "ADMIN" могут принудительно запускать очистку БД

# Дополнительные задания
В проекте были реализованы все дополнительные задания:
* Возможность добавлять других пользователей в друзья, а также просматривать список своих друзей
* Документирование запросов через Swagger
  * (примечание: для доступа к GUI, необходимо войти с правами админа по адресу "/login/gui")
* Написание тестов (JUnit)

# Примеры REST-запросов
## Регистрация
```sh
POST /register
Content-Type: application/json

{
  "email": "example@website.com",
  "login": "login",
  "password": "password",
  "username": "CoolUsername",
  "first_name": "Name",
  "last_name": "Surname"
}
```
## Логин
```sh
POST /login
Content-Type: application/json

{
  "login": "login",
  "password": "password"
}
```
## Логаут
```sh
POST /logout
Authorization: Bearer <token>
```
## Домашняя страница
```sh
GET /home
Authorization: Bearer <token>
```

## Профиль пользователя
### Получить основную информацию
```sh
GET /profile
Authorization: Bearer <token>
```
### Получить информацию, доступную для изменения
```sh
GET /profile/edit
Authorization: Bearer <token>
```
### Изменить информацию, доступную для изменения
```sh
POST /profile/edit
Authorization: Bearer <token>
Content-Type: application/json

{
  "username": "newUsername",
  "personal_status": "newStatus",
  "description": "newDescription",
  "first_name": "Newname",
  "last_name": "Newsurname"
}
```
### Получить форму для изменения почты
```sh
GET /profile/edit/email
Authorization: Bearer <token>
```
### Изменить почту
```sh
POST /profile/edit/email
Authorization: Bearer <token>

{
  "email": "newemail@website.com"
}
```
### Подтвердить новую почту (по ссылке)
```sh
GET /profile/edit/email/confirm/{token}
```
### Получить аватарку пользователя
```sh
GET /profile/edit/pfp
Authorization: Bearer <token>
```
### Изменить аватарку пользователя
```sh
POST /profile/edit/pfp
Authorization: Bearer <token>
Content-Type: multipart/form-data

- image: <path/to/the/image.jpg>
```
### Получить форму для изменения пароля
```sh
GET /profile/edit/pfp
Authorization: Bearer <token>
```
### Изменить пароль
```sh
POST /profile/edit/password
Authorization: Bearer <token>
Content-Type: application/json

{
  "old_password": "your old password",
  "new_password": "your new password"
}
```

## Сообщество (друзья)
### Получить информацию сообщества (новые запросы в друзья, новые сообщения)
```sh
GET /community
Authorization: Bearer <token>
```

### Получить свой список друзей
```sh
GET /community/friends
Authorization: Bearer <token>
```
### Удалить пользователя из списка друзей
```sh
POST /community/friends/{username}/remove
Authorization: Bearer <token>
```

### Получить информацию о профиле конкретного пользователя
```sh
GET /community/users/{username}
Authorization: Bearer <token>
```
### Добавить пользователя в друзья (отправить запрос)
```sh
POST /community/users/{username}/add
Authorization: Bearer <token>
```

### Принять пользователя в друзья (принять запрос)
```sh
POST /community/requests/accept/user/{username}
Authorization: Bearer <token>
```
### Принять все запросы в друзья
```sh
POST /community/requests/accept/all
Authorization: Bearer <token>
```
### Отклонить запрос от пользователя в друзья
```sh
POST /community/requests/deny/user/{username}
Authorization: Bearer <token>
```
### Отклонить все запросы в друзья
```sh
POST /community/requests/deny/all
Authorization: Bearer <token>
```

## Сообщения
### Получить все непрочитанные сообщения
```sh
GET /messages/unread
Authorization: Bearer <token>
```
### Получить все непрочитанные сообщения от конкретного пользователя
```sh
GET /messages/user/{username}/unread
Authorization: Bearer <token>
```
### Получить количество непрочитанных сообщений от конкретного пользователя
```sh
GET /messages/user/{username}/unread/count
Authorization: Bearer <token>
```
### Получить (скачать) историю сообщений с конкретным пользователем
```sh
GET /messages/user/{username}/all/download
Authorization: Bearer <token>
```

## Команды админа
### Принудительная очистка БД
```sh
POST /admin/database/cleanup
Authorization: Bearer <token>
```
### Принудительно разлогинить всех пользователей
```sh
POST /admin/logout/all
Authorization: Bearer <token>
```


# Пример использования WebSocket чата
## Страница для входа ("/login/gui")
![2023-10-15_17-42-26](https://github.com/MehoiLs/relex-messenger/assets/129596462/57ddf1e3-caf4-4b0a-a79c-a20c8f58f333)
## Страница выбора собеседника ("/chat")
![2023-10-15_17-43-17](https://github.com/MehoiLs/relex-messenger/assets/129596462/e2712c86-18ef-49cf-b717-ed817196390d)
## Сообщение об ошибке (пользователя не существует)
![2023-10-15_17-43-53](https://github.com/MehoiLs/relex-messenger/assets/129596462/1d0380e0-a9fc-43dc-8f5e-9150481474f8)
## Сообщение об ошибке (пользователь ограничил получение сообщений)
![2023-10-15_17-44-34](https://github.com/MehoiLs/relex-messenger/assets/129596462/bb8c0020-e3c2-4f08-b026-d5bba5fdec2e)
## Обмен сообщениями
![2023-10-15_17-46-43](https://github.com/MehoiLs/relex-messenger/assets/129596462/1959e969-94af-45da-8bf5-1256a9b3f7dd)
![2023-10-15_17-46-54](https://github.com/MehoiLs/relex-messenger/assets/129596462/df1d93b0-684d-4ee8-ab81-89e559b753cc)


# Демонстрационное видео
