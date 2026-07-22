package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.filled.Palette
import com.example.ui.theme.AppThemeStyle
import com.example.ui.theme.AppThemeMode
import com.example.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.WaterLog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.sin

// Representation of a Water Container Option
data class ContainerOption(
    val id: String,
    val name: String,
    val capacityMl: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val description: String
)

@Composable
fun WaterTrackerScreen(
    viewModel: WaterViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCustomDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    val currentThemeStyle by viewModel.themeStyle.collectAsStateWithLifecycle()
    val currentThemeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    var selectedContainerId by remember { mutableStateOf("glass") }
    
    // Rotating advice tips state
    val tips = remember {
        listOf(
            "Boire de l'eau le matin active vos organes internes et élimine les toxines.",
            "N'attendez pas d'avoir soif pour boire, la soif est un signe de déshydratation légère.",
            "Une bonne hydratation améliore la concentration, l'énergie et la clarté d'esprit.",
            "Boire un verre d'eau 30 minutes avant le repas facilite grandement la digestion.",
            "L'eau lubrifie vos articulations et aide à protéger vos tissus sensibles."
        )
    }
    var currentTipIndex by remember { mutableIntStateOf(0) }
    
    // Auto-advance tips periodically
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(12000)
            currentTipIndex = (currentTipIndex + 1) % tips.size
        }
    }

    val containers = listOf(
        ContainerOption("glass", "Verre d'eau", 250, Icons.Default.LocalDrink, "Standard"),
        ContainerOption("bottle_small", "Bouteille", 500, Icons.Default.LocalDrink, "Individuelle"),
        ContainerOption("bottle_large", "Gd. Bouteille", 1500, Icons.Default.WaterDrop, "Format familial")
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentWindowInsets = WindowInsets.navigationBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            HeaderSection(
                onReset = { viewModel.resetIntake() },
                onThemeClick = { showThemeDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Quick Overview Card
            StatsOverviewRow(
                totalIntake = uiState.totalIntake,
                dailyGoal = uiState.dailyGoal,
                progress = uiState.progress
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Main Hydration Circle Card with Wave Water effect
            HydrationWaveCard(
                progress = uiState.progress,
                totalIntake = uiState.totalIntake,
                dailyGoal = uiState.dailyGoal
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Health Hydration Tip Card
            AdviceTipCard(
                tip = tips[currentTipIndex],
                onClickNext = { currentTipIndex = (currentTipIndex + 1) % tips.size }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Water Preset Increments Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Choisir votre récipient",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Text(
                    text = "Perso. ⚙️",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showCustomDialog = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Horizontally Scrollable Container Preset Picker
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(containers) { container ->
                    val isSelected = selectedContainerId == container.id
                    ContainerPickerItem(
                        container = container,
                        isSelected = isSelected,
                        onSelect = {
                            selectedContainerId = container.id
                            viewModel.addWater(container.capacityMl)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Today's Logs Title / Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mon Journal de Boisson",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                if (uiState.logs.isNotEmpty()) {
                    Text(
                        text = "${uiState.logs.size} prise(s) d'eau",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // List of Logs
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (uiState.logs.isEmpty()) {
                    EmptyHistoryView()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = uiState.logs,
                            key = { it.id }
                        ) { log ->
                            WaterLogItem(
                                log = log,
                                onDelete = { viewModel.removeWaterLog(log) }
                            )
                        }
                    }
                }
            }
        }

        // Custom Amount Dialog
        if (showCustomDialog) {
            CustomAmountDialog(
                onDismiss = { showCustomDialog = false },
                onConfirm = { amount ->
                    viewModel.addWater(amount)
                    showCustomDialog = false
                }
            )
        }

        // Theme Selection Dialog
        if (showThemeDialog) {
            ThemeSelectionDialog(
                currentStyle = currentThemeStyle,
                currentMode = currentThemeMode,
                onStyleSelect = { viewModel.setThemeStyle(it) },
                onModeSelect = { viewModel.setThemeMode(it) },
                onDismiss = { showThemeDialog = false }
            )
        }
    }
}

@Composable
fun HeaderSection(
    onReset: () -> Unit,
    onThemeClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "HydroTrack",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )
            )
            Text(
                text = "Optimisez votre santé goutte à goutte",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
                )
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Theme Switcher Button
            IconButton(
                onClick = onThemeClick,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                        shape = CircleShape
                    )
                    .testTag("theme_button"),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Changer le thème",
                    modifier = Modifier.size(22.dp)
                )
            }

            // Beautiful Action Layout (Reset Quick Button with dynamic hover appearance)
            IconButton(
                onClick = onReset,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                        shape = CircleShape
                    )
                    .testTag("reset_button"),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = "Réinitialiser les données",
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun StatsOverviewRow(
    totalIntake: Int,
    dailyGoal: Int,
    progress: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stat 1: Target
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Objectif",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                )
                Text(
                    text = "2.0 L",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            // Stat 2: Remaining ml
            val remaining = (dailyGoal - totalIntake).coerceAtLeast(0)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Restant",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                )
                Text(
                    text = "$remaining ml",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (remaining > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.tertiary
                    )
                )
            }

            // Stat 3: Day Streak
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Série d'eau",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = Color(0xFFFF9100),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "3 jours",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF9100)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun HydrationWaveCard(
    progress: Float,
    totalIntake: Int,
    dailyGoal: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Custom Water Progress Circle with realistic Wave liquid filling
            WaterProgressWave(
                progress = progress,
                totalIntake = totalIntake,
                dailyGoal = dailyGoal,
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Dynamic Motivational Phrase
            val statusMessage = when {
                progress >= 1f -> "Objectif atteint ! Vous rayonnez de santé ! 💧🎉"
                progress >= 0.75f -> "Presque là ! Plus qu'un verre d'eau !"
                progress >= 0.5f -> "À mi-chemin ! Votre corps vous remercie."
                progress >= 0.25f -> "Bonne lancée, continuez sur ce rythme !"
                else -> "Commencez à vous hydrater dès maintenant !"
            }

            Text(
                text = statusMessage,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (progress >= 1f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun WaterProgressWave(
    progress: Float,
    totalIntake: Int,
    dailyGoal: Int,
    modifier: Modifier = Modifier
) {
    // 1. Core animate float progress for the outer ring
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceAtMost(2f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "outerRingAnimation"
    )

    // 2. Continuous wave animation parameters
    val infiniteTransition = rememberInfiniteTransition(label = "liquidWaveAnimation")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhaseAngle"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val trackColor = surfaceVariantColor.copy(alpha = 0.35f)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Double layered background glow
        Box(
            modifier = Modifier
                .size(185.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.16f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Canvas(
            modifier = Modifier
                .size(172.dp)
                .testTag("progress_circle")
        ) {
            val strokeWidth = 12.dp.toPx()
            val canvasSize = size.width
            val innerSize = Size(canvasSize - strokeWidth, canvasSize - strokeWidth)
            val topLeftOffset = Offset(strokeWidth / 2, strokeWidth / 2)

            // Draw outer gray trace circle
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeftOffset,
                size = innerSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Draw outer active turquoise progress ring
            drawArc(
                brush = Brush.linearGradient(
                    colors = listOf(primaryColor, secondaryColor)
                ),
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f,
                useCenter = false,
                topLeft = topLeftOffset,
                size = innerSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // 3. Clip path to draw realistic liquid waves inside
            val innerContentRadius = (canvasSize - strokeWidth * 2) / 2f
            val centerOffset = Offset(canvasSize / 2f, canvasSize / 2f)

            clipPath(
                path = Path().apply {
                    addOval(Rect(centerOffset, innerContentRadius))
                }
            ) {
                // Background subtle sky gradient inside the circle
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.05f),
                            secondaryColor.copy(alpha = 0.12f)
                        )
                    )
                )

                // Draw Water wave
                val wavePath = Path()
                val clipHeight = canvasSize
                val clipWidth = canvasSize
                
                // Liquid level calculations
                val waterLevelPercent = progress.coerceIn(0f, 1f)
                val waterHeight = clipHeight * (1f - waterLevelPercent)

                wavePath.moveTo(0f, clipHeight)
                
                // Construct the wavy sinusoid path
                for (x in 0..clipWidth.toInt() step 2) {
                    val xFloat = x.toFloat()
                    val normalizedX = xFloat / clipWidth
                    
                    // Summing two sinusoids for natural ripple variance
                    val amplitude1 = 8.dp.toPx()
                    val amplitude2 = 3.dp.toPx()
                    
                    val waveY = waterHeight +
                            sin(normalizedX * 2 * Math.PI.toFloat() + wavePhase) * amplitude1 +
                            sin(normalizedX * 4 * Math.PI.toFloat() - wavePhase * 1.5f) * amplitude2

                    wavePath.lineTo(xFloat, waveY)
                }
                
                wavePath.lineTo(clipWidth, clipHeight)
                wavePath.close()

                // Fill liquid wave with rich Turquoise to Blue gradient
                drawPath(
                    path = wavePath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.85f),
                            secondaryColor.copy(alpha = 0.65f),
                            surfaceVariantColor.copy(alpha = 0.9f)
                        )
                    )
                )
            }
        }

        // Numerical readout & details
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            val percent = (progress * 100).toInt()
            
            Text(
                text = "$percent%",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = if (progress > 0.45f) Color.White else MaterialTheme.colorScheme.onSurface,
                    letterSpacing = (-1.5).sp,
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.25f),
                        offset = Offset(0f, 4f),
                        blurRadius = 8f
                    )
                )
            )

            Text(
                text = "$totalIntake / $dailyGoal ml",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (progress > 0.3f) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            )
        }
    }
}

