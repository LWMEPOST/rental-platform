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
INSERT INTO device (name, brand, model, description, main_image, specs, deposit_amount, rental_price, stock_quantity, status)
SELECT 'Sony/索尼 A7M4 全画幅微单相机', 'Sony', 'ILCE-7M4', '3300万像素全画幅背照式Exmor R CMOS影像传感器。BIONZ XR影像处理器。支持4K 60p视频录制。15级动态范围，色彩还原更真实。实时眼部对焦（人/动物/鸟类）。', 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80', '{"传感器尺寸":"全画幅","有效像素":"约3300万","视频拍摄":"4K 60p","防抖":"5.5级机身防抖","重量":"约658g（含电池和存储卡）"}', 15000.00, 200.00, 10, 1
FROM DUAL WHERE NOT EXISTS (SELECT * FROM device WHERE model = 'ILCE-7M4');

-- 2. Canon EOS R5
INSERT INTO device (name, brand, model, description, main_image, specs, deposit_amount, rental_price, stock_quantity, status)
SELECT 'Canon/佳能 EOS R5 专业微单', 'Canon', 'EOS R5', '4500万像素全画幅CMOS图像感应器。支持8K RAW视频内录。最高约20张/秒连拍。最高8级协同防抖。全像素双核CMOS AF II。', 'https://images.unsplash.com/photo-1519183071298-a2962feb14f4?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80', '{"传感器尺寸":"全画幅","有效像素":"约4500万","视频拍摄":"8K RAW","防抖":"最高8级协同防抖","重量":"约738g（含电池和存储卡）"}', 20000.00, 300.00, 5, 1
FROM DUAL WHERE NOT EXISTS (SELECT * FROM device WHERE model = 'EOS R5');

-- 3. DJI Mavic 3
INSERT INTO device (name, brand, model, description, main_image, specs, deposit_amount, rental_price, stock_quantity, status)
SELECT 'DJI/大疆 Mavic 3 航拍无人机', 'DJI', 'Mavic 3', '搭载4/3 CMOS哈苏相机，呈现12.8档动态范围，色彩更自然。最长46分钟飞行时间。全向避障系统，飞行更安全。15公里O3+图传。', 'https://images.unsplash.com/photo-1473968512647-3e447244af8f?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80', '{"传感器":"4/3 CMOS","最长续航":"46分钟","避障":"全向避障","图传":"15公里O3+","重量":"约895g"}', 12000.00, 250.00, 8, 1
FROM DUAL WHERE NOT EXISTS (SELECT * FROM device WHERE model = 'Mavic 3');

-- 4. Sony 24-70mm GM II
INSERT INTO device (name, brand, model, description, main_image, specs, deposit_amount, rental_price, stock_quantity, status)
SELECT 'Sony/索尼 FE 24-70mm F2.8 GM II', 'Sony', 'SEL2470GM2', '索尼第二代大三元标准变焦镜头。采用了先进的光学设计，包括两枚XA（超级非球面）镜片。重量相比前代减轻约20%，体积缩小约18%。四个XD线性马达提供快速、精准、安静的自动对焦。', 'https://images.unsplash.com/photo-1617005082133-548c4dd27f35?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80', '{"焦距":"24-70mm","最大光圈":"F2.8","卡口":"E卡口","滤镜直径":"82mm","重量":"约695g"}', 10000.00, 150.00, 15, 1
FROM DUAL WHERE NOT EXISTS (SELECT * FROM device WHERE model = 'SEL2470GM2');

-- 5. Nintendo Switch OLED
INSERT INTO device (name, brand, model, description, main_image, specs, deposit_amount, rental_price, stock_quantity, status)
SELECT 'Nintendo/任天堂 Switch OLED版', 'Nintendo', 'Switch OLED', '配备7英寸OLED屏幕，色彩更鲜艳，对比度更高。更宽且可调节角度的支架。底座内置有线网络插孔。64GB内置存储空间。增强的音频效果。', 'https://images.unsplash.com/photo-1578303512597-81e6cc155b3e?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80', '{"屏幕":"7英寸OLED","存储":"64GB","模式":"电视/桌面/掌机模式","续航":"约4.5-9小时","重量":"约420g（含手柄）"}', 2000.00, 30.00, 20, 1
FROM DUAL WHERE NOT EXISTS (SELECT * FROM device WHERE model = 'Switch OLED');

