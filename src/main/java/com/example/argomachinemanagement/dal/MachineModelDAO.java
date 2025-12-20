package com.example.argomachinemanagement.dal;

import com.example.argomachinemanagement.entity.MachineModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DAO for machine_models table
 * Quản lý các dòng máy (VD: Kubota L3408, John Deere 5055E)
 */
public class MachineModelDAO extends DBContext implements I_DAO<MachineModel> {

    @Override
    public List<MachineModel> findAll() {
        List<MachineModel> models = new ArrayList<>();
        String sql = "SELECT m.*, mt.type_name " +
                     "FROM machine_models m " +
                     "LEFT JOIN machine_types mt ON m.type_id = mt.id " +
                     "ORDER BY m.created_at DESC";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                MachineModel model = getFromResultSet(resultSet);
                models.add(model);
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineModelDAO.findAll: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return models;
    }

    @Override
    public MachineModel findById(Integer id) {
        MachineModel model = null;
        String sql = "SELECT m.*, mt.type_name " +
                     "FROM machine_models m " +
                     "LEFT JOIN machine_types mt ON m.type_id = mt.id " +
                     "WHERE m.id = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                model = getFromResultSet(resultSet);
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineModelDAO.findById: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return model;
    }

    /**
     * Tìm kiếm với bộ lọc
     */
    public List<MachineModel> findByFilters(Integer typeId, String brand, String keyword) {
        List<MachineModel> models = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT m.*, mt.type_name " +
            "FROM machine_models m " +
            "LEFT JOIN machine_types mt ON m.type_id = mt.id " +
            "WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();

        if (typeId != null && typeId > 0) {
            sql.append(" AND m.type_id = ?");
            params.add(typeId);
        }

        if (brand != null && !brand.trim().isEmpty() && !brand.equals("All")) {
            sql.append(" AND m.brand = ?");
            params.add(brand.trim());
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (m.model_code LIKE ? OR m.model_name LIKE ? OR m.brand LIKE ?)");
            String searchPattern = "%" + keyword.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        sql.append(" ORDER BY m.created_at DESC");

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                MachineModel model = getFromResultSet(resultSet);
                models.add(model);
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineModelDAO.findByFilters: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return models;
    }

    @Override
    public int insert(MachineModel model) {
        int modelId = 0;
        String sql = "INSERT INTO machine_models (model_code, model_name, brand, type_id, specs) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, model.getModelCode());
            statement.setString(2, model.getModelName());
            statement.setString(3, model.getBrand());
            statement.setInt(4, model.getTypeId());
            statement.setString(5, model.getSpecs());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    modelId = generatedKeys.getInt(1);
                    generatedKeys.close();
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineModelDAO.insert: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return modelId;
    }

    @Override
    public boolean update(MachineModel model) {
        boolean success = false;
        String sql = "UPDATE machine_models SET model_code = ?, model_name = ?, brand = ?, " +
                     "type_id = ?, specs = ? WHERE id = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, model.getModelCode());
            statement.setString(2, model.getModelName());
            statement.setString(3, model.getBrand());
            statement.setInt(4, model.getTypeId());
            statement.setString(5, model.getSpecs());
            statement.setInt(6, model.getId());

            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error in MachineModelDAO.update: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return success;
    }

    @Override
    public boolean delete(MachineModel model) {
        // Không cho phép xóa nếu còn asset liên kết
        // Kiểm tra trước khi xóa
        return deleteById(model.getId());
    }

    /**
     * Xóa model theo ID (kiểm tra ràng buộc FK trước)
     */
    public boolean deleteById(Integer id) {
        // Kiểm tra xem có asset nào liên kết không
        if (countAssetsByModel(id) > 0) {
            return false;
        }

        boolean success = false;
        String sql = "DELETE FROM machine_models WHERE id = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error in MachineModelDAO.deleteById: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return success;
    }

    /**
     * Đếm số asset theo model
     */
    public int countAssetsByModel(Integer modelId) {
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
            System.out.println("Error in MachineModelDAO.countAssetsByModel: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return count;
    }

    /**
     * Kiểm tra model_code đã tồn tại chưa
     */
    public boolean isModelCodeExists(String modelCode, Integer excludeId) {
        boolean exists = false;
        String sql = "SELECT COUNT(*) FROM machine_models WHERE model_code = ?";
        if (excludeId != null) {
            sql += " AND id != ?";
        }

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, modelCode);
            if (excludeId != null) {
                statement.setInt(2, excludeId);
            }
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                exists = resultSet.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineModelDAO.isModelCodeExists: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return exists;
    }

    /**
     * Lấy danh sách tất cả các brand (cho dropdown filter)
     */
    public List<String> getAllBrands() {
        List<String> brands = new ArrayList<>();
        String sql = "SELECT DISTINCT brand FROM machine_models ORDER BY brand";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                brands.add(resultSet.getString("brand"));
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineModelDAO.getAllBrands: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return brands;
    }

    @Override
    public Map<Integer, MachineModel> findAllMap() {
        return Map.of();
    }

    @Override
    public MachineModel getFromResultSet(ResultSet rs) throws SQLException {
        MachineModel model = MachineModel.builder()
                .id(rs.getInt("id"))
                .modelCode(rs.getString("model_code"))
                .modelName(rs.getString("model_name"))
                .brand(rs.getString("brand"))
                .typeId(rs.getInt("type_id"))
                .specs(rs.getString("specs"))
                .createdAt(rs.getTimestamp("created_at"))
                .build();

        // Display field từ JOIN
        try {
            model.setTypeName(rs.getString("type_name"));
        } catch (SQLException e) {
            // Column might not exist in some queries
        }

        return model;
    }
}
