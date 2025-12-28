package com.kirkukhealth.poster.service;

import com.kirkukhealth.poster.model.PosterContent;
import com.kirkukhealth.poster.model.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * High-Resolution Poster Export Service
 * خدمة تصدير البوسترات بجودة عالية للطباعة
 * 
 * Generates print-quality images (PNG) suitable for professional printing
 * توليد صور بجودة طباعة احترافية (PNG)
 */
@Service
public class PosterExportService {

    @Autowired
    private PosterImageService posterImageService;

    // Print quality: 300 DPI (already set in PosterImageService)
    // جودة الطباعة: 300 DPI (محددة مسبقاً في PosterImageService)
    private static final int PRINT_DPI = 300;
    private static final int POSTER_WIDTH_300DPI = 2480;  // A4 width at 300 DPI
    private static final int POSTER_HEIGHT_300DPI = 3508; // A4 height at 300 DPI

    /**
     * Generate high-resolution PNG for printing
     * توليد PNG بجودة عالية للطباعة
     * 
     * @param content Poster content
     * @param profile User profile with health center info
     * @return PNG image bytes (300 DPI, suitable for printing)
     */
    public byte[] generatePrintQualityPNG(PosterContent content, UserProfile profile) throws IOException {
        // Use existing service which already generates 300 DPI images
        return posterImageService.generatePosterImage(content, profile);
    }

    /**
     * Generate high-resolution PNG with custom DPI
     * توليد PNG بجودة مخصصة
     * 
     * @param content Poster content
     * @param profile User profile
     * @param dpi Desired DPI (300 recommended for printing)
     * @return PNG image bytes
     */
    public byte[] generateCustomDPI(PosterContent content, UserProfile profile, int dpi) throws IOException {
        // For now, use standard 300 DPI (already optimal for printing)
        // يمكن توسيع هذا لاحقاً لدعم DPI مخصص
        // Note: Current implementation uses fixed 300 DPI which is optimal for printing
        return generatePrintQualityPNG(content, profile);
    }

    /**
     * Get image metadata
     * الحصول على معلومات الصورة
     */
    public ImageMetadata getImageMetadata() {
        return ImageMetadata.builder()
            .width(POSTER_WIDTH_300DPI)
            .height(POSTER_HEIGHT_300DPI)
            .dpi(PRINT_DPI)
            .format("PNG")
            .colorMode("RGB")
            .suitableForPrinting(true)
            .build();
    }

    /**
     * Image metadata class
     */
    public static class ImageMetadata {
        private int width;
        private int height;
        private int dpi;
        private String format;
        private String colorMode;
        private boolean suitableForPrinting;

        public static ImageMetadataBuilder builder() {
            return new ImageMetadataBuilder();
        }

        public static class ImageMetadataBuilder {
            private int width;
            private int height;
            private int dpi;
            private String format;
            private String colorMode;
            private boolean suitableForPrinting;

            public ImageMetadataBuilder width(int width) {
                this.width = width;
                return this;
            }

            public ImageMetadataBuilder height(int height) {
                this.height = height;
                return this;
            }

            public ImageMetadataBuilder dpi(int dpi) {
                this.dpi = dpi;
                return this;
            }

            public ImageMetadataBuilder format(String format) {
                this.format = format;
                return this;
            }

            public ImageMetadataBuilder colorMode(String colorMode) {
                this.colorMode = colorMode;
                return this;
            }

            public ImageMetadataBuilder suitableForPrinting(boolean suitableForPrinting) {
                this.suitableForPrinting = suitableForPrinting;
                return this;
            }

            public ImageMetadata build() {
                ImageMetadata metadata = new ImageMetadata();
                metadata.width = this.width;
                metadata.height = this.height;
                metadata.dpi = this.dpi;
                metadata.format = this.format;
                metadata.colorMode = this.colorMode;
                metadata.suitableForPrinting = this.suitableForPrinting;
                return metadata;
            }
        }

        // Getters
        public int getWidth() { return width; }
        public int getHeight() { return height; }
        public int getDpi() { return dpi; }
        public String getFormat() { return format; }
        public String getColorMode() { return colorMode; }
        public boolean isSuitableForPrinting() { return suitableForPrinting; }
    }
}