-- 6. PS5
INSERT INTO device (name, brand, model, description, main_image, specs, deposit_amount, rental_price, stock_quantity, status)
SELECT 'Sony/索尼 PlayStation 5 光驱版', 'Sony', 'PS5', '搭载超高速SSD，实现闪电般的加载速度。支持触觉反馈、自适应扳机和3D音效技术，带来更沉浸的游戏体验。支持4K电视游戏，最高可达120fps帧率。', 'https://images.unsplash.com/photo-1606144042614-b2417e99c4e3?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80', '{"CPU":"x86-64-AMD Ryzen Zen 2","GPU":"AMD Radeon RDNA 2","内存":"GDDR6 16GB","存储":"825GB SSD","分辨率支持":"4K 120Hz / 8K"}', 3500.00, 50.00, 10, 1
FROM DUAL WHERE NOT EXISTS (SELECT * FROM device WHERE model = 'PS5');

-- 7. GoPro Hero 11
INSERT INTO device (name, brand, model, description, main_image, specs, deposit_amount, rental_price, stock_quantity, status)
SELECT 'GoPro Hero 11 Black 运动相机', 'GoPro', 'Hero 11', '全新图像传感器带来更广的视野。HyperSmooth 5.0超强防抖。可拍摄5.3K60和4K120视频，以及2700万像素高分辨率照片。支持10-bit色彩。裸机10米防水。', 'https://images.unsplash.com/photo-1565849904461-04a58ad377e0?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80', '{"视频最高规格":"5.3K 60fps","照片像素":"2700万","防抖":"HyperSmooth 5.0","防水":"裸机10米","重量":"约154g"}', 2500.00, 40.00, 12, 1
FROM DUAL WHERE NOT EXISTS (SELECT * FROM device WHERE model = 'Hero 11');

-- 8. Fujifilm X-T5
INSERT INTO device (name, brand, model, description, main_image, specs, deposit_amount, rental_price, stock_quantity, status)
SELECT 'Fujifilm/富士 X-T5 无反相机', 'Fujifilm', 'X-T5', '4020万像素APS-C画幅，胶片模拟色彩表现出色，支持机身五轴防抖与高分辨率模式。', 'https://images.unsplash.com/photo-1500534623283-312aade485b7?auto=format&fit=crop&w=800&q=80', '{"传感器尺寸":"APS-C","有效像素":"约4020万","视频拍摄":"6.2K 30p","防抖":"机身五轴防抖","重量":"约557g"}', 9000.00, 160.00, 9, 1
FROM DUAL WHERE NOT EXISTS (SELECT * FROM device WHERE model = 'X-T5');

-- 9. Nikon Z6 II
INSERT INTO device (name, brand, model, description, main_image, specs, deposit_amount, rental_price, stock_quantity, status)
SELECT 'Nikon/尼康 Z6 II 全画幅微单', 'Nikon', 'Z6II', '双EXPEED 6处理器，低光表现优秀，支持4K视频录制，适合婚礼与人像拍摄。', 'https://images.unsplash.com/photo-1510127034890-ba27508e9f1c?auto=format&fit=crop&w=800&q=80', '{"传感器尺寸":"全画幅","有效像素":"约2450万","视频拍摄":"4K 60p","连拍":"14fps","重量":"约705g"}', 12000.00, 180.00, 7, 1
FROM DUAL WHERE NOT EXISTS (SELECT * FROM device WHERE model = 'Z6II');

-- 10. Insta360 X3
INSERT INTO device (name, brand, model, description, main_image, specs, deposit_amount, rental_price, stock_quantity, status)
SELECT 'Insta360 X3 全景运动相机', 'Insta360', 'Insta360 X3', '5.7K全景视频，单镜头4K模式，FlowState防抖，适合Vlog和户外骑行。', 'https://images.unsplash.com/photo-1526178613552-2b45c6c302f0?auto=format&fit=crop&w=800&q=80', '{"视频最高规格":"5.7K 360°","单镜头模式":"4K 30fps","防抖":"FlowState","防水":"裸机10米","重量":"约180g"}', 1800.00, 45.00, 16, 1
FROM DUAL WHERE NOT EXISTS (SELECT * FROM device WHERE model = 'Insta360 X3');

