import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Sieć oddziałów. Kluczem mapy jest nazwa oddziału.
 * {@link LinkedHashMap} zachowuje kolejność dodania, więc podsumowanie jest stabilne.
 */
public class StoreManager {
    private final Map<String, Branch> branches = new LinkedHashMap<>();

    public Branch addBranch(String name, int capacity) {
        if (name == null || name.trim().isEmpty()) {
            reportError("Nie dodano oddziału: nazwa jest pusta.");
            return null;
        }
        String branchName = name.trim();
        if (branches.containsKey(branchName)) {
            System.out.println("Oddział \"" + branchName + "\" już istnieje.");
            return branches.get(branchName);
        }
        if (capacity <= 0) {
            reportError("Nie dodano oddziału \"" + branchName + "\": pojemność musi być dodatnia.");
            return null;
        }
        Branch branch = new Branch(branchName, capacity);
        branches.put(branchName, branch);
        System.out.println("Dodano oddział \"" + branchName + "\" (pojemność " + capacity + ").");
        return branch;
    }

    public boolean removeBranch(String name) {
        if (name == null) {
            System.out.println("Nie usunięto oddziału: brak nazwy.");
            return false;
        }
        Branch removed = branches.remove(name.trim());
        if (removed == null) {
            System.out.println("Nie znaleziono oddziału \"" + name + "\".");
            return false;
        }
        System.out.println("Usunięto oddział \"" + removed.getBranchName() + "\" (laptopów w środku: "
                + removed.size() + ").");
        return true;
    }

    public List<Branch> findEmpty() {
        List<Branch> empty = new ArrayList<>();
        for (Branch branch : branches.values()) {
            if (branch.isEmpty()) {
                empty.add(branch);
            }
        }
        return empty;
    }

    /** Nazwa oddziału i procentowe zapełnienie. */
    public void summary() {
        System.out.println("=== Sieć sklepów (" + branches.size() + " oddziałów) ===");
        if (branches.isEmpty()) {
            System.out.println("Brak oddziałów.");
            return;
        }
        for (Branch branch : branches.values()) {
            System.out.printf("Oddział: %s — zapełnienie %d/%d (%.1f%%)%n",
                    branch.getBranchName(),
                    branch.size(),
                    branch.getMaxCapacity(),
                    branch.fillRatio() * 100.0);
        }
    }

    /**
     * Przenosi laptop między oddziałami. Najpierw sprawdza miejsce i duplikat w celu,
     * dopiero potem zdejmuje laptop ze źródła — nieudany transfer nic nie usuwa.
     */
    public boolean transferLaptop(Laptop laptop, String fromBranch, String toBranch) {
        if (laptop == null) {
            reportError("Nie przeniesiono laptopa: brak obiektu.");
            return false;
        }
        Branch source = findBranch(fromBranch);
        Branch destination = findBranch(toBranch);
        if (source == null) {
            System.out.println("Nie przeniesiono laptopa: brak oddziału \"" + fromBranch + "\".");
            return false;
        }
        if (destination == null) {
            System.out.println("Nie przeniesiono laptopa: brak oddziału \"" + toBranch + "\".");
            return false;
        }
        if (source == destination) {
            System.out.println("Nie przeniesiono laptopa: źródło i cel to ten sam oddział.");
            return false;
        }
        if (!source.contains(laptop)) {
            System.out.println("Nie przeniesiono laptopa: nie ma go w oddziale \"" + source.getBranchName() + "\".");
            return false;
        }
        if (destination.contains(laptop)) {
            System.out.println("Nie przeniesiono laptopa: w \"" + destination.getBranchName()
                    + "\" jest już egzemplarz o tym samym modelu, marce i wieku.");
            return false;
        }
        if (destination.isFull()) {
            reportError("Nie przeniesiono laptopa: oddział \"" + destination.getBranchName() + "\" jest pełny ("
                    + destination.size() + "/" + destination.getMaxCapacity() + ").");
            return false;
        }
        Laptop moved = source.take(laptop);
        destination.attach(moved);
        System.out.println("Przeniesiono " + moved.getBrand() + " " + moved.getModel()
                + " (" + moved.getYear() + ") z \"" + source.getBranchName()
                + "\" do \"" + destination.getBranchName() + "\".");
        return true;
    }

    public double totalValue() {
        double sum = 0;
        for (Branch branch : branches.values()) {
            sum += branch.totalValue();
        }
        return sum;
    }

    public Branch getBranch(String name) {
        if (name == null) {
            return null;
        }
        return branches.get(name.trim());
    }

    private Branch findBranch(String name) {
        if (name == null) {
            return null;
        }
        return branches.get(name.trim());
    }

    /** Komunikat na stderr. flush utrzymuje kolejność względem stdout. */
    private static void reportError(String message) {
        System.out.flush();
        System.err.println(message);
        System.err.flush();
    }
}
