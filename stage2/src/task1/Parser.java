package task1;

import java.util.Scanner;

//TODO
public class Parser {

    private static final String POLYNOMIAL_PATTERN = "[-+]?[\\[0-9\\]*\\[A-Za-z\\]?]+\\^?[0-9]*";
    private String source;
    private String variable;

    /**
     * @param source Выражение для разбора
     * */
    public Parser(String source) {
        spellCheck(source);
    }

    //TODO
    public Polynomial parse() {

        return parsePolynomial(source);
    }

    private Polynomial parsePolynomial(String string) {
        Polynomial result = new Polynomial(variable);
        Scanner scanner = new Scanner(string);
        String match;
        while ((match = scanner.findInLine(POLYNOMIAL_PATTERN)) != null) {
            result.add(parseMonomial(match));
        }
        return result;
    }

    private Polynomial parseMonomial(String monomial) {
        Polynomial result = new Polynomial(variable);
        int power;
        int coefficient;
        if (monomial.matches("[-+]?[0-9]*")) {
            power = 0;
            coefficient = new Scanner(monomial).nextInt();
        } else if (monomial.matches("[-+]?" + variable)) {
            power = 1;
            coefficient = monomial.matches("-" + variable) ? -1 : 1;
        } else if (monomial.matches("[-+]?[0-9]*" + variable)) {
            power = 1;
            coefficient = new Scanner(monomial).useDelimiter(variable).nextInt();
        } else if (monomial.matches("[-+]?" + variable + "\\^[0-9]*")) {
            power = new Scanner(monomial).useDelimiter("[-+]?" + variable + "\\^").nextInt();
            coefficient = monomial.matches("-.*") ? -1 : 1;
        } else if (monomial.matches("[-+]?[0-9]*" + variable + "\\^[0-9]*")) {
            Scanner scanner = new Scanner(monomial).useDelimiter(variable + "\\^");
            coefficient = scanner.nextInt();    //сначала коэффициент
            power = scanner.nextInt();          //затем степень
        } else {
            throw new IllegalArgumentException("Not a monomial: " + monomial);
        }
        result.add(power, coefficient);
        return result;
    }

    private void spellCheck(String rawSource) {

        //убираем лишние символы
        source = rawSource.replaceAll("[^A-Za-z0-9+-^()]", "");
        if (source.equals("")) return;

        //извлекаем переменную
        String vars = source.replaceAll("[^A-Za-z]", "");
        String firstVar;
        if (vars.length() > 0
                && vars.matches((firstVar = String.valueOf(vars.charAt(0))) + "+")) {
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
