package com.example.argomachinemanagement.controller.order;

import com.example.argomachinemanagement.dal.OrderDAO;
import com.example.argomachinemanagement.dal.MachineTypeDAO;
import com.example.argomachinemanagement.dal.UserDAO;
import com.example.argomachinemanagement.entity.Order;
import com.example.argomachinemanagement.entity.MachineType;
import com.example.argomachinemanagement.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet(name = "OrderServlet", urlPatterns = {"/sale/orders"})
public class OrderServlet extends HttpServlet {

    private OrderDAO orderDAO = new OrderDAO();
    private MachineTypeDAO machineTypeDAO = new MachineTypeDAO();
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        // Check both userRole and roleName attributes
        String userRole = (String) session.getAttribute("userRole");
        if (userRole == null) {
            userRole = (String) session.getAttribute("roleName");
        }
        Integer userId = (Integer) session.getAttribute("userId");

        if (action == null || action.equals("list")) {
            handleListOrders(request, response, userRole, userId);
        } else if (action.equals("create")) {
            if ("sale".equals(userRole) || "admin".equals(userRole)) {
                List<MachineType> machineTypes = machineTypeDAO.findAll();
                List<User> customers = userDAO.findCustomers();
                request.setAttribute("machineTypes", machineTypes);
                request.setAttribute("customers", customers);
                request.getRequestDispatcher("/view/order/order-create.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/sale/orders?action=list&error=permission");
            }
        } else if (action.equals("edit")) {
            handleEditOrder(request, response, userRole, userId);
        } else if (action.equals("detail")) {
            handleDetailOrder(request, response);
        } else if (action.equals("delete")) {
            handleDeleteOrder(request, response, userRole, userId);
        } else {
            response.sendRedirect(request.getContextPath() + "/sale/orders?action=list");
        }
    }

    private void handleListOrders(HttpServletRequest request, HttpServletResponse response, 
                                   String userRole, Integer userId) 
            throws ServletException, IOException {
        // Lấy các tham số tìm kiếm
        String searchContract = request.getParameter("searchContract");
        String searchCustomer = request.getParameter("searchCustomer");
        String searchStatus = request.getParameter("searchStatus");
        
        List<Order> orders;
        
        // Nếu có tìm kiếm, gọi method search
        if ((searchContract != null && !searchContract.trim().isEmpty()) ||
            (searchCustomer != null && !searchCustomer.trim().isEmpty()) ||
            (searchStatus != null && !searchStatus.trim().isEmpty())) {
            orders = orderDAO.searchOrders(searchContract, searchCustomer, searchStatus, userRole, userId);
        } else {
            // Hiển thị tất cả orders
            orders = orderDAO.findAll();
        }
        
        request.setAttribute("orders", orders);
        request.setAttribute("userRole", userRole);
        request.getRequestDispatcher("/view/order/order-list.jsp").forward(request, response);
    }

    private void handleEditOrder(HttpServletRequest request, HttpServletResponse response,
                                  String userRole, Integer userId) 
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/sale/orders?action=list");
            return;
        }

        int orderId = Integer.parseInt(idParam);
        Order order = orderDAO.findById(orderId);

        if (order == null) {
            response.sendRedirect(request.getContextPath() + "/orders?action=list&error=notfound");
            return;
        }

        // Sale can only edit their own orders with PENDING status
        if ("sale".equals(userRole)) {
            if (!order.getCreatedBy().equals(userId)) {
                response.sendRedirect(request.getContextPath() + "/sale/orders?action=list&error=permission");
                return;
            }
            if (!"PENDING".equals(order.getStatus())) {
                response.sendRedirect(request.getContextPath() + "/sale/orders?action=list&error=cannot_edit");
                return;
            }
        }

        List<MachineType> machineTypes = machineTypeDAO.findAll();
        List<User> customers = userDAO.findCustomers();
        request.setAttribute("machineTypes", machineTypes);
        request.setAttribute("customers", customers);
        request.setAttribute("order", order);
        request.getRequestDispatcher("/view/order/order-edit.jsp").forward(request, response);
    }

    private void handleDetailOrder(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/sale/orders?action=list");
            return;
        }

        int orderId = Integer.parseInt(idParam);
        Order order = orderDAO.findById(orderId);

        if (order == null) {
            response.sendRedirect(request.getContextPath() + "/orders?action=list&error=notfound");
            return;
        }

        request.setAttribute("order", order);
        request.getRequestDispatcher("/view/order/order-detail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        // Check both userRole and roleName attributes
        String userRole = (String) session.getAttribute("userRole");
        if (userRole == null) {
            userRole = (String) session.getAttribute("roleName");
        }
        Integer userId = (Integer) session.getAttribute("userId");

        if (action == null) action = "create";

        if (action.equals("create")) {
            handleCreateOrder(request, response, userId);
        } else if (action.equals("update")) {
            handleUpdateOrder(request, response, userRole, userId);
        } else if (action.equals("delete")) {
            handleDeleteOrder(request, response, userRole, userId);
        } else if (action.equals("approve")) {
            handleApproveOrder(request, response, userRole, userId);
        } else if (action.equals("reject")) {
            handleRejectOrder(request, response, userRole, userId);
        } else {
            response.sendRedirect(request.getContextPath() + "/sale/orders?action=list");
        }
    }

    private void handleCreateOrder(HttpServletRequest request, HttpServletResponse response,
                                    Integer userId) 
            throws ServletException, IOException {
        try {
            String contractCode = request.getParameter("contractCode");
            String customerName = request.getParameter("customerName");
            String customerPhone = request.getParameter("customerPhone");
            String customerAddress = request.getParameter("customerAddress");
            String machineTypeIdRaw = request.getParameter("machineTypeId");
            String quantityRaw = request.getParameter("quantity");
            String serviceDescription = request.getParameter("serviceDescription");
            String startDateRaw = request.getParameter("startDate");
            String endDateRaw = request.getParameter("endDate");
            String totalCostRaw = request.getParameter("totalCost");
            
            // Kiểm tra khách hàng có trong hệ thống không
            if (!userDAO.isValidCustomer(customerName)) {
                // Giữ lại các giá trị đã nhập
                request.setAttribute("error", "invalid_customer");
                request.setAttribute("contractCode", contractCode);
                request.setAttribute("customerName", customerName);
                request.setAttribute("customerPhone", customerPhone);
                request.setAttribute("customerAddress", customerAddress);
                request.setAttribute("machineTypeId", machineTypeIdRaw);
                request.setAttribute("quantity", quantityRaw);
                request.setAttribute("serviceDescription", serviceDescription);
                request.setAttribute("startDate", startDateRaw);
                request.setAttribute("endDate", endDateRaw);
                request.setAttribute("totalCost", totalCostRaw);
                
                // Load lại machine types và customers
                List<MachineType> machineTypes = machineTypeDAO.findAll();
                List<User> customers = userDAO.findCustomers();
                request.setAttribute("machineTypes", machineTypes);
                request.setAttribute("customers", customers);
                
                request.getRequestDispatcher("/view/order/order-create.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra mã hợp đồng đã tồn tại chưa
            if (orderDAO.isContractCodeExists(contractCode, null)) {
                // Giữ lại các giá trị đã nhập
                request.setAttribute("error", "duplicate_contract");
                request.setAttribute("contractCode", contractCode);
                request.setAttribute("customerName", customerName);
                request.setAttribute("customerPhone", customerPhone);
                request.setAttribute("customerAddress", customerAddress);
                request.setAttribute("machineTypeId", machineTypeIdRaw);
                request.setAttribute("quantity", quantityRaw);
                request.setAttribute("serviceDescription", serviceDescription);
                request.setAttribute("startDate", startDateRaw);
                request.setAttribute("endDate", endDateRaw);
                request.setAttribute("totalCost", totalCostRaw);
                
                // Load lại machine types và customers
                List<MachineType> machineTypes = machineTypeDAO.findAll();
                List<User> customers = userDAO.findCustomers();
                request.setAttribute("machineTypes", machineTypes);
                request.setAttribute("customers", customers);
                
                request.getRequestDispatcher("/view/order/order-create.jsp").forward(request, response);
                return;
            }

            Order o = new Order();
            o.setContractCode(contractCode);
            o.setCustomerName(customerName);
            o.setCustomerPhone(customerPhone);
            o.setCustomerAddress(customerAddress);

            if (machineTypeIdRaw != null && !machineTypeIdRaw.isBlank()) {
                o.setMachineId(Integer.parseInt(machineTypeIdRaw));  // Lưu machine_type_id
            }
            if (quantityRaw != null && !quantityRaw.isBlank()) {
                o.setQuantity(Integer.parseInt(quantityRaw));
            } else {
                o.setQuantity(1);
            }
            o.setServiceDescription(serviceDescription);

            if (startDateRaw != null && !startDateRaw.isBlank()) {
                o.setStartDate(Date.valueOf(startDateRaw));
            }
            if (endDateRaw != null && !endDateRaw.isBlank()) {
                o.setEndDate(Date.valueOf(endDateRaw));
            }

            // New orders are always PENDING for approval
            o.setStatus("PENDING");

            if (totalCostRaw != null && !totalCostRaw.isBlank()) {
                o.setTotalCost(Double.parseDouble(totalCostRaw));
            }

            o.setCreatedBy(userId);

            orderDAO.create(o);
            response.sendRedirect(request.getContextPath() + "/sale/orders?action=list&success=created");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/sale/orders?action=create&error=create_failed");
        }
    }

    private void handleUpdateOrder(HttpServletRequest request, HttpServletResponse response,
                                    String userRole, Integer userId) 
            throws ServletException, IOException {
        try {
            int orderId = Integer.parseInt(request.getParameter("id"));
            Order existingOrder = orderDAO.findById(orderId);

            if (existingOrder == null) {
                response.sendRedirect(request.getContextPath() + "/orders?action=list&error=notfound");
                return;
            }

            // Sale can only update their own orders with PENDING status
            if ("sale".equals(userRole)) {
                if (!existingOrder.getCreatedBy().equals(userId)) {
                    response.sendRedirect(request.getContextPath() + "/sale/orders?action=list&error=permission");
                    return;
                }
                if (!"PENDING".equals(existingOrder.getStatus())) {
                    response.sendRedirect(request.getContextPath() + "/sale/orders?action=list&error=cannot_edit");
                    return;
                }
            }

            String contractCode = request.getParameter("contractCode");
            String customerName = request.getParameter("customerName");
            String customerPhone = request.getParameter("customerPhone");
            String customerAddress = request.getParameter("customerAddress");
            String machineTypeIdRaw = request.getParameter("machineTypeId");
            String quantityRaw = request.getParameter("quantity");
            String serviceDescription = request.getParameter("serviceDescription");
            
            // Kiểm tra khách hàng có trong hệ thống không
            if (!userDAO.isValidCustomer(customerName)) {
                existingOrder.setContractCode(contractCode);
                existingOrder.setCustomerName(customerName);
                existingOrder.setCustomerPhone(customerPhone);
                existingOrder.setCustomerAddress(customerAddress);
                existingOrder.setServiceDescription(serviceDescription);
                
                request.setAttribute("error", "invalid_customer");
                request.setAttribute("order", existingOrder);
                request.setAttribute("machineTypeId", machineTypeIdRaw);
                request.setAttribute("quantity", quantityRaw);
                
                // Load lại machine types và customers
                List<MachineType> machineTypes = machineTypeDAO.findAll();
                List<User> customers = userDAO.findCustomers();
                request.setAttribute("machineTypes", machineTypes);
                request.setAttribute("customers", customers);
                
                request.getRequestDispatcher("/view/order/order-edit.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra mã hợp đồng trùng (trừ order hiện tại)
            if (orderDAO.isContractCodeExists(contractCode, orderId)) {
                // Giữ lại các giá trị đã nhập
                existingOrder.setContractCode(contractCode);
                existingOrder.setCustomerName(customerName);
                existingOrder.setCustomerPhone(customerPhone);
                existingOrder.setCustomerAddress(customerAddress);
                existingOrder.setServiceDescription(serviceDescription);
                
                request.setAttribute("error", "duplicate_contract");
                request.setAttribute("order", existingOrder);
                request.setAttribute("machineTypeId", machineTypeIdRaw);
                request.setAttribute("quantity", quantityRaw);
                
                // Load lại machine types và customers
                List<MachineType> machineTypes = machineTypeDAO.findAll();
                List<User> customers = userDAO.findCustomers();
                request.setAttribute("machineTypes", machineTypes);
                request.setAttribute("customers", customers);
                
                request.getRequestDispatcher("/view/order/order-edit.jsp").forward(request, response);
                return;
            }
            String startDateRaw = request.getParameter("startDate");
            String endDateRaw = request.getParameter("endDate");
            String totalCostRaw = request.getParameter("totalCost");

            Order o = new Order();
            o.setId(orderId);
            o.setContractCode(contractCode);
            o.setCustomerName(customerName);
            o.setCustomerPhone(customerPhone);
            o.setCustomerAddress(customerAddress);

            if (machineTypeIdRaw != null && !machineTypeIdRaw.isBlank()) {
                o.setMachineId(Integer.parseInt(machineTypeIdRaw));  // Lưu machine_type_id
            }
            if (quantityRaw != null && !quantityRaw.isBlank()) {
                o.setQuantity(Integer.parseInt(quantityRaw));
            } else {
                o.setQuantity(1);
            }
            o.setServiceDescription(serviceDescription);

            if (startDateRaw != null && !startDateRaw.isBlank()) {
                o.setStartDate(Date.valueOf(startDateRaw));
            }
            if (endDateRaw != null && !endDateRaw.isBlank()) {
                o.setEndDate(Date.valueOf(endDateRaw));
            }

            if (totalCostRaw != null && !totalCostRaw.isBlank()) {
                o.setTotalCost(Double.parseDouble(totalCostRaw));
            }

            orderDAO.update(o);
            response.sendRedirect(request.getContextPath() + "/sale/orders?action=list&success=updated");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/sale/orders?action=list&error=update_failed");
        }
    }

    private void handleDeleteOrder(HttpServletRequest request, HttpServletResponse response,
                                    String userRole, Integer userId) 
            throws ServletException, IOException {
        try {
            int orderId = Integer.parseInt(request.getParameter("id"));
            Order existingOrder = orderDAO.findById(orderId);

            if (existingOrder == null) {
                response.sendRedirect(request.getContextPath() + "/sale/orders?action=list&error=notfound");
                return;
            }

            // Sale can only delete their own orders with PENDING status
            if ("sale".equals(userRole)) {
                if (!existingOrder.getCreatedBy().equals(userId)) {
                    response.sendRedirect(request.getContextPath() + "/sale/orders?action=list&error=permission");
                    return;
                }
                if (!"PENDING".equals(existingOrder.getStatus())) {
                    response.sendRedirect(request.getContextPath() + "/sale/orders?action=list&error=cannot_delete");
                    return;
                }
            }

            orderDAO.delete(orderId);
            response.sendRedirect(request.getContextPath() + "/sale/orders?action=list&success=deleted");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/sale/orders?action=list&error=delete_failed");
        }
    }

    private void handleApproveOrder(HttpServletRequest request, HttpServletResponse response,
                                     String userRole, Integer userId) 
            throws ServletException, IOException {
        if (!"manager".equals(userRole)) {
            response.sendRedirect(request.getContextPath() + "/sale/orders?action=list&error=permission");
            return;
        }

        try {
            int orderId = Integer.parseInt(request.getParameter("id"));
            orderDAO.approve(orderId, userId);
            response.sendRedirect(request.getContextPath() + "/sale/orders?action=list&success=approved");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/sale/orders?action=list&error=approve_failed");
        }
    }

    private void handleRejectOrder(HttpServletRequest request, HttpServletResponse response,
                                    String userRole, Integer userId) 
            throws ServletException, IOException {
        if (!"manager".equals(userRole)) {
            response.sendRedirect(request.getContextPath() + "/sale/orders?action=list&error=permission");
            return;
        }

        try {
            int orderId = Integer.parseInt(request.getParameter("id"));
            orderDAO.reject(orderId, userId);
            response.sendRedirect(request.getContextPath() + "/sale/orders?action=list&success=rejected");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/sale/orders?action=list&error=reject_failed");
        }
    }
}
