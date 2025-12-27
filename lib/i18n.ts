/**
 * Internationalization (i18n) Support
 * 
 * Simple i18n implementation for Next.js App Router
 */

export type Language = 'ar' | 'ku' | 'tr' | 'en'

export const supportedLanguages: Language[] = ['ar', 'ku', 'tr', 'en']

export const defaultLanguage: Language = 'ar'

// Translation keys
export type TranslationKey = 
  | 'app.name'
  | 'app.description'
  | 'auth.login'
  | 'auth.logout'
  | 'auth.email'
  | 'auth.password'
  | 'dashboard.title'
  | 'dashboard.admin'
  | 'dashboard.user'
  | 'poster.create'
  | 'poster.title'
  | 'poster.topic'
  | 'poster.generate'
  | 'poster.download'
  | 'poster.print'
  | 'poster.status.draft'
  | 'poster.status.under_review'
  | 'poster.status.approved'
  | 'poster.status.rejected'
  | 'user.role.admin'
  | 'user.role.user'
  | 'common.save'
  | 'common.cancel'
  | 'common.delete'
  | 'common.edit'
  | 'common.loading'
  | 'common.error'

// Translations
const translations: Record<Language, Record<TranslationKey, string>> = {
  ar: {
    'app.name': 'منصة بوسترات التوعية الصحية',
    'app.description': 'منصة مؤسسية رسمية لتوليد بوسترات التوعية الصحية',
    'auth.login': 'تسجيل الدخول',
    'auth.logout': 'تسجيل الخروج',
    'auth.email': 'البريد الإلكتروني',
    'auth.password': 'كلمة المرور',
    'dashboard.title': 'لوحة التحكم',
    'dashboard.admin': 'لوحة التحكم الإدارية',
    'dashboard.user': 'لوحة تحكم المستخدم',
    'poster.create': 'إنشاء بوستر',
    'poster.title': 'عنوان البوستر',
    'poster.topic': 'موضوع التوعية الصحية',
    'poster.generate': 'توليد البوستر',
    'poster.download': 'تحميل',
    'poster.print': 'طباعة',
    'poster.status.draft': 'مسودة',
    'poster.status.under_review': 'قيد المراجعة',
    'poster.status.approved': 'معتمد',
    'poster.status.rejected': 'مرفوض',
    'user.role.admin': 'مدير',
    'user.role.user': 'مستخدم',
    'common.save': 'حفظ',
    'common.cancel': 'إلغاء',
    'common.delete': 'حذف',
    'common.edit': 'تعديل',
    'common.loading': 'جاري التحميل...',
    'common.error': 'حدث خطأ',
  },
  ku: {
    'app.name': 'پلاتفۆرمی پۆستەری تەندروستی',
    'app.description': 'پلاتفۆرمی فەرمی بۆ دروستکردنی پۆستەری تەندروستی',
    'auth.login': 'چوونەژوورەوە',
    'auth.logout': 'دەرچوون',
    'auth.email': 'ئیمەیڵ',
    'auth.password': 'وشەی نهێنی',
    'dashboard.title': 'داشبۆرد',
    'dashboard.admin': 'داشبۆردی بەڕێوەبردن',
    'dashboard.user': 'داشبۆردی بەکارهێنەر',
    'poster.create': 'دروستکردنی پۆستەر',
    'poster.title': 'ناونیشانی پۆستەر',
    'poster.topic': 'بابەتی تەندروستی',
    'poster.generate': 'دروستکردنی پۆستەر',
    'poster.download': 'داگرتن',
    'poster.print': 'چاپکردن',
    'poster.status.draft': 'پێشنووس',
    'poster.status.under_review': 'لەژێر پێداچوونەوە',
    'poster.status.approved': 'پەسندکراو',
    'poster.status.rejected': 'ڕەتکراوە',
    'user.role.admin': 'بەڕێوەبەر',
    'user.role.user': 'بەکارهێنەر',
    'common.save': 'پاشەکەوتکردن',
    'common.cancel': 'هەڵوەشاندن',
    'common.delete': 'سڕینەوە',
    'common.edit': 'دەستکاریکردن',
    'common.loading': 'بارکردن...',
    'common.error': 'هەڵەیەک ڕوویدا',
  },
  tr: {
    'app.name': 'Sağlık Poster Platformu',
    'app.description': 'Sağlık bilinçlendirme posterleri oluşturma platformu',
    'auth.login': 'Giriş Yap',
    'auth.logout': 'Çıkış Yap',
    'auth.email': 'E-posta',
    'auth.password': 'Şifre',
    'dashboard.title': 'Kontrol Paneli',
    'dashboard.admin': 'Yönetim Paneli',
    'dashboard.user': 'Kullanıcı Paneli',
    'poster.create': 'Poster Oluştur',
    'poster.title': 'Poster Başlığı',
    'poster.topic': 'Sağlık Konusu',
    'poster.generate': 'Poster Oluştur',
    'poster.download': 'İndir',
    'poster.print': 'Yazdır',
    'poster.status.draft': 'Taslak',
    'poster.status.under_review': 'İncelemede',
    'poster.status.approved': 'Onaylandı',
    'poster.status.rejected': 'Reddedildi',
    'user.role.admin': 'Yönetici',
    'user.role.user': 'Kullanıcı',
    'common.save': 'Kaydet',
    'common.cancel': 'İptal',
    'common.delete': 'Sil',
    'common.edit': 'Düzenle',
    'common.loading': 'Yükleniyor...',
    'common.error': 'Bir hata oluştu',
  },
  en: {
    'app.name': 'Health Poster Platform',
    'app.description': 'Official platform for creating health awareness posters',
    'auth.login': 'Login',
    'auth.logout': 'Logout',
    'auth.email': 'Email',
    'auth.password': 'Password',
    'dashboard.title': 'Dashboard',
    'dashboard.admin': 'Admin Dashboard',
    'dashboard.user': 'User Dashboard',
    'poster.create': 'Create Poster',
    'poster.title': 'Poster Title',
    'poster.topic': 'Health Topic',
    'poster.generate': 'Generate Poster',
    'poster.download': 'Download',
    'poster.print': 'Print',
    'poster.status.draft': 'Draft',
    'poster.status.under_review': 'Under Review',
    'poster.status.approved': 'Approved',
    'poster.status.rejected': 'Rejected',
    'user.role.admin': 'Admin',
    'user.role.user': 'User',
    'common.save': 'Save',
    'common.cancel': 'Cancel',
    'common.delete': 'Delete',
    'common.edit': 'Edit',
    'common.loading': 'Loading...',
    'common.error': 'An error occurred',
  },
}

/**
 * Get translation for a key
 */
export function t(key: TranslationKey, lang: Language = defaultLanguage): string {
  return translations[lang]?.[key] || translations[defaultLanguage][key] || key
}

/**
 * Get current language from context or default
 */
export function getLanguage(): Language {
  if (typeof window === 'undefined') return defaultLanguage
  
  const stored = localStorage.getItem('language') as Language
  return stored && supportedLanguages.includes(stored) ? stored : defaultLanguage
}

/**
 * Set language preference
 */
export function setLanguage(lang: Language): void {
  if (typeof window === 'undefined') return
  localStorage.setItem('language', lang)
}