-- 11. DJI Air 3
INSERT INTO device (name, brand, model, description, main_image, specs, deposit_amount, rental_price, stock_quantity, status)
SELECT 'DJI/大疆 Air 3 双摄无人机', 'DJI', 'Air 3', '双主摄系统，支持4K 100fps视频，最长46分钟续航，适合旅行航拍。', 'https://images.unsplash.com/photo-1508614589041-895b88991e3e?auto=format&fit=crop&w=800&q=80', '{"镜头":"广角+中长焦双摄","视频拍摄":"4K 100fps","最长续航":"46分钟","图传":"O4","避障":"全向感知"}', 8500.00, 220.00, 6, 1
FROM DUAL WHERE NOT EXISTS (SELECT * FROM device WHERE model = 'Air 3');

-- 12. Meta Quest 3
INSERT INTO device (name, brand, model, description, main_image, specs, deposit_amount, rental_price, stock_quantity, status)
SELECT 'Meta Quest 3 VR一体机', 'Meta', 'Quest 3', '新一代混合现实头显，性能大幅提升，支持海量VR内容与手势交互。', 'https://images.unsplash.com/photo-1622979135225-d2ba269cf1ac?auto=format&fit=crop&w=800&q=80', '{"分辨率":"2064x2208(单眼)","存储":"128GB","刷新率":"最高120Hz","定位":"Inside-out","重量":"约515g"}', 3800.00, 80.00, 14, 1
FROM DUAL WHERE NOT EXISTS (SELECT * FROM device WHERE model = 'Quest 3');

-- 13. Xbox Series X
INSERT INTO device (name, brand, model, description, main_image, specs, deposit_amount, rental_price, stock_quantity, status)
SELECT 'Microsoft Xbox Series X 主机', 'Microsoft', 'Xbox Series X', '次世代4K游戏主机，支持快速恢复与高帧率体验。', 'https://images.unsplash.com/photo-1621259182978-fbf93132d53d?auto=format&fit=crop&w=800&q=80', '{"CPU":"8核Zen 2","GPU":"12 TFLOPS","内存":"16GB GDDR6","存储":"1TB SSD","分辨率支持":"4K 120Hz"}', 3200.00, 48.00, 11, 1
FROM DUAL WHERE NOT EXISTS (SELECT * FROM device WHERE model = 'Xbox Series X');

-- 14. Steam Deck OLED
INSERT INTO device (name, brand, model, description, main_image, specs, deposit_amount, rental_price, stock_quantity, status)
SELECT 'Valve Steam Deck OLED 掌机', 'Valve', 'Steam Deck OLED', 'OLED高刷屏，掌上PC游戏平台，适合随时随地畅玩3A。', 'https://images.unsplash.com/photo-1511512578047-dfb367046420?auto=format&fit=crop&w=800&q=80', '{"屏幕":"7.4英寸OLED","刷新率":"90Hz","存储":"512GB SSD","系统":"SteamOS","重量":"约640g"}', 3600.00, 65.00, 10, 1
FROM DUAL WHERE NOT EXISTS (SELECT * FROM device WHERE model = 'Steam Deck OLED');

-- 15. Sony FE 70-200 GM II
INSERT INTO device (name, brand, model, description, main_image, specs, deposit_amount, rental_price, stock_quantity, status)
SELECT 'Sony/索尼 FE 70-200mm F2.8 GM OSS II', 'Sony', 'SEL70200GM2', '旗舰长焦大三元镜头，轻量化设计，追焦性能优异，适合体育与人像。', 'https://images.unsplash.com/photo-1581591524425-c7e0978865fc?auto=format&fit=crop&w=800&q=80', '{"焦距":"70-200mm","最大光圈":"F2.8","防抖":"OSS光学防抖","卡口":"E卡口","重量":"约1045g"}', 16000.00, 220.00, 8, 1
FROM DUAL WHERE NOT EXISTS (SELECT * FROM device WHERE model = 'SEL70200GM2');

-- 16. Canon RF 50mm F1.2 L
INSERT INTO device (name, brand, model, description, main_image, specs, deposit_amount, rental_price, stock_quantity, status)
SELECT 'Canon/佳能 RF 50mm F1.2 L USM', 'Canon', 'RF50F12L', '高端人像定焦镜头，F1.2大光圈，焦外柔美，成像锐利。', 'https://images.unsplash.com/photo-1616423640778-28d1b53229bd?auto=format&fit=crop&w=800&q=80', '{"焦距":"50mm","最大光圈":"F1.2","卡口":"RF卡口","对焦马达":"USM","重量":"约950g"}', 12500.00, 170.00, 9, 1
FROM DUAL WHERE NOT EXISTS (SELECT * FROM device WHERE model = 'RF50F12L');

