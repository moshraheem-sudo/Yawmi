package com.example.localization

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.LayoutDirection

interface AppStrings {
    // App Title
    val appName: String
    val appTagline: String

    // Navigation
    val navHome: String
    val navTasks: String
    val navNotes: String
    val navExpenses: String
    val navCalendar: String
    val navSettings: String

    // Greetings & Home
    val greetingHello: String
    val greetingSubtitle: String
    val todaySummaryTitle: String
    val todayTasks: String
    val tasksCompleted: String
    val tasksRemaining: String
    val quickActionsTitle: String
    val actionAddTask: String
    val actionAddNote: String
    val actionAddExpense: String
    val actionCalendar: String
    val upcomingRemindersTitle: String
    val noUpcomingReminders: String
    val todayPriorityTasks: String
    val viewAll: String

    // Tasks Screen
    val tasksTitle: String
    val filterAll: String
    val filterToday: String
    val filterUpcoming: String
    val filterCompleted: String
    val searchTasksPlaceholder: String
    val noTasksFound: String
    val noTasksSubtitle: String
    val taskPriorityHigh: String
    val taskPriorityMedium: String
    val taskPriorityLow: String
    val categoryWork: String
    val categoryPersonal: String
    val categoryStudy: String
    val categoryHealth: String
    val categoryShopping: String
    val categoryOther: String
    val dialogAddTaskTitle: String
    val dialogEditTaskTitle: String
    val taskTitleLabel: String
    val taskTitlePlaceholder: String
    val taskDescLabel: String
    val taskDescPlaceholder: String
    val dueDateLabel: String
    val selectDueDate: String
    val dueTimeLabel: String
    val selectDueTime: String
    val enableReminder: String
    val reminderScheduledSuccess: String
    val reminderPastTimeError: String
    val pinTask: String
    val priorityLabel: String
    val categoryLabel: String

    // Notes Screen
    val notesTitle: String
    val searchNotesPlaceholder: String
    val noNotesFound: String
    val noNotesSubtitle: String
    val dialogAddNoteTitle: String
    val dialogEditNoteTitle: String
    val noteTitleLabel: String
    val noteTitlePlaceholder: String
    val noteContentLabel: String
    val noteContentPlaceholder: String
    val pinNote: String
    val noteColorLabel: String
    val pinnedNotesHeader: String
    val otherNotesHeader: String
    val attachImage: String
    val attachPdf: String
    val recordAudio: String
    val stopRecording: String
    val recordingAudio: String
    val deleteAudio: String
    val sharePdf: String
    val shareText: String
    val noteReminderTitle: String
    val setNoteReminder: String
    val cancelNoteReminder: String
    val boldFormat: String
    val italicFormat: String
    val headingFormat: String
    val listFormat: String
    val checkboxFormat: String
    val quoteFormat: String
    val noteBackgroundTheme: String
    val pdfExportSuccess: String
    val audioRecordPermissionNeeded: String

    // Notes Filters & Details & Word Typesetting
    val filterAllNotes: String
    val filterPinnedNotes: String
    val filterVoiceNotes: String
    val filterAudioNotes: String
    val filterMediaNotes: String
    val filterReminderNotes: String
    val noteWordCount: String
    val noteCharCount: String
    val noteParagraphCount: String
    val noteReadingTime: String
    val noteAutoSaved: String
    val attachAudioFile: String
    val wordModeView: String
    val wordModeEdit: String
    val formatHeading1: String
    val formatHeading2: String
    val formatHeading3: String
    val formatUnderline: String
    val formatStrikethrough: String
    val formatHighlight: String
    val formatAlignRight: String
    val formatAlignCenter: String
    val formatAlignLeft: String
    val formatClear: String
    val formatDivider: String
    val formatCallout: String
    val copyAllContent: String
    val contentCopiedToast: String

    // Expenses & Salary Management
    val expensesTitle: String
    val currencyUnit: String
    val basicSalaryTitle: String
    val basicSalaryDesc: String
    val addedSalaryTitle: String
    val addedSalaryDesc: String
    val totalIncomeTitle: String
    val remainingSalaryTitle: String
    val editBasicSalaryDialogTitle: String
    val editAddedSalaryDialogTitle: String
    val salaryInputPlaceholder: String
    val saveSalary: String
    val salarySummaryTitle: String
    val salarySpentRatio: String
    val salarySafeStatus: String
    val salaryWarningStatus: String
    val todayTotalExpenses: String
    val monthTotalExpenses: String
    val dialogAddExpenseTitle: String
    val dialogEditExpenseTitle: String
    val expenseTitleLabel: String
    val expenseTitlePlaceholder: String
    val expenseAmountLabel: String
    val expenseAmountPlaceholder: String
    val expenseCategoryLabel: String
    val expenseDateLabel: String
    val expenseNoteLabel: String
    val noExpensesFound: String
    val noExpensesSubtitle: String
    val expCategoryFood: String
    val expCategoryTransport: String
    val expCategoryBills: String
    val expCategoryShopping: String
    val expCategoryHealth: String
    val expCategoryEntertainment: String
    val expCategoryOther: String

