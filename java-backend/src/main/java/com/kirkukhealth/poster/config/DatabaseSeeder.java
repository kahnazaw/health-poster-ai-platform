package com.kirkukhealth.poster.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Database Seeder
 * بذار قاعدة البيانات
 * 
 * Seeds the database with 66 health topics on application startup
 * يملأ قاعدة البيانات بـ 66 موضوع صحي عند بدء التطبيق
 */
@Component
@Order(1)
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Map<String, List<String>> CATEGORY_TOPICS = new LinkedHashMap<>();

    static {
        // 1. Maternal & Child Health | صحة الأم والطفل
        CATEGORY_TOPICS.put("صحة الأم والطفل", Arrays.asList(
            "رعاية ما قبل الولادة",
            "رعاية ما بعد الولادة",
            "الرضاعة الطبيعية",
            "تغذية الرضع والأطفال",
            "نمو وتطور الطفل",
            "صحة المراهقين"
        ));

        // 2. Immunization | التطعيم
        CATEGORY_TOPICS.put("التطعيم", Arrays.asList(
            "التطعيم الروتيني",
            "التطعيم الموسمي",
            "أهمية التطعيم",
            "جدول التطعيمات",
            "التطعيم للأطفال",
            "التطعيم للبالغين"
        ));

        // 3. Communicable Diseases | الأمراض المعدية
        CATEGORY_TOPICS.put("الأمراض المعدية", Arrays.asList(
            "الوقاية من الإنفلونزا",
            "الوقاية من COVID-19",
            "الوقاية من السل",
            "الوقاية من التهاب الكبد",
            "الوقاية من الملاريا",
            "النظافة الشخصية للوقاية",
            "التباعد الاجتماعي"
        ));

        // 4. Non-Communicable Diseases | الأمراض غير المعدية
        CATEGORY_TOPICS.put("الأمراض غير المعدية", Arrays.asList(
            "الوقاية من السكري",
            "الوقاية من أمراض القلب",
            "الوقاية من السرطان",
            "الوقاية من ارتفاع ضغط الدم",
            "الوقاية من السمنة",
            "النشاط البدني",
            "التغذية الصحية"
        ));

        // 5. Mental Health | الصحة النفسية
        CATEGORY_TOPICS.put("الصحة النفسية", Arrays.asList(
            "الصحة النفسية العامة",
            "التعامل مع التوتر",
            "الصحة النفسية للأطفال",
            "الصحة النفسية للمراهقين",
            "الصحة النفسية لكبار السن",
            "الوقاية من الاكتئاب"
        ));

        // 6. First Aid | الإسعافات الأولية
        CATEGORY_TOPICS.put("الإسعافات الأولية", Arrays.asList(
            "الإسعافات الأولية الأساسية",
            "الإسعافات الأولية للأطفال",
            "الإسعافات الأولية في حالات الطوارئ",
            "إنعاش القلب والرئتين (CPR)",
            "معالجة الجروح",
            "معالجة الحروق"
        ));

        // 7. Hygiene | النظافة
        CATEGORY_TOPICS.put("النظافة", Arrays.asList(
            "النظافة الشخصية",
            "نظافة الأسنان",
            "نظافة اليدين",
            "نظافة البيئة",
            "نظافة الغذاء",
            "نظافة المياه"
        ));

        // 8. Drug Misuse | سوء استخدام الأدوية
        CATEGORY_TOPICS.put("سوء استخدام الأدوية", Arrays.asList(
            "الاستخدام الصحيح للأدوية",
            "مخاطر إساءة استخدام الأدوية",
            "التداخل الدوائي",
            "الجرعات الصحيحة",
            "تخزين الأدوية",
            "التخلص من الأدوية المنتهية"
        ));

        // 9. Antimicrobial Resistance | مقاومة المضادات الحيوية
        CATEGORY_TOPICS.put("مقاومة المضادات الحيوية", Arrays.asList(
            "الاستخدام الصحيح للمضادات الحيوية",
            "مخاطر مقاومة المضادات الحيوية",
            "الوقاية من مقاومة المضادات الحيوية",
            "متى تستخدم المضادات الحيوية",
            "إكمال دورة المضادات الحيوية"
        ));

        // 10. Health Occasions | المناسبات الصحية
        CATEGORY_TOPICS.put("المناسبات الصحية", Arrays.asList(
            "اليوم العالمي للصحة",
            "اليوم العالمي لمكافحة التدخين",
            "اليوم العالمي للسكري",
            "اليوم العالمي للقلب",
            "أسبوع التطعيم",
            "أسبوع الرضاعة الطبيعية",
            "مناسبات صحية أخرى"
        ));

        // 11. Others | أخرى
        CATEGORY_TOPICS.put("أخرى", Arrays.asList(
            "مواضيع صحية عامة",
            "الوعي الصحي",
            "الخدمات الصحية المتاحة",
            "مواضيع أخرى"
        ));
    }

    @Override
    public void run(String... args) {
        try {
            // Create health_topics table if it doesn't exist
            createTableIfNotExists();

            // Seed topics
            seedHealthTopics();

            System.out.println("✅ Successfully seeded 66 health topics into database");
        } catch (Exception e) {
            System.err.println("❌ Error seeding database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists() {
        String createTableSql = """
            CREATE TABLE IF NOT EXISTS health_topics (
                topic_id VARCHAR(255) PRIMARY KEY,
                topic_name VARCHAR(255) NOT NULL,
                category_name VARCHAR(255) NOT NULL,
                topic_order INTEGER,
                is_active BOOLEAN DEFAULT TRUE,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                CONSTRAINT uk_health_topics UNIQUE (topic_name, category_name)
            )
            """;

        jdbcTemplate.execute(createTableSql);

        // Create indexes
        try {
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_health_topics_category ON health_topics(category_name)");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_health_topics_active ON health_topics(is_active)");
        } catch (Exception e) {
            // Indexes might already exist
        }
    }

    private void seedHealthTopics() {
        int topicCounter = 1;

        for (Map.Entry<String, List<String>> categoryEntry : CATEGORY_TOPICS.entrySet()) {
            String categoryName = categoryEntry.getKey();
            List<String> topics = categoryEntry.getValue();

            for (int i = 0; i < topics.size(); i++) {
                String topicName = topics.get(i);
                String topicId = String.format("topic_%03d", topicCounter++);

                String insertSql = """
                    INSERT INTO health_topics (topic_id, topic_name, category_name, topic_order)
                    VALUES (?, ?, ?, ?)
                    ON CONFLICT (topic_name, category_name) DO NOTHING
                    """;

                try {
                    jdbcTemplate.update(insertSql, topicId, topicName, categoryName, i + 1);
                } catch (Exception e) {
                    // Topic might already exist, continue
                    System.err.println("Warning: Could not insert topic " + topicName + ": " + e.getMessage());
                }
            }
        }
    }
}

