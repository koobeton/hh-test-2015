package task2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 2. Дробь
 * Даны два числа: a и b. Найдите значение числа a/b, записанного в k-ичной системе счисления.
 * Если a/b — периодическая дробь, то период следует заключить в скобки.
 *
 * Пример входных данных:
 * 1 2 8
 * 1 12 10
 *
 * Пример выходных данных:
 * 0.4
 * 0.08(3)
 * */
public class Task2 {

    public static void main(String... args) throws IOException {

        Task2 instance = new Task2();

        //читаем входные данные:
        //если при запуске программы первым аргументом указано имя файла со входными данными
        //- то из первой строки этого файла, иначе - вводим последовательно с клавиатуры
        String[] params;
        if (args.length != 0) {
            try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
                String line;
                if ((line = br.readLine()) == null) {
                    throw new IllegalArgumentException("Error: Wrong parameters number\n" +
                            "Must be: (int)dividend (int)divisor (int)radix");
                }
                params = line.split(" ", 3);
                if (params.length != 3) {
                    throw new IllegalArgumentException("Error: Wrong parameters number\n" +
                            "Must be: (int)dividend (int)divisor (int)radix");
                }
            } catch (IllegalArgumentException | IOException e) {
                System.err.println(e.getMessage());
                params = getFromKeyboard();
            }
        } else {
            params = getFromKeyboard();
        }

        int a = 0;
        int b = 0;
        int k = 0;

        //заполняем входные данные
        try {
            a = Integer.parseInt(params[0]);
            b = Integer.parseInt(params[1]);
            k = Integer.parseInt(params[2]);
            if (b == 0) {
                throw new IllegalArgumentException("Error: The second argument (divisor) " +
                        "can not be equal to 0 (division by 0)");
            }
            if (k <= 0) {
                throw new IllegalArgumentException("Error: The third argument (radix) " +
                        "must be greater than 0 (division by 0)");
            }
        } catch (NumberFormatException e) {
            System.err.println("Error: All arguments must be an integer");
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        //переводим число в k-ичную систему счисления
        System.out.println(instance.divideToRadix(a, b, k));
    }

    private static String[] getFromKeyboard() throws IOException {
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

    /*
     * Перевод десятичного числа в другую систему счисления
     */
    private String divideToRadix(int dividend, int divisor, int radix) {
        //для перевода отрицательных чисел учитываем знак
        String sign = dividend * divisor < 0 ? "-" : "";
        dividend = Math.abs(dividend);
        divisor = Math.abs(divisor);
        return sign + integerPartConvert(dividend / divisor, radix) +
                fractionalPartConvert(dividend, divisor, radix);
    }

    /*
     * Перевод целой части числа.
     * Для этого целая часть десятичного числа делится на основание новой системы счисления. Цифра,
     * полученная в остатке, есть последняя цифра k-ичной записи данного числа. Полученное
     * частное снова делится на k. Остаток будет предпоследней цифрой k-ичной записи числа, а
     * частное опять делится на k и т.д. Процесс деления прекращается, когда в частном получается
     * цифра, меньшая k, которая будет первой цифрой k-ичной записи данного числа.
     */
    private String integerPartConvert(int number, int radix) {
        StringBuilder result = new StringBuilder();
        while (number >= radix) {
            result.insert(0, number % radix);
            number /= radix;
        }
        result.insert(0, number);
        return result.toString();
    }

    /*
     * Перевод дробной части числа.
     * Дробная часть десятичного числа умножается на основание новой системы счисления.
     * Целая часть полученного произведения является первой цифрой в k-ичной записи
     * дробной части числа. Далее умножаем на k дробную часть этого произведения, целая часть
     * результата будет второй цифрой дробной части и т.д.
     */
    private String fractionalPartConvert(int dividend, int divisor, int radix) {
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
}
