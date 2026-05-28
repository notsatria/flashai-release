# DESIGN.md — AI Flashcard App
**Workshop: Build & Launch Android App with Kotlin + AI**
**Version:** 2.0 — Colorful & Playful Design System

> Dokumen ini mendefinisikan custom design system, komponen UI, dan panduan implementasi Jetpack Compose. Gaya desain: **colorful & playful** terinspirasi Quizlet + Duolingo — bukan Material3 default.

---

## 1. Prinsip Desain

- **Playful tapi fokus:** Warna cerah dan rounded corners yang bold, tapi tidak mengorbankan keterbacaan
- **Karakter visual:** Setiap screen punya identitas warna sendiri (deck punya warna unik)
- **Micro-interaction:** Animasi kecil yang memberikan feedback langsung — tap, swipe, generate
- **Depth:** Gunakan shadow dan layering untuk menciptakan kedalaman visual, bukan flat sepenuhnya
- **Custom, bukan default:** Hindari tampilan Material3 generik — setiap komponen punya gaya sendiri

---

## 2. Color Palette

### Brand Colors
```kotlin
object FlashColors {
    // Primary — Indigo
    val Indigo50  = Color(0xFFEEF2FF)
    val Indigo100 = Color(0xFFE0E7FF)
    val Indigo300 = Color(0xFFA5B4FC)
    val Indigo500 = Color(0xFF6366F1)  // Primary action
    val Indigo600 = Color(0xFF4F46E5)  // Primary pressed
    val Indigo700 = Color(0xFF4338CA)  // Dark accent

    // Secondary — Cyan (untuk AI feature)
    val Cyan400   = Color(0xFF22D3EE)
    val Cyan500   = Color(0xFF06B6D4)

    // Accent — Amber (untuk highlight / streak)
    val Amber400  = Color(0xFFFBBF24)
    val Amber500  = Color(0xFFF59E0B)

    // Deck Colors — setiap deck dapat warna unik
    val DeckBlue    = Color(0xFF6366F1)
    val DeckPurple  = Color(0xFFA855F7)
    val DeckPink    = Color(0xFFEC4899)
    val DeckGreen   = Color(0xFF22C55E)
    val DeckOrange  = Color(0xFFF97316)
    val DeckTeal    = Color(0xFF14B8A6)

    // Neutral
    val Gray50   = Color(0xFFF9FAFB)
    val Gray100  = Color(0xFFF3F4F6)
    val Gray200  = Color(0xFFE5E7EB)
    val Gray400  = Color(0xFF9CA3AF)
    val Gray600  = Color(0xFF4B5563)
    val Gray900  = Color(0xFF111827)

    // Background
    val Background = Color(0xFFF5F7FF)  // Slightly blue-tinted white
    val Surface    = Color(0xFFFFFFFF)
}
```

### Deck Color List (untuk assign otomatis)
```kotlin
val deckColorPalette = listOf(
    FlashColors.DeckBlue,
    FlashColors.DeckPurple,
    FlashColors.DeckPink,
    FlashColors.DeckGreen,
    FlashColors.DeckOrange,
    FlashColors.DeckTeal
)

fun getDeckColor(index: Int): Color = deckColorPalette[index % deckColorPalette.size]
```

---

## 3. Typography

Gunakan **Nunito** — rounded, friendly, dan playful. Download via Google Fonts.

```kotlin
// Type.kt
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

val NunitoFamily = FontFamily(
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_semibold, FontWeight.SemiBold),
    Font(R.font.nunito_bold, FontWeight.Bold),
    Font(R.font.nunito_extrabold, FontWeight.ExtraBold)
)

val FlashTypography = Typography(
    displayLarge  = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp),
    titleLarge    = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp),
    titleMedium   = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp),
    bodyLarge     = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    bodyMedium    = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    labelLarge    = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp),
    labelMedium   = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
)
```

> Cara pasang font: taruh file .ttf di `res/font/` — `nunito_regular.ttf`, `nunito_semibold.ttf`, `nunito_bold.ttf`, `nunito_extrabold.ttf`

---

## 4. Spacing, Shape & Shadow

