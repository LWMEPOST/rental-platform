-- Fix Admin Password to MD5(123456)
UPDATE sys_user SET password = 'e10adc3949ba59abbe56e057f20f883e' WHERE username = 'admin';

-- Create Test User (password: 123456)
INSERT INTO sys_user (username, password, nickname, role, avatar) 
SELECT 'user', 'e10adc3949ba59abbe56e057f20f883e', '测试用户', 'CLIENT', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
FROM DUAL WHERE NOT EXISTS (SELECT * FROM sys_user WHERE username = 'user');

-- Mock Data for Device Categories (Flat)
INSERT INTO device_category (name, sort_order) SELECT '摄影摄像', 1 FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category WHERE name = '摄影摄像');
INSERT INTO device_category (name, sort_order) SELECT '相机', 2 FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category WHERE name = '相机');
INSERT INTO device_category (name, sort_order) SELECT '镜头', 3 FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category WHERE name = '镜头');
INSERT INTO device_category (name, sort_order) SELECT '无人机', 4 FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category WHERE name = '无人机');
INSERT INTO device_category (name, sort_order) SELECT '游戏电玩', 5 FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category WHERE name = '游戏电玩');
INSERT INTO device_category (name, sort_order) SELECT '游戏主机', 6 FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category WHERE name = '游戏主机');
INSERT INTO device_category (name, sort_order) SELECT 'VR设备', 7 FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category WHERE name = 'VR设备');
INSERT INTO device_category (name, sort_order) SELECT '户外露营', 8 FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category WHERE name = '户外露营');

-- Mock Data for Devices (No category_id)
-- 1. Sony A7M4
INSERT INTO device (name, brand, model, description, main_image, deposit_amount, rental_price, stock_quantity, status)
SELECT 'Sony/索尼 A7M4 全画幅微单相机', 'Sony', 'ILCE-7M4', '3300万像素，4K60p视频录制，实时眼部对焦。', 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80', 15000.00, 200.00, 10, 1
FROM DUAL WHERE NOT EXISTS (SELECT * FROM device WHERE model = 'ILCE-7M4');

-- 2. Canon EOS R5
INSERT INTO device (name, brand, model, description, main_image, deposit_amount, rental_price, stock_quantity, status)
SELECT 'Canon/佳能 EOS R5 专业微单', 'Canon', 'EOS R5', '4500万像素，8K视频，机身防抖。', 'https://images.unsplash.com/photo-1519183071298-a2962feb14f4?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80', 20000.00, 300.00, 5, 1
FROM DUAL WHERE NOT EXISTS (SELECT * FROM device WHERE model = 'EOS R5');

-- 3. DJI Mavic 3
INSERT INTO device (name, brand, model, description, main_image, deposit_amount, rental_price, stock_quantity, status)
SELECT 'DJI/大疆 Mavic 3 航拍无人机', 'DJI', 'Mavic 3', '4/3 CMOS哈苏相机，46分钟续航，全向避障。', 'https://images.unsplash.com/photo-1473968512647-3e447244af8f?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80', 12000.00, 250.00, 8, 1
FROM DUAL WHERE NOT EXISTS (SELECT * FROM device WHERE model = 'Mavic 3');

-- 4. Sony 24-70mm GM II
INSERT INTO device (name, brand, model, description, main_image, deposit_amount, rental_price, stock_quantity, status)
SELECT 'Sony/索尼 FE 24-70mm F2.8 GM II', 'Sony', 'SEL2470GM2', '轻量化大三元标准变焦镜头，G Master画质。', 'https://images.unsplash.com/photo-1617005082133-548c4dd27f35?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80', 10000.00, 150.00, 15, 1
FROM DUAL WHERE NOT EXISTS (SELECT * FROM device WHERE model = 'SEL2470GM2');

-- 5. Nintendo Switch OLED
INSERT INTO device (name, brand, model, description, main_image, deposit_amount, rental_price, stock_quantity, status)
SELECT 'Nintendo/任天堂 Switch OLED版', 'Nintendo', 'Switch OLED', '7英寸OLED屏幕，掌机模式色彩更艳丽。', 'https://images.unsplash.com/photo-1578303512597-81e6cc155b3e?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80', 2000.00, 30.00, 20, 1
FROM DUAL WHERE NOT EXISTS (SELECT * FROM device WHERE model = 'Switch OLED');

-- 6. PS5
INSERT INTO device (name, brand, model, description, main_image, deposit_amount, rental_price, stock_quantity, status)
SELECT 'Sony/索尼 PlayStation 5 光驱版', 'Sony', 'PS5', '次世代游戏主机，支持4K 120Hz，光线追踪。', 'https://images.unsplash.com/photo-1606144042614-b2417e99c4e3?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80', 3500.00, 50.00, 10, 1
FROM DUAL WHERE NOT EXISTS (SELECT * FROM device WHERE model = 'PS5');

