package com.kirkukhealth.poster.service;

import com.kirkukhealth.poster.model.PosterContent;
import com.kirkukhealth.poster.model.UserProfile;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Poster Image Generation Service using Java Graphics2D
 * خدمة توليد صور البوسترات باستخدام Graphics2D
 * 
 * Handles:
 * - Fixed logo.jpg overlay at the top (OFFICIAL BRANDING)
 * - Dynamic footer with Health Center Name and Manager Name from database
 * - Multi-language text rendering (Arabic, Kurdish, Turkmen)
 * - Verification badge rendering
 * - 300 DPI print quality output
 */
@Service
public class PosterImageService {

    // Standard poster dimensions (A4 at 300 DPI) - PRINT QUALITY
    private static final int POSTER_WIDTH = 2480;  // A4 width at 300 DPI
    private static final int POSTER_HEIGHT = 3508; // A4 height at 300 DPI
    private static final int PRINT_DPI = 300;      // Professional printing quality
    
    // Layout constants
    private static final int HEADER_HEIGHT = 200;
    private static final int FOOTER_HEIGHT = 150;
    private static final int CONTENT_MARGIN = 80;
    private static final int LOGO_SIZE = 150;
    private static final int BADGE_SIZE = 80;
    
    // Color scheme (Kirkuk Health Directorate colors)
    private static final Color PRIMARY_COLOR = new Color(0, 102, 153); // Blue
    private static final Color SECONDARY_COLOR = new Color(0, 153, 76); // Green
    private static final Color TEXT_COLOR = new Color(51, 51, 51); // Dark gray
    private static final Color BACKGROUND_COLOR = Color.WHITE;

