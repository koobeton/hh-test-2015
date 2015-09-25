package task2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <b>2.&#160;Дробь.<br></b>
 * <p>Даны два числа: a и b. Найдите значение числа a/b, записанного в k-ичной системе счисления.
 * Если a/b — периодическая дробь, то период следует заключить в скобки.</p>
 *
 * <p>Пример входных данных:<br>
 * 1 2 8<br>
 * 1 12 10
 * </p>
 *
 * <p>Пример выходных данных:<br>
 * 0.4<br>
 * 0.08(3)
 * </p>
 *
 * <p><b>ВВОД:</b>
 * <ul>
 *     <li><b>&#1048;з файла</b><br>
 *         Наборы входных данных должны быть записаны в текстовом файле по строкам,
 *         каждый набор в отдельной строке. В строке данные разделяются пробелом:<br>
 *         <i><b>делимое делитель основание_системы_счисления</b></i><br>
 *         <u>Пример:</u><br>
 *         <i>1 2 8<br>
 *         1 12 10</i><br>
 *         &#1048;мя файла передается в программу первым аргументом.<br>
 *         <u>Пример:</u> <i>java Task2 filename</i>
 *     </li>
 *     <li><b>С клавиатуры</b><br>
 *         Если при запуске программы не указано имя файла, программа запрашивает данные у пользователя.
 *         В этом случае вводится только один набор данных.
 *     </li>
 * </ul>
 *
 * <p><b>ВЫВОД:</b>
 * <ul>
 *     <li>Основной вывод в <b>{@code System.out}</b><br>
 *         Ошибки в <b>{@code System.err}</b>
 *     </li>
 * </ul>
 * */
public class Task2 {

    /**
     * @param args Первый аргумент - имя файла, содержащего входные данные
     * */
    public static void main(String... args) {

        Converter converter = new Converter();

        //получаем входные данные
        List<int[]> dataList = getData(args);

        //переводим число в k-ичную систему счисления
        for (int[] data : dataList) {
            try {
                System.out.println(converter.divideToRadix(data[0], data[1], data[2]));
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private static List<int[]> getData(String... args) {
        List<int[]> result = new ArrayList<>();
        List<String[]> strings = new ArrayList<>();
        try {
            if (args.length != 0) {
                strings = getDataFromFile(args[0]);
            } else {
                strings.add(getDataFromKeyboard());
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
        for (String[] strArr : strings) {
            try {
                int a = Integer.parseInt(strArr[0]);
                int b = Integer.parseInt(strArr[1]);
                int k = Integer.parseInt(strArr[2]);
                result.add(new int[]{a, b, k});
            } catch (NumberFormatException e) {
                System.err.println("Error parsing integers from data: " +
                                Arrays.toString(strArr));
            }
        }
        return result;
    }

    private static List<String[]> getDataFromFile(String filename) throws IOException {
        List<String[]> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(" ");
                if (data.length == 3) {
                    result.add(data);
                } else {
                    throw new IllegalArgumentException("Error: Wrong parameters number for data: " +
                            Arrays.toString(data) + "\n" +
                            "Must be: (int)dividend (int)divisor (int)radix");
                }
            }
        }
        return result;
    }

    private static String[] getDataFromKeyboard() throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String[] result = new String[3];
            System.out.println("Enter data:");
            System.out.print("a = ");
            result[0] = br.readLine();
            System.out.print("b = ");
            result[1] = br.readLine();
            System.out.print("k = ");
            result[2] = br.readLine();
            return result;
        }
    }
}
