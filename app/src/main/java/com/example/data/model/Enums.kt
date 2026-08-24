package com.example.data.model

enum class FitnessLevel(val title: String, val description: String) {
    BEGINNER("Beginner", "0-6 months of consistent training"),
    NOVICE("Novice", "6-12 months of structured training"),
    INTERMEDIATE("Intermediate", "1-3 years of progressive overload"),
    ADVANCED("Advanced", "3+ years of mastery and periodization")
}

enum class TrainingEnvironment(val title: String) {
    FULL_GYM("Full Commercial Gym"),
    BASIC_GYM("Basic Gym / Hotel Gym"),
    HOME_EQUIPMENT("Home with Equipment"),
    HOME_BODYWEIGHT("Home Calisthenics / No Equipment")
}

enum class EquipmentType(val displayName: String, val iconName: String) {
    BARBELL("Barbell & Plates", "fitness_center"),
    DUMBBELLS("Dumbbells", "fitness_center"),
    ADJUSTABLE_DUMBBELLS("Adjustable Dumbbells", "tune"),
    PULL_UP_BAR("Pull-up Bar", "vertical_align_top"),
    DIP_BARS("Dip Bars / Parallel Bars", "height"),
    RESISTANCE_BANDS("Resistance Bands", "gesture"),
    GYM_MACHINES("Cable & Gym Machines", "settings_input_component"),
    BENCH("Adjustable Bench", "table_bar"),
    BACKPACK("Weighted Backpack / Vest", "backpack"),
    RINGS("Gymnastic Rings", "radio_button_unchecked"),
    NONE("Bodyweight Only", "accessibility_new")
}

enum class PhysiqueGoal(val title: String, val subtitle: String) {
    AESTHETIC("Aesthetic Physique", "V-taper, balanced shoulder-to-waist ratio, lean definition"),
    LEAN_ATHLETIC("Lean Athletic", "Agility, explosive power, functional lean muscle"),
    MUSCULAR("Muscular Physique", "High hypertrophy, density, and full muscle development"),
    BULKY_MASS("Bulky / Mass-Building", "Maximum size, heavy strength foundation, caloric surplus"),
    CALISTHENICS("Calisthenics Mastery", "Relative strength, skill mastery, gymnastic control"),
    STRENGTH_FOCUSED("Strength-Focused", "Raw power, compound PRs, neurological adaptations"),
    ATHLETIC_SPORTS("Athletic Sports Physique", "Speed, stamina, sport-specific conditioning & endurance"),
    CUSTOM("Custom Goal", "Personalized mix of strength and body composition")
}

enum class MuscleGroup(val title: String, val category: String) {
    CHEST("Chest", "Upper Body Push"),
    UPPER_CHEST("Upper Chest", "Upper Body Push"),
    SHOULDERS("Shoulders", "Upper Body Push"),
    LATERAL_DELTS("Lateral Delts", "Upper Body Push"),
    REAR_DELTS("Rear Delts", "Upper Body Pull"),
    BACK("Back", "Upper Body Pull"),
    LATS("Lats", "Upper Body Pull"),
    BICEPS("Biceps", "Upper Body Pull"),
    TRICEPS("Triceps", "Upper Body Push"),
    FOREARMS("Forearms", "Grip & Pull"),
    CORE("Core / Abs", "Trunk Stability"),
    LEGS("Legs / Quads", "Lower Body"),
    GLUTES("Glutes & Hamstrings", "Lower Body"),
    CALVES("Calves", "Lower Body"),
    SYMMETRY("Overall Symmetry", "Full Body"),
    POSTURE("Posture & Upper Back", "Structural")
}

enum class Timeframe(val label: String, val weeks: Int, val description: String) {
    WEEKS_4("4 Weeks", 4, "Rapid habit formation & neuromuscular activation"),
    WEEKS_8("8 Weeks", 8, "Initial visible tone & measurable strength jumps"),
    WEEKS_12("12 Weeks (Recommended)", 12, "Full macrocycle: definitive physique reshaping"),
    WEEKS_16("16 Weeks", 16, "Deep body recomposition & skill solidification"),
    MONTHS_6("6 Months", 26, "Substantial lean tissue growth and structural transformation"),
    MONTHS_9("9 Months", 39, "Advanced athletic adaptation and power milestones"),
    MONTHS_12("12 Months", 52, "Complete systemic physical evolution")
}

enum class TrainingPreference(val title: String) {
    STRENGTH_TRAINING("Heavy Strength Training"),
    HYPERTROPHY("Hypertrophy / Muscle Growth"),
    CALISTHENICS("Calisthenics & Bodyweight"),
    ATHLETIC_TRAINING("Athletic Conditioning"),
    FOOTBALL_CONDITIONING("Sports & Field Performance"),
    RUNNING("Running & Cardio Endurance"),
    MOBILITY("Mobility & Joint Health"),
    SKILL_TRAINING("Skill Progression (Handstands, Levers)"),
    FREE_WEIGHTS("Free Weights (Dumbbells/Barbells)"),
    GYM_MACHINES("Gym Cable & Resistance Machines")
}

enum class MealType(val title: String, val icon: String) {
    BREAKFAST("Breakfast", "wb_sunny"),
    LUNCH("Lunch", "restaurant"),
    DINNER("Dinner", "dinner_dining"),
    SNACK("Snack", "bakery_dining"),
    PRE_WORKOUT("Pre-Workout", "bolt"),
    POST_WORKOUT("Post-Workout", "fitness_center")
}

enum class CalisthenicsDifficulty(val label: String, val colorHex: Long) {
    BEGINNER("Beginner", 0xFF10B981),
    NOVICE("Novice", 0xFF38BDF8),
    INTERMEDIATE("Intermediate", 0xFFF59E0B),
    ADVANCED("Advanced", 0xFFEF4444),
    ELITE("Elite", 0xFFA855F7)
}
