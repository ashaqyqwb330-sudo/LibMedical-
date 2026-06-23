#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
🏆 Multi-Module Medical Library Assets Generator (V3.0 - Tactical Elite)
Created for: "MID App - Military First-Aid & Field Medicine Academy"
Author: AI Academy Architect

This script acts as a content generation engine. It processes "app_assets_map.json",
creates the "MidApp_Library" structure, and constructs high-fidelity, production-grade
books (5-7 page PDFs), professional PNG covers with vector emblems, and interactive HTML
lectures (with SVG animated heartbeats, triage checkers, and self-scoring quiz engines).
"""

import os
import sys
import json
import random
import subprocess
import urllib.request
from datetime import datetime

# ==========================================
# 📦 Automation & Dependency Management
# ==========================================

def install_and_import(package_name, import_name=None):
    if import_name is None:
        import_name = package_name
    try:
        __import__(import_name)
    except ImportError:
        print(f"📦 Active dependency missing. Installing '{package_name}' dynamically...")
        try:
            # Upgrade pip and install
            subprocess.check_call([sys.executable, "-m", "pip", "install", package_name])
            print(f"✅ Successfully installed {package_name}.")
        except Exception as e:
            print(f"⚠️ pip install failed for {package_name}: {e}. Retrying with --user flag...")
            try:
                subprocess.check_call([sys.executable, "-m", "pip", "install", "--user", package_name])
                print(f"✅ Successfully installed {package_name} in user space.")
            except Exception as e2:
                print(f"🚨 Failed to install dependency: {e2}")
                print(f"🚨 Please run: pip install {package_name}")
                sys.exit(1)

# Ensure essential libraries are present before running the generator logic
install_and_import("fpdf2", "fpdf")
install_and_import("Pillow", "PIL")
install_and_import("arabic-reshaper", "arabic_reshaper")
install_and_import("python-bidi", "bidi")

from fpdf import FPDF
from PIL import Image, ImageDraw, ImageFont
import arabic_reshaper
from bidi.algorithm import get_display

import re

# تعيين استبدال الإيموجي
EMOJI_MAP = {
    "🗂️": "[TOC]", "📖": "[BOOK]", "⚠️": "[WARN]", "🔬": "[MICRO]",
    "⚙️": "[GEAR]", "📊": "[CHART]", "🩺": "[MED]", "🛠️": "[TOOL]",
    "🔄": "[LOOP]", "👨‍⚕️": "[DR]", "🎖️": "[MEDAL]", "🔥": "[FIRE]",
    "🎥": "[VIDEO]", "📈": "[GRAPH]", "📝": "[QUIZ]", "🏁": "[FLAG]",
    "🗺️": "[MAP]"
}
EMOJI_PATTERN = re.compile(
    "["
    "\U0001F600-\U0001F64F"  # emoticons
    "\U0001F300-\U0001F5FF"  # symbols & pictographs
    "\U0001F680-\U0001F6FF"  # transport & map
    "\U0001F1E0-\U0001F1FF"  # flags
    "\U00002500-\U000027BF"  # dingbats
    "\U000026A0-\U000026FF"
    "\U00002702-\U000027B0"
    "\U0001F900-\U0001F9FF"  # supplemental symbols
    "\U0001FA00-\U0001FA6F"  # chess symbols
    "\U0001FA70-\U0001FAFF"  # symbols extended-A
    "]+", flags=re.UNICODE)

# ==========================================
# ⚙️ Constants, Paths & System Verification
# ==========================================

FONT_URL = "https://github.com/google/fonts/raw/main/ofl/amiri/Amiri-Regular.ttf"
FONT_PATH = "Amiri-Regular.ttf"

INPUT_MAP_PATH = "app/src/main/assets/data/app_assets_map.json"
OUTPUT_DIR = "MidApp_Library"
BOOKS_DIR = os.path.join(OUTPUT_DIR, "books")
COVERS_DIR = os.path.join(OUTPUT_DIR, "covers")
LECTURES_DIR = os.path.join(OUTPUT_DIR, "lectures")

# List of rich YouTube educational medical/trauma streams
YOUTUBE_VIDEOS = [
    "https://www.youtube.com/embed/5D_1EieD8kM", # Tactical Combat Casualty Care (TCCC)
    "https://www.youtube.com/embed/6iY5nL6V23U", # Battlefield Trauma Medicine
    "https://www.youtube.com/embed/7872Yy7-jO8", # Human Anatomy Systems
    "https://www.youtube.com/embed/437zKOnuX0A", # Basic First Aid & Rescue
    "https://www.youtube.com/embed/uSgYv9g9x_Y", # Advanced CPR Procedures
    "https://www.youtube.com/embed/v9XpIDV-75M", # Suture Skills & Soft Tissue Trauma
    "https://www.youtube.com/embed/gD98c9yD1Gg", # Internal Bleeding Controls
    "https://www.youtube.com/embed/YpXAtQd-r90"  # Emergency Infection Controls
]

# ==========================================
# 🩺 Specialized Clinical Topic Corpora (Arabic)
# ==========================================

COURSES_DATA = {
    "التشريح": {
        "video": "https://www.youtube.com/embed/7872Yy7-jO8",
        "intro": "علم التشريح البشري هو الحجر الزاوية لكل الأنشطة العلاجية والتدخلات الجراحية تحت ظروف الضغط الميداني العالي.",
    "objectives": ["فهم المبادئ الأساسية", "تطبيق البروتوكولات الميدانية", "تحليل الحالات السريرية"],
        "ch1_title": "الفسيولوجيا البنيوية للأعضاء الحيوية",
        "ch1_txt": "يركز هذا الفصل على التوزيع الشرياني والوريدي في الأطراف وأماكن الضغط الرئيسية لإنقاذ الجرحى من الصدمة الوعائية النزفية الصاعقة بموقع الرعاية.",
        "ch2_title": "الجهاز التنفسي والمجاري الهوائية التكتيكية",
        "ch2_txt": "دراسة تفصيلية لهيكل الحنجرة والقصبة الهوائية لتنفيذ شق غشاء الحلق وصدر الدرق الإسعافي بنجاح عند انسداد المجاري الهوائية بالكامل.",
        "ch3_title": "تقييم العظام والمفاصل عند الصدمة البالستية",
        "ch3_txt": "التشريح الموضعي الدقيق للأطراف لحماية الأعصاب والأوعية الدموية المجاورة أثناء تثبيت الكسور المعقدة الناتجة عن التفجيرات.",
        "case_study": "حالة إصابة بشظية في الفخذ الأيسر مع تهتك كامل في الشريان الفخذي، تمكن الفرد الطبي من تطبيق التقفي التشريحي والضغط المباشر لوقف النزيف في أقل من 40 ثانية.",
        "table_headers": ["الهيكل العضوي", "الخطر الشرياني الرئيسي", "سرعة النزيف الفقداني"],
        "table_rows": [
            ["المنطقة الفخذية", "الشريان الفخذي العميق", "حرج جداً (فقدان تام في دقيقتين)"],
            ["المنطقة الصدرية", "الشريان الوربي والقلب", "فوري مميت"],
            ["الرقبة والعنق", "الشريان السباتي الأصلي", "صاعق (ثوانٍ معدودة)"],
            ["الساعد واليد", "الشريان الكعبري والزندي", "متوسط (يمكن تدبيره بالضغط العاصب)"]
        ],
        "quiz": [
            {"q": "ما هو الشريان الرئيسي المغذي للموجة الدموية في الأطراف السفلى؟", "a": ["الشريان الفخذي", "الشريان العضدي", "الشريان الأبهر", "الشريان الكعبري"], "correct": 0, "hint": "يمتد عبر تجويف الفخذ الداخلي."},
            {"q": "أي منطقة تشريحية يتم استهدافها لعمل ثقب هوائي طارئ؟", "a": ["الغشاء الحلقي الدرقي", "الغضروف الرغامي السفلي", "أعلى عظمة القص", "التجويف الصدر الوربي الثالث"], "correct": 0, "hint": "تقع مباشرة تحت تفاحة آدم."}
        ],
        "references": [
            "Gray's Anatomy for Students, 4th Edition - Clinical Focus Section",
            "Emergency War Surgery Manual, Chapter 4: Anatomical landmarks"
        ]
    },
    "الجراحة": {
        "video": "https://www.youtube.com/embed/v9XpIDV-75M",
        "intro": "تهدف الجراحة الميدانية بالدرجة الأولى إلى الحفاظ على الحياة عبر إستراتيجية السيطرة على الأضرار (Damage Control) وتأجيل الجراحات التجميلية.",
    "objectives": ["فهم المبادئ الأساسية", "تطبيق البروتوكولات الميدانية", "تحليل الحالات السريرية"],
        "ch1_title": "عقيدة السيطرة على الأضرار الجراحية",
        "ch1_txt": "التحكم في النزيف والسيطرة التامة على التلوث الجرثومي الناتج عن تهتك الأحشاء الداخلية بأسرع وقت، مع إبقاء البطن مفتوحاً مؤقتاً لتسهيل الرقابة اللاحقة.",
        "ch2_title": "التدبير الجراحي لإصابات الصدر والبطن البالستية",
        "ch2_txt": "خطوات استكشاف البطن الإسعافي السريع والتعامل مع أضرار الكبد والطحال الناتجة عن ضغوط الانفجار المباشرة والشظايا سريعة الحركة.",
        "ch3_title": "تنضير الجروح الملوثة والوقاية من الغرغرينا",
        "ch3_txt": "إزالة الأنسجة الميتة والتهتكات الجلدية وتنظيف الجروح المفتوحة بكميات وفيرة من السوائل الملحية للوصول للأنسجة النابضة والحية قبل الإخلاء.",
        "case_study": "استكشاف جراحي إسعافي سريع لبطن جندي متعرض للانفجار، تم السيطرة على النزيف الكبدي الفادح باستعمال قماش التعبئة الجراحي وتأجيل خياطة جدار البطن.",
        "table_headers": ["السيناريو الجراحي", "الإجراء المالي الفوري", "أولوية التدخل الجراحي"],
        "table_rows": [
            ["نزيف حاد من الأحشاء", "تعبئة البطن بالبواكت الجراحية", "رتبة (أ) - طوارئ قصوى"],
            ["انسداد المجاري اللوزية", "خياطة الفتحة الصدرية المفتوحة", "رتبة (أ) - طوارئ قصوى"],
            ["كسر مفتوح في الضلع", "تنضير خفيف وتثبيت وتغطية ورقية", "رتبة (ب) - فوري"],
            ["جرح شظية تجميلي بالذراع", "تنظيف سطحي وتضميد تكتيكي تأجيلي", "رتبة (ج) - مؤجل"]
        ],
        "quiz": [
            {"q": "ما هي الأولوية القصوى في عقيدة جراحة الحروب التكتيكية؟", "a": ["التحكم بالنزيف ومنع التلوث المباشر", "إعادة البناء الجمالي للأنسجة المتهتكة", "خياطة الجروح المعقدة خياطة تجميلية", "عزل المريض بالكامل في غرف معقمة"], "correct": 0, "hint": "الحفاظ على الحياة يسبق المحافظة على الهيكل الخارجي."},
            {"q": "متى يتم اللجوء لتعبئة تجويف البطن بالبواكت الجراحية؟", "a": ["عند النزيف الأحشائي الفادح العصي على الخياطة السريعة", "عند حدوث التهاب خفيف في جدار المعدة", "لتخزين الأدوية المسكنة داخل البطن", "قبل نقل المريض للإخلاء الجوي غير المسلح"], "correct": 0, "hint": "يهدد بالوفاة سريعة بالصدمة الوعائية."}
        ],
        "references": [
            "Clinical Guidelines for Operations - War Surgery Protocols",
            "NATO Combat Casualty Surgical Manual, Volume II"
        ]
    },
    "الوقائي": {
        "video": "https://www.youtube.com/embed/YpXAtQd-r90",
        "intro": "الصحة العامة والطب الوقائي هما حائط الصد الأول للحفاظ على الجاهزية القتالية والحيوية للقوى المتواجدة ومعسكرات التدريب بالميدان.",
    "objectives": ["فهم المبادئ الأساسية", "تطبيق البروتوكولات الميدانية", "تحليل الحالات السريرية"],
        "ch1_title": "السيطرة الوبائية وإدارة المياه الميدانية",
        "ch1_txt": "تطهير مصادر المياه المتوفرة باستخدام تركيبات الكلور وحبوب الفلترة السريعة ورصد سلامة خزانات الغذاء ضد أي تلوث بكتيري.",
        "ch2_title": "السيطرة على تفشي العدوى الفيروسية في المعسكرات",
        "ch2_txt": "بروتوكولات العزل الطبية المنظمة، والفحص البدني الأسبوعي الدوري لأفراد القوات، وتلقي الجرعات والتحصينات البيولوجية بصفة استباقية ثابتة.",
        "ch3_title": "إدارة المخلفات الطبية والوقاية البيئية",
        "ch3_txt": "تصميم وتشييد معامل حرق النفايات الحيوية بعيداً عن مجاري الهواء والمياه لمنع تسرب السموم وناقلات الأوبئة إلى المناطق اللوجستية العامة.",
        "case_study": "تفشي حاد للكوليرا في قطاع عسكري معزول، تم السيطرة الفورية عبر إقامة بروتوكول التطهير الكيميائي للمياه وتحييد الآبار الملوثة تماماً.",
        "table_headers": ["العامل البيئي المعزول", "طريقة التفتيش والمراقبة", "بروتوكول المعالجة الوقائي"],
        "table_rows": [
            ["مياه الآبار", "فحص الجودة البكتريولوجي", "إضافة أقراص الكلور بمعدل 2 ملجم/لتر"],
            ["الطعام الجاف والمعلب", "مراقبة التواريخ التالفة والانتفاخ", "الإتلاف الفوري والحرق المعقّم"],
            ["نفايات المستشفيات", "تصنيف النفايات الملونة الخطرة", "الحرق الحراري التام في أفران مغلقة"],
            ["الأوبئة الفايروسية", "العزل الفوري والفرز والتباعد", "التحصين الجماعي الشامل والفحص الحركي"]
        ],
        "quiz": [
            {"q": "ما هي النسبة الآمنة لإضافة الكلور في تطهير خزانات المياه المستعملة بالميدان؟", "a": ["من 2 إلى 5 ملجم لكل لتر", "100 ملجم لكل لتر", "لا يصح استخدام الكلور بالميدان", "نصف ملجم لكل 100 لتر"], "correct": 0, "hint": "تضمن التخلص من البكتيريا الممرضة دون تسميم الأدميين."},
            {"q": "كيف يتم التعامل مع النفايات العضوية الناجمة عن مستشفى ميداني؟", "a": ["الحرق التام في أفران حرارية مخصصة بعيدة عن الآبار والمعسكرات", "رميها في أقرب مجرى مائي لتصريفها بالتوازي", "دفنها بصفة مكشوفة تحت أشعة الشمس المباشرة فقط", "إرجاعها لمحطات التموين اللوجستي مع المركبات العائدة"], "correct": 0, "hint": "لتفادي تسريب مسببات الخمج والعدوى الفتاكة للأرض والجو."}
        ],
        "references": [
            "Military Preventative Medicine Textbook, US Army Medical Department",
            "Field Sanitation Team Operations Manual, Joint Forces Edition"
        ]
    },
    "الأدوية": {
        "video": "https://www.youtube.com/embed/gD98c9yD1Gg",
        "intro": "علم الأدوية الطارئة في الطب العسكري تملي على الفرد الطبي معرفة الجرعات الدقيقة والموانع الحرجة للمواد العلاجية في بيئة الخطر المعزولة.",
    "objectives": ["فهم المبادئ الأساسية", "تطبيق البروتوكولات الميدانية", "تحليل الحالات السريرية"],
        "ch1_title": "إدارة الآلام الشديدة والصدمة النفسية الحادة",
        "ch1_txt": "استدعاء المسكنات الأفيونية مثل الفنتانيل والمورفين وجرعات استخدام الكيتامين التكتيكية لتسكين آلام الصدمات الشديدة مع مراقبة التنفس وجودة الهواء الرئوي.",
        "ch2_title": "سوائل الإنعاش الوريدي ومعالجة الجفاف والنزيف",
        "ch2_txt": "دور محاليل الملح والرينجر لاكتات، ومتى ينبغي التوقف لإعطاء بدائل الدم والحيويات المصلية لتفادي تفكك الكبد وتخفيف معاملات التخثر الطبيعية.",
        "ch3_title": "المضادات الحيوية واسعة الطيف في بيئة الإصابة الجراحية",
        "ch3_txt": "الإعطاء السريع للمضادات الحيوية الوقائية (مثل Moxifloxacin) لمنع تكوين الخمج المعقد والبكتيريا اللاهوائية الشرسة داخل الجروح العميقة المفتوحة.",
        "case_study": "جندي مصاب ببتر جزئي في اليد ويصرخ من الألم، تم حقنه بالكيتامين الميداني تحت الجلد بمعدل 50 ملجم، مما أدى لتسكين رائع للألم دون تثبيط تنفسه.",
        "table_headers": ["المادة الدوائية المعطاة", "الجرعة الميدانية القياسية", "الموانع الحرجة الملاحظة"],
        "table_rows": [
            ["فينتانيل لاصق / مصاص", "200 إلى 800 ميكروجرام", "صدمة الرأس الشديدة وصعوبة التنفس"],
            ["كيتامين (IV / IM)", "20-50 ملجم وريد أو 50-100 عضلي", "الهلوسة الممتدة أو اضطرابات ضغط هائلة"],
            ["مضاد موكسيفلوكساسين", "400 ملجم حبة واحدة يومياً للبلع", "وجود حساسية للأدوية من فئة الكينولونات"],
            ["حمض الترانيكساميك (TXA)", "1 جرام وريد ببطء خلال 3 ساعات", "النزيف المستمر لمدة تتعدى ثلاث ساعات كاملة"]
        ],
        "quiz": [
            {"q": "أي مضاد حيوي يوصى بتقديمه فمواً في الميدان لغالب الإصابات المفتوحة الشديدة؟", "a": ["موكسيفلوكساسين (Moxifloxacin)", "البنسلين السائل الأساسي", "الأموكسيسيلين العادي للالتهاب الرئوي", "مشروب مغلي البابونج والعسل"], "correct": 0, "hint": "حبة واحدة سهلة البلع ومحمية."},
            {"q": "ما هي الفائدة الكبرى لعلاج حمض الترانيكساميك (TXA) ميدانياً؟", "a": ["تثبيت الخثرة الدموية ومكافحة التحلل المستمر للنزيف الشاري", "إزالة وعلاج الآلام العصبية الشديدة والهلوسة", "تطهير مجاري البول والكلى ضد السموم الخارجية", "مكافحة ارتفاع درجات الحرارة والالتهابات المعوية البسيطة"], "correct": 0, "hint": "يعمل كعامل حارس يتكامل مع عمليات إيقاف النزيف الفوري."}
        ],
        "references": [
            "Tactical Combat Casualty Care guidelines (TCCC), Clinical Drugs Protocols",
            "Emergency Resuscitation Pharmacology Reader, 2025"
        ]
    }
}

# Default text fallback if topic not in COURSES_DATA
DEFAULT_COURSE = {
    "video": "https://www.youtube.com/embed/5D_1EieD8kM",
    "intro": "المناهج الأكاديمية والمبادئ الطبية العسكرية الموحدة تهدف لحماية القوات وضمان سير واستدامة العمليات الإسعافية التكتيكية بامتياز ودراسة كافية.",
    "objectives": ["فهم المبادئ الأساسية", "تطبيق البروتوكولات الميدانية", "تحليل الحالات السريرية"],
    "ch1_title": "مبادئ العمل والمنهج الأكاديمي الموحد",
    "ch1_txt": "تأصيل أسس الرعاية السريرية وحسن تقدير الموارد الطبية والمعدات اللوجستية للتعويض عن شح الاحتياجات في ساحة العمليات المتكاملة غير الفندقية.",
    "ch2_title": "عقيدة التدخل والفرز والوقاية",
    "ch2_txt": "قواعد التعامل بمهنية مع الحالات المتأثرة ورعاية المصابين وتقسيم الطاقة الإسعافية بكفاءة عالية للحفاظ على حيوية الأفراد وحمايتهم الحثيثة.",
    "ch3_title": "البروتوكولات التوجيهية وتجنب الأخطاء التشخيصية",
    "ch3_txt": "مكافحة الجهل المعرفي وتعليم فني التشخيص والتدريب على الإجراءات السريرية بشكل يحاكي النظم العلاجية العالمية في أقسى الأجواء.",
    "case_study": "عملية إنقاذ ناجحة تمت تحت ظروف جوية قاسية وشديدة البرودة، طبق فيها الفريق خطة الفرز السليم للحفاظ على حيوية طاقم الرعاية وعلاج الأفراد.",
    "table_headers": ["مستوى الاستيعاب", "العلامات والوظائف الحيوية", "بروتوكول الفرد المسعف المعتمد"],
    "table_rows": [
        ["الأول: مبتدئ تكتيكي", "العلامات الخارجية الظاهرة وفقط", "الضغط المباشر والربط الموضعي الآمن"],
        ["الثاني: مسعف متقدّم", "النبض المركزي وضغط الرئة والتشخيص", "تثبيت المسالك الهوائية والفتح الشغافي بالصدر"],
        ["الثالث: طبيب ميداني", "الكيمياء العضوية والإنعاش التكميلي الكامل", "الجراحة العاجلة وعلاج الصدمة وتأجيل البناء الجمالي"],
        ["الرابع: إدارة وقيادة", "رصد المجموع وتتبع الفايروس وتأمين البيئة", "الخطط اللوجستية وعزل الحالات وحفظ سلامة القواعد"]
    ],
    "quiz": [
        {"q": "أي إجراء تكتيكي يجب عمله عند أول التقدم لمصاب يصرخ بشدة في الميدان العسكري النشط؟", "a": ["عزل المصاب بالكامل تحت الساتر وتطبيق العاصبة الفورية إذا لزم الأمر", "بدء فحص ضغط الدم والأوعية الدقيقة قبل الإخلاء الموضعي", "حقنه بالمورفين في الوريد مباشرة ودون تثبيت مسبق للعصب الحركي", "انتظار عودة سيارات الدعم العالي اللوجستي إلى الموقع الخلفي"], "correct": 0, "hint": "سلامة الميدان والوقاية الموضعية تسبق كل شيء."},
        {"q": "ما هو الأسلوب الأمثل للتعويض عن عجز الإمداد الميداني بالمستشفى الأمامي؟", "a": ["الابتكار الطبي العسكري واستخدام بدائل بيئية محلية معقمة وقوية", "تأجيل العلاج وترك الجرحى لمناعة أجسادهم الطبيعية", "الانسحاب الفوري إلى الخط الفاصل الخلفي دون توجيه مسبق", "استهلاك الجرعات الدوائية المخصصة للحالات البسيطة الأخرى"], "correct": 0, "hint": "الارتجال والابتكار تحت التدريب العلمي المنضبط هو سلاح المسعف الحقيقي."}
    ],
    "references": [
        "Handbook of Wilderness & Tactical Medicine, Comprehensive Edition",
        "Clinical Field Protocols for Isolated Personnel and Expeditionary Units, 2026"
    ]
}

# ==========================================
# 🛠️ Helper Utilities - Arabic Shaping & RTL
# ==========================================

def reshape_text(text, filter_emojis=True):
    if not text:
        return ""
    if filter_emojis:
        text = filter_emoji(text)
    try:
        reshaped = arabic_reshaper.reshape(text)
        bidi_text = get_display(reshaped)
        return bidi_text
    except Exception:
        return text
def filter_emoji(text):
    """استبدال الإيموجي بنصوص رمزية لتفادي خطأ الخطوط"""
    if not text:
        return text
    for emoji, replacement in EMOJI_MAP.items():
        text = text.replace(emoji, replacement)
    return EMOJI_PATTERN.sub("", text)


def reshape_text_visual(text):
    """تشكيل النص العربي فقط، بدون إعادة ترتيب ثنائي الاتجاه (خاص بالصور)"""
    if not text:
        return ""
    try:
        return arabic_reshaper.reshape(text)
    except Exception:
        return text


def download_font_if_needed():
    """Retrieve Google Amiri-Regular.ttf cleanly or fallback gracefully to maintain font consistency."""
    if not os.path.exists(FONT_PATH):
        print("📥 Deploying Amiri Regular typography from Google Fonts workspace...")
        headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'}
        try:
            req = urllib.request.Request(FONT_URL, headers=headers)
            with urllib.request.urlopen(req, timeout=15) as response:
                with open(FONT_PATH, 'wb') as out_file:
                    out_file.write(response.read())
            print("✅ Amiri Regular downloaded successfully.")
        except Exception as e:
            print(f"⚠️ Primary font download mirrored. Attending backup repository: {e}")
            alt_url = "https://raw.githubusercontent.com/alif-type/amiri/master/Amiri-Regular.ttf"
            try:
                req = urllib.request.Request(alt_url, headers=headers)
                with urllib.request.urlopen(req, timeout=15) as response:
                    with open(FONT_PATH, 'wb') as out_file:
                        out_file.write(response.read())
                print("✅ Amiri Regular downloaded from alternative backup.")
            except Exception as e2:
                print(f"🚨 Crucial: Font unavailable: {e2}. Attempting to locate default OS font...")
                # We will write an empty placeholder font warning or exit
                print("🚨 Place Amiri-Regular.ttf in the script root directory and relaunch.")
                sys.exit(1)

def get_clinical_data_for_title(title):
    """Parse title contents to select the most relevant clinical metadata profile."""
    for kw, data in COURSES_DATA.items():
        if kw in title:
            # We copy default-fallback options for fields that might be absent
            combined = DEFAULT_COURSE.copy()
            combined.update(data)
            return combined
    return DEFAULT_COURSE

# ==========================================
# 📘 1. 5-7 Page PDF Generator (FPDF2 Subclass)
# ==========================================

class AcademyAcademicPDF(FPDF):
    """Custom FPDF2 context to inject military headers, dynamic footagers, and page boundaries."""
    def __init__(self, book_title, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.book_title = book_title

    def header(self):
        # Header starts from Page 2 to leave front cover fully immersive
        if self.page_no() > 1:
            self.set_font("Amiri", "", 10)
            self.set_text_color(112, 128, 144)
            header_text = reshape_text(f"الأكاديمية الطبية العسكرية — مقرر: {self.book_title}")
            self.cell(0, 8, header_text, ln=0, align="R")
            
            # Subtle steel-gold line separator
            self.set_draw_color(197, 160, 89)
            self.set_line_width(0.3)
            self.line(10, 16, 200, 16)
            self.ln(10)

    def footer(self):
        # Footer starts from Page 2
        if self.page_no() > 1:
            self.set_y(-15)
            self.set_font("Amiri", "", 9)
            self.set_text_color(148, 163, 184)
            footer_text = reshape_text("⚠️ وثيقة سريرية تكتيكية - غير مخصصة للتداول الخارجي العام")
            self.cell(0, 10, footer_text, ln=0, align="R")
            
            # Dynamic page counter positioned neatly on the left side
            page_text = reshape_text(f"الصفحة {self.page_no()}")
            self.cell(0, 10, page_text, ln=0, align="L")

def build_pdf_clinical_book(pdf_path, title, book_data):
    """Assembles a robust, beautifully laid out 5-7 Page Clinical Textbook Document."""
    pdf = AcademyAcademicPDF(title)
    pdf.add_page()
    
    # Enable Amiri arabic typeface
    pdf.add_font("Amiri", "", FONT_PATH)
    pdf.set_font("Amiri", size=14)
    
    # ---------------------------------------------
    # 📄 Page 1: IMMERSIVE MILITARY COVER DESIGN
    # ---------------------------------------------
    # Thick Steel Outer Frame
    pdf.set_draw_color(30, 41, 59)
    pdf.set_line_width(0.8)
    pdf.rect(6, 6, 198, 285)
    
    pdf.set_font("Amiri", size=12)
    pdf.set_text_color(100, 116, 139)
    pdf.cell(0, 8, reshape_text("الخدمات الطبية العسكرية بالقوات المسلحة"), ln=True, align="R")
    pdf.cell(0, 5, reshape_text("مجمع البحوث والتعليم الطبي التكتيكي المشترك"), ln=True, align="R")
    
    pdf.ln(50)
    
    # Bold Crimson / Gold double separator
    pdf.set_draw_color(197, 160, 89)
    pdf.set_line_width(1.8)
    pdf.line(20, pdf.get_y(), 190, pdf.get_y())
    pdf.ln(10)
    
    pdf.set_font("Amiri", size=24)
    pdf.set_text_color(15, 23, 42) # Premium deep navy
    pdf.cell(0, 15, reshape_text(title), ln=True, align="C")
    
    pdf.ln(8)
    pdf.set_draw_color(197, 160, 89)
    pdf.set_line_width(0.8)
    pdf.line(40, pdf.get_y(), 170, pdf.get_y())
    pdf.ln(15)
    
    pdf.set_font("Amiri", size=13)
    pdf.set_text_color(197, 160, 89)
    pdf.cell(0, 8, reshape_text("المجلد الأكاديمي والتعليمي الحراكي المعتمد"), ln=True, align="C")
    
    pdf.ln(75)
    
    pdf.set_font("Amiri", size=11)
    pdf.set_text_color(112, 128, 144)
    pdf.cell(0, 6, reshape_text("أقرّ بصفة رسمية من هيئة الاعتماد العام للأطباء والمسعفين"), ln=True, align="C")
    current_year = datetime.now().strftime("%Y")
    pdf.cell(0, 6, reshape_text(f"طبعة ميدانية منقحة ومكثفة - لعام {current_year} م"), ln=True, align="C")
    
    # ---------------------------------------------
    # 📄 Page 2: TABLE OF CONTENTS & INTRODUCTION
    # ---------------------------------------------
    pdf.add_page()
    pdf.rect(6, 6, 198, 285)
    
    pdf.ln(10)
    pdf.set_font("Amiri", size=16)
    pdf.set_text_color(15, 23, 42)
    pdf.cell(0, 10, reshape_text("🗂️ فهرس الموضوعات والمبادئ العلمية"), ln=True, align="R")
    
    pdf.ln(4)
    pdf.set_font("Amiri", size=12)
    pdf.set_text_color(71, 85, 105)
    
    # Draw polished Table of Contents lines
    toc_items = [
        ("الفصل الأول: التأصيل السريري والفيزيولوجي للهدف", "الصفحة 3"),
        (f"الفصل الثاني: {book_data['ch1_title']}", "الصفحة 4"),
        (f"الفصل الثالث: {book_data['ch2_title']}", "الصفحة 5"),
        (f"الفصل الرابع: {book_data['ch3_title']}", "الصفحة 6"),
        ("المبحث الخامس: الهيئات الطبية والمراجع الأكاديمية", "الصفحة 7")
    ]
    for item_title, item_page in toc_items:
        # Create dashed dot fill line for professional TOC layout
        dots_count = 55 - len(item_title) - len(item_page)
        dots = "." * max(10, dots_count)
        pdf.cell(30, 8, reshape_text(item_page), ln=0, align="L")
        pdf.cell(160, 8, reshape_text(f"{item_title} {dots}"), ln=1, align="R")
        pdf.ln(2)
        
    pdf.ln(12)
    pdf.set_font("Amiri", size=15)
    pdf.set_text_color(197, 160, 89)
    pdf.cell(0, 10, reshape_text("📖 مقدمة علمية عامة"), ln=True, align="R")
    
    pdf.set_font("Amiri", size=11.5)
    pdf.set_text_color(51, 65, 85)
    pdf.multi_cell(0, 7.5, reshape_text(book_data["intro"]), align="R")
    pdf.ln(5)
    pdf.multi_cell(0, 7.5, reshape_text("تعتبر القدرة السريعة على التعامل الطبي الميداني في نطاق الوقت الذهبي (Golden Hour) العامل المعوض المرجح لسلامة وأرواح الجنود ومقاومة الخسائر البالستية بالموقع."), align="R")

    # ---------------------------------------------
    # 📄 Page 3: CHAPTER 1 - CLINICAL CORE STUDY
    # ---------------------------------------------
    pdf.add_page()
    pdf.rect(6, 6, 198, 285)
    
    pdf.ln(10)
    pdf.set_font("Amiri", size=16)
    pdf.set_text_color(15, 23, 42)
    pdf.cell(0, 10, reshape_text("🔬 الفصل الأول: التأصيل والتحليل الفيزيولوجي"), ln=True, align="R")
    pdf.ln(4)
    
    pdf.set_font("Amiri", size=11.5)
    pdf.set_text_color(51, 65, 85)
    pdf.multi_cell(0, 8, reshape_text("إن الفهم الكامل لتطورات الفسيولوجيا والموازنة العضوية الداخلية يجنب المستجيب ارتكاب أخطاء تشخيصية فادحة تحت ضغط ساحة العراك الشديد."), align="R")
    pdf.ln(6)
    pdf.multi_cell(0, 8, reshape_text("تتكامل الأجهزة الحيوية للجسم البشري لمواجهة الصدمة العميقة عبر آليات تعويض ذاتية تسعى جاهدة للمحافظة على الأوكسجين الواصل إلى المخ وعضلات القلب. هنا تبرز أهمية رصد النبض الشرياني المركزي وتجنب هدر السوائل المعطاة للمريض قبل إجراء العمليات التكتيكية المانعة للنزيف الصدري أو المغلق."), align="R")
    
    pdf.ln(8)
    pdf.set_font("Amiri", size=14)
    pdf.set_text_color(197, 160, 89)
    pdf.cell(0, 10, reshape_text("⚙️ عقيدة الاستجابة اللوجستية المنضبطة:"), ln=True, align="R")
    
    pdf.set_font("Amiri", size=11)
    pdf.set_text_color(71, 85, 105)
    points = [
        "1. التقييم السريع والتصنيف الموضوعي الفوري وفق الفرز الطبي المعتمد.",
        "2. موازنة السوائل وحظر إعطاء المسكنات العشوائية الشديدة للمتأثرين بالارتجاج.",
        "3. المراقبة اللاحقة المستمرة وتوفير البيئة الحرارية الآمنة لتطويق النزيف."
    ]
    for pt in points:
        pdf.multi_cell(0, 7.5, reshape_text(pt), align="R")
        pdf.ln(2)

    # ---------------------------------------------
    # 📄 Page 4: CHAPTER 2 - TACTICAL MULTI-COLUMN DATA TABLE
    # ---------------------------------------------
    pdf.add_page()
    pdf.rect(6, 6, 198, 285)
    
    pdf.ln(10)
    pdf.set_font("Amiri", size=15)
    pdf.set_text_color(15, 23, 42)
    pdf.cell(0, 10, reshape_text(f"📊 الفصل الثاني: المعايير والمصفوفات الطبية لعلاج {title}"), ln=True, align="R")
    pdf.ln(4)
    
    pdf.set_font("Amiri", size=11)
    pdf.set_text_color(51, 65, 85)
    pdf.multi_cell(0, 7.5, reshape_text("توفر الجداول والمصفوفات السريرية التالية وسيلة سريعة لتأطير الحالة واتخاذ القرارات السريرية الدقيقة دون تباطؤ أو تردد:"), align="R")
    pdf.ln(8)
    
    # Table Config - 3 columns
    # We must fit to 180mm width total
    col_widths = [50, 60, 70] # left to right
    headers = book_data["table_headers"]
    rows = book_data["table_rows"]
    
    # Render Header (Gold Background look - custom fpdf text cell)
    pdf.set_fill_color(197, 160, 89)
    pdf.set_text_color(255, 255, 255)
    pdf.set_font("Amiri", size=11)
    
    # Iterate in reverse for RTL layout in table rendering
    pdf.cell(col_widths[2], 10, reshape_text(headers[0]), border=1, fill=True, align="C")
    pdf.cell(col_widths[1], 10, reshape_text(headers[1]), border=1, fill=True, align="C")
    pdf.cell(col_widths[0], 10, reshape_text(headers[2]), border=1, fill=True, align="C")
    pdf.ln(10)
    
    # Render Rows
    pdf.set_text_color(51, 65, 85)
    pdf.set_font("Amiri", size=10)
    
    alternate = False
    for row in rows:
        if alternate:
            pdf.set_fill_color(248, 250, 252) # Slate light
        else:
            pdf.set_fill_color(255, 255, 255)
            
        pdf.cell(col_widths[2], 9.5, reshape_text(row[0]), border=1, fill=True, align="R")
        pdf.cell(col_widths[1], 9.5, reshape_text(row[1]), border=1, fill=True, align="R")
        pdf.cell(col_widths[0], 9.5, reshape_text(row[2]), border=1, fill=True, align="R")
        pdf.ln(9.5)
        alternate = not alternate
        
    pdf.ln(15)
    pdf.set_font("Amiri", size=11)
    pdf.set_text_color(112, 128, 144)
    pdf.multi_cell(0, 7, reshape_text("ملاحظة هامة: تخضع المعايير أعلاه ومستويات الاستجابة لفهم الظروف اللوجستية المتاحة وقدرات الفرد الطبي وعقد الرعاية الحالية."), align="R")

    # ---------------------------------------------
    # 📄 Page 5: CHAPTER 3 - ACTIONABLE CLINICAL PROCEDURES
    # ---------------------------------------------
    pdf.add_page()
    pdf.rect(6, 6, 198, 285)
    
    pdf.ln(10)
    pdf.set_font("Amiri", size=15)
    pdf.set_text_color(15, 23, 42)
    pdf.cell(0, 10, reshape_text(f"🩺 الفصل الثالث: {book_data['ch2_title']}"), ln=True, align="R")
    pdf.ln(4)
    
    pdf.set_font("Amiri", size=11.5)
    pdf.set_text_color(51, 65, 85)
    pdf.multi_cell(0, 8, reshape_text(book_data["ch2_txt"]), align="R")
    pdf.ln(8)
    
    pdf.set_font("Amiri", size=14)
    pdf.set_text_color(197, 160, 89)
    pdf.cell(0, 10, reshape_text("🛠️ الخطوات التشغيلية الثابتة (Standard Steps Overview):"), ln=True, align="R")
    pdf.ln(4)
    
    pdf.set_font("Amiri", size=11)
    pdf.set_text_color(71, 85, 105)
    procedures = [
        "🔄 تفقد الميدان أولاً وتأكيد التأمين العسكري الكامل لمحيط الحدث قبل التقدم.",
        "🩹 تركيب دعامات وقف النزيف التكتيكي والضغط الشرياني باستخدام العاصبات الشريانية.",
        "🌬️ فتح وتأمين المسلك التنفسي ووضع المصاب في وضع النجاة والإنعاش التكتيكي.",
        "📊 الفحص الهيكلي الإكلينيكي الشامل والبدء بالرعاية الجراحية أو المصلية المانعة للصدمة."
    ]
    for proc in procedures:
        pdf.multi_cell(0, 8, reshape_text(proc), align="R")
        pdf.ln(3)

    # ---------------------------------------------
    # 📄 Page 6: CHAPTER 4 - INTEGRATED TACTICAL CASE SCENARIOS
    # ---------------------------------------------
    pdf.add_page()
    pdf.rect(6, 6, 198, 285)
    
    pdf.ln(10)
    pdf.set_font("Amiri", size=15)
    pdf.set_text_color(15, 23, 42)
    pdf.cell(0, 10, reshape_text(f"🔥 الفصل الرابع: {book_data['ch3_title']} وسيناريوهات المحاكاة"), ln=True, align="R")
    pdf.ln(4)
    
    pdf.set_font("Amiri", size=11.5)
    pdf.set_text_color(51, 65, 85)
    pdf.multi_cell(0, 8, reshape_text(book_data["ch3_txt"]), align="R")
    pdf.ln(10)
    
    # Render Case Study Area with a shaded slate block background
    pdf.set_fill_color(241, 245, 249)
    pdf.set_draw_color(148, 163, 184)
    pdf.set_line_width(0.5)
    
    # Calculate box height dynamically or set safely. We use multi_cell with Rect for styling.
    # Note: drawing border first
    pdf.rect(12, pdf.get_y(), 186, 65)
    pdf.ln(4)
    pdf.set_font("Amiri", size=12, style="")
    pdf.set_text_color(197, 160, 89)
    pdf.cell(180, 8, reshape_text("👨‍⚕️ دراسة حالة ميدانية واقعية (Clinical Narrative Record):"), ln=True, align="R")
    pdf.ln(2)
    
    pdf.set_font("Amiri", size=11)
    pdf.set_text_color(15, 23, 42)
    pdf.multi_cell(180, 7.5, reshape_text(book_data["case_study"]), align="R")
    pdf.ln(4)
    pdf.set_font("Amiri", size=10)
    pdf.set_text_color(71, 85, 105)
    pdf.multi_cell(180, 7, reshape_text("الدروس المستفادة: تبرهن الرقابة الحركية والتدخل الفيروزي تحت ساتر عائق النيران المانعة على حفظ الجهد والأرواح بمعدل 95% مقارنة بالإسعاف العشوائي المستجيب."), align="R")
    
    pdf.ln(20)
    pdf.set_font("Amiri", size=11)
    pdf.set_text_color(224, 86, 86)
    pdf.multi_cell(0, 7.5, reshape_text("🚨 تنبيه الطبيب المسؤول: يمنع منعاً باتاً ممارسة هذه الإجراءات خارج الأطر التدريبية العسكرية المعدة لطلبة الأكاديمية والمقيدة بمدونات الحماية السريرية."), align="R")

    # ---------------------------------------------
    # 📄 Page 7: BIBLIOGRAPHY & SIGNATURES OF ACADEMIC BOARD
    # ---------------------------------------------
    pdf.add_page()
    pdf.rect(6, 6, 198, 285)
    
    pdf.ln(15)
    pdf.set_font("Amiri", size=16)
    pdf.set_text_color(15, 23, 42)
    pdf.cell(0, 10, reshape_text("📚 المبحث الخامس: الهيئات الطبية والمراجع الأكاديمية"), ln=True, align="R")
    pdf.ln(4)
    
    pdf.set_font("Amiri", size=11)
    pdf.set_text_color(51, 65, 85)
    pdf.multi_cell(0, 8, reshape_text("تمت صياغة ومواءمة البنود الفسطاطية السريرية في هذا المقرر الأكاديمي بالرجوع للمناهج والدوريات الطبية العالمية المتخصصة في الطب الحرابي والبحوث التكتيكية التالية:"), align="R")
    pdf.ln(8)
    
    # References
    pdf.set_font("Amiri", "", 10)  # Courier/Sans fallback font or Amiri
    pdf.set_font("Amiri", size=11)
    pdf.set_text_color(71, 85, 105)
    for ref in book_data["references"]:
        pdf.cell(0, 8, f"- {ref}", ln=True, align="L")
        pdf.ln(2)
        
    pdf.ln(30)
    
    # Administrative board signatures section
    pdf.set_draw_color(226, 232, 240)
    pdf.line(20, pdf.get_y(), 190, pdf.get_y())
    pdf.ln(8)
    
    # 3 Signature Columns (RTL representation)
    pdf.set_font("Amiri", size=11)
    pdf.set_text_color(15, 23, 42)
    pdf.cell(60, 8, reshape_text("أمين عام مجلس المناهج"), ln=0, align="C")
    pdf.cell(60, 8, reshape_text("مدير الأكاديمية الطبية"), ln=0, align="C")
    pdf.cell(60, 8, reshape_text("هيئة الاعتماد العسكري"), ln=1, align="C")
    
    pdf.set_font("Amiri", size=10)
    pdf.set_text_color(100, 116, 139)
    pdf.cell(60, 6, reshape_text("لواء طبيب/ حمزة آل ثاني"), ln=0, align="C")
    pdf.cell(60, 6, reshape_text("عميد طبيب/ وسام العجمي"), ln=0, align="C")
    pdf.cell(60, 6, reshape_text("مجلس الدفاع الصيرفي المشترك"), ln=1, align="C")
    
    # Save clinical document
    pdf.output(pdf_path)

# ==========================================
# 🎨 2. Premium PNG Cover Generator (PIL Artwork)
# ==========================================

def draw_luxurious_png_cover(cover_path, title):
    """Draws massive, beautiful, highly professional publication covers with tactical details."""
    width, height = 400, 600
    
    # Selection of authoritative military & clinical color spectrums
    color_schemes = [
        {"bg_start": (7, 20, 36),   "bg_end": (15, 36, 61),   "theme": "navy"},   # Tactical Royal Navy
        {"bg_start": (11, 31, 18),  "bg_end": (22, 54, 32),   "theme": "olive"},  # Military Combat Forest
        {"bg_start": (36, 15, 15),  "bg_end": (59, 21, 21),   "theme": "crimson"},# Field Operations Red
        {"bg_start": (24, 21, 15),  "bg_end": (41, 35, 24),   "theme": "sand"}    # Desert Shield Bronze
    ]
    scheme = random.choice(color_schemes)
    
    # Create smooth gradient backgrounds
    image = Image.new("RGBA", (width, height))
    draw = ImageDraw.Draw(image)
    
    for y in range(height):
        factor = y / height
        r = int(scheme["bg_start"][0] * (1 - factor) + scheme["bg_end"][0] * factor)
        g = int(scheme["bg_start"][1] * (1 - factor) + scheme["bg_end"][1] * factor)
        b = int(scheme["bg_start"][2] * (1 - factor) + scheme["bg_end"][2] * factor)
        draw.line([(0, y), (width, y)], fill=(r, g, b, 255))
        
    # Render glowing internal geometric borders
    gold_rgb = (197, 160, 89)
    for i in range(12):
        alpha = int(140 * (1.0 - i/12.0))
        draw.rectangle([i, i, width - 1 - i, height - 1 - i], outline=gold_rgb + (alpha,), width=1)
        
    # Draw military cross shield crest using PIL polygon and line drawings
    cx, cy = width // 2, 105
    shield_coords = [
        (cx, cy - 35), (cx + 30, cy - 25), (cx + 30, cy + 10),
        (cx, cy + 40), (cx - 30, cy + 10), (cx - 30, cy - 25)
    ]
    # Draw Shield fill and borders
    draw.polygon(shield_coords, fill=gold_rgb + (40,), outline=gold_rgb + (255,), width=2)
    # Inside Cross +
    draw.line([(cx, cy - 20), (cx, cy + 25)], fill=gold_rgb + (255,), width=4)
    draw.line([(cx - 18, cy + 2), (cx + 18, cy + 2)], fill=gold_rgb + (255,), width=4)
    
    # Star accents around the shield
    star_points = [
        (cx, cy - 50), (cx + 4, cy - 43), (cx + 12, cy - 43), (cx + 6, cy - 38),
        (cx + 8, cy - 30), (cx, cy - 35), (cx - 8, cy - 30), (cx - 6, cy - 38),
        (cx - 12, cy - 43), (cx - 4, cy - 43)
    ]
    draw.polygon(star_points, fill=gold_rgb + (255,))
    
    # Try deploying Amiri fonts, fallback to standard if not found
    try:
        font_title = ImageFont.truetype(FONT_PATH, 24)
        font_crest = ImageFont.truetype(FONT_PATH, 11)
        font_tagline = ImageFont.truetype(FONT_PATH, 11)
        font_sub = ImageFont.truetype(FONT_PATH, 12)
    except Exception:
        font_title = ImageFont.load_default()
        font_crest = ImageFont.load_default()
        font_tagline = ImageFont.load_default()
        font_sub = ImageFont.load_default()
        
    # Typography - Header Text
    draw.text((cx, 165), reshape_text_visual("كليّة الطب الميداني العسكري الأكاديمي"), fill=(241, 245, 249, 255), font=font_crest, anchor="mm")
    draw.text((cx, 185), reshape_text_visual("مناهج الطوارئ والدفاع الطبي الحربي"), fill=gold_rgb + (255,), font=font_tagline, anchor="mm")
    
    # Process Title for clean linebreaks on the cover
    words = title.split()
    lines = []
    line_cur = []
    for w in words:
        candidate_line = " ".join(line_cur + [w])
        # Calculate size limit
        w_px = draw.textlength(reshape_text_visual(candidate_line), font=font_title)
        if w_px < (width - 70):
            line_cur.append(w)
        else:
            if line_cur:
                lines.append(" ".join(line_cur))
            line_cur = [w]
    if line_cur:
        lines.append(" ".join(line_cur))
        
    # Render multiple lines dynamically centered on the canvas
    starting_y = 265
    for l in lines:
        # Give heading drop shadow effect for immense visual clarity
        draw.text((cx + 1, starting_y + 1), reshape_text_visual(l), fill=(0, 0, 0, 180), font=font_title, anchor="mm")
        draw.text((cx, starting_y), reshape_text_visual(l), fill=(255, 255, 255, 255), font=font_title, anchor="mm")
        starting_y += 34
        
    # Glowing Badge Panel
    badge_y = starting_y + 40
    badge_w = 115
    draw.rectangle([cx - badge_w, badge_y - 12, cx + badge_w, badge_y + 12], fill=gold_rgb + (30,), outline=gold_rgb + (255,), width=1)
    draw.text((cx, badge_y), reshape_text_visual("🎖️ مقرر رسمي ومكثف - معتمد للدفاع"), fill=gold_rgb + (255,), font=font_tagline, anchor="mm")
    
    # Outer Framing & Signature Labels
    draw.text((cx, height - 70), reshape_text_visual("منشورات الجمعية الطبية العسكرية المشتركة"), fill=(148, 163, 184, 255), font=font_sub, anchor="mm")
    draw.text((cx, height - 45), reshape_text_visual("⚠️ سري وللاستخدام التدريبي فقط"), fill=(231, 76, 60, 255), font=font_crest, anchor="mm")
    
    image.save(cover_path, "PNG")

# ==========================================
# 📄 3. Interactive HTML Lectures (Theoretical & Practical)
# ==========================================

def write_interactive_html_lecture(theory_path, practical_path, title, book_data):
    """Outputs modern, interactive HTML pages with responsive CSS systems, charts, videos, and quizzes."""
    
    # Premium responsive stylesheet including heart pulse animations and custom layouts
    css_content = """
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Cairo:wght@400;600;700;800&family=Amiri:ital,wght@0,400;0,700;1,400&display=swap');
        
        :root {
            --bg-color: #040912;
            --card-bg: rgba(11, 24, 43, 0.7);
            --border-glow: rgba(197, 160, 89, 0.3);
            --gold-accent: #c5a059;
            --emerald-accent: #2ecc71;
            --blue-accent: #3498db;
            --text-main: #f1f5f9;
            --text-secondary: #94a3b8;
            --danger-accent: #e74c3c;
        }
        
        body {
            font-family: 'Cairo', 'Amiri', Tahoma, sans-serif;
            direction: rtl;
            background-color: var(--bg-color);
            color: var(--text-main);
            margin: 0;
            padding: 20px;
            line-height: 1.8;
            background-image: radial-gradient(circle at 10% 20%, rgba(11, 24, 43, 0.5) 0%, rgba(4, 9, 18, 1) 90%);
        }
        
        .container {
            max-width: 850px;
            margin: 0 auto;
            padding-bottom: 60px;
        }
        
        /* Premium Glowing Header */
        .header-card {
            background: linear-gradient(135deg, rgba(15, 34, 58, 0.95) 0%, rgba(11, 24, 43, 0.95) 100%);
            border: 1px solid var(--border-glow);
            border-radius: 16px;
            padding: 35px;
            text-align: center;
            margin-bottom: 25px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.6);
            position: relative;
            overflow: hidden;
            animation: fadeIn 1s ease-in-out;
        }
        
        .header-card::before {
            content: '';
            position: absolute;
            top: 0; left: 0; right: 0; height: 4px;
            background: linear-gradient(90deg, var(--gold-accent), var(--emerald-accent), var(--blue-accent));
        }
        
        h1 {
            font-size: 26px;
            font-weight: 800;
            color: var(--gold-accent);
            margin: 0 0 10px 0;
            line-height: 1.4;
            text-shadow: 0 0 10px rgba(197, 160, 89, 0.3);
        }
        
        .type-badge {
            display: inline-block;
            padding: 5px 16px;
            border-radius: 50px;
            font-size: 11.5px;
            font-weight: 700;
            margin-bottom: 15px;
            text-transform: uppercase;
            box-shadow: inset 0 2px 4px rgba(0,0,0,0.4);
        }
        
        .type-badge.theory {
            background-color: rgba(197, 160, 89, 0.15);
            color: var(--gold-accent);
            border: 1px solid rgba(197, 160, 89, 0.4);
        }
        
        .type-badge.practical {
            background-color: rgba(46, 204, 113, 0.15);
            color: var(--emerald-accent);
            border: 1px solid rgba(46, 204, 113, 0.4);
        }
        
        .metadata-row {
            display: flex;
            justify-content: center;
            gap: 30px;
            font-size: 13px;
            color: var(--text-secondary);
            margin-top: 15px;
            flex-wrap: wrap;
        }
        
        .meta-item {
            display: flex;
            align-items: center;
            gap: 8px;
        }
        
        .meta-icon {
            font-size: 16px;
            color: var(--gold-accent);
        }
        
        /* Card Layout Panels */
        .card {
            background: var(--card-bg);
            border: 1px solid rgba(255,255,255, 0.05);
            border-radius: 12px;
            padding: 26px;
            margin-bottom: 22px;
            box-shadow: 0 5px 25px rgba(0,0,0,0.4);
            backdrop-filter: blur(10px);
            transition: transform 0.3s ease, border-color 0.3s ease;
        }
        
        .card:hover {
            transform: translateY(-2px);
            border-color: rgba(197, 160, 89, 0.2);
        }
        
        .card-title {
            font-size: 17px;
            font-weight: 700;
            color: var(--gold-accent);
            margin-top: 0;
            margin-bottom: 18px;
            display: flex;
            align-items: center;
            gap: 12px;
            border-bottom: 1px solid rgba(255,255,255,0.08);
            padding-bottom: 12px;
        }
        
        p {
            font-size: 14.5px;
            color: #d1d5db;
            margin: 0 0 15px 0;
            line-height: 1.8;
            text-align: justify;
        }
        
        ul {
            margin: 0;
            padding-right: 22px;
        }
        
        li {
            font-size: 14px;
            color: #e2e8f0;
            margin-bottom: 10px;
            line-height: 1.7;
        }
        
        /* Responsive Video Player Mock */
        .video-container {
            position: relative;
            padding-bottom: 56.25%;
            height: 0;
            overflow: hidden;
            border-radius: 10px;
            border: 1px solid rgba(255,255,255,0.1);
            box-shadow: 0 10px 30px rgba(0,0,0,0.5);
            margin-bottom: 10px;
        }
        
        .video-container iframe {
            position: absolute;
            top: 0; left: 0; width: 100%; height: 100%;
            border: none;
        }
        
        /* Animated ECG Graph representation using SVG */
        .ecg-svg {
            background-color: #030712;
            border: 1px solid rgba(197,160,89, 0.25);
            border-radius: 10px;
            box-shadow: inset 0 0 15px rgba(0,250,50,0.1);
            margin: 15px 0;
        }
        
        .ecg-path {
            stroke-dasharray: 1000;
            stroke-dashoffset: 1000;
            animation: ecgSweep 4s linear infinite;
        }
        
        .heart-pulse {
            transform-origin: center;
            animation: heartAnim 1s ease infinite;
        }
        
        /* Accordion Interactive Quiz Blocks */
        details {
            background-color: rgba(255,255,255, 0.02);
            border: 1px solid rgba(255,255,255, 0.05);
            border-radius: 8px;
            margin-bottom: 12px;
            padding: 14px;
            transition: background-color 0.3s ease;
        }
        
        details[open] {
            background-color: rgba(255,255,255,0.05);
            border-color: rgba(197, 160, 89, 0.3);
        }
        
        summary {
            font-size: 14.5px;
            font-weight: 600;
            color: var(--text-main);
            cursor: pointer;
            outline: none;
            user-select: none;
        }
        
        summary:hover {
            color: var(--gold-accent);
        }
        
        .details-content {
            margin-top: 15px;
            line-height: 1.7;
            padding-right: 10px;
            border-right: 2px solid var(--gold-accent);
        }
        
        /* Dynamic MCQ Interactive Board */
        .quiz-option {
            background: rgba(255,255,255, 0.04);
            border: 1px solid rgba(255,255,255, 0.08);
            border-radius: 8px;
            padding: 12px 18px;
            margin: 10px 0;
            cursor: pointer;
            transition: all 0.25s ease;
            font-size: 13.5px;
            display: flex;
            align-items: center;
            gap: 12px;
        }
        
        .quiz-option:hover {
            background: rgba(197, 160, 89, 0.1);
            border-color: var(--gold-accent);
            transform: translateX(-5px);
        }
        
        .quiz-option.active {
            border-color: var(--blue-accent);
            background: rgba(52, 152, 219, 0.15);
        }
        
        .quiz-submit {
            background: linear-gradient(90deg, var(--gold-accent), #a78040);
            color: #040912;
            border: none;
            padding: 12px 28px;
            border-radius: 8px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 700;
            width: 100%;
            margin-top: 15px;
            transition: filter 0.2s;
        }
        
        .quiz-submit:hover {
            filter: brightness(1.15);
        }
        
        .feedback-banner {
            display: none;
            padding: 15px;
            border-radius: 8px;
            margin-top: 15px;
            font-size: 13.5px;
            line-height: 1.6;
        }
        
        .feedback-banner.success {
            background: rgba(46, 204, 113, 0.15);
            border: 1px solid var(--emerald-accent);
            color: var(--emerald-accent);
        }
        
        .feedback-banner.fail {
            background: rgba(231, 76, 60, 0.15);
            border: 1px solid var(--danger-accent);
            color: var(--danger-accent);
        }
        
        /* Stepper progress */
        .stepper {
            display: flex;
            justify-content: space-between;
            margin: 20px 0;
            position: relative;
        }
        
        .stepper::before {
            content: '';
            position: absolute;
            top: 15px; left: 0; right: 0; height: 2px;
            background: rgba(255,255,255,0.1);
            z-index: 1;
        }
        
        .step {
            width: 32px; height: 32px;
            background: #0d1e34;
            border: 2px solid rgba(255,255,255,0.15);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 12px;
            font-weight: 700;
            color: var(--text-secondary);
            z-index: 2;
            box-shadow: 0 4px 10px rgba(0,0,0,0.5);
        }
        
        .step.active {
            border-color: var(--gold-accent);
            color: var(--gold-accent);
            box-shadow: 0 0 10px rgba(197,160,89, 0.4);
        }
        
        .warning-strip {
            background: rgba(231, 76, 60, 0.08);
            border: 1.5px solid rgba(231, 76, 60, 0.35);
            border-radius: 10px;
            padding: 18px;
            margin-top: 25px;
        }
        
        .warning-title {
            color: var(--danger-accent);
            font-size: 14px;
            font-weight: 700;
            margin-bottom: 8px;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .footer-trademark {
            text-align: center;
            margin-top: 50px;
            font-size: 12px;
            color: var(--text-secondary);
            opacity: 0.6;
            letter-spacing: 0.3px;
        }
        
        /* Animations */
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
        }
        
        @keyframes ecgSweep {
            to { stroke-dashoffset: 0; }
        }
        
        @keyframes heartAnim {
            0% { transform: scale(1); }
            30% { transform: scale(1.15); }
            60% { transform: scale(1); }
            80% { transform: scale(1.10); }
            100% { transform: scale(1); }
        }
    </style>
    """

    # Interactive Quiz Core JS Engine Setup
    # Generates client-side responses, checking correctness, revealing hints, and rendering scores
    quiz_code_maker = lambda quiz_list, q_id_prefix: f"""
    <script>
        const answers_{q_id_prefix} = {json.dumps([q["correct"] for q in quiz_list])};
        let selected_{q_id_prefix} = {{}};
        
        function selectOption_{q_id_prefix}(questionIdx, optionIdx) {{
            selected_{q_id_prefix}[questionIdx] = optionIdx;
            
            // clear active status on adjacent buttons
            const parent = document.getElementById('{q_id_prefix}_q' + questionIdx);
            const options = parent.getElementsByClassName('quiz-option');
            for(let i=0; i<options.length; i++) {{
                options[i].classList.remove('active');
            }}
            // mark current active
            event.currentTarget.classList.add('active');
        }}
        
        function evaluateQuiz_{q_id_prefix}() {{
            let correctCount = 0;
            const size = answers_{q_id_prefix}.length;
            
            for (let i = 0; i < size; i++) {{
                if (selected_{q_id_prefix}[i] === undefined) {{
                    alert("يرجى الإجابة على جميع الأسئلة لتلقي التقييم السريري!");
                    return;
                }}
                if (selected_{q_id_prefix}[i] === answers_{q_id_prefix}[i]) {{
                    correctCount++;
                }}
            }}
            
            const scorePercent = Math.round((correctCount / size) * 100);
            const banner = document.getElementById('fb_{q_id_prefix}');
            banner.style.display = 'block';
            
            if (scorePercent >= 80) {{
                banner.className = 'feedback-banner success';
                banner.innerHTML = `🏁 <b>تقييم الأكاديمية: اجتياز ناجح وممتاز!</b><br>لقد أحرزت نسبة دراسية تقارب ${{scorePercent}}% (${{correctCount}} من أصل ${{size}}). جدارتك الطبية التكتيكية مكتملة ومثبتة عملياً!`;
            }} else {{
                banner.className = 'feedback-banner fail';
                banner.innerHTML = `🏁 <b>تقييم الأكاديمية: رسوب دراسي أو تعليق جدارة!</b><br>لقد أحرزت نسبة دراسية ${{scorePercent}}% (${{correctCount}} من أصل ${{size}}). يوصى بإعادة قراءة الفصول ومراجعة الشرح الطبي بعناية لإعادة التقديم.`;
            }}
        }}
    </script>
    """

    # ------------------
    # 📑 Render Theoretical Module (Lecture 1)
    # ------------------
    mcq_rendered_t = ""
    for idx, q_card in enumerate(book_data["quiz"]):
        options_html = "".join(f"""
            <div class="quiz-option" onclick="selectOption_t({idx}, {oidx})">
                <span style="font-weight: 700; color:var(--gold-accent);">{"أبجد"[oidx]} .</span>
                <span>{opt}</span>
            </div>""" for oidx, opt in enumerate(q_card["a"]))
            
        mcq_rendered_t += f"""
        <div class="question-block" id="t_q{idx}" style="margin-top:20px; border-bottom:1px dashed rgba(255,255,255,0.05); padding-bottom:15px;">
            <p style="font-weight: 700; color:#f1f5f9;">{idx+1}. {q_card["q"]}</p>
            {options_html}
            <details style="margin-top:10px;">
                <summary style="font-size:12px; color:var(--text-secondary);">💡 تلميح للمساعدة في الحل</summary>
                <div class="details-content" style="font-size:11.5px; color:#d1d5db;">
                    {q_card["hint"]}
                </div>
            </details>
        </div>"""

    html_theory = f"""<!DOCTYPE html>
    <html lang="ar">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>{title} - الأطروحة الطبية النظرية</title>
        {css_content}
    </head>
    <body>
        <div class="container">
            <div class="header-card">
                <span class="type-badge theory">دراسة أكاديمية نظرية - التقييم السريري</span>
                <h1>{title}</h1>
                <div class="metadata-row">
                    <span class="meta-item"><span class="meta-icon">👤</span>العقيد الطبيب/ د. سليم الحامد</span>
                    <span class="meta-item"><span class="meta-icon">⏱️</span>45 دقيقة دراسية</span>
                    <span class="meta-item"><span class="meta-icon">📂</span>مطبوعات الأكاديمية العسكرية</span>
                </div>
            </div>
            
            <div class="card">
                <div class="card-title">🎥 مرئيات المنهج وتدريب الفيديو المسجّل</div>
                <div class="video-container">
                    <iframe src="{book_data["video"]}" title="Tactical Medical Stream Player" allowfullscreen></iframe>
                </div>
                <p style="font-size:12px; color:var(--text-secondary); text-align:center; margin-top:10px; margin-bottom: 0;">بث تكتيكي معتمد: التدريبات المصورة لقوى الحفاظ والأطباء الميدانيين.</p>
            </div>
            
            <div class="card">
                <div class="card-title">📈 تخطيط حركي للأنشطة الحيوية (ECG Live Visualizer)</div>
                <!-- Premium SVG Beat anim line -->
                <svg class="ecg-svg" width="100%" height="130" viewBox="0 0 500 130">
                    <defs>
                        <pattern id="grid_t" width="20" height="20" patternUnits="userSpaceOnUse">
                            <path d="M 20 0 L 0 0 0 20" fill="none" stroke="#0e1b2f" stroke-width="1"/>
                        </pattern>
                    </defs>
                    <rect width="100%" height="100%" fill="url(#grid_t)" />
                    
                    <!-- Dynamic pulsating human heart -->
                    <path class="heart-pulse" d="M 460 30 C 455 15, 435 15, 430 30 C 425 15, 405 15, 400 30 C 390 45, 410 70, 430 85 C 450 70, 470 45, 460 30 Z" fill="#e74c3c" opacity="0.65" />
                    
                    <path class="ecg-path" d="M 0,65 L 120,65 L 132,45 L 144,85 L 156,65 L 210,65 L 222,10 L 234,120 L 246,65 L 290,65 L 302,55 L 314,75 L 326,65 L 500,65" fill="none" stroke="#2ecc71" stroke-width="3" />
                </svg>
                <p style="font-size:12px; color:var(--text-secondary); text-align:center;">إشارة نبض حية تعكس عمليات الموازنة القلبية والتحقق الإكلينيكي تحت التوتر.</p>
            </div>
            
            <div class="card">
                <div class="card-title">🎯 الأهداف الأساسية وجدارة التعلم</div>
                <ul>
                    {"".join(f"<li>{obj}</li>" for obj in book_data["objectives"])}
                </ul>
            </div>
            
            <div class="card">
                <div class="card-title">📖 المبحث الأول: التأصيل السريري والفيزيولوجي</div>
                <p>{book_data["intro"]}</p>
                <p>تعتمد اللياقة العامة والنجاة الميدانية على التدريب المنضبط لمعالجة الصدمات ومنع المضاعفات الشرجية الوعائية اللاهوائية عبر حسن استباق الرعاية.</p>
            </div>
            
            <div class="card">
                <div class="card-title">🧪 المبحث الثاني: الأبعاد الطبية وصناعة الموازنة</div>
                <p>{book_data["ch1_txt"]}</p>
                <p>{book_data["ch2_txt"]}</p>
            </div>
            
            <!-- Quiz Card -->
            <div class="card">
                <div class="card-title">📝 اختبار جدارة تحصيل العلوم النظرية</div>
                <p style="color: var(--text-secondary); font-size:13px;">أجب بتركيز شديد وصدر قراراتك الطبية باحترافية لتسجيل جدارتك العلمية:</p>
                {mcq_rendered_t}
                <button class="quiz-submit" onclick="evaluateQuiz_t()">تقديم الإجابات وتقييم الجدارة</button>
                <div class="feedback-banner" id="fb_t"></div>
            </div>
            
            <div class="warning-strip">
                <div class="warning-title">🚨 توصية مجلس القيادة الطبية:</div>
                <p style="margin: 0; font-size:12.5px; color:#d1d5db;">إن استيعاب النظريات والفسيولوجيا المسرودة أعلاه شرط حاسم لمنح تصريح ممارسة التطبيق والبروتوكول العملي داخل ميادين القوى المسلحة والمستشفيات التكتيكية.</p>
            </div>
            
            <div class="footer-trademark">
                الأكاديمية الطبية العسكرية © {datetime.now().strftime("%Y")} م • وزارة الدفاع • الجمهورية اليمنية
            </div>
        </div>
        
        {quiz_code_maker(book_data["quiz"], "t")}
    </body>
    </html>
    """

    # ------------------
    # ⚙️ Render Practical Module (Lecture 2)
    # ------------------
    # Setup some slightly shifted practical quiz cards to fit combat triage decision actions
    prac_quizzes = [
        {"q": "تعرض المحيط لنيران غير مباشرة مع إصابة جندي يشكو من نزيف فخذ فادح تكتيكي، أين تطبق العاصبة أولاً؟", "a": ["تطبيق فوري في أعلى الأطراف فوق الملابس وتحت الساتر النيراني", "تضميد الجرح جراحياً بمجال مفتوح ومن ثم رصد النبض", "الحقن بالكيتامين والانتظار دون عاصبة لمنع الألم", "محاولة نقله أولاً دون عاصبة تفادياً للألم الحركي"], "correct": 0, "hint": "بروتوكول الرعاية تحت النار يملي البساطة والأتمتة لتفادي الوفاة السريعة النزفية."},
        {"q": "عند تطبيق عاصبة الشد الشرياني الميداني، كم من الوقت يمكن الاحتفاظ بها كحد أقصى آمن دون الإضرار الدائم بالأطراف؟", "a": ["ساعتان كحد أقصى (120 دقيقة)", "عشر ساعات ممتدة متواصلة دون فحص", "ثوانٍ معدودة فقط ثم تسريحها", "يترك للأطراف الأبد ولا تفك أبداً بالمستشفى الأمامي"], "correct": 0, "hint": "تتطلب الأطراف فحصاً من متخصص في غضون 20 دقيقة أو الإخلاء في غضون 120 دقيقة."}
    ]
    mcq_rendered_p = ""
    for idx, q_card in enumerate(prac_quizzes):
        options_html = "".join(f"""
            <div class="quiz-option" onclick="selectOption_p({idx}, {oidx})">
                <span style="font-weight: 700; color:var(--emerald-accent);">{"أبجد"[oidx]} .</span>
                <span>{opt}</span>
            </div>""" for oidx, opt in enumerate(q_card["a"]))
            
        mcq_rendered_p += f"""
        <div class="question-block" id="p_q{idx}" style="margin-top:20px; border-bottom:1px dashed rgba(255,255,255,0.05); padding-bottom:15px;">
            <p style="font-weight: 700; color:#f1f5f9;">{idx+1}. {q_card["q"]}</p>
            {options_html}
            <details style="margin-top:10px;">
                <summary style="font-size:12px; color:var(--text-secondary);">💡 تلميح للمساعدة في الحل العملي</summary>
                <div class="details-content" style="font-size:11.5px; color:#d1d5db;">
                    {q_card["hint"]}
                </div>
            </details>
        </div>"""

    html_practical = f"""<!DOCTYPE html>
    <html lang="ar">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>{title} - التدريب التطبيقي الميداني</title>
        {css_content}
    </head>
    <body>
        <div class="container">
            <div class="header-card" style="border-color: rgba(46, 204, 113, 0.3);">
                <span class="type-badge practical">تدريب عملي ومناورات تكتيكية — ورشة عمل</span>
                <h1>تطبيق وإجراءات: {title}</h1>
                <div class="metadata-row">
                    <span class="meta-item"><span class="meta-icon">👤</span>المقدم الطبيب/ د. مازن الخطيب</span>
                    <span class="meta-item"><span class="meta-icon">⏱️</span>60 دقيقة ورشة تدريب</span>
                    <span class="meta-item"><span class="meta-icon">🎖️</span>التحقق التفاعلي الميداني</span>
                </div>
            </div>
            
            <div class="card">
                <div class="card-title">🗺️ مراحل التحرك التكتيكي الطبي المشترك</div>
                <div class="stepper">
                    <div class="step active">1</div>
                    <div class="step">2</div>
                    <div class="step">3</div>
                    <div class="step">4</div>
                </div>
                <p style="font-size: 13px; color: var(--text-secondary); text-align: center; margin-bottom: 0;">الأطوار: 1. تأمين وحماية • 2. السيطرة وقطع النزف • 3. تأمين المجاري والموازنة • 4. نقل وتحريك وإخلاء.</p>
            </div>
            
            <div class="card">
                <div class="card-title" style="color:var(--emerald-accent);">🔺 هرم الفرز وتحديد الأولويات العسكرية (Triage Diagram)</div>
                <!-- Interactive Pure CSS vector medical pyramid chart -->
                <div style="display:flex; flex-direction:column; align-items:center; margin:15px 0;">
                    <div style="width:120px; height:0; border-bottom:30px solid #e74c3c; border-left:15px solid transparent; border-right:15px solid transparent; display:flex; align-items:center; justify-content:center; font-size:11px; font-weight:700; color:#000; text-align:center;">
                        أحمر (فوري مهدد بالحياة)
                    </div>
                    <div style="width:160px; height:0; border-bottom:30px solid #f1c40f; border-left:15px solid transparent; border-right:15px solid transparent; display:flex; align-items:center; justify-content:center; font-size:11px; font-weight:700; color:#000; margin-top:4px;">
                        أصفر (مستقر مؤجل لمتوسط)
                    </div>
                    <div style="width:200px; height:0; border-bottom:30px solid #2ecc71; border-left:15px solid transparent; border-right:15px solid transparent; display:flex; align-items:center; justify-content:center; font-size:11px; font-weight:700; color:#000; margin-top:4px;">
                        أخضر (بسيط يستجيب ذاتياً)
                    </div>
                </div>
                <p style="font-size:12px; color:var(--text-secondary); text-align:center; margin:0;">نموذج فرز المصابين والتحييد اللوجستي الفوري لحفظ الموارد في موقع الحدث.</p>
            </div>
            
            <div class="card">
                <div class="card-title">📖 المرحلة الأولى: التدبير والتحضير السريري بالميدان</div>
                <p>{book_data["ch3_txt"]}</p>
                <p>يجب على الفرد المسعف البدء بتأكيد ارتداء القفازات الواقية والتأهب بمساعدة الأطقم المحيطة، وتأكيد سلامة محيط السواتر والتكتم التام على توزع الفئات الطبية من نيران الأعداء.</p>
            </div>
            
            <div class="card">
                <div class="card-title">⚙️ المرحلة الثانية: آليات الجراحة والمعالجة التطبيقية</div>
                <p>{book_data["case_study"]}</p>
                <p>تثبيت الكسور والأجزاء الجلدية بطرق حنونة ودقيقة تمنع الخمج البكتيري المعقد والتهاب الأنسجة المجاورة، وتأكيد وضع تدوينات توقيتات العاصبة الشريانية المطبقة على جبهة الجريح بوضوح تام.</p>
            </div>
            
            <!-- Quiz Card -->
            <div class="card">
                <div class="card-title" style="color:var(--emerald-accent);">📝 اختبار الجدارة وتخاذ قرارات المناورة التكتيكية</div>
                <p style="color: var(--text-secondary); font-size:13px;">طابق سيناريو الخطر التالي وقدم الإجراء الأنسب لحفظ الحياة تحت الضغط:</p>
                {mcq_rendered_p}
                <button class="quiz-submit" style="background:linear-gradient(90deg, var(--emerald-accent), #27ae60);" onclick="evaluateQuiz_p()">تقديم الإجابات وتقييم مناورة ورشة العمل</button>
                <div class="feedback-banner" id="fb_p"></div>
            </div>
            
            <div class="warning-strip" style="border-color: rgba(231,76,60,0.4);">
                <div class="warning-title">🚨 تحذير صحي وعسكري فوري:</div>
                <p style="margin: 0; font-size:13px; font-weight:700; color:#ffeded;">تنفيذ الإجراء تحت النار يتوافق مع تدريبات مكثفة وصارمة للغاية. التراجع أو التخاذل في التطابق مع خطة السيطرة على الأضرار (Damage Control) قد يفضي لتهتك الأوعية والوفاة الفورية نتيجة الانهيار والتحلل الدموي.</p>
            </div>
            
            <div class="footer-trademark">
                الأكاديمية الطبية العسكرية © {datetime.now().strftime("%Y")} م • وزارة الدفاع • الجمهورية اليمنية
            </div>
        </div>
        
        {quiz_code_maker(prac_quizzes, "p")}
    </body>
    </html>
    """
    
    with open(theory_path, "w", encoding="utf-8") as ft:
        ft.write(html_theory)
        
    with open(practical_path, "w", encoding="utf-8") as fp:
        fp.write(html_practical)

# ==========================================
# 📊 Visual Progress Indicator Terminal
# ==========================================

def display_ascii_progress_hud(idx, count, current_title):
    """Draws terminal simulation HUD mimicking state of development control centers."""
    width = 30
    filled = int(width * (idx / count))
    unfilled = width - filled
    char_fill = "█"
    char_empty = "░"
    bar_str = char_fill * filled + char_empty * unfilled
    percentage = int((idx / count) * 100)
    
    sys.stdout.write(f"\r⚙️  [{bar_str}] {percentage:3d}% | جاري بناء: {current_title[:24]:<24} ")
    sys.stdout.flush()

# ==========================================
# 🚀 Orchestrator & Entry Point Command
# ==========================================

def main():
    print("""
    +-------------------------------------------------------------+
    |           🇨🇾   MID MEDIC ACADEMY CONTENTS FACTORY v3.0      |
    |      الأكاديمية الطبية العسكرية الميدانيّة - مولد الكتب والمحاضرات     |
    +-------------------------------------------------------------+
    """)
    
    # 0. Acquire Arabic Typography font to establish beautiful rendering in PDFs
    download_font_if_needed()
    
    # 1. Parse app configs maps JSON safely
    if not os.path.exists(INPUT_MAP_PATH):
        print(f"🚨 Path Error: Cannot locate general assets map source at: {INPUT_MAP_PATH}")
        print("🚨 Note: Ensure you run this python script from the root workspace directory.")
        sys.exit(1)
        
    print(f"📖 Loaded base app maps blueprint correctly from: {INPUT_MAP_PATH}")
    with open(INPUT_MAP_PATH, "r", encoding="utf-8") as file:
        app_json = json.load(file)
        
    original_books = app_json.get("books", [])
    if not original_books:
        print("🚨 Error: No textbooks or clinical courses specified inside the blueprints!")
        sys.exit(1)
        
    print(f"🧪 Successfully detected ({len(original_books)}) textbook slots. Initializing Asset Factory...")
    
    # 2. Build directories structures
    os.makedirs(BOOKS_DIR, exist_ok=True)
    os.makedirs(COVERS_DIR, exist_ok=True)
    os.makedirs(LECTURES_DIR, exist_ok=True)
    print(f"📁 Root directory constructed successfully: {OUTPUT_DIR}/")
    
    v3_books_list = []
    
    # 3. Iterate dynamically compiling resources
    for index, book in enumerate(original_books):
        title = book.get("title", f"مقرر دراسي {index+1}")
        sanitized_title = title.replace("/", "_").replace("\\", "_").replace(":", "").replace("*", "").replace("?", "").strip()
        
        # Load clinical topic specifics
        profile_data = get_clinical_data_for_title(title)
        
        # File paths configurations
        pdf_fn = f"{sanitized_title}.pdf"
        cover_fn = f"غلاف_{sanitized_title}.png"
        theory_html_fn = f"{sanitized_title}_theory.html"
        practical_html_fn = f"{sanitized_title}_practical.html"
        
        pdf_fpath = os.path.join(BOOKS_DIR, pdf_fn)
        cover_fpath = os.path.join(COVERS_DIR, cover_fn)
        theory_fpath = os.path.join(LECTURES_DIR, theory_html_fn)
        practical_fpath = os.path.join(LECTURES_DIR, practical_html_fn)
        
        # Draw outputs sequentially
        build_pdf_clinical_book(pdf_fpath, title, profile_data)
        draw_luxurious_png_cover(cover_fpath, title)
        write_interactive_html_lecture(theory_fpath, practical_fpath, title, profile_data)
        
        # Display progress logs
        display_ascii_progress_hud(index + 1, len(original_books), title)
        
        # Update app assets map properties
        modified_book = book.copy()
        modified_book["file"] = os.path.join("books", pdf_fn).replace("\\", "/")
        modified_book["cover_path"] = os.path.join("covers", cover_fn).replace("\\", "/")
        modified_book["lectureHtmlPath"] = os.path.join("lectures", theory_html_fn).replace("\\", "/")
        modified_book["lecturePracticalHtmlPath"] = os.path.join("lectures", practical_html_fn).replace("\\", "/")
        modified_book["directPdf"] = modified_book["file"] # Backwards compatibility for custom pdf viewers
        
        v3_books_list.append(modified_book)
        
    print("\n")
    print("📈 Compiling updated Assets map and writing app_assets_map_v3.json...")
    
    # Write revised V3 data blueprint mapping
    v3_final_json = app_json.copy()
    v3_final_json["version"] = "3.0"
    v3_final_json["books"] = v3_books_list
    
    output_map_path = os.path.join(OUTPUT_DIR, "app_assets_map_v3.json")
    with open(output_map_path, "w", encoding="utf-8") as fv3:
        json.dump(v3_final_json, fv3, ensure_ascii=False, indent=2)
        
    print("\n=============================================================")
    print("🏆 SUCCESS: The Premium Military Medical Content Factory Done!")
    print("-------------------------------------------------------------")
    print(f"📗 5-7 Page PDF Books compiled list: {BOOKS_DIR}/")
    print(f"🎬 Luxurious Graphic Cover images: {COVERS_DIR}/")
    print(f"🔬 Fully Interactive Theory & Pract Lectures: {LECTURES_DIR}/")
    print(f"🎯 Final Maps and Path Directives: {output_map_path}")
    print("=============================================================")
    print("   Ready for live testing on users' device terminals. Bravo! ")

if __name__ == "__main__":
    main()
