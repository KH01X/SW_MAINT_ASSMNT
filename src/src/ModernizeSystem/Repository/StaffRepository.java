package ModernizeSystem.Repository;

import ModernizeSystem.Model.StaffModel;
import java.util.Optional;
import java.util.List;

public interface StaffRepository {

    List<StaffModel> findAll();

    Optional<StaffModel> findById(String id);  // ← FIXED (Renamed)

}
