## Инструкция

*Необходима Java версии 11 и выше*

1. [Скачать последнюю сборку](https://github.com/vldF/stella-labs/releases) или собрать её самостоятельно:
   1. Склонить репозиторий и сабмодуль с тестами:
      ```shell
      git clone --recurse-submodules https://github.com/vldF/stella-labs
      ```
   2. Собрать проект:
      ```shell
      ./gradlew build
      ```
   3. Сгенерируются архивы `./build/distributions/stella-labs.(tar|zip)`. 
      Выберите любой и распакуйте.
2. Воспользуйтесь скриптом внутри папки `bin` для своей ОС
3. Введите в консоль текст программы, а затем отправьте EOF (`ctrl+d`, на macos 
   иногда `command+z`)
4. Выведется или сообщение 'OK' если всё в порядке, или основная ошибка с описанием и кодом 
   возврата 1

## Тесты

Для запуска тестов можно воспользоваться пресетами запуска в IntelliJ Idea:

1. `Generate Tests` генерирует файл [StellaTests.kt](./src/test/kotlin/StellaTests.kt), 
   который позволяет запускать наборы тестов (все, для конкретных ошибок или по одному)
2. `Run Supported Tests` запускает поддерживаемые тесты. Список поддерживаемых расширений указывается в 
   [StellaTestsRunner.kt](./src/test/kotlin/StellaTestsRunner.kt)
3. `Run All Tests` запускает все доступные тесты

Тестовые данные находятся в git submodule `stella-tests`. После добавления новых необходимо запустить `Generate Tests`
для актуализации файла [StellaTests.kt](./src/test/kotlin/StellaTests.kt). 

Также, можно запустить тесты при помощи gradle:

1. Градл-задача runAllTests запускает все тесты
2. Задача runSupportedTests только поддерживаемые

## Поддерживаются

1. [X] Ядро языка Stella (логические типы, натуральные числа, функции)
2. [X] let-связывания
3. [X] приписывание типа (аннотация)
4. [X] единичный тип
5. [X] пары 
6. [X] записи
7. [X] типы-суммы и варианты
8. [X] рекурсия общего вида и оператор неподвижной точки
9. [X] встроенные списки

## Дополнительно

1. [X] natural literals
2. [X] вложенные функции
3. [X] проверка арности главной функции
