package ua.kiev.prog;

import javax.persistence.*;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("JPATest");
            EntityManager em = emf.createEntityManager();
            try {
                while (true) {
                    System.out.println("1: add new Menu item");
                    System.out.println("2: show menu filtered by price");
                    System.out.println("3: show menu items with discount");
                    System.out.println("4: select menu items less then 1kg");
                    System.out.print("-> ");

                    String s = sc.nextLine();
                    switch (s) {
                        case "1":
                            addMenu(sc, em);
                            break;
                        case "2":
                            showFilteredByPrice(sc, em);
                            break;
                        case "3":
                            showDiscounted(em);
                            break;
                        case "4":
                            showOneKg(em);
                            break;
                        default:
                            return;
                    }
                }
            } finally {
                em.close();
                emf.close();
                sc.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addMenu(Scanner sc, EntityManager em) {
        System.out.print("Enter item name: ");
        String name = sc.nextLine();

        System.out.print("Enter price: ");
        String sPrice = sc.nextLine();
        Double price = Double.parseDouble(sPrice);

        System.out.print("Enter weight(g): ");
        String sWeight = sc.nextLine();
        int weight = Integer.parseInt(sWeight);

        System.out.print("Discount(%) :");
        String sDiscount = sc.nextLine();
        int discount = Integer.parseInt(sDiscount);

        em.getTransaction().begin();
        Menu menu = new Menu(name, price, weight, discount);
        em.persist(menu);
        em.getTransaction().commit();
    }

    private static void showFilteredByPrice(Scanner sc, EntityManager em) {
        System.out.print("From price: ");
        String sFromPrice = sc.nextLine();
        Double fromPrice = Double.parseDouble(sFromPrice);

        System.out.print("To price: ");
        String sToPrice = sc.nextLine();
        Double toPrice = Double.parseDouble(sToPrice);

        Query query = em.createQuery("SELECT m FROM Menu m WHERE m.price > :price1 AND m.price < :price2", Menu.class);
        query.setParameter("price1", fromPrice);
        query.setParameter("price2", toPrice);

        List<Menu> menuList = query.getResultList();

        for (Menu menu : menuList) {
            System.out.println(menu);
        }
    }

    private static void showDiscounted(EntityManager em) {
        try {
            Query query = em.createQuery("SELECT m FROM Menu m WHERE m.discount>0 ORDER BY m.discount DESC", Menu.class);
            List<Menu> menuList = query.getResultList();

            for (Menu menu : menuList) {
                System.out.println(menu);
            }
        } catch (NoResultException ex) {
            System.out.println("We don't have menu items with discounts!");
            return;
        }
    }

    private static void showOneKg(EntityManager em) {
        Query query = em.createQuery("SELECT m FROM Menu m ORDER BY RAND()", Menu.class);
        List<Menu> menuList = query.getResultList();
        int tSum = 0;

        for (Menu menu : menuList) {
            if (tSum <= 1000) {
                System.out.println(menu);
                tSum = tSum + menu.getWeight();
            }
        }
    }
}