<%-- Logout Modal Component - Include this in pages that need logout functionality --%>
<!-- Logout Modal -->
<div class="modal fade" id="logoutModal" tabindex="-1" aria-labelledby="logoutModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content" style="border-radius: 10px; box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2); border: none;">
            <div class="modal-body p-5 text-center">

                <!-- Title -->
                <h2 class="mb-4">Logout</h2>

                <!-- Message -->
                <p class="text-muted mb-4">Do you want to logout?</p>

                <!-- Buttons -->
                <div class="d-flex justify-content-center gap-3">
                    <form action="${pageContext.request.contextPath}/logout" method="POST" style="display: inline;">
                        <input type="hidden" name="confirm" value="yes">
                        <button type="submit" class="btn btn-primary px-4">
                            <i class="fas fa-check"></i> Yes
                        </button>
                    </form>
                    <button type="button" class="btn btn-secondary px-4" data-bs-dismiss="modal">
                        <i class="fas fa-times"></i> No
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>
