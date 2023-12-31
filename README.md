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

_(примечание: в процессе разработки использовалась локальная БД)_

# Основные возможности
## Регистрация и авторизация
* Регистрация
  * Пользователь может зарегистрироваться в системе при помощи email, логина и пароля
  * Реализована проверка уникальности логина и email
  * Отправка подтверждения по почте с использованием ссылки в письме
* Авторизация
  * Возможность входа в систему при помощи логина и пароля
  * Используется Spring Security и JWT токены для хранения информации о сессии
  * Возможность выхода из системы с инвалидацией сессии и токена
 
## Профиль пользователя
* Обновление данных профиля
  * Пользователь может обновлять данные своего профиля, такие как: никнейм, аватарка, имя, фамилия, статус, описание и другие дополнительные данные
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
  * Возможность скрывать свой список друзей от других пользователей
 
## Дополнительный функционал
* Автоматическая очистка базы данных (ежедневно в 00:00)
  * Очистка БД от просроченных инвалидированных JWT токенов
  * Очистка БД от просроченных токенов для смены email
  * Очистка БД от просроченных токенов регистрации
  * Очистка БД от удалённых пользователей (с момента удаления должно пройти 7 дней)
* Пользователи с ролью "ADMIN" могут принудительно запускать очистку БД

# Дополнительные задания
В проекте были реализованы все дополнительные задания:
* Возможность добавлять других пользователей в друзья, а также просматривать список своих друзей
* Документирование запросов через Swagger
  * _(примечание: для доступа к GUI, необходимо войти с правами админа по адресу "/login/gui")_
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
Ответ в случае успешной регистрации:
```sh
{
  "message": "A confirmation letter will be sent to your email shortly. To complete registration, you will have to activate your account via the sent link. However, if you haven't received the letter, try sending the registration request again."
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
Ответ в случае успешного логина:
```sh
{
  "message": "You have successfully logged in. Remember, the token is given once. If you lost it, you have to logout and then login again to receive a new one.",
  "token": "<token>"
}
```

## Логаут
```sh
POST /logout
Authorization: Bearer <token>
```
Ответ в случае успешного логаута:
```sh
{
  "message": "You have successfully logged out."
}
```

## Профиль пользователя
### Получить основную информацию
```sh
GET /profile
Authorization: Bearer <token>
```
Ответ:
```sh
{
  "id": <id>,
  "username": "username",
  "personalStatus": "status",
  "description": "description",
  "firstName": "Name",
  "lastName": "Surname",
  "email": "email@website.com",
  "role": "USER",
  "locked": false
}
```


### Получить информацию, доступную для изменения
```sh
GET /profile/edit
Authorization: Bearer <token>
```
Ответ:
```sh
{
  "username": "username",
  "personalStatus": "status",
  "description": "description",
  "firstName": "Name",
  "lastName": "Surname",
}
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
Ответ:
```sh
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
Ответ:
```sh
{
  "email": "email@website.com"
}
```

### Изменить почту
```sh
POST /profile/edit/email
Authorization: Bearer <token>

{
  "email": "newemail@website.com"
}
```
Ответ:
```sh
{
  "message": "A confirmation letter will be sent to the email shortly. To complete e-mail change, you will have to set your new e-mail via the sent link. However, if you haven't received the letter, try sending the request again."
}
```


### Подтвердить новую почту
```sh
GET /profile/edit/email/confirm/{token}
Authorization: Bearer <token>
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
Ответ:
```sh
{
  "message": "You have successfully updated your profile picture."
}
```


### Получить форму для изменения пароля
```sh
GET /profile/edit/password
Authorization: Bearer <token>
```
Ответ:
```sh
{
  "old_password": "your old password",
  "new_password": "your new password"
}
```

### Изменить пароль
```sh
POST /profile/edit/password
Authorization: Bearer <token>
Content-Type: application/json

{
  "old_password": "old_password",
  "new_password": "new_password"
}
```
Ответ в случае успешной смены пароля:
```sh
{
  "message": "You have successfully changed your password."
}
```

### Удалить профиль
```sh
DELETE /profile/delete
Authorization: Bearer <token>
```
Ответ:
```sh
{
  "message": "You have successfully deleted your account. However, it will remain being deactivated for 7 days, before it is deleted permanently. In case you want to restore your account, you will have to login with your credentials again."
}
```



## Сообщество (друзья)
### Получить информацию сообщества (новые запросы в друзья, новые сообщения)
```sh
GET /community
Authorization: Bearer <token>
```
Ответ:
```sh
{
  "friend_requests": 0,
  "unread_messages": 0
}
```

