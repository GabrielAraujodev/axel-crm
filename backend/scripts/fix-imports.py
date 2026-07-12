"""Fix all imports after Auth Service extraction Phase 1.

Two issues:
1. Existing import statements pointing to old packages need replacement
2. Missing import statements for classes that were in the same package but are now in new packages

OLD SAME-PACKAGE CLASSES (no import needed, now moved):
  com.axelcrm.entity.BaseEntity     -> com.axelcrm.commons.entity.BaseEntity
  com.axelcrm.entity.Organization   -> com.axelcrm.commons.entity.Organization
  com.axelcrm.entity.User           -> com.axelcrm.auth.entity.User
  com.axelcrm.entity.enums.Role     -> com.axelcrm.commons.entity.enums.Role

Also handles FQN references and import path replacements.
"""
import os
import re

ROOT_SRC = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "src")

# Old import -> New import replacements (for existing import statements)
IMPORT_REPLACEMENTS = {
    "com.axelcrm.entity.BaseEntity": "com.axelcrm.commons.entity.BaseEntity",
    "com.axelcrm.entity.Organization": "com.axelcrm.commons.entity.Organization",
    "com.axelcrm.entity.User": "com.axelcrm.auth.entity.User",
    "com.axelcrm.entity.enums.Role": "com.axelcrm.commons.entity.enums.Role",
    "com.axelcrm.dto.UserResponse": "com.axelcrm.auth.dto.UserResponse",
    "com.axelcrm.dto.AuthRequest": "com.axelcrm.auth.dto.AuthRequest",
    "com.axelcrm.dto.RegisterRequest": "com.axelcrm.auth.dto.RegisterRequest",
    "com.axelcrm.dto.LoginResponse": "com.axelcrm.auth.dto.LoginResponse",
    "com.axelcrm.repository.UserRepository": "com.axelcrm.auth.repository.UserRepository",
    "com.axelcrm.repository.OrganizationRepository": "com.axelcrm.auth.repository.OrganizationRepository",
    "com.axelcrm.service.AuthService": "com.axelcrm.auth.service.AuthService",
    "com.axelcrm.service.UserService": "com.axelcrm.auth.service.UserService",
    "com.axelcrm.exception.ResourceNotFoundException": "com.axelcrm.commons.exception.ResourceNotFoundException",
    "com.axelcrm.exception.BadRequestException": "com.axelcrm.commons.exception.BadRequestException",
    "com.axelcrm.controller.UserController": "com.axelcrm.auth.controller.UserController",
    "com.axelcrm.controller.AuthController": "com.axelcrm.auth.controller.AuthController",
}

# FQN replacements (fully qualified names in method bodies)
FQN_REPLACEMENTS = {
    "com.axelcrm.security.TenantContext": "com.axelcrm.auth.security.TenantContext",
    "com.axelcrm.exception.BadRequestException": "com.axelcrm.commons.exception.BadRequestException",
    "com.axelcrm.exception.ResourceNotFoundException": "com.axelcrm.commons.exception.ResourceNotFoundException",
}

# Types that moved packages - their simple names and full new paths
# (these may need to be ADDED as imports if they're used but not imported)
MISSING_IMPORTS = {
    "BaseEntity": "com.axelcrm.commons.entity.BaseEntity",
    "Organization": "com.axelcrm.commons.entity.Organization",
    "User": "com.axelcrm.auth.entity.User",
    "Role": "com.axelcrm.commons.entity.enums.Role",
    "UserResponse": "com.axelcrm.auth.dto.UserResponse",
    "AuthRequest": "com.axelcrm.auth.dto.AuthRequest",
    "RegisterRequest": "com.axelcrm.auth.dto.RegisterRequest",
    "LoginResponse": "com.axelcrm.auth.dto.LoginResponse",
    "UserRepository": "com.axelcrm.auth.repository.UserRepository",
    "OrganizationRepository": "com.axelcrm.auth.repository.OrganizationRepository",
    "AuthService": "com.axelcrm.auth.service.AuthService",
    "UserService": "com.axelcrm.auth.service.UserService",
    "ResourceNotFoundException": "com.axelcrm.commons.exception.ResourceNotFoundException",
    "BadRequestException": "com.axelcrm.commons.exception.BadRequestException",
    "UserController": "com.axelcrm.auth.controller.UserController",
    "AuthController": "com.axelcrm.auth.controller.AuthController",
}

