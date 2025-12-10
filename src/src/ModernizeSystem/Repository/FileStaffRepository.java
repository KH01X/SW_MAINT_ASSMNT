package ModernizeSystem.Repository;

import ModernizeSystem.Model.StaffModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FileStaffRepository implements StaffRepository {

    private final List<StaffModel> staffList = new ArrayList<>();

    public FileStaffRepository() {
        loadStaffData();
    }

    private void loadStaffData() {
        File file = new File("staffData.txt");

        if (!file.exists()) {
            System.out.println("staffData.txt not found! Using empty list.");
            return;
        }

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] p = line.split("\\|");

                if (p.length == 3) {
                    staffList.add(new StaffModel(p[0], p[1], p[2]));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error loading staff file.");
        }
    }

    @Override
    public List<StaffModel> findAll() {
        return staffList;
    }

    @Override
    public Optional<StaffModel> findById(String id) {     // ← NEW
        return staffList.stream()
                .filter(s -> s.getId().equalsIgnoreCase(id))
                .findFirst();
    }
}