-- 17. Blackmagic Pocket Cinema Camera 6K
INSERT INTO device (name, brand, model, description, main_image, specs, deposit_amount, rental_price, stock_quantity, status)
SELECT 'Blackmagic Pocket Cinema Camera 6K', 'Blackmagic', 'BMPCC6K', '电影机级别色彩科学，Super 35传感器，支持Blackmagic RAW，独立影视拍摄利器。', 'https://images.unsplash.com/photo-1495707902641-75cac588d2e9?auto=format&fit=crop&w=800&q=80', '{"传感器":"Super 35","视频":"6K 50fps","编码":"Blackmagic RAW/ProRes","卡口":"EF卡口","屏幕":"5英寸触摸屏"}', 14000.00, 260.00, 5, 1
FROM DUAL WHERE NOT EXISTS (SELECT * FROM device WHERE model = 'BMPCC6K');

-- Repair existing mojibake rows by model (idempotent)
UPDATE device SET name='Sony/索尼 A7M4 全画幅微单相机', description='3300万像素全画幅背照式Exmor R CMOS影像传感器。BIONZ XR影像处理器。支持4K 60p视频录制。15级动态范围，色彩还原更真实。实时眼部对焦（人/动物/鸟类）。', specs='{"传感器尺寸":"全画幅","有效像素":"约3300万","视频拍摄":"4K 60p","防抖":"5.5级机身防抖","重量":"约658g（含电池和存储卡）"}' WHERE model='ILCE-7M4';
UPDATE device SET name='Canon/佳能 EOS R5 专业微单', description='4500万像素全画幅CMOS图像感应器。支持8K RAW视频内录。最高约20张/秒连拍。最高8级协同防抖。全像素双核CMOS AF II。', specs='{"传感器尺寸":"全画幅","有效像素":"约4500万","视频拍摄":"8K RAW","防抖":"最高8级协同防抖","重量":"约738g（含电池和存储卡）"}' WHERE model='EOS R5';
UPDATE device SET name='DJI/大疆 Mavic 3 航拍无人机', description='搭载4/3 CMOS哈苏相机，呈现12.8档动态范围，色彩更自然。最长46分钟飞行时间。全向避障系统，飞行更安全。15公里O3+图传。', specs='{"传感器":"4/3 CMOS","最长续航":"46分钟","避障":"全向避障","图传":"15公里O3+","重量":"约895g"}' WHERE model='Mavic 3';
UPDATE device SET name='Sony/索尼 FE 24-70mm F2.8 GM II', description='索尼第二代大三元标准变焦镜头。采用了先进的光学设计，包括两枚XA（超级非球面）镜片。重量相比前代减轻约20%，体积缩小约18%。四个XD线性马达提供快速、精准、安静的自动对焦。', specs='{"焦距":"24-70mm","最大光圈":"F2.8","卡口":"E卡口","滤镜直径":"82mm","重量":"约695g"}' WHERE model='SEL2470GM2';
UPDATE device SET name='Nintendo/任天堂 Switch OLED版', description='配备7英寸OLED屏幕，色彩更鲜艳，对比度更高。更宽且可调节角度的支架。底座内置有线网络插孔。64GB内置存储空间。增强的音频效果。', specs='{"屏幕":"7英寸OLED","存储":"64GB","模式":"电视/桌面/掌机模式","续航":"约4.5-9小时","重量":"约420g（含手柄）"}' WHERE model='Switch OLED';
UPDATE device SET name='Sony/索尼 PlayStation 5 光驱版', description='搭载超高速SSD，实现闪电般的加载速度。支持触觉反馈、自适应扳机和3D音效技术，带来更沉浸的游戏体验。支持4K电视游戏，最高可达120fps帧率。', specs='{"CPU":"x86-64-AMD Ryzen Zen 2","GPU":"AMD Radeon RDNA 2","内存":"GDDR6 16GB","存储":"825GB SSD","分辨率支持":"4K 120Hz / 8K"}' WHERE model='PS5';
UPDATE device SET name='GoPro Hero 11 Black 运动相机', description='全新图像传感器带来更广的视野。HyperSmooth 5.0超强防抖。可拍摄5.3K60和4K120视频，以及2700万像素高分辨率照片。支持10-bit色彩。裸机10米防水。', specs='{"视频最高规格":"5.3K 60fps","照片像素":"2700万","防抖":"HyperSmooth 5.0","防水":"裸机10米","重量":"约154g"}' WHERE model='Hero 11';
UPDATE device SET name='Fujifilm/富士 X-T5 无反相机', description='4020万像素APS-C画幅，胶片模拟色彩表现出色，支持机身五轴防抖与高分辨率模式。', specs='{"传感器尺寸":"APS-C","有效像素":"约4020万","视频拍摄":"6.2K 30p","防抖":"机身五轴防抖","重量":"约557g"}' WHERE model='X-T5';
UPDATE device SET name='Nikon/尼康 Z6 II 全画幅微单', description='双EXPEED 6处理器，低光表现优秀，支持4K视频录制，适合婚礼与人像拍摄。', specs='{"传感器尺寸":"全画幅","有效像素":"约2450万","视频拍摄":"4K 60p","连拍":"14fps","重量":"约705g"}' WHERE model='Z6II';
UPDATE device SET name='Insta360 X3 全景运动相机', description='5.7K全景视频，单镜头4K模式，FlowState防抖，适合Vlog和户外骑行。', specs='{"视频最高规格":"5.7K 360°","单镜头模式":"4K 30fps","防抖":"FlowState","防水":"裸机10米","重量":"约180g"}' WHERE model='Insta360 X3';
UPDATE device SET name='DJI/大疆 Air 3 双摄无人机', description='双主摄系统，支持4K 100fps视频，最长46分钟续航，适合旅行航拍。', specs='{"镜头":"广角+中长焦双摄","视频拍摄":"4K 100fps","最长续航":"46分钟","图传":"O4","避障":"全向感知"}' WHERE model='Air 3';
UPDATE device SET name='Meta Quest 3 VR一体机', description='新一代混合现实头显，性能大幅提升，支持海量VR内容与手势交互。', specs='{"分辨率":"2064x2208(单眼)","存储":"128GB","刷新率":"最高120Hz","定位":"Inside-out","重量":"约515g"}' WHERE model='Quest 3';
UPDATE device SET name='Microsoft Xbox Series X 主机', description='次世代4K游戏主机，支持快速恢复与高帧率体验。', specs='{"CPU":"8核Zen 2","GPU":"12 TFLOPS","内存":"16GB GDDR6","存储":"1TB SSD","分辨率支持":"4K 120Hz"}' WHERE model='Xbox Series X';
UPDATE device SET name='Valve Steam Deck OLED 掌机', description='OLED高刷屏，掌上PC游戏平台，适合随时随地畅玩3A。', specs='{"屏幕":"7.4英寸OLED","刷新率":"90Hz","存储":"512GB SSD","系统":"SteamOS","重量":"约640g"}' WHERE model='Steam Deck OLED';
UPDATE device SET name='Sony/索尼 FE 70-200mm F2.8 GM OSS II', description='旗舰长焦大三元镜头，轻量化设计，追焦性能优异，适合体育与人像。', specs='{"焦距":"70-200mm","最大光圈":"F2.8","防抖":"OSS光学防抖","卡口":"E卡口","重量":"约1045g"}' WHERE model='SEL70200GM2';
UPDATE device SET name='Canon/佳能 RF 50mm F1.2 L USM', description='高端人像定焦镜头，F1.2大光圈，焦外柔美，成像锐利。', specs='{"焦距":"50mm","最大光圈":"F1.2","卡口":"RF卡口","对焦马达":"USM","重量":"约950g"}' WHERE model='RF50F12L';
UPDATE device SET name='Blackmagic Pocket Cinema Camera 6K', description='电影机级别色彩科学，Super 35传感器，支持Blackmagic RAW，独立影视拍摄利器。', specs='{"传感器":"Super 35","视频":"6K 50fps","编码":"Blackmagic RAW/ProRes","卡口":"EF卡口","屏幕":"5英寸触摸屏"}' WHERE model='BMPCC6K';

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

