from pathlib import Path
import base64
import gzip

ROOT = Path(__file__).resolve().parents[1]
V6 = ROOT / "tools" / "v6"


def read_parts(*names: str) -> str:
    return "".join((V6 / name).read_text(encoding="utf-8").strip() for name in names)


def decode_b64(encoded: str) -> bytes:
    # GitHub text payloads may omit terminal '=' padding. Restore it safely.
    encoded = "".join(encoded.split())
    encoded += "=" * (-len(encoded) % 4)
    return base64.b64decode(encoded)


def write_gzip_b64(target: str, *parts: str):
    data = gzip.decompress(decode_b64(read_parts(*parts)))
    path = ROOT / target
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes(data)


def write_b64(target: str, encoded: str):
    path = ROOT / target
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes(decode_b64(encoded))


# Replace the app source deterministically. This avoids the fragile V5 string-patch build.
write_gzip_b64(
    "app/src/main/java/com/catools/templeapp/ui/TempleAppUi.kt",
    "ui_1.b64", "ui_2.b64"
)
write_gzip_b64(
    "app/src/main/java/com/catools/templeapp/data/TempleModels.kt",
    "models.b64"
)
write_gzip_b64(
    "app/src/main/java/com/catools/templeapp/data/TempleRepository.kt",
    "repository.b64"
)
write_gzip_b64("app/build.gradle.kts", "app_gradle.b64")

# Current user-provided temple photograph embedded in the APK for reliable offline display.
write_b64(
    "app/src/main/res/drawable-nodpi/temple_banner.jpg",
    read_parts("temple_new_1.b64", "temple_new_2.b64", "temple_new_3.b64", "temple_new_4.b64")
)

# Current Viharadhipathi portrait embedded in the APK for reliable offline display.
write_b64(
    "app/src/main/res/drawable-nodpi/viharaadhipathi_portrait.jpg",
    read_parts("monk_new_1.b64", "monk_new_2.b64")
)

# Remove the temporary V6 placeholder if present.
temp = ROOT / "app/src/main/java/com/catools/templeapp/ui/TempleAppUiV6.kt"
if temp.exists():
    temp.unlink()

print("Stable V6 source and current embedded images applied")
