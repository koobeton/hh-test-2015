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

    private void removeZeroCoefficients() {
        Iterator<Integer> iterator = monomials.keySet().iterator();
        while (iterator.hasNext()) {
            if (monomials.get(iterator.next()) == 0) iterator.remove();
        }
    }

    /**
     * @return Текстовое представление полинома в развернутом виде
     * */
    @Override
    public String toString() {

        if (monomials.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();

        removeZeroCoefficients();
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
