package com.example.argomachinemanagement.controller.dashboard;

import com.example.argomachinemanagement.dal.MachineDAO;
import com.example.argomachinemanagement.dal.ContractDAO;
import com.example.argomachinemanagement.dal.MaintenanceDAO;
import com.example.argomachinemanagement.dal.MachineTypeDAO;
import com.example.argomachinemanagement.entity.Machine;
import com.example.argomachinemanagement.entity.Contract;
import com.example.argomachinemanagement.entity.MachineType;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller cho Manager Statistics với dữ liệu cho charts
 */
@WebServlet(name = "ManagerStatisticsController", urlPatterns = {"/manager/statistics"})
public class ManagerStatisticsController extends HttpServlet {
    
    private MachineDAO machineDAO;
    private ContractDAO contractDAO;
    private MaintenanceDAO maintenanceDAO;
    private MachineTypeDAO machineTypeDAO;
    
    @Override
    public void init() throws ServletException {
        machineDAO = new MachineDAO();
        contractDAO = new ContractDAO();
        maintenanceDAO = new MaintenanceDAO();
        machineTypeDAO = new MachineTypeDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            // ========== MACHINE STATISTICS ==========
            List<Machine> allMachines = machineDAO.findAll();
            int totalMachines = allMachines.size();
            int activeMachines = 0;
            int inactiveMachines = 0;
            int rentableMachines = 0;
            
            // Count machines by type for chart
            Map<String, Integer> machinesByType = new HashMap<>();
            
            for (Machine m : allMachines) {
                if ("ACTIVE".equals(m.getStatus())) {
                    activeMachines++;
                } else {
                    inactiveMachines++;
                }
                if (Boolean.TRUE.equals(m.getIsRentable())) {
                    rentableMachines++;
                }
                
                // Count by type
                String typeName = m.getMachineTypeName() != null ? m.getMachineTypeName() : "Khác";
                machinesByType.put(typeName, machinesByType.getOrDefault(typeName, 0) + 1);
            }
            
            // ========== CONTRACT STATISTICS ==========
            List<Contract> allContracts = contractDAO.findAll();
            int totalContracts = allContracts.size();
            int activeContracts = 0;
            int finishedContracts = 0;
            int draftContracts = 0;
            int cancelledContracts = 0;
            
            // Contracts by month (last 6 months)
            Calendar cal = Calendar.getInstance();
            Map<String, Integer> contractsByMonth = new HashMap<>();
            for (int i = 5; i >= 0; i--) {
                cal.setTime(new Date());
                cal.add(Calendar.MONTH, -i);
                int month = cal.get(Calendar.MONTH) + 1;
                int year = cal.get(Calendar.YEAR);
                String monthKey = String.format("%02d/%d", month, year);
                contractsByMonth.put(monthKey, 0);
            }
            
            // Count contracts
            for (Contract c : allContracts) {
                String status = c.getStatus();
                if ("ACTIVE".equals(status)) {
                    activeContracts++;
                } else if ("FINISHED".equals(status)) {
                    finishedContracts++;
                } else if ("DRAFT".equals(status)) {
                    draftContracts++;
                } else if ("CANCELLED".equals(status)) {
                    cancelledContracts++;
                }
                
                // Count by month
                if (c.getCreatedAt() != null) {
                    cal.setTime(c.getCreatedAt());
                    int month = cal.get(Calendar.MONTH) + 1;
                    int year = cal.get(Calendar.YEAR);
                    String monthKey = String.format("%02d/%d", month, year);
                    if (contractsByMonth.containsKey(monthKey)) {
                        contractsByMonth.put(monthKey, contractsByMonth.get(monthKey) + 1);
                    }
                }
            }
            
            // MAINTENANCE STATISTICS
            int totalMaintenances = maintenanceDAO.countAll();
            int completedMaintenances = maintenanceDAO.countByStatus("COMPLETED");
            int pendingMaintenances = maintenanceDAO.countByStatus("PENDING");
            
            // Maintenance by type
            Map<String, Integer> maintenancesByType = new HashMap<>();
            List<com.example.argomachinemanagement.entity.Maintenance> allMaintenances = maintenanceDAO.findAll();
            for (com.example.argomachinemanagement.entity.Maintenance m : allMaintenances) {
                String type = m.getMaintenanceType() != null ? m.getMaintenanceType() : "Khác";
                maintenancesByType.put(type, maintenancesByType.getOrDefault(type, 0) + 1);
            }
            
            // MACHINE TYPE STATISTICS
            int totalMachineTypes = machineTypeDAO.findAll().size();
            
            // SET ATTRIBUTES
            request.setAttribute("totalMachines", totalMachines);
            request.setAttribute("activeMachines", activeMachines);
            request.setAttribute("inactiveMachines", inactiveMachines);
            request.setAttribute("rentableMachines", rentableMachines);
            
            request.setAttribute("totalContracts", totalContracts);
            request.setAttribute("activeContracts", activeContracts);
            request.setAttribute("finishedContracts", finishedContracts);
            request.setAttribute("draftContracts", draftContracts);
            request.setAttribute("cancelledContracts", cancelledContracts);
            
            request.setAttribute("totalMaintenances", totalMaintenances);
            request.setAttribute("completedMaintenances", completedMaintenances);
            request.setAttribute("pendingMaintenances", pendingMaintenances);
            
            request.setAttribute("totalMachineTypes", totalMachineTypes);
            
            // Chart data
            request.setAttribute("machinesByType", machinesByType);
            request.setAttribute("contractsByMonth", contractsByMonth);
            request.setAttribute("maintenancesByType", maintenancesByType);
            
            // Forward to JSP
            request.getRequestDispatcher("/view/dashboard/manager/statistics.jsp").forward(request, response);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading statistics");
        }
    }
}


