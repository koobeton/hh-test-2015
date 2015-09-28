package task1;

import java.util.Iterator;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Представляет полином от одной переменной.
 * */
public class Polynomial {

    private String variable;
    private NavigableMap<Integer, Integer> monomials; //<степень, коэффициент>

    /**
     * @param variable Переменная полинома
     * */
    public Polynomial(String variable) {
        this.variable = variable;
        this.monomials = new TreeMap<>();
    }

    /**
     * Добавляет к этому полиному моном с указанными степенью и коэффициентом.
     * Если полином уже содержит моном указанной степени, коэффициенты мономов
     * складываются (с учетом знака).
     *
     * @param power Степень монома
     * @param coefficient Коэффициент монома
     *
     * @return Этот полином
     *
     * @see #add(Polynomial)
     * @see #subtract(int, int)
     * @see #subtract(Polynomial)
     * */
    public Polynomial add(int power, int coefficient) {
        if (monomials.containsKey(power)) {
            monomials.put(power, monomials.get(power) + coefficient);
        } else {
            monomials.put(power, coefficient);
        }
        return this;
    }

    /**
     * Добавляет к этому полиному другой полином.
     * Сложение коэффициентов при степенях полиномов происходит как описано в {@link #add(int, int)}.
     *
     * @param polynomial Добавляемый полином
     *
     * @return Этот полином
     *
     * @see #add(int, int)
     * @see #subtract(int, int)
     * @see #subtract(Polynomial)
     * */
    public Polynomial add(Polynomial polynomial) {
        for (Integer power : polynomial.monomials.keySet()) {
            this.add(power, polynomial.monomials.get(power));
        }
        return this;
    }

    /**
     * Вычитает из этого полинома моном с указанными степенью и коэффициентом.
     * Обработка коэффициентов при степенях полиномов происходит как описано в {@link #add(int, int)}.
     *
     * @param power Степень монома
     * @param coefficient Коэффициент монома
     *
     * @return Этот полином
     *
     * @see #add(int, int)
     * @see #add(Polynomial)
     * @see #subtract(Polynomial)
     * */
    public Polynomial subtract(int power, int coefficient) {
        return this.add(power, - coefficient);
    }

    /**
     * Вычитает из этого полинома другой полином.
     * Обработка коэффициентов при степенях полиномов происходит как описано в {@link #add(int, int)}.
     *
     * @param polynomial Вычитаемый полином
     *
     * @return Этот полином
     *
     * @see #add(int, int)
     * @see #add(Polynomial)
     * @see #subtract(int, int)
     * */
    public Polynomial subtract(Polynomial polynomial) {
        return this.add(multiply(new Polynomial(variable).add(0, -1), polynomial));
    }

    /**
     * Произведение двух полиномов. Степени мономов складываются, а их коэффициенты перемножаются.
     *
     * @param p1 Первый полином
     * @param p2 Второй полином
     *
     * @return Произведение двух полиномов
     *
     * @see #multiply(Polynomial)
     * @see #power(Polynomial, int)
     * @see #power(int)
     * */
    public static Polynomial multiply(Polynomial p1, Polynomial p2) {
        Polynomial result = new Polynomial(p1.variable);
        for (Integer p1Power : p1.monomials.keySet()) {
            for (Integer p2Power : p2.monomials.keySet()) {
                result.add(
                        p1Power + p2Power,
                        p1.monomials.get(p1Power) * p2.monomials.get(p2Power)
                );
            }
        }
        return result;
    }

    /**
     * Произведение этого полинома на другой полином.
     * Мономы обрабатываются как описано в {@link #multiply(Polynomial, Polynomial)}.
     *
     * @param polynomial Второй полином
     *
     * @return Произведение двух полиномов
     *
     * @see #multiply(Polynomial, Polynomial)
     * @see #power(Polynomial, int)
     * @see #power(int)
     * */
    public Polynomial multiply(Polynomial polynomial) {
        return multiply(this, polynomial);
    }


    /**
     * Возведение полинома в степень.
     * Мономы обрабатываются как описано в {@link #multiply(Polynomial, Polynomial)}.
     *
     * @param polynomial Полином
     * @param power Степень
     *
     * @return Полином в указанной степени
     *
     * @see #multiply(Polynomial, Polynomial)
     * @see #multiply(Polynomial)
     * @see #power(int)
     * */
    public static Polynomial power(Polynomial polynomial, int power) {
        return power == 0
                ? new Polynomial(polynomial.variable).add(0, 1)
                : power == 1
                ? polynomial
                : multiply(polynomial, power(polynomial, --power));
    }

    /**
     * Возведение этого полинома в степень.
     * Мономы обрабатываются как описано в {@link #multiply(Polynomial, Polynomial)}.
     *
     * @param power Степень
     *
     * @return Полином в указанной степени
     *
     * @see #multiply(Polynomial, Polynomial)
     * @see #multiply(Polynomial)
     * @see #power(Polynomial, int)
     * */
    public Polynomial power(int power) {
        return power(this, power);
    }

    private void removeZeroCoefficients() {
        Iterator<Integer> iterator = monomials.keySet().iterator();
        while (iterator.hasNext()) {
            if (monomials.get(iterator.next()) == 0) iterator.remove();
        }
    }

    /**
     * Текстовое представление полинома в развернутом виде.
     *
     * @return Текстовое представление полинома в развернутом виде
     * */
    @Override
    public String toString() {

        removeZeroCoefficients();
        if (monomials.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();

        //степень полинома
        int highestPower = monomials.lastKey();
        for (Integer power : monomials.descendingKeySet()) {
            int coefficient = monomials.get(power);
            //определяем знак коэффициента первого монома и последующих
            boolean firstMonomial = power == highestPower;
            if (firstMonomial) {
                sb.append(coefficient > 0 ? "" : "-");
            } else {
                sb.append(coefficient > 0 ? " + " : " - ");
            }
            //форматируем вывод мономов
            int absCoefficient = Math.abs(coefficient); //т.к. знак определен ранее
            if (power == 0) {
                sb.append(absCoefficient);
            } else if (power == 1 && absCoefficient == 1) {
                sb.append(variable);
            } else if (power == 1) {
                sb.append(absCoefficient).append(variable);
            } else if (absCoefficient == 1) {
                sb.append(variable).append("^").append(power);
            } else {
                sb.append(absCoefficient).append(variable).append("^").append(power);
            }
        }

        return sb.toString();
    }
}
