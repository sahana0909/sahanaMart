<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Admin Panel | SahanaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="margin-bottom: 24px;">
    <h1 style="font-size: 26px; font-weight: 800;">Platform Administration (F7)</h1>
    <p style="color: var(--gray-500); font-size: 14px;">Platform metrics, user oversight, global orders, and catalog moderation</p>
</div>

<!-- Key Metrics Cards -->
<div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin-bottom: 30px;">
    <div class="card" style="margin-bottom: 0;">
        <span style="font-size: 12px; color: var(--gray-500); text-transform: uppercase; font-weight: 700;">Total Users</span>
        <div style="font-size: 28px; font-weight: 800; color: var(--primary); margin-top: 6px;"><c:out value="${users.size()}"/></div>
    </div>
    <div class="card" style="margin-bottom: 0;">
        <span style="font-size: 12px; color: var(--gray-500); text-transform: uppercase; font-weight: 700;">Total Listings</span>
        <div style="font-size: 28px; font-weight: 800; color: var(--secondary); margin-top: 6px;"><c:out value="${products.size()}"/></div>
    </div>
    <div class="card" style="margin-bottom: 0;">
        <span style="font-size: 12px; color: var(--gray-500); text-transform: uppercase; font-weight: 700;">Total Orders Placed</span>
        <div style="font-size: 28px; font-weight: 800; color: var(--success); margin-top: 6px;"><c:out value="${orders.size()}"/></div>
    </div>
</div>

<!-- Section 1: Product Moderation -->
<div class="card">
    <h2 style="font-size: 18px; font-weight: 800; margin-bottom: 16px;">Listing Moderation (F7)</h2>
    <div class="table-responsive">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Product Name</th>
                    <th>Category</th>
                    <th>Price</th>
                    <th>Seller</th>
                    <th>Status</th>
                    <th>Moderation Action</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="p" items="${products}">
                    <tr>
                        <td>#<c:out value="${p.id}"/></td>
                        <td style="font-weight: 600;"><c:out value="${p.name}"/></td>
                        <td><c:out value="${p.categoryName}"/></td>
                        <td>₹<fmt:formatNumber value="${p.price}" pattern="#,##0.00"/></td>
                        <td><c:out value="${p.sellerName}"/></td>
                        <td>
                            <span class="role-badge ${p.active ? 'BUYER' : 'ADMIN'}">
                                <c:out value="${p.active ? 'Active' : 'Hidden / Inactive'}"/>
                            </span>
                        </td>
                        <td>
                            <form action="${pageContext.request.contextPath}/admin/listings/moderate" method="post" style="display: inline;">
                                <input type="hidden" name="productId" value="${p.id}" />
                                <input type="hidden" name="active" value="${!p.active}" />
                                <button type="submit" class="btn btn-sm ${p.active ? 'btn-danger' : 'btn-success'}">
                                    <c:out value="${p.active ? 'Deactivate' : 'Activate'}"/>
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<!-- Section 2: Registered Users Oversight -->
<div class="card">
    <h2 style="font-size: 18px; font-weight: 800; margin-bottom: 16px;">Registered Users (F7)</h2>
    <div class="table-responsive">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Full Name</th>
                    <th>Email Address</th>
                    <th>Role</th>
                    <th>Phone</th>
                    <th>Joined At</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="u" items="${users}">
                    <tr>
                        <td>#<c:out value="${u.id}"/></td>
                        <td style="font-weight: 700;"><c:out value="${u.fullName}"/></td>
                        <td><c:out value="${u.email}"/></td>
                        <td><span class="role-badge ${u.role}"><c:out value="${u.role}"/></span></td>
                        <td><c:out value="${u.phone != null ? u.phone : '-'}"/></td>
                        <td style="font-size: 13px; color: var(--gray-500);"><c:out value="${u.createdAt}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<!-- Section 3: All System Orders -->
<div class="card">
    <h2 style="font-size: 18px; font-weight: 800; margin-bottom: 16px;">System-wide Order Book (F7)</h2>
    <div class="table-responsive">
        <table>
            <thead>
                <tr>
                    <th>Order #</th>
                    <th>Buyer</th>
                    <th>Amount</th>
                    <th>Workflow Status</th>
                    <th>Items</th>
                    <th>Date Placed</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="o" items="${orders}">
                    <tr>
                        <td style="font-weight: 700; color: var(--primary);"><c:out value="${o.orderNumber}"/></td>
                        <td><c:out value="${o.buyerName}"/></td>
                        <td style="font-weight: 700;">₹<fmt:formatNumber value="${o.totalAmount}" pattern="#,##0.00"/></td>
                        <td><span class="status-badge ${o.status}"><c:out value="${o.status}"/></span></td>
                        <td><c:out value="${o.items.size()}"/> items</td>
                        <td style="font-size: 13px; color: var(--gray-500);"><c:out value="${o.createdAt}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
