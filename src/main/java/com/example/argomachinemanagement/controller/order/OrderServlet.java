package com.example.argomachinemanagement.controller.order;

import com.example.argomachinemanagement.dal.OrderDAO;
import com.example.argomachinemanagement.entity.Order;
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
                request.getRequestDispatcher("/view/order/order-create.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/sale/orders?action=list&error=permission");
            }
        } else if (action.equals("edit")) {
            handleEditOrder(request, response, userRole, userId);
        } else if (action.equals("detail")) {
            handleDetailOrder(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/sale/orders?action=list");
        }
    }

    private void handleListOrders(HttpServletRequest request, HttpServletResponse response, 
                                   String userRole, Integer userId) 
            throws ServletException, IOException {
        List<Order> orders;
        
        // Sale user can only see their own orders
        if ("sale".equals(userRole)) {
            orders = orderDAO.findBySaleUser(userId);
        } else {
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
            String machineIdRaw = request.getParameter("machineId");
            String serviceDescription = request.getParameter("serviceDescription");
            String startDateRaw = request.getParameter("startDate");
            String endDateRaw = request.getParameter("endDate");
            String totalCostRaw = request.getParameter("totalCost");

            Order o = new Order();
            o.setContractCode(contractCode);
            o.setCustomerName(customerName);
            o.setCustomerPhone(customerPhone);
            o.setCustomerAddress(customerAddress);

            if (machineIdRaw != null && !machineIdRaw.isBlank()) {
                o.setMachineId(Integer.parseInt(machineIdRaw));
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
            String machineIdRaw = request.getParameter("machineId");
            String serviceDescription = request.getParameter("serviceDescription");
            String startDateRaw = request.getParameter("startDate");
            String endDateRaw = request.getParameter("endDate");
            String totalCostRaw = request.getParameter("totalCost");

            Order o = new Order();
            o.setId(orderId);
            o.setContractCode(contractCode);
            o.setCustomerName(customerName);
            o.setCustomerPhone(customerPhone);
            o.setCustomerAddress(customerAddress);

            if (machineIdRaw != null && !machineIdRaw.isBlank()) {
                o.setMachineId(Integer.parseInt(machineIdRaw));
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
        if (!"admin".equals(userRole)) {
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
        if (!"admin".equals(userRole)) {
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
