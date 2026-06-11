#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
إضافة حقل directPdf إلى app_assets_map.json (الإصدار 2.1 - مُصحَّح)
يستخرج اسم المجلد الحقيقي من مسار file الأصلي.
"""

import json
import os
import re
import sys
from datetime import datetime

# ========== الإعدادات ==========
INPUT_FILE = "/storage/4403-B0CA/Projects/app_assets_map.json"
OUTPUT_FILE = "/storage/4403-B0CA/Projects/app_assets_map_v2.json"
# ==============================

def extract_real_folder(file_path):
    """
    استخراج اسم المجلد الجذري الحقيقي من مسار file الأصلي.
    مثلاً: "06-مواد عامة\\مادة\\العملي\\ملف.pdf" → "06-مواد عامة"
           "06-الأجهزة المتقدمة\\الجهاز\\المادة\\النظري\\ملف.pdf" → "06-الأجهزة المتقدمة"
    """
    if not file_path:
        return None
    # تقسيم المسار إلى أجزاء
    parts = file_path.replace("\\", "/").split("/")
    if parts:
        return parts[0]  # الجزء الأول هو المجلد الجذري
    return None

def extract_device_name(file_path):
    """
    استخراج اسم الجهاز من مسار file لمواد subject.
    مثلاً: "06-الأجهزة المتقدمة\\الجهاز الدموي\\المادة\\النظري\\ملف.pdf" → "الجهاز الدموي"
    """
    if not file_path:
        return "أخرى"
    parts = file_path.replace("\\", "/").split("/")
    if len(parts) >= 2:
        return parts[1]  # الجزء الثاني هو اسم الجهاز
    return "أخرى"

def extract_subject_name_from_path(file_path):
    """
    استخراج اسم المادة من مسار file لمواد subject.
    مثلاً: "06-الأجهزة المتقدمة\\الجهاز\\التخدير\\النظري\\ملف.pdf" → "التخدير"
    """
    if not file_path:
        return ""
    parts = file_path.replace("\\", "/").split("/")
    if len(parts) >= 3:
        return parts[2]  # الجزء الثالث هو اسم المادة
    return ""

def extract_general_subject_name(file_path):
    """
    استخراج اسم المادة العامة من مسار file.
    مثلاً: "06-مواد عامة\\مبادى التغذية\\العملي\\ملف.pdf" → "مبادى التغذية"
    """
    if not file_path:
        return ""
    parts = file_path.replace("\\", "/").split("/")
    if len(parts) >= 2:
        return parts[1]  # الجزء الثاني هو اسم المادة
    return ""

def generate_direct_pdf(book):
    """توليد مسار PDF مباشر لمادة subject أو general."""
    book_type = book.get("type", "")
    file_path = book.get("file", "")
    
    if book_type not in ("subject", "general") or not file_path:
        return None
    
    if book_type == "subject":
        # استخراج المعلومات من المسار الأصلي
        root_folder = extract_real_folder(file_path)      # مثلاً "06-الأجهزة المتقدمة"
        device = extract_device_name(file_path)           # مثلاً "الجهاز الدموي واللمفاوي"
        subject = extract_subject_name_from_path(file_path)  # مثلاً "التخدير أثناء الحروب"
        
        if not root_folder or not device or not subject:
            return None
        
        # بناء المسار المباشر: root_folder/الجهاز/المادة/المادة.pdf
        direct_path = f"{root_folder}/{device}/{subject}/{subject}.pdf"
    
    elif book_type == "general":
        # استخراج المعلومات من المسار الأصلي
        root_folder = extract_real_folder(file_path)      # مثلاً "06-مواد عامة"
        subject = extract_general_subject_name(file_path) # مثلاً "مبادى التغذية العلاجية عام"
        
        if not root_folder or not subject:
            return None
        
        # بناء المسار المباشر: root_folder/المادة/المادة.pdf
        direct_path = f"{root_folder}/{subject}/{subject}.pdf"
    
    # استبدال \\ بـ /
    direct_path = direct_path.replace("\\", "/")
    return direct_path

def main():
    print("=" * 60)
    print("🔄 إضافة حقل directPdf إلى app_assets_map.json (v2.1 - مُصحَّح)")
    print("=" * 60)
    
    if not os.path.exists(INPUT_FILE):
        print(f"❌ الملف غير موجود: {INPUT_FILE}")
        sys.exit(1)
    
    print(f"📄 قراءة الملف: {INPUT_FILE}")
    with open(INPUT_FILE, 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    books = data.get("books", [])
    print(f"📚 عدد العناصر الكلي: {len(books)}")
    
    updated_count = 0
    skipped_count = 0
    sample_outputs = []  # عينات للتأكيد
    
    for book in books:
        book_type = book.get("type", "")
        if book_type in ("subject", "general"):
            direct_path = generate_direct_pdf(book)
            if direct_path:
                book["directPdf"] = direct_path
                updated_count += 1
                if len(sample_outputs) < 5:  # حفظ أول 5 عينات
                    sample_outputs.append({
                        "title": book.get("title", ""),
                        "type": book_type,
                        "directPdf": direct_path
                    })
            else:
                skipped_count += 1
    
    # تحديث metadata
    data["metadata"] = data.get("metadata", {})
    data["metadata"]["last_updated"] = datetime.now().isoformat()
    data["metadata"]["directPdf_added"] = True
    data["metadata"]["total_updated"] = updated_count
    data["metadata"]["version"] = "2.1"
    
    # حفظ الملف الجديد
    with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)
    
    print(f"✅ تم تحديث {updated_count} عنصراً (تم تخطي {skipped_count})")
    print(f"💾 الملف الناتج: {OUTPUT_FILE}")
    
    # عرض عينات للتأكيد
    if sample_outputs:
        print("\n📋 عينات من النتائج:")
        print("-" * 60)
        for s in sample_outputs:
            print(f"  📗 [{s['type']}] {s['title'][:60]}")
            print(f"     → {s['directPdf']}")
            print()
    
    print("📋 الخطوة التالية:")
    print(f"   انسخ الملف الناتج إلى:")
    print(f"   app/src/main/assets/data/app_assets_map.json")

if __name__ == "__main__":
    main()
