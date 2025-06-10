import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

class PhoneNumber implements Serializable {
    String countryCode;
    String number;

    PhoneNumber(String countryCode, String number) {
        this.countryCode = countryCode;
        this.number = number;
    }

    @Override
    public String toString() {
        return "+" + countryCode + " " + number;
    }
}

class Contact implements Serializable {
    String name;
    ArrayList<PhoneNumber> phoneNumbers = new ArrayList<>();
    String email;

    Contact(String name, String email) {
        this.name = name;
        this.email = email;
    }

    void addPhoneNumber(String countryCode, String number) {
        phoneNumbers.add(new PhoneNumber(countryCode, number));
    }

    void display() {
        System.out.println("Name: " + name);
        System.out.print("Phone Numbers: ");
        for (PhoneNumber pn : phoneNumbers) {
            System.out.print(pn + "  ");
        }
        System.out.println();
        System.out.println("Email: " + email);
        System.out.println();
    }
}

public class PhoneBook implements Serializable {
    private static final String DATA_FILE = "contacts.dat";
    private ArrayList<Contact> contacts = new ArrayList<>();
    private static final HashMap<String, Integer> countryCodeLengths = new HashMap<>();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.\\w+$");

    static {
        countryCodeLengths.put("1", 10);
        countryCodeLengths.put("44", 10);
        countryCodeLengths.put("91", 10);
        countryCodeLengths.put("81", 10);
        countryCodeLengths.put("61", 9);
        // Add more country codes & lengths as needed
    }

    public static void main(String[] args) {
        PhoneBook pb = new PhoneBook();
        pb.loadContacts();

        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("1. Add Contact");
            System.out.println("2. Display Contacts (Sorted)");
            System.out.println("3. Search Contact (Partial Name)");
            System.out.println("4. Delete Contact");
            System.out.println("5. Update Contact");
            System.out.println("6. Display Contacts Grouped by Country Code");
            System.out.println("7. Export Contacts to CSV");
            System.out.println("8. Count Contacts");
            System.out.println("9. Exit");
            System.out.print("Enter your choice: ");

            while (!scanner.hasNextInt()) {
                System.out.print("Invalid input. Enter your choice: ");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    pb.addContact(scanner);
                    break;
                case 2:
                    pb.displaySorted();
                    break;
                case 3:
                    pb.searchContact(scanner);
                    break;
                case 4:
                    pb.deleteContact(scanner);
                    break;
                case 5:
                    pb.updateContact(scanner);
                    break;
                case 6:
                    pb.displayGroupedByCountryCode();
                    break;
                case 7:
                    pb.exportToCSV();
                    break;
                case 8:
                    System.out.println("Total contacts: " + pb.contacts.size() + "\n");
                    break;
                case 9:
                    pb.saveContacts();
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
                    break;
            }
        } while (choice != 9);

