package alt.first;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.*;

public class HashCodeBase {
    // Наш алфавит по которому всё кодируется, переводится(Здесь 128 символов).
    private static final List<Character> alphabet = Arrays.asList(
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
            'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'а', 'б', 'в', 'г', 'д', 'е',
            'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х',
            'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я', 'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё',
            'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц',
            'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я'
    );
    /*private static final List<Character> alphabet = Arrays.asList(
            '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '.', '/', '0', '1',
            '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[',
            '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '♫', '…', '„', '€', '‹', '“', '”',
            '•', '–', '—', '™', '›', '§', '©', 'Є', '«', '¬', '®', '°', '»', '☺', '↨', '↑', '↓', '→', '←', '▲', '▼',
            '►', '◄', '‼', '¶', '○');*/

    private static String login = null; // Логин
    private static String password = null; // Пароль
    private static String loginFromFile = null; // Логин из файла
    private static String hashCodeFromFile = null; // Хэш-код из файла
    private static String saltFromFile = null; // Соль из файла
    private static Boolean error = false; // Флаг для выявления ошибки
    private static Integer menuItem = 0; // Выбор пункта меню в callMenu()
    private static final Scanner sc = new Scanner(System.in); // Средство для ввода информации через консоль


    public static void main(String[] args) {
        callMenu(); // Вызываем обычное диалоговое меню с выбором

        // В зависимости от выбора выполняем операции
        switch (menuItem) {
            case 1 -> calculateHashCode(); // Вычисляем хэш-код по ведённым данным и пишем в файл
            case 2 -> compareHashCodes(); // Сравниваем хэш-код из файла с новым хэш-кодом
        }
    }


    /**
     * Метод вызова меню
     */
    private static void callMenu() {
        System.out.println("Выберите, что нужно выполнить: ");
        System.out.println("1 - Вычислить хэш-код;");
        System.out.println("2 - Сравнить логин и пароль с имеющимся хэш-кодом");
        System.out.print("Выбор: ");
        do {
            menuItem = Integer.valueOf(sc.nextLine());
            if (menuItem != 1 && menuItem != 2) {
                System.out.print("\nПовторите ввод: ");
            }
        } while (menuItem != 1 && menuItem != 2);
    }