# Files in these directories should be EXCLUDED (they are new files that don't need fixing)
EXCLUDE_PREFIXES = [
    os.path.join(ROOT_SRC, "main", "java", "com", "axelcrm", "commons"),
    os.path.join(ROOT_SRC, "main", "java", "com", "axelcrm", "auth"),
]


def get_package(content: str) -> str:
    m = re.search(r"^package\s+([\w.]+);", content, re.MULTILINE)
    return m.group(1) if m else ""


def get_imports(content: str) -> set:
    return set(re.findall(r"^import\s+([\w.*]+);", content, re.MULTILINE))


def has_type_reference(content: str, type_name: str) -> bool:
    """Check if a type name is used as a reference (not just string/comment)."""
    # Match word boundary before and after
    pattern = r"(?<![.\w])" + re.escape(type_name) + r"(?![.\w])"
    return bool(re.search(pattern, content))


def add_missing_import(content: str, existing_imports: set) -> str:
    """Add imports for types that are used but not imported."""
    lines = content.split("\n")
    package_line_end = -1
    last_import_line = -1

    for i, line in enumerate(lines):
        if line.startswith("package ") and package_line_end < 0:
            package_line_end = i
        if line.startswith("import "):
            last_import_line = i

    for type_name, full_import in MISSING_IMPORTS.items():
        if full_import in existing_imports:
            continue
        # Check if this type is referenced but not imported
        if has_type_reference(content, type_name):
            # Don't add import if the type is in the same package as the file
            pkg = get_package(content)
            imp_pkg = ".".join(full_import.split(".")[:-1])
            if pkg == imp_pkg:
                continue
            # Don't add if it's already imported via wildcard
            wildcard_pkg = ".".join(full_import.split(".")[:-1]) + ".*"
            if wildcard_pkg in existing_imports:
                continue

            # Insert after the last import line, or after package line
            insert_at = last_import_line if last_import_line >= 0 else package_line_end
            indent = ""
            lines.insert(insert_at + 1, f"import {full_import};")
            last_import_line += 1
            content = "\n".join(lines)
            lines = content.split("\n")
            print(f"  + added import {full_import}")

    return content


def replace_imports(content: str) -> str:
    """Replace old import paths with new ones."""
    for old, new in IMPORT_REPLACEMENTS.items():
        old_import = f"import {old};"
        new_import = f"import {new};"
        if old_import in content:
            content = content.replace(old_import, new_import)
    # Fix wildcard security imports
    content = re.sub(r"import\s+com\.axelcrm\.security\.(\w+)\s*;", r"import com.axelcrm.auth.security.\1;", content)
    return content


def replace_fqn(content: str) -> str:
    """Replace FQN references in method bodies."""
    for old, new in FQN_REPLACEMENTS.items():
        content = content.replace(old, new)
    return content


def process_file(filepath: str) -> bool:
    rel = os.path.relpath(filepath, ROOT_SRC)
    for prefix in EXCLUDE_PREFIXES:
        if filepath.startswith(prefix):
            return False

    with open(filepath, "r", encoding="utf-8") as f:
        content = f.read()

    original = content

    content = replace_imports(content)
    content = replace_fqn(content)

    existing = get_imports(content)
    needs_imports = True

    content = add_missing_import(content, existing)

    if content != original:
        with open(filepath, "w", encoding="utf-8") as f:
            f.write(content)
        print(f"  FIXED: {rel}")
        return True
    return False


def main():
    print("Scanning for Java files...")
    fixed = 0
    total = 0
    for dirpath, dirnames, filenames in os.walk(ROOT_SRC):
        for fn in filenames:
            if fn.endswith(".java"):
                total += 1
                if process_file(os.path.join(dirpath, fn)):
                    fixed += 1

    print(f"\nTotal files: {total}")
    print(f"Fixed: {fixed}")


if __name__ == "__main__":
    main()