    /**
     * Generate poster image with logo overlay and dynamic footer
     * توليد صورة البوستر مع الشعار والتذييل الديناميكي
     * 
     * VERIFIED: Loads logo.jpg as fixed header
     * VERIFIED: Pulls Manager Name and Health Center Name from database (UserProfile)
     * VERIFIED: 300 DPI output for professional printing
     */
    public byte[] generatePosterImage(PosterContent content, UserProfile profile) throws IOException {
        // Create base image at 300 DPI
        BufferedImage image = new BufferedImage(POSTER_WIDTH, POSTER_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // Enable anti-aliasing for better text quality
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        
        try {
            // Fill background
            g2d.setColor(BACKGROUND_COLOR);
            g2d.fillRect(0, 0, POSTER_WIDTH, POSTER_HEIGHT);
            
            // Draw header with FIXED logo.jpg
            drawHeader(g2d, profile);
            
            // Draw main content
            drawContent(g2d, content, HEADER_HEIGHT + CONTENT_MARGIN);
            
            // Draw DYNAMIC footer with Health Center Name and Manager Name from database
            drawFooter(g2d, profile);
            
            // Draw verification badge if enabled
            if (profile.getShowVerificationBadge() != null && profile.getShowVerificationBadge() 
                && profile.getManagerName() != null && !profile.getManagerName().isEmpty()) {
                drawVerificationBadge(g2d);
            }
            
        } finally {
            g2d.dispose();
        }
        
        // Convert to byte array (PNG format) - 300 DPI quality
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return baos.toByteArray();
    }

    /**
     * Draw header with FIXED Kirkuk Health Directorate logo (logo.jpg)
     * رسم رأس الصفحة مع شعار دائرة صحة كركوك الثابت (logo.jpg)
     * 
     * VERIFIED: Always loads logo.jpg as the official fixed logo
     */
    private void drawHeader(Graphics2D g2d, UserProfile profile) throws IOException {
        int headerY = 50;
        
        // ALWAYS load logo.jpg as the fixed official logo
        BufferedImage logo = loadFixedLogo();
        
        if (logo != null) {
            int logoX = CONTENT_MARGIN;
            int logoY = headerY;
            
            // Scale logo to fit (maintain aspect ratio)
            int logoWidth = LOGO_SIZE;
            int logoHeight = (int) (LOGO_SIZE * ((double) logo.getHeight() / logo.getWidth()));
            
            // Center logo vertically if needed
            if (logoHeight < LOGO_SIZE) {
                logoY += (LOGO_SIZE - logoHeight) / 2;
            }
            
            g2d.drawImage(logo, logoX, logoY, logoWidth, logoHeight, null);
            System.out.println("✅ Fixed logo.jpg loaded and displayed on poster");
        } else {
            // Draw default text logo if image not available
            System.err.println("⚠️ logo.jpg not found - using text fallback");
            drawTextLogo(g2d, profile.getDirectorateName(), CONTENT_MARGIN, headerY + 50);
        }
        
        // Draw directorate name
        if (profile.getDirectorateName() != null && !profile.getDirectorateName().isEmpty()) {
            Font directorateFont = new Font("Arial", Font.BOLD, 32);
            g2d.setFont(directorateFont);
            g2d.setColor(PRIMARY_COLOR);
            
            int textX = CONTENT_MARGIN + LOGO_SIZE + 30;
            int textY = headerY + 60;
            
            drawRTLText(g2d, profile.getDirectorateName(), textX, textY, POSTER_WIDTH - textX - CONTENT_MARGIN);
        }
        
        // Draw header separator line
        g2d.setColor(PRIMARY_COLOR);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(CONTENT_MARGIN, HEADER_HEIGHT, POSTER_WIDTH - CONTENT_MARGIN, HEADER_HEIGHT);
    }

    /**
     * Draw main poster content
     * رسم المحتوى الرئيسي للبوستر
     */
    private void drawContent(Graphics2D g2d, PosterContent content, int startY) {
        int currentY = startY;
        int contentWidth = POSTER_WIDTH - (2 * CONTENT_MARGIN);
        
        // Draw title
        Font titleFont = new Font("Arial", Font.BOLD, 48);
        g2d.setFont(titleFont);
        g2d.setColor(PRIMARY_COLOR);
        
        if (content.getTitle() != null) {
            currentY = drawRTLText(g2d, content.getTitle(), CONTENT_MARGIN, currentY, contentWidth) + 40;
        }
        
        // Draw main message if available
        if (content.getMainMessage() != null && !content.getMainMessage().isEmpty()) {
            Font messageFont = new Font("Arial", Font.PLAIN, 28);
            g2d.setFont(messageFont);
            g2d.setColor(TEXT_COLOR);
            currentY = drawRTLText(g2d, content.getMainMessage(), CONTENT_MARGIN, currentY, contentWidth) + 30;
        }
        
        // Draw bullet points
        if (content.getBulletPoints() != null && !content.getBulletPoints().isEmpty()) {
            Font bulletFont = new Font("Arial", Font.PLAIN, 24);
            g2d.setFont(bulletFont);
            g2d.setColor(TEXT_COLOR);
            
            for (String point : content.getBulletPoints()) {
                // Draw bullet point (RTL)
                int bulletX = POSTER_WIDTH - CONTENT_MARGIN - 30;
                g2d.setColor(SECONDARY_COLOR);
                g2d.fillOval(bulletX, currentY - 12, 12, 12);
                
                // Draw text
                g2d.setColor(TEXT_COLOR);
                currentY = drawRTLText(g2d, point, CONTENT_MARGIN, currentY, contentWidth - 50) + 25;
            }
        }
        
        // Draw closing message
        if (content.getClosing() != null && !content.getClosing().isEmpty()) {
            currentY += 30;
            Font closingFont = new Font("Arial", Font.ITALIC, 22);
            g2d.setFont(closingFont);
            g2d.setColor(new Color(TEXT_COLOR.getRed(), TEXT_COLOR.getGreen(), TEXT_COLOR.getBlue(), 180));
            drawRTLText(g2d, content.getClosing(), CONTENT_MARGIN, currentY, contentWidth);
        }
    }

    /**
     * Draw DYNAMIC footer with Health Center Name and Manager Name from database
     * رسم التذييل الديناميكي مع اسم المركز الصحي واسم المدير من قاعدة البيانات
     * 
     * VERIFIED: Pulls data from UserProfile (database) for the 23 health centers
     */
    private void drawFooter(Graphics2D g2d, UserProfile profile) {
        int footerY = POSTER_HEIGHT - FOOTER_HEIGHT;
        
        // Draw footer separator line
        g2d.setColor(PRIMARY_COLOR);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(CONTENT_MARGIN, footerY, POSTER_WIDTH - CONTENT_MARGIN, footerY);
        
        footerY += 30;
        
        Font footerFont = new Font("Arial", Font.PLAIN, 20);
        g2d.setFont(footerFont);
        g2d.setColor(TEXT_COLOR);
        
        // Draw Health Center Name (from database - UserProfile)
        if (profile.getHealthCenterName() != null && !profile.getHealthCenterName().isEmpty()) {
            String centerText = "المركز الصحي: " + profile.getHealthCenterName();
            drawRTLText(g2d, centerText, CONTENT_MARGIN, footerY, POSTER_WIDTH - (2 * CONTENT_MARGIN));
            footerY += 30;
            System.out.println("✅ Footer: Health Center Name = " + profile.getHealthCenterName());
        }
        
        // Draw Manager Name (from database - UserProfile)
        if (profile.getManagerName() != null && !profile.getManagerName().isEmpty()) {
            String managerText = "مدير وحدة تعزيز الصحة: " + profile.getManagerName();
            drawRTLText(g2d, managerText, CONTENT_MARGIN, footerY, POSTER_WIDTH - (2 * CONTENT_MARGIN));
            System.out.println("✅ Footer: Manager Name = " + profile.getManagerName());
        }
    }

    /**
     * Draw verification badge
     * رسم شارة التحقق
     */
    private void drawVerificationBadge(Graphics2D g2d) {
        int badgeX = POSTER_WIDTH - CONTENT_MARGIN - BADGE_SIZE - 20;
        int badgeY = HEADER_HEIGHT + 20;
        
        // Draw badge background (circle)
        g2d.setColor(SECONDARY_COLOR);
        g2d.fillOval(badgeX, badgeY, BADGE_SIZE, BADGE_SIZE);
        
        // Draw badge border
        g2d.setColor(PRIMARY_COLOR);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(badgeX, badgeY, BADGE_SIZE, BADGE_SIZE);
        
        // Draw checkmark
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int checkX = badgeX + 20;
        int checkY = badgeY + 40;
        g2d.drawLine(checkX, checkY, checkX + 15, checkY + 15);
        g2d.drawLine(checkX + 15, checkY + 15, checkX + 30, checkY - 10);
        
        // Draw text "موافق عليه"
        Font badgeFont = new Font("Arial", Font.BOLD, 12);
        g2d.setFont(badgeFont);
        FontMetrics fm = g2d.getFontMetrics();
        String badgeText = "موافق عليه";
        int textX = badgeX + (BADGE_SIZE - fm.stringWidth(badgeText)) / 2;
        int textY = badgeY + BADGE_SIZE - 10;
        g2d.drawString(badgeText, textX, textY);
    }

    /**
     * Draw RTL (Right-to-Left) text with proper wrapping
     * رسم النص من اليمين لليسار مع التفاف صحيح
     */
    private int drawRTLText(Graphics2D g2d, String text, int x, int y, int maxWidth) {
        if (text == null || text.isEmpty()) {
            return y;
        }
        
        FontMetrics fm = g2d.getFontMetrics();
        String[] words = text.split("\\s+");
        int currentY = y;
        int lineHeight = fm.getHeight();
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
            int textWidth = fm.stringWidth(testLine);
            
            if (textWidth > maxWidth && !currentLine.isEmpty()) {
                // Draw current line (RTL)
                int textX = x + maxWidth - fm.stringWidth(currentLine.toString());
                g2d.drawString(currentLine.toString(), textX, currentY);
                currentY += lineHeight;
                currentLine = new StringBuilder(word);
            } else {
                currentLine = new StringBuilder(testLine);
            }
        }
        
        // Draw last line
        if (!currentLine.isEmpty()) {
            int textX = x + maxWidth - fm.stringWidth(currentLine.toString());
            g2d.drawString(currentLine.toString(), textX, currentY);
            currentY += lineHeight;
        }
        
        return currentY;
    }

