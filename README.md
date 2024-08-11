# Проект Task Management Systems

## Описание

Этот проект представляет собой Spring Boot API, использующее PostgreSQL в качестве базы данных. Для упрощения развертывания и управления зависимостями проект использует Docker и Docker Compose.


## Требования

Перед тем как начать, убедитесь, что у вас установлены следующие инструменты:

- [Docker](https://www.docker.com/get-started) (для запуска контейнеров)

## Настройка

### 1. Склонируйте репозиторий
Если вы еще не клонировали репозиторий

### 2. Настройте переменные окружения
В файле docker-compose.yml указаны переменные окружения для подключения к базе данных. Вы можете изменить значения по умолчанию, если это необходимо.
```yml
services:
  app:
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/TaskManagementSystems
      SPRING_DATASOURCE_USERNAME: myuser
      SPRING_DATASOURCE_PASSWORD: mypassword
  db:
    environment:
      POSTGRES_DB: TaskManagementSystems
      POSTGRES_USER: myuser
      POSTGRES_PASSWORD: mypassword
```
### 3. Запустите Docker Compose
Для сборки и запуска контейнеров выполните следующую команду в корневой директории проекта:
```shell
docker-compose up --build
```
Команда --build заставляет Docker пересобрать образы перед запуском контейнеров, что гарантирует, что все изменения в исходном коде будут включены.
### 4. Проверьте работу приложения
После успешного запуска контейнеров, ваше Spring Boot приложение будет доступно по адресу:
```
http://localhost:8080
```
Swagger UI будет доступен по адресу:
```
http://localhost:8080/swagger-ui.html
```

База данных PostgreSQL будет доступна по адресу:
```
localhost:5432
```
### 5. Остановка и удаление контейнеров
Чтобы остановить и удалить все контейнеры, сети и тома, используйте:
```shell
docker-compose down
```
