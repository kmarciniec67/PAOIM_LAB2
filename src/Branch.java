import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Oddział (punkt sprzedaży) przechowujący laptopy do odsprzedaży.
 * Lista jest zadeklarowana jako {@link List}, a konkretną implementacją jest {@link ArrayList}:
 * oddziały są niewielkie, często czytamy je po indeksie i sortujemy kopie.
 */
public class Branch {
    private final String branchName;
    private final List<Laptop> laptopList;
    private final int maxCapacity;

    public Branch(String branchName, int maxCapacity) {
        if (branchName == null || branchName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nazwa oddziału jest wymagana.");
        }
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Pojemność oddziału musi być dodatnia.");
        }
        this.branchName = branchName.trim();
        this.maxCapacity = maxCapacity;
        this.laptopList = new ArrayList<>();
    }

    /**
     * Dodaje laptop. Duplikat (ten sam model, marka i wiek) jest odrzucany komunikatem na stdout.
     * Przekroczenie pojemności jest zgłaszane na stderr.
     */
    public boolean addLaptop(Laptop laptop) {
        if (laptop == null) {
            reportError("Nie można dodać laptopa do oddziału \"" + branchName + "\": brak obiektu.");
            return false;
        }
        if (laptopList.contains(laptop)) {
            System.out.println("Laptop już istnieje w oddziale \"" + branchName
                    + "\" (ten sam model, marka i wiek): " + laptop.getBrand() + " " + laptop.getModel()
                    + ", rok " + laptop.getYear() + ".");
            return false;
        }
        if (laptopList.size() >= maxCapacity) {
            reportError("Przekroczono pojemność oddziału \"" + branchName + "\" ("
                    + laptopList.size() + "/" + maxCapacity + "). Nie dodano: "
                    + laptop.getBrand() + " " + laptop.getModel() + ".");
            return false;
        }
        laptopList.add(laptop);
        System.out.println("Dodano do \"" + branchName + "\": " + laptop.getBrand() + " "
                + laptop.getModel() + " (" + laptop.getYear() + ").");
        return true;
    }

    /** Usuwa laptop z oddziału (utylizacja albo zwrot do dostawcy). */
    public boolean removeLaptop(Laptop laptop) {
        Laptop removed = take(laptop);
        if (removed == null) {
            System.out.println("Nie usunięto laptopa — brak go w oddziale \"" + branchName + "\".");
            return false;
        }
        System.out.println("Usunięto (utylizacja / zwrot do dostawcy) z \"" + branchName + "\": " + removed);
        return true;
    }

    /** Usuwa laptop w wyniku sprzedaży. */
    public boolean sellLaptop(Laptop laptop) {
        Laptop sold = take(laptop);
        if (sold == null) {
            System.out.println("Nie sprzedano — w oddziale \"" + branchName + "\" nie ma takiego laptopa.");
            return false;
        }
        System.out.printf("Sprzedano z \"%s\": %s %s za %.2f zł.%n",
                branchName, sold.getBrand(), sold.getModel(), sold.getPrice());
        return true;
    }

    public boolean changeCondition(Laptop laptop, LaptopCondition condition) {
        if (condition == null) {
            reportError("Nie zmieniono stanu: brak nowej wartości.");
            return false;
        }
        Laptop stored = find(laptop);
        if (stored == null) {
            System.out.println("Nie zmieniono stanu — brak laptopa w oddziale \"" + branchName + "\".");
            return false;
        }
        LaptopCondition previous = stored.getCondition();
        stored.setCondition(condition);
        System.out.println("Stan " + stored.getBrand() + " " + stored.getModel()
                + " w \"" + branchName + "\": " + previous + " -> " + condition + ".");
        return true;
    }

    public boolean changeYear(Laptop laptop, int year) {
        int index = indexOf(laptop);
        if (index < 0) {
            System.out.println("Nie zmieniono roku — brak laptopa w oddziale \"" + branchName + "\".");
            return false;
        }
        int currentYear = Year.now().getValue();
        if (year < 1980 || year > currentYear) {
            reportError("Nie zmieniono roku: " + year + " jest poza zakresem 1980–" + currentYear + ".");
            return false;
        }
        Laptop stored = laptopList.get(index);
        if (stored.getYear() == year) {
            System.out.println("Rok produkcji " + stored.getBrand() + " " + stored.getModel()
                    + " pozostaje " + year + ".");
            return true;
        }
        for (int i = 0; i < laptopList.size(); i++) {
            if (i == index) {
                continue;
            }
            Laptop other = laptopList.get(i);
            if (other.getModel().equals(stored.getModel())
                    && other.getBrand().equals(stored.getBrand())
                    && other.getYear() == year) {
                System.out.println("Nie zmieniono roku " + stored.getBrand() + " " + stored.getModel()
                        + ": w oddziale \"" + branchName + "\" jest już taki model, marka i wiek (rok " + year + ").");
                return false;
            }
        }
        int previous = stored.getYear();
        stored.setYear(year);
        System.out.println("Rok produkcji " + stored.getBrand() + " " + stored.getModel()
                + " w \"" + branchName + "\": " + previous + " -> " + year + ".");
        return true;
    }

    public int countByCondition(LaptopCondition condition) {
        if (condition == null) {
            return 0;
        }
        int count = 0;
        for (Laptop laptop : laptopList) {
            if (laptop.getCondition() == condition) {
                count++;
            }
        }
        return count;
    }

    /** Nowa lista posortowana alfabetycznie po modelu. Kolejność w oddziale zostaje bez zmian. */
    public List<Laptop> sortByName() {
        List<Laptop> sorted = new ArrayList<>(laptopList);
        Collections.sort(sorted, Comparator.comparing(Laptop::getModel));
        return sorted;
    }

    /** Nowa lista posortowana po cenie rosnąco. */
    public List<Laptop> sortByPrice() {
        List<Laptop> sorted = new ArrayList<>(laptopList);
        Collections.sort(sorted, (o1, o2) -> Double.compare(o1.getPrice(), o2.getPrice()));
        return sorted;
    }

    /**
     * Zwraca laptop o podanym modelu albo {@code null}.
     * Lista jest sortowana komparatorem po modelu, a wynik znajduje {@code Collections.binarySearch}.
     */
    public Laptop search(String model) {
        if (model == null || laptopList.isEmpty()) {
            return null;
        }
        List<Laptop> sorted = new ArrayList<>(laptopList);
        Comparator<Laptop> byModel = Comparator.comparing(Laptop::getModel);
        Collections.sort(sorted, byModel);
        int index = Collections.binarySearch(sorted, Laptop.modelKey(model), byModel);
        if (index < 0) {
            return null;
        }
        return sorted.get(index);
    }

    /** Wszystkie laptopy, których model albo marka zawiera podany fragment ({@code String.contains}). */
    public List<Laptop> searchPartial(String text) {
        List<Laptop> found = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return found;
        }
        for (Laptop laptop : laptopList) {
            if (laptop.getModel().contains(text) || laptop.getBrand().contains(text)) {
                found.add(laptop);
            }
        }
        return found;
    }

    public void summary() {
        System.out.println("--- Oddział: " + branchName + " (" + laptopList.size() + "/" + maxCapacity + ") ---");
        if (laptopList.isEmpty()) {
            System.out.println("Brak laptopów.");
            return;
        }
        int index = 1;
        for (Laptop laptop : laptopList) {
            System.out.println(index + ". " + laptop);
            index++;
        }
        System.out.printf("Łączna wartość: %.2f zł%n", totalValue());
    }

    /** Najdroższy laptop w oddziale. Pusta półka zwraca {@code null}. */
    public Laptop max() {
        if (laptopList.isEmpty()) {
            return null;
        }
        return Collections.max(laptopList, new Comparator<Laptop>() {
            @Override
            public int compare(Laptop o1, Laptop o2) {
                return Double.compare(o1.getPrice(), o2.getPrice());
            }
        });
    }

    public double totalValue() {
        double sum = 0;
        for (Laptop laptop : laptopList) {
            sum += laptop.getPrice();
        }
        return sum;
    }

    public boolean contains(Laptop laptop) {
        return laptop != null && laptopList.contains(laptop);
    }

    public boolean isEmpty() {
        return laptopList.isEmpty();
    }

    public boolean isFull() {
        return laptopList.size() >= maxCapacity;
    }

    public int size() {
        return laptopList.size();
    }

    public String getBranchName() {
        return branchName;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public double fillRatio() {
        return (double) laptopList.size() / maxCapacity;
    }

    /** Wyjmuje egzemplarz z półki i zwraca obiekt przechowywany w oddziale. */
    Laptop take(Laptop laptop) {
        int index = indexOf(laptop);
        if (index < 0) {
            return null;
        }
        return laptopList.remove(index);
    }

    /** Wkłada laptop już sprawdzony przez wywołującego (używane przy przeniesieniu). */
    void attach(Laptop laptop) {
        laptopList.add(laptop);
    }

    private Laptop find(Laptop laptop) {
        int index = indexOf(laptop);
        if (index < 0) {
            return null;
        }
        return laptopList.get(index);
    }

    private int indexOf(Laptop laptop) {
        if (laptop == null) {
            return -1;
        }
        return laptopList.indexOf(laptop);
    }

    /** Komunikat na stderr. flush utrzymuje kolejność względem stdout. */
    private static void reportError(String message) {
        System.out.flush();
        System.err.println(message);
        System.err.flush();
    }
}
