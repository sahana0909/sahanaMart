<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Seller Dashboard | SahanaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
    <div>
        <h1 style="font-size: 26px; font-weight: 800;">Seller Management Dashboard</h1>
        <p style="color: var(--gray-500); font-size: 14px;">Manage your active product inventory and listings (F2)</p>
    </div>
    <button type="button" class="btn btn-primary" onclick="toggleListingModal('createModal', true)">
        + Add New Product Listing
    </button>
</div>

<!-- Listings Table -->
<div class="table-responsive">
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Product</th>
                <th>Category</th>
                <th>Price</th>
                <th>Inventory Stock</th>
                <th>Status</th>
                <th>Rating</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${not empty products}">
                    <c:forEach var="p" items="${products}">
                        <tr>
                            <td>#<c:out value="${p.id}"/></td>
                            <td>
                                <div style="display: flex; align-items: center; gap: 12px;">
                                    <img src="<c:out value='${p.imageUrl}'/>" alt="<c:out value='${p.name}'/>"
                                         style="width: 44px; height: 44px; object-fit: cover; border-radius: 6px;" />
                                    <div>
                                        <div style="font-weight: 700; color: var(--gray-900); max-width: 250px;" class="product-title">
                                            <c:out value="${p.name}"/>
                                        </div>
                                    </div>
                                </div>
                            </td>
                            <td><c:out value="${p.categoryName}"/></td>
                            <td style="font-weight: 700;">₹<fmt:formatNumber value="${p.price}" pattern="#,##0.00"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${p.stockQty <= 5}">
                                        <span style="color: var(--danger); font-weight: 700;">⚠️ <c:out value="${p.stockQty}"/> (Low Stock)</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color: var(--success); font-weight: 600;"><c:out value="${p.stockQty}"/> in stock</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <span class="role-badge ${p.active ? 'BUYER' : 'ADMIN'}">
                                    <c:out value="${p.active ? 'Active' : 'Inactive'}"/>
                                </span>
                            </td>
                            <td>
                                <span style="color: var(--accent); font-weight: 700;">★ <fmt:formatNumber value="${p.averageRating}" pattern="0.0"/></span>
                                <span style="font-size: 12px; color: var(--gray-500);">(<c:out value="${p.reviewCount}"/>)</span>
                            </td>
                            <td>
                                <div style="display: flex; gap: 8px;">
                                    <button type="button" class="btn btn-outline btn-sm"
                                            onclick="openEditModal('${p.id}', '${p.name}', '${p.price}', '${p.stockQty}', '${p.categoryId}', '${p.imageUrl}', '${p.active}')">
                                        Edit
                                    </button>
                                    <form action="${pageContext.request.contextPath}/seller/product/delete" method="post"
                                          onsubmit="return confirm('Are you sure you want to permanently delete this listing?');" style="display: inline;">
                                        <input type="hidden" name="productId" value="${p.id}" />
                                        <button type="submit" class="btn btn-outline btn-sm" style="color: var(--danger);">Delete</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="8" style="text-align: center; padding: 40px; color: var(--gray-500);">
                            You have not listed any products yet. Click "+ Add New Product Listing" to start.
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
</div>

