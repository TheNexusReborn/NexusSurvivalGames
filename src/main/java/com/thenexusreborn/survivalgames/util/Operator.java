package com.thenexusreborn.survivalgames.util;

public enum Operator {
    ADD("+") {
        public Number calculate(Number number1, Number number2) {
            if (number1 instanceof Integer && number2 instanceof Integer) {
                return number1.intValue() + number2.intValue();
            } else if (number1 instanceof Integer && number2 instanceof Double) {
                return number1.intValue() + number2.doubleValue();
            } else if (number1 instanceof Double && number2 instanceof Integer) {
                return number1.doubleValue() + number2.intValue();
            } else if (number1 instanceof Double && number2 instanceof Double) {
                return number1.doubleValue() + number2.doubleValue();
            }

            return 0;
        }
    }, SUBTRACT("-") {
        public Number calculate(Number number1, Number number2) {
            if (number1 instanceof Integer && number2 instanceof Integer) {
                return number1.intValue() - number2.intValue();
            } else if (number1 instanceof Integer && number2 instanceof Double) {
                return number1.intValue() - number2.doubleValue();
            } else if (number1 instanceof Double && number2 instanceof Integer) {
                return number1.doubleValue() - number2.intValue();
            } else if (number1 instanceof Double && number2 instanceof Double) {
                return number1.doubleValue() - number2.doubleValue();
            }

            return 0;
        }
    }, MULTIPLY("*") {
        public Number calculate(Number number1, Number number2) {
            if (number1 instanceof Integer && number2 instanceof Integer) {
                return number1.intValue() * number2.intValue();
            } else if (number1 instanceof Integer && number2 instanceof Double) {
                return number1.intValue() * number2.doubleValue();
            } else if (number1 instanceof Double && number2 instanceof Integer) {
                return number1.doubleValue() * number2.intValue();
            } else if (number1 instanceof Double && number2 instanceof Double) {
                return number1.doubleValue() * number2.doubleValue();
            }

            return 0;
        }
    }, DIVIDE("/") {
        public Number calculate(Number number1, Number number2) {
            if (number1 instanceof Integer && number2 instanceof Integer) {
                return number1.intValue() / number2.intValue();
            } else if (number1 instanceof Integer && number2 instanceof Double) {
                return number1.intValue() / number2.doubleValue();
            } else if (number1 instanceof Double && number2 instanceof Integer) {
                return number1.doubleValue() / number2.intValue();
            } else if (number1 instanceof Double && number2 instanceof Double) {
                return number1.doubleValue() / number2.doubleValue();
            }
            return 0;
        }
    };

    private final String symbol;

    Operator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    /**
     * Calculates the two numbers together
     *
     * @param number1 The first number to perform the operation on
     * @param number2 The second number in which the operation is used with
     * @return The result or 0 by default
     */
    public Number calculate(Number number1, Number number2) {
        return 0;
    }

    /**
     * Gets an operator based in an input. The input can have a +, -, * or / at the beginning. This method parses those
     *
     * @param input The input string.
     * @return The operator if the input contains an arithmetic symbol or null if it does not.
     */
    public static Operator getOperator(String input) {
        if (input.startsWith("+")) {
            return Operator.ADD;
        } else if (input.startsWith("-")) {
            return Operator.SUBTRACT;
        } else if (input.startsWith("*")) {
            return Operator.MULTIPLY;
        } else if (input.startsWith("/")) {
            return Operator.DIVIDE;
        }
        return null;
    }
}