    val commitmentsAndBillsTitle: String
    val commitmentsSectionSubtitle: String
    val filterAndSortBills: String
    val sortClosestDue: String
    val sortHighestAmount: String
    val showAllBills: String
    val showUnpaidBills: String
    val btnPayBill: String
    val btnEditBill: String
    val btnAddBill: String
    val reservedInBudget: String
    val notReserved: String
    val actualCurrentBalance: String
    val availableToSpend: String
    val afterReservedCommitments: String
    val reservedCommitments: String
    val unpaidDues: String
    val previousMonthBalanceTitle: String
    val manualEntryCarryover: String
    val daysRemainingForSalary: String
    val dailyAverageExpense: String
    val quickActions: String
    val btnLogExpense: String
    val btnAddIncome: String
    val tabOverview: String
    val tabCommitments: String
    val tabHistory: String
    val tabDailyExpenses: String
    val tabAddedAmounts: String
    val searchExpensesPlaceholder: String
    val latestTransactions: String
    val financialAccountingDetails: String
    val financialAccountingSubtitle: String
    val setSalaryReceptionDay: String

    // Calendar Screen
    val calendarTitle: String
    val calendarTasksOnDay: String
    val noTasksForSelectedDay: String
    val addTaskForThisDay: String

    // Settings Screen
    val settingsTitle: String
    val languageSection: String
    val langArabic: String
    val langEnglish: String
    val appearanceSection: String
    val themeMode: String
    val themeSystem: String
    val themeDark: String
    val themeLight: String
    val themeWhite: String
    val themeBlack: String
    val themeNatural: String
    val themeTransparent: String
    val noteColorWhite: String
    val noteColorBlack: String
    val noteColorTransparent: String
    val noteColorNatural: String
    val noteColorNaturalEarth: String
    val fontSizeSection: String
    val fontSizeLabel: String
    val fontSizeExtraSmall: String
    val fontSizeSmall: String
    val fontSizeMedium: String
    val fontSizeNormal: String
    val notificationsSection: String
    val enableRemindersToggle: String
    val vibrateToggle: String
    val stopAlarm: String
    val snoozeAlarm: String
    val alarmRingingTitle: String
    val batteryOptimizationTitle: String
    val batteryOptimizationDesc: String
    val btnIgnoreBattery: String
    val btnExactAlarm: String
    val btnNotificationPerm: String
    val testAlarmBtn: String
    val testAlarmTriggered: String
    val permissionGranted: String
    val permissionNeeded: String
    val alarmBannerWarning: String
    val dataManagementSection: String
    val clearAllData: String
    val clearAllDataDesc: String
    val clearDataConfirmTitle: String
    val clearDataConfirmMessage: String
    val clearDataSuccess: String
    val aboutSection: String
    val developerCredit: String
    val developerName: String
    val appVersion: String
    val offlineSecurityBadge: String

    // Common Dialogs & Buttons
    val btnSave: String
    val btnCancel: String
    val btnDelete: String
    val btnConfirm: String
    val btnBack: String
    val moreOptions: String
    val deleteConfirmTitle: String
    val deleteConfirmMessage: String
    val deleteNoteConfirmMessage: String
    val deleteExpenseConfirmMessage: String
    val validationRequiredField: String
    val validationInvalidAmount: String

    // Universal Quick Input Component
    val quickEntryTitle: String
    val quickEntrySubtitle: String
    val entryCategoryLabel: String
    val categoryTask: String
    val categoryNote: String
    val categoryExpense: String
    val quickAddSuccess: String
}

object ArabicStrings : AppStrings {
    override val appName: String = "يومي"
    override val appTagline: String = "مفكرتك اليومية الذكية، لمهامك وملاحظاتك ومصاريفك"
    override val navHome: String = "الرئيسية"
    override val navTasks: String = "المهام"
    override val navNotes: String = "الملاحظات"
    override val navExpenses: String = "المصاريف"
    override val navCalendar: String = "التقويم"
    override val navSettings: String = "الإعدادات"

    override val greetingHello: String = "أهلاً بك"
    override val greetingSubtitle: String = "نتمنى لك يوماً مثمراً ومنظماً"
    override val todaySummaryTitle: String = "ملخص إنجازات اليوم"
    override val todayTasks: String = "مهام اليوم"
    override val tasksCompleted: String = "مكتملة"
    override val tasksRemaining: String = "متبقية"
    override val quickActionsTitle: String = "إجراءات سريعة"
    override val actionAddTask: String = "مهمة جديدة"
    override val actionAddNote: String = "ملاحظة"
    override val actionAddExpense: String = "مصروف"
    override val actionCalendar: String = "التقويم"
    override val upcomingRemindersTitle: String = "التذكيرات القادمة"
    override val noUpcomingReminders: String = "لا توجد تذكيرات مجدولة حالياً"
    override val todayPriorityTasks: String = "مهام اليوم الهامة"
    override val viewAll: String = "عرض الكل"

