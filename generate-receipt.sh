#!/bin/bash

# ==============================================================================
# Build Receipt Generator
# Creates a snapshot of code + build output for debugging and archiving.
# ==============================================================================

# 1. Setup Directory and Filename
RECEIPTS_DIR="receipts"
mkdir -p "$RECEIPTS_DIR"

TIMESTAMP=$(date +"%Y-%m-%d_%H-%M-%S")
OUTPUT_FILE="${RECEIPTS_DIR}/build_receipt_${TIMESTAMP}.md"

echo "Generating Build Receipt: $OUTPUT_FILE"

# --- 2. Header & Metadata ---
{
    echo "# Build Receipt"
    echo "**Date:** $(date)"
    echo "**System:** $(uname -sr)"

    if command -v git &> /dev/null && git rev-parse --is-inside-work-tree &> /dev/null; then
        echo "**Git Branch:** \`$(git rev-parse --abbrev-ref HEAD)\`"
        echo "**Git Commit:** \`$(git rev-parse HEAD)\`"
        echo "**Git Status:**"
        echo "\`\`\`"
        git status --short
        echo "\`\`\`"
    fi
    echo "---"
} > "$OUTPUT_FILE"

# --- 3. Project Structure ---
{
    echo "## Project Structure"
    echo "\`\`\`text"
    if command -v tree &> /dev/null; then
        tree -I "target|.git|.idea|receipts" # Added 'receipts' to ignore list so it doesn't list itself
    else
        find . -maxdepth 4 -not -path '*/.*' -not -path './target*' -not -path './receipts*' | sort
    fi
    echo "\`\`\`"
    echo "---"
} >> "$OUTPUT_FILE"

# --- 4. Configuration Files ---
echo "Snapshotting pom.xml..."
{
    echo "## Configuration: pom.xml"
    echo "\`\`\`xml"
    cat pom.xml
    echo "" # ensure newline
    echo "\`\`\`"
    echo "---"
} >> "$OUTPUT_FILE"

# --- 5. Source Code ---
echo "Snapshotting source code..."
echo "## Source Code" >> "$OUTPUT_FILE"

# Find all files in src, ignoring binaries/hidden
find src -type f -not -path '*/.*' | sort | while read -r file; do
    echo "Processing $file..."

    # Determine syntax highlighting based on extension
    ext="${file##*.}"
    case "$ext" in
        java) lang="java" ;;
        xml)  lang="xml" ;;
        properties) lang="properties" ;;
        yml|yaml) lang="yaml" ;;
        sh)   lang="bash" ;;
        *)    lang="" ;;
    esac

    {
        echo "### File: \`$file\`"
        echo "\`\`\`$lang"
        cat "$file"
        echo "" # ensure newline
        echo "\`\`\`"
        echo ""
    } >> "$OUTPUT_FILE"
done

# --- 6. Build Execution ---
echo "Running Maven Build..."
{
    echo "---"
    echo "## Build Output"
    echo "\`\`\`text"
} >> "$OUTPUT_FILE"

# Run Maven with filtering for the "Unsafe" warnings
# 1. -B: Batch mode
# 2. -e: Errors
# 3. grep -vE: Removes the "Unsafe" and "Guice" noise lines
mvn -B -e clean verify 2>&1 \
    | grep -vE "sun.misc.Unsafe|HiddenClassDefiner|com.google.inject.internal.aop" \
    | tee -a "$OUTPUT_FILE"

# Close the code block
echo "\`\`\`" >> "$OUTPUT_FILE"

echo "------------------------------------------------------------------------"
echo "Receipt Complete: $OUTPUT_FILE"