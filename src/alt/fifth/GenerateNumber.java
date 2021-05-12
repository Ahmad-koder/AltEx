package alt.fifth;

import java.time.LocalTime;
import java.util.Arrays;

public class GenerateNumber {

    public static void main(String[] args) {

        int[][] automat = new int[64][63];

        LocalTime now = LocalTime.now();
        int check = 0;
        int hour = now.getSecond();

        String bits = Integer.toBinaryString(hour);//Перевод секунд в двоичную форму счисления

        //Вставляем полученное двоичное число в первую строку автомата
        for (int i = 0; i < 64; i++) {
            if (check != bits.length()) {
                automat[0][i] = Character.getNumericValue(bits.charAt(check++));
            } else
                break;
        }

        //генерация всех последующих поколений по правилу
        for (int i = 1; i < automat.length; i++)
            for (int j = 0; j < automat[i].length; j++) {
                int first = (j - 1 + automat[i].length) % automat[i].length;
                int second = (j + 1 + automat[i].length) % automat[i].length;

                automat[i][j] = automat[i - 1][first] ^ (automat[i - 1][j] | automat[i - 1][second]);
            }

        int startNumber = ((int) (Math.random() * (63))); //рандомная точка старта числа в столбце

        int lenghtNumber = ((int) (Math.random() * (63 - startNumber))); //рандомная длина двоичного кода числа

        // Для длины числа 7: int lenghtNumber = 7;

        int[] number = new int[lenghtNumber + 1];

        //Копируем число из центрального столбца автомата
        for (int i = 0; i < lenghtNumber + 1; i++) {
            number[i] = automat[25][startNumber++];
        }

        StringBuilder newNumber = new StringBuilder();

        //образуем строку для удобного перевода
        for (int value : number)
            newNumber.append(Integer.toString(value));

        System.out.println(Long.parseLong(newNumber.toString(), 2));

    }
}