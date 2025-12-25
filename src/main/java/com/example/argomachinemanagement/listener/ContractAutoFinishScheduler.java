package com.example.argomachinemanagement.listener;

import com.example.argomachinemanagement.dal.DBContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@WebListener
public class ContractAutoFinishScheduler implements ServletContextListener {
    
    private ScheduledExecutorService scheduler;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[ContractAutoFinishScheduler] Initializing scheduler...");
        

        scheduler = Executors.newScheduledThreadPool(1);


        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("[ContractAutoFinishScheduler] Running scheduled job at " + new java.util.Date());
                autoFinishExpiredContracts();
            } catch (Exception e) {
                System.err.println("[ContractAutoFinishScheduler] Error executing job: " + e.getMessage());
                e.printStackTrace();
            }
        }, 300, 300, TimeUnit.SECONDS);
        
        System.out.println("[ContractAutoFinishScheduler] Scheduler started. Job will run every 1 minute.");
    }
    

    private int autoFinishExpiredContracts() throws SQLException {
        Connection conn = null;
        PreparedStatement psSelect = null;
        PreparedStatement psUpdateContract = null;
        PreparedStatement psUpdateAsset = null;
        ResultSet rs = null;
        
        int finishedCount = 0;
        List<Integer> contractIds = new ArrayList<>();
        List<Integer> assetIds = new ArrayList<>();
        
        try {
            conn = new DBContext().getConnection();
            conn.setAutoCommit(false);


            String selectSql = "SELECT id FROM contracts " +
                              "WHERE status = 'ACTIVE' " +
                              "AND end_date IS NOT NULL " +
                              "AND end_date < CURDATE()";
            
            psSelect = conn.prepareStatement(selectSql);
            rs = psSelect.executeQuery();
            
            while (rs.next()) {
                contractIds.add(rs.getInt("id"));
            }
            
            if (contractIds.isEmpty()) {
                conn.commit();
                return 0;
            }
            
            System.out.println("[ContractAutoFinishScheduler] Found " + contractIds.size() + " expired contracts to finish");




            String selectItemsSql = "SELECT DISTINCT asset_id FROM contract_items WHERE contract_id = ?";
            psSelect = conn.prepareStatement(selectItemsSql);
            
            for (Integer contractId : contractIds) {
                psSelect.setInt(1, contractId);
                ResultSet rsItems = psSelect.executeQuery();
                while (rsItems.next()) {
                    int assetId = rsItems.getInt("asset_id");
                    if (!assetIds.contains(assetId)) {
                        assetIds.add(assetId);
                    }
                }
                rsItems.close();
            }




            String updateContractSql = "UPDATE contracts SET status = 'FINISHED', updated_at = CURRENT_TIMESTAMP " +
                                      "WHERE id = ? AND status = 'ACTIVE'";
            psUpdateContract = conn.prepareStatement(updateContractSql);
            
            for (Integer contractId : contractIds) {
                psUpdateContract.setInt(1, contractId);
                int rowsAffected = psUpdateContract.executeUpdate();
                if (rowsAffected > 0) {
                    finishedCount++;
                    System.out.println("[ContractAutoFinishScheduler] Finished contract ID: " + contractId);
                }
            }
            



            if (!assetIds.isEmpty()) {
                String updateAssetSql = "UPDATE machine_assets SET rental_status = 'AVAILABLE' WHERE id = ?";
                psUpdateAsset = conn.prepareStatement(updateAssetSql);
                
                for (Integer assetId : assetIds) {
                    psUpdateAsset.setInt(1, assetId);
                    psUpdateAsset.executeUpdate();
                    System.out.println("[ContractAutoFinishScheduler] Set asset ID " + assetId + " to AVAILABLE");
                }
            }
            
            conn.commit();
            System.out.println("[ContractAutoFinishScheduler] Successfully finished " + finishedCount + " contracts and freed " + assetIds.size() + " assets");
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("[ContractAutoFinishScheduler] Error rolling back: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // Ignore
                }
            }
            if (psSelect != null) {
                try {
                    psSelect.close();
                } catch (SQLException e) {
                    // Ignore
                }
            }
            if (psUpdateContract != null) {
                try {
                    psUpdateContract.close();
                } catch (SQLException e) {
                    // Ignore
                }
            }
            if (psUpdateAsset != null) {
                try {
                    psUpdateAsset.close();
                } catch (SQLException e) {
                    // Ignore
                }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // Ignore
                }
            }
        }
        
        return finishedCount;
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[ContractAutoFinishScheduler] Shutting down scheduler...");
        
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                // Đợi tối đa 10 giây để các task đang chạy hoàn thành
                if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("[ContractAutoFinishScheduler] Scheduler stopped.");
    }
}
