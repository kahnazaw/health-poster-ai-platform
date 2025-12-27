/**
 * Multi-channel Export Utilities
 * 
 * Handles export to various formats and channels
 */

export type ExportFormat = 
  | 'png-hq' 
  | 'pdf-a4' 
  | 'pdf-a3' 
  | 'instagram' 
  | 'whatsapp' 
  | 'facebook'

export interface ExportOptions {
  format: ExportFormat
  element: HTMLElement
  filename: string
  onProgress?: (progress: number) => void
}

/**
 * Export poster to specified format
 */
export async function exportPoster(options: ExportOptions): Promise<void> {
  const { format, element, filename, onProgress } = options

  onProgress?.(10)

  try {
    const html2canvas = (await import('html2canvas')).default
    const { default: jsPDF } = await import('jspdf')

    onProgress?.(30)

    // Determine dimensions based on format
    const dimensions = getFormatDimensions(format)
    
    // Capture canvas
    const canvas = await html2canvas(element, {
      scale: format.includes('hq') ? 3 : 2,
      useCORS: true,
      backgroundColor: '#ffffff',
      logging: false,
      width: dimensions.width,
      height: dimensions.height,
    })

    onProgress?.(60)

    if (format.startsWith('png')) {
      // PNG export
      const link = document.createElement('a')
      link.download = `${filename}.png`
      link.href = canvas.toDataURL('image/png', 1.0)
      link.click()
      onProgress?.(100)
      return
    }

    if (format.startsWith('pdf')) {
      // PDF export
      const pdfFormat = format === 'pdf-a3' ? 'a3' : 'a4'
      const pdf = new jsPDF({
        orientation: dimensions.orientation as 'portrait' | 'landscape',
        unit: 'mm',
        format: pdfFormat,
      })

      const pdfWidth = pdf.internal.pageSize.getWidth()
      const pdfHeight = pdf.internal.pageSize.getHeight()
      const imgWidth = canvas.width
      const imgHeight = canvas.height
      const ratio = Math.min(pdfWidth / imgWidth, pdfHeight / imgHeight)
      const imgX = (pdfWidth - imgWidth * ratio) / 2
      const imgY = 0

      pdf.addImage(canvas.toDataURL('image/png'), 'PNG', imgX, imgY, imgWidth * ratio, imgHeight * ratio)
      pdf.save(`${filename}.pdf`)
      onProgress?.(100)
      return
    }

    // Social media formats (square, vertical, landscape)
    if (format === 'instagram' || format === 'whatsapp' || format === 'facebook') {
      const socialCanvas = await createSocialFormat(canvas, format as 'instagram' | 'whatsapp' | 'facebook')
      const link = document.createElement('a')
      link.download = `${filename}-${format}.png`
      link.href = socialCanvas.toDataURL('image/png', 1.0)
      link.click()
      onProgress?.(100)
      return
    }
  } catch (error) {
    console.error('Export error:', error)
    throw new Error('فشل التصدير')
  }
}

/**
 * Get dimensions for export format
 */
function getFormatDimensions(format: ExportFormat): {
  width: number
  height: number
  orientation: 'portrait' | 'landscape'
} {
  // Base A4 dimensions in pixels at 300 DPI
  const a4Width = 2480
  const a4Height = 3508

  switch (format) {
    case 'png-hq':
      return { width: a4Width, height: a4Height, orientation: 'portrait' }
    case 'pdf-a4':
      return { width: a4Width, height: a4Height, orientation: 'portrait' }
    case 'pdf-a3':
      return { width: 3508, height: 4961, orientation: 'portrait' }
    case 'instagram':
      // Square format (1080x1080)
      return { width: 1080, height: 1080, orientation: 'portrait' }
    case 'whatsapp':
      // Vertical format (1080x1920)
      return { width: 1080, height: 1920, orientation: 'portrait' }
    case 'facebook':
      // Landscape format (1200x630)
      return { width: 1200, height: 630, orientation: 'landscape' }
    default:
      return { width: a4Width, height: a4Height, orientation: 'portrait' }
  }
}

/**
 * Create social media format from canvas
 */
function createSocialFormat(
  sourceCanvas: HTMLCanvasElement,
  format: 'instagram' | 'whatsapp' | 'facebook'
): Promise<HTMLCanvasElement> {
  return new Promise((resolve) => {
    const dimensions = getFormatDimensions(format)
    const canvas = document.createElement('canvas')
    canvas.width = dimensions.width
    canvas.height = dimensions.height
    const ctx = canvas.getContext('2d')!

    // Fill background
    ctx.fillStyle = '#ffffff'
    ctx.fillRect(0, 0, canvas.width, canvas.height)

    // Calculate scaling to fit
    const scale = Math.min(
      canvas.width / sourceCanvas.width,
      canvas.height / sourceCanvas.height
    )

    const x = (canvas.width - sourceCanvas.width * scale) / 2
    const y = (canvas.height - sourceCanvas.height * scale) / 2

    ctx.drawImage(sourceCanvas, x, y, sourceCanvas.width * scale, sourceCanvas.height * scale)
    resolve(canvas)
  })
}

/**
 * Generate QR code for poster
 */
export async function generateQRCode(url: string): Promise<string> {
  try {
    const QRCode = (await import('qrcode')).default
    const qrDataUrl = await QRCode.toDataURL(url, {
      width: 300,
      margin: 2,
      color: {
        dark: '#000000',
        light: '#FFFFFF',
      },
    })
    return qrDataUrl
  } catch (error) {
    console.error('QR code generation error')
    throw new Error('فشل توليد QR code')
  }
}

