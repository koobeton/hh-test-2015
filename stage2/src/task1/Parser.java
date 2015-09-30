package task1;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;

//TODO
public class Parser {

    private enum Operation {
        ADD_SUBTRACT,           //сложение и вычитание монома
        ADD_PARENTHESIS,        //добавление скобок
        SUBTRACT_PARENTHESIS,   //вычитание скобок
        MULTIPLY,               //умножение
        POWER,                  //возведение в степень
        OPEN_PARENTHESIS,       //открывающая скобка
        CLOSE_PARENTHESIS       //закрывающая скобка
    }

    private static final String POLYNOMIAL_PATTERN = "[-+]?[\\[0-9\\]*\\[A-Za-z\\]?]+\\^?[0-9]*";
    private String source;
    private String variable;
    private int charPointer;

    /**
     * @param source Выражение для разбора
     * */
    public Parser(String source) {
        spellCheck(source);
        charPointer = 0;
    }

    //TODO
    public Polynomial parse() {

        return source == null || source.length() == 0
                ? null
                : parseOperations();
    }

    private Polynomial parseOperations() {

        Deque<Polynomial> summary = new ArrayDeque<>();

        loop:
        while (charPointer < source.length()) {

            char symbol = source.charAt(charPointer);
            char nextSymbol = charPointer < source.length() - 1
                    ? source.charAt(charPointer + 1)
                    : 0;
            Operation op;

            if (symbol == '+' || symbol == '-' || Character.isLetterOrDigit(symbol)) {
                if (symbol == '+' && nextSymbol == '(') {
                    op = Operation.ADD_PARENTHESIS;
                } else if (symbol == '-' && nextSymbol == '(') {
                    op = Operation.SUBTRACT_PARENTHESIS;
                } else {
                    op = Operation.ADD_SUBTRACT;
                }
            } else if (symbol == '(') {
                op = Operation.OPEN_PARENTHESIS;
            } else if (symbol == ')') {
                if (nextSymbol == '^') {
                    op = Operation.POWER;
                } else {
                    op = Operation.CLOSE_PARENTHESIS;
                }
            } else if (symbol == '*') {
                op = Operation.MULTIPLY;
            } else throw new IllegalStateException("Fail on symbol: " + symbol);

            switch (op) {
                case ADD_SUBTRACT:
                    String monomial = new Scanner(source.substring(charPointer))
                            .findInLine(POLYNOMIAL_PATTERN);
                    charPointer += monomial.length();
                    summary.add(parseMonomial(monomial));
                    break;
                case ADD_PARENTHESIS:
                    charPointer += 2;
                    summary.add(parseOperations());
                    break;
                case SUBTRACT_PARENTHESIS:
                    charPointer += 2;
                    summary.add(Polynomial
                            .multiply(new Polynomial(variable).add(0, -1), parseOperations()));
                    break;
                case OPEN_PARENTHESIS:
                    charPointer++;
                    summary.add(parseOperations());
                    break;
                case CLOSE_PARENTHESIS:
                    charPointer++;
                    break loop;
                    //break;
                case POWER:
                    charPointer += 2;
                    String power = new Scanner(source.substring(charPointer))
                            .findInLine("[-+]?[0-9]*");
                    charPointer += power.length();
                    Polynomial result = new Polynomial(variable);
                    for (Polynomial polynomial : summary) {
                        result.add(polynomial);
                    }
                    return Polynomial.power(result, Integer.parseInt(power));
            }
        }

        Polynomial result = new Polynomial(variable);
        for (Polynomial polynomial : summary) {

            //TODO
            System.out.println(polynomial);

            result.add(polynomial);
        }

        return result;
    }

    /**
     * Парсит моном из строки.
     *
     * @param monomial Строка для разбора
     *
     * @return Полином
     *
     * @throws IllegalArgumentException Если строка не является корректной записью монома
     * */
    Polynomial parseMonomial(String monomial) {
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

    /**
     * Проверяет корректность выражения и парсит из него переменную.
     *
     * @param rawSource Выражение для проверки
     *
     * @throws IllegalArgumentException Если выражение некорректно
     * */
    void spellCheck(String rawSource) {

        //убираем лишние символы
        source = rawSource.replaceAll("[^A-Za-z0-9+-^()*]", "");
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
