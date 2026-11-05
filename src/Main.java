import java.util.List;

/**
 * Demonstracja symulatora sieci sklepów z używanymi laptopami.
 * Kompilacja i uruchomienie (z katalogu projektu):
 * javac -encoding UTF-8 -d out src/*.java && java -cp out Main
 */
public class Main {
    public static void main(String[] args) {
        StoreManager manager = new StoreManager();

        section("1. Oddziały sieci");
        manager.addBranch("Warszawa Centrum", 4);
        manager.addBranch("Kraków Kazimierz", 3);
        manager.addBranch("Gdańsk Wrzeszcz", 2);
        manager.addBranch("Poznań Rynek", 2);
        manager.addBranch("Warszawa Centrum", 10);
        manager.addBranch("  ", 3);
        manager.addBranch("Łódź", 0);
        manager.summary();

        Branch warszawa = manager.getBranch("Warszawa Centrum");
        Branch krakow = manager.getBranch("Kraków Kazimierz");
        Branch gdansk = manager.getBranch("Gdańsk Wrzeszcz");
        Branch poznan = manager.getBranch("Poznań Rynek");

        section("2. Laptopy — konstrukcja, print() i compareTo");
        Laptop t480 = new Laptop(
                "ThinkPad T480", "Lenovo", LaptopCondition.WORKING, 2018, 1199.0,
                16, "Intel Core i5-8350U", 87, "PF-T480-2018");
        Laptop latitude = new Laptop(
                "Latitude 7490", "Dell", LaptopCondition.IN_TESTING, 2019, 999.0,
                8, "Intel Core i5-8365U", 92, "PF-L7490-2019");
        Laptop macbook = new Laptop(
                "MacBook Pro 13", "Apple", LaptopCondition.WORKING, 2017, 2499.0,
                8, "Intel Core i5-7360U", 78, "PF-MBP13-2017");
        Laptop elitebook = new Laptop(
                "EliteBook 840 G5", "HP", LaptopCondition.DAMAGED, 2018, 799.0,
                16, "Intel Core i5-8250U", 64, "PF-EB840-2018");
        Laptop t480SameIdentity = new Laptop(
                "ThinkPad T480", "Lenovo", LaptopCondition.DAMAGED, 2018, 500.0,
                8, "Intel Core i5-8350U", 40, "PF-T480-DUPLIKAT");
        Laptop t480Newer = new Laptop(
                "ThinkPad T480", "Lenovo", LaptopCondition.WORKING, 2020, 1399.0,
                16, "Intel Core i5-10210U", 95, "PF-T480-2020");
        Laptop zenbook = new Laptop(
                "ZenBook 14", "Asus", LaptopCondition.IN_REPAIR, 2021, 1899.0,
                16, "AMD Ryzen 5 5500U", 90, "PF-ZU14-2021");
        Laptop xps = new Laptop(
                "XPS 13", "Dell", LaptopCondition.IN_TESTING, 2020, 2199.0,
                16, "Intel Core i7-1165G7", 88, "PF-XPS13-2020");
        Laptop chromebook = new Laptop(
                "Chromebook 14", "Acer", LaptopCondition.WORKING, 2019, 499.0);
        Laptop vivobook = new Laptop(
                "VivoBook 15", "Asus", LaptopCondition.WORKING, 2022, 1599.0,
                16, "Intel Core i5-1235U", 100, "PF-VB15-2022");

        t480.print();
        System.out.println();
        latitude.print();
        System.out.println();
        System.out.println("compareTo ThinkPad 2018 vs Latitude 2019: " + t480.compareTo(latitude));
        System.out.println("compareTo ThinkPad 2018 vs kopia (ten sam model, marka, rok): "
                + t480.compareTo(t480SameIdentity) + " (equals=" + t480.equals(t480SameIdentity) + ")");
        System.out.println("compareTo ThinkPad 2018 vs ThinkPad 2020: " + t480.compareTo(t480Newer)
                + " — starszy (większy wiek) jest po młodszym");
        System.out.println("Wiek ThinkPada 2018: " + t480.age() + " lat, ThinkPada 2020: " + t480Newer.age() + " lat");

        section("3. Przyjęcia do oddziałów — addLaptop, duplikat, limit pojemności");
        warszawa.addLaptop(t480);
        warszawa.addLaptop(latitude);
        warszawa.addLaptop(macbook);
        warszawa.addLaptop(elitebook);
        warszawa.addLaptop(vivobook);
        warszawa.addLaptop(t480SameIdentity);
        warszawa.addLaptop(null);

        krakow.addLaptop(t480Newer);
        krakow.addLaptop(zenbook);
        krakow.addLaptop(xps);

        poznan.addLaptop(chromebook);
        System.out.println("Gdańsk po otwarciu jest pusty: " + gdansk.isEmpty());

        section("4. Puste oddziały, zapełnienie sieci i usunięcie oddziału");
        printBranches("findEmpty()", manager.findEmpty());
        manager.summary();
        poznan.summary();
        manager.removeBranch("Poznań Rynek");
        manager.removeBranch("Szczecin");
        printBranches("findEmpty() po usunięciu Poznania", manager.findEmpty());

        section("5. Podsumowanie półek, liczniki stanu, wartość");
        warszawa.summary();
        krakow.summary();
        System.out.println("Warszawa — sprawne: " + warszawa.countByCondition(LaptopCondition.WORKING)
                + ", uszkodzone: " + warszawa.countByCondition(LaptopCondition.DAMAGED)
                + ", w naprawie: " + warszawa.countByCondition(LaptopCondition.IN_REPAIR)
                + ", w testach: " + warszawa.countByCondition(LaptopCondition.IN_TESTING));
        System.out.printf("Wartość Warszawy: %.2f zł, Krakowa: %.2f zł, sieci: %.2f zł%n",
                warszawa.totalValue(), krakow.totalValue(), manager.totalValue());

        section("6. sortByName i sortByPrice — kopie, półka zostaje w kolejności przyjęcia");
        printLaptops("Warszawa alfabetycznie po modelu", warszawa.sortByName());
        printLaptops("Warszawa po cenie rosnąco", warszawa.sortByPrice());
        System.out.println("Kolejność na półce (bez zmian po sortowaniu):");
        warszawa.summary();

        section("7. search i searchPartial");
        Laptop found = warszawa.search("Latitude 7490");
        System.out.println("search(\"Latitude 7490\"): " + (found == null ? "brak" : found));
        System.out.println("search(\"Inspiron 15\"): " + warszawa.search("Inspiron 15"));
        System.out.println("search na pustym Gdańsku: " + gdansk.search("Latitude 7490"));
        printLaptops("searchPartial(\"Think\")", warszawa.searchPartial("Think"));
        printLaptops("searchPartial(\"Book\") — fragment modelu", warszawa.searchPartial("Book"));
        printLaptops("searchPartial(\"Dell\") — fragment marki", warszawa.searchPartial("Dell"));
        printLaptops("searchPartial(\"xyz\")", warszawa.searchPartial("xyz"));

        section("8. max() — Collections.max po cenie");
        System.out.println("Najdroższy w Warszawie: " + warszawa.max());
        System.out.println("Najdroższy w Krakowie: " + krakow.max());
        System.out.println("Najdroższy w pustym Gdańsku: " + gdansk.max());

        section("9. changeCondition i changeYear");
        warszawa.changeCondition(
                new Laptop("Latitude 7490", "Dell", LaptopCondition.DAMAGED, 2019, 1),
                LaptopCondition.WORKING);
        warszawa.changeCondition(vivobook, LaptopCondition.IN_REPAIR);
        System.out.println("Po zmianie — w testach: " + warszawa.countByCondition(LaptopCondition.IN_TESTING)
                + ", sprawne: " + warszawa.countByCondition(LaptopCondition.WORKING));
        warszawa.changeYear(elitebook, 2015);
        warszawa.changeYear(elitebook, 2015);
        warszawa.changeYear(elitebook, 1970);

        section("10. sellLaptop, removeLaptop i kolizja wieku");
        warszawa.sellLaptop(macbook);
        warszawa.sellLaptop(macbook);
        warszawa.addLaptop(new Laptop(
                "ThinkPad T480", "Lenovo", LaptopCondition.WORKING, 2016, 1099.0,
                8, "Intel Core i5-8250U", 80, "PF-T480-2016"));
        warszawa.changeYear(t480, 2016);
        warszawa.removeLaptop(elitebook);
        warszawa.removeLaptop(elitebook);
        warszawa.summary();

        section("11. transferLaptop");
        manager.transferLaptop(latitude, "Warszawa Centrum", "Gdańsk Wrzeszcz");
        manager.transferLaptop(zenbook, "Kraków Kazimierz", "Gdańsk Wrzeszcz");
        manager.transferLaptop(xps, "Kraków Kazimierz", "Gdańsk Wrzeszcz");
        krakow.addLaptop(new Laptop(
                "ThinkPad T480", "Lenovo", LaptopCondition.WORKING, 2018, 1250.0,
                16, "Intel Core i5-8350U", 85, "PF-T480-2018-KRK"));
        manager.transferLaptop(t480, "Warszawa Centrum", "Kraków Kazimierz");
        manager.transferLaptop(t480Newer, "Kraków Kazimierz", "Warszawa Centrum");
        manager.transferLaptop(latitude, "Warszawa Centrum", "Gdańsk Wrzeszcz");
        manager.transferLaptop(latitude, "Gdańsk Wrzeszcz", "Gdańsk Wrzeszcz");
        manager.transferLaptop(latitude, "Gdańsk Wrzeszcz", "Nieistniejący");
        printBranches("findEmpty() gdy żaden oddział nie jest pusty", manager.findEmpty());

        section("12. Stan końcowy");
        manager.summary();
        warszawa.summary();
        krakow.summary();
        gdansk.summary();
        System.out.printf("Łączna wartość sieci: %.2f zł%n", manager.totalValue());
        System.out.println("Najdroższy laptop w Gdańsku po transferach: " + gdansk.max());
        System.out.println("search(\"ThinkPad T480\") w Warszawie zwraca jeden rocznik: " + warszawa.search("ThinkPad T480"));
        printLaptops("Wszystkie ThinkPady w Warszawie (searchPartial)", warszawa.searchPartial("Think"));
        printLaptops("Gdańsk po cenie", gdansk.sortByPrice());
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }

    private static void printLaptops(String title, List<Laptop> laptops) {
        System.out.println(title + ":");
        if (laptops == null || laptops.isEmpty()) {
            System.out.println("  (brak)");
            return;
        }
        for (Laptop laptop : laptops) {
            System.out.println("  - " + laptop);
        }
    }

    private static void printBranches(String title, List<Branch> branches) {
        System.out.println(title + ":");
        if (branches.isEmpty()) {
            System.out.println("  (brak)");
            return;
        }
        for (Branch branch : branches) {
            System.out.println("  - " + branch.getBranchName());
        }
    }
}