```kotlin
object FlashSpacing {
    val xs  = 4.dp
    val sm  = 8.dp
    val md  = 16.dp
    val lg  = 24.dp
    val xl  = 32.dp
    val xxl = 48.dp
}

object FlashShape {
    val small  = RoundedCornerShape(8.dp)
    val medium = RoundedCornerShape(16.dp)
    val large  = RoundedCornerShape(24.dp)
    val full   = RoundedCornerShape(50)   // pill shape untuk chip/badge
}
```

### Shadow Custom
```kotlin
fun Modifier.flashShadow(
    color: Color = Color(0x1A6366F1),
    borderRadius: Dp = 16.dp,
    blurRadius: Dp = 12.dp,
    offsetY: Dp = 4.dp
) = this.shadow(
    elevation = blurRadius,
    shape = RoundedCornerShape(borderRadius),
    ambientColor = color,
    spotColor = color
)
```

---

## 5. Theme

```kotlin
// Theme.kt
@Composable
fun FlashcardTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary          = FlashColors.Indigo500,
        onPrimary        = Color.White,
        primaryContainer = FlashColors.Indigo100,
        secondary        = FlashColors.Cyan500,
        background       = FlashColors.Background,
        surface          = FlashColors.Surface,
        onBackground     = FlashColors.Gray900,
        onSurface        = FlashColors.Gray900,
    )
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = FlashTypography,
        content     = content
    )
}
```

---

## 6. Komponen UI

---

### 6.1 FlashButton — Primary Button

Tombol dengan rounded penuh, shadow colorful, dan scale animation saat di-tap.

```kotlin
@Composable
fun FlashButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = FlashColors.Indigo500,
    isLoading: Boolean = false
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(FlashShape.full)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(color, color.copy(alpha = 0.85f))
                )
            )
            .shadow(8.dp, FlashShape.full, ambientColor = color, spotColor = color)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!isLoading) onClick()
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                    }
                )
            }
            .padding(horizontal = FlashSpacing.lg, vertical = FlashSpacing.md),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text(text, style = FlashTypography.labelLarge, color = Color.White)
        }
    }
}
```

---

### 6.2 DeckCard — Colorful dengan Emoji Icon

```kotlin
@Composable
fun DeckCard(
    deck: Deck,
    deckIndex: Int,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val deckColor = getDeckColor(deckIndex)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .flashShadow(color = deckColor.copy(alpha = 0.25f)),
        shape = FlashShape.large,
        colors = CardDefaults.cardColors(containerColor = FlashColors.Surface)
    ) {
        Row(
            modifier = Modifier.padding(FlashSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color badge / icon box
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(FlashShape.medium)
                    .background(deckColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text("📚", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(FlashSpacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    deck.name,
                    style = FlashTypography.titleMedium,
                    color = FlashColors.Gray900
                )
                Spacer(modifier = Modifier.height(FlashSpacing.xs))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(FlashShape.full)
                            .background(deckColor.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            "${deck.cardCount} kartu",
                            style = FlashTypography.labelMedium,
                            color = deckColor
                        )
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = FlashColors.Gray400
                )
            }
        }
    }
}
```

---

### 6.3 FlipCard — Study Mode

```kotlin
@Composable
fun FlipCard(
    question: String,
    answer: String,
    deckColor: Color = FlashColors.Indigo500
) {
    var isFlipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .graphicsLayer { rotationY = rotation; cameraDistance = 12f * density }
            .clip(FlashShape.large)
            .background(
                brush = if (rotation <= 90f)
                    Brush.linearGradient(
                        colors = listOf(deckColor, deckColor.copy(alpha = 0.75f)),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                else
                    Brush.linearGradient(
                        colors = listOf(FlashColors.Indigo100, FlashColors.Surface),
                    )
            )
            .clickable { isFlipped = !isFlipped }
            .padding(FlashSpacing.lg),
        contentAlignment = Alignment.Center
    ) {
        if (rotation <= 90f) {
            // Front — Pertanyaan
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("❓", fontSize = 32.sp)
                Spacer(modifier = Modifier.height(FlashSpacing.md))
                Text(
                    question,
                    style = FlashTypography.titleMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(FlashSpacing.lg))
                Text(
                    "Tap untuk lihat jawaban",
                    style = FlashTypography.labelMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        } else {
            // Back — Jawaban
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer { rotationY = 180f }
            ) {
                Text("✅", fontSize = 32.sp)
                Spacer(modifier = Modifier.height(FlashSpacing.md))
                Text(
                    answer,
                    style = FlashTypography.bodyLarge,
                    color = FlashColors.Gray900,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
```

---

### 6.4 CardItem — List di DeckDetail

```kotlin
@Composable
fun CardItem(
    card: FlashCard,
    deckColor: Color,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = FlashShape.medium,
        colors = CardDefaults.cardColors(containerColor = FlashColors.Surface),
        border = BorderStroke(1.dp, FlashColors.Gray100)
    ) {
        Row(modifier = Modifier.padding(FlashSpacing.md)) {
            // Left accent bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .clip(FlashShape.full)
                    .background(deckColor)
            )
            Spacer(modifier = Modifier.width(FlashSpacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    card.question,
                    style = FlashTypography.bodyLarge,
                    color = FlashColors.Gray900,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(FlashSpacing.xs))
                Text(
                    card.answer,
                    style = FlashTypography.bodyMedium,
                    color = FlashColors.Gray400,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = FlashColors.Gray400)
            }
        }
    }
}
```

---

### 6.5 AIGenerateButton — Special Button

Tombol khusus untuk fitur AI dengan gradient cyan-indigo dan sparkle icon.

```kotlin
@Composable
fun AIGenerateButton(
    onClick: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(FlashShape.full)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(FlashColors.Cyan500, FlashColors.Indigo500)
                )
            )
            .clickable { if (!isLoading) onClick() }
            .padding(horizontal = FlashSpacing.lg, vertical = FlashSpacing.md),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FlashSpacing.sm)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                Text("Generating...", style = FlashTypography.labelLarge, color = Color.White)
            } else {
                Text("✨", fontSize = 16.sp)
                Text("Generate dengan AI", style = FlashTypography.labelLarge, color = Color.White)
            }
        }
    }
}
```

---

## 7. Screen Layouts

---

### 7.1 HomeScreen

```
┌─────────────────────────────────┐
│  Background: #F5F7FF            │
│                                 │
│  Halo, Selamat Belajar! 👋      │  ← displayLarge, Gray900
│  Kamu punya 3 deck aktif        │  ← bodyMedium, Gray400
│                                 │
│  ┌─────────────────────────┐    │
│  │ 📚  Kotlin Basics       │    │  ← DeckCard (warna: Indigo)
│  │      12 kartu  [badge]  │    │
│  └─────────────────────────┘    │
│                                 │
│  ┌─────────────────────────┐    │
│  │ 📚  Design Patterns     │    │  ← DeckCard (warna: Purple)
│  │      8 kartu   [badge]  │    │
│  └─────────────────────────┘    │
│                                 │
│              ╭──────────────╮   │
│              │  + Deck Baru │   │  ← FAB: gradient Indigo, pill shape
│              ╰──────────────╯   │
└─────────────────────────────────┘
```

---

### 7.2 DeckDetailScreen

```
┌─────────────────────────────────┐
│  ╔═══════════════════════════╗  │
│  ║  Header gradient (warna   ║  │  ← gradient dari deck color
│  ║  deck)                    ║  │
│  ║  ← Kotlin Basics    [▶]  ║  │  ← teks putih
│  ║  12 kartu                 ║  │
│  ╚═══════════════════════════╝  │
│                                 │
│  ┌──────── CardItem ──────────┐ │
│  │ ▌  Apa itu coroutine?      │ │  ← left accent bar warna deck
│  │    Fungsi yang dapat...    │ │
│  └────────────────────────────┘ │
│                                 │
│  ┌──────── CardItem ──────────┐ │
│  │ ▌  Apa itu sealed class?   │ │
│  │    Class yang...           │ │
│  └────────────────────────────┘ │
│                                 │
│  [✨ Generate AI] [+ Tambah]    │  ← sticky bottom bar
└─────────────────────────────────┘
```

---

### 7.3 StudyModeScreen

```
┌─────────────────────────────────┐
│  ← Kotlin Basics                │
│                                 │
│  Progress: ━━━━━━━━░░░░  7/12  │  ← LinearProgressIndicator, warna deck
│                                 │
│  ┌─────────────────────────┐    │
│  │  gradient deck color    │    │  ← FlipCard
│  │                         │    │
│  │    ❓                   │    │
│  │  Apa itu coroutine      │    │
│  │  di Kotlin?             │    │
│  │                         │    │
│  │  Tap untuk lihat jawaban│    │
│  └─────────────────────────┘    │
│                                 │
│  [  ← Prev  ]    [  Next →  ]  │  ← dua tombol outlined
└─────────────────────────────────┘
```

**Progress bar:**
```kotlin
LinearProgressIndicator(
    progress = { currentIndex.toFloat() / totalCards.toFloat() },
    modifier = Modifier
        .fillMaxWidth()
        .height(8.dp)
        .clip(FlashShape.full),
    color = deckColor,
    trackColor = deckColor.copy(alpha = 0.2f)
)
```

---

### 7.4 GenerateAIScreen

```
┌─────────────────────────────────┐
│  ← Generate dengan AI  ✨       │
│                                 │
│  ┌───────────────────────────┐  │
│  │ 🔍  Masukkan topik...     │  │  ← custom styled TextField
│  └───────────────────────────┘  │
│                                 │
│  Jumlah kartu:                  │
│  ╭─────╮  ╭──────╮             │
│  │  5  │  │  10  │             │  ← pill-shaped FilterChip, warna Indigo
│  ╰─────╯  ╰──────╯             │
│                                 │
│  ╭─────────────────────────────╮│
│  │ ✨  Generate dengan AI      ││  ← AIGenerateButton (cyan→indigo gradient)
│  ╰─────────────────────────────╯│
│                                 │
│  ── Hasil (3 kartu) ──          │
│  ┌──────── CardItem ──────────┐ │
│  │ ▌  Q: ...                  │ │
│  │    A: ...                  │ │
│  └────────────────────────────┘ │
│                                 │
│  [  Simpan Semua ke Deck  ]     │  ← FlashButton Indigo
└─────────────────────────────────┘
```

**Custom TextField:**
```kotlin
@Composable
fun FlashTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(FlashShape.medium)
            .background(FlashColors.Gray100)
            .padding(FlashSpacing.md),
        textStyle = FlashTypography.bodyLarge.copy(color = FlashColors.Gray900),
        minLines = minLines,
        decorationBox = { inner ->
            if (value.isEmpty()) {
                Text(placeholder, style = FlashTypography.bodyLarge, color = FlashColors.Gray400)
            }
            inner()
        }
    )
}
```

---

## 8. Navigation, Package Structure & Dependencies

*(Sama dengan versi sebelumnya — tidak ada perubahan)*

Lihat bagian 6, 7, 8 di versi DESIGN v1.0 untuk referensi navigation sealed class, package structure, dan Gradle dependencies.

Tambahan dependency untuk font:
```kotlin
// Tidak perlu library tambahan — taruh file .ttf di res/font/
// Download Nunito dari: https://fonts.google.com/specimen/Nunito
```

---

## 9. Gemini API — Prompt Template

*(Sama dengan versi sebelumnya)*

```kotlin
suspend fun generateFlashcards(topic: String, count: Int): List<FlashCard> {
    val prompt = """
        Buatkan $count flashcard untuk topik "$topic".
        Format respons HARUS dalam JSON array seperti ini, tanpa teks tambahan:
        [
          {"question": "...", "answer": "..."},
          {"question": "...", "answer": "..."}
        ]
        Gunakan Bahasa Indonesia. Jawaban harus singkat dan padat, maksimal 2 kalimat.
    """.trimIndent()
}
```

---

## 10. Checklist Prebuilt

- [ ] File font Nunito dengan google font
- [ ] `FlashColors`, `FlashTypography`, `FlashShape`, `FlashSpacing` sudah terdefinisi di `theme/`
- [ ] `FlashButton`, `AIGenerateButton`, `FlashTextField` sudah jadi di `components/`
- [ ] `DeckCard` dengan color assignment sudah jadi
- [ ] `FlipCard` dengan animasi flip sudah jadi dan tested
- [ ] `CardItem` dengan left accent bar sudah jadi
- [ ] Header gradient di DeckDetailScreen sudah jadi
- [ ] Progress bar di StudyModeScreen sudah jadi
- [ ] Project setup + Gradle dependencies berjalan tanpa error
- [ ] Firebase project terhubung (google-services.json)
- [ ] Firestore rules: allow read/write untuk anonymous user
- [ ] GeminiService: generate + parse JSON — sudah tested
- [ ] Checkpoint branch GitHub per segmen workshop sudah dibuat