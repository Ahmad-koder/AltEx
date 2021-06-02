package alt.fifth;

import java.time.LocalTime;

public class GenerateNumber{


    public static void main(String[] args) {

        int [][] automat = new int[64][63];

        LocalTime now = LocalTime.now();

        int nanoSeconds = now.getNano();

        String bitsNanoSeconds = Integer.toBinaryString(nanoSeconds);


        for(int i = 0;i < bitsNanoSeconds.length(); i++)
            automat[0][i] = Character.getNumericValue(bitsNanoSeconds.charAt(i));


        for (int i = 1; i < automat.length; i++)
            for (int j = 0; j < automat[i].length; j++)
            {
                int first = (j - 1 + automat[i].length) % automat[i].length;
                int second = (j + 1 + automat[i].length) % automat[i].length;

                automat[i][j] = automat[i - 1][first] ^ (automat[i - 1][j] | automat[i - 1][second]);
            }

        int startNumber = (now.getSecond() + now.getHour()+ now.getMinute()) % (63);

        int lengthNumber = (now.getNano() / now.getSecond()) % (63-startNumber);


        //int lenghtNumber = 7;

        int [] number = new int [lengthNumber+1];

        for(int i = 0; i < number.length; i++) {
            number[i] = automat[25][startNumber++];
        }

        StringBuilder numberString = new StringBuilder();

        for (int value : number)
            numberString.append(Integer.toString(value));

        System.out.println(Long.parseLong(numberString.toString(), 2));

    }
}
