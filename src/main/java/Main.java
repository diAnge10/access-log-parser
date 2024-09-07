import java.util.Scanner;


public class Main {

        public static void main(String[] args) {
            System.out.println("Введите первое число");
            int number1 = new Scanner(System.in).nextInt();
            System.out.println("Введите второе число");
            int number2 = new Scanner(System.in).nextInt();
            int summ = number1 + number2;
            System.out.println("Сумма данных числа равна " + summ);
            int raznost = number1 - number2;
            System.out.println("Разность данных числа равна " + raznost);
            int proizv = number1 * number2;
            System.out.println("Произведение данных числа равно " + proizv);
            double chastnoe = (double) number1 / number2;
            System.out.println("Частное данных числа равно " + chastnoe);
        }
    }
