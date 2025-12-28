package com.kirkukhealth.poster.service;

import com.ibm.icu.text.Bidi;
import org.springframework.stereotype.Service;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.util.HashMap;
import java.util.Map;

/**
 * Multi-language Text Rendering Service
 * خدمة عرض النصوص متعددة اللغات
 * 
 * Supports: Arabic (ar), Kurdish (ku), Turkmen (tr), English (en)
 * يدعم: العربية، الكردية، التركمانية، الإنجليزية
 */
@Service
public class MultiLanguageTextService {

    private static final Map<String, String> FONT_MAP = new HashMap<>();
    
    static {
        // Font mappings for different languages
        // Arabic, Kurdish, Turkmen: Use Arial Unicode MS or similar
        FONT_MAP.put("ar", "Arial Unicode MS");
        FONT_MAP.put("ku", "Arial Unicode MS");
        FONT_MAP.put("tr", "Arial Unicode MS");
        FONT_MAP.put("en", "Arial");
    }

    /**
     * Get appropriate font for language
     * الحصول على الخط المناسب للغة
     */
    public Font getFontForLanguage(String language, int style, int size) {
        String fontName = FONT_MAP.getOrDefault(language, "Arial Unicode MS");
        
        // Try to load the font, fallback to Arial if not available
        try {
            Font font = new Font(fontName, style, size);
            if (font.getFamily().equals(fontName)) {
                return font;
            }
        } catch (Exception e) {
            // Font not available, use default
        }
        
        // Fallback to Arial
        return new Font("Arial", style, size);
    }

    /**
     * Check if text is RTL (Right-to-Left)
     * التحقق من كون النص من اليمين لليسار
     */
    public boolean isRTL(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        
        // Use ICU4J Bidi for accurate RTL detection
        Bidi bidi = new Bidi(text, Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
        return bidi.isRightToLeft();
    }

    /**
     * Get text direction
     * الحصول على اتجاه النص
     */
    public int getTextDirection(String text) {
        if (isRTL(text)) {
            return Bidi.DIRECTION_RIGHT_TO_LEFT;
        }
        return Bidi.DIRECTION_LEFT_TO_RIGHT;
    }

    /**
     * Reverse text for RTL languages if needed
     * عكس النص للغات من اليمين لليسار إذا لزم الأمر
     */
    public String prepareTextForRendering(String text, String language) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        // For RTL languages, ensure proper text direction
        if (isRTL(text)) {
            // Use ICU4J Bidi to properly handle mixed-direction text
            Bidi bidi = new Bidi(text, Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
            if (bidi.isRightToLeft()) {
                // Text is already RTL, return as is
                return text;
            }
        }
        
        return text;
    }

    /**
     * Get language-specific formatting
     * الحصول على التنسيق الخاص باللغة
     */
    public Map<String, Object> getLanguageFormatting(String language) {
        Map<String, Object> formatting = new HashMap<>();
        
        switch (language) {
            case "ar":
            case "ku":
            case "tr":
                formatting.put("direction", "RTL");
                formatting.put("alignment", "right");
                formatting.put("fontFamily", "Arial Unicode MS");
                break;
            case "en":
            default:
                formatting.put("direction", "LTR");
                formatting.put("alignment", "left");
                formatting.put("fontFamily", "Arial");
                break;
        }
        
        return formatting;
    }

    /**
     * Wrap text for multi-line rendering with language awareness
     * لف النص لعرض متعدد الأسطر مع مراعاة اللغة
     */
    public String[] wrapText(String text, int maxWidth, Font font, FontRenderContext frc) {
        if (text == null || text.isEmpty()) {
            return new String[0];
        }
        
        TextLayout layout = new TextLayout(text, font, frc);
        if (layout.getAdvance() <= maxWidth) {
            return new String[]{text};
        }
        
        // Simple word wrapping (can be enhanced with ICU4J BreakIterator)
        String[] words = text.split("\\s+");
        java.util.List<String> lines = new java.util.ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
            TextLayout testLayout = new TextLayout(testLine, font, frc);
            
            if (testLayout.getAdvance() > maxWidth && !currentLine.isEmpty()) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                currentLine = new StringBuilder(testLine);
            }
        }
        
        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }
        
        return lines.toArray(new String[0]);
    }
}