    override val tasksTitle: String = "إدارة المهام"
    override val filterAll: String = "الكل"
    override val filterToday: String = "اليوم"
    override val filterUpcoming: String = "القادمة"
    override val filterCompleted: String = "المكتملة"
    override val searchTasksPlaceholder: String = "ابحث في المهام..."
    override val noTasksFound: String = "لا توجد مهام في هذا التصنيف"
    override val noTasksSubtitle: String = "اضغط على زر الإضافة (+) لإنشاء أول مهمة لك"
    override val taskPriorityHigh: String = "مرتفعة"
    override val taskPriorityMedium: String = "متوسطة"
    override val taskPriorityLow: String = "منخفضة"
    override val categoryWork: String = "عمل"
    override val categoryPersonal: String = "شخصي"
    override val categoryStudy: String = "دراسة"
    override val categoryHealth: String = "صحة"
    override val categoryShopping: String = "تسوق"
    override val categoryOther: String = "أخرى"
    override val dialogAddTaskTitle: String = "إضافة مهمة جديدة"
    override val dialogEditTaskTitle: String = "تعديل المهمة"
    override val taskTitleLabel: String = "عنوان المهمة"
    override val taskTitlePlaceholder: String = "مثال: مراجعة التقرير الشهري"
    override val taskDescLabel: String = "تفاصيل أو ملاحظات"
    override val taskDescPlaceholder: String = "اكتب أي تفاصيل إضافية للمهمة..."
    override val dueDateLabel: String = "تاريخ الاستحقاق"
    override val selectDueDate: String = "تحديد التاريخ"
    override val dueTimeLabel: String = "وقت التذكير"
    override val selectDueTime: String = "تحديد الوقت"
    override val enableReminder: String = "تفعيل منبه التذكير"
    override val reminderScheduledSuccess: String = "تم ضبط التذكير بنجاح"
    override val reminderPastTimeError: String = "يرجى تحديد وقت في المستقبل"
    override val pinTask: String = "تثبيت في الأعلى"
    override val priorityLabel: String = "مستوى الأولوية"
    override val categoryLabel: String = "التصنيف"

    override val notesTitle: String = "دفتر الملاحظات"
    override val searchNotesPlaceholder: String = "ابحث في الملاحظات بالاسم أو المحتوى..."
    override val noNotesFound: String = "لا توجد ملاحظات حالياً"
    override val noNotesSubtitle: String = "سجّل أفكارك وخواطرك بالضغط على زر الإضافة"
    override val dialogAddNoteTitle: String = "ملاحظة جديدة"
    override val dialogEditNoteTitle: String = "تعديل الملاحظة"
    override val noteTitleLabel: String = "عنوان الملاحظة"
    override val noteTitlePlaceholder: String = "اكتب عنواناً للملاحظة..."
    override val noteContentLabel: String = "نص الملاحظة"
    override val noteContentPlaceholder: String = "ابدأ بكتابة أفكارك وملاحظاتك هنا..."
    override val pinNote: String = "تثبيت الملاحظة"
    override val noteColorLabel: String = "لون التمييز"
    override val pinnedNotesHeader: String = "الملاحظات المثبتة"
    override val otherNotesHeader: String = "جميع الملاحظات"
    override val attachImage: String = "إرفاق صورة"
    override val attachPdf: String = "إرفاق ملف PDF"
    override val recordAudio: String = "تسجيل صوت"
    override val stopRecording: String = "إيقاف التسجيل"
    override val recordingAudio: String = "جاري التسجيل..."
    override val deleteAudio: String = "حذف التسجيل"
    override val sharePdf: String = "مشاركة كملف PDF"
    override val shareText: String = "مشاركة كنص"
    override val noteReminderTitle: String = "تذكير للملاحظة"
    override val setNoteReminder: String = "ضبط منبه للملاحظة"
    override val cancelNoteReminder: String = "إلغاء التذكير"
    override val boldFormat: String = "غامق"
    override val italicFormat: String = "مائل"
    override val headingFormat: String = "عنوان"
    override val listFormat: String = "قائمة"
    override val checkboxFormat: String = "مهمة"
    override val quoteFormat: String = "اقتباس"
    override val noteBackgroundTheme: String = "خلفية الملاحظة"
    override val pdfExportSuccess: String = "تم إنشاء ومشاركة ملف PDF بنجاح"
    override val audioRecordPermissionNeeded: String = "يرجى منح إذن الميكروفون لتسجيل الصوت"

    override val filterAllNotes: String = "الكل"
    override val filterPinnedNotes: String = "المثبتة"
    override val filterVoiceNotes: String = "تسجيلات"
    override val filterAudioNotes: String = "ملفات صوتية"
    override val filterMediaNotes: String = "وسائط ومستندات"
    override val filterReminderNotes: String = "تذكيرات"
    override val noteWordCount: String = "كلمة"
    override val noteCharCount: String = "حرف"
    override val noteParagraphCount: String = "فقرة"
    override val noteReadingTime: String = "وقت القراءة"
    override val noteAutoSaved: String = "محفوظ تلقائياً"
    override val attachAudioFile: String = "إرفاق ملف صوتي"
    override val wordModeView: String = "معاينة منضدة"
    override val wordModeEdit: String = "تحرير النص"
    override val formatHeading1: String = "عنوان رئيسي"
    override val formatHeading2: String = "عنوان فرعي"
    override val formatHeading3: String = "عنوان صغير"
    override val formatUnderline: String = "تسطير"
    override val formatStrikethrough: String = "شطب"
    override val formatHighlight: String = "تمييز ماركر"
    override val formatAlignRight: String = "محاذاة لليمين"
    override val formatAlignCenter: String = "توسيط"
    override val formatAlignLeft: String = "محاذاة لليسار"
    override val formatClear: String = "مسح التنسيق"
    override val formatDivider: String = "فاصل أفقي"
    override val formatCallout: String = "مربع ملاحظة"
    override val copyAllContent: String = "نسخ النص كاملاً"
    override val contentCopiedToast: String = "تم نسخ النص إلى الحافظة بنجاح"

