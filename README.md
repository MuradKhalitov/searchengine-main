# SearchEngine
Данный проект реализует поисковый движок, предоставляющий пользователю специальный
API со следующими основными функциями: 
<li>предварительное индексирование сайтов;</li>
<li>выдача основных сведений по сайтам;</li>
<li>поиск ключевых слов в проиндексированных сайтах и предоставление их пользователю.</li>

## Веб-страница
<p>
В проект также входит веб-страница, которая позволяет управлять процессами, реализованными
в движке.
<p>
Страница содержит три вкладки.

### Вкладка DASHBOARD
<img src="./readme_assets/dashboard.png" width="80%"/><p>
Эта вкладка открывается по умолчанию. На ней
отображается общая статистика по всем проиндексированным сайтам, а также
детальная статистика и статус по каждому из сайтов (статистика,
получаемая по запросу <i>/statistics</i>).<p>
<img src="./readme_assets/detailstatistics.png" width="80%"/><p>

### Вкладка MANAGEMENT
<img src="./readme_assets/management.png" width="80%"/><p>
На этой вкладке находятся инструменты управления 
поисковым движком — запуск (запрос <i>/startIndexing</i>) 
и остановка (запрос <i>/stopIndexing</i>) полной индексации
(переиндексации), а также возможность добавить (обновить)
отдельную страницу по ссылке (запрос <i>/indexPage/{pagePath}</i>).
Отметим, что если в последнем запросе присутствует только
URL сайта без завершающего слэша (/), как в приведённом выше
скриншоте, то индексироваться будет указанный сайт целиком.

### Вкладка SEARCH
<img src="./readme_assets/search.png" width="80%"/><p>
Эта вкладка предназначена для тестирования поискового
движка. На ней находится поле поиска и выпадающий список с
выбором сайта, по которому искать, а при нажатии на кнопку
<i>SEARCH</i> выводятся результаты поиска (по запросу /search).

## Файлы настройки
Данное приложение работает с СУБД MySQL.

### Раздел server
<p>
В этом разделе задаётся параметр <i>port</i> — порт, через который контроллеры 
приложения "слушают" веб-запросы. Задавая разные порты, можно, например, 
из разных папок, в которых находятся файлы настройки, запустить несколько 
экземпляров приложения.
<p>

### Раздел spring
<p>
Здесь задаются параметры СУБД, в которой приложение хранит 
данные конфигурации.Специфичные для выбранной
СУБД параметры 
<p>
Следует отметить важность параметра <i>spring.jpa.hibernate.ddl-auto</i>:
<li>СУБД MySQL. База данных создаётся на основе классов из пакета
<i>searchengine.model</i>. В том случае, когда параметр принимает значение 
<i>create</i>, при запуске приложения база данных пересоздаётся, 
то есть содержимое всех таблиц БД уничтожается. При следующих запусках
значение этого параметра следует установить в <i>update</i>.
</li>

### Раздел <i>logging</i>
Здесь можно задать уровень логирования <i>level.root</i> и имя
файла журнала <i>file.name</i>.

### Раздел config
На режим индексации влияют следующие параметры:
<li>
<i>maxPagesInSite</i> — когда количество страниц для данного сайта
достигает этого значения, индексация сайта останавливается.
Чаще всего ещё несколько страниц будет проиндексировано
после достижения <i>maxPagesInSite</i>.
</li>

#### Список <i>sites</i>
Здесь приведён список сайтов, которые программа будет
в состоянии индексировать. Каждый сайт характеризуется
следующими параметрами:
<li>
<i>url</i> — адрес сайта. Если в адресе присутствует страница
(как, например, в случае <i>https://et-cetera.ru/mobile</i>), то всё
равно индексирование начнётся с главной страницы (как если бы
в параметре было указано <i>https://et-cetera.ru</i>);
</li>
<li>
<i>name</i> — имя сайта. Оно, в частности, выводится в списке сайтов
на вкладке DASHBOARD;
</li>
<li>
pause — минимальный интервал обращения к одному сайту 
при индексировании, выраженный в миллисекундах. 
Параметр может принимать положительные значения и 0. 
При нуле задержек между обращениями не будет. 
</li>

## Используемые технологии
Приложение построено на платформе <i>Spring Boot</i>.
<p>Необходимые компоненты собираются с помощью фреймворка Maven.
Maven подключает следующие относящиеся к <i>Spring Boot</i> стартеры:
<li>
<i>spring-boot-starter-web</i> — подтягивает в проект библиотеки, 
необходимые для выполнения Spring-MVC функций приложения. При этом обмен
данными между браузером и сервером выполняется по технологии AJAX;
</li>
<li>
<i>spring-boot-starter-data-jpa</i> — отвечает за подключение библиотек,
требующихся для работы приложения с базой данных;
</li>
<li>
<i>spring-boot-starter-thymeleaf</i> — шаблонизатор веб-страницы программы.
</li>
<p>
Для загрузки и разбора страниц с сайтов используется библиотека <i>jsoup</i>.
<p>
Данная версия программы работает с СУБД MySQL. Для этого 
подключается зависимость <i>mysql-connector-java</i>.
<p>
Для удобства написания (и чтения) программного кода и для
расширения функциональности языка Java используется библиотека
Lombok (зависимость <i>lombok</i>).

## Запуск программы
Репозиторий с приложением SearchEngine находится по адресу
https://github.com/MuradKhalitov/searchengine-main.
<p>Если проект загрузить на локальный диск, то он готов к тому,
чтобы его можно было скомпилировать и запустить с помощью среды
разработки IntelliJ IDEA.
<p>
Перед первой компиляцией программы следует выполнить следующие шаги:
<ol>
<li>
Установить СУБД MySql.
</li>
<li>
В базе данных создать схему <i>search_engine</i>.
</li>
<li>
В схеме нужно создать пользователя <i>root</i> с паролем 
<i>Test1234</i>. Пользователь и пароль могут быть другими, это опять
же должно соответствовать параметрам <i>spring.datasource.username</i> и 
<i>spring.datasource.password</i> в файле <i>application.yaml</i>.
</li>
<li>
Установить параметры <i>jpa.hibernate.ddl-auto</i>, для первого запуска можно установить: "create", для дальнейшего можно установить "update".
</li>
<li>
Установить фреймворк Apache Maven, если он ещё не установлен.
</li>
<li>
  Если библиотеки с морфологией автоматически не подтянутся, то подключить библиотеки из папки lib.
</li>
</ol>

