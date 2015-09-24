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

        Task2 instance = new Task2();

        //получаем входные данные
        List<int[]> dataList = getData(args);

        //переводим число в k-ичную систему счисления
        for (int[] data : dataList) {
            try {
                System.out.println(instance.divideToRadix(data[0], data[1], data[2]));
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Перевод десятичного числа в другую систему счисления.
     *
     * @param dividend Делимое
     * @param divisor Делитель
     * @param radix Основание системы счисления
     *
     * @return Текстовое представление числа a/b, записанного в k-ичной системе счисления
     *
     * @throws IllegalArgumentException Если делитель равен 0 или основание системы счисления
     * меньше либо равно 0
     */
    String divideToRadix(int dividend, int divisor, int radix) {
        if (divisor == 0) {
            throw new IllegalArgumentException("Error: The second argument (divisor) " +
                    "can not be equal to 0 (division by 0)");
        }
        if (radix <= 0) {
            throw new IllegalArgumentException("Error: The third argument (radix) " +
                    "must be greater than 0 (division by 0)");
        }
        //для перевода отрицательных чисел учитываем знак
        String sign = dividend * divisor < 0 ? "-" : "";
        dividend = Math.abs(dividend);
        divisor = Math.abs(divisor);
        return sign + integerPartConvert(dividend / divisor, radix) +
                fractionalPartConvert(dividend, divisor, radix);
    }

    /**
     * Перевод целой части числа.
     * Для этого целая часть десятичного числа делится на основание новой системы счисления. Цифра,
     * полученная в остатке, есть последняя цифра k-ичной записи данного числа. Полученное
     * частное снова делится на k. Остаток будет предпоследней цифрой k-ичной записи числа, а
     * частное опять делится на k и т.д. Процесс деления прекращается, когда в частном получается
     * цифра, меньшая k, которая будет первой цифрой k-ичной записи данного числа.
     *
     * @param number Целая часть числа
     * @param radix Основание системы счисления
     *
     * @return Текстовое представление целой части числа в k-ичной системе счисления
     */
    String integerPartConvert(int number, int radix) {
        StringBuilder result = new StringBuilder();
        while (number >= radix) {
            result.insert(0, number % radix);
            number /= radix;
        }
        result.insert(0, number);
        return result.toString();
    }

    /**
     * Перевод дробной части числа.
     * Дробная часть десятичного числа умножается на основание новой системы счисления.
     * Целая часть полученного произведения является первой цифрой в k-ичной записи
     * дробной части числа. Далее умножаем на k дробную часть этого произведения, целая часть
     * результата будет второй цифрой дробной части и т.д.
     *
     * @param dividend Делимое
     * @param divisor Делитель
     * @param radix Основание системы счисления
     *
     * @return Текстовое представление дробной части числа в k-ичной системе счисления
     */
    String fractionalPartConvert(int dividend, int divisor, int radix) {
        //Для вычисления периода периодической дроби создаем список остатков от деления
        List<Integer> remainders = new ArrayList<>();

        //Остаток от деления
        int number = dividend % divisor;

        int count = 0;
        String result = "";

        //Перевод дробной части.
        //Если остаток от деления равен 0, то дробь конечна и перевод закончен.
        //В периодической дроби количесво различных остатков от деления не может превышать размер делителя.
        while (number != 0 && count < divisor) {
            result += number * radix / divisor;
            remainders.add(number);
            number = number * radix % divisor ;
            count++;
        }

        //Вычисление периода периодической дроби
        int start = 0;
        int end = 0;
        boolean found = false;
        search:
        while (start < remainders.size() - 1) {
            for (int i = start + 1; i < remainders.size(); i++) {
                //если найдены два одинаковых остатка, то соответствующие им и последующие цифры
                //в дроби также будут совпадать, т.е. начнется следующий период
                if (remainders.get(start).intValue() == remainders.get(i).intValue()) {
                    found = true;
                    end = i;
                    break search;
                }
            }
            start++;
        }

        //если дробь периодическая, то выделяем предпериод, а период заключаем в скобки
        if (found) {
            result = result.substring(0, start) +               //предпериод
                    "(" + result.substring(start, end) + ")";   //период
        }

        //возвращаем результат
        return result.equals("") ? result : "." + result;
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