-- Fujifilm X-T5 -> 摄影摄像, 相机
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='X-T5'), (SELECT id FROM device_category WHERE name='摄影摄像') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='X-T5') AND category_id=(SELECT id FROM device_category WHERE name='摄影摄像'));
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='X-T5'), (SELECT id FROM device_category WHERE name='相机') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='X-T5') AND category_id=(SELECT id FROM device_category WHERE name='相机'));

-- Nikon Z6 II -> 摄影摄像, 相机
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='Z6II'), (SELECT id FROM device_category WHERE name='摄影摄像') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='Z6II') AND category_id=(SELECT id FROM device_category WHERE name='摄影摄像'));
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='Z6II'), (SELECT id FROM device_category WHERE name='相机') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='Z6II') AND category_id=(SELECT id FROM device_category WHERE name='相机'));

-- Insta360 X3 -> 户外露营, 摄影摄像
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='Insta360 X3'), (SELECT id FROM device_category WHERE name='户外露营') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='Insta360 X3') AND category_id=(SELECT id FROM device_category WHERE name='户外露营'));
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='Insta360 X3'), (SELECT id FROM device_category WHERE name='摄影摄像') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='Insta360 X3') AND category_id=(SELECT id FROM device_category WHERE name='摄影摄像'));

-- DJI Air 3 -> 无人机, 摄影摄像
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='Air 3'), (SELECT id FROM device_category WHERE name='无人机') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='Air 3') AND category_id=(SELECT id FROM device_category WHERE name='无人机'));
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='Air 3'), (SELECT id FROM device_category WHERE name='摄影摄像') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='Air 3') AND category_id=(SELECT id FROM device_category WHERE name='摄影摄像'));