### Получить свой список друзей
```sh
GET /community/friends
Authorization: Bearer <token>
```
Ответ:
```sh
{
  "friends_list": [
    "<friend>"
  ]
}
```

### Получить информацию о профиле конкретного пользователя
```sh
GET /community/users/{username}
Authorization: Bearer <token>
```
Ответ:
```sh
{
  "id": <id>,
  "username": "username",
  "personalStatus": "status",
  "description": "description,
  "firstName": "Name",
  "lastName": "Surname",
  "email": "email@website.com",
  "role": "USER"
}
```

### Добавить пользователя в друзья (отправить запрос)
```sh
POST /community/users/{username}/add
Authorization: Bearer <token>
```
Ответ:
```sh
{
  "message": "Successfully sent the request to: <friend>."
}
```

### Принять пользователя в друзья (принять запрос)
```sh
POST /community/requests/accept/user/{username}
Authorization: Bearer <token>
```
Ответ:
```sh
{
  "message": "Added <username> as friend!"
}
```

### Принять все запросы в друзья
```sh
POST /community/requests/accept/all
Authorization: Bearer <token>
```
Ответ:
```sh
{
  "message": "Successfully accepted all friend requests!"
}
```

### Отклонить запрос от пользователя в друзья
```sh
POST /community/requests/deny/user/{username}
Authorization: Bearer <token>
```
Ответ:
```sh
{
  "message": "Denied <username>'s friend request."
}
```

### Отклонить все запросы в друзья
```sh
POST /community/requests/deny/all
Authorization: Bearer <token>
```
Ответ:
```sh
{
  "message": "Successfully denied all friend requests."
}
```

### Удалить пользователя из друзей
```sh
POST /community/friends/{username}/remove
Authorization: Bearer <token>
```
Ответ:
```sh
{
  "message": "Successfully removed <username> from your friends list."
}
```


## Сообщения
### Получить все непрочитанные сообщения
```sh
GET /messages/unread
Authorization: Bearer <token>
```
Ответ:
```sh
{
  "chat_messages": [
    {
      "sender": "<username>",
      "content": "<message>"
    }
  ]
}
```

### Получить все непрочитанные сообщения от конкретного пользователя
```sh
GET /messages/user/{username}/unread
Authorization: Bearer <token>
```
Ответ:
```sh
[
  {
    "sender": "<username>",
    "content": "<message>"
  }
]
```

### Получить количество непрочитанных сообщений от конкретного пользователя
```sh
GET /messages/user/{username}/unread/count
Authorization: Bearer <token>
```
Ответ:
```sh
{
  "message": "4"
}
```

### Получить (скачать) историю сообщений с конкретным пользователем
```sh
GET /messages/user/{username}/all/download
Authorization: Bearer <token>
```
Ответ:
```sh
(<date, time>)[<username>]: <message1>
(<date, time>)[<username>]: <message2>
(<date, time>)[<username>]: <message3>
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
![2023-10-15_17-42-26](https://github.com/MehoiLs/relex-messenger/assets/129596462/15bec566-c6e9-4ef3-94b3-94e72cee21d1)
## Страница выбора собеседника ("/chat")
![2023-10-15_17-43-17](https://github.com/MehoiLs/relex-messenger/assets/129596462/c3a76943-6711-487f-9869-d8df7931c0be)
## Сообщение об ошибке (пользователя не существует)
![2023-10-15_17-43-53](https://github.com/MehoiLs/relex-messenger/assets/129596462/f51a4823-f231-4820-9df3-9b3dcae4eafa)
## Сообщение об ошибке (пользователь ограничил получение сообщений)
![2023-10-15_17-44-34](https://github.com/MehoiLs/relex-messenger/assets/129596462/505a1731-b447-48f7-bb1f-3978d7ce146e)
## Обмен сообщениями
![2023-10-15_17-46-43](https://github.com/MehoiLs/relex-messenger/assets/129596462/ffcb4b4d-4901-4561-9ffd-b9c7c5169bb3)
![2023-10-15_17-46-54](https://github.com/MehoiLs/relex-messenger/assets/129596462/ced63532-381d-4abc-8790-20ece8a23d55)


# Демонстрационное видео
https://drive.google.com/file/d/1eUTVXfElo1ZU6Wxo-lUNlmf23S5kg6YZ/view?usp=sharing


# Контакты
* Автор: Дорохов Михаил
* E-mail: m-d.3@ya.ru
* Telegram: @mehoil