    override val expensesTitle: String = "المصاريف والميزانية"
    override val currencyUnit: String = "د.ع"
    override val basicSalaryTitle: String = "الراتب الأساسي"
    override val basicSalaryDesc: String = "الراتب الشهري الثابت"
    override val addedSalaryTitle: String = "الراتب المضاف / الإضافي"
    override val addedSalaryDesc: String = "مكافآت، ساعات إضافية أو دخل مساند"
    override val totalIncomeTitle: String = "إجمالي الدخل الشهري"
    override val remainingSalaryTitle: String = "المتبقي من الراتب"
    override val editBasicSalaryDialogTitle: String = "تحديد مقدار الراتب الأساسي"
    override val editAddedSalaryDialogTitle: String = "تحديد مقدار الراتب المضاف"
    override val salaryInputPlaceholder: String = "أدخل المبلغ بالدينار العراقي (مثال: 750000)"
    override val saveSalary: String = "حفظ المبلغ"
    override val salarySummaryTitle: String = "إدارة الراتب والميزانية الشهرية"
    override val salarySpentRatio: String = "نسبة المصروف"
    override val salarySafeStatus: String = "الوضع المالي ممتاز ومستقر ✓"
    override val salaryWarningStatus: String = "تنبيه: تجاوزت المصاريف الدخل المحدد!"
    override val todayTotalExpenses: String = "مصاريف اليوم"
    override val monthTotalExpenses: String = "إجمالي هذا الشهر"
    override val dialogAddExpenseTitle: String = "تسجيل مصروف جديد"
    override val dialogEditExpenseTitle: String = "تعديل المصروف"
    override val expenseTitleLabel: String = "بيان المصروف"
    override val expenseTitlePlaceholder: String = "مثال: وجبة غداء، فاتورة إنترنت"
    override val expenseAmountLabel: String = "المبلغ (بالدينار العراقي)"
    override val expenseAmountPlaceholder: String = "مثال: 15000"
    override val expenseCategoryLabel: String = "تصنيف المصروف"
    override val expenseDateLabel: String = "تاريخ العملية"
    override val expenseNoteLabel: String = "ملاحظة إضافية (اختياري)"
    override val noExpensesFound: String = "لا توجد مصاريف مسجلة"
    override val noExpensesSubtitle: String = "تتبع مصاريفك اليومية بالدينار العراقي بسهولة"
    override val expCategoryFood: String = "طعام ومطاعم"
    override val expCategoryTransport: String = "مواصلات ووقود"
    override val expCategoryBills: String = "فواتير وخدمات"
    override val expCategoryShopping: String = "تسوق واحتياجات"
    override val expCategoryHealth: String = "صحة وأدوية"
    override val expCategoryEntertainment: String = "ترفيه"
    override val expCategoryOther: String = "أخرى"

    override val commitmentsAndBillsTitle: String = "فواتير والتزامات قادمة"
    override val commitmentsSectionSubtitle: String = "ترتيب وتصفية الفواتير بضغطة واحدة عبر زر الفلتر الموحد"
    override val filterAndSortBills: String = "فلتر وترتيب الفواتير"
    override val sortClosestDue: String = "الترتيب: الأقرب استحقاقاً"
    override val sortHighestAmount: String = "الترتيب: الأكبر مبلغاً"
    override val showAllBills: String = "العرض: الكل"
    override val showUnpaidBills: String = "العرض: غير المسددة"
    override val btnPayBill: String = "تسديد"
    override val btnEditBill: String = "تعديل"
    override val btnAddBill: String = "إضافة التزام / فاتورة"
    override val reservedInBudget: String = "محجوزة"
    override val notReserved: String = "غير محجوزة"
    override val actualCurrentBalance: String = "الرصيد الفعلي الحالي (أموالك في الجيب والحساب)"
    override val availableToSpend: String = "المتاح للصرف"
    override val afterReservedCommitments: String = "بعد حجز الالتزامات"
    override val reservedCommitments: String = "التزامات محجوزة"
    override val unpaidDues: String = "مستحقات غير مدفوعة"
    override val previousMonthBalanceTitle: String = "المتبقي من الشهر السابق"
    override val manualEntryCarryover: String = "إدخال يدوي للرصيد المرحّل عند بدء الدورة"
    override val daysRemainingForSalary: String = "متبقي للراتب القادم"
    override val dailyAverageExpense: String = "متوسط الصرف اليومي"
    override val quickActions: String = "إجراءات سريعة"
    override val btnLogExpense: String = "مصروف"
    override val btnAddIncome: String = "إضافة مبلغ جديد"
    override val tabOverview: String = "الرئيسية"
    override val tabCommitments: String = "الالتزامات"
    override val tabHistory: String = "السجل"
    override val tabDailyExpenses: String = "المصروفات اليومية"
    override val tabAddedAmounts: String = "المبالغ المضافة"
    override val searchExpensesPlaceholder: String = "بحث في المصروفات أو الفئات..."
    override val latestTransactions: String = "آخر المعاملات المسجلة"
    override val financialAccountingDetails: String = "تفصيل الحساب المالي للدورة الحالية"
    override val financialAccountingSubtitle: String = "بيان العمليات الحسابية لميزانية الدورة الشهرية"
    override val setSalaryReceptionDay: String = "تحديد يوم استلام الراتب"

    override val calendarTitle: String = "التقويم وجدول الأيام"
    override val calendarTasksOnDay: String = "مهام هذا اليوم"
    override val noTasksForSelectedDay: String = "لا توجد مهام أو تذكيرات في هذا اليوم"
    override val addTaskForThisDay: String = "إضافة مهمة لهذا اليوم"