-- Meta Quest 3 -> VR设备, 游戏电玩
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='Quest 3'), (SELECT id FROM device_category WHERE name='VR设备') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='Quest 3') AND category_id=(SELECT id FROM device_category WHERE name='VR设备'));
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='Quest 3'), (SELECT id FROM device_category WHERE name='游戏电玩') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='Quest 3') AND category_id=(SELECT id FROM device_category WHERE name='游戏电玩'));

-- Xbox Series X -> 游戏主机, 游戏电玩
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='Xbox Series X'), (SELECT id FROM device_category WHERE name='游戏主机') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='Xbox Series X') AND category_id=(SELECT id FROM device_category WHERE name='游戏主机'));
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='Xbox Series X'), (SELECT id FROM device_category WHERE name='游戏电玩') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='Xbox Series X') AND category_id=(SELECT id FROM device_category WHERE name='游戏电玩'));

-- Steam Deck OLED -> 游戏主机, 游戏电玩
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='Steam Deck OLED'), (SELECT id FROM device_category WHERE name='游戏主机') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='Steam Deck OLED') AND category_id=(SELECT id FROM device_category WHERE name='游戏主机'));
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='Steam Deck OLED'), (SELECT id FROM device_category WHERE name='游戏电玩') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='Steam Deck OLED') AND category_id=(SELECT id FROM device_category WHERE name='游戏电玩'));

-- Sony FE 70-200 GM II -> 镜头, 摄影摄像
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='SEL70200GM2'), (SELECT id FROM device_category WHERE name='镜头') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='SEL70200GM2') AND category_id=(SELECT id FROM device_category WHERE name='镜头'));
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='SEL70200GM2'), (SELECT id FROM device_category WHERE name='摄影摄像') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='SEL70200GM2') AND category_id=(SELECT id FROM device_category WHERE name='摄影摄像'));

-- Canon RF 50mm F1.2 L -> 镜头, 摄影摄像
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='RF50F12L'), (SELECT id FROM device_category WHERE name='镜头') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='RF50F12L') AND category_id=(SELECT id FROM device_category WHERE name='镜头'));
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='RF50F12L'), (SELECT id FROM device_category WHERE name='摄影摄像') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='RF50F12L') AND category_id=(SELECT id FROM device_category WHERE name='摄影摄像'));

-- BMPCC6K -> 相机, 摄影摄像
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='BMPCC6K'), (SELECT id FROM device_category WHERE name='相机') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='BMPCC6K') AND category_id=(SELECT id FROM device_category WHERE name='相机'));
INSERT INTO device_category_mapping (device_id, category_id)
SELECT (SELECT id FROM device WHERE model='BMPCC6K'), (SELECT id FROM device_category WHERE name='摄影摄像') FROM DUAL WHERE NOT EXISTS (SELECT * FROM device_category_mapping WHERE device_id=(SELECT id FROM device WHERE model='BMPCC6K') AND category_id=(SELECT id FROM device_category WHERE name='摄影摄像'));

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