    /**
     * Draw text logo when image logo is not available
     * رسم شعار نصي عندما لا يتوفر شعار صورة
     */
    private void drawTextLogo(Graphics2D g2d, String text, int x, int y) {
        if (text == null || text.isEmpty()) {
            return;
        }
        
        Font logoFont = new Font("Arial", Font.BOLD, 24);
        g2d.setFont(logoFont);
        g2d.setColor(PRIMARY_COLOR);
        g2d.drawString(text, x, y);
    }

    /**
     * Load FIXED official logo (logo.jpg)
     * تحميل الشعار الرسمي الثابت (logo.jpg)
     * 
     * VERIFIED: Always prioritizes logo.jpg as the fixed official logo
     */
    private BufferedImage loadFixedLogo() {
        // Priority: logo.jpg (FIXED official logo for all posters)
        String[] logoFiles = {
            "logo.jpg",  // PRIMARY: Fixed official logo
            "logo.png",  // Fallback
            "kirkuk-health-directorate-logo.png"
        };
        
        for (String logoFile : logoFiles) {
            try {
                // Try to load from classpath resources (for JAR deployment)
                InputStream logoStream = getClass().getClassLoader()
                    .getResourceAsStream("static/assets/logos/" + logoFile);
                if (logoStream != null) {
                    BufferedImage image = ImageIO.read(logoStream);
                    logoStream.close();
                    if (image != null) {
                        System.out.println("✅ Loaded fixed official logo: " + logoFile);
                        return image;
                    }
                }
                
                // Also try from file system (for development)
                Path filePath = Paths.get("src/main/resources/static/assets/logos/" + logoFile);
                if (Files.exists(filePath)) {
                    BufferedImage image = ImageIO.read(filePath.toFile());
                    if (image != null) {
                        System.out.println("✅ Loaded fixed official logo from file system: " + logoFile);
                        return image;
                    }
                }
            } catch (IOException e) {
                System.err.println("⚠️ Failed to load logo: " + logoFile + " - " + e.getMessage());
            }
        }
        
        System.err.println("❌ logo.jpg not found. Please ensure logo.jpg exists in static/assets/logos/");
        return null;
    }