    override val settingsTitle: String = "الإعدادات"
    override val languageSection: String = "اللغة والاتجاه"
    override val langArabic: String = "العربية (RTL)"
    override val langEnglish: String = "English (LTR)"
    override val appearanceSection: String = "المظهر والتصميم"
    override val themeMode: String = "خلفية التطبيق والسمة"
    override val themeSystem: String = "تلقائي حسب النظام"
    override val themeDark: String = "الوضع الداكن (كحلي)"
    override val themeLight: String = "الوضع الفاتح"
    override val themeWhite: String = "خلفية بيضاء ناصعة"
    override val themeBlack: String = "خلفية سوداء (AMOLED)"
    override val themeNatural: String = "خلفية طبيعية هادئة"
    override val themeTransparent: String = "خلفية شفافة وزجاجية"
    override val noteColorWhite: String = "أبيض"
    override val noteColorBlack: String = "أسود"
    override val noteColorTransparent: String = "شفاف"
    override val noteColorNatural: String = "طبيعي أخضر"
    override val noteColorNaturalEarth: String = "طبيعي ترابي"
    override val fontSizeSection: String = "حجم الخط وتقليل التراكم"
    override val fontSizeLabel: String = "اختر حجم الخط لتناسب شاشة جهازك وتجنب تداخل النصوص"
    override val fontSizeExtraSmall: String = "صغير جداً (0.72x)"
    override val fontSizeSmall: String = "صغير ومضغوط (0.78x)"
    override val fontSizeMedium: String = "متوسط (0.86x)"
    override val fontSizeNormal: String = "عادي (1.0x)"
    override val notificationsSection: String = "التنبيهات والرنين وصلاحيات النظام"
    override val enableRemindersToggle: String = "تفعيل إشعارات ورنين التذكير"
    override val vibrateToggle: String = "الاهتزاز عند الرنين والتنبيه"
    override val stopAlarm: String = "إيقاف التنبيه والرنين"
    override val snoozeAlarm: String = "غفوة (5 دقائق)"
    override val alarmRingingTitle: String = "⏰ تنبيه تذكير نشط"
    override val batteryOptimizationTitle: String = "أذونات التنبيه الإجباري وتحسين البطارية"
    override val batteryOptimizationDesc: String = "لضمان عمل الرنين وصوت التنبيه الإجباري في وقته المحدد بدقة حتى أثناء إغلاق الشاشة أو تفعيل وضع توفير الطاقة بالهاتف."
    override val btnIgnoreBattery: String = "إلغاء قيود البطارية (تشغيل دائم)"
    override val btnExactAlarm: String = "منح إذن التنبيهات الدقيقة"
    override val btnNotificationPerm: String = "منح إذن الإشعارات"
    override val testAlarmBtn: String = "🔔 تجربة صوت الرنين والتنبيه الآن"
    override val testAlarmTriggered: String = "جاري إطلاق رنين تجريبي لاختبار الصوت والشاشة!"
    override val permissionGranted: String = "مفعل ومسموح به ✓"
    override val permissionNeeded: String = "مطلوب التفعيل ⚠️"
    override val alarmBannerWarning: String = "تنبيه: يلزم منح أذونات البطارية والتنبيه الدقيق لضمان سماع الرنين في الموعد بالضبط."
    override val dataManagementSection: String = "إدارة البيانات"
    override val clearAllData: String = "مسح جميع بيانات التطبيق"
    override val clearAllDataDesc: String = "حذف كافة المهام، الملاحظات والمصاريف نهائياً"
    override val clearDataConfirmTitle: String = "تأكيد مسح البيانات"
    override val clearDataConfirmMessage: String = "هل أنت متأكد من رغبتك في حذف كافة المهام والملاحظات والمصاريف؟ لا يمكن التراجع عن هذا الإجراء."
    override val clearDataSuccess: String = "تم مسح كافة البيانات بنجاح"
    override val aboutSection: String = "حول التطبيق"
    override val developerCredit: String = "تصميم وتطوير:"
    override val developerName: String = "م. محمد شهيد العاجيبي\n(Eng. Mohammed Shaheed Al-A'ajibi)"
    override val appVersion: String = "الإصدار 2.10.0 (نسخة أصلية تعمل بدون إنترنت)"
    override val offlineSecurityBadge: String = "التطبيق يعمل محلياً 100% دون خوادم لضمان خصوصيتك الكاملة"

    override val btnSave: String = "حفظ"
    override val btnCancel: String = "إلغاء"
    override val btnDelete: String = "حذف"
    override val btnConfirm: String = "تأكيد"
    override val btnBack: String = "رجوع"
    override val moreOptions: String = "خيارات إضافية"
    override val deleteConfirmTitle: String = "تأكيد الحذف"
    override val deleteConfirmMessage: String = "هل أنت متأكد من حذف هذه المهمة؟"
    override val deleteNoteConfirmMessage: String = "هل أنت متأكد من حذف هذه الملاحظة؟"
    override val deleteExpenseConfirmMessage: String = "هل أنت متأكد من حذف هذا المصروف؟"
    override val validationRequiredField: String = "هذا الحقل مطلوب"
    override val validationInvalidAmount: String = "يرجى إدخال مبلغ صحيح"