        scanner.close();
    }

    void addContact(Scanner scanner) {
        System.out.print("Enter Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            System.out.println("Invalid email format.");
            return;
        }

        Contact contact = new Contact(name, email);

        while (true) {
            System.out.print("Enter Country Code (e.g., 1 for USA): ");
            String countryCode = scanner.nextLine().trim();
            if (!countryCodeLengths.containsKey(countryCode)) {
                System.out.println("Unsupported country code. Try again.");
                continue;
            }

            int requiredLen = countryCodeLengths.get(countryCode);

            System.out.print("Enter Number (without country code): ");
            String number = scanner.nextLine().trim();

            if (number.length() != requiredLen || !number.matches("\\d+")) {
                System.out.println("Invalid number length or format for country code +" + countryCode + ". Required length: " + requiredLen);
                continue;
            }

            contact.addPhoneNumber(countryCode, number);

            System.out.print("Add another phone number? (y/n): ");
            String more = scanner.nextLine().trim();
            if (!more.equalsIgnoreCase("y")) break;
        }

        contacts.add(contact);
        System.out.println("Contact added successfully.\n");
    }

    void displaySorted() {
        if (contacts.isEmpty()) {
            System.out.println("No contacts to display.\n");
            return;
        }
        contacts.stream()
                .sorted(Comparator.comparing(c -> c.name.toLowerCase()))
                .forEach(Contact::display);
    }

    void searchContact(Scanner scanner) {
        System.out.print("Enter partial name to search: ");
        String partial = scanner.nextLine().toLowerCase();
        boolean found = false;
        for (Contact c : contacts) {
            if (c.name.toLowerCase().contains(partial)) {
                c.display();
                found = true;
            }
        }
        if (!found) System.out.println("Contact not found.\n");
    }

    void deleteContact(Scanner scanner) {
        System.out.print("Enter name or phone number to delete: ");
        String input = scanner.nextLine().trim().toLowerCase();

        boolean removed = contacts.removeIf(c -> 
            c.name.toLowerCase().equals(input) || 
            c.phoneNumbers.stream().anyMatch(pn -> (("+" + pn.countryCode + pn.number).toLowerCase().contains(input) || pn.number.toLowerCase().contains(input))));

        System.out.println(removed ? "Contact deleted.\n" : "Contact not found.\n");
    }

    void updateContact(Scanner scanner) {
        System.out.print("Enter name to update: ");
        String name = scanner.nextLine().trim();

        Optional<Contact> opt = contacts.stream()
                .filter(c -> c.name.equalsIgnoreCase(name))
                .findFirst();

        // Fix here: use !opt.isPresent() instead of opt.isEmpty()
        if (!opt.isPresent()) {
            System.out.println("Contact not found.\n");
            return;
        }

        Contact c = opt.get();

        System.out.print("Update email? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            System.out.print("Enter new email: ");
            String newEmail = scanner.nextLine().trim();
            if (EMAIL_PATTERN.matcher(newEmail).matches()) {
                c.email = newEmail;
            } else {
                System.out.println("Invalid email format, keeping old email.");
            }
        }

        System.out.print("Update phone numbers? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            c.phoneNumbers.clear();
            while (true) {
                System.out.print("Enter Country Code: ");
                String cc = scanner.nextLine().trim();
                if (!countryCodeLengths.containsKey(cc)) {
                    System.out.println("Unsupported country code.");
                    continue;
                }
                int len = countryCodeLengths.get(cc);
                System.out.print("Enter Number (without country code): ");
                String num = scanner.nextLine().trim();
                if (num.length() != len || !num.matches("\\d+")) {
                    System.out.println("Invalid number length or format.");
                    continue;
                }
                c.addPhoneNumber(cc, num);
                System.out.print("Add another phone number? (y/n): ");
                if (!scanner.nextLine().equalsIgnoreCase("y")) break;
            }
        }

        System.out.println("Contact updated.\n");
    }

    void displayGroupedByCountryCode() {
        if (contacts.isEmpty()) {
            System.out.println("No contacts to display.\n");
            return;
        }
        Map<String, Set<Contact>> grouped = new HashMap<>();

        for (Contact c : contacts) {
            for (PhoneNumber pn : c.phoneNumbers) {
                grouped.computeIfAbsent(pn.countryCode, k -> new HashSet<>()).add(c);
            }
        }

        for (String code : grouped.keySet()) {
            System.out.println("Country Code +" + code + ":");
            grouped.get(code).stream()
                    .sorted(Comparator.comparing(c -> c.name.toLowerCase()))
                    .forEach(Contact::display);
        }
    }

    void exportToCSV() {
        try (PrintWriter pw = new PrintWriter(new File("contacts.csv"))) {
            pw.println("Name,Email,PhoneNumbers");
            for (Contact c : contacts) {
                StringBuilder phones = new StringBuilder();
                for (PhoneNumber pn : c.phoneNumbers) {
                    phones.append("+").append(pn.countryCode).append(pn.number).append(" | ");
                }
                if (phones.length() > 3) phones.setLength(phones.length() - 3); // remove trailing " | "
                pw.printf("\"%s\",\"%s\",\"%s\"\n", c.name, c.email, phones);
            }
            System.out.println("Contacts exported to contacts.csv\n");
        } catch (IOException e) {
            System.out.println("Failed to export contacts: " + e.getMessage());
        }
    }

    void saveContacts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(contacts);
            System.out.println("Contacts saved. Exiting...");
        } catch (IOException e) {
            System.out.println("Error saving contacts: " + e.getMessage());
        }
    }

    void loadContacts() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            contacts = (ArrayList<Contact>) ois.readObject();
            System.out.println("Contacts loaded successfully.\n");
        } catch (Exception e) {
            System.out.println("Failed to load contacts: " + e.getMessage());
        }
    }
}
