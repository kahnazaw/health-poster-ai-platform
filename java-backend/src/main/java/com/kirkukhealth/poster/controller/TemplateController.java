package com.kirkukhealth.poster.controller;

import com.kirkukhealth.poster.service.ContentAuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * REST Controller for Template Management
 * متحكم REST لإدارة القوالب
 */
@RestController
@RequestMapping("/api/templates")
@CrossOrigin(origins = "*")
public class TemplateController {

    @Autowired
    private ContentAuthorityService contentAuthorityService;

    /**
     * Get MOH-approved topics
     * الحصول على المواضيع المعتمدة من وزارة الصحة
     * 
     * GET /api/templates/moh-topics
     */
    @GetMapping("/moh-topics")
    public ResponseEntity<Map<String, Object>> getMOHTopics() {
        Set<String> topics = contentAuthorityService.getMOHApprovedTopics();
        
        Map<String, Object> response = new HashMap<>();
        response.put("topics", topics);
        response.put("count", topics.size());
        response.put("message", "المواضيع المعتمدة من وزارة الصحة العراقية");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get MOH guidelines for a specific topic
     * الحصول على إرشادات وزارة الصحة لموضوع معين
     * 
     * GET /api/templates/moh-guidelines/{topic}
     */
    @GetMapping("/moh-guidelines/{topic}")
    public ResponseEntity<Map<String, Object>> getMOHGuidelines(@PathVariable String topic) {
        java.util.List<String> guidelines = contentAuthorityService.getMOHGuidelines(topic);
        boolean isApproved = contentAuthorityService.isMOHApprovedTopic(topic);
        
        Map<String, Object> response = new HashMap<>();
        response.put("topic", topic);
        response.put("guidelines", guidelines);
        response.put("mohApproved", isApproved);
        response.put("count", guidelines.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Toggle between Global and MOH templates
     * التبديل بين القوالب العالمية وقوالب وزارة الصحة
     * 
     * GET /api/templates?type={global|moh}
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getTemplates(
            @RequestParam(value = "type", defaultValue = "all") String type) {
        
        Map<String, Object> response = new HashMap<>();
        
        if ("moh".equalsIgnoreCase(type)) {
            Set<String> mohTopics = contentAuthorityService.getMOHApprovedTopics();
            response.put("type", "MOH");
            response.put("topics", mohTopics);
            response.put("message", "قوالب وزارة الصحة العراقية");
        } else if ("global".equalsIgnoreCase(type)) {
            // Global templates (can be extended)
            response.put("type", "GLOBAL");
            response.put("topics", java.util.Collections.emptyList());
            response.put("message", "القوالب العالمية");
        } else {
            // All templates
            Set<String> mohTopics = contentAuthorityService.getMOHApprovedTopics();
            response.put("type", "ALL");
            response.put("mohTopics", mohTopics);
            response.put("globalTopics", java.util.Collections.emptyList());
            response.put("message", "جميع القوالب");
        }
        
        return ResponseEntity.ok(response);
    }
}

