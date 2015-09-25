package task2;

import java.util.ArrayList;
import java.util.List;

/**
 * Представляет значение числа a/b, записанного в k-ичной системе счисления.
 * Если a/b — периодическая дробь, то период будет заключен в скобки.
 * */
public class Converter {

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
    public String divideToRadix(int dividend, int divisor, int radix) {
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
}