    override val quickEntryTitle: String = "إدخال سريع موحد"
    override val quickEntrySubtitle: String = "سجّل مهمة أو ملاحظة أو مصروفاً بلمسة واحدة"
    override val entryCategoryLabel: String = "نوع الإدخال"
    override val categoryTask: String = "مهمة"
    override val categoryNote: String = "ملاحظة"
    override val categoryExpense: String = "مصروف"
    override val quickAddSuccess: String = "تم الحفظ بنجاح"
}

object EnglishStrings : AppStrings {
    override val appName: String = "Yawmi"
    override val appTagline: String = "Your smart daily organizer for tasks, notes & expenses"
    override val navHome: String = "Home"
    override val navTasks: String = "Tasks"
    override val navNotes: String = "Notes"
    override val navExpenses: String = "Expenses"
    override val navCalendar: String = "Calendar"
    override val navSettings: String = "Settings"

    override val greetingHello: String = "Welcome back"
    override val greetingSubtitle: String = "Have a productive and organized day"
    override val todaySummaryTitle: String = "Today's Overview"
    override val todayTasks: String = "Today's Tasks"
    override val tasksCompleted: String = "Completed"
    override val tasksRemaining: String = "Remaining"
    override val quickActionsTitle: String = "Quick Actions"
    override val actionAddTask: String = "New Task"
    override val actionAddNote: String = "New Note"
    override val actionAddExpense: String = "Expense"
    override val actionCalendar: String = "Calendar"
    override val upcomingRemindersTitle: String = "Upcoming Reminders"
    override val noUpcomingReminders: String = "No reminders scheduled right now"
    override val todayPriorityTasks: String = "Important Today"
    override val viewAll: String = "View all"

    override val tasksTitle: String = "Task Manager"
    override val filterAll: String = "All"
    override val filterToday: String = "Today"
    override val filterUpcoming: String = "Upcoming"
    override val filterCompleted: String = "Done"
    override val searchTasksPlaceholder: String = "Search tasks..."
    override val noTasksFound: String = "No tasks found in this view"
    override val noTasksSubtitle: String = "Tap (+) to create your first task"
    override val taskPriorityHigh: String = "High"
    override val taskPriorityMedium: String = "Medium"
    override val taskPriorityLow: String = "Low"
    override val categoryWork: String = "Work"
    override val categoryPersonal: String = "Personal"
    override val categoryStudy: String = "Study"
    override val categoryHealth: String = "Health"
    override val categoryShopping: String = "Shopping"
    override val categoryOther: String = "Other"
    override val dialogAddTaskTitle: String = "Create New Task"
    override val dialogEditTaskTitle: String = "Edit Task"
    override val taskTitleLabel: String = "Task Title"
    override val taskTitlePlaceholder: String = "e.g., Review monthly report"
    override val taskDescLabel: String = "Details & Notes"
    override val taskDescPlaceholder: String = "Add any extra details here..."
    override val dueDateLabel: String = "Due Date"
    override val selectDueDate: String = "Select Date"
    override val dueTimeLabel: String = "Reminder Time"
    override val selectDueTime: String = "Select Time"
    override val enableReminder: String = "Enable Notification Alarm"
    override val reminderScheduledSuccess: String = "Reminder scheduled successfully"
    override val reminderPastTimeError: String = "Please select a future time"
    override val pinTask: String = "Pin to top"
    override val priorityLabel: String = "Priority Level"
    override val categoryLabel: String = "Category"

    override val notesTitle: String = "My Notes"
    override val searchNotesPlaceholder: String = "Search notes by title or content..."
    override val noNotesFound: String = "No notes found"
    override val noNotesSubtitle: String = "Capture ideas and thoughts by tapping (+)"
    override val dialogAddNoteTitle: String = "New Note"
    override val dialogEditNoteTitle: String = "Edit Note"
    override val noteTitleLabel: String = "Note Title"
    override val noteTitlePlaceholder: String = "Enter note title..."
    override val noteContentLabel: String = "Note Body"
    override val noteContentPlaceholder: String = "Type your thoughts and notes here..."
    override val pinNote: String = "Pin Note"
    override val noteColorLabel: String = "Color Accent"
    override val pinnedNotesHeader: String = "Pinned Notes"
    override val otherNotesHeader: String = "All Notes"
    override val attachImage: String = "Attach Image"
    override val attachPdf: String = "Attach PDF"
    override val recordAudio: String = "Record Audio"
    override val stopRecording: String = "Stop Recording"
    override val recordingAudio: String = "Recording audio..."
    override val deleteAudio: String = "Delete Recording"
    override val sharePdf: String = "Share as PDF"
    override val shareText: String = "Share as Text"
    override val noteReminderTitle: String = "Note Reminder"
    override val setNoteReminder: String = "Set Note Reminder"
    override val cancelNoteReminder: String = "Cancel Reminder"
    override val boldFormat: String = "Bold"
    override val italicFormat: String = "Italic"
    override val headingFormat: String = "Heading"
    override val listFormat: String = "List"
    override val checkboxFormat: String = "Task"
    override val quoteFormat: String = "Quote"
    override val noteBackgroundTheme: String = "Note Color Theme"
    override val pdfExportSuccess: String = "PDF created successfully"
    override val audioRecordPermissionNeeded: String = "Microphone permission is required to record audio"

