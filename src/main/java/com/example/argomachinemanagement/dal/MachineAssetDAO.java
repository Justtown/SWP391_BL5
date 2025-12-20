package com.example.argomachinemanagement.dal;

import com.example.argomachinemanagement.entity.MachineAsset;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DAO for machine_assets table
 * Quản lý từng chiếc máy vật lý với serial number riêng
 */
public class MachineAssetDAO extends DBContext implements I_DAO<MachineAsset> {

    private static final String BASE_SELECT =
        "SELECT a.*, m.model_code, m.model_name, m.brand, mt.type_name " +
        "FROM machine_assets a " +
        "LEFT JOIN machine_models m ON a.model_id = m.id " +
        "LEFT JOIN machine_types mt ON m.type_id = mt.id ";

    @Override
    public List<MachineAsset> findAll() {
        List<MachineAsset> assets = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY a.created_at DESC";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                MachineAsset asset = getFromResultSet(resultSet);
                assets.add(asset);
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineAssetDAO.findAll: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return assets;
    }

    @Override
    public MachineAsset findById(Integer id) {
        MachineAsset asset = null;
        String sql = BASE_SELECT + "WHERE a.id = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                asset = getFromResultSet(resultSet);
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineAssetDAO.findById: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return asset;
    }

    /**
     * Tìm kiếm với bộ lọc
     */
    public List<MachineAsset> findByFilters(Integer modelId, String status, String rentalStatus, String keyword) {
        List<MachineAsset> assets = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT + "WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (modelId != null && modelId > 0) {
            sql.append(" AND a.model_id = ?");
            params.add(modelId);
        }

        if (status != null && !status.trim().isEmpty() && !status.equals("All")) {
            sql.append(" AND a.status = ?");
            params.add(status.trim());
        }

        if (rentalStatus != null && !rentalStatus.trim().isEmpty() && !rentalStatus.equals("All")) {
            sql.append(" AND a.rental_status = ?");
            params.add(rentalStatus.trim());
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (a.serial_number LIKE ? OR a.location LIKE ? OR m.model_name LIKE ? OR m.brand LIKE ?)");
            String searchPattern = "%" + keyword.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        sql.append(" ORDER BY a.created_at DESC");

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                MachineAsset asset = getFromResultSet(resultSet);
                assets.add(asset);
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineAssetDAO.findByFilters: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return assets;
    }

    /**
     * Lấy danh sách máy có thể cho thuê (ACTIVE + AVAILABLE)
     */
    public List<MachineAsset> findAvailableAssets() {
        List<MachineAsset> assets = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE a.status = 'ACTIVE' AND a.rental_status = 'AVAILABLE' ORDER BY m.model_name, a.serial_number";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                MachineAsset asset = getFromResultSet(resultSet);
                assets.add(asset);
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineAssetDAO.findAvailableAssets: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return assets;
    }

    /**
     * Alias for findAvailableAssets
     */
    public List<MachineAsset> findAvailable() {
        return findAvailableAssets();
    }

    @Override
    public int insert(MachineAsset asset) {
        int assetId = 0;
        String sql = "INSERT INTO machine_assets (serial_number, model_id, status, rental_status, location, purchase_date, note) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, asset.getSerialNumber());
            statement.setInt(2, asset.getModelId());
            statement.setString(3, asset.getStatus() != null ? asset.getStatus() : "ACTIVE");
            statement.setString(4, asset.getRentalStatus() != null ? asset.getRentalStatus() : "AVAILABLE");
            statement.setString(5, asset.getLocation());
            statement.setDate(6, asset.getPurchaseDate());
            statement.setString(7, asset.getNote());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    assetId = generatedKeys.getInt(1);
                    generatedKeys.close();
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineAssetDAO.insert: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return assetId;
    }

    @Override
    public boolean update(MachineAsset asset) {
        boolean success = false;
        String sql = "UPDATE machine_assets SET serial_number = ?, model_id = ?, status = ?, " +
                     "rental_status = ?, location = ?, purchase_date = ?, note = ? WHERE id = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, asset.getSerialNumber());
            statement.setInt(2, asset.getModelId());
            statement.setString(3, asset.getStatus());
            statement.setString(4, asset.getRentalStatus());
            statement.setString(5, asset.getLocation());
            statement.setDate(6, asset.getPurchaseDate());
            statement.setString(7, asset.getNote());
            statement.setInt(8, asset.getId());

            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error in MachineAssetDAO.update: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return success;
    }

    /**
     * Cập nhật status của asset (ACTIVE/INACTIVE)
     */
    public boolean updateStatus(Integer id, String status) {
        boolean success = false;
        String sql = "UPDATE machine_assets SET status = ? WHERE id = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, status);
            statement.setInt(2, id);

            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error in MachineAssetDAO.updateStatus: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return success;
    }

    /**
     * Cập nhật rental_status của asset (AVAILABLE/RENTED/MAINTENANCE)
     */
    public boolean updateRentalStatus(Integer id, String rentalStatus) {
        boolean success = false;
        String sql = "UPDATE machine_assets SET rental_status = ? WHERE id = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, rentalStatus);
            statement.setInt(2, id);

            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error in MachineAssetDAO.updateRentalStatus: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return success;
    }

    @Override
    public boolean delete(MachineAsset asset) {
        // Sử dụng soft delete thay vì hard delete
        return updateStatus(asset.getId(), "INACTIVE");
    }

    /**
     * Kiểm tra serial_number đã tồn tại chưa
     */
    public boolean isSerialNumberExists(String serialNumber, Integer excludeId) {
        boolean exists = false;
        String sql = "SELECT COUNT(*) FROM machine_assets WHERE serial_number = ?";
        if (excludeId != null) {
            sql += " AND id != ?";
        }

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, serialNumber);
            if (excludeId != null) {
                statement.setInt(2, excludeId);
            }
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                exists = resultSet.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineAssetDAO.isSerialNumberExists: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return exists;
    }

    /**
     * Đếm số asset theo model
     */
    public int countByModel(Integer modelId) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM machine_assets WHERE model_id = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, modelId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineAssetDAO.countByModel: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return count;
    }

    /**
     * Lấy danh sách tất cả locations (cho filter/autocomplete)
     */
    public List<String> getAllLocations() {
        List<String> locations = new ArrayList<>();
        String sql = "SELECT DISTINCT location FROM machine_assets WHERE location IS NOT NULL ORDER BY location";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                locations.add(resultSet.getString("location"));
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineAssetDAO.getAllLocations: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return locations;
    }

    @Override
    public Map<Integer, MachineAsset> findAllMap() {
        return Map.of();
    }

    @Override
    public MachineAsset getFromResultSet(ResultSet rs) throws SQLException {
        MachineAsset asset = MachineAsset.builder()
                .id(rs.getInt("id"))
                .serialNumber(rs.getString("serial_number"))
                .modelId(rs.getInt("model_id"))
                .status(rs.getString("status"))
                .rentalStatus(rs.getString("rental_status"))
                .location(rs.getString("location"))
                .purchaseDate(rs.getDate("purchase_date"))
                .note(rs.getString("note"))
                .createdAt(rs.getTimestamp("created_at"))
                .build();

        // Display fields từ JOIN
        try {
            asset.setModelCode(rs.getString("model_code"));
            asset.setModelName(rs.getString("model_name"));
            asset.setBrand(rs.getString("brand"));
            asset.setTypeName(rs.getString("type_name"));
        } catch (SQLException e) {
            // Columns might not exist in some queries
        }

        return asset;
    }
}
