package alt.third;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class EncryptionMessage1 {

    // Наш алфавит по которому всё кодируется, переводится(Здесь 128 символов).
    private static final List<Character> alphabet = Arrays.asList(
            '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '.', '/', '0', '1',
            '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[',
            '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', ' ', '…', '„', '€', '‹', '“', '”',
            '•', '–', '—', '™', '›', '§', '©', 'Є', '«', '¬', '®', '°', '»', '☺', '↨', '↑', '↓', '→', '←', '▲', '▼',
            '►', '◄', '‼', '¶', '○');

   /* private static final List<Character> alphabet = Arrays.asList(
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
            'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'а', 'б', 'в', 'г', 'д', 'е',
            'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х',
            'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я', 'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё',
            'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц',
            'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я'
    );*/

    private static String openText = null; // Открытый текст
    private static String binaryOpenText = null; // Открытый текст в бинарном виде
    private static String kriptogramma = null; // Криптограмма
    private static String binaryKriptogramma = null; // Криптограмма в бинарном виде
    private static Integer menuItem = 0; // Выбор пункта меню в callMenu()
    private static final Scanner sc = new Scanner(System.in); // Средство для ввода информации через консоль

    public static void main(String[] args) {
        callMenu(); // Вызываем обычное диалоговое меню с выбором

        // В зависимости от выбора выполняем операции
        switch (menuItem) {
            case 1 -> encryption(); // Шифрование сообщения
            case 2 -> decryption(); // Дешифрование сообщения
        }


    }

    /**
     * Метод для первого пункта меню - Шифрование сообщения по алгоритму:
     * 1) Считывание криптограммы из файла
     * 2) Считывание начального состояния клеточного автомата из файла
     * 3) Перевод криптограммы в двоичный вид
     * 4) Запись начального состояния клеточного автомата в двумерный массив размерностью 3 на Х, где Х - делится на 3
     * без остатка
     * 5) Считывание количество раундов шифрования
     * 6) Из первого файла в список записываются всевозможные текущие окрестности клетки
     * 7) Из второго файла в список записываются новые состояния клеток
     * 8) Дешифрование сообщения n раз, где n - количество раундов шифрования
     * 9) Запись конечного состояния двумерного клеточного автомата из массива в строку
     * 10) Операция xor между клеточным автоматом, выписанным в строку, и блоками криптограммы в двоичном виде
     * 11) Далее происходит перевод сообщения в символьное представление
     * 12) Запись в файл (полученное сообщение)
     */
    private static void decryption() {
        // 1) Считывание криптограммы из файла
        kriptogramma = getInfoFromFile("message.txt").toString();
        // 2) Считывание начального состояния клеточного автомата из файла
        StringBuilder initStateCellAut = getInfoFromFile("initStateOfCellAut.txt");
        // 3) Перевод криптограммы в двоичный вид
        binaryKriptogramma = convertTextToBinarySequence(kriptogramma);

        // Если длина начального состояния клеточного автомата больше длины криптограммы в двоичном виде,
        // то программа выведет соответствующее сообщение об ошибке и прекратит свою работу
        if (binaryKriptogramma.length() < initStateCellAut.length()) {
            System.out.println("Ошибка: длина введённого клеточного автомата больше, чем длина двоичного представления криптограммы");
            System.exit(0);
        }

        int lengthInitStateCellAut = initStateCellAut.length(); // Применяется очень много раз, чтобы постоянно не вычислять

        // Если длина начального состояния клеточного автомата не удовлетворяет размерности массива(3 на Х),
        // то в начало автомата записывается необходимое количество нулей;
        while ((lengthInitStateCellAut % 3 != 0) || ((lengthInitStateCellAut / 3) % 3 != 0)) {
            initStateCellAut.insert(0, "0");
            lengthInitStateCellAut++;
        }

        // 4) Запись начального состояния клеточного автомата в двумерный массив размерностью 3 на Х, где Х -
        // делится на 3 без остатка
        String[][] cellAutArray = new String[3][lengthInitStateCellAut / 3];
        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < lengthInitStateCellAut / 3; j++) {
                cellAutArray[i][j] = String.valueOf(initStateCellAut.charAt(index++));
            }
        }

        // 5) Считывание количество раундов шифрования
        System.out.print("Введите количество раундов шифрования: ");
        int numberOfRounds = sc.nextInt();

        // 6) Из первого файла в список записываются всевозможные текущие окрестности клетки
        ArrayList<String> currentPattern = getPatternsFromFile("currentPattern.txt");

        // 7) Из второго файла в список записываются новые состояния клеток
        ArrayList<String> newPattern = getPatternsFromFile("newStates.txt");

        if (currentPattern.isEmpty()) {
            System.out.println("Файл currentPattern.txt пуст");
            System.exit(-1);
        }
        if (newPattern.isEmpty()) {
            System.out.println("Файл newStates.txt пуст");
            System.exit(-1);
        }


        System.out.println("Правила развития клеточного автомата: ");
        System.out.println(currentPattern);
        System.out.println(newPattern);


        // Количество автоматов 3 на 3
        int numberOfBlocks = lengthInitStateCellAut / 9;

        // 8) Дешифрование сообщения numberOfRounds раз, где numberOfRounds - количество раундов шифрования
        for (int i = 1; i < numberOfRounds + 1; i++) {
            // tmpIndex - индекс, чтобы бегать по блокам (у нас есть автомат 3 на Х, мы делим его на блоки 3 на 3 и начи
            // наем обрабатывать. Так вот этот индекс и помогает бегать по блокам)
            for (int j = 0, tmpIndex = 0; j < numberOfBlocks; j++, tmpIndex += 3) {
                // Получаем окрестность. Далее мы её сверим в currentPattern для получения индекса
                String block =
                        (cellAutArray[0][tmpIndex] + cellAutArray[0][tmpIndex + 1] + cellAutArray[0][tmpIndex + 2] +
                                cellAutArray[1][tmpIndex] + cellAutArray[1][tmpIndex + 1] + cellAutArray[1][tmpIndex + 2] +
                                cellAutArray[2][tmpIndex] + cellAutArray[2][tmpIndex + 1] + cellAutArray[2][tmpIndex + 2]);

                // Здесь получаем новое состояние для автомата по индексу окрестности в currentPattern
                block = newPattern.get(currentPattern.indexOf(block));

                // в двумерном массиве происходит замена элементов согласно элементу списка новых состояний
                cellAutArray[0][tmpIndex] = String.valueOf(block.charAt(0));
                cellAutArray[0][tmpIndex + 1] = String.valueOf(block.charAt(1));
                cellAutArray[0][tmpIndex + 2] = String.valueOf(block.charAt(2));
                cellAutArray[1][tmpIndex] = String.valueOf(block.charAt(3));
                cellAutArray[1][tmpIndex + 1] = String.valueOf(block.charAt(4));
                cellAutArray[1][tmpIndex + 2] = String.valueOf(block.charAt(5));
                cellAutArray[2][tmpIndex] = String.valueOf(block.charAt(6));
                cellAutArray[2][tmpIndex + 1] = String.valueOf(block.charAt(7));
                cellAutArray[2][tmpIndex + 2] = String.valueOf(block.charAt(8));
            }
            System.out.println("Раунд №" + i + " шифрования: " + Arrays.deepToString(cellAutArray));
        }

        // 9) Запись конечного состояния двумерного клеточного автомата из массива в строку
        String cellAutString = convertCellAutArrayToString(cellAutArray, lengthInitStateCellAut);

        // 10) Операция xor между клеточным автоматом, выписанным в строку, и блоками криптограммы в двоичном виде
        binaryOpenText = xor(cellAutString, binaryKriptogramma);

        // 11) Далее происходит перевод сообщения в символьное представление
        openText = convertBinarySequenceToText(binaryOpenText);

        // 12) Запись в файл (полученное сообщение)
        Path file = Paths.get("message1.txt");
        try {
            Files.write(file, Collections.singleton(openText), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Криптограмма: " + kriptogramma);
        System.out.println("Сообщение: " + openText);
    }

    /**
     * Метод для первого пункта меню - Шифрование сообщения по алгоритму:
     * 1) Считывание открытого текста из файла
     * 2) Считывание начального состояния клеточного автомата из файла
     * 3) Перевод открытого текста в двоичный вид
     * 4) Запись начального состояния клеточного автомата в двумерный массив размерностью 3 на Х, где Х - делится на 3
     * без остатка
     * 5) Считывание количество раундов шифрования
     * 6) Из первого файла в список записываются всевозможные текущие окрестности клетки
     * 7) Из второго файла в список записываются новые состояния клеток
     * 8) Шифрование сообщения n раз, где n - количество раундов шифрования
     * 9) Запись конечного состояния двумерного клеточного автомата из массива в строку
     * 10) Операция xor между клеточным автоматом, выписанным в строку, и блоками открытого текста в двоичном виде
     * 11) Перевод криптограммы в символьное представление
     * 12) Запись в файл (итоговая криптограмма)
     */
    private static void encryption() {
        // 1) Считывание открытого текста из файла
        openText = getInfoFromFile("openText.txt").toString();

        // 2) Считывание начального состояния клеточного автомата из файла
        StringBuilder initStateCellAut = getInfoFromFile("initStateOfCellAut.txt");

        // 3) Перевод открытого текста в двоичный вид
        binaryOpenText = convertTextToBinarySequence(openText);

        int lengthInitStateCellAut = initStateCellAut.length(); // Применяется очень много раз, чтобы постоянно не вычислять

        // Если длина начального состояния клеточного автомата больше длины открытого текста в двоичном виде,
        // то программа выведет соответствующее сообщение об ошибке и прекратит свою работу
        if (binaryOpenText.length() < lengthInitStateCellAut) {
            System.out.println("Ошибка: длина введённого клеточного автомата больше, чем длина двоичного представления открытого текста");
            System.exit(0);
        }

        // Если длина начального состояния клеточного автомата не удовлетворяет размерности массива,
        // то в начало автомата записывается необходимое количество нулей
        while ((lengthInitStateCellAut % 3 != 0) || ((lengthInitStateCellAut / 3) % 3 != 0)) {
            initStateCellAut.insert(0, "0");
            lengthInitStateCellAut++;
        }

        // 4) Запись начального состояния клеточного автомата в двумерный массив размерностью 3 на Х, где Х -
        // делится на 3 без остатка
        String[][] cellAutArray = new String[3][lengthInitStateCellAut / 3];
        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < lengthInitStateCellAut / 3; j++) {
                cellAutArray[i][j] = String.valueOf(initStateCellAut.charAt(index++));
            }
        }

        // 5) Считывание количество раундов шифрования
        System.out.print("Введите количество раундов шифрования: ");
        int numberOfRounds = sc.nextInt();

        // 6) Из первого файла в список записываются всевозможные текущие окрестности клетки
        ArrayList<String> currentPattern = getPatternsFromFile("currentPattern.txt");
        // 7) Из второго файла в список записываются новые состояния клеток
        ArrayList<String> newPattern = getPatternsFromFile("newStates.txt");

        if (currentPattern.isEmpty()) {
            System.out.println("Файл currentPattern.txt пуст");
            System.exit(-1);
        }
        if (newPattern.isEmpty()) {
            System.out.println("Файл newStates.txt пуст");
            System.exit(-1);
        }


        System.out.println("Правила развития клеточного автомата: ");
        System.out.println(currentPattern);
        System.out.println(newPattern);


        // Количество автоматов 3х3
        int numberOfBlocks = lengthInitStateCellAut / 9;

        // 8) Шифрование сообщения n раз, где n - количество раундов шифрования
        for (int i = 1; i < numberOfRounds + 1; i++) {
            // tmpIndex - индекс, чтобы бегать по блокам (у нас есть автомат 3 на Х, мы делим его на блоки 3 на 3 и начи
            // наем обрабатывать. Так вот этот индекс и помогает бегать по блокам)
            for (int j = 0, tmpIndex = 0; j < numberOfBlocks; j++, tmpIndex += 3) {
                // Получаем окрестность. Далее мы её сверим в currentPattern для получения индекса
                String block =
                        (cellAutArray[0][tmpIndex] + cellAutArray[0][tmpIndex + 1] + cellAutArray[0][tmpIndex + 2] +
                                cellAutArray[1][tmpIndex] + cellAutArray[1][tmpIndex + 1] + cellAutArray[1][tmpIndex + 2] +
                                cellAutArray[2][tmpIndex] + cellAutArray[2][tmpIndex + 1] + cellAutArray[2][tmpIndex + 2]);

                // Здесь получаем новое состояние для автомата по индексу окрестности в currentPattern
                block = newPattern.get(currentPattern.indexOf(block));

                // в двумерном массиве происходит замена элементов согласно элементу списка новых состояний
                cellAutArray[0][tmpIndex] = String.valueOf(block.charAt(0));
                cellAutArray[0][tmpIndex + 1] = String.valueOf(block.charAt(1));
                cellAutArray[0][tmpIndex + 2] = String.valueOf(block.charAt(2));
                cellAutArray[1][tmpIndex] = String.valueOf(block.charAt(3));
                cellAutArray[1][tmpIndex + 1] = String.valueOf(block.charAt(4));
                cellAutArray[1][tmpIndex + 2] = String.valueOf(block.charAt(5));
                cellAutArray[2][tmpIndex] = String.valueOf(block.charAt(6));
                cellAutArray[2][tmpIndex + 1] = String.valueOf(block.charAt(7));
                cellAutArray[2][tmpIndex + 2] = String.valueOf(block.charAt(8));
            }
            System.out.println("Раунд №" + i + " шифрования: " + Arrays.deepToString(cellAutArray));
        }

        // 9) Запись конечного состояния двумерного клеточного автомата из массива в строку
        String cellAutString = convertCellAutArrayToString(cellAutArray, lengthInitStateCellAut);

        // 10) Операция xor между клеточным автоматом, выписанным в строку, и блоками открытого текста в двоичном виде
        binaryKriptogramma = xor(cellAutString, binaryOpenText);

        // 11) Перевод криптограммы в символьное представление
        kriptogramma = convertBinarySequenceToText(binaryKriptogramma);

        // 12) Запись в файл (итоговая криптограмма)
        Path file = Paths.get("message.txt");
        try {
            Files.write(file, Collections.singleton(kriptogramma), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Открытый текст: " + openText);
        System.out.println("Открытый текст в двоичном виде: " + binaryOpenText);
        System.out.println("Клеточный автомат в двоичном виде: " + cellAutString);
        System.out.println("Криптограмма: " + kriptogramma);


    }

    /**
     * Метод вызова меню
     */
    private static void callMenu() {
        System.out.println("Выберите, что нужно выполнить: ");
        System.out.println("1 - Шифрование сообщения;");
        System.out.println("2 - Дешифрование сообщения;");
        System.out.print("Выбор: ");
        do {
            menuItem = Integer.valueOf(sc.nextLine());
            if (menuItem != 1 && menuItem != 2) {
                System.out.print("\nПовторите ввод: ");
            }
        } while (menuItem != 1 && menuItem != 2);
    }

    /**
     * Метод для операции xor между между клеточным автоматом, выписанным в строку, и блоками открытого
     * текста в двоичном виде.
     *
     * @param cellAutArray Двумерный клеточный автомат, записанный в виде строки
     * @param binaryObject Либо бинарный открытый текст, либо бинарная криптограмма (зависит от того,
     *                     шифруем или дешифруем)
     * @return Результат операции xor: либо сообщение, либо криптограмма (зависит от того,
     * *                     шифруем или дешифруем)
     */
    private static String xor(String cellAutArray, String binaryObject) {

        int indexOpenText = 0;
        int indexBlocks = 0;

        StringBuilder binaryKriptogramma = new StringBuilder("");

        while (binaryObject.length() / cellAutArray.length() != indexBlocks) {
            for (int i = 0; i < cellAutArray.length(); i++) {
                binaryKriptogramma.append(Character.getNumericValue(cellAutArray.charAt(i)) ^ Character.getNumericValue(binaryObject.charAt(indexOpenText)));
                indexOpenText++;
            }
            indexBlocks += 1;
        }


        int diff = binaryObject.length() - binaryKriptogramma.length();
        for (int i = 0; i < diff; i++) {
            binaryKriptogramma.append(Character.getNumericValue(cellAutArray.charAt(i)) ^ Character.getNumericValue(binaryObject.charAt(indexOpenText)));
            indexOpenText++;
        }

        return binaryKriptogramma.toString();
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
     * Метод для конвертации клеточного автомата из массива в строку
     *
     * @param cellAutArray           Двумерный клеточный автомат, записанный в виде массива
     * @param lengthInitStateCellAut Длина начального состояния двумерного клеточного автомата
     * @return Двумерный клеточный автомат, записанный в виде строки
     */
    private static String convertCellAutArrayToString(String[][] cellAutArray, int lengthInitStateCellAut) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < lengthInitStateCellAut / 3; j++) {
                sb.append(cellAutArray[i][j]);
            }
        }
        return sb.toString();
    }


    /**
     * Метод взятия информация из файла.
     * Требования: Информация должна быть записана в виде строки, никаких пробелов и переносов строки. Не должно
     * содеражаться информации для нескольких полей (Нельзя хранить в файле, например, пароль и логин -> или только
     * логин, или только пароль).
     * Кроме самого взятия, также есть проверка инфорамции на пригодность.
     *
     * @param fileName Имя файла, из которого нужно взять информацию
     * @return Информация из файла в виде StringBuilder
     */
    private static StringBuilder getInfoFromFile(String fileName) {
        StringBuilder info = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                info.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (info.isEmpty()) {
            System.out.println("Ошибка: файл " + fileName + " пуст!");
            System.exit(0);
        }

        for (int i = 0; i < info.length(); i++) {
            if (!alphabet.contains(info.charAt(i))) {
                System.out.println("Недопустимые символы в файле " + fileName);
                System.exit(0);
            }
        }
        return info;
    }

    /**
     * Метод взятия состояний из файла.
     * Требования: Информация должна быть записана в виде строк. На одной строке - одно состояние.
     *
     * @param fileName Имя файла, из которого нужно взять информацию
     * @return Информация из файла в виде списка состояний
     */
    private static ArrayList<String> getPatternsFromFile(String fileName) {
        ArrayList<String> arrayList = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                arrayList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arrayList;
    }


}