    override val filterAllNotes: String = "All"
    override val filterPinnedNotes: String = "Pinned"
    override val filterVoiceNotes: String = "Voice"
    override val filterAudioNotes: String = "Audio Files"
    override val filterMediaNotes: String = "Media & Docs"
    override val filterReminderNotes: String = "Reminders"
    override val noteWordCount: String = "words"
    override val noteCharCount: String = "chars"
    override val noteParagraphCount: String = "paragraphs"
    override val noteReadingTime: String = "Reading time"
    override val noteAutoSaved: String = "Auto-saved"
    override val attachAudioFile: String = "Attach Audio File"
    override val wordModeView: String = "Typeset View"
    override val wordModeEdit: String = "Edit Mode"
    override val formatHeading1: String = "Heading 1"
    override val formatHeading2: String = "Heading 2"
    override val formatHeading3: String = "Heading 3"
    override val formatUnderline: String = "Underline"
    override val formatStrikethrough: String = "Strikethrough"
    override val formatHighlight: String = "Marker Highlight"
    override val formatAlignRight: String = "Align Right"
    override val formatAlignCenter: String = "Center"
    override val formatAlignLeft: String = "Align Left"
    override val formatClear: String = "Clear Formatting"
    override val formatDivider: String = "Divider"
    override val formatCallout: String = "Callout Box"
    override val copyAllContent: String = "Copy All Text"
    override val contentCopiedToast: String = "Note copied to clipboard"

    override val expensesTitle: String = "Expenses & Budget"
    override val currencyUnit: String = "IQD"
    override val basicSalaryTitle: String = "Basic Salary"
    override val basicSalaryDesc: String = "Fixed monthly base salary"
    override val addedSalaryTitle: String = "Added / Bonus Salary"
    override val addedSalaryDesc: String = "Bonuses, overtime, extra earnings"
    override val totalIncomeTitle: String = "Total Monthly Income"
    override val remainingSalaryTitle: String = "Remaining Salary"
    override val editBasicSalaryDialogTitle: String = "Set Basic Salary"
    override val editAddedSalaryDialogTitle: String = "Set Added Salary"
    override val salaryInputPlaceholder: String = "Enter amount in IQD (e.g. 750000)"
    override val saveSalary: String = "Save Amount"
    override val salarySummaryTitle: String = "Salary & Monthly Budget"
    override val salarySpentRatio: String = "Spent Ratio"
    override val salarySafeStatus: String = "Financial status healthy ✓"
    override val salaryWarningStatus: String = "Warning: Expenses exceed income!"
    override val todayTotalExpenses: String = "Today's Total"
    override val monthTotalExpenses: String = "This Month's Total"
    override val dialogAddExpenseTitle: String = "Add New Expense"
    override val dialogEditExpenseTitle: String = "Edit Expense"
    override val expenseTitleLabel: String = "Expense Description"
    override val expenseTitlePlaceholder: String = "e.g., Lunch, Internet bill"
    override val expenseAmountLabel: String = "Amount (Iraqi Dinar)"
    override val expenseAmountPlaceholder: String = "e.g., 15000"
    override val expenseCategoryLabel: String = "Category"
    override val expenseDateLabel: String = "Date"
    override val expenseNoteLabel: String = "Extra Note (optional)"
    override val noExpensesFound: String = "No expenses recorded"
    override val noExpensesSubtitle: String = "Track your daily spend in IQD effortlessly"
    override val expCategoryFood: String = "Food & Dining"
    override val expCategoryTransport: String = "Transit & Fuel"
    override val expCategoryBills: String = "Bills & Utilities"
    override val expCategoryShopping: String = "Shopping"
    override val expCategoryHealth: String = "Health & Pharmacy"
    override val expCategoryEntertainment: String = "Entertainment"
    override val expCategoryOther: String = "Other"

    override val commitmentsAndBillsTitle: String = "Upcoming Bills & Commitments"
    override val commitmentsSectionSubtitle: String = "Filter and manage all bills and commitments in one place"
    override val filterAndSortBills: String = "Filter & Sort Bills"
    override val sortClosestDue: String = "Sort: Closest Due Date"
    override val sortHighestAmount: String = "Sort: Highest Amount"
    override val showAllBills: String = "View: All"
    override val showUnpaidBills: String = "View: Unpaid Only"
    override val btnPayBill: String = "Pay"
    override val btnEditBill: String = "Edit"
    override val btnAddBill: String = "Add Commitment / Bill"
    override val reservedInBudget: String = "Reserved"
    override val notReserved: String = "Not Reserved"
    override val actualCurrentBalance: String = "Current Actual Balance (In Pocket & Account)"
    override val availableToSpend: String = "Available to Spend"
    override val afterReservedCommitments: String = "After reserving commitments"
    override val reservedCommitments: String = "Reserved Commitments"
    override val unpaidDues: String = "Unpaid dues"
    override val previousMonthBalanceTitle: String = "Remaining from Previous Month"
    override val manualEntryCarryover: String = "Manual entry for cycle carryover balance"
    override val daysRemainingForSalary: String = "Days until next salary"
    override val dailyAverageExpense: String = "Daily average spend"
    override val quickActions: String = "Quick Actions"
    override val btnLogExpense: String = "Expense"
    override val btnAddIncome: String = "Add Income / Bonus"
    override val tabOverview: String = "Overview"
    override val tabCommitments: String = "Commitments"
    override val tabHistory: String = "History"
    override val tabDailyExpenses: String = "Daily Expenses"
    override val tabAddedAmounts: String = "Added Income"
    override val searchExpensesPlaceholder: String = "Search expenses or categories..."
    override val latestTransactions: String = "Recent Transactions"
    override val financialAccountingDetails: String = "Current Cycle Financial Breakdown"
    override val financialAccountingSubtitle: String = "Detailed accounting of cycle income and expenses"
    override val setSalaryReceptionDay: String = "Set Salary Reception Day"

