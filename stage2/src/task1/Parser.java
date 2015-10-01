package task1;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;

/**
 * Парсит выражение, содержащее скобки, операции сложения, вычитания, умножения,
 * возведения в константную степень и одну переменную и представляет это выражение в развёрнутом виде.
 * */
public class Parser {

    private enum Operation {
        ADD_SUBTRACT,           //сложение и вычитание монома
        ADD_PARENTHESIS,        //добавление скобок
        SUBTRACT_PARENTHESIS,   //вычитание скобок
        MULTIPLY,               //умножение
        MULTIPLY_MONOMIAL,      //умножение на моном
        PARENTHESIS_MULTIPLY,   //умножение скобки на моном
        POWER,                  //возведение в степень
        OPEN_PARENTHESIS,       //открывающая скобка
        CLOSE_PARENTHESIS       //закрывающая скобка
    }

    private static final String POLYNOMIAL_PATTERN = "[-+]?[0-9]*[A-Za-z]?\\^?[0-9]*";
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

    /**
     * Возвращает результат разбора выражения в виде полинома.
     *
     * @return Полином
     * */
    public Polynomial parse() {

        return source == null || source.length() == 0
                ? null
                : parseOperations();
    }

    /**
     * Парсит выражение по операциям.
     *
     * @return Полином
     *
     * @throws IllegalStateException Если выражение содержит некорректные символы или операции
     * */
    Polynomial parseOperations() {

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
                if (summary.isEmpty()) {
                    op = Operation.OPEN_PARENTHESIS;
                } else {
                    op = Operation.MULTIPLY;
                }
            } else if (symbol == ')') {
                if (nextSymbol == '^') {
                    op = Operation.POWER;
                } else if (Character.isLetterOrDigit(nextSymbol)) {
                    op = Operation.PARENTHESIS_MULTIPLY;
                } else {
                    op = Operation.CLOSE_PARENTHESIS;
                }
            } else if (symbol == '*') {
                if (nextSymbol == '(') {
                    charPointer++;
                    op = Operation.MULTIPLY;
                } else {
                    op = Operation.MULTIPLY_MONOMIAL;
                }
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
                case MULTIPLY:
                case MULTIPLY_MONOMIAL:
                    charPointer++;
                    if (summary.isEmpty()) {
                        throw new IllegalStateException("Operations stack is empty on symbol: " + symbol);
                    }
                    Polynomial operand1 = summary.pollLast();
                    Polynomial operand2;
                    if (op == Operation.MULTIPLY) {
                        operand2 = parseOperations();
                    } else {
                        monomial = new Scanner(source.substring(charPointer))
                                .findInLine(POLYNOMIAL_PATTERN);
                        charPointer += monomial.length();
                        operand2 = parseMonomial(monomial);
                    }
                    summary.add(Polynomial.multiply(operand1, operand2));
                    break;
                case PARENTHESIS_MULTIPLY:
                    charPointer++;
                    operand1 = new Polynomial(variable);
                    for (Polynomial polynomial : summary) {
                        operand1.add(polynomial);
                    }
                    monomial = new Scanner(source.substring(charPointer))
                            .findInLine(POLYNOMIAL_PATTERN);
                    charPointer += monomial.length();
                    operand2 = parseMonomial(monomial);
                    return Polynomial.multiply(operand1, operand2);
                case POWER:
                    charPointer += 2;
                    String power = new Scanner(source.substring(charPointer))
                            .findInLine("[-+]?[0-9]*");
                    charPointer += power.length();
                    Polynomial operand = new Polynomial(variable);
                    for (Polynomial polynomial : summary) {
                        operand.add(polynomial);
                    }
                    return Polynomial.power(operand, Integer.parseInt(power));
            }
        }

        Polynomial result = new Polynomial(variable);
        for (Polynomial polynomial : summary) {
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
