<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Admin Panel | SahanaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="margin-bottom: 28px;">
    <h1 style="font-size: 28px; font-weight: 800; color: var(--text-main); letter-spacing: -0.5px;">Platform Administration</h1>
    <p style="color: var(--text-secondary); font-size: 14px; margin-top: 4px;">Platform metrics, account oversight, global order book, and catalog moderation</p>
</div>

<!-- Key Metrics Stat Cards -->
<div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 20px; margin-bottom: 32px;">
    <div class="card" style="margin-bottom: 0; border-top: 4px solid var(--primary);">
        <span style="font-size: 12px; color: var(--text-secondary); text-transform: uppercase; font-weight: 800; letter-spacing: 0.5px;">Registered Accounts</span>
        <div style="font-size: 32px; font-weight: 900; color: var(--primary); margin-top: 6px;"><c:out value="${users.size()}"/></div>
        <div style="font-size: 12px; color: var(--teal); font-weight: 600; margin-top: 4px;">Buyers, Sellers & Admins</div>
    </div>
    <div class="card" style="margin-bottom: 0; border-top: 4px solid var(--teal);">
        <span style="font-size: 12px; color: var(--text-secondary); text-transform: uppercase; font-weight: 800; letter-spacing: 0.5px;">Total Marketplace Listings</span>
        <div style="font-size: 32px; font-weight: 900; color: var(--teal-hover); margin-top: 6px;"><c:out value="${products.size()}"/></div>
        <div style="font-size: 12px; color: var(--text-secondary); margin-top: 4px;">Active & Moderated Items</div>
    </div>
    <div class="card" style="margin-bottom: 0; border-top: 4px solid var(--violet);">
        <span style="font-size: 12px; color: var(--text-secondary); text-transform: uppercase; font-weight: 800; letter-spacing: 0.5px;">Total Orders Placed</span>
        <div style="font-size: 32px; font-weight: 900; color: var(--violet); margin-top: 6px;"><c:out value="${orders.size()}"/></div>
        <div style="font-size: 12px; color: var(--success); font-weight: 600; margin-top: 4px;">Platform Transaction Volume</div>
    </div>
</div>

<!-- Section 1: Product Moderation -->
<div class="card">
    <div style="margin-bottom: 18px;">
        <h2 style="font-size: 19px; font-weight: 800; color: var(--text-main);">Listing Moderation</h2>
        <p style="font-size: 13px; color: var(--text-secondary); margin-top: 2px;">Activate or deactivate product listings to maintain marketplace compliance</p>
    </div>
    <div class="table-responsive">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Product Title</th>
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
                        <td><strong style="color: var(--text-secondary);">#<c:out value="${p.id}"/></strong></td>
                        <td style="font-weight: 700; color: var(--text-main);"><c:out value="${p.name}"/></td>
                        <td><span class="product-category" style="margin-bottom: 0;"><c:out value="${p.categoryName}"/></span></td>
                        <td style="font-weight: 800; color: var(--primary);">
                            <span class="currency-symbol">&#8377;</span><fmt:formatNumber value="${p.price}" pattern="#,##0.00"/>
                        </td>
                        <td><strong style="color: var(--text-secondary);"><c:out value="${p.sellerName}"/></strong></td>
                        <td>
                            <span class="role-badge ${p.active ? 'BUYER' : 'ADMIN'}">
                                <c:out value="${p.active ? 'Active' : 'Inactive'}"/>
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
    <div style="margin-bottom: 18px;">
        <h2 style="font-size: 19px; font-weight: 800; color: var(--text-main);">Registered User Accounts</h2>
        <p style="font-size: 13px; color: var(--text-secondary); margin-top: 2px;">Directory of all registered buyers, verified merchants, and platform administrators</p>
    </div>
    <div class="table-responsive">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Full Name</th>
                    <th>Email Address</th>
                    <th>Account Role</th>
                    <th>Phone</th>
                    <th>Registration Date</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="u" items="${users}">
                    <tr>
                        <td><strong style="color: var(--text-secondary);">#<c:out value="${u.id}"/></strong></td>
                        <td style="font-weight: 700; color: var(--text-main);"><c:out value="${u.fullName}"/></td>
                        <td><c:out value="${u.email}"/></td>
                        <td><span class="role-badge ${u.role}"><c:out value="${u.role}"/></span></td>
                        <td><c:out value="${u.phone != null ? u.phone : '-'}"/></td>
                        <td style="font-size: 13px; color: var(--text-secondary);"><c:out value="${u.createdAt}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<!-- Section 3: All System Orders -->
<div class="card">
    <div style="margin-bottom: 18px;">
        <h2 style="font-size: 19px; font-weight: 800; color: var(--text-main);">Global Orders Log</h2>
        <p style="font-size: 13px; color: var(--text-secondary); margin-top: 2px;">Complete ledger of customer orders, transactional totals, and fulfillment states</p>
    </div>
    <div class="table-responsive">
        <table>
            <thead>
                <tr>
                    <th>Order #</th>
                    <th>Buyer</th>
                    <th>Amount</th>
                    <th>Workflow Status</th>
                    <th>Item Count</th>
                    <th>Date Placed</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="o" items="${orders}">
                    <tr>
                        <td style="font-weight: 800; color: var(--primary);"><c:out value="${o.orderNumber}"/></td>
                        <td><c:out value="${o.buyerName}"/></td>
                        <td style="font-weight: 800; color: var(--text-main);">
                            <span class="currency-symbol">&#8377;</span><fmt:formatNumber value="${o.totalAmount}" pattern="#,##0.00"/>
                        </td>
                        <td><span class="status-badge ${o.status}"><c:out value="${o.status}"/></span></td>
                        <td><c:out value="${o.items.size()}"/> items</td>
                        <td style="font-size: 13px; color: var(--text-secondary);"><c:out value="${o.createdAt}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
