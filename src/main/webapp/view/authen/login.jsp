

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Login - Argo Machine Management</title>

        <!-- Bootstrap 5 CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

        <!-- Font Awesome -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

      
    </head>
    <body>
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-5 col-lg-4">
                    <div class="card login-card">
                        <div class="card-body p-5">
                            <!-- Header -->
                            <h2 class="text-center mb-4">Login</h2>

                            <!-- Error Message -->
                            <% if (request.getAttribute("error") != null) { %>
                            <div class="alert alert-danger" role="alert">
                                <i class="fas fa-exclamation-circle"></i>
                                <%= request.getAttribute("error") %>
                            </div>
                            <% } %>

                            <!-- Success Message -->
                            <% if (request.getAttribute("success") != null) { %>
                            <div class="alert alert-success" role="alert">
                                <i class="fas fa-check-circle"></i>
                                <%= request.getAttribute("success") %>
                            </div>
                            <% } %>

                            <!-- Login Form -->
                            <form action="${pageContext.request.contextPath}/login" method="POST">
                                <!-- Username -->
                                <div class="mb-3">
                                    <label for="username" class="form-label">Username</label>
                                    <div class="input-group">
                                        <span class="input-group-text">
                                            <i class="fas fa-user"></i>
                                        </span>
                                        <input 
                                            type="text" 
                                            class="form-control" 
                                            id="username" 
                                            name="username" 
                                            placeholder="a@gmail.com"
                                            value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>"
                                            required>
                                    </div>
                                </div>

                                <!-- Password -->
                                <div class="mb-3">
                                    <label for="password" class="form-label">Password</label>
                                    <div class="input-group">
                                        <span class="input-group-text">
                                            <i class="fas fa-lock"></i>
                                        </span>
                                        <input 
                                            type="password" 
                                            class="form-control" 
                                            id="password" 
                                            name="password" 
                                            placeholder="**********"
                                            required>
                                    </div>
                                </div>

                                <!-- Remember Me & Forgot Password -->
                                <div class="d-flex justify-content-between align-items-center mb-3">
                                    <div class="form-check">
                                        <input class="form-check-input" type="checkbox" id="remember" name="remember">
                                        <label class="form-check-label" for="remember">
                                            Remember me
                                        </label>
                                    </div>
                                    <a class="text-decoration-none">
                                        forgot password
                                    </a>
                                </div>
                                <div class="d-flex justify-content-between align-items-center mb-2 gap-2">
                                    <button type="submit" class="btn btn-primary w-50">
                                        <i class="fas fa-sign-in-alt"></i> Login
                                    </button>

                                    <a class="btn btn-outline-primary w-50">
                                        <i class="fas fa-user-plus"></i> Sign up
                                    </a>
                                </div>



                                <!-- Google Login Button -->
                                <button type="button" class="btn btn-outline-secondary w-100">
                                    <i class="fab fa-google"></i> Login with google
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Bootstrap 5 JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
