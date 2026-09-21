-- SahanaMart Seed Data
-- Realistic catalog items and demo categories

INSERT INTO categories (name, description) VALUES
('Electronics', 'Premium gadgets, laptops, audio gear, and accessories'),
('Fashion & Apparel', 'Contemporary clothing, footwear, and casual wear for men and women'),
('Home & Kitchen', 'Modern cookware, coffee makers, smart appliances, and lifestyle goods'),
('Books & Stationery', 'Technical guides, software engineering classics, and notebooks');

-- Insert Products for seller (seller_id = 2, from initial seed)
INSERT INTO products (seller_id, category_id, name, description, price, stock_qty, image_url, is_active) VALUES
(2, 1, 'Sony WH-1000XM5 Wireless Noise-Cancelling Headphones', 'Industry-leading noise cancellation with two processors and 8 microphones. Up to 30-hour battery life with quick charging, crystal clear hands-free calling, and multipoint Bluetooth connection.', 29990.00, 25, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&q=80', TRUE),
(2, 1, 'Apple MacBook Air M2 (13.6-inch, 16GB RAM, 512GB SSD)', 'Strikingly thin design with the lightning-fast Apple M2 chip. 13.6-inch Liquid Retina display, 1080p FaceTime HD camera, MagSafe 3 charging port, and all-day battery life up to 18 hours.', 94990.00, 15, 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600&q=80', TRUE),
(2, 1, 'Logitech MX Master 3S Ergonomic Wireless Mouse', 'Performance wireless mouse with 8K DPI tracking on glass, quiet clicks, MagSpeed electromagnetic scrolling, and customizable thumb buttons.', 8495.00, 40, 'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=600&q=80', TRUE),
(2, 1, 'Samsung 27-inch 4K UHD IPS Designer Monitor', 'Ultra HD 3840x2160 IPS panel with HDR10 support, borderless three-sided design, height-adjustable stand, and USB-C connectivity.', 24999.00, 18, 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=600&q=80', TRUE),
(2, 2, 'Classic Oxford Cotton Button-Down Shirt', 'Tailored from 100% breathable organic Oxford cotton. Perfect for both office smart-casual and weekend leisure wear. Pre-shrunk and easy care.', 1899.00, 50, 'https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600&q=80', TRUE),
(2, 2, 'Slim Fit Stretch Denim Jeans (Deep Indigo)', 'Crafted from premium 12oz denim with 2% elastane for maximum flexibility and comfort throughout the day. Classic five-pocket styling.', 2499.00, 35, 'https://images.unsplash.com/photo-1542272604-787c3835535d?w=600&q=80', TRUE),
(2, 3, 'Stainless Steel Double-Wall Insulated Tumbler 500ml', 'Keeps beverages piping hot for 12 hours or ice-cold for 24 hours. Leakproof lid, condensation-free exterior, and BPA-free food-grade stainless steel.', 899.00, 60, 'https://images.unsplash.com/photo-1517256064527-09c73fc73e38?w=600&q=80', TRUE),
(2, 3, 'Ceramic Pour-Over Coffee Dripper Set with Glass Carafe', 'Handcrafted ceramic V-dripper paired with a 600ml heat-resistant borosilicate glass server. Includes measuring scoop and 40 unbleached paper filters.', 1599.00, 30, 'https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=600&q=80', TRUE),
(2, 4, 'Designing Data-Intensive Applications by Martin Kleppmann', 'The definitive guide to distributed systems architecture, reliable storage engines, streaming processing, and fault-tolerant data pipelines.', 1450.00, 45, 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600&q=80', TRUE),
(2, 4, 'Clean Code: A Handbook of Agile Software Craftsmanship', 'Legendary software engineer Robert C. Martin provides timeless principles, patterns, and practices for writing elegant, maintainable Java code.', 999.00, 50, 'https://images.unsplash.com/photo-1532012164546-f432f2e3777f?w=600&q=80', TRUE);

-- Sample initial delivered order for Buyer (buyer_id = 3) to enable review demo
INSERT INTO orders (order_number, buyer_id, total_amount, status, shipping_address, payment_status)
VALUES ('SM-20260901-1001', 3, 29990.00, 'DELIVERED', 'Flat 402, Green Valley Apartments, Anna Nagar, Chennai 600040', 'COMPLETED');

INSERT INTO order_items (order_id, product_id, seller_id, quantity, unit_price, total_price)
VALUES (1, 1, 2, 1, 29990.00, 29990.00);

-- Initial review for product 1
INSERT INTO reviews (product_id, buyer_id, order_id, rating, comment)
VALUES (1, 3, 1, 5, 'Phenomenal active noise cancellation and super comfortable during long study and coding sessions! Highly recommend.');
