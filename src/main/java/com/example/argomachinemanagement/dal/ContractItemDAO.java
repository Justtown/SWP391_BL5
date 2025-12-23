package com.example.argomachinemanagement.dal;

import com.example.argomachinemanagement.entity.ContractItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DAO cho bảng contract_items
 */
public class ContractItemDAO extends DBContext implements I_DAO<ContractItem> {

    /**
     * Lấy tất cả items của một contract
     */
    public List<ContractItem> findByContractId(Integer contractId) {
        List<ContractItem> items = new ArrayList<>();
        String sql = "SELECT ci.*, " +
                     "ma.serial_number, ma.status AS asset_status, ma.rental_status, " +
                     "mm.model_code, mm.model_name, mm.brand, " +
                     "mt.type_name " +
                     "FROM contract_items ci " +
                     "INNER JOIN machine_assets ma ON ci.asset_id = ma.id " +
                     "INNER JOIN machine_models mm ON ma.model_id = mm.id " +
                     "INNER JOIN machine_types mt ON mm.type_id = mt.id " +
                     "WHERE ci.contract_id = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, contractId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                ContractItem item = getFromResultSet(resultSet);
                items.add(item);
            }
        } catch (SQLException ex) {
            System.out.println("Error in findByContractId: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return items;
    }

    @Override
    public List<ContractItem> findAll() {
        List<ContractItem> items = new ArrayList<>();
        String sql = "SELECT ci.*, " +
                     "ma.serial_number, ma.status AS asset_status, ma.rental_status, " +
                     "mm.model_code, mm.model_name, mm.brand, " +
                     "mt.type_name " +
                     "FROM contract_items ci " +
                     "INNER JOIN machine_assets ma ON ci.asset_id = ma.id " +
                     "INNER JOIN machine_models mm ON ma.model_id = mm.id " +
                     "INNER JOIN machine_types mt ON mm.type_id = mt.id";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                ContractItem item = getFromResultSet(resultSet);
                items.add(item);
            }
        } catch (SQLException ex) {
            System.out.println("Error in findAll: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return items;
    }

    @Override
    public ContractItem findById(Integer id) {
        ContractItem item = null;
        String sql = "SELECT ci.*, " +
                     "ma.serial_number, ma.status AS asset_status, ma.rental_status, " +
                     "mm.model_code, mm.model_name, mm.brand, " +
                     "mt.type_name " +
                     "FROM contract_items ci " +
                     "INNER JOIN machine_assets ma ON ci.asset_id = ma.id " +
                     "INNER JOIN machine_models mm ON ma.model_id = mm.id " +
                     "INNER JOIN machine_types mt ON mm.type_id = mt.id " +
                     "WHERE ci.id = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                item = getFromResultSet(resultSet);
            }
        } catch (SQLException ex) {
            System.out.println("Error in findById: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return item;
    }

    @Override
    public int insert(ContractItem item) {
        int itemId = 0;
        String sql = "INSERT INTO contract_items (contract_id, asset_id, price, note) VALUES (?, ?, ?, ?)";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, item.getContractId());
            statement.setInt(2, item.getAssetId());
            statement.setBigDecimal(3, item.getPrice());
            statement.setString(4, item.getNote());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    itemId = generatedKeys.getInt(1);
                    generatedKeys.close();
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in insert: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return itemId;
    }

    /**
     * Insert batch items cho một contract
     */
    public boolean insertBatch(List<ContractItem> items) {
        if (items == null || items.isEmpty()) {
            return true;
        }

        String sql = "INSERT INTO contract_items (contract_id, asset_id, price, note) VALUES (?, ?, ?, ?)";
        boolean success = true;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(sql);

            for (ContractItem item : items) {
                statement.setInt(1, item.getContractId());
                statement.setInt(2, item.getAssetId());
                statement.setBigDecimal(3, item.getPrice());
                statement.setString(4, item.getNote());
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();
        } catch (SQLException ex) {
            System.out.println("Error in insertBatch: " + ex.getMessage());
            success = false;
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            closeResources();
        }

        return success;
    }

    @Override
    public boolean update(ContractItem item) {
        boolean success = false;
        String sql = "UPDATE contract_items SET price = ?, note = ? WHERE id = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setBigDecimal(1, item.getPrice());
            statement.setString(2, item.getNote());
            statement.setInt(3, item.getId());

            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error in update: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return success;
    }

    @Override
    public boolean delete(ContractItem item) {
        boolean success = false;
        String sql = "DELETE FROM contract_items WHERE id = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, item.getId());

            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error in delete: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return success;
    }

    /**
     * Xóa tất cả items của một contract
     */
    public boolean deleteByContractId(Integer contractId) {
        boolean success = false;
        String sql = "DELETE FROM contract_items WHERE contract_id = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, contractId);

            statement.executeUpdate();
            success = true;
        } catch (SQLException ex) {
            System.out.println("Error in deleteByContractId: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return success;
    }

    @Override
    public Map<Integer, ContractItem> findAllMap() {
        return Map.of();
    }

    @Override
    public ContractItem getFromResultSet(ResultSet rs) throws SQLException {
        ContractItem item = ContractItem.builder()
                .id(rs.getInt("id"))
                .contractId(rs.getInt("contract_id"))
                .assetId(rs.getInt("asset_id"))
                .price(rs.getBigDecimal("price"))
                .note(rs.getString("note"))
                .build();

        // Set display fields from JOIN
        try {
            item.setSerialNumber(rs.getString("serial_number"));
            item.setModelCode(rs.getString("model_code"));
            item.setModelName(rs.getString("model_name"));
            item.setBrand(rs.getString("brand"));
            item.setTypeName(rs.getString("type_name"));
            item.setAssetStatus(rs.getString("asset_status"));
            item.setRentalStatus(rs.getString("rental_status"));
            
            // Set contract fields if available
            try {
                item.setContractCode(rs.getString("contract_code"));
                item.setContractStatus(rs.getString("contract_status"));
                item.setContractStartDate(rs.getDate("start_date"));
                item.setContractEndDate(rs.getDate("end_date"));
            } catch (SQLException e) {
                // Ignore if contract columns don't exist
            }
        } catch (SQLException e) {
            // Ignore if columns don't exist
        }

        return item;
    }

    /**
     * Đếm số items trong một contract
     */
    public int countByContractId(Integer contractId) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM contract_items WHERE contract_id = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, contractId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            System.out.println("Error in countByContractId: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return count;
    }

    /**
     * Tính tổng giá trị của một contract
     */
    public java.math.BigDecimal getTotalPriceByContractId(Integer contractId) {
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        String sql = "SELECT COALESCE(SUM(price), 0) AS total FROM contract_items WHERE contract_id = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, contractId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                total = resultSet.getBigDecimal("total");
            }
        } catch (SQLException ex) {
            System.out.println("Error in getTotalPriceByContractId: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return total;
    }

    /**
     * Lấy tất cả máy mà customer đang thuê và đã thuê
     * Bao gồm thông tin contract để phân biệt đang thuê (ACTIVE) và đã thuê (FINISHED)
     */
    public List<ContractItem> findByCustomerId(Integer customerId) {
        List<ContractItem> items = new ArrayList<>();
        String sql = "SELECT ci.*, " +
                     "ma.serial_number, ma.status AS asset_status, ma.rental_status, " +
                     "mm.model_code, mm.model_name, mm.brand, " +
                     "mt.type_name, " +
                     "c.contract_code, c.status AS contract_status, c.start_date, c.end_date " +
                     "FROM contract_items ci " +
                     "INNER JOIN contracts c ON ci.contract_id = c.id " +
                     "INNER JOIN machine_assets ma ON ci.asset_id = ma.id " +
                     "INNER JOIN machine_models mm ON ma.model_id = mm.id " +
                     "INNER JOIN machine_types mt ON mm.type_id = mt.id " +
                     "WHERE c.customer_id = ? " +
                     "AND c.status IN ('ACTIVE', 'FINISHED') " +
                     "ORDER BY c.status DESC, c.start_date DESC, ci.id DESC";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, customerId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                ContractItem item = getFromResultSet(resultSet);
                // Set thêm thông tin contract
                try {
                    item.setContractCode(resultSet.getString("contract_code"));
                    item.setContractStatus(resultSet.getString("contract_status"));
                    item.setContractStartDate(resultSet.getDate("start_date"));
                    item.setContractEndDate(resultSet.getDate("end_date"));
                } catch (SQLException e) {
                    // Ignore if columns don't exist
                }
                items.add(item);
            }
        } catch (SQLException ex) {
            System.out.println("Error in findByCustomerId: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            closeResources();
        }

        return items;
    }
}