<!-- Modal: Create Product -->
<div id="createModal" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 1000; align-items: center; justify-content: center;">
    <div class="card" style="width: 100%; max-width: 550px; max-height: 90vh; overflow-y: auto;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
            <h2 style="font-size: 20px; font-weight: 800;">Create New Product Listing</h2>
            <button type="button" onclick="toggleListingModal('createModal', false)" style="background: none; border: none; font-size: 20px; cursor: pointer;">✕</button>
        </div>

        <form action="${pageContext.request.contextPath}/seller/product/create" method="post">
            <div class="form-group">
                <label for="createName">Product Title</label>
                <input type="text" id="createName" name="name" class="form-control" required placeholder="e.g. Wireless Ergonomic Keyboard" />
            </div>

            <div class="form-group">
                <label for="createCategory">Category</label>
                <select id="createCategory" name="categoryId" class="form-control" required>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.id}"><c:out value="${cat.name}"/></option>
                    </c:forEach>
                </select>
            </div>

            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 14px;">
                <div class="form-group">
                    <label for="createPrice">Price (₹)</label>
                    <input type="number" step="0.01" min="1" id="createPrice" name="price" class="form-control" required placeholder="1499.00" />
                </div>
                <div class="form-group">
                    <label for="createStock">Stock Quantity</label>
                    <input type="number" min="0" id="createStock" name="stockQty" class="form-control" required placeholder="50" />
                </div>
            </div>

            <div class="form-group">
                <label for="createImage">Image URL</label>
                <input type="url" id="createImage" name="imageUrl" class="form-control"
                       value="https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&q=80" required />
            </div>

            <div class="form-group">
                <label for="createDesc">Detailed Description</label>
                <textarea id="createDesc" name="description" rows="3" class="form-control" required placeholder="Explain features, specifications, and warranty"></textarea>
            </div>

            <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px;">
                <button type="button" class="btn btn-outline" onclick="toggleListingModal('createModal', false)">Cancel</button>
                <button type="submit" class="btn btn-primary">Publish Listing</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal: Edit Product -->
<div id="editModal" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 1000; align-items: center; justify-content: center;">
    <div class="card" style="width: 100%; max-width: 550px; max-height: 90vh; overflow-y: auto;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
            <h2 style="font-size: 20px; font-weight: 800;">Edit Product Listing</h2>
            <button type="button" onclick="toggleListingModal('editModal', false)" style="background: none; border: none; font-size: 20px; cursor: pointer;">✕</button>
        </div>

        <form action="${pageContext.request.contextPath}/seller/product/edit" method="post">
            <input type="hidden" id="editProductId" name="productId" />

            <div class="form-group">
                <label for="editName">Product Title</label>
                <input type="text" id="editName" name="name" class="form-control" required />
            </div>

            <div class="form-group">
                <label for="editCategory">Category</label>
                <select id="editCategory" name="categoryId" class="form-control" required>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.id}"><c:out value="${cat.name}"/></option>
                    </c:forEach>
                </select>
            </div>

            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 14px;">
                <div class="form-group">
                    <label for="editPrice">Price (₹)</label>
                    <input type="number" step="0.01" min="1" id="editPrice" name="price" class="form-control" required />
                </div>
                <div class="form-group">
                    <label for="editStock">Stock Quantity</label>
                    <input type="number" min="0" id="editStock" name="stockQty" class="form-control" required />
                </div>
            </div>

            <div class="form-group">
                <label for="editImage">Image URL</label>
                <input type="url" id="editImage" name="imageUrl" class="form-control" required />
            </div>

            <div class="form-group">
                <label for="editDesc">Detailed Description</label>
                <textarea id="editDesc" name="description" rows="3" class="form-control" required></textarea>
            </div>

            <div class="form-group">
                <label style="display: flex; align-items: center; gap: 8px;">
                    <input type="checkbox" id="editActive" name="active" value="true" />
                    <span>Active Listing (Visible to buyers)</span>
                </label>
            </div>

            <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px;">
                <button type="button" class="btn btn-outline" onclick="toggleListingModal('editModal', false)">Cancel</button>
                <button type="submit" class="btn btn-primary">Save Changes</button>
            </div>
        </form>
    </div>
</div>

<script>
function toggleListingModal(modalId, show) {
    const el = document.getElementById(modalId);
    if (el) {
        el.style.display = show ? "flex" : "none";
    }
}

function openEditModal(id, name, price, stock, categoryId, imageUrl, active) {
    document.getElementById('editProductId').value = id;
    document.getElementById('editName').value = name;
    document.getElementById('editPrice').value = price;
    document.getElementById('editStock').value = stock;
    document.getElementById('editCategory').value = categoryId;
    document.getElementById('editImage').value = imageUrl;
    document.getElementById('editDesc').value = name;
    document.getElementById('editActive').checked = (active === 'true' || active === true);
    toggleListingModal('editModal', true);
}
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
