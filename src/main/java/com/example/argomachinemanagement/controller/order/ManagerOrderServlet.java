// package com.example.argomachinemanagement.controller.order;

// import com.example.argomachinemanagement.dal.OrderDAO;
// import com.example.argomachinemanagement.dal.ContractDAO;
// import com.example.argomachinemanagement.dal.MachineDAO;
// import com.example.argomachinemanagement.dal.UserDAO;
// import com.example.argomachinemanagement.entity.Contract;
// import com.example.argomachinemanagement.entity.Order;
// import com.example.argomachinemanagement.entity.Machine;
// import com.example.argomachinemanagement.entity.User;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.annotation.WebServlet;
// import jakarta.servlet.http.HttpServlet;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import jakarta.servlet.http.HttpSession;

// import java.io.IOException;
// import java.sql.Date;
// import java.time.LocalDate;
// import java.util.List;
// import java.util.logging.Level;
// import java.util.logging.Logger;
// @WebServlet(name = "ManagerOrderServlet", urlPatterns = {"/manager/orders"})
// public class ManagerOrderServlet extends HttpServlet {

//     private OrderDAO orderDAO = new OrderDAO();
//     private final ContractDAO contractDAO = new ContractDAO();
//     private final UserDAO userDAO = new UserDAO();
//     private final MachineDAO machineDAO = new MachineDAO();
//     private static final Logger LOGGER = Logger.getLogger(ManagerOrderServlet.class.getName());

//     @Override
//     protected void doGet(HttpServletRequest request, HttpServletResponse response)
//             throws ServletException, IOException {
//         HttpSession session = request.getSession(false);
//         if (session == null || session.getAttribute("userId") == null) {
//             response.sendRedirect(request.getContextPath() + "/login");
//             return;
//         }

//         String userRole = (String) session.getAttribute("userRole");
//         if (userRole == null) {
//             userRole = (String) session.getAttribute("roleName");
//         }

//         // Check if user is manager
//         if (!"manager".equals(userRole)) {
//             response.sendRedirect(request.getContextPath() + "/login");
//             return;
//         }

//         String action = request.getParameter("action");

//         if (action == null || action.equals("list")) {
//             handleListOrders(request, response);
//         } else if (action.equals("detail")) {
//             handleDetailOrder(request, response);
//         } else if (action.equals("approve")) {
//             handleApproveOrder(request, response);
//         } else if (action.equals("reject")) {
//             handleRejectOrder(request, response);
//         } else {
//             response.sendRedirect(request.getContextPath() + "/manager/orders?action=list");
//         }
//     }

//     private void handleListOrders(HttpServletRequest request, HttpServletResponse response)
//             throws ServletException, IOException {
//         // Lấy các tham số tìm kiếm
//         String searchContract = request.getParameter("searchContract");
//         String searchCustomer = request.getParameter("searchCustomer");
//         String searchStatus = request.getParameter("searchStatus");

//         List<Order> orders;

//         // Nếu có tìm kiếm, gọi method search
//         if ((searchContract != null && !searchContract.trim().isEmpty()) ||
//             (searchCustomer != null && !searchCustomer.trim().isEmpty()) ||
//             (searchStatus != null && !searchStatus.trim().isEmpty())) {
//             orders = orderDAO.searchOrders(searchContract, searchCustomer, searchStatus, null, null);
//         } else {
//             // Hiển thị tất cả orders
//             orders = orderDAO.findAll();
//         }

//         request.setAttribute("orders", orders);
//         request.setAttribute("userRole", "manager");
//         request.getRequestDispatcher("/view/order/manager-order-list.jsp").forward(request, response);
//     }

//     private void handleDetailOrder(HttpServletRequest request, HttpServletResponse response)
//             throws ServletException, IOException {
//         String idParam = request.getParameter("id");
//         if (idParam == null) {
//             response.sendRedirect(request.getContextPath() + "/manager/orders?action=list");
//             return;
//         }

//         try {
//             int orderId = Integer.parseInt(idParam);
//             Order order = orderDAO.findById(orderId);