    /**
     * Метод для первого пункта меню - Вычисление хэш-кода по алгоритму
     * 1) Сначала считываем и проверяем логин и пароль
     * 2) Переводим их(логин и пароль) в бинарный вид
     * 3) Создаём правило развития на основе логина
     * 4) Создание дополнительного списка развития клеточного автомата,где внесены всевозможные текущие окрестности клетки
     * 5) Получение списка развития клеточного автомата, где внесены новые состояния клеток с помощью кодов Вольфрама
     * 6) Генерация случайного слова – соли
     * 7) Переводим соль в бинарный вид
     * 8) Создаём начальное состояние автомата = пароль в бинарном виде + соль в бинарном виде
     * 9) Вычисление хэш-кода пароля + соли
     * 10) Получившееся состояние клеточного автомата переводится в символьное представление
     * 11) Запись в файл (логин, соль в символьном представлении и хэш-код в символьном представлении )
     */
    private static void calculateHashCode() {
        // 1) Сначала считываем и проверяем логин и пароль
        enterLogin();
        enterPassword();

        System.out.println("Ваш логин: " + login);
        System.out.println("Ваш пароль: " + password);

        // 2) Переводим их(логин и пароль) в бинарный вид
        String binaryLogin = convertTextToBinarySequence(login);
        String binaryPassword = convertTextToBinarySequence(password);

        // 3) Создаём правило развития на основе логина
        String binaryRule = createRule(binaryLogin);

        // 4) Создание дополнительного списка развития клеточного автомата,где внесены всевозможные текущие окрестности клетки
        List<String> currentPattern = Arrays.asList("111", "110", "101", "100", "011", "010", "001", "000");
        System.out.println("Текущее состояние: " + currentPattern);

        // 5) Получение списка развития клеточного автомата, где внесены новые состояния клеток с помощью кодов Вольфрама
        ArrayList<Character> newPattern = convertStringToArrayList(binaryRule);
        System.out.println("Новое состояние центральной клетки: " + newPattern);

        // 6) Генерация случайного слова – соли
        String salt = createSalt();
        System.out.println("Ваша соль: " + salt);

        // 7) Переводим соль в бинарный вид
        String binarySalt = convertTextToBinarySequence(salt);

        // 8) Создаём начальное состояние автомата = пароль в бинарном виде + соль в бинарном виде
        String initStateCellAut = binaryPassword + binarySalt;

        // 9) Вычисление хэш-кода пароля + соли
        String binaryHashCode = createHashCode(initStateCellAut, currentPattern, newPattern);

        // 10) Получившееся состояние клеточного автомата переводится в символьное представление
        String hashCode = convertBinarySequenceToText(binaryHashCode);
        System.out.println("Ваш хэш-код: " + hashCode);

        // 11) Запись в файл (логин, соль в символьном представлении и хэш-код в символьном представлении)
        Path file = Paths.get("resultsForAlg1.txt");
        try {
            Files.write(file, Collections.singleton((login + " " + salt + " " + hashCode)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод для второго пункта меню - Сравнение нового хэш-кода(на основе нового пароля для одинакового логина)
     * и хэш-кода из файла. Алгоритм получения нового хэш-кода описан в calculateHashCode().
     */
    private static void compareHashCodes() {
        // Берём информацию из файла
        getInfoFromFile();
        if (!error) {
            // Считываем логин и пароль с консоли
            enterLogin();
            enterPassword();

            // Если логины не совпали, то сразу закрываем программу с сообщением
            if (!login.equals(loginFromFile)) {
                System.out.println("Логин не совпадает с логином из файла");
                System.exit(0);
            }

            System.out.println("Ваш логин: " + login);
            System.out.println("Ваш пароль: " + password);

            // Логин и пароль в бинарный вид
            String binaryLogin = convertTextToBinarySequence(login);
            String binaryPassword = convertTextToBinarySequence(password);

            // Создание правила разивития
            String binaryRule = createRule(binaryLogin);
            // Создание соли
            String binarySalt = convertTextToBinarySequence(saltFromFile);

            // currentPattern - текущее состояние (все окрестности, которые только могут быть)
            List<String> currentPattern = Arrays.asList("111", "110", "101", "100", "011", "010", "001", "000");
            System.out.println("Текущее состояние: " + currentPattern);

            // newPattern - новое состояние центральной клетки (например currentPattern[0] соотвествует newPattern[0] и т.д)
            // Как в таблице для правил развитий Вольфрама
            ArrayList<Character> newPattern = convertStringToArrayList(binaryRule);
            System.out.println("Новое состояние центральной клетки: " + newPattern);


            // Создаём начальное состояние автомата = пароль в бинарном виде + соль в бинарном виде
            String initStateCellAut = binaryPassword + binarySalt;

            // Получаем hashcode в бинарном виде
            String binaryHashCode = createHashCode(initStateCellAut, currentPattern, newPattern);

            // Перевод хэш-кода в символьное представление
            String hashCode = convertBinarySequenceToText(binaryHashCode);
            System.out.println("Ваш хэш-код: " + hashCode);

            if (hashCode.equals(hashCodeFromFile)) {
                System.out.println("Хэш-коды равны!");
            } else {
                System.out.println("Хэш-коды не совпадают!");
            }

        }


    }


    /**
     * Метод создания хэш-кода. Идея в том, что берём окрестность точки( neighborhoodOfPoint), находим её(окрестность)
     * в currentPattern, берём соотвествующий currentPattern новое состояние клетки в newPattern (той самой клетки,
     * которой и смотрели окрестность). Заносим это новое состояние клетки в новое состояние автомата (newStateCellAut).
     * newStateCellAut - новое состояние клеточного автомата
     * neighborhoodOfPoint - окрестность точки, например "101" или "011" и тп.
     *
     * @param currentPattern   Текущее состояние (верхняя строчка кодов Вольфрама - все окрестности, которые только могут быть)
     * @param initStateCellAut Начальное состояние клеточного автомата
     * @param newPattern       Новое состояние центральных клеток
     * @return Хэш-код в бинарном представлении
     */
    private static String createHashCode(String initStateCellAut, List<String> currentPattern, ArrayList<Character> newPattern) {

        StringBuilder newStateCellAut = new StringBuilder();
        String neighborhoodOfPoint = "";

        // С помощью цикла мы можем обработать почти все окрестности, кроме первой и последней точек(иначе выйдем заграницу)
        for (int i = 0; i < initStateCellAut.length() - 2; i++) {
            // Берём окрестность рассматриваемой точки
            neighborhoodOfPoint = initStateCellAut.substring(i, i + 3);

            // Здесь смотрим этого соседа(neighborhoodOfPoint) в currentPattern. Далее берём новое состояние,
            // которое относилось к определенной currentPattern и заносим его в newStateCellAut
            newStateCellAut.append(newPattern.get(currentPattern.indexOf(neighborhoodOfPoint)));
        }

        // Чтобы обработать последний и первый бит: как бы заворачиваем нашу последовательность в кольцо
        // Например: "1101010". Мы обработали "11010", но не обработали конец 0 и начало 1 (см дальше)

        // Связываем препоследний и последний бит с первым битом (как кольцо), тем самым обрабатываем последний бит.
        // Если брать пример "1101010", то здесь neighborhoodOfPoint = "101"
        neighborhoodOfPoint = initStateCellAut.substring(initStateCellAut.length() - 2) + initStateCellAut.charAt(0);
        newStateCellAut.append(newPattern.get(currentPattern.indexOf(neighborhoodOfPoint)));

        // Связываем последний бит с первым и вторым битом, тем самым обрабатываем первый бит
        // Если брать пример "1101010", то здесь neighborhoodOfPoint = "011"
        neighborhoodOfPoint = initStateCellAut.substring(initStateCellAut.length() - 1) + initStateCellAut.charAt(0) + initStateCellAut.charAt(1);
        newStateCellAut.append(newPattern.get(currentPattern.indexOf(neighborhoodOfPoint)));

        return newStateCellAut.toString();
    }

    /**
     * Метод создания соли.
     * salt - наша соль.
     * Создаем с помощью рандомных чисел и нашего алфавита: alphabet[randomNumber] == есть какой-то символ;
     * По нашему условию длина соли должна быть (32 - длина пароля) (Напоминание: длина пароля не больше чем 32)
     *
     * @return Соль (String)
     */
    private static String createSalt() {
        StringBuilder salt = new StringBuilder("");
        for (int i = 0; i < 32 - password.length(); i++) {
            long randomNumber = generateRandomNumber();
            salt.append(alphabet.get((int) randomNumber));
        }
        return salt.toString();
    }

    /**
     * Метод для создания правила развития.
     * Логин в двоичном виде переводится в десятичное число , остаток от деления на 255 которого
     * выступает в роли номера правила развития клеточного автомата (нумерация правил идет от 0 до 255)
     *
     * @param binaryLogin Логин в бинарном представлении
     * @return Правило развития в двоичном представлении
     */
    private static String createRule(String binaryLogin) {
        BigInteger loginFromBinaryLogin = new BigInteger(convertBinaryNumberToDecimalNumber(binaryLogin));
        BigInteger ruleDecimalNumber = loginFromBinaryLogin.remainder(BigInteger.valueOf(255));
        System.out.println("Ваш номер правила развития: " + ruleDecimalNumber.toString());

        // Нам нужно получить правило в бинарной записи через 7 битов
        StringBuilder ruleBinaryNumber = new StringBuilder(ruleDecimalNumber.toString(2));
        while (ruleBinaryNumber.length() != 8) {
            ruleBinaryNumber.insert(0, "0");
        }
        return ruleBinaryNumber.toString();
    }

    /**
     * Метод генерации псевдослучайного числа;
     * Алгоритм на основе клеточных автоматов;
     * Работа данного алгоритма рассматривается отдельно, здесь лишь практическое применение
     *
     * @return Псевдослучайное число
     */
    private static long generateRandomNumber() {
        int[][] automat = new int[64][51];

        LocalTime now = LocalTime.now();
        int check = 0;
        int hour = now.getHour();

        String bits = Integer.toBinaryString(hour);

        for (int i = 0; i < 51; i++) {
            if (check != bits.length()) {
                automat[0][i] = Character.getNumericValue(bits.charAt(check++));
            } else
                break;
        }

        for (int i = 1; i < automat.length; i++) //генерация всех последующих поколений по правилу
            for (int j = 0; j < automat[i].length; j++) {
                int first = (j - 1 + automat[i].length) % automat[i].length;
                int second = (j + 1 + automat[i].length) % automat[i].length;

                automat[i][j] = automat[i - 1][first] ^ (automat[i - 1][j] | automat[i - 1][second]);
            }
        int lenghtNumber = 6; //рандомная длина двоичного кода числа
        int startNumber = ((int) (Math.random() * (64 - lenghtNumber))); //рандомная точка старта в столбце
        int[] number = new int[lenghtNumber + 1];
        for (int i = 0; i < lenghtNumber + 1; i++) {
            number[i] = automat[25][startNumber++];
        }
        StringBuilder newNumber = new StringBuilder();
        for (int value : number)
            newNumber.append(Integer.toString(value)); //образуем строку для удобного перевода
        return Long.parseLong(newNumber.toString(), 2);
    }


    /**
     * Метод разделения строки на список символов
     * Например: "Liza" -> ['L', 'i', 'z', 'a']
     *
     * @param string Строка для разделения
     * @return Cписок символов
     */
    private static ArrayList<Character> convertStringToArrayList(String string) {
        ArrayList<Character> arrayList = new ArrayList<Character>();
        for (int i = 0; i < string.length(); i++) {
            arrayList.add(string.charAt(i));
        }
        return arrayList;
    }

    /**
     * Метод для перевода двоичного числа в десятичное.
     *
     * @param binaryNumber Двоичное число (String)
     * @return Десятичное число (String)
     */
    private static String convertBinaryNumberToDecimalNumber(String binaryNumber) {
        BigInteger decimalNumber = BigInteger.valueOf(0L);
        long number = 0; // временная переменная
        for (int i = 0; i < binaryNumber.length(); i++) {
            // Здесь как мы и обычно переводим, например из числа 1010 будет  (1·2^3) или (0·2^2) или (1·2^1) или (0·2^0)
            // Счёт здесь слева направо, то есть степень уменьшается
            number = (long) (Character.getNumericValue(binaryNumber.charAt(i)) * Math.pow((double) 2, (double) (binaryNumber.length() - i - 1)));
            // Складываем в общую сумму, например 1010 = (1·2^3)+(0·2^2)+(1·2^1)+(0·2^0) = 10
            decimalNumber = decimalNumber.add(BigInteger.valueOf(number));
        }
        return decimalNumber.toString();
    }

    /**
     * Метод конвертации информации из двоичного представления в символьное представление, используя alphabet
     *
     * @param binarySequence информация в двоичном виде
     * @return Информация в символьное представлении
     */
    private static String convertBinarySequenceToText(String binarySequence) {
        StringBuilder text = new StringBuilder();
        String letter = "";
        for (int i = 0; i < binarySequence.length(); i += 7) {
            letter = convertBinaryNumberToDecimalNumber(binarySequence.substring(i, i + 7));
            text.append(alphabet.get(Integer.parseInt(letter)));
        }
        return text.toString();
    }

    /**
     * Метод для перевода информации из строки символов в двоичную последовательность используя alphabet
     * binaryNumber - Временная переменная, по сути код символова по нашему alphabet. Если длины кода не хватает до 7,
     * то он её дополнит до 7.
     *
     * @param text информация для перевода
     * @return переведённая информация в двоичную последовательность
     */
    private static String convertTextToBinarySequence(String text) {
        StringBuilder binaryInfo = new StringBuilder();
        StringBuilder binaryNumber = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            // Переводим символ в двоичный код, например 'b' = 1 (по нашей таблице alphabet)
            binaryNumber.append(Integer.toBinaryString(alphabet.indexOf(text.charAt(i))));

            // Если длина кода не 7, то дополним до 7, например 'b' = 1 -> 'b' = 0000001
            // Приписываем не значащие нули - слева.
            while (binaryNumber.length() != 7) {
                binaryNumber.insert(0, "0");
            }

            // Заносим сюда наше переведенное число, например 0000001
            binaryInfo.append(binaryNumber);

            // Очищаем нашу временную переменную для следующих итераций
            binaryNumber.setLength(0);

        }
        return binaryInfo.toString();
    }


    /**
     * Метод ввода логина c проверками
     */
    private static void enterLogin() {
        do {
            System.out.println("Введите свой логин");
            System.out.print("Login: ");
            login = sc.nextLine();
            checkLogin();
        } while (error);
    }

    /**
     * Метод ввода пароля с проверками
     */
    private static void enterPassword() {
        do {
            System.out.println("Введите свой пароль");
            System.out.print("Password: ");
            password = sc.nextLine();
            checkPassword();
        } while (error);
    }

    /**
     * Метод взятия информация из файла. Кроме самого взятия, также есть проверка инфорамации на пригодность.
     */
    private static void getInfoFromFile() {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("resultsForAlg1.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(" ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // data = [login, salt, hashcode]
        String[] data = sb.toString().split(" ");


        if (data[0].isEmpty()) {
            System.out.println("Ошибка, файл пуст!");
            System.exit(0);

        }
        if (data.length != 3) {
            System.out.println("Ошибка, не корректный файл!");
            System.exit(0);

        }
        loginFromFile = data[0];
        saltFromFile = data[1];
        hashCodeFromFile = data[2];
        checkInfoFromFile();
    }


    /**
     * Метод проверки логина на длину и допустимые символы.
     * Если есть какие-то неполадки, то error = true, иначе error = false
     */
    private static void checkLogin() {
        if (login != null) {
            if (login.isEmpty()) {
                System.out.println("Ошибка: логин не может быть пустым");
                error = true;
                return;
            }

            for (int i = 0; i < login.length(); i++) {
                if (!alphabet.contains(login.charAt(i))) {
                    System.out.println("Ошибка: недопустимые символы в логине");
                    error = true;
                    return;
                }
            }
        }
        error = false;
    }

    /**
     * Метод проверки пароля на длину и допустимые символы.
     * Если есть какие-то неполадки, то error = true, иначе error = false
     */
    private static void checkPassword() {
        if (password != null) {
            if (password.length() < 6 || password.length() > 32) {
                System.out.println("Неверная длина пароля (от 6 до 32 символов)");
                error = true;
                return;
            }

            for (int i = 0; i < password.length(); i++) {
                if (!alphabet.contains(password.charAt(i))) {
                    System.out.println("Недопустимые символы в пароле");
                    error = true;
                    return;
                }
            }
        }
        error = false;
    }

    /**
     * Метод проверки данных(логин, хэш-код, соль) из файла.
     * Если есть какие-то неполадки, выход из программы с кодом 0
     */
    private static void checkInfoFromFile() {
        if (loginFromFile != null) {
            if (loginFromFile.isEmpty()) {
                System.out.println("Ошибка: логин не может быть пустым.");
                System.exit(0);

            }

            for (int i = 0; i < loginFromFile.length(); i++) {
                if (!alphabet.contains(loginFromFile.charAt(i))) {
                    System.out.println("Ошибка: недопустимые символы в логине из файла.");
                    System.exit(0);

                }
            }
        }

        if (hashCodeFromFile != null) {
            for (int i = 0; i < hashCodeFromFile.length(); i++) {
                if (!alphabet.contains(hashCodeFromFile.charAt(i))) {
                    System.out.println("Ошибка: недопустимые символы в хэш-коде из файла.");
                    System.exit(0);


                }
            }

            if (hashCodeFromFile.length() != 32) {
                System.out.println("Ошибка: длина хэш-кода должна составлять 32 символа.");
                System.exit(0);

            }
        }

        if (saltFromFile != null) {
            for (int i = 0; i < saltFromFile.length(); i++) {
                if (!alphabet.contains(saltFromFile.charAt(i))) {
                    System.out.println("Ошибка: недопустимые символы в соли из файла.");
                    System.exit(0);

                }
            }
        }

    }
}
