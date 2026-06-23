#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
============================================================
              ⚡ COVER THUMBNAIL GENERATOR ⚡
        مُولِّد الصور المصغرة لأغلفة الكتب الطبية العسكرية
============================================================
"""

import os
import sys

def install_and_import(package_name, import_name=None):
    if import_name is None:
        import_name = package_name
    try:
        __import__(import_name)
    except ImportError:
        import subprocess
        print(f"📦 Package '{package_name}' is missing. Installing dynamically...")
        subprocess.check_call([sys.executable, "-m", "pip", "install", package_name])

# Ensure Pillow (PIL) is installed
install_and_import("Pillow", "PIL")

from PIL import Image

# Directories configurations
BASE_DIR = "MidApp_Library"
COVERS_DIR = os.path.join(BASE_DIR, "covers")
TEMP_COVERS_DIR = os.path.join(BASE_DIR, "temp_covers")

def main():
    print("""
    +-------------------------------------------------------------+
    |         ⚡  MEDICAL LIBRARY THUMBNAIL CACHE FACTORY  ⚡       |
    |          مُنشئ التخزين المؤقت للأغلفة - أداء فائق السرعة          |
    +-------------------------------------------------------------+
    """)

    if not os.path.exists(COVERS_DIR):
        print(f"🚨 Path Error: Cannot locate covers folder at: {COVERS_DIR}")
        print("🚨 Please make sure to run 'generate_assets_v4.py' first to produce covers.")
        sys.exit(1)

    os.makedirs(TEMP_COVERS_DIR, exist_ok=True)
    print(f"📁 Destination directory verified: {TEMP_COVERS_DIR}/")

    # Read all png/jpg cover files
    all_files = [f for f in os.listdir(COVERS_DIR) if f.lower().endswith(('.png', '.jpg', '.jpeg'))]
    if not all_files:
        print(f"ℹ️ No cover images found inside: {COVERS_DIR}")
        sys.exit(0)

    print(f"🧪 Found ({len(all_files)}) cover image files. Generating thumbnails...")

    # Determine Pillow Resampling filter safely (compatibility across different Pillow versions)
    try:
        resample_filter = Image.Resampling.LANCZOS
    except AttributeError:
        try:
            resample_filter = Image.LANCZOS
        except AttributeError:
            resample_filter = Image.ANTIALIAS

    success_count = 0
    for idx, filename in enumerate(all_files, 1):
        src_path = os.path.join(COVERS_DIR, filename)
        dest_path = os.path.join(TEMP_COVERS_DIR, filename)

        try:
            with Image.open(src_path) as img:
                # Target width: 50px, scale height proportionally to maintain aspect ratio
                orig_width, orig_height = img.size
                if orig_width > 0:
                    aspect_ratio = orig_height / orig_width
                    target_width = 50
                    target_height = int(target_width * aspect_ratio)
                    if target_height <= 0:
                        target_height = 1
                    
                    # Generate high quality thumbnail
                    thumb = img.resize((target_width, target_height), resample=resample_filter)
                    
                    # Convert to RGB if saving as JPEG or if PNG has alpha and we want transparency preserved
                    thumb.save(dest_path, optimize=True)
                    success_count += 1
                    
                    # Quick progress report
                    print(f"   [{idx}/{len(all_files)}] Generated thumbnail for: {filename} ({target_width}x{target_height})")
        except Exception as e:
            print(f"   ❌ Failed to process cover '{filename}': {e}")

    print("\n=============================================================")
    print(f"🏆 SUCCESS: Completed thumbnail generation! ({success_count}/{len(all_files)}) processed.")
    print(f"🎯 Cached thumbnails location: {TEMP_COVERS_DIR}/")
    print("=============================================================")

if __name__ == "__main__":
    main()
