# ktor-notes
REST API на Ktor для приложения с заметками.

## Стек
- Kotlin
- Ktor
- MongoDB
- JWT-аутентификация

## Функции
- Регистрация / вход пользователя
- Проверка валидности полей при регистрации
- Хэширование паролей
- Операции с заметками (доступны после авторизации): добавление, удаление, редактирование, получение одной заметки по id, получение всех заметок пользователя
- Проверка доступа -- пользователь не может увидеть / изменить не свою заметку
