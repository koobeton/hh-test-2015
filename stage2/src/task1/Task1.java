package task1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * <b>1.&#160;Полином.</b><br>
 * <p>Дано выражение, содержащее скобки, операции сложения, вычитания, умножения,
 * возведения в константную степень и одну переменную, например: (x - 5)(2x^3 + x(x^2 - 9)).</p>
 *
 * <p>Представьте это выражение в развёрнутом виде, например: 3x^4 - 15x^3 - 9x^2 + 45x</p>
 *
 * <p><b>ВВОД:</b>
 * <ul>
 *     <li><b>&#1048;з файла</b><br>
 *         Выражения должны быть записаны в текстовом файле по строкам,
 *         каждое выражение в отдельной строке.<br>
 *         <u>Пример:</u><br>
 *         <i>(x - 5)(2x^3 + x(x^2 - 9))<br>
 *         3x^4 - 15x^3 - 9x^2 + 45x</i><br>
 *         &#1048;мя файла передается в программу первым аргументом.<br>
 *         <u>Пример:</u> <i>java Task1 filename</i>
 *     </li>
 *     <li><b>С клавиатуры</b><br>
 *         Если при запуске программы не указано имя файла, программа запрашивает данные у пользователя.
 *         В этом случае вводится только одно выражение.
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
public class Task1 {

    /**
     * @param args Первый аргумент - имя файла, содержащего входные данные
     * */
    public static void main(String... args) {

        Task1 instance = new Task1();

        //получаем входные данные
        List<String> dataList = getData(args);

        //разворачиваем выражение
        for (String data : dataList) {
            System.out.println(data);
        }
    }

    private static List<String> getData(String... args) {
        List<String> result = new ArrayList<>();
        try {
            if (args.length != 0) {
                result = getDataFromFile(args[0]);
            } else {
                result.add(getDataFromKeyboard());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    private static List<String> getDataFromFile(String filename) throws IOException {
        List<String> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                result.add(line);
            }
        }
        return result;
    }

    private static String getDataFromKeyboard() throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.print("Enter expression: ");
            return br.readLine();
        }
    }
}
