# TCPFileTransferApp
Клиент-серверное приложение Java для передачи файлов по протоколу TCP. Сервер прослушивает указанный IP-адрес и порт, принимает фреймы с файлами (имя, размер, содержимое) и сохраняет их в указанной директории. При ошибке парсинга соединение закрывается, приложение ожидает нового соединения. Скорость передачи данных выводится в реальном времени.
Ключевые функции:

Поддержка передачи файлов любого типа через TCP.
Обработка фреймов с информацией о файле (имя, размер, содержимое).
Сохранение файлов в указанную пользователем директорию.
Логирование ошибок парсинга и закрытие соединения при ошибке.
Подсчёт и вывод скорости передачи данных в консоль.