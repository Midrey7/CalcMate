import os
from PIL import Image, ImageDraw, ImageFont

os.makedirs("screenshots", exist_ok=True)

W, H = 1080, 2400
FONT_BOLD = "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf"
FONT_REG = "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"

def get_font(path, size):
    try:
        return ImageFont.truetype(path, size)
    except:
        return ImageFont.load_default()

font_tag = get_font(FONT_BOLD, 36)
font_title = get_font(FONT_BOLD, 72)
font_sub = get_font(FONT_REG, 40)
font_display_expr = get_font(FONT_REG, 64)
font_display_res = get_font(FONT_BOLD, 120)
font_key = get_font(FONT_BOLD, 54)
font_card_title = get_font(FONT_BOLD, 46)
font_card_sub = get_font(FONT_REG, 34)

# Colors
BG_DARK = (11, 15, 25)
SURFACE = (18, 24, 38)
CARD_BG = (26, 34, 52)
BRAND_INDIGO = (99, 102, 241)
BRAND_CYAN = (0, 229, 255)
BRAND_ROSE = (244, 63, 94)
TEXT_WHITE = (248, 250, 252)
TEXT_GRAY = (148, 163, 184)
TEXT_MUTED = (100, 116, 139)

def draw_header(draw, tag_text, title_text, sub_text, tag_color=BRAND_CYAN):
    # Tag Pill
    tag_w = draw.textlength(tag_text, font=font_tag) + 40
    tag_box = [60, 120, 60 + tag_w, 180]
    draw.rounded_rectangle(tag_box, radius=30, fill=(tag_color[0]//5, tag_color[1]//5, tag_color[2]//5), outline=tag_color, width=2)
    draw.text((80, 132), tag_text, font=font_tag, fill=tag_color)
    
    # Title & Sub
    draw.text((60, 210), title_text, font=font_title, fill=TEXT_WHITE)
    draw.text((60, 310), sub_text, font=font_sub, fill=TEXT_GRAY)

# =========================================================================
# Screenshot 1: Home Screen / Toolbox Hub
# =========================================================================
im1 = Image.new("RGBA", (W, H), BG_DARK)
d1 = ImageDraw.Draw(im1)
draw_header(d1, "ALL-IN-ONE SUITE", "16+ Smart Tools\nIn One App", "From everyday math to advanced finance.", BRAND_CYAN)

# Search Bar
d1.rounded_rectangle([60, 480, 1020, 580], radius=28, fill=CARD_BG, outline=(45, 55, 75), width=2)
d1.text((100, 508), "🔍  Search calculators & converters...", font=font_card_sub, fill=TEXT_MUTED)

# Category Chips
chips = [("All", True), ("Finance", False), ("Math", False), ("Converters", False), ("Daily", False)]
cx = 60
for label, active in chips:
    cw = d1.textlength(label, font=font_tag) + 48
    fill = BRAND_INDIGO if active else CARD_BG
    text_c = TEXT_WHITE if active else TEXT_GRAY
    d1.rounded_rectangle([cx, 610, cx + cw, 680], radius=24, fill=fill)
    d1.text((cx + 24, 626), label, font=font_tag, fill=text_c)
    cx += cw + 20

# Featured Hero Card
d1.rounded_rectangle([60, 720, 1020, 960], radius=36, fill=(45, 48, 120), outline=BRAND_INDIGO, width=2)
d1.text((100, 760), "FEATURED TOOL", font=font_tag, fill=BRAND_CYAN)
d1.text((100, 810), "Smart Calculator", font=get_font(FONT_BOLD, 54), fill=TEXT_WHITE)
d1.text((100, 880), "Live evaluation, tactile keys & scientific tray", font=font_card_sub, fill=TEXT_GRAY)

# Tool Cards Grid
tools = [
    ("Loan & EMI Calculator", "Accurate monthly payoff breakdown", "💰", (16, 185, 129)),
    ("Unit Converter", "Length, weight, temperature & speed", "🔄", (59, 130, 246)),
    ("Compound Interest", "Plan investment growth over time", "📈", (168, 85, 247)),
    ("Tip & Split Bill", "Instant per-person shares & gratuity", "🧾", (245, 158, 11)),
    ("Age & Countdown", "Exact years, months & birthday alerts", "🎂", (244, 63, 94)),
    ("Digital Storage", "Bytes, MB, GB, TB in 1024 & 1000 base", "💾", (6, 182, 212))
]

ty = 1000
for name, desc, icon, color in tools:
    d1.rounded_rectangle([60, ty, 1020, ty + 180], radius=28, fill=SURFACE, outline=(35, 45, 65), width=2)
    # Icon Box
    d1.rounded_rectangle([90, ty + 35, 200, ty + 145], radius=22, fill=(color[0]//4, color[1]//4, color[2]//4))
    d1.text((120, ty + 50), icon, font=font_key, fill=TEXT_WHITE)
    # Text
    d1.text((230, ty + 45), name, font=font_card_title, fill=TEXT_WHITE)
    d1.text((230, ty + 105), desc, font=font_card_sub, fill=TEXT_GRAY)
    ty += 210

im1.save("screenshots/screenshot_1_home.png")

# =========================================================================
# Screenshot 2: Smart Calculator with Live Preview
# =========================================================================
im2 = Image.new("RGBA", (W, H), BG_DARK)
d2 = ImageDraw.Draw(im2)
draw_header(d2, "REAL-TIME PREVIEW", "Live Answers\nAs You Type", "No equals sign needed to see the result.", BRAND_INDIGO)

# Calculator Display Box
d2.rounded_rectangle([60, 480, 1020, 960], radius=36, fill=SURFACE, outline=(35, 45, 65), width=2)
# Mode Pills
d2.rounded_rectangle([90, 510, 240, 565], radius=16, fill=(35, 45, 65))
d2.text((115, 520), "DEG", font=font_tag, fill=BRAND_CYAN)
d2.text((180, 520), "RAD", font=font_tag, fill=TEXT_MUTED)

# Expression & Live Result
d2.text((100, 640), "(145 × 8.5) + 320", font=font_display_expr, fill=TEXT_GRAY)
d2.text((100, 740), "= 1,552.5", font=font_display_res, fill=TEXT_WHITE)

# Keypad
keys = [
    [("AC", BRAND_ROSE, CARD_BG), ("( )", TEXT_WHITE, CARD_BG), ("%", TEXT_WHITE, CARD_BG), ("÷", BRAND_CYAN, (28, 45, 75))],
    [("7", TEXT_WHITE, CARD_BG), ("8", TEXT_WHITE, CARD_BG), ("9", TEXT_WHITE, CARD_BG), ("×", BRAND_CYAN, (28, 45, 75))],
    [("4", TEXT_WHITE, CARD_BG), ("5", TEXT_WHITE, CARD_BG), ("6", TEXT_WHITE, CARD_BG), ("−", BRAND_CYAN, (28, 45, 75))],
    [("1", TEXT_WHITE, CARD_BG), ("2", TEXT_WHITE, CARD_BG), ("3", TEXT_WHITE, CARD_BG), ("+", BRAND_CYAN, (28, 45, 75))],
    [("+/-", TEXT_WHITE, CARD_BG), ("0", TEXT_WHITE, CARD_BG), (".", TEXT_WHITE, CARD_BG), ("=", TEXT_WHITE, BRAND_INDIGO)]
]

ky = 1020
for row in keys:
    kx = 60
    for label, text_c, bg_c in row:
        d2.rounded_rectangle([kx, ky, kx + 215, ky + 215], radius=32, fill=bg_c)
        lw = d2.textlength(label, font=font_key)
        d2.text((kx + (215 - lw)//2, ky + 75), label, font=font_key, fill=text_c)
        kx += 245
    ky += 245

im2.save("screenshots/screenshot_2_calculator.png")

# =========================================================================
# Screenshot 3: Scientific Calculator
# =========================================================================
im3 = Image.new("RGBA", (W, H), BG_DARK)
d3 = ImageDraw.Draw(im3)
draw_header(d3, "SCIENTIFIC POWER", "Complete Math\n& Functions", "Trig, logs, powers, roots, constants & more.", BRAND_CYAN)

# Display Box
d3.rounded_rectangle([60, 480, 1020, 880], radius=36, fill=SURFACE, outline=(35, 45, 65), width=2)
d3.text((100, 560), "sin(45°) + ln(e^2) × √64", font=get_font(FONT_REG, 52), fill=TEXT_GRAY)
d3.text((100, 680), "= 16.7071", font=font_display_res, fill=TEXT_WHITE)

# Scientific Tray Grid
sci_keys = [
    [("sin", BRAND_CYAN), ("cos", BRAND_CYAN), ("tan", BRAND_CYAN), ("ln", BRAND_CYAN), ("log", BRAND_CYAN)],
    [("x²", BRAND_CYAN), ("xʸ", BRAND_CYAN), ("√", BRAND_CYAN), ("π", (245, 158, 11)), ("e", (245, 158, 11))],
    [("INV", TEXT_MUTED), ("DEG", TEXT_MUTED), ("1/x", BRAND_CYAN), ("(", TEXT_WHITE), (")", TEXT_WHITE)]
]

sy = 940
for row in sci_keys:
    sx = 60
    for label, col in row:
        d3.rounded_rectangle([sx, sy, sx + 170, sy + 130], radius=24, fill=CARD_BG)
        lw = d3.textlength(label, font=font_card_title)
        d3.text((sx + (170 - lw)//2, sy + 38), label, font=font_card_title, fill=col)
        sx += 195
    sy += 155

# Standard Pad Below
std_rows = [
    [("7", TEXT_WHITE), ("8", TEXT_WHITE), ("9", TEXT_WHITE), ("÷", BRAND_CYAN)],
    [("4", TEXT_WHITE), ("5", TEXT_WHITE), ("6", TEXT_WHITE), ("×", BRAND_CYAN)],
    [("1", TEXT_WHITE), ("2", TEXT_WHITE), ("3", TEXT_WHITE), ("−", BRAND_CYAN)],
    [("0", TEXT_WHITE), (".", TEXT_WHITE), ("AC", BRAND_ROSE), ("=", TEXT_WHITE)]
]

ny = sy + 30
for row in std_rows:
    nx = 60
    for label, col in row:
        bg = BRAND_INDIGO if label == "=" else CARD_BG
        d3.rounded_rectangle([nx, ny, nx + 215, ny + 180], radius=28, fill=bg)
        lw = d3.textlength(label, font=font_key)
        d3.text((nx + (215 - lw)//2, ny + 55), label, font=font_key, fill=col)
        nx += 245
    ny += 205

im3.save("screenshots/screenshot_3_scientific.png")

# =========================================================================
# Screenshot 4: Finance & Conversion Tools
# =========================================================================
im4 = Image.new("RGBA", (W, H), BG_DARK)
d4 = ImageDraw.Draw(im4)
draw_header(d4, "FINANCE & TOOLS", "Loan, EMI & Bill\nSplit Calculators", "Make smart financial decisions in seconds.", (16, 185, 129))

# EMI Card
d4.rounded_rectangle([60, 480, 1020, 1140], radius=36, fill=SURFACE, outline=(35, 45, 65), width=2)
d4.text((100, 520), "LOAN & EMI CALCULATOR", font=font_tag, fill=(16, 185, 129))

# Inputs Display
inputs = [("Loan Amount", "$25,000"), ("Interest Rate", "6.5% Annual"), ("Loan Tenure", "36 Months (3 Yrs)")]
iy = 590
for lbl, val in inputs:
    d4.rounded_rectangle([100, iy, 980, iy + 110], radius=20, fill=CARD_BG)
    d4.text((130, iy + 35), lbl, font=font_card_sub, fill=TEXT_GRAY)
    d4.text((700, iy + 35), val, font=font_card_title, fill=TEXT_WHITE)
    iy += 130

# EMI Result Highlight Box
d4.rounded_rectangle([100, 1000, 980, 1100], radius=24, fill=(16//2, 185//4, 129//4), outline=(16, 185, 129), width=2)
d4.text((140, 1030), "Monthly Payment:", font=font_card_sub, fill=TEXT_WHITE)
d4.text((660, 1015), "$766.23", font=get_font(FONT_BOLD, 54), fill=(16, 185, 129))

# Tip Split Card
d4.rounded_rectangle([60, 1180, 1020, 1720], radius=36, fill=SURFACE, outline=(35, 45, 65), width=2)
d4.text((100, 1220), "TIP & BILL SPLITTER", font=font_tag, fill=(245, 158, 11))

bill_inputs = [("Bill Amount", "$140.00"), ("Tip Percentage", "18% ($25.20)"), ("Split Among", "4 People")]
by = 1290
for lbl, val in bill_inputs:
    d4.rounded_rectangle([100, by, 980, by + 100], radius=20, fill=CARD_BG)
    d4.text((130, by + 30), lbl, font=font_card_sub, fill=TEXT_GRAY)
    d4.text((680, by + 30), val, font=font_card_title, fill=TEXT_WHITE)
    by += 120

# Per Person Box
d4.rounded_rectangle([100, 1670, 980, 1790], radius=24, fill=(245//2, 158//4, 11//4), outline=(245, 158, 11), width=2)
d4.text((140, 1705), "Each Person Pays:", font=font_card_sub, fill=TEXT_WHITE)
d4.text((660, 1690), "$41.30", font=get_font(FONT_BOLD, 54), fill=(245, 158, 11))

# Age & Countdown Card
d4.rounded_rectangle([60, 1840, 1020, 2300], radius=36, fill=SURFACE, outline=(35, 45, 65), width=2)
d4.text((100, 1880), "EXACT AGE & COUNTDOWN", font=font_tag, fill=BRAND_ROSE)
d4.text((100, 1960), "24 Years, 8 Months, 12 Days", font=font_card_title, fill=TEXT_WHITE)
d4.text((100, 2030), "Total Days: 9,021 days lived", font=font_card_sub, fill=TEXT_GRAY)
d4.text((100, 2120), "🎉 Next Birthday in 112 Days!", font=get_font(FONT_BOLD, 42), fill=BRAND_CYAN)

im4.save("screenshots/screenshot_4_finance.png")

print("All 4 screenshots generated successfully in screenshots/")
