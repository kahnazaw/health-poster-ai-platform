package com.kirkukhealth.poster.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kirkukhealth.poster.model.PosterContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Gemini AI Service
 * خدمة Gemini AI
 * 
 * Generates AI-powered content for health posters using Google Gemini API
 * يولد محتوى مدعوم بالذكاء الاصطناعي للبوسترات الصحية باستخدام Google Gemini API
 */
@Service
public class GeminiAIService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent}")
    private String apiUrl;

    @Autowired
    private ContentAuthorityService contentAuthorityService;

    @Autowired
    private HealthStatisticsService healthStatisticsService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Generate AI content for a poster topic
     * توليد محتوى ذكي لموضوع بوستر
     * 
     * @param topic The health topic (from 66 MOH topics)
     * @param language Language code (ar, ku, tr)
     * @param tone Tone (formal, friendly, emergency, educational)
     * @return Generated PosterContent
     */
    public PosterContent generatePosterContent(String topic, String language, String tone) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("Gemini API key is not configured. Please set gemini.api.key in application.properties");
        }

        try {
            // Build prompt based on topic and tone
            String prompt = buildPrompt(topic, language, tone);

            // Call Gemini API
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> contents = new HashMap<>();
            List<Map<String, Object>> parts = new ArrayList<>();
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);
            parts.add(part);
            contents.put("parts", parts);
            requestBody.put("contents", Collections.singletonList(contents));

            // Generation config
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", getTemperatureForTone(tone));
            generationConfig.put("topK", 40);
            generationConfig.put("topP", 0.95);
            generationConfig.put("maxOutputTokens", 2048);
            requestBody.put("generationConfig", generationConfig);

            // Make API call
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String url = apiUrl + "?key=" + apiKey;

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return parseGeminiResponse(response.getBody(), topic, language);
            } else {
                throw new RuntimeException("Gemini API returned status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("❌ Error calling Gemini API: " + e.getMessage());
            e.printStackTrace();
            // Fallback to template-based generation
            return generateFallbackContent(topic, language, tone);
        }
    }

    /**
     * Build prompt for Gemini AI
     * بناء المطالبة لـ Gemini AI
     */
    private String buildPrompt(String topic, String language, String tone) {
        StringBuilder prompt = new StringBuilder();

        // Context setting
        prompt.append("أنت مساعد ذكي متخصص في إنشاء محتوى توعية صحية رسمي لوزارة الصحة العراقية - دائرة صحة كركوك.\n\n");
        prompt.append("الموضوع: ").append(topic).append("\n\n");
        prompt.append("المطلوب:\n");
        prompt.append("1. عنوان احترافي بالعربية (أو التركمانية إذا طُلب) - جذاب وواضح (15-20 كلمة كحد أقصى)\n");
        prompt.append("2. 3-5 نقاط رئيسية موجزة وطبية دقيقة (كل نقطة 10-15 كلمة)\n");
        prompt.append("3. شعار صحي محلي لكركوك/العراق (جملة واحدة ملهمة)\n\n");

        // Tone instructions
        if ("emergency".equals(tone)) {
            prompt.append("النبرة: عاجلة وواضحة - للمواضيع الحرجة مثل الكوليرا، COVID-19، الطوارئ الصحية.\n");
        } else if ("educational".equals(tone)) {
            prompt.append("النبرة: تعليمية وودية - للمواضيع التعليمية مثل التغذية، النظافة، التطعيم.\n");
        } else if ("friendly".equals(tone)) {
            prompt.append("النبرة: ودية ومشجعة - للمواضيع العامة.\n");
        } else {
            prompt.append("النبرة: رسمية واحترافية - للمواضيع الرسمية.\n");
        }

        // Language instructions
        if ("ku".equals(language)) {
            prompt.append("اللغة: الكردية (Kurdish)\n");
        } else if ("tr".equals(language)) {
            prompt.append("اللغة: التركمانية (Turkmen)\n");
        } else {
            prompt.append("اللغة: العربية\n");
        }

        prompt.append("\n");
        prompt.append("المتطلبات:\n");
        prompt.append("- يجب أن يكون المحتوى متوافقاً مع إرشادات وزارة الصحة العراقية (MOH)\n");
        prompt.append("- يجب أن يكون مناسباً للسياق الثقافي لكركوك والعراق\n");
        prompt.append("- يجب أن يكون دقيقاً طبياً وآمناً\n");
        prompt.append("- يجب أن يكون واضحاً وسهل الفهم للجمهور العام\n\n");

        prompt.append("يرجى إرجاع النتيجة بالتنسيق التالي (JSON):\n");
        prompt.append("{\n");
        prompt.append("  \"title\": \"العنوان هنا\",\n");
        prompt.append("  \"bulletPoints\": [\"النقطة 1\", \"النقطة 2\", \"النقطة 3\"],\n");
        prompt.append("  \"slogan\": \"الشعار الصحي المحلي\"\n");
        prompt.append("}\n");

        return prompt.toString();
    }

    /**
     * Get temperature for tone
     * الحصول على درجة الحرارة للنبرة
     */
    private double getTemperatureForTone(String tone) {
        switch (tone) {
            case "emergency":
                return 0.3; // Lower temperature for more focused, urgent content
            case "educational":
                return 0.7; // Medium temperature for balanced educational content
            case "friendly":
                return 0.9; // Higher temperature for more creative, friendly content
            default:
                return 0.5; // Default for formal tone
        }
    }

    /**
     * Parse Gemini API response
     * تحليل استجابة Gemini API
     */
    private PosterContent parseGeminiResponse(String responseBody, String topic, String language) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content").path("parts").get(0).path("text");
                String text = content.asText();

                // Try to extract JSON from the response
                String jsonText = extractJsonFromText(text);
                if (jsonText != null) {
                    JsonNode json = objectMapper.readTree(jsonText);
                    String title = json.path("title").asText();
                    String slogan = json.path("slogan").asText();
                    List<String> bulletPoints = new ArrayList<>();
                    JsonNode points = json.path("bulletPoints");
                    if (points.isArray()) {
                        for (JsonNode point : points) {
                            bulletPoints.add(point.asText());
                        }
                    }

                    // Build PosterContent
                    PosterContent contentObj = PosterContent.builder()
                        .title(title)
                        .topic(topic)
                        .mainMessage(slogan)
                        .bulletPoints(bulletPoints)
                        .closing("للمزيد من المعلومات، يرجى زيارة أقرب مركز صحي أو الاتصال بخط الطوارئ الصحي 122")
                        .language(language != null ? language : "ar")
                        .aiGenerated(true)
                        .mohApproved(true)
                        .build();

                    return contentObj;
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing Gemini response: " + e.getMessage());
        }

        // Fallback if parsing fails
        return generateFallbackContent(topic, language, "formal");
    }

    /**
     * Extract JSON from text response
     * استخراج JSON من نص الاستجابة
     */
    private String extractJsonFromText(String text) {
        if (text == null) return null;

        // Try to find JSON object in the text
        int startIdx = text.indexOf("{");
        int endIdx = text.lastIndexOf("}");
        if (startIdx >= 0 && endIdx > startIdx) {
            return text.substring(startIdx, endIdx + 1);
        }

        return null;
    }

    /**
     * Generate fallback content if AI fails
     * توليد محتوى احتياطي إذا فشل الذكاء الاصطناعي
     */
    private PosterContent generateFallbackContent(String topic, String language, String tone) {
        // Get MOH guidelines for the topic
        List<String> guidelines = contentAuthorityService.getMOHGuidelines(topic);
        if (guidelines == null || guidelines.isEmpty()) {
            // Try to get from HealthStatisticsService
            Map<String, List<String>> allTopics = healthStatisticsService.getAllCategoryTopics();
            for (Map.Entry<String, List<String>> entry : allTopics.entrySet()) {
                if (entry.getValue().contains(topic)) {
                    // Use category name as fallback
                    guidelines = Arrays.asList(
                        "اتبع إرشادات وزارة الصحة العراقية",
                        "استشر الطبيب للحصول على معلومات دقيقة",
                        "اتخذ الإجراءات الوقائية المناسبة"
                    );
                    break;
                }
            }
        }

        if (guidelines == null || guidelines.isEmpty()) {
            guidelines = Arrays.asList(
                "اتبع إرشادات وزارة الصحة العراقية",
                "استشر الطبيب للحصول على معلومات دقيقة"
            );
        }

        // Build fallback content
        String title = "توعية صحية: " + topic;
        String slogan = "معاً من أجل صحة أفضل في كركوك";

        return PosterContent.builder()
            .title(title)
            .topic(topic)
            .mainMessage(slogan)
            .bulletPoints(guidelines.subList(0, Math.min(5, guidelines.size())))
            .closing("للمزيد من المعلومات، يرجى زيارة أقرب مركز صحي أو الاتصال بخط الطوارئ الصحي 122")
            .language(language != null ? language : "ar")
            .aiGenerated(false) // Mark as not AI-generated since it's fallback
            .mohApproved(true)
            .build();
    }

    /**
     * Regenerate content with different variations
     * إعادة توليد المحتوى مع اختلافات
     */
    public PosterContent regenerateContent(String topic, String language, String tone, int variation) {
        // Add variation to prompt to get different results
        if (variation % 2 == 0) {
            tone = "friendly";
        } else {
            tone = "educational";
        }
        return generatePosterContent(topic, language, tone);
    }
}


