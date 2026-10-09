# Sprint Retrospective Log (RETRO.md)

Anna University R2025 Semester 3 Capstone Sprint Tracking.

| Sprint / Week | Dates | What Worked | What Didn't | One Change for Next Sprint |
|---------------|-------|-------------|-------------|----------------------------|
| **Kickoff** | Jul 24 – Jul 27 | Problem statement, package skeleton, and D2 Use Case diagram created swiftly. | Clarifying Tomcat 9 javax vs jakarta servlet imports. | Standardized strictly on javax.servlet.* 4.0. |
| **Week 1** | Jul 27 – Aug 2 | HikariCP pool and jBCrypt password hashing integrated seamlessly. | Initial H2 connection string needed AUTO_SERVER flag. | Enabled AUTO_SERVER=TRUE for persistent DB. |
| **Week 2** | Aug 3 – Aug 9 | Core shopping flow (browse, cart, checkout) and seed data completed for MVP. | Cart item quantity edge cases when adding same item multiple times. | Added UPSERT logic in CartDAO.addItem. |
| **Week 3** | Aug 10 – Aug 16 | DTO encapsulation and ApiResponse envelope standardized across endpoints. | Manual entity-to-DTO conversion boilerplates. | Introduced clean static factory methods on DTOs. |
| **Week 4** | Aug 17 – Aug 23 | Seller dashboard and Admin user/order management completed. | Admin moderation state didn't instantly hide from browse view. | Added `activeOnly` filter parameter in ProductDAO. |
| **Week 5** | Aug 24 – Aug 30 | Search refinement, category pills, and O2 order status workflow established. | Order status transition was initially unconstrained. | Enforced state machine validation in OrderStatus enum. |
| **Week 6** | Aug 31 – Sep 6 | Product reviews with star ratings (F8) and edge case validation complete. | Needed to prevent buyers from reviewing items before delivery. | Added `hasPurchasedAndCompleted` verification check. |
| **Week 7** | Sep 7 – Sep 13 | Full security hardening verified; 100% PreparedStatement compliance; load test created. | Static analysis identified need for explicit session timeout. | Set 30-minute session timeout in web.xml. |
| **Week 8** | Sep 14 – Sep 20 | WAR packaging, Tomcat 9 live deployment, health endpoint verified, release v1.0.0 tagged. | Tomcat context path URL redirection edge cases. | Supported both `/` and `/sahanamart/` deployments cleanly. |
| **Week 9** | Sep 21 – Sep 29 | Comprehensive integration audit, feedback aggregation, automated load script verification. | Review form order ID linkage bug and route naming divergences between web.xml and servlets identified. | Planned architectural fixes for review workflow and synchronized routing. |
| **Final Review** | Sep 30 – Oct 10 | AI Chatbot completed with Smart FAQ + Gemini fallback, IDOR vulnerability neutralized, blue/black/white UI polish, 40 tests passing (100% green). | Rate limiting and caching in server sessions needed thread-safe data structures. | Implemented sliding-window ConcurrentHashMap rate limiting and session caching. Ready for Anna University Capstone evaluation. |
