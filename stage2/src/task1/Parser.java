package task1;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//TODO
public class Parser {

    private String source;
    private String variable;

    /**
     * @param source Выражение для разбора
     * */
    public Parser(String source) {
        this.source = source;
    }

    //TODO
    public List<String> parse() {

        spellCheck();

        return getPolynomial();
    }

    //TODO
    private List<String> getPolynomial() {
        List<String> result = new ArrayList<>();
        Scanner scanner = new Scanner(source);
        String pattern = "[-+]?[\\[0-9\\]*\\[A-Za-z\\]?]+\\^?[0-9]*";
        String match;
        while ((match = scanner.findInLine(pattern)) != null) {
            result.add(match);
        }
        return result;
    }

    private void spellCheck() {

        //убираем лишние символы
        source = source.replaceAll("[^A-Za-z0-9+-^()]", "");
        if (source.equals("")) return;

        //извлекаем переменную
        variable = source.replaceAll("[^A-Za-z]", "");
        String firstVar;
        if (variable.length() > 0
                && variable.matches((firstVar = String.valueOf(variable.charAt(0))) + "+")) {
            variable = firstVar;
        } else {
            throw new IllegalArgumentException("Syntax error: Statement must contain exactly one variable");
        }

        //проверяем количество скобок
        int count = source.replaceAll("[^(]", "").length() - source.replaceAll("[^)]", "").length();
        if (count != 0) {
            throw new IllegalArgumentException(
                    String.format("Syntax error: Missing %d %s parenthes%s '%s'",
                            Math.abs(count),
                            count > 0 ? "closing" : "opening",
                            Math.abs(count) == 1 ? "is" : "es",
                            count > 0 ? ")" : "("
                    )
            );
        }
    }
}