-- 7. GoPro Hero 11
INSERT INTO device (name, brand, model, description, main_image, deposit_amount, rental_price, stock_quantity, status)
SELECT 'GoPro Hero 11 Black 运动相机', 'GoPro', 'Hero 11', 'HyperSmooth 5.0防抖，5.3K视频，2700万像素。', 'https://images.unsplash.com/photo-1565849904461-04a58ad377e0?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80', 2500.00, 40.00, 12, 1
FROM DUAL WHERE NOT EXISTS (SELECT * FROM device WHERE model = 'Hero 11');

-- Device Mappings
-- Sony A7M4 -> 摄影摄像, 相机
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='ILCE-7M4'), (SELECT id FROM device_category WHERE name='摄影摄像') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='ILCE-7M4') AND category_id=(SELECT id FROM device_category WHERE name='摄影摄像'));
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='ILCE-7M4'), (SELECT id FROM device_category WHERE name='相机') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='ILCE-7M4') AND category_id=(SELECT id FROM device_category WHERE name='相机'));

-- Canon R5 -> 摄影摄像, 相机
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='EOS R5'), (SELECT id FROM device_category WHERE name='摄影摄像') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='EOS R5') AND category_id=(SELECT id FROM device_category WHERE name='摄影摄像'));
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='EOS R5'), (SELECT id FROM device_category WHERE name='相机') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='EOS R5') AND category_id=(SELECT id FROM device_category WHERE name='相机'));

-- Mavic 3 -> 摄影摄像, 无人机
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='Mavic 3'), (SELECT id FROM device_category WHERE name='摄影摄像') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='Mavic 3') AND category_id=(SELECT id FROM device_category WHERE name='摄影摄像'));
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='Mavic 3'), (SELECT id FROM device_category WHERE name='无人机') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='Mavic 3') AND category_id=(SELECT id FROM device_category WHERE name='无人机'));

-- Sony Lens -> 摄影摄像, 镜头
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='SEL2470GM2'), (SELECT id FROM device_category WHERE name='摄影摄像') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='SEL2470GM2') AND category_id=(SELECT id FROM device_category WHERE name='摄影摄像'));
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='SEL2470GM2'), (SELECT id FROM device_category WHERE name='镜头') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='SEL2470GM2') AND category_id=(SELECT id FROM device_category WHERE name='镜头'));

-- Switch -> 游戏电玩, 游戏主机
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='Switch OLED'), (SELECT id FROM device_category WHERE name='游戏电玩') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='Switch OLED') AND category_id=(SELECT id FROM device_category WHERE name='游戏电玩'));
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='Switch OLED'), (SELECT id FROM device_category WHERE name='游戏主机') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='Switch OLED') AND category_id=(SELECT id FROM device_category WHERE name='游戏主机'));

-- PS5 -> 游戏电玩, 游戏主机
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='PS5'), (SELECT id FROM device_category WHERE name='游戏电玩') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='PS5') AND category_id=(SELECT id FROM device_category WHERE name='游戏电玩'));
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='PS5'), (SELECT id FROM device_category WHERE name='游戏主机') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='PS5') AND category_id=(SELECT id FROM device_category WHERE name='游戏主机'));

-- GoPro -> 户外露营, 摄影摄像
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='Hero 11'), (SELECT id FROM device_category WHERE name='户外露营') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='Hero 11') AND category_id=(SELECT id FROM device_category WHERE name='户外露营'));
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='Hero 11'), (SELECT id FROM device_category WHERE name='摄影摄像') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='Hero 11') AND category_id=(SELECT id FROM device_category WHERE name='摄影摄像'));

-- Mock Orders
INSERT INTO rental_order (order_no, user_id, device_id, merchant_id, start_time, end_time, total_amount, deposit_amount, status, create_time)
SELECT 'ORD20231027001', (SELECT id FROM sys_user WHERE username = 'user'), (SELECT id FROM device WHERE model='Switch OLED'), 1, NOW(), DATE_ADD(NOW(), INTERVAL 3 DAY), 90.00, 2000.00, 1, NOW()
FROM DUAL WHERE NOT EXISTS (SELECT * FROM rental_order WHERE order_no = 'ORD20231027001');

INSERT INTO rental_order (order_no, user_id, device_id, merchant_id, start_time, end_time, total_amount, deposit_amount, status, create_time)
SELECT 'ORD20231027002', (SELECT id FROM sys_user WHERE username = 'user'), (SELECT id FROM device WHERE model='PS5'), 1, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 350.00, 3500.00, 2, NOW()
FROM DUAL WHERE NOT EXISTS (SELECT * FROM rental_order WHERE order_no = 'ORD20231027002');

-- Reviews
INSERT INTO rental_comment (order_id, user_id, username, device_id, rating, content, create_time)
SELECT 
    (SELECT id FROM rental_order WHERE order_no = 'ORD20231027001'),
    (SELECT id FROM sys_user WHERE username = 'user'),
    'user',
    (SELECT id FROM device WHERE model='Switch OLED'),
    4,
    '屏幕确实比老款好很多，但是续航感觉差不多。',
    NOW()
FROM DUAL WHERE NOT EXISTS (SELECT * FROM rental_comment WHERE content = '屏幕确实比老款好很多，但是续航感觉差不多。');
