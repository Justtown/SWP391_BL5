package com.mycompany.argomachinemanagement.src.controller.dashboard.admin;

import com.mycompany.argomachinemanagement.src.dal.AccountDAO;
import com.mycompany.argomachinemanagement.src.entity.Account;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet(name = "MyProfileController", urlPatterns = {"/my-profile"})
public class MyProfileController extends HttpServlet {

    private final AccountDAO accountDAO = new AccountDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer userId = 1;
        Account account = accountDAO.findById(userId);
        request.setAttribute("account", account);

        request.getRequestDispatcher("/view/common/home/my-profile.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        Integer userId = 1;
        Account account = accountDAO.findById(userId);
        if (account == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String avatar = request.getParameter("avatar");
        String birthdateStr = request.getParameter("birthdate");

        account.setFullName(fullName);
        account.setEmail(email);
        account.setPhone(phone);
        account.setAddress(address);
        account.setAvatar(avatar);

        if (birthdateStr != null && !birthdateStr.isBlank()) {
            account.setBirthdate(LocalDate.parse(birthdateStr));
        } else {
            account.setBirthdate(null);
        }

        boolean updated = accountDAO.updateProfile(account);
        if (updated) {
            request.setAttribute("success", "Update profile successfully!");
        } else {
            request.setAttribute("error", "Update profile failed!");
        }

        request.setAttribute("account", account);
        request.getRequestDispatcher("/view/common/home/my-profile.jsp")
                .forward(request, response);
    }
}