    override val calendarTitle: String = "Calendar Schedule"
    override val calendarTasksOnDay: String = "Tasks for this day"
    override val noTasksForSelectedDay: String = "No tasks or reminders on this date"
    override val addTaskForThisDay: String = "Add task for this day"

    override val settingsTitle: String = "Settings"
    override val languageSection: String = "Language & Direction"
    override val langArabic: String = "العربية (Arabic RTL)"
    override val langEnglish: String = "English (LTR)"
    override val appearanceSection: String = "Appearance & Theme"
    override val themeMode: String = "App Background & Theme"
    override val themeSystem: String = "System Default"
    override val themeDark: String = "Dark Navy"
    override val themeLight: String = "Light Mode"
    override val themeWhite: String = "Pure White"
    override val themeBlack: String = "AMOLED Black"
    override val themeNatural: String = "Natural Nature"
    override val themeTransparent: String = "Transparent Glass"
    override val noteColorWhite: String = "White"
    override val noteColorBlack: String = "Black"
    override val noteColorTransparent: String = "Transparent"
    override val noteColorNatural: String = "Natural Green"
    override val noteColorNaturalEarth: String = "Natural Earth"
    override val fontSizeSection: String = "Font Size & Layout Scale"
    override val fontSizeLabel: String = "Select text scale to prevent overlapping on your screen"
    override val fontSizeExtraSmall: String = "Extra Small (0.72x)"
    override val fontSizeSmall: String = "Compact (0.78x)"
    override val fontSizeMedium: String = "Medium (0.86x)"
    override val fontSizeNormal: String = "Normal (1.0x)"
    override val notificationsSection: String = "Notifications, Alarms & Battery Permissions"
    override val enableRemindersToggle: String = "Enable Reminder Ringing & Alerts"
    override val vibrateToggle: String = "Vibrate on Alarm"
    override val stopAlarm: String = "Stop Alarm Ringing"
    override val snoozeAlarm: String = "Snooze (5 min)"
    override val alarmRingingTitle: String = "⏰ Active Reminder Alarm"
    override val batteryOptimizationTitle: String = "Mandatory Alarm & Battery Permissions"
    override val batteryOptimizationDesc: String = "Ensures mandatory alarm sound and ringing fires accurately on time even when screen is locked or in battery saver mode."
    override val btnIgnoreBattery: String = "Disable Battery Restrictions (Run Always)"
    override val btnExactAlarm: String = "Grant Exact Alarm Permission"
    override val btnNotificationPerm: String = "Grant Notification Permission"
    override val testAlarmBtn: String = "🔔 Test Alarm Ringing Now"
    override val testAlarmTriggered: String = "Test alarm triggered to check audio and screen!"
    override val permissionGranted: String = "Granted & Active ✓"
    override val permissionNeeded: String = "Permission Needed ⚠️"
    override val alarmBannerWarning: String = "Notice: Battery exemption and exact alarm permissions are recommended to guarantee timely ringing."
    override val dataManagementSection: String = "Data Management"
    override val clearAllData: String = "Clear All App Data"
    override val clearAllDataDesc: String = "Permanently remove all tasks, notes and expenses"
    override val clearDataConfirmTitle: String = "Confirm Data Wipe"
    override val clearDataConfirmMessage: String = "Are you sure you want to delete all tasks, notes, and expenses? This action cannot be undone."
    override val clearDataSuccess: String = "All data cleared successfully"
    override val aboutSection: String = "About App"
    override val developerCredit: String = "Designed and developed by:"
    override val developerName: String = "Eng. Mohammed Shaheed Al-A'ajibi\n(م. محمد شهيد العاجيبي)"
    override val appVersion: String = "Version 2.10.0 (Offline Native Edition)"
    override val offlineSecurityBadge: String = "100% Offline & Private on device without external servers"

    override val btnSave: String = "Save"
    override val btnCancel: String = "Cancel"
    override val btnDelete: String = "Delete"
    override val btnConfirm: String = "Confirm"
    override val btnBack: String = "Back"
    override val moreOptions: String = "More Options"
    override val deleteConfirmTitle: String = "Confirm Delete"
    override val deleteConfirmMessage: String = "Are you sure you want to delete this task?"
    override val deleteNoteConfirmMessage: String = "Are you sure you want to delete this note?"
    override val deleteExpenseConfirmMessage: String = "Are you sure you want to delete this expense?"
    override val validationRequiredField: String = "This field is required"
    override val validationInvalidAmount: String = "Please enter a valid amount"

    override val quickEntryTitle: String = "Universal Quick Entry"
    override val quickEntrySubtitle: String = "Add a task, note, or expense in one touch"
    override val entryCategoryLabel: String = "Entry Category"
    override val categoryTask: String = "Task"
    override val categoryNote: String = "Note"
    override val categoryExpense: String = "Expense"
    override val quickAddSuccess: String = "Saved successfully"
}

val LocalAppStrings = compositionLocalOf<AppStrings> { ArabicStrings }
val LocalAppLayoutDirection = compositionLocalOf { LayoutDirection.Rtl }