@Composable
fun AdviceTipCard(
    tip: String,
    onClickNext: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickNext() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        ),
        border = borderStrokeForTheme()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Conseil Hydratation",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = tip,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )
                )
            }
        }
    }
}

@Composable
fun ContainerPickerItem(
    container: ContainerOption,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val highlightColor = MaterialTheme.colorScheme.primary
    val borderColor = if (isSelected) highlightColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
    val backgroundBrush = if (isSelected) {
        Brush.verticalGradient(
            colors = listOf(
                highlightColor.copy(alpha = 0.18f),
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
            )
        )
    }

    Card(
        modifier = Modifier
            .width(88.dp)
            .height(108.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onSelect() }
            .testTag("container_${container.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = borderStrokeForState(isSelected)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Glass Icon
                Icon(
                    imageVector = container.icon,
                    contentDescription = container.name,
                    tint = if (isSelected) highlightColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(28.dp)
                        .shadow(
                            elevation = if (isSelected) 8.dp else 0.dp,
                            shape = CircleShape,
                            ambientColor = highlightColor,
                            spotColor = highlightColor
                        )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = container.name,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 11.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${container.capacityMl} ml",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) highlightColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun WaterLogItem(
    log: WaterLog,
    onDelete: () -> Unit
) {
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val timeString = formatter.format(Date(log.timestamp))

    // Automatically deduce which container icon to show based on standard amounts
    val (containerLabel, icon) = when (log.amountMl) {
        250 -> "Verre d'eau" to Icons.Default.LocalDrink
        500 -> "Bouteille d'eau" to Icons.Default.LocalDrink
        1500 -> "Grande Bouteille" to Icons.Default.WaterDrop
        else -> "Apport Personnalisé" to Icons.Default.LocalDrink
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("water_log_item_${log.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        border = borderStrokeForTheme(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "+${log.amountMl} ml",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "$containerLabel • $timeString",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                        )
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.error.copy(alpha = 0.85f)
                ),
                modifier = Modifier.testTag("delete_log_button_${log.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Supprimer l'entrée",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyHistoryView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Beautiful drinking person illustration with an animated pulsing/loading glow effect
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(8000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(160.dp)
                .graphicsLayer(scaleX = scale, scaleY = scale)
        ) {
            // Animated spinning outer loading indicator ring
            Canvas(modifier = Modifier.size(150.dp)) {
                drawArc(
                    color = com.example.ui.theme.TurquoisePrimary.copy(alpha = 0.3f),
                    startAngle = rotation,
                    sweepAngle = 280f,
                    useCenter = false,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 4.dp.toPx(),
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                    )
                )
            }

            // Beautiful rounded crop of the drinking person illustration
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .size(120.dp)
                    .border(3.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), CircleShape),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_person_drinking_1784313094859),
                    contentDescription = "Personne qui boit",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Aucun apport enregistré",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Touchez un récipient ci-dessus pour vous hydrater !",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
fun ThemeSelectionDialog(
    currentStyle: AppThemeStyle,
    currentMode: AppThemeMode,
    onStyleSelect: (AppThemeStyle) -> Unit,
    onModeSelect: (AppThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Personnaliser le thème",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Palette de couleurs (Theme Styles)
                Text(
                    text = "Palette de couleurs",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val styles = listOf(
                        Triple(AppThemeStyle.TURQUOISE, "Eau", Color(0xFF00E5FF)),
                        Triple(AppThemeStyle.FOREST, "Forêt", Color(0xFF00E676)),
                        Triple(AppThemeStyle.SUNSET, "Soleil", Color(0xFFFF7043)),
                        Triple(AppThemeStyle.LAVENDER, "Sérénité", Color(0xFFB388FF))
                    )

                    styles.forEach { (style, name, color) ->
                        val isSelected = currentStyle == style
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onStyleSelect(style) }
                                .padding(vertical = 4.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 0.dp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        shape = CircleShape
                                    )
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.WaterDrop,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = name,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Section 2: Mode d'affichage (Light, Dark, System)
                Text(
                    text = "Mode d'affichage",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val modes = listOf(
                        Triple(AppThemeMode.LIGHT, "Clair", "☀️"),
                        Triple(AppThemeMode.DARK, "Sombre", "🌙"),
                        Triple(AppThemeMode.SYSTEM, "Système", "⚙️")
                    )

                    modes.forEach { (mode, name, emoji) ->
                        val isSelected = currentMode == mode
                        Button(
                            onClick = { onModeSelect(mode) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                            )
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(emoji, fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(name, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Terminé", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun CustomAmountDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var amountSliderValue by remember { mutableStateOf(250f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Quantité Personnalisée",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${amountSliderValue.toInt()} ml",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Slider(
                    value = amountSliderValue,
                    onValueChange = { amountSliderValue = it },
                    valueRange = 50f..1000f,
                    steps = 18, // Steps of 50ml each
                    colors = SliderDefaults.colors(
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                        thumbColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("50 ml", style = MaterialTheme.typography.labelSmall)
                    Text("500 ml", style = MaterialTheme.typography.labelSmall)
                    Text("1000 ml", style = MaterialTheme.typography.labelSmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(amountSliderValue.toInt()) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Enregistrer", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp)
    )
}

// Helpers for unified styling & border accents
@Composable
fun borderStrokeForTheme(alpha: Float = 0.15f) = androidx.compose.foundation.BorderStroke(
    width = 1.dp,
    color = MaterialTheme.colorScheme.primary.copy(alpha = alpha)
)

@Composable
fun borderStrokeForState(isSelected: Boolean) = androidx.compose.foundation.BorderStroke(
    width = if (isSelected) 1.5.dp else 1.dp,
    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
)
