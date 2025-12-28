-- Seed Data: 23 Health Centers for Kirkuk Health Directorate - First Sector
-- بيانات أولية: 23 مركز صحي لدائرة صحة كركوك – قطاع كركوك الأول

-- Note: Replace 'user_id_XX' with actual user IDs from your users table
-- ملاحظة: استبدل 'user_id_XX' بمعرفات المستخدمين الفعلية من جدول المستخدمين

INSERT INTO user_profiles (id, user_id, health_center_name, manager_name, directorate_name, logo_path, show_verification_badge, created_at, updated_at)
VALUES
    -- Health Center 1
    (gen_random_uuid()::text, 'user_id_01', 'المركز الصحي الأول', 'د. أحمد محمد علي', 'دائرة صحة كركوك – قطاع كركوك الأول', 'logo.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Health Center 2
    (gen_random_uuid()::text, 'user_id_02', 'المركز الصحي الثاني', 'د. فاطمة حسن', 'دائرة صحة كركوك – قطاع كركوك الأول', 'logo.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Health Center 3
    (gen_random_uuid()::text, 'user_id_03', 'المركز الصحي الثالث', 'د. خالد إبراهيم', 'دائرة صحة كركوك – قطاع كركوك الأول', 'logo.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Health Center 4
    (gen_random_uuid()::text, 'user_id_04', 'المركز الصحي الرابع', 'د. سارة محمود', 'دائرة صحة كركوك – قطاع كركوك الأول', 'logo.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Health Center 5
    (gen_random_uuid()::text, 'user_id_05', 'المركز الصحي الخامس', 'د. عمر عبدالله', 'دائرة صحة كركوك – قطاع كركوك الأول', 'logo.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Health Center 6
    (gen_random_uuid()::text, 'user_id_06', 'المركز الصحي السادس', 'د. ليلى كريم', 'دائرة صحة كركوك – قطاع كركوك الأول', 'logo.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Health Center 7
    (gen_random_uuid()::text, 'user_id_07', 'المركز الصحي السابع', 'د. يوسف سالم', 'دائرة صحة كركوك – قطاع كركوك الأول', 'logo.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Health Center 8
    (gen_random_uuid()::text, 'user_id_08', 'المركز الصحي الثامن', 'د. نورا عبدالرحمن', 'دائرة صحة كركوك – قطاع كركوك الأول', 'logo.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Health Center 9
    (gen_random_uuid()::text, 'user_id_09', 'المركز الصحي التاسع', 'د. طارق فاضل', 'دائرة صحة كركوك – قطاع كركوك الأول', 'logo.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Health Center 10
    (gen_random_uuid()::text, 'user_id_10', 'المركز الصحي العاشر', 'د. هدى ناصر', 'دائرة صحة كركوك – قطاع كركوك الأول', 'logo.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Health Center 11
    (gen_random_uuid()::text, 'user_id_11', 'المركز الصحي الحادي عشر', 'د. باسل رشيد', 'دائرة صحة كركوك – قطاع كركوك الأول', 'logo.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Health Center 12
    (gen_random_uuid()::text, 'user_id_12', 'المركز الصحي الثاني عشر', 'د. ريم عدنان', 'دائرة صحة كركوك – قطاع كركوك الأول', 'logo.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Health Center 13
    (gen_random_uuid()::text, 'user_id_13', 'المركز الصحي الثالث عشر', 'د. وليد جمال', 'دائرة صحة كركوك – قطاع كركوك الأول', 'logo.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Health Center 14
    (gen_random_uuid()::text, 'user_id_14', 'المركز الصحي الرابع عشر', 'د. سمر حامد', 'دائرة صحة كركوك – قطاع كركوك الأول', 'logo.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Health Center 15
    (gen_random_uuid()::text, 'user_id_15', 'المركز الصحي الخامس عشر', 'د. علي كاظم', 'دائرة صحة كركوك – قطاع كركوك الأول', 'logo.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Health Center 16
    (gen_random_uuid()::text, 'user_id_16', 'المركز الصحي السادس عشر', 'د. نادية طه', 'دائرة صحة كركوك – قطاع كركوك الأول', 'logo.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Health Center 17
    (gen_random_uuid()::text, 'user_id_17', 'المركز الصحي السابع عشر', 'د. حيدر عادل', 'دائرة صحة كركوك – قطاع كركوك الأول', 'logo.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Health Center 18
    (gen_random_uuid()::text, 'user_id_18', 'المركز الصحي الثامن عشر', 'د. زينب محسن', 'دائرة صحة كركوك – قطاع كركوك الأول', 'logo.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Health Center 19
    (gen_random_uuid()::text, 'user_id_19', 'المركز الصحي التاسع عشر', 'د. مهدي صالح', 'دائرة صحة كركوك – قطاع كركوك الأول', 'logo.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Health Center 20
    (gen_random_uuid()::text, 'user_id_20', 'المركز الصحي العشرون', 'د. ابتسام رضا', 'دائرة صحة كركوك – قطاع كركوك الأول', 'logo.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Health Center 21
    (gen_random_uuid()::text, 'user_id_21', 'المركز الصحي الحادي والعشرون', 'د. حسام الدين', 'دائرة صحة كركوك – قطاع كركوك الأول', 'logo.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Health Center 22
    (gen_random_uuid()::text, 'user_id_22', 'المركز الصحي الثاني والعشرون', 'د. مريم عبدالله', 'دائرة صحة كركوك – قطاع كركوك الأول', 'logo.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- Health Center 23
    (gen_random_uuid()::text, 'user_id_23', 'المركز الصحي الثالث والعشرون', 'د. كريم حسن', 'دائرة صحة كركوك – قطاع كركوك الأول', 'logo.jpg', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (user_id) DO NOTHING;

-- Verify insertion
SELECT COUNT(*) as total_health_centers FROM user_profiles;

