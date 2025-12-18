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
import java.util.List;

@WebServlet(name = "ManagerOrderServlet", urlPatterns = {"/manager/orders"})
public class ManagerOrderServlet extends HttpServlet {

    private OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String userRole = (String) session.getAttribute("userRole");
        if (userRole == null) {
            userRole = (String) session.getAttribute("roleName");
        }

        // Check if user is manager
        if (!"manager".equals(userRole)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");

        if (action == null || action.equals("list")) {
            handleListOrders(request, response);
        } else if (action.equals("detail")) {
            handleDetailOrder(request, response);
        } else if (action.equals("approve")) {
            handleApproveOrder(request, response);
        } else if (action.equals("reject")) {
            handleRejectOrder(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/manager/orders?action=list");
        }
    }

    private void handleListOrders(HttpServletRequest request, HttpServletResponse response)
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
            orders = orderDAO.searchOrders(searchContract, searchCustomer, searchStatus, null, null);
        } else {
            // Hiển thị tất cả orders
            orders = orderDAO.findAll();
        }

        request.setAttribute("orders", orders);
        request.setAttribute("userRole", "manager");
        request.getRequestDispatcher("/view/order/manager-order-list.jsp").forward(request, response);
    }

    private void handleDetailOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/manager/orders?action=list");
            return;
        }

        try {
            int orderId = Integer.parseInt(idParam);
            Order order = orderDAO.findById(orderId);

            if (order != null) {
                request.setAttribute("order", order);
                request.setAttribute("userRole", "manager");
                request.getRequestDispatcher("/view/order/manager-order-detail.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/manager/orders?action=list&error=notfound");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/manager/orders?action=list&error=invalid");
        }
    }

    private void handleApproveOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/manager/orders?action=list");
            return;
        }

        HttpSession session = request.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int orderId = Integer.parseInt(idParam);
            boolean success = orderDAO.updateStatusWithApprover(orderId, "APPROVED", userId);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/manager/orders?action=list&success=approved");
            } else {
                response.sendRedirect(request.getContextPath() + "/manager/orders?action=list&error=failed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/manager/orders?action=list&error=invalid");
        }
    }

    private void handleRejectOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/manager/orders?action=list");
            return;
        }

        HttpSession session = request.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int orderId = Integer.parseInt(idParam);
            boolean success = orderDAO.updateStatusWithApprover(orderId, "REJECTED", userId);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/manager/orders?action=list&success=rejected");
            } else {
                response.sendRedirect(request.getContextPath() + "/manager/orders?action=list&error=failed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/manager/orders?action=list&error=invalid");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
