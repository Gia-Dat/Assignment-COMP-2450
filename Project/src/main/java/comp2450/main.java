package comp2450;

import comp2450.model.menuPrinter;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        System.out.println("Enter your name: ");
        var scan = new Scanner(System.in);
        String name = scan.nextLine();
        menuPrinter system = new menuPrinter(name);
        system.start();
    }
}