//             if (order != null) {
//                 request.setAttribute("order", order);
//                 request.setAttribute("userRole", "manager");
//                 request.getRequestDispatcher("/view/order/manager-order-detail.jsp").forward(request, response);
//             } else {
//                 response.sendRedirect(request.getContextPath() + "/manager/orders?action=list&error=notfound");
//             }
//         } catch (NumberFormatException e) {
//             response.sendRedirect(request.getContextPath() + "/manager/orders?action=list&error=invalid");
//         }
//     }

//     private void handleApproveOrder(HttpServletRequest request, HttpServletResponse response)
//             throws ServletException, IOException {
//         String idParam = request.getParameter("id");
//         if (idParam == null) {
//             response.sendRedirect(request.getContextPath() + "/manager/orders?action=list");
//             return;
//         }

//         HttpSession session = request.getSession(false);
//         Integer userId = (Integer) session.getAttribute("userId");
//         if (userId == null) {
//             response.sendRedirect(request.getContextPath() + "/login");
//             return;
//         }

//         try {
//             int orderId = Integer.parseInt(idParam);
//             boolean success = orderDAO.updateStatusWithApprover(orderId, "APPROVED", userId);

//             if (success) {
//                 // Create contract from approved order (so it appears in Contract list)
//                 boolean contractOkOrExists = false;
//                 String contractCode = null;
//                 try {
//                     Order order = orderDAO.findById(orderId);
//                     if (order != null) {
//                         // Ensure order has a contract code (some data may be missing)
//                         if (order.getContractCode() == null || order.getContractCode().trim().isEmpty()) {
//                             String generated = contractDAO.getNextContractCode();
//                             order.setContractCode(generated);
//                             orderDAO.update(order);
//                             LOGGER.info("[OrderApprove->Contract] Generated missing contractCode=" + generated + " for orderId=" + orderId);
//                         }

//                         contractCode = order.getContractCode();
//                         if (contractCode == null || contractCode.trim().isEmpty()) {
//                             LOGGER.warning("[OrderApprove->Contract][FAILED] contractCode is empty after generation (orderId=" + orderId + ")");
//                             contractOkOrExists = false;
//                             // stop creation attempt
//                             throw new IllegalStateException("Missing contractCode for orderId=" + orderId);
//                         }
//                         Integer existingContractId = contractDAO.findIdByContractCode(order.getContractCode());
//                         if (existingContractId == null) {
//                             User customer = userDAO.findByUsername(order.getCustomerName());
//                             if (customer == null) {
//                                 // fallback: try full_name match
//                                 customer = userDAO.findByFullName(order.getCustomerName());
//                             }
//                             if (customer == null) {
//                                 LOGGER.warning("[OrderApprove->Contract][FAILED] Customer not found by username/full_name: " + order.getCustomerName()
//                                         + " (orderId=" + orderId + ")");
//                             } else {
//                                 Integer machineTypeId = null;
//                                 if (order.getMachineId() != null) {
//                                     Machine m = machineDAO.findById(order.getMachineId());
//                                     if (m != null) {
//                                         machineTypeId = m.getMachineTypeId();
//                                     }
//                                 }

//                                 // Block contract creation if machine is not rentable/active
//                                 if (order.getMachineId() != null) {
//                                     Machine m = machineDAO.findById(order.getMachineId());
//                                     if (m != null) {
//                                         String mStatus = m.getStatus() != null ? m.getStatus().trim().toUpperCase() : "UNKNOWN";
//                                         boolean rentable = m.getIsRentable() != null ? m.getIsRentable() : false;
//                                         if (!"ACTIVE".equals(mStatus) || !rentable) {
//                                             LOGGER.warning("[OrderApprove->Contract][BLOCKED] machineId=" + m.getId()
//                                                     + ", machineCode=" + m.getMachineCode()
//                                                     + ", status=" + mStatus
//                                                     + ", isRentable=" + rentable
//                                                     + " (orderId=" + orderId + ")");
//                                             contractOkOrExists = false;
//                                             // do not create contract
//                                             throw new IllegalStateException("Machine not active/rentable: " + mStatus);
//                                         }
//                                     }
//                                 }

//                                 // Block if startDate is in the past
//                                 if (order.getStartDate() != null) {
//                                     Date today = Date.valueOf(LocalDate.now());
//                                     if (order.getStartDate().before(today)) {
//                                         LOGGER.warning("[OrderApprove->Contract][BLOCKED] startDate in past: "
//                                                 + order.getStartDate() + " (orderId=" + orderId + ")");
//                                         contractOkOrExists = false;
//                                         throw new IllegalStateException("Start date in the past");
//                                     }
//                                 }