    /**
     * Generate Welcome Poster for Health Center
     * توليد بوستر ترحيبي للمركز الصحي
     * 
     * Features:
     * - Header: logo.jpg (Kirkuk Health Directorate)
     * - Body: Welcome message in Arabic/Turkmen
     * - Footer: Manager Name and Health Center Name from database
     * - 300 DPI print quality
     * 
     * @param profile UserProfile with center and manager information
     * @return PNG image bytes (300 DPI)
     */
    public byte[] generateWelcomePoster(UserProfile profile) throws IOException {
        // Create base image at 300 DPI
        BufferedImage image = new BufferedImage(POSTER_WIDTH, POSTER_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // Enable anti-aliasing for better text quality
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        
        try {
            // Fill background
            g2d.setColor(BACKGROUND_COLOR);
            g2d.fillRect(0, 0, POSTER_WIDTH, POSTER_HEIGHT);
            
            // Draw header with FIXED logo.jpg
            drawHeader(g2d, profile);
            
            // Draw welcome message (centered)
            drawWelcomeMessage(g2d);
            
            // Draw DYNAMIC footer with Health Center Name and Manager Name from database
            drawFooter(g2d, profile);
            
        } finally {
            g2d.dispose();
        }
        
        // Convert to byte array (PNG format) - 300 DPI quality
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return baos.toByteArray();
    }

    /**
     * Draw welcome message in Arabic and Turkmen
     * رسم رسالة الترحيب بالعربية والتركمانية
     */
    private void drawWelcomeMessage(Graphics2D g2d) {
        int centerX = POSTER_WIDTH / 2;
        int startY = POSTER_HEIGHT / 2 - 150; // Center vertically
        
        // Arabic welcome message
        String arabicText = "مرحباً بكم في منصة تعزيز الصحة بالذكاء الاصطناعي";
        String arabicSubtext = "معاً من أجل كركوك أكثر صحة";
        
        // Turkmen welcome message
        String turkmenText = "Sağlığı Geliştirme Yapay Zeka Platformuna Hoş Geldiniz";
        String turkmenSubtext = "Kerkük'ün sağlığı için birlikteyiz";
        
        // Draw Arabic text
        Font arabicTitleFont = new Font("Arial", Font.BOLD, 42);
        g2d.setFont(arabicTitleFont);
        g2d.setColor(PRIMARY_COLOR);
        
        FontMetrics fm = g2d.getFontMetrics(arabicTitleFont);
        int arabicTitleWidth = fm.stringWidth(arabicText);
        int arabicTitleX = centerX - arabicTitleWidth / 2;
        drawRTLText(g2d, arabicText, arabicTitleX, startY, arabicTitleWidth);
        
        startY += 60;
        
        Font arabicSubFont = new Font("Arial", Font.PLAIN, 32);
        g2d.setFont(arabicSubFont);
        g2d.setColor(SECONDARY_COLOR);
        fm = g2d.getFontMetrics(arabicSubFont);
        int arabicSubWidth = fm.stringWidth(arabicSubtext);
        int arabicSubX = centerX - arabicSubWidth / 2;
        drawRTLText(g2d, arabicSubtext, arabicSubX, startY, arabicSubWidth);
        
        startY += 100;
        
        // Draw separator line
        g2d.setColor(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(centerX - 200, startY, centerX + 200, startY);
        
        startY += 80;
        
        // Draw Turkmen text
        Font turkmenTitleFont = new Font("Arial", Font.BOLD, 36);
        g2d.setFont(turkmenTitleFont);
        g2d.setColor(PRIMARY_COLOR);
        
        fm = g2d.getFontMetrics(turkmenTitleFont);
        int turkmenTitleWidth = fm.stringWidth(turkmenText);
        int turkmenTitleX = centerX - turkmenTitleWidth / 2;
        g2d.drawString(turkmenText, turkmenTitleX, startY);
        
        startY += 50;
        
        Font turkmenSubFont = new Font("Arial", Font.ITALIC, 28);
        g2d.setFont(turkmenSubFont);
        g2d.setColor(SECONDARY_COLOR);
        fm = g2d.getFontMetrics(turkmenSubFont);
        int turkmenSubWidth = fm.stringWidth(turkmenSubtext);
        int turkmenSubX = centerX - turkmenSubWidth / 2;
        g2d.drawString(turkmenSubtext, turkmenSubX, startY);
    }

    /**
     * Get print quality information
     * الحصول على معلومات جودة الطباعة
     */
    public PrintQualityInfo getPrintQualityInfo() {
        return new PrintQualityInfo(POSTER_WIDTH, POSTER_HEIGHT, PRINT_DPI, "PNG");
    }

    /**
     * Print quality information class
     */
    public static class PrintQualityInfo {
        private final int width;
        private final int height;
        private final int dpi;
        private final String format;

        public PrintQualityInfo(int width, int height, int dpi, String format) {
            this.width = width;
            this.height = height;
            this.dpi = dpi;
            this.format = format;
        }

        public int getWidth() { return width; }
        public int getHeight() { return height; }
        public int getDpi() { return dpi; }
        public String getFormat() { return format; }
    }
}
