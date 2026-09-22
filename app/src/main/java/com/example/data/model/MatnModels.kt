package com.example.data.model

enum class MatnId(val titleAr: String, val authorAr: String, val versesCount: Int, val descriptionAr: String) {
    TUHFAT_AL_ATFAL(
        titleAr = "متن تحفة الأطفال",
        authorAr = "الشيخ سليمان الجمزوري رحمه الله",
        versesCount = 61,
        descriptionAr = "منظومة جامعة ميسرة في أحكام التجويد الأساسية، النون والميم الساكنتين والمدود"
    ),
    AL_JAZARIYYAH(
        titleAr = "منظومة المقدمة (الجزرية)",
        authorAr = "إمام القراء شمس الدين محمد بن الجزري رحمه الله",
        versesCount = 109,
        descriptionAr = "العمدة في علم التجويد ومخارج الحروف وصفاتها والوقف والابتداء ورسم المصحف"
    )
}

data class Bayt(
    val number: Int,
    val firstHalf: String,
    val secondHalf: String,
    val chapterId: String,
    val matnId: MatnId
) {
    val fullText: String get() = "$firstHalf ... $secondHalf"
}

data class ExplanationPoint(
    val title: String,
    val details: String,
    val referenceBayt: Int? = null,
    val examples: List<String> = emptyList()
)

enum class DiagramType {
    MAKHARIJ_TREE,
    SIFAT_CLASSIFICATION,
    NOON_SAKINAH_RULES,
    MEEM_SAKINAH_RULES,
    MUDOOD_SYSTEM,
    LAMAT_RAAT,
    TAFKHEEM_TARQEEQ,
    MAQTOO_MAWSOOL
}

data class VisualDiagramData(
    val type: DiagramType,
    val title: String,
    val subtitle: String,
    val items: List<DiagramItem>
)

data class DiagramItem(
    val title: String,
    val description: String,
    val badge: String? = null,
    val subItems: List<String> = emptyList()
)

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val feedback: String,
    val referenceBaytNumber: Int
)

data class Chapter(
    val id: String,
    val matnId: MatnId,
    val title: String,
    val startBayt: Int,
    val endBayt: Int,
    val commentarySource: String,
    val verses: List<Bayt>,
    val explanations: List<ExplanationPoint>,
    val diagram: VisualDiagramData?,
    val quiz: List<QuizQuestion>
)