//                                 Contract contract = Contract.builder()
//                                         .contractCode(order.getContractCode())
//                                         .customerId(customer.getId())
//                                         .managerId(userId) // approver manager
//                                         .startDate(order.getStartDate())
//                                         .endDate(order.getEndDate())
//                                         .status("APPROVED")
//                                         .customerName(order.getCustomerName())
//                                         .customerPhone(order.getCustomerPhone())
//                                         .customerAddress(order.getCustomerAddress())
//                                         .machineId(order.getMachineId())
//                                         .machineTypeId(machineTypeId)
//                                         .quantity(order.getQuantity())
//                                         .totalCost(order.getTotalCost())
//                                         .serviceDescription(order.getServiceDescription())
//                                         // backward compatibility: store description in note too
//                                         .note(order.getServiceDescription())
//                                         .build();

//                                 int newContractId = contractDAO.insert(contract);
//                                 if (newContractId > 0) {
//                                     LOGGER.info("[OrderApprove->Contract][OK] Created contractId=" + newContractId
//                                             + " from orderId=" + orderId + ", code=" + order.getContractCode());
//                                     contractOkOrExists = true;
//                                 } else {
//                                     LOGGER.warning("[OrderApprove->Contract][FAILED] Insert returned 0 for orderId="
//                                             + orderId + ", code=" + order.getContractCode()
//                                             + ", error=" + contractDAO.getLastError());
//                                 }
//                             }
//                         } else {
//                             LOGGER.info("[OrderApprove->Contract][SKIP] Contract already exists: contractId="
//                                     + existingContractId + ", code=" + order.getContractCode());
//                             contractOkOrExists = true;
//                         }
//                     }
//                 } catch (Exception e) {
//                     LOGGER.log(Level.SEVERE, "[OrderApprove->Contract][ERROR]", e);
//                 }

//                 // Always take manager to Contract list after approving
//                 if (contractOkOrExists) {
//                     String msg = "Duyệt đơn thành công. Hợp đồng đã được tạo";
//                     if (contractCode != null) {
//                         msg += " (" + contractCode + ")";
//                     }
//                     response.sendRedirect(request.getContextPath() + "/manager/contracts?success=" + java.net.URLEncoder.encode(msg, "UTF-8"));
//                 } else {
//                     String err = "Duyệt đơn thành công nhưng tạo hợp đồng thất bại";
//                     if (contractCode != null) {
//                         err += " (" + contractCode + ")";
//                     }
//                     response.sendRedirect(request.getContextPath() + "/manager/contracts?error=" + java.net.URLEncoder.encode(err, "UTF-8"));
//                 }
//             } else {
//                 response.sendRedirect(request.getContextPath() + "/manager/orders?action=list&error=failed");
//             }
//         } catch (NumberFormatException e) {
//             response.sendRedirect(request.getContextPath() + "/manager/orders?action=list&error=invalid");
//         }
//     }

//     private void handleRejectOrder(HttpServletRequest request, HttpServletResponse response)
//             throws ServletException, IOException {
//         String idParam = request.getParameter("id");
//         if (idParam == null) {
//             response.sendRedirect(request.getContextPath() + "/manager/orders?action=list");
//             return;
//         }

//         HttpSession session = request.getSession(false);
//         Integer userId = (Integer) session.getAttribute("userId");
//         if (userId == null) {
//             response.sendRedirect(request.getContextPath() + "/login");
//             return;
//         }

//         try {
//             int orderId = Integer.parseInt(idParam);
//             boolean success = orderDAO.updateStatusWithApprover(orderId, "REJECTED", userId);

//             if (success) {
//                 response.sendRedirect(request.getContextPath() + "/manager/orders?action=list&success=rejected");
//             } else {
//                 response.sendRedirect(request.getContextPath() + "/manager/orders?action=list&error=failed");
//             }
//         } catch (NumberFormatException e) {
//             response.sendRedirect(request.getContextPath() + "/manager/orders?action=list&error=invalid");
//         }
//     }

//     @Override
//     protected void doPost(HttpServletRequest request, HttpServletResponse response)
//             throws ServletException, IOException {
//         doGet(request, response);
//     }
// }